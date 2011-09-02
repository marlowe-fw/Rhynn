#ifndef MinServer_h__
#define MinServer_h__

#include "net_basics.h"
#include <map>
#include <list>
#include <iostream>

#include "MinClient.h"
#include "SendingClientsManager.h"
#include "MinClientObserver.h"

#ifndef _WIN32
	#define MIN_USE_EPOLL 1
#endif


namespace min {

	class MinMessage;

	// client map shortcut type
	typedef std::map<int, MinClient*> SocketClientMap;

	class MinServer : public MinClientObserver {
		public:
			MinServer(int port);
			virtual ~MinServer();
			bool start();
			bool stop();
			bool queueMessageForSending(MinMessage& msg, MinClient* client, bool validateLength = true);

			// implemented from MinClientObserver
			void notifyClientDestroyed(MinClient* client);
			void notifyClientStateChanged(MinClient* client);

		protected:
			static const int server_version_major = 1;
			static const int server_version_minor = 2;
			static const int server_version_sub = 0;

			/** Maximum number of bytes to send for one outgoing client in one go. */
			unsigned int clientSendLimit;

			void requestDisconnectClient(MinClient* client);

			virtual bool init();
			virtual void executeMessage(MinClient* pCurClient, const unsigned char* msg);
			virtual void executeLogic();
			virtual void onClientRemoval(MinClient* client, MinClient::ClientRemovalInfo removalInfo);
			virtual void onClientConnect(MinClient* client);
			virtual void onServerStart();
			virtual void onServerStop();
			virtual MinClient* newClientInstance();
			void queueClientForSending(MinClient* client);


		private:
			#ifdef MIN_USE_EPOLL
			// linux only
			// this is just a hint for epoll, this is not the limit of connected clients
			static const int INITIAL_EPOLL_POOL_SIZE = 2048;
			// how many events (reads) to handle in one server loop iteration
			static const int EPOLL_MAX_EVENTS_PER_ITERATION = 512;
			int epoll_fd;	// for Linux epoll only
			//struct epoll_event eventServerSocket;
			struct epoll_event* pendingEpollEvents;
			#else
			fd_set allSockets;
			fd_set readSockets;
			//fd_set writeSockets;
			#endif


			std::map<int, MinClient*> clientMap;
			std::list<MinClient*> outgoingClients;
			std::map<int, MinClient*> disconnectedClients;

			long totalBytesReceived;
			long totalBatchReceives;
			long totalSingleReceives;
			long totalMessagesReceived;
			long totalBatchMessagesReceived;

			int port;
			int serverSocket;
			int minSocketNum;
			int maxSocketNum;
			int numClientsConnected;

			bool initialized;
			bool running;

			SendingClientsManager sendingClientsManager;

			int acceptClient();
			bool createServerSocket();
			void receive(int clientSocket);
			void receive(MinClient* pCurClient);

			#ifdef MIN_USE_EPOLL
			void epollSetup();
			void executeEpoll();
			#else
			void executeSelect();
			#endif

			void queueClientForDisconnect(MinClient* pCurClient, MinClient::ClientRemovalInfo removalInfo, bool notify = true);
			void dropClientsQueuedForDisconnect();
			void sendQueuedOutgoingMessages();
			void handleStop();
	};
}

#endif // MinServer_h__
