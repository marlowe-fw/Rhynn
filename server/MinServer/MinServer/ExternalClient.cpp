#include "ExternalClient.h"
#include "net_basics.h"
#include <iostream>

using namespace min;

ExternalClient::ExternalClient(int newBufferSize) : MinClient(newBufferSize) {
}

ExternalClient::ExternalClient(int newBufferSize, unsigned long outBufferLaneSize)
: MinClient(newBufferSize, newBufferSize, outBufferLaneSize) {

}


ExternalClient::~ExternalClient() {
	if (clientSocket > 0) {
		disconnectFromServer();
	}
}

bool ExternalClient::connectToServer(const std::string& connectAddr, int connectPort, bool nonBlocking /* = true */ ) {
	if (getState() != MinClient::Disconnected) {
		disconnectFromServer();
	}

	clientSocket = static_cast<int>(socket(PF_INET, SOCK_STREAM, 0));
	struct sockaddr_in remoteAddress;
	remoteAddress.sin_family = AF_INET;
	remoteAddress.sin_addr.s_addr = inet_addr(connectAddr.c_str());
	remoteAddress.sin_port = htons( connectPort );
	memset(remoteAddress.sin_zero, '\0', sizeof remoteAddress.sin_zero);

	//std::cout << "connecting to " << connectAddr << " on port " << connectPort << " .. ";
	int ret = connect(clientSocket, (sockaddr*)(&remoteAddress), sizeof remoteAddress);

	if (ret < 0) {
		std::cout << "connecting to " << connectAddr << " on port " << connectPort << " failed " << std::endl;
		setDisconnected();
		return false;
	} else {
		if (nonBlocking) {
			NetPort::setSocketNonBlocking(clientSocket);
		}
		setConnected(clientSocket);
		//std::cout << " ok" << std::endl;
		return true;
	}
}

bool ExternalClient::disconnectFromServer() {
	//std::cout << "disconnecting..";
	if (clientSocket > 0) {
		NetPort::closeSocket(clientSocket);
	}
	setDisconnected();
	//std::cout << " done" << std::endl;

	return true;
}

/*
int send(const unsigned char* const msg) {
	return send(clientSocket, msg, msg[0], 0);
}*/



/*
int ExternalClient::receive() {
	char buf[100];
	int retVal = recv(connectSocket, buf, 100, 0);
	if (retVal < 0) {
		if (!NetPort::hadOperationOnBlockedSocket()) {
			// aread form the blocked socket is ok
			// error
			// terminate
		}
	} else if (retVal == 0) {
		cout << "Server closed connection" << endl;
		// terminate
	}
}*/
