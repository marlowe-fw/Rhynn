#ifndef ModuleUsers_h__
#define ModuleUsers_h__

// %%GENERATOR_START%%MESSAGE_INCLUDES%%
#include "messages/MsgGameUserRegisterRequest.h"
#include "messages/MsgGameUserGetEmailRequest.h"
#include "messages/MsgGameUserEmailChangeRequest.h"
#include "messages/MsgGameUserLoginRequest.h"
#include "messages/MsgGameUserChallengenumber.h"
#include "messages/MsgGameUserPasswordResetCodeRequest.h"
#include "messages/MsgGameUserPasswordResetNewRequest.h"
// %%GENERATOR_END%%MESSAGE_INCLUDES%%

namespace fwworld {
	class FWWorld;
	class User;
}

using namespace fwworld;

namespace fws {

	class FWServer;
	class FWClient;

	class ModuleUsers {

	public:
		ModuleUsers(FWServer& server, FWWorld& world);

		// %%GENERATOR_START%%HANDLER_DECL%%
	bool handleMessageGameUserRegisterRequest(FWClient* pCurClient, MsgGameUserRegisterRequest& msg);
	bool handleMessageGameUserGetEmailRequest(FWClient* pCurClient, MsgGameUserGetEmailRequest& msg);
	bool handleMessageGameUserEmailChangeRequest(FWClient* pCurClient, MsgGameUserEmailChangeRequest& msg);
	bool handleMessageGameUserLoginRequest(FWClient* pCurClient, MsgGameUserLoginRequest& msg);
	bool handleMessageGameUserChallengenumber(FWClient* pCurClient, MsgGameUserChallengenumber& msg);
	bool handleMessageGameUserPasswordResetCodeRequest(FWClient* pCurClient, MsgGameUserPasswordResetCodeRequest& msg);
	bool handleMessageGameUserPasswordResetNewRequest(FWClient* pCurClient, MsgGameUserPasswordResetNewRequest& msg);
		// %%GENERATOR_END%%HANDLER_DECL%%

	void removeUser(User* user);

	private:
		FWServer& server;
		FWWorld& world;
	};


}
#endif // ModuleUsers_h__