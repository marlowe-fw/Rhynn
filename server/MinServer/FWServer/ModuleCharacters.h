#ifndef ModuleCharacters_h__
#define ModuleCharacters_h__

// %%GENERATOR_START%%MESSAGE_INCLUDES%%
#include "messages/MsgGameUserGetCharactersRequest.h"
#include "messages/MsgGameUserCharacterCreatePermissionRequest.h"
#include "messages/MsgGameUserCharacterCreateRequest.h"
#include "messages/MsgGameUserCharacterRenameRequest.h"
#include "messages/MsgGameUserCharacterDeleteRequest.h"
#include "messages/MsgGameUserCharacterSelectRequest.h"
#include "messages/MsgGameCharacterHighscoreRequest.h"
#include "messages/MsgGameCharacterRespawnRequest.h"
// %%GENERATOR_END%%MESSAGE_INCLUDES%%

namespace fwworld {
	class FWWorld;
	class Character;
}

using namespace fwworld;

namespace fws {

class FWServer;
class FWClient;

class ModuleCharacters {

public:
	ModuleCharacters(FWServer& server, FWWorld& world);

	// %%GENERATOR_START%%HANDLER_DECL%%
	bool handleMessageGameUserGetCharactersRequest(FWClient* pCurClient, MsgGameUserGetCharactersRequest& msg);
	bool handleMessageGameUserCharacterCreatePermissionRequest(FWClient* pCurClient, MsgGameUserCharacterCreatePermissionRequest& msg);
	bool handleMessageGameUserCharacterCreateRequest(FWClient* pCurClient, MsgGameUserCharacterCreateRequest& msg);
	bool handleMessageGameUserCharacterRenameRequest(FWClient* pCurClient, MsgGameUserCharacterRenameRequest& msg);
	bool handleMessageGameUserCharacterDeleteRequest(FWClient* pCurClient, MsgGameUserCharacterDeleteRequest& msg);
	bool handleMessageGameUserCharacterSelectRequest(FWClient* pCurClient, MsgGameUserCharacterSelectRequest& msg);
	bool handleMessageGameCharacterHighscoreRequest(FWClient* pCurClient, MsgGameCharacterHighscoreRequest& msg);
	bool handleMessageGameCharacterRespawnRequest(FWClient* pCurClient, MsgGameCharacterRespawnRequest& msg);
	// %%GENERATOR_END%%HANDLER_DECL%%

	void checkAutomaticVitalityRefill(Character* c);
	void removeCharacterFromWorld(Character* c);

private:
	FWServer& server;
	FWWorld& world;
};


}
#endif // ModuleCharacters_h__