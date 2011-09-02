#include "MinServer.h"
#include "MinMessage.h"
#include <iostream>
#include <algorithm>

using namespace min;

MinServer::MinServer(int newPort)
:
clientSendLimit(0),
#ifdef MIN_USE_EPOLL
pendingEpollEvents(0),
#endif
totalBytesReceived(0),
totalBatchReceives(0),
totalSingleReceives(0),
totalMessagesReceived(0),
totalBatchMessagesReceived(0),
port(newPort),
serverSocket(0),
minSocketNum(1),
maxSocketNum(-1),
numClientsConnected(0),
initialized(false),
running(false)
{
	#ifndef MIN_USE_EPOLL
	FD_ZERO(&allSockets);
	FD_ZERO(&readSockets);
	#endif

	std::cout << std::endl << "MinServer " << MinServer::server_version_major << "." << MinServer::server_version_minor << "."<< MinServer::server_version_sub << std::endl;
	std::cout << "Copyright 2008-2010 macrolutions Ltd." << std::endl << "-----------------------------------------" << std::endl;
}

MinServer::~MinServer() {
	#ifdef MIN_USE_EPOLL
	if (pendingEpollEvents != 0) {
		free(pendingEpollEvents);
	}
	#endif

	if (running) {
		stop();
		handleStop();
	}

	//std::cout << "before minserver destructor end" << std::endl;
	//Sleep(10000);
}

/**
 * Do initialization work for the server, implement in subclasses.
 * This function is called at the beginning of the start() function.
 * If this function returns false, startup of the server will be canceled
 * @return true on success, false otherwise.
 */
bool MinServer::init() {
	return true;
}

/**
 * Actually starts up the server, initializes networking, creates the server
 * socket and listens for incoming connections. Also invokes the onServerStart callback
 * which may be implemented in the subclass.
 */
bool MinServer::start() {
	if (!initialized) {
		initialized = init();
	}

	if (!initialized) {
		std:: cout << "***" << std::endl;
		std:: cout << "*** FATAL ERROR: Could not start server because initialization failed." << std::endl;
		std:: cout << "***" << std::endl;
		return false;
	}

	if (running) {
		stop();
		handleStop();
	}


	NetPort::netStart();
	running = createServerSocket();
	if (running) {
		// add to connected sockets
		#ifndef MIN_USE_EPOLL
		FD_SET(serverSocket, &allSockets);
		#else
		//linux only
		epollSetup();
		#endif
		// callback function for subclass
		onServerStart();
	}

	std:: cout << "-----------------------------------------" << std::endl;

	while (running) {
		#ifdef MIN_USE_EPOLL
 		executeEpoll();
		#else
		executeSelect();
		#endif
	}

	handleStop();
	return true;
}

/**
 * Called to stop the server. This will make the server leave the main executions
 * loop and call handleStop() afterwards. also calls onserverStop() which must be
 * implemented by subclasses to properly react on the server being stopped.
 */
bool MinServer::stop() {
	running = false;
	onServerStop();
	return true;
}

/**
 * Shutdown logic, cleanup, statistics, etc.
 */
void MinServer::handleStop() {
	sendingClientsManager.clear();
	//outgoingClients.clear();
	disconnectedClients.clear();	// all clients will be disconnected below

	// remove the clients, not exception safe, might use shared_ptr as map elements
	for(std::map<int, MinClient*>::iterator i=clientMap.begin(); i!=clientMap.end(); ++i) {
		queueClientForDisconnect((*i).second, MinClient::Remove_ServerShutdown);
	}

	dropClientsQueuedForDisconnect();
	clientMap.clear();

	#ifndef MIN_USE_EPOLL
	FD_ZERO(&allSockets);
	FD_ZERO(&readSockets);
	#endif

	// at this point, all clients should be deleted, so there is no need to delete them before other containers are destructed

	if (serverSocket > 0) {
		std::cout << "server socket closed" << std::endl;
		NetPort::closeSocket(serverSocket);
		NetPort::netStop();
		serverSocket = 0;
	}

	// todo: notify game logic of shutdown --> or: destructor will handle it

	std::cout << std::endl << std::endl << "---" << std::endl << "Server was shut down." << std::endl << "---" << std::endl << std::endl;

	//Sleep(10000);

	std::cout << "received total bytes: " << totalBytesReceived << std::endl;
	std::cout << "single receives: " << totalSingleReceives << std::endl;
	std::cout << "batch receives:  " << totalBatchReceives << std::endl;
	std::cout << "batch messages:  " << totalBatchMessagesReceived << std::endl;
	std::cout << "------------------------------------" << std::endl;
	std::cout << "total messages:  " << totalMessagesReceived << std::endl << std::endl;
}


#ifdef MIN_USE_EPOLL
// linux only

/**
 * Specific setup for the linux epoll call (which is used instead of select).
 */
void MinServer::epollSetup() {
	std::cout << "setting up epoll .. ";

	// request to allocate the event structures and set the epoll file descriptor
	epoll_fd = epoll_create(INITIAL_EPOLL_POOL_SIZE);
	// add the server socket to the set of observed file descriptors
	// add server socket to epoll
	struct epoll_event eventServerSocket;
	eventServerSocket.events = EPOLLIN;
	eventServerSocket.data.ptr = 0;	// noclient object associated, we use this to find the serverSocket later
	epoll_ctl(epoll_fd, EPOLL_CTL_ADD, serverSocket, &eventServerSocket);

	// allocate the appropriate number of pending events (the event pool)
	pendingEpollEvents = (struct epoll_event*) malloc (sizeof (struct epoll_event) * EPOLL_MAX_EVENTS_PER_ITERATION);

	/*
	struct sockaddr_in client_addr;
	int len = sizeof(client_addr);
    */
	std::cout << "OK" << std::endl;
}

/**
 * Main execution loop on unix / linux, using epoll. This should scale much better than the default execute function.
 */
void MinServer::executeEpoll() {
	//static unsigned long cycle = 0;


	bool repeat = false;
	do {
		// last parameter is timeout in milliseconds
		int num_fd = epoll_wait(epoll_fd, pendingEpollEvents, EPOLL_MAX_EVENTS_PER_ITERATION, 3);
		if (num_fd < 0) {
			if (errno == EINTR) {
				// just interrupted by a signal
				repeat = true;
			} else {
				std::cout << "Error at epoll(): " << errno << std::endl;
				return;
			}
		} else {
			for (int i=0; i<num_fd; i++) {
				if(pendingEpollEvents[i].data.ptr == 0) {
					acceptClient();
				} else if(pendingEpollEvents[i].events & EPOLLIN) {
					if (pendingEpollEvents[i].data.ptr != 0) {
						// can read
						MinClient* pCurClient = static_cast<MinClient*>(pendingEpollEvents[i].data.ptr);
						receive(pCurClient);
					}
				} else if(pendingEpollEvents[i].events & EPOLLERR) {
					if (pendingEpollEvents[i].data.ptr != 0) {
						MinClient* pCurClient = static_cast<MinClient*>(pendingEpollEvents[i].data.ptr);
						struct epoll_event event;	// jsut used for the parameter, won't be changed
						queueClientForDisconnect(pCurClient, MinClient::Remove_Error);
						epoll_ctl(epoll_fd, EPOLL_CTL_DEL, pCurClient->getClientSocket(), &event);
						// make sure client is removed from server data structures before this iteration ends
					}
				}
			}
		}
	} while (repeat == true);

	//++cycle;

	executeLogic();

	// if (cycle > 3) {
		sendQueuedOutgoingMessages();
	//	cycle = 0;
	// }
	dropClientsQueuedForDisconnect();

}

#else

/**
 * Main execution loop of the server, using select which is portable for Linux and Windows (however slower on Linux than using epoll, see above).
 */
void MinServer::executeSelect() {
	// copy all connected sockets to detect those ready for reading
	readSockets = allSockets;

	static unsigned long cycle = 0;

	struct timeval tv;
	tv.tv_sec = 0;
	// this means that after a given timeout, even if there is no data to read,
	// the select call will time out after the given number of milliseconds, effectively setting the execution
	// "frame" rate
	tv.tv_usec = 30;	// 0.03ms, this is a very short timeout but this still prevents the server from just spinning in a loop

	bool repeat = false;
	do {
		if (select(maxSocketNum + 1, &readSockets, 0, 0, &tv) == -1) {
			#ifndef _WIN32
			// this is for linux
			if (errno == EINTR) {
				// just interrupted by a signal
				repeat = true;
			} else {
				std::cout << "Error at select(): " << errno << std::endl;
				return;
			}
			#else
				std::cout << "Error at net select()." << std::endl;
				return;
			#endif
		}
	} while (repeat == true);

	for (int i= minSocketNum; i <= maxSocketNum; i++) {
		// todo: explicitly check for listener socket then remove from set if ready to read

		if (FD_ISSET(i, &readSockets)) {
			// socket ready for reading
			if (i == serverSocket) {
				acceptClient();
				//Sleep(2000);
			} else {
				// regular client socket
				receive(i);
				//Sleep(20);
			}
		}
	}

	++cycle;

	executeLogic();

	// if (cycle > 3) {
		sendQueuedOutgoingMessages();
	//	cycle = 0;
	// }
	dropClientsQueuedForDisconnect();


	/*
	std::cout << "stopping.. " << std::endl;
	Sleep(3500);
	stop();
	std::cout << "stopped " << std::endl;
	Sleep(5000);
	*/

}
#endif

void MinServer::sendQueuedOutgoingMessages() {
	sendingClientsManager.processSending();


	/*
	//std::cout << "in sending queue" << std::endl;
	// do sending
	int error;
	unsigned long curBytesToSend, totalBytesToSend;
	unsigned int numBytesSent;
	MinClient* pCurClient;
	for(std::list<MinClient*>::iterator lit = outgoingClients.begin(); lit != outgoingClients.end();) {
		error = 0;
		pCurClient = *lit;
		curBytesToSend = totalBytesToSend = pCurClient->getOutBufferRemainingFill();
		if (clientSendLimit < curBytesToSend) {
			curBytesToSend = clientSendLimit;
		}
		numBytesSent = pCurClient->sendFromBuffer(curBytesToSend, &error);
		//outgoingClients
		if (error != 0 || numBytesSent == totalBytesToSend) {
			lit = outgoingClients.erase(lit);
			pCurClient->setInOutgoingQueue(false);
			if (error != 0) {
				queueClientForDisconnect(pCurClient, MinClient::Remove_Error);
			}
		} else {
			// not all messages sent yet, remain in out queue
			++lit;
		}
	}
	*/
}

/**
 * Make the server listen on the specified port by creating a server socket.
 */
bool MinServer::createServerSocket() {
	serverSocket = 0;
	struct sockaddr_in localAddress;
	localAddress.sin_family = AF_INET;
	localAddress.sin_addr.s_addr = htonl(INADDR_ANY);
	//inet_addr("127.0.0.1");
	localAddress.sin_port = htons(port);
	memset(localAddress.sin_zero, '\0', sizeof localAddress.sin_zero);
	serverSocket = static_cast<int>(socket(PF_INET, SOCK_STREAM, 0));
	if (serverSocket == -1) {
		std::cout << "Error at socket()." << std::endl;
		return false;
	}
	NetPort::setNoTCPDelay(serverSocket);

	// prevent address already in use
	NetPort::allowSocketAddrReuse(serverSocket);

	if (bind(serverSocket, (struct sockaddr*)(&localAddress), sizeof localAddress) == -1) {
		std::cout << "Error at bind()." << std::endl;
		return false;
	}

	if (listen(serverSocket, 10) == -1) {
		std::cout << "Error at listen()." << std::endl;
		return false;
	}

	minSocketNum = maxSocketNum = serverSocket;

	std::cout << "server socket created" << std::endl;
	std::cout << "listening on port " << port << std::endl;
	return true;
}

/**
 * Accept a client connection, add client to the internal server data structures.
 */
int MinServer::acceptClient() {

	// client accept socket info holder
	int clientSocket = 0;
	struct sockaddr_in clientAddress;
	socklen_t clientAdressSize = sizeof clientAddress;

	//fprintf(stderr, "Listening for client connections.");
	clientSocket = static_cast<int>(accept(serverSocket, (struct sockaddr*)(&clientAddress), &clientAdressSize));
	//fprintf(stderr, "Got client connection.\n");

	NetPort::setSocketNonBlocking(clientSocket);

	MinClient* pClient = newClientInstance();
	pClient->setConnected(clientSocket);
	clientMap.insert(std::pair<int,MinClient*>(clientSocket, pClient));
	// callback for subclasses
	onClientConnect(pClient);
	numClientsConnected++;

	std::cout << "Connect: " << clientSocket << ", Clients online: " << numClientsConnected << std::endl;


	// add to set of connected sockets
	#ifndef MIN_USE_EPOLL
	// only use this with select() (i.e. on windows)
	FD_SET(clientSocket, &allSockets);
	#else
	// use epoll on linux
	// add the client socket to the observed sockets pool
	struct epoll_event event;
	//event.data.fd = clientSocket;
	event.data.ptr = /*(void*)*/pClient;	// set a reference to the actual client object
	event.events = EPOLLIN | EPOLLERR;	// only interested in read and error notification
	epoll_ctl(epoll_fd, EPOLL_CTL_ADD, clientSocket, &event);
	#endif

	if (clientSocket > maxSocketNum) {
		maxSocketNum = clientSocket;
	} else if (clientSocket < minSocketNum) {
		minSocketNum = clientSocket;
	}

	return clientSocket;
}

/**
 * Receive from the network for the given socket, will lookup the corresponding client and call receive for it.
 * @param clientSocket The socket file descriptor for which to receive
 */
void MinServer::receive(int clientSocket) {

	SocketClientMap::iterator it = clientMap.find(clientSocket);

	if (it != clientMap.end()) {
		// client was found
		MinClient* pCurClient = (*it).second;
		receive(pCurClient);
	} else {
		// this should not happen under normal conditions
		std::cout << "Client object for socket was not found" << std::endl;
		#ifndef MIN_USE_EPOLL
		FD_CLR(clientSocket, &allSockets);
		#endif
		--numClientsConnected;
	}
}

/**
 * Receive from the network for the given client object.
 * @param pCurClient The current client object for which to receive
 */
void MinServer::receive(MinClient* pCurClient) {
	int numBytes = 0;
	numBytes = pCurClient->receiveMax();

	if (numBytes > 0) {
		totalBytesReceived += numBytes;
		// check if complete message
		int numReceive = 0;
		while (pCurClient->hasCompleteMessage()) {
			numReceive++;
			executeMessage(pCurClient, pCurClient->getBufferIn());
			pCurClient->removeCompleteMessage();
		}

		if (numReceive > 1) {
			totalBatchReceives++;
			totalBatchMessagesReceived += numReceive;
		} else {
			totalSingleReceives++;
		}
		totalMessagesReceived += numReceive;


	}
	// state of the client might have changes, so check this in either case
	if (pCurClient->getState() != MinClient::Connected) {
		// todo: use proper authorization for shutdown, see MinClient
		if (pCurClient->getState() == MinClient::AuthorizedShutdown) {
			queueClientForDisconnect(pCurClient, MinClient::Remove_Logic);
			stop();
		} else {
			queueClientForDisconnect(pCurClient, MinClient::Remove_ClientDisconnect);
		}
		//clientMap.erase(it);
	}

}

/** Called by subclasses when the application logic decides to drop a client. */
void MinServer::requestDisconnectClient(MinClient* client) {
	queueClientForDisconnect(client, MinClient::Remove_Logic, false);
}

