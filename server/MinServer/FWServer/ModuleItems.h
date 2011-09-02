#ifndef ModuleItems_h__
#define ModuleItems_h__

// %%GENERATOR_START%%MESSAGE_INCLUDES%%
#include "messages/MsgGameItemPickupRequest.h"
#include "messages/MsgGameItemEquipRequest.h"
#include "messages/MsgGameItemUnequipRequest.h"
#include "messages/MsgGameItemDropRequest.h"
#include "messages/MsgGameItemUseRequest.h"
// %%GENERATOR_END%%MESSAGE_INCLUDES%%

namespace fwworld {
	class FWWorld;
	class Character;
}
 
using namespace fwworld;

namespace fws {
 
class FWServer;
class FWClient;

class ModuleItems {

public:
	ModuleItems(FWServer& server, FWWorld& world);

	// %%GENERATOR_START%%HANDLER_DECL%%
	bool handleMessageGameItemPickupRequest(FWClient* pCurClient, MsgGameItemPickupRequest& msg);
	bool handleMessageGameItemEquipRequest(FWClient* pCurClient, MsgGameItemEquipRequest& msg);
	bool handleMessageGameItemUnequipRequest(FWClient* pCurClient, MsgGameItemUnequipRequest& msg);
	bool handleMessageGameItemDropRequest(FWClient* pCurClient, MsgGameItemDropRequest& msg);
	bool handleMessageGameItemUseRequest(FWClient* pCurClient, MsgGameItemUseRequest& msg);
	// %%GENERATOR_END%%HANDLER_DECL%%

	void sendInventoryForCharacter(Character& selectedCharacter);

private:
	FWServer& server;
	FWWorld& world;
};


}
#endif // ModuleItems_h__