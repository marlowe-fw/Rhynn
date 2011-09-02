#ifndef MessageHandler_h__
#define MessageHandler_h__


#include "MessageHandlerBase.h"

namespace fws {

template <class T, class MessageClass> class MessageHandler : public MessageHandlerBase {
private:
	T* objectPtr;
	bool (T::*fpt)(FWClient*, MessageClass&);

public:
	MessageHandler(T* newObjectPtr, bool(T::*newFpt)(FWClient*, MessageClass&))
		: objectPtr(newObjectPtr), fpt(newFpt)
	{}


	inline bool handleImpl(FWClient* pCurClient, const unsigned char* msgBytes, unsigned int length) {
		MessageClass msg(msgBytes, length);
		if (msg.isValid()) {
			(objectPtr->*fpt)(pCurClient, msg);
			return true;
		}
		return false;
	}

	inline bool handleImpl(FWClient* pCurClient, MessageClass& newMsg) {
		(objectPtr->*fpt)(pCurClient, newMsg);
		return true;
	}
};

}
#endif // MessageHandler_h__