void MinServer::queueClientForDisconnect(MinClient* pCurClient, MinClient::ClientRemovalInfo reason, bool notify /* = true */) {
	if (pCurClient != 0) {
		if (notify) {
			// allow subclass to handle this
			onClientRemoval(pCurClient, reason);
		}

		// clients marked for disconnect will be removed collectively on each iteration, see dropDisconnectedClients
		// reasoning: disconnectClient may be called while iteration through a container and thus erasing the client here
		//  would invalidate the iterator
		disconnectedClients.insert(std::pair<int, MinClient*>(pCurClient->getClientSocket(), pCurClient));
	}
}

/**
 * Serialize a message object to the client's output buffer and queue the client for sending.
 * @param msg The message object
 * @param client The client which should be sent the message
 * @return true if the message could be stored for the client (in which case queuing of the client took place), false on error
 */
bool MinServer::queueMessageForSending(MinMessage& msg, MinClient* client, bool validateLength /* = true */) {
	if (client->putMessageIntoOutBuffer(msg, validateLength)) {
		queueClientForSending(client);
		return true;
	} else if (client->getState() == MinClient::Error) {
		queueClientForDisconnect(client, MinClient::Remove_Error, true);
	}
	return false;
}

/**
 * Queue the given client in the sending queue.
 * The sending queue contains all clients which have pending messages for sending on their outgoing buffer.
 * @param client The client object to queues
 */
void MinServer::queueClientForSending(MinClient* client) {
	sendingClientsManager.addClient(*client);
	/*
	if (!(client->isInOutgoingQueue())) {
		outgoingClients.push_back(client);
		client->setInOutgoingQueue(true);
	}*/
}



/**
* Housekeeping: remove disconnected client pointers from internal data structures and free memory taken up by clients.
*/
void MinServer::dropClientsQueuedForDisconnect() {
	MinClient* pCurClient;
	for(std::map<int, MinClient*>::iterator it = disconnectedClients.begin(); it != disconnectedClients.end(); ++it) {
		pCurClient = (*it).second;
		int clientSocket = (*it).first;

		// actual disconnect
		pCurClient->setDisconnected();
		NetPort::closeSocket(clientSocket);

		#ifdef MIN_USE_EPOLL
		// close of the socket should automatically remove the socket from the epoll observed fds, but be sure and remove on our own, too
		struct epoll_event event;
		epoll_ctl(epoll_fd, EPOLL_CTL_DEL, clientSocket, &event);
		#else
		FD_CLR(clientSocket, &allSockets);
		#endif

		// remove from global map
		clientMap.erase(clientSocket);
		// remove from outgoing queue
		sendingClientsManager.removeClient(*pCurClient);
		//outgoingClients.remove(pCurClient);

		// delete the actual allocated client object
		delete pCurClient;

		--numClientsConnected;
		std::cout << "Disconnect: " << clientSocket << ", Clients online: " << numClientsConnected << std::endl;
	}
	disconnectedClients.clear();
}


MinClient* MinServer::newClientInstance() {
	// override this in subclasses if necessary
	return _TRACK_NEW(new MinClient(MinClient::default_client_buffer_in_size));
}


void MinServer::executeMessage(MinClient* pCurClient, const unsigned char* msg) {
    // implement in subclasses
}

/**
 * Logic to execute on every server loop iteration.
 * Implement this is in subclasses to execute custom logic (e.g. AI, housekeeping, cleanup, statistics).
 */
void MinServer::executeLogic() {
	// implement in subclasses
}

void MinServer::onClientRemoval(MinClient* client, MinClient::ClientRemovalInfo removalInfo) {
	// implement in subclasses
}

void MinServer::onClientConnect(MinClient* client) {
	// implement in subclasses
}

void MinServer::onServerStart() {
	// implement in subclasses
}

void MinServer::onServerStop() {
	// implement in subclasses
}

void MinServer::notifyClientDestroyed(MinClient* client) {
	MinClientObserver::notifyClientDestroyed(client);
}

void MinServer::notifyClientStateChanged(MinClient* client) {
	int state = client->getState();
	if (state == MinClient::Error) {
		queueClientForDisconnect(client, MinClient::Remove_Error);
	}
}

