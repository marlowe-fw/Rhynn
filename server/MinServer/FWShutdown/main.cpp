#include "net_basics.h"

#include <string.h>
#include <stdio.h>

#define CONNECT_PORT 23179
#define CONNECT_ADDR "127.0.0.1"

using namespace min;

void connectToServer() {
	int connectSocket = static_cast<int>(socket(PF_INET, SOCK_STREAM, 0));
	struct sockaddr_in remoteAddress;
	remoteAddress.sin_family = AF_INET;
	remoteAddress.sin_addr.s_addr = inet_addr(CONNECT_ADDR);
	remoteAddress.sin_port = htons( CONNECT_PORT );
	memset(remoteAddress.sin_zero, '\0', sizeof remoteAddress.sin_zero);

	fprintf(stderr, "connecting..");

	int ret = connect(connectSocket, (sockaddr*)(&remoteAddress), sizeof remoteAddress);

	if (ret < 0) {
		fprintf(stderr, " failed.\n");
	} else {
		fprintf(stderr, " done\n");

		fprintf(stderr, " \nsending shutdown ..");
		
		unsigned char msg[255];
		msg[0] = 255;
		int sent = send(connectSocket, (char*)msg, 255, 0);
		// may not send all bytes in one go, theoretically needs a loop to send all data for sure
		if (sent < 0) {
			fprintf(stderr, " failed\n\n");
		} else {
			fprintf(stderr, " done\n\n");
		}
	}
	NetPort::closeSocket(connectSocket);
}

int main() {
	NetPort::netStart();
	connectToServer();
	NetPort::netStop();
}