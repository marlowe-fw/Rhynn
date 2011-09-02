#ifndef ExternalClient_h__
#define ExternalClient_h__


#include "MinClient.h"
#include <string>

namespace min {

	class ExternalClient : public MinClient {
		public:
			ExternalClient(int bufferSize);
			ExternalClient(int bufferSize, unsigned long outBuffeLaneSize);
			virtual ~ExternalClient();
			bool connectToServer(const std::string& connectAddr, int connectPort, bool nonBlocking = true);
			bool disconnectFromServer();

		private:

	};

}

#endif // ExternalClient_h__
