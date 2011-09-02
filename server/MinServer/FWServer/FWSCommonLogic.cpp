#include "FWServer.h"
#include "FWClient.h"
#include "FWWorld.h"
#include "world_objects/Playfield.h"

#include "Random.h"

#include "messages/MsgGameUserCharacterClassForList.h"
#include "messages/MsgGameItemInventoryEnd.h"
#include "messages/MsgGameCharacterAdd.h"
#include "messages/MsgGameCharacterRemove.h"
#include "messages/MsgGameItemAdd.h"
#include "messages/MsgGameItemRemove.h"
#include "messages/MsgGamePong.h"

#include "mysql++.h"
#include <boost/algorithm/string.hpp>

using namespace fws;
using namespace fwworld;




/**
 * Helper method to send all character classes which are available to choose from for the user.
 * @param pCurClient The client to send the character classes to
 */
void FWServer::sendAvailableCharacterClassesToUser(FWClient* pCurClient) {
	// send back the character classes to the client so the values are known by the client
	std::map<unsigned int, SPCharacterClass> chClasses = world->characterClasses;
	for(std::map<unsigned int, SPCharacterClass>::iterator it = chClasses.begin(); it != chClasses.end(); ++it) {
		SPCharacterClass curClass = (*it).second;
		AvailableStatus curAvStatus = curClass->getAvailableStatus();
		// only consider those freely available and those available for premium users
		// non-premium users will also see the premium-only classes on character creation as an incentive to go premium
		if (curAvStatus == AvailableStatus::all || curAvStatus == AvailableStatus::premium_only) {
			MsgGameUserCharacterClassForList msgChClass;
			msgChClass.fromCharacterClass(*curClass);
			queueMessageForSending(msgChClass, pCurClient, true);
		}
	}
}

/**
 * Common logic to be executed when a user has successfully logged in.
 * This is just to factor out some of the code form the user management message handler.
 * @param u The user object (smart pointer)
 * @param pCurClient The client which will be associated with the user (client owns the user and therefore stores the smart pointer to the user)
 * @param challengeNumber The challengeNumber for this user which must be sent back to have some level of verification that the normal client program is being used
 * @param newCounter The initial counter value to use which server for generating the message signature which is sent with every message, the counter is increased after each sent message from the client
 */
void FWServer::onSuccessfulLogin(SPUser u, FWClient* pCurClient, const std::string& challengeNumber, int newCounter) {
	// save challenge number for user object
	u->setChallengeNumber(challengeNumber);
	// setup the client to use message signatures from here onwards, using the new packet counter
	pCurClient->initializePacketValidator(newCounter);
	pCurClient->setUsesMessageSignatures(true);
	// finally set the user object for the client (smart pointer) and make the user aware of the client, too
	// client owns the user as the client takes care of the shared_ptr
	pCurClient->setUser(u);
	u->setClient(pCurClient);

	// start user session
	UserSession& session = u->getUserSession();

	session.startSession();

	session.setUserId(u->getId(), true);	// also saves the session to db

	// assign to live users, map lookup by id, remember to remove from liveUsers whenever suitable
	//world->liveUsers.insert(std::pair<unsigned int, User*>(u->getId(), u.get()));
	world->addLiveUser(u.get());

	// send the character classes available to the user to choose from (also used to join in the modifiers and base values for the selected character on the client
	sendAvailableCharacterClassesToUser(pCurClient);

}

/**
 * Create an alphanumeric code of the given length which can be used to do authorized password resets.
 * @param codeLength The length of the code which should be generated
 * @return The code as a string
 */
std::string FWServer::generatePasswordResetCode(unsigned int codeLength) {
	std::string charCode;
	charCode.reserve(codeLength);

	// only chars which cannot be mixed up easily such as l (L) and 1 (one)
	std::string validChars = "2346789ABCDEFHJKLMNPQRSTUVWXYZ2346789";

	unsigned int numValidChars = (unsigned int)(validChars.length());

	for (unsigned int i=0; i<codeLength; i++) {
		int randPos = fwutil::Random::nextInt(numValidChars-1);
		charCode.append(1, validChars[randPos]);
	}

	return charCode;
}


/**
 * Set the password reset code for the given user, if possible. This assumes, checks for valid e-mail etc. have been done before this function is called.
 * Any existing reset-code requests of this user will be set to 'discarded' in the account_recovery table.
 * @param u A reference to the user object for whom the reset-code record should be added.
 * @return true on success, false otherwise
 */
bool FWServer::setNewPasswordResetCodeForUser(const User& u) {
	std::string resetCode = generatePasswordResetCode(6);

	mysqlpp::Query query = world->conn.query();
	// update the status of any pending code requests for this user to discarded
	query << "UPDATE account_recovery SET status = 'discarded' where user_id = " << u.getId();
	if (fwutil::DBHelper::exec(query, "set reset codes discarded")) {
		// insert new record
		query = world->conn.query();
		query << "INSERT INTO account_recovery (user_id, email, reset_code, created_date, sent, status) VALUES ("
			<< u.getId() << ", "
			<< mysqlpp::quote << u.getEmail() << ", "
			<< mysqlpp::quote << resetCode << ", "
			<< "NOW(), '0000-00-00 00:00:00', 'pending')";
		if (fwutil::DBHelper::exec(query, "insert new reset code")) {
			// everything worked well, new reset code was inserted
			return true;
		}
	}
	return false;
}

/**
 * Reset the password for a user to the newly defined password. This checks if a record exists in the account_recovery table which matches the given reset-code.
 * Password and user checking must be done by the caller before calling this function.
 * @param u Reference to the user object for which the password should be reset.
 * @param resetCode The reset-code sent by the user which must match the one found in the db.
 * @param hashedPassword The password in SHA1 hashed form.
 * @return true if the password was reset to the new value, false otherwise
 */
bool FWServer::resetPasswordForUser(User& u, std::string& resetCode, const std::string& hashedPassword) {
	mysqlpp::Query query = world->conn.query();
	// get stored reset-code entry
	query << "SELECT id, reset_code from account_recovery where status = 'sent' and user_id = " << u.getId() << " ORDER BY sent DESC limit 1";

	mysqlpp::StoreQueryResult res;
	if (fwutil::DBHelper::select(query, &res, "reset pass for user")) {
		if (res && res.num_rows() > 0) {
			mysqlpp::Row& row = res[0];
			// compare sent reset-code
			boost::trim(resetCode);
			std::string dbResetCode = row["reset_code"].c_str();
			boost::to_lower(dbResetCode);
			if (dbResetCode == resetCode) {
				// reset code matches, so set the new password, be sure to store it as otherwise the changes would be lost
				u.setPassword(hashedPassword, true);

				// update the db record for the rest code so we know this code has been used
				query = world->conn.query();
				query << "UPDATE account_recovery SET status = 'used' where id = " << row["id"];
				fwutil::DBHelper::exec(query, "update reset code record to used");

				return true;
			}
		}
	}
	return false;
}


/**
 * Check if a client can access a playfield using the currently active character.
 * @param pCurClient The net client
 * @param c The selected character
 * @param playfield The playfield for which to check access
 * @return true if access is ok, false otherwise
 */
bool FWServer::checkGenericPlayfieldAccess(FWClient* pCurClient, const Character& c, fwworld::Playfield* playfield) {

	if (playfield == 0) {
		onClientError(pCurClient, "Character had a playfield set which does not exist: " + fwutil::Common::intToString(c.getPlayfieldId()));
		return false;
	}

	// premium check
	bool isPremium = (pCurClient->getUser()->getPremiumStatus() == UserPremiumStatus::premium);
	fwworld::AvailableStatus availableStatus = playfield->getAvailableStatus();

	if (availableStatus == AvailableStatus::premium_only) {
		if (!isPremium) {
			onClientSuspiciousAction(pCurClient, "Character (non-premium) illegally tried request data for a playfield which is premium: " + fwutil::Common::intToString(c.getId()) + " pf: " + fwutil::Common::intToString(playfield->getId()) );
			return false;
		}
	} else if (availableStatus != AvailableStatus::all) {
		onClientSuspiciousAction(pCurClient, "Character illegally tried to request data for a playfield which is not available: " + fwutil::Common::intToString(c.getId()) + " pf: " + fwutil::Common::intToString(playfield->getId()) );
		return false;
	}
	return true;
}

void FWServer::broadCastMessageToCharacters(const std::vector<Character*>& others, min::MinMessage& msg, unsigned int excludeId/*=0*/) {
	for (std::vector<Character*>::const_iterator it = others.begin(); it!=others.end(); it++) {
		Character* otherChar = (*it);
		if (excludeId==0 || otherChar->getId() != excludeId) {
			User* u = otherChar->getUser();
			if (u!=0) {
				// user
				queueMessageForSending(msg, u->getClient(), true);
			} else {
				// bot
			}
		}
	}
}


void FWServer::onCharacterCellChange(Character* c, FWClient* pCurClient, Playfield* pf, unsigned int curCellX, unsigned int curCellY, unsigned int newCellX, unsigned int newCellY) {
		PlayfieldCell** cells = pf->getCells();

		cells[curCellX][curCellY].removeCharacterById(c->getId());
		cells[newCellX][newCellY].addCharacter(c);

		std::vector<Character*> charsToAdd;
		std::vector<Character*> charsToRemove;
		std::vector<Item*> itemsToAdd;
		std::vector<Item*> itemsToRemove;

		pf->appendObjectsAffectedByCellChange(curCellX, curCellY, newCellX, newCellY, &charsToAdd, &charsToRemove, &itemsToAdd, &itemsToRemove);

		MsgGameCharacterAdd msgAdd;
		msgAdd.fromCharacter(*c);

		MsgGameCharacterRemove msgRemove;
		msgRemove.objectId = c->getId();

		// inform add character for cells now in range
		for (std::vector<Character*>::iterator it = charsToAdd.begin(); it!=charsToAdd.end(); it++) {
			Character* otherChar = (*it);
			User* u = otherChar->getUser();
			if (u!=0) {
				// user
				queueMessageForSending(msgAdd, u->getClient(), true);
			} else {
				// bot
			}
			if (otherChar->getId() != c->getId()) {
				// inform own character that this character is now in range
				MsgGameCharacterAdd msgAddOther;
				msgAddOther.fromCharacter(*otherChar);
				queueMessageForSending(msgAddOther, pCurClient, true);
			}
		}

		// inform remove character for cells now out of range
		for (std::vector<Character*>::iterator it = charsToRemove.begin(); it!=charsToRemove.end(); it++) {
			Character* otherChar = (*it);
			User* u = otherChar->getUser();
			if (u!=0) {
				// user
				queueMessageForSending(msgRemove, u->getClient(), true);
			} else {
				// bot
			}

			if (otherChar->getId() != c->getId()) {
				// inform own character that this character is now out of range
				MsgGameCharacterRemove msgRemoveOther;
				msgRemoveOther.objectId = otherChar->getId();
				queueMessageForSending(msgRemoveOther, pCurClient, true);
			}
		}

		// inform add item for cells now in range
		MsgGameItemAdd msgAddItem;
		for (std::vector<Item*>::iterator it = itemsToAdd.begin(); it!=itemsToAdd.end(); it++) {
			msgAddItem.fromItem(*(*it));
			queueMessageForSending(msgAddItem, pCurClient, true);
		}
		MsgGameItemRemove msgRemoveItem;
		for (std::vector<Item*>::iterator it = itemsToRemove.begin(); it!=itemsToRemove.end(); it++) {
			msgRemoveItem.itemId = (*it)->getId();
			queueMessageForSending(msgRemoveItem, pCurClient, true);
		}


}
