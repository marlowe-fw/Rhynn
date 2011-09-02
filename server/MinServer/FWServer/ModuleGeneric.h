#ifndef ModuleGeneric_h__
#define ModuleGeneric_h__

// %%GENERATOR_START%%MESSAGE_INCLUDES%%
#include "messages/MsgSystemChat.h"
#include "messages/MsgSystemPing.h"
#include "messages/MsgTestObjectMove.h"
#include "messages/MsgTestPayload.h"
#include "messages/MsgTestRegisterListener.h"
#include "messages/MsgGamePing.h"
#include "messages/MsgGameDebug.h"
#include "messages/MsgGameServerListRequest.h"
#include "messages/MsgGameVersionRequest.h"
#include "messages/MsgGameGraphicsLoadRequest.h"
// %%GENERATOR_END%%MESSAGE_INCLUDES%%


namespace fwworld {
	class FWWorld;
}

using namespace fwworld;

namespace fws {

class FWServer;
class FWClient;

class ModuleGeneric {

public:
	ModuleGeneric(FWServer& server, FWWorld& world);
	
	// %%GENERATOR_START%%HANDLER_DECL%%
	bool handleMessageSystemChat(FWClient* pCurClient, MsgSystemChat& msg);
	bool handleMessageSystemPing(FWClient* pCurClient, MsgSystemPing& msg);
	bool handleMessageTestObjectMove(FWClient* pCurClient, MsgTestObjectMove& msg);
	bool handleMessageTestPayload(FWClient* pCurClient, MsgTestPayload& msg);
	bool handleMessageTestRegisterListener(FWClient* pCurClient, MsgTestRegisterListener& msg);
	bool handleMessageGamePing(FWClient* pCurClient, MsgGamePing& msg);
	bool handleMessageGameDebug(FWClient* pCurClient, MsgGameDebug& msg);
	bool handleMessageGameServerListRequest(FWClient* pCurClient, MsgGameServerListRequest& msg);
	bool handleMessageGameVersionRequest(FWClient* pCurClient, MsgGameVersionRequest& msg);
	bool handleMessageGameGraphicsLoadRequest(FWClient* pCurClient, MsgGameGraphicsLoadRequest& msg);
	// %%GENERATOR_END%%HANDLER_DECL%%

private:
	FWServer& server;
	FWWorld& world;
};


}
#endif // ModuleGeneric_h__