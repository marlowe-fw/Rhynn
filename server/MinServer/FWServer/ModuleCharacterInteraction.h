#ifndef ModuleCharacterInteraction_h__
#define ModuleCharacterInteraction_h__

// %%GENERATOR_START%%MESSAGE_INCLUDES%%
#include "messages/MsgGameCharacterMove.h"
#include "messages/MsgGameCharacterChatAllRequest.h"
#include "messages/MsgGameCharacterChatRequest.h"
#include "messages/MsgGameCharacterAttackRequest.h"
// %%GENERATOR_END%%MESSAGE_INCLUDES%%

namespace fwworld {
	class FWWorld;
}

using namespace fwworld;

namespace fws {

class FWServer;
class FWClient;

class ModuleCharacterInteraction {

public:
	ModuleCharacterInteraction(FWServer& server, FWWorld& world);

	// %%GENERATOR_START%%HANDLER_DECL%%
	bool handleMessageGameCharacterMove(FWClient* pCurClient, MsgGameCharacterMove& msg);
	bool handleMessageGameCharacterChatAllRequest(FWClient* pCurClient, MsgGameCharacterChatAllRequest& msg);
	bool handleMessageGameCharacterChatRequest(FWClient* pCurClient, MsgGameCharacterChatRequest& msg);
	bool handleMessageGameCharacterAttackRequest(FWClient* pCurClient, MsgGameCharacterAttackRequest& msg);
	// %%GENERATOR_END%%HANDLER_DECL%%

private:
	FWServer& server;
	FWWorld& world;
};


}
#endif // ModuleCharacterInteraction_h__