#include "MinClient.h"
#include "MinMessage.h"
#include "MinClientObserver.h"
#include <iostream>
#include <algorithm>

using namespace min;

MinClient::MinClient() : 
curMsgLength(0), bufferSizeIn(MinClient::default_client_buffer_in_size), bufferFill(0), clientSocket(0),
	bufferIn(_TRACK_NEW(new unsigned char[MinClient::default_client_buffer_in_size])),
	bufferOut(_TRACK_NEW(new NetBuffer(default_client_buffer_out_lane_size, default_client_buffer_out_max_lanes))),
	inOutgoingQueue(false)
{
}

MinClient::MinClient(int newBufferSizeIn) :
state(MinClient::Disconnected),
curMsgLength(0), bufferSizeIn(newBufferSizeIn), bufferFill(0), clientSocket(0),
bufferIn(_TRACK_NEW(new unsigned char[newBufferSizeIn])),
bufferOut(_TRACK_NEW(new NetBuffer(default_client_buffer_out_lane_size, default_client_buffer_out_max_lanes))),
inOutgoingQueue(false)
{
}

MinClient::MinClient(int newBufferSizeIn, unsigned long outBufferLaneSize, unsigned int outBufferMaxLanes) :
state(MinClient::Disconnected),
curMsgLength(0), bufferSizeIn(newBufferSizeIn), bufferFill(0), clientSocket(0),
bufferIn(_TRACK_NEW(new unsigned char[newBufferSizeIn])),
bufferOut(_TRACK_NEW(new NetBuffer(outBufferLaneSize, outBufferMaxLanes))),
inOutgoingQueue(false)
{
}


MinClient::~MinClient() {
	if (bufferIn != 0) {
		delete[] bufferIn;
		bufferIn = 0;
		clientSocket = 0;
	}
	if (bufferOut != 0) {
		// not an array
		delete bufferOut;
	}

	notifyDestroyed();
}

void MinClient::addObserver(MinClientObserver* observer) {
	observers.push_back(observer);
}

void MinClient::removeObserver(MinClientObserver* observer) {
	observers.erase(std::remove(observers.begin(), observers.end(), observer), observers.end());
}


void MinClient::setState(int newState) {
	state = newState;
	notifyStateChanged();	
}

void MinClient::notifyStateChanged() {
	for (std::vector<MinClientObserver*>::iterator it = observers.begin(); it!=observers.end(); it++) {
		(*it)->notifyClientStateChanged(this);
	}
}

void MinClient::notifyDestroyed() {
	for (std::vector<MinClientObserver*>::iterator it = observers.begin(); it!=observers.end(); ) {
		MinClientObserver* observer = (*it);
		it = observers.erase(it);
		observer->notifyClientDestroyed(this);
	}
}

void MinClient::disallowTCPDelay() {
	min::NetPort::setNoTCPDelay(clientSocket);
}

int MinClient::receiveMax() {
	if (state != MinClient::Connected) {
		return 0;
	}

	// receive up to bufferSize
	// refuse to receive if buffer is full
	int maxReceive = bufferSizeIn - bufferFill;

	int numReceived = 0;
	if (maxReceive > 0) {
		// note the offset of bufferIn, to only fill up after the last filled byte pos
		numReceived = recv(clientSocket, (char*)(bufferIn)+bufferFill, maxReceive, 0);

		if (numReceived > 0) {
			bufferFill += numReceived;
			if (curMsgLength == 0) {
				curMsgLength = bufferIn[0];
				if (curMsgLength > bufferSizeIn) {
					state = MinClient::Error;
					curMsgLength = 0;
					numReceived = 0;
				} else if (curMsgLength == 255) {
					// $-> todo: this has to be changed so that only admin clients can do this, or read special key from message
					std::cout << "received shutdown" << std::endl;
					state = MinClient::AuthorizedShutdown;
				}
			}
		} else if (numReceived == 0) {
			state = MinClient::DisconnectNotified;
		} else if (numReceived < 0) {
			if (!NetPort::hadOperationOnBlockedSocket()) {
				state = MinClient::Error;
			}
		}
	} else {
		std::cout << "buffer full in client" << std::endl;
	}

	return numReceived;
}

/* Force to send all data provided in one go, i.e. will not return until all is sent. */
int MinClient::sendAll(const unsigned char* const msg) {
	int total = 0; // how many bytes we've sent
	int len = msg[0];
	// note: if the first byte of a message is 0 that means that the length is encoded in the following 2 bytes
	// this is for messages with a length longer than 255 bytes
	if (len == 0) {
		len = NetPort::uintFrom2Bytes(msg, 1);
		std::cout << "sending long message: " + len;
	}
	int bytesleft = len; // how many we have left to send
	int n;
	while(total < len) {
		n = send(clientSocket, (const char*)msg+total, bytesleft, 0);
		if (n < 0) {
			if (!NetPort::hadOperationOnBlockedSocket()) {
				state = MinClient::Error;
				return -1;
			} else {
				continue;
			}
		}
		total += n;
		bytesleft -= n;
	}
	return total;
}

/**
 * Convenience function to send all data remaining in the out buffer.
 * Note that this will retry in a loop if sending is not possible, so this function should probably
 * not be used with the client objects that have outgoing messages on the server.
 * It is suited well for use with 'real' connecting clients.
 * @return true if there was no error
 */
bool MinClient::sendAllFromBuffer() {
	int error = 0;
	sendFromBuffer(0, &error, true);
	return error == 0;
}

/**
 * Will send all data in the outgoing buffer (or up to a defined limit) as long as sending is possible.
 * In case the forceSendAll parameter is set this will force sending until all data is really sent
 * Note that setting forceSendAll to true will make the function retry in a loop if sending is not possible,
 * so this setting should probably not be used with the client objects that have outgoing messages on the server.
 * @param clientSendLimit Maximum number of bytes to send, 0 for no limit
 * @param error Changed by reference to indicate errors
 * @return The number of bytes sent in total
 */
unsigned int MinClient::sendFromBuffer(unsigned int clientSendLimit, int* error, bool forceSendAll /* = false */) {
	unsigned int totalBytesSent = 0;
	int curBytesSent = 0;
	const unsigned char* sendData = 0;
	unsigned long maxBytesToSend = 0;
	*error = 0;

	//bufferOut->getRemainingFill();
	while ((maxBytesToSend = bufferOut->getNextContiguousDataSize()) > 0 && (clientSendLimit == 0 || clientSendLimit > totalBytesSent)) {
		if (clientSendLimit != 0 && maxBytesToSend + totalBytesSent > clientSendLimit) {
			maxBytesToSend = clientSendLimit - totalBytesSent;
		}

		sendData = bufferOut->getHeadCurosor();
		curBytesSent = send(clientSocket, (const char*)(sendData), maxBytesToSend, 0);
		if (curBytesSent > 0) {
			totalBytesSent += curBytesSent;
			bufferOut->shiftHeadCursor(curBytesSent);
			if (curBytesSent < static_cast<long>(maxBytesToSend)) {
				if (!forceSendAll) {
					// exit the loop if requested number of bytes could not be sent
					break;
				}
			}
		} else {
			if (!NetPort::hadOperationOnBlockedSocket()) {
				*error = -1;
			}
			break;
		}
	}
	return totalBytesSent;
}

/**
 * Take a message object and serialize it to the client's outBuffer.
 * @param msg The message object
 * @param validateLength Whether or not to check and set the message length before storing
 * @return true if the message could be stored, false otherwise
 */
bool MinClient::putMessageIntoOutBuffer(MinMessage& msg, bool validateLength /* = true */) {
	if (validateLength && ! msg.checkValidateLength()) {
		return false;
	}
	unsigned char * buf = requestBytesFromOutBuffer(msg.length);
	return (buf != 0 && msg.storeToBuffer(buf, false));
}

/**
* Take a message object and serialize it to the client's outBuffer, then send it completely.
* @param msg The message object
* @param validateLength Whether or not to check and set the message length before storing
* @return true if the message could be stored and sent, false otherwise
*/
bool MinClient::putMessageAndSendAll(MinMessage& msg, bool validateLength /* = true */) {
	if (putMessageIntoOutBuffer(msg, validateLength)) {
		return sendAllFromBuffer();
	}
	return false;
}


/**
* Request given number of bytes from the outgoing buffer of the client.
* @param numBytes The number of bytes to request into the outgoing buffer
* @return A pointer to the beginning of the requested bytes in the buffer, 0 on failure
*/
unsigned char* MinClient::requestBytesFromOutBuffer(unsigned long numBytes) {
	unsigned char* ret = bufferOut->requestBytes(numBytes);
	if (ret == 0) {
		state = MinClient::Error;
	}
	return ret;
}

/**
 * Request given number of bytes and copy the provided numBytes bytes into the outgoing buffer of the client.
 * @param numBytes The number of bytes to put into the outgoing buffer
 * @param copyBytes Array holding the bytes to copy
 * @return true if successful, false on error (most likely out buffer full due to some limit)
 */
bool MinClient::putBytesIntoOutBuffer(const unsigned char* copyBytes, unsigned long numBytes) {
	unsigned char* bufferBytes = bufferOut->requestBytes(numBytes);
	if (bufferBytes == 0) {
		state = MinClient::Error;
	} else {
		memcpy(bufferBytes, copyBytes, numBytes);
	}
	return false;
}


void MinClient::setConnected(int newSocket) {
	clientSocket = newSocket;
	//disallowTCPDelay();
	state = MinClient::Connected;
}


int MinClient::setDisconnected() {
	int tempSocket = clientSocket;
	clientSocket = 0;
	state = MinClient::Disconnected;
	return tempSocket;
}


bool MinClient::hasCompleteMessage() const {
	if (curMsgLength > 0 && bufferFill >= curMsgLength) {
		//cout << "message: " << curMsgLength << endl;
		return true;
	}
	return false;
}

void MinClient::removeCompleteMessage() {
	if (bufferFill < curMsgLength) {
		return;
	} else if (bufferFill == curMsgLength) {
		// exactly the complete message is in the buffer, removal is trivial
		curMsgLength = 0;
		bufferFill = 0;
	} else {
		// messages with length > bufferSize are refused (client will go to error state)
		// thus we assume that the whole message can be removed which will leave all housekeeping intact (no negative fill or msgLength)

		for (unsigned int i=0; curMsgLength+i < bufferFill; i++) {
			bufferIn[i] = bufferIn[curMsgLength+i];
		}
        // memcpy should be faster but get complaint from valgrind - source and destination can easily overlap
		// -- memcpy(bufferIn, bufferIn+curMsgLength, bufferFill-curMsgLength);

		bufferFill -= curMsgLength;
		curMsgLength = bufferIn[0];	// note: if clients are going to send long messages (length > 255 bytes) then this will have to change
		if (curMsgLength > bufferSizeIn || curMsgLength <= 0) {
			state = MinClient::Error;
			curMsgLength = 0;
		}
	}
}
