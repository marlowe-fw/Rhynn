#ifndef OutgoingClientsContainer_h__
#define OutgoingClientsContainer_h__
#include <list>

namespace min {

class MinClient;

class SendingClientsManager {

	public:
		SendingClientsManager();
		unsigned int getNumClients();
		void addClient(MinClient& client);
		void removeClient(MinClient& client);
		void clear();
		void processSending();

	private:
		std::list<MinClient*> container;
		unsigned int clientSendLimit;
		
};

}
#endif // OutgoingClientsContainer_h__