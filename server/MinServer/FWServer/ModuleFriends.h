#ifndef ModuleFriends_h__
#define ModuleFriends_h__

// %%GENERATOR_START%%MESSAGE_INCLUDES%%
#include "messages/MsgGameFriendListRequest.h"
// %%GENERATOR_END%%MESSAGE_INCLUDES%%


namespace fwworld {
	class FWWorld;
}

using namespace fwworld;

namespace fws {

class FWServer;
class FWClient;

class ModuleFriends {

public:
	ModuleFriends(FWServer& server, FWWorld& world);

	// %%GENERATOR_START%%HANDLER_DECL%%
	bool handleMessageGameFriendListRequest(FWClient* pCurClient, MsgGameFriendListRequest& msg);
	// %%GENERATOR_END%%HANDLER_DECL%%

private:
	FWServer& server;
	FWWorld& world;
};

}

#endif // ModuleFriends_h__