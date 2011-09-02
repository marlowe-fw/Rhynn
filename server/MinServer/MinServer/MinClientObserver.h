#ifndef MinClientObserver_h__
#define MinClientObserver_h__

#include <vector>

namespace min {

class MinClient;

class MinClientObserver {

public:
	MinClientObserver();
	virtual ~MinClientObserver();

	void observeClient(MinClient* client);
	void stopObservingClient(MinClient* client);

	virtual void notifyClientDestroyed(MinClient* client);
	virtual void notifyClientStateChanged(MinClient* client) = 0;

	unsigned int getNumObservedClients();

private:
	std::vector<MinClient*> observedClients;

};

}

#endif // MinClientObserver_h__