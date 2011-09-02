#ifndef FWServer_h__
#define FWServer_h__

#include "MinServer.h"
#include "ModuleCharacters.h"
#include "ModuleCharacterInteraction.h"
#include "ModuleFriends.h"
#include "ModuleGeneric.h"
#include "ModuleItems.h"
#include "ModulePlayfields.h"
#include "ModuleQuests.h"
#include "ModuleUsers.h"

#include "FWSMessageIDs.h"
//#include "world_objects/Gameserver.h"
#include "world_objects/WorldObjectTypeDefs.h"
#include "messages/MessageHandler.h"


#include "GenericClock.h"
#include "ConfigSetting.h"

#include "mysql++.h"
#include <map>
#include <string>
#include <vector>

namespace fwworld {
	class FWWorld;
	class User;
	class Playfield;
}

using namespace fwworld;


namespace fws {

class FWClient;
//class FWWorld;

class FWServer : public min::MinServer {
	typedef bool (FWServer::*fnpMessageHandler)(FWClient* pCurClient, const unsigned char*, unsigned int length);

public:
		static const unsigned int fws_version_major = 1;
		static const unsigned int fws_version_minor = 2;
		static const unsigned int fws_version_sub = 6;

		static const unsigned int required_client_version_major = 1;
		static const unsigned int required_client_version_minor = 4;
		static const unsigned int required_client_version_sub = 4;

		FWServer(int newPort, std::string configFileName);
		virtual ~FWServer();

		std::map<int, FWClient*> receivers;	// the receivers map is for the test messages only
		std::map<int, FWClient*> allClients;

		inline ModuleGeneric* getModuleGeneric() {return serverModuleGeneric;}
		inline ModuleCharacters* getModuleCharacters() {return serverModuleCharacters;}
		inline ModuleCharacterInteraction* getModuleCharacterInteraction() {return serverModuleCharacterInteraction;}
		inline ModuleFriends* getModuleFriends() {return serverModuleFriends;}
		inline ModuleItems* getModuleItems() {return serverModuleItems;}
		inline ModulePlayfields* getModulePlayfields() {return serverModulePlayfields;}
		inline ModuleQuests* getModuleQuests() {return serverModuleQuests;}
		inline ModuleUsers* getModuleUsers() {return serverModuleUsers;}


		void removeClient(FWClient* client, bool requestDisconnectInParent=true);
		void handleClientError(FWClient* pCurClient, const std::string& info = "", bool disconnect = true);
		void onClientSuspiciousAction(FWClient* pCurClient, const std::string& info = "", bool disconnect = true);
		void onClientError(FWClient* pCurClient, const std::string& info = "", bool disconnect = true);

		// common logic (in FWSCommonLogic.cpp)
		void onSuccessfulLogin(SPUser u, FWClient* pCurClient, const std::string& challengeNumber, int newCounter);
		void sendAvailableCharacterClassesToUser(FWClient* pCurClient);
		std::string generatePasswordResetCode(unsigned int codeLength);
		bool setNewPasswordResetCodeForUser(const User& u);
		bool resetPasswordForUser(User& u, std::string& resetCode, const std::string& newPassword);
		bool checkGenericPlayfieldAccess(FWClient* pCurClient, const Character& c, fwworld::Playfield* playfield);
		void broadCastMessageToCharacters(const std::vector<Character*>& others, min::MinMessage& msg, unsigned int excludeId=0);
		void onCharacterCellChange(Character* c, FWClient* pCurClient, Playfield* pf, unsigned int curCellX, unsigned int curCellY, unsigned int newCellX, unsigned int newCellY);
		// end common logic

	protected:
		// implemented methods of the base class
		virtual bool init();
		virtual void executeMessage(min::MinClient* pCurClient, const unsigned char* msg);
		virtual void executeLogic();
		virtual void onClientRemoval(min::MinClient* client, min::MinClient::ClientRemovalInfo reason);
		virtual void onClientConnect(min::MinClient* client);
		virtual void onServerStart();
		virtual void onServerStop();
		virtual min::MinClient* newClientInstance();


		/**  Global world clock. */
		fwutil::GenericClock clock;
		/**  Global world database connection. */
		mysqlpp::Connection conn;

		/** Global world. */
		FWWorld* world;




	private:
		/** Name of the file used to read the config settings (db settings etc.). */
		std::string configFileName;
		/** The settings which are read from the config file (usually config.txt). */
		fwutil::ConfigSetting configSettings;

		clock_ms_t lastMediumPulse;
		clock_ms_t lastLongPulse;
		clock_ms_t lastXLongPulse;
		clock_ms_t lastHourBatchPulse;
		//clock_ms_t lastEternityPulse;
		

		//FWServer::fnpMessageHandler messageHandlers[FWSMessageIDs::MAX_MESSAGE_ID];
		MessageHandlerBase* messageHandlers[FWSMessageIDs::MAX_MESSAGE_ID];

		ModuleGeneric* serverModuleGeneric;
		ModuleCharacters* serverModuleCharacters;
		ModuleCharacterInteraction* serverModuleCharacterInteraction;
		ModuleFriends* serverModuleFriends;
		ModuleItems* serverModuleItems;
		ModulePlayfields* serverModulePlayfields;
		ModuleQuests* serverModuleQuests;
		ModuleUsers* serverModuleUsers;

        bool messageHandlersRegistered;

		bool initDB();
		void registerMessageHandlers();
		void cleanupMessageHandlers();

		bool packetOK(FWClient* pCurClient, const unsigned char* msg, unsigned int id, int len);
		void pulseDB();
		void checkRemoveInactiveClients();

};

}

#endif // FWServer_h__
