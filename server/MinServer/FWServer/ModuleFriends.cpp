#include "ModuleFriends.h"
#include "FWClient.h"
#include "FWServer.h"
#include "FWWorld.h"

#include "messages/MsgGameFriendListEnd.h"

using namespace fws;
using namespace fwworld;
using namespace fwutil;

ModuleFriends::ModuleFriends(FWServer& newServer, FWWorld& newWorld)
: server(newServer), world(newWorld)
{}

// %%GENERATOR_START%%HANDLER_IMPL%%



/**
 * Client requests the friendlist for the selected character (co).
 */
bool ModuleFriends::handleMessageGameFriendListRequest(FWClient* pCurClient, MsgGameFriendListRequest& msg) {
	if (!pCurClient->hasSelectedCharacter()) {
		server.handleClientError(pCurClient, "handleMessageGameFriendListRequest");
		return false;
	}

	// todo: send the actual friend entries to the client

	// send end of friend list
	MsgGameFriendListEnd endMsg;
	server.queueMessageForSending(endMsg, pCurClient, true);

	return true;
}
// %%GENERATOR_END%%HANDLER_IMPL%%