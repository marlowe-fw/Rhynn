#include "MinClientObserver.h"
#include "MinClient.h"
#include <algorithm>

using namespace min;

MinClientObserver::MinClientObserver() {
}

MinClientObserver::~MinClientObserver() {
	for (std::vector<MinClient*>::iterator it=observedClients.begin(); it!=observedClients.end(); it++) {
		MinClient* client = *it;
		client->removeObserver(this);
	}
	observedClients.clear();
}

void MinClientObserver::observeClient(MinClient* client) {
	observedClients.push_back(client);
	client->addObserver(this);
}

void MinClientObserver::stopObservingClient(MinClient* client) {
	observedClients.erase(std::remove(observedClients.begin(), observedClients.end(), client), observedClients.end());
	client->removeObserver(this);
}


void MinClientObserver::notifyClientDestroyed(MinClient* client) {
	stopObservingClient(client);
}

unsigned int MinClientObserver::getNumObservedClients() {
	return observedClients.size();
}
