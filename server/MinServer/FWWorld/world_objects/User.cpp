#include "User.h"
#include "../FWWorld.h"
#include <boost/regex.hpp>


using namespace fwworld;

User::User(FWWorld& world) : DBSynchObject(world),
name(""),
password(""),
email(""),
type(UserType::user),
userSystemStatus(UserSystemStatus::active),
premiumStatus(UserPremiumStatus::none),
premiumExpiryDate(),
registeredDate(),
systemStatus(SystemStatus::normal),
wbLastChanged(),
debugLoglevel(0),
client(0),
userSession(SPUserSession(_TRACK_NEW(new UserSession(world)))),
challengeNumber(""), isValidated(false)
{
	objectTypeId = WorldObject::otUser;
}


User::~User() {
}

bool User::storeToDB() {
	mysqlpp::Query query = world.conn.query();
	if (id == 0) {
		// insert new user record
		query	<< "insert into users (name, password, email, type, user_system_status, premium_status, premium_expiry_date, registered_date, \
				   system_status, wb_last_changed, debug_loglevel) \
				values ("
				<< mysqlpp::quote << name << ","
				<< mysqlpp::quote << password << ","
				<< mysqlpp::quote << email << ","
				<< mysqlpp::quote << type.str() << ","
				<< mysqlpp::quote << userSystemStatus.str() << ","
				<< mysqlpp::quote << premiumStatus.str() << ","
				<< mysqlpp::quote << premiumExpiryDate.strSQL() << ","
				<< mysqlpp::quote << registeredDate.strSQL() << ","
				<< mysqlpp::quote << systemStatus.str() << "," << mysqlpp::quote << wbLastChanged.strSQL() << "," << debugLoglevel
				<< ")";
	} else {
		// update user record
		query	<< "update users set "
			<< "name = " << mysqlpp::quote << name << ","
			<< "password = " << mysqlpp::quote << password << ","
			<< "email = " << mysqlpp::quote << email << ","
			<< "type = " << mysqlpp::quote << type.str() << ","
			<< "user_system_status = " << mysqlpp::quote << userSystemStatus.str() << ","
			<< "premium_status = " << mysqlpp::quote << premiumStatus.str() << ","
			<< "premium_expiry_date = " << mysqlpp::quote << premiumExpiryDate.strSQL() << ","
			<< "registered_date = " << mysqlpp::quote << registeredDate.strSQL()
			<< ", system_status = " << mysqlpp::quote << systemStatus.str()
			<< ", wb_last_changed = " << mysqlpp::quote << wbLastChanged.strSQL()
			<< ", debug_loglevel = " << debugLoglevel
			<< " where id = " << id;
	}

	if (storeByQuery(query, "User store")) {
		//userSession.endSession();
		if (userSession.get() != 0) {
			userSession->storeToDBIfNotSynchronized();
		}
		return true;
	}
	return false;
}


bool User::loadFromDB(unsigned int existingId) {
	mysqlpp::Query query = world.conn.query();
	query << "select * from users where id = " << existingId;
	mysqlpp::StoreQueryResult res;

	fwutil::DBHelper::select(query, &res, "User load");

	if (res && res.num_rows() > 0) {
		return loadFromResultRow(res[0]);
	}
	return false;
}


/**
* A helper function, retrieve object values from a mysqlpp result row.
* @param row The result row to read the value from
* @return true on success, false otherwise
*/
bool User::loadFromResultRow(const mysqlpp::Row& row) {
	id = row["id"];
	name = row["name"].c_str();
	password = row["password"].c_str();
	email = row["email"].c_str();
	type = row["type"].c_str();
	userSystemStatus = row["user_system_status"].c_str();
	premiumStatus = row["premium_status"].c_str();
	premiumExpiryDate.setFromSQL(row["premium_expiry_date"].c_str());
	registeredDate.setFromSQL(row["registered_date"].c_str());
	systemStatus = row["system_status"].c_str();
	wbLastChanged.setFromSQL(row["wb_last_changed"].c_str());
	debugLoglevel = row["debug_loglevel"];

	onSuccessfulLoad();
	return true;
}

/**
 * Remove the given user from the database.
 * @return true on success, false otherwise
 */
bool User::removeFromDB() {
	if (id > 0) {
		mysqlpp::Query query = world.conn.query();
		query << "delete from users where id = " << mysqlpp::quote << id;
		return removeByQuery(query, "User remove");
	}
	return false;
}

/**
 * Execute common logic necessary after the user has been validated (usually after completing the challenge number check).
 */
void User::onValidationCompleted() {
	// from here onwards the user is considered to be validated
	setIsValidated(true);
	// as the user is validated we may now load in the characters including a reference to their respective character class but without any other dependencies
	// it is important to load the characters at this point as the logic relies on the fact that a validated user has all his characters loaded (for things
	// like checking if another character can be created for example)
	logKeyAction("User validated");
	loadCharactersWithoutDependencies();
}

/**
 * Load the characters belonging to this user. Note that for normal game operation the characters of a user should only be loaded
 * when the user is fully validated after logging in. Note that this does only a flat load of the characters without the
 * dependencies the characters might have (items etc.), the characterClass reference is however  loaded for the character objects.
 * @return true on success, false otherwise
 */
bool User::loadCharactersWithoutDependencies() {
	mysqlpp::Query query = world.conn.query();
	query << "select * from characters where user_id = " << id << " and system_status <> 'deleted' order by created_date asc";
	mysqlpp::StoreQueryResult res;

	fwutil::DBHelper::select(query, &res, "User::loadCharactersWithoutDependencies");

	//std::cout << "load, num: " << res.num_rows() << std::endl;

	if (res && res.num_rows() > 0) {
		size_t numRows = res.num_rows();
		for (size_t i=0; i<numRows; i++) {
			SPCharacter c(_TRACK_NEW(new Character(world)));
			if (c->loadFromResultRow(res[i], true)) {
				//std::cout << "adding character of user: " << c->getId() << ": " << c->getName() << std::endl;
				characters.insert(std::pair<unsigned int, SPCharacter>(c->getId(), c));
			}
		}
	}
	return true;
}

/**
 * Set a character of the user to be deleted, which does not cause the DB record to be deleted but instead sets the character's
 * status flag to deleted. For the user however this is transparent and the character appears to be deleted.
 * Note that the caller must check that the user must not have this character as the currently selected character.
 * In fact, the user can only delete character in user management mode which implies that the user did not yet select
 * any character.
 * @param characterId The DB id of the character which should be set to deleted, this must be an id of a character which is
 * actually owned by the user - this is checked in the function by as the character must be found in the characters container.
 * @return true on success, false otherwise
 */
bool User::setCharacterDeleted(unsigned int characterId) {
	// get character from container of owned characters
	std::map<unsigned int, SPCharacter>::iterator cIt = characters.find(characterId);
	if (cIt != characters.end()) {
		SPCharacter c = (*cIt).second;
		// we should save this immediately, or the change will be lost
		// since the characters inside the characters array of the user are not saved when the client / user is removed or periodically stored
		c->setSystemStatus(SystemStatus::deleted, true);
		// remove from container
		characters.erase(cIt);
		return true;
	}
	return false;
}

/**
 * Check whether or not this user owns a character with the given id.
 * @param characterId The character id to look for.
 * @return true if user owns the character, false otherwise
 */
bool User::hasCharacter(unsigned int characterId) {
	// get character from container of owned characters
	std::map<unsigned int, SPCharacter>::iterator cIt = characters.find(characterId);
	return (cIt != characters.end());
}

/**
* Rename character of this user.
* @param characterId The character id to look for.
* @return true if renaming was successful, false otherwise
*/
bool User::renameCharacter(unsigned int characterId, const std::string& newName) {
	// get character from container of owned characters
	std::map<unsigned int, SPCharacter>::iterator cIt = characters.find(characterId);
	if (cIt != characters.end()) {
		SPCharacter c = (*cIt).second;
		// save immediately, as the characters of the user are not saved at any later point
		c->setName(newName, true);
		return true;
	}
	return false;
}


/**
 * Add a character for the given user (add to the flat characters map).
 * @param newCharacter A smart pointer to the character object.
 */
void User::addCharacter(SPCharacter newCharacter) {
	characters.insert(std::pair<unsigned int, SPCharacter>(newCharacter->getId(), newCharacter));
}

/**
* Check if this user may create a new character of the given class given his premium status.
* @param characterClass The character class which is intended as the class of the character to be created.
* @param infoMessage Changed by reference to contain potential fail messages.
* @return true if user may create a character as requested, false otherwise
*/
bool User::canUseCharacterClass(const CharacterClass& characterClass, std::string& infoMessage) {
	infoMessage = "";

	bool isPremium = (premiumStatus == UserPremiumStatus::premium);

	AvailableStatus availableStatus = characterClass.getAvailableStatus();
	if (availableStatus == AvailableStatus::all) {
		return true;
	} else if (availableStatus == AvailableStatus::premium_only) {
		if (isPremium) {
			return true;
		} else {
			infoMessage = "This character class is only available for premium accounts. See www.rhynn.com";
		}
	} else {
		userSession->logSuspiciousAction("User tried to create a character for a class not available: " + characterClass.getDisplayName());
		infoMessage = "Class not available.";
	}

	return false;
}

/**
* Check if this user may create a new character given his number of already created characters.
* @param infoMessage Changed by reference to contain potential fail messages.
* @return true if user may create a character as requested, false otherwise
*/
bool User::canCreateAdditionalCharacter(std::string& infoMessage) {
	infoMessage = "";
	// check number of characters
	// premium users may create a higher number of characters
	bool isPremium = (premiumStatus == UserPremiumStatus::premium);


	// if we got this far, check if the max. number of characters won't be exceeded by adding one more for this character
	if (
		(isPremium && characters.size() < User::max_characters_premium) || // premium user may create more
		(!isPremium && characters.size() < User::max_characters_default)
		) {
			// may create more characters
			return true;
	} else {
		// may NOT create more characters (limit reached)
		if (!isPremium) {
			infoMessage = "You need a premium account to create more than " + fwutil::Common::intToString(User::max_characters_default) + " characters per account. More info at: www.rhynn.com";
		} else {
			infoMessage = "You cannot have more than " + fwutil::Common::intToString(User::max_characters_premium) + "characters per account.";
		}
		return false;
	}
}

/**
* Check if this user may create a new character given his number of already created characters and given the character class requested.
* @param characterClass The character class which is intended as the class of the character to be created.
* @param infoMessage Changed by reference to contain potential fail messages.
* @return true if user may create a character as requested, false otherwise
*/
bool User::canCreateCharacter(const CharacterClass& characterClass, std::string &infoMessage) {
	return (canCreateAdditionalCharacter(infoMessage) && canUseCharacterClass(characterClass, infoMessage));
}

/**
 * Select a character from the list of characters owned by this user. The selected character from here on is the character which is used
 * for conducting any actions in the game world.
 * @characterId The object id of the character which should be selected by the user.
 * @return A raw pointer to the selected character object, suitable for insertion into the world's live characters
 */
Character* User::selectCharacter(unsigned int characterId) {
	std::map<unsigned int, SPCharacter>::iterator cIt = characters.find(characterId);
	if (cIt != characters.end()) {
		selectedCharacter = (*cIt).second;
		// the character should know the user to allow tracing back to the client if required
		selectedCharacter->setUser(this);
		userSession->setCharacterId(characterId);
		return selectedCharacter.get();
	}
	return 0;
}

/**
 * Add a message to the debug log if this user's debug log level is equal to or higher than the requested log level.
 */
void User::logDebug(const std::string& msg, unsigned int requestedLogLevel) {
	if (requestedLogLevel <= debugLoglevel) {
		userSession->logDebug(msg);
	}
}

void User::logSuspiciousAction(const std::string& msg) {
	userSession->logSuspiciousAction(msg);
}

void User::logKeyAction(const std::string& msg) {
	userSession->logKeyAction(msg);
}

void User::logError(const std::string& msg) {
	userSession->logError(msg);
}


// -------------------
// static
// -------------------
unsigned int User::getIdForName(FWWorld& world, const std::string& newName) {
	unsigned int existingId = 0;

	mysqlpp::Query query = world.conn.query();
	query << "select id from users where name = BINARY " << mysqlpp::quote << newName;

	mysqlpp::StoreQueryResult res;

	fwutil::DBHelper::select(query, &res, "getIdForName");

	if (res && res.num_rows() > 0) {
		existingId = res[0]["id"];
	}
	return existingId;
}


unsigned int User::getIdForNameAndPassword(FWWorld& world, const std::string& newName, const std::string& newPass) {
	unsigned int existingId = 0;

	mysqlpp::Query query = world.conn.query();
	query << "select id from users where name = BINARY " << mysqlpp::quote << newName << " and password = BINARY " << mysqlpp::quote << newPass;

	mysqlpp::StoreQueryResult res;

	fwutil::DBHelper::select(query, &res, "getIdForNameAndPassword");

	if (res && res.num_rows() > 0) {
		existingId = res[0]["id"];
	}
	return existingId;
}

unsigned int User::getIdForEmail(FWWorld& world, const std::string& email) {
	unsigned int existingId = 0;

	mysqlpp::Query query = world.conn.query();
	query << "select id from users where email = " << mysqlpp::quote << email;
	mysqlpp::StoreQueryResult res;
	fwutil::DBHelper::select(query, &res, "getIdForEmail");
	if (res && res.num_rows() > 0) {
		existingId = res[0]["id"];
	}
	return existingId;
}

bool User::validName(const std::string& newName) {
	// allow lower case for now only, otherwise use boost::regex_constants::icase as the second param
	static const boost::regex nameRegex("^([a-z0-9.\\-_]){4,10}$");
	return boost::regex_match(newName, nameRegex);
}

bool User::validPassword(const std::string& newPass) {
	// allow lower case for now only, otherwise use boost::regex_constants::icase as the second param
	static const boost::regex passRegex("^([a-z0-9.\\-_]){4,10}$");
	return boost::regex_match(newPass, passRegex);
}

bool User::validEmail(const std::string& input) {
	static const boost::regex testRegex("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.(?:com|org|net|gov|biz|info|name|aero|biz|info|jobs|museum|[A-Z]{2})$", boost::regex_constants::icase);
	return boost::regex_match(input, testRegex);
}

const std::string User::encryptPassword(const std::string& newPass) {
	return "";
}
