#ifndef ModulePlayfields_h__
#define ModulePlayfields_h__

// %%GENERATOR_START%%MESSAGE_INCLUDES%%
#include "messages/MsgGamePlayfieldEnterWorldRequest.h"
#include "messages/MsgGamePlayfieldLoadRequest.h"
#include "messages/MsgGamePlayfieldEnterRequest.h"
// %%GENERATOR_END%%MESSAGE_INCLUDES%%

#include "GenericClock.h"

namespace fwworld {
	class FWWorld;
	class Playfield;
	class Character;
}

using namespace fwworld;

namespace fws {

	class FWServer;
	class FWClient;

class ModulePlayfields {

public:
	ModulePlayfields(FWServer& server, FWWorld& world);

	// %%GENERATOR_START%%HANDLER_DECL%%
	bool handleMessageGamePlayfieldEnterWorldRequest(FWClient* pCurClient, MsgGamePlayfieldEnterWorldRequest& msg);
	bool handleMessageGamePlayfieldLoadRequest(FWClient* pCurClient, MsgGamePlayfieldLoadRequest& msg);
	bool handleMessageGamePlayfieldEnterRequest(FWClient* pCurClient, MsgGamePlayfieldEnterRequest& msg);
	// %%GENERATOR_END%%HANDLER_DECL%%
	
	void placeCharacterOnPlayfield(Character* c, Playfield* playfield, bool forRespawn);
	void removeCharacterFromPlayfield(Character* c);

	void checkScheduledItems(clock_ms_t curTime);
	void broadCastMessageToCharactersInVisRangePx(unsigned int x, unsigned int y, Playfield& playfield, min::MinMessage& msg, unsigned int excludeObjectId=0);

private:
	FWServer& server;
	FWWorld& world;

	void notifyCharacterRemovedFromPlayfield(Character* c, Playfield* playfield);

};


}
#endif // ModulePlayfields_h__