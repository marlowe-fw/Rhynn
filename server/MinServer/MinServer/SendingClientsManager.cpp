#include "SendingClientsManager.h"
#include "MinClient.h"

using namespace min;

SendingClientsManager::SendingClientsManager() : clientSendLimit(2048)
{
}

void SendingClientsManager::addClient(MinClient& client) {
	container.push_back(&client);
	client.setInOutgoingQueue(true);
}

unsigned int SendingClientsManager::getNumClients() {
	return container.size();
}

void SendingClientsManager::removeClient(MinClient& client) {
	container.remove(&client);
	client.setInOutgoingQueue(false);
}

void SendingClientsManager::clear() {
	container.clear();
}

void SendingClientsManager::processSending() {
	for(std::list<MinClient*>::iterator lit = container.begin(); lit != container.end();) {
		int error = 0;
		MinClient* pCurClient = *lit;
		pCurClient->sendFromBuffer(clientSendLimit, &error);

		if (pCurClient->getOutBufferRemainingFill() == 0 || error != 0) {
			lit = container.erase(lit);
			pCurClient->setInOutgoingQueue(false);
			if (error != 0) {
				pCurClient->setState(MinClient::Error);
			}
		} else {
			++lit;
		}
	}
}