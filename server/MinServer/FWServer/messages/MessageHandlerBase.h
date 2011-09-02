#ifndef MessageHandlerBase_h__
#define MessageHandlerBase_h__

#include "../FWClient.h"

namespace fws {

	class MessageHandlerBase {
	public:
        virtual ~MessageHandlerBase() {}

		inline bool handle(FWClient* pCurClient, const unsigned char* msgBytes, unsigned int length) {
			return handleImpl(pCurClient, msgBytes, length);
		}

		virtual bool handleImpl(FWClient* pCurClient, const unsigned char* msgBytes, unsigned int length) = 0;

	};

}

#endif // MessageHandlerBase_h__
