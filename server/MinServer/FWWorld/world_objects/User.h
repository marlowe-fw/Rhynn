#ifndef User_h__
#define User_h__

#include "DBSynchObject.h"
#include "DBSynchObjectMacros.h"
#include "WorldObjectTypeDefs.h"
#include "UserSession.h"
#include "Character.h"
#include "CharacterClass.h"
#include "DBHelper.h"
#include "DateTime.h"
#include "db_fieldtypes/AvailableStatus.h"
#include "db_fieldtypes/UserPremiumStatus.h"
#include "db_fieldtypes/UserSystemStatus.h"
#include "db_fieldtypes/UserType.h"
#include "db_fieldtypes/SystemStatus.h"
#include <iostream>


namespace fws {
	class FWClient;
}

namespace fwworld {

class FWWorld;

class User : public DBSynchObject {

	protected:
		bool loadFromResultRow(const mysqlpp::Row& row);

	public:

		static const unsigned int max_characters_premium = 20;
		static const unsigned int max_characters_default = 4;

		User(FWWorld& world);
		virtual ~User();

		virtual bool storeToDB();
		virtual bool loadFromDB(unsigned int existingId);
		virtual bool removeFromDB();

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string&, name, Name, users, name, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string&, password, Password, users, password, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string&, email, Email, users, email, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const UserPremiumStatus&, premiumStatus, PremiumStatus, users, premium_status, .str())
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const UserSystemStatus&, userSystemStatus, UserSystemStatus, users, user_system_status, .str())
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const UserType&, type, Type, users, type, .str())
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const fwutil::DateTime&, premiumExpiryDate, PremiumExpiryDate, users, premium_expiry_date, .strSQL())
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const fwutil::DateTime&, registeredDate, RegisteredDate, users, registered_date, .strSQL())

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const SystemStatus&, systemStatus, SystemStatus, users, system_status, .str())
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const fwutil::DateTime&, wbLastChanged, WbLastChanged, users, wb_last_changed, .strSQL())
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, debugLoglevel, DebugLoglevel, users, debug_loglevel, )
		

		inline void setChallengeNumber(const std::string& newNumber) {
			challengeNumber = newNumber;
		}

		inline const std::string& getChallengeNumber() {
			return challengeNumber;
		}

		// This is used only for sending the list of characters
		inline std::map<unsigned int, SPCharacter>& getCharacters() {
			return characters;
		}

		inline unsigned int getCharacterCount() {
			return static_cast<unsigned int>(characters.size());
		}

		inline void setClient( fws::FWClient* newClient) {client = newClient;}
		inline fws::FWClient* getClient() const {return client;}

		inline UserSession& getUserSession() {return *userSession;}
		inline Character* getSelectedCharacter() {
			if (selectedCharacter != 0) {
				return selectedCharacter.get();
			}
			return 0;
		}

		bool hasCharacter(unsigned int characterId);
		inline bool hasSelectedCharacter() {return selectedCharacter != 0;}

		inline bool getIsValidated() {return isValidated;}
		inline void setIsValidated(bool newValue) {isValidated = newValue;}

		void onValidationCompleted();
		bool loadCharactersWithoutDependencies();
		bool setCharacterDeleted(unsigned int characterId);
		bool renameCharacter(unsigned int characterId, const std::string& newName);
		void addCharacter(SPCharacter newCharacter);
		Character* selectCharacter(unsigned int characterId);

		bool canCreateAdditionalCharacter(std::string& infoMessage);
		bool canUseCharacterClass(const CharacterClass& characterClass, std::string& infoMessage);
		bool canCreateCharacter(const CharacterClass& characterClass, std::string &infoMessage);

		void logDebug(const std::string& msg, unsigned int logLevel);
		void logSuspiciousAction(const std::string& msg);
		void logKeyAction(const std::string& msg);
		void logError(const std::string& msg);

		static unsigned int getIdForNameAndPassword(FWWorld& world, const std::string& newName, const std::string& newPass);
		static unsigned int getIdForName(FWWorld& world, const std::string& newName);
		static unsigned int getIdForEmail(FWWorld& world, const std::string& email);
		static bool validName(const std::string& newName);
		static bool validPassword(const std::string& newPass);
		static bool validEmail(const std::string& input);
		static const std::string encryptPassword(const std::string& newPass);

	private:
		std::string name;
		std::string password;
		std::string email;
        UserType type;
		UserSystemStatus userSystemStatus;
		UserPremiumStatus premiumStatus;
		fwutil::DateTime premiumExpiryDate;
		fwutil::DateTime registeredDate;

		SystemStatus systemStatus;
		fwutil::DateTime wbLastChanged;
		
		unsigned int debugLoglevel;

		// non-db members:

		/**  User has a link back to the owner client, this may be null but should never be null for live / logged in users. */
		fws::FWClient* client;

		/** User session associated with this user. */
		SPUserSession userSession;

		/** The character this user has selected for playing, may be null as long as no character is selected. */
		SPCharacter selectedCharacter;

		/** All characters owned by this user, the selected character (if any) is also part of the map (same reference as selectedCharacter). */
		std::map<unsigned int, SPCharacter> characters;

		/** The challenge number string which is sent to the client after logging the user in. */
		std::string challengeNumber;

		/** Flag to indicate if this user has successfully logged in and has also correctly send back the challenge number. */
		bool isValidated;

		// Character* character // ownership
			// inventory // ownership of items in inventory
			// equipment
			// belt
			// questelements
};


}	// end namespace fwworld

#endif // User_h__
