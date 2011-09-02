#include "ModuleCharacters.h"

#include "FWServer.h"
#include "FWClient.h"
#include "world_objects/User.h"
#include "world_objects/CharacterClass.h"
#include "world_objects/components/HighscoreEntry.h"

#include "state/ClientCondition.h"

#include "messages/MsgGameUserGetCharactersRequest.h"
#include "messages/MsgGameUserCharacterCreatePermissionRequest.h"
#include "messages/MsgGameUserCharacterCreatePermissionResult.h"
#include "messages/MsgGameUserCharacterCreateRequest.h"
#include "messages/MsgGameUserCharacterCreateResult.h"
#include "messages/MsgGameUserCharacterForList.h"
#include "messages/MsgGameUserCharacterRenameRequest.h"
#include "messages/MsgGameUserCharacterRenameResult.h"
#include "messages/MsgGameUserCharacterDeleteRequest.h"
#include "messages/MsgGameUserCharacterDeleteResult.h"
#include "messages/MsgGameUserCharacterSelectRequest.h"
#include "messages/MsgGameCharacterHighscoreRequest.h"
#include "messages/MsgGameCharacterHighscoreListEntry.h"
#include "messages/MsgGameCharacterIncreaseVitality.h"


using namespace fws;
using namespace fwworld;
using namespace fwutil;

ModuleCharacters::ModuleCharacters(FWServer& newServer, FWWorld& newWorld) 
: server(newServer), world(newWorld)
{}


// %%GENERATOR_START%%HANDLER_IMPL%%

/**
* Client requests character (co), server will send back the characters (without dependencies), if any.
*/
bool ModuleCharacters::handleMessageGameUserGetCharactersRequest(FWClient* pCurClient, MsgGameUserGetCharactersRequest& msgObj) {
	if (!pCurClient->hasValidatedUser() || pCurClient->getUser()->hasSelectedCharacter()) {
		server.handleClientError(pCurClient, "handleMessageGameUserGetCharactersRequest");
		return false;
	}

	// send all characters of user

	User* u = pCurClient->getUser();
	typedef std::map<unsigned int, SPCharacter> ChMap;
	ChMap& characters = u->getCharacters();
	for (ChMap::iterator it = characters.begin(); it!=characters.end(); it++) {
		MsgGameUserCharacterForList msg;
		SPCharacter c = (*it).second;
		msg.fromCharacter(*c);
		server.queueMessageForSending(msg, pCurClient, true);
	}
	return true;
}

/**
* Client requests permission to create a new character (co). This might not be allowed for non-premium users as the number of characters is limited for each account.
*/
bool ModuleCharacters::handleMessageGameUserCharacterCreatePermissionRequest(FWClient* pCurClient, MsgGameUserCharacterCreatePermissionRequest& msgObj) {
	if (!pCurClient->hasValidatedUser() || pCurClient->hasSelectedCharacter()) {
		server.handleClientError(pCurClient, "handleMessageGameUserCharacterCreatePermissionRequest");
		return false;
	}

	MsgGameUserCharacterCreatePermissionResult returnMsg;
	User* u = pCurClient->getUser();

	if (u->canCreateAdditionalCharacter(returnMsg.infoMessage)) {
		returnMsg.success = 1;
	} else {
		returnMsg.success = 0;
	}

	server.queueMessageForSending(returnMsg, pCurClient, true);
	return true;
}

/**
* Client requests to create a new character (co). Note that some character classes are only available for premium users.
*/
bool ModuleCharacters::handleMessageGameUserCharacterCreateRequest(FWClient* pCurClient, MsgGameUserCharacterCreateRequest& msgObj) {
	if (!pCurClient->hasValidatedUser() || pCurClient->hasSelectedCharacter()) {
		server.handleClientError(pCurClient, "handleMessageGameUserCharacterCreateRequest");
		return false;
	}

	MsgGameUserCharacterCreateResult returnMsg;
	returnMsg.success = 0;

	User* u = pCurClient->getUser();

	// get character class 
	CharacterClass* chClass = world.getCharacterClass(msgObj.classId);
	if (chClass==0) {
		// user requests to create a character class which does not exist - assume this is a hacker
		u->getUserSession().logSuspiciousAction("user tried to request character creation for invalid class: " + fwutil::Common::intToString(msgObj.classId));
		server.removeClient(pCurClient, true);
		return false;
	}	
	
	// main conditions: can create more characters and class available for user 
	if (u->canCreateCharacter(*chClass, returnMsg.infoMessage)) {
		// check for valid name
		if (Character::validName(msgObj.name)) {
			// name valid, check character name taken
			unsigned int existingId = Character::getIdForNameOfUserCharacter(world, msgObj.name);
			if (existingId > 0) {
				returnMsg.infoMessage = "This character name is already used. Try another name.";
			} else {
				// everything fine, actually create character
				SPCharacter newCharacter = Character::createNewCharacterForClass(world, msgObj.classId, u->getId(), msgObj.name, true);
				if (newCharacter != 0) {
					// send back newly created character object for display on the client
					MsgGameUserCharacterForList characterListMsg;
					characterListMsg.fromCharacter(*newCharacter);
					server.queueMessageForSending(characterListMsg, pCurClient, true);
					
					returnMsg.success = 1;
					// add to user's characters
					u->addCharacter(newCharacter);
				} else {
					u->getUserSession().logError("character could not be created, classId: " + Common::intToString(msgObj.classId) + ", requ. name: " + msgObj.name);
					returnMsg.infoMessage = "Character could not be created. Contact the development team at www.rhynn.com.";
				}
			}
		} else {
			// character name invalid
			u->getUserSession().logSuspiciousAction("user tried to create character with invalid name: " + msgObj.name);
			returnMsg.infoMessage = "Invalid character name.";
		}
	} else {
		u->getUserSession().logSuspiciousAction("user tried to create a character without proper permission - after the initial permission check, info: " + returnMsg.infoMessage);
	}

	server.queueMessageForSending(returnMsg, pCurClient, true);

	return true;
}



/**
 * Client requests to rename the given character (co).
 */
bool ModuleCharacters::handleMessageGameUserCharacterRenameRequest(FWClient* pCurClient, MsgGameUserCharacterRenameRequest& msgObj) {
	
	if (true) {
		// at the moment: disallow renaming, needs proper rules
		server.handleClientError(pCurClient, "handleMessageGameUserCharacterCreateRequest");
		return false;
	}


	if (!pCurClient->hasValidatedUser() || pCurClient->hasSelectedCharacter()) {
		server.handleClientError(pCurClient, "handleMessageGameUserCharacterCreateRequest");
		return false;
	}

	User* u = pCurClient->getUser();


	MsgGameUserCharacterRenameResult returnMsg;
	returnMsg.success = 0;

	if (!u->hasCharacter(msgObj.characterId)) {
		// user tried to rename a character which he doesn't possess, drop this client
		u->getUserSession().logSuspiciousAction("user tried to rename a character which he doesn't possess: " + fwutil::Common::intToString(msgObj.characterId));
		server.removeClient(pCurClient, true);
		return false;
	}

	if (Character::validName(msgObj.name)) {
		// name valid, check character name taken
		unsigned int existingId = Character::getIdForNameOfUserCharacter(world, msgObj.name);
		if (existingId > 0 && existingId !=msgObj.characterId) {
			returnMsg.infoMessage = "This character name is already used. Try another name.";
		} else {
			// everything fine, actually rename character
			returnMsg.success = 1;
			returnMsg.characterId = msgObj.characterId;	// id is used on the client to rename the correct list entry
			u->renameCharacter(msgObj.characterId, msgObj.name);
		}
	} else {
		// character name invalid
		u->getUserSession().logSuspiciousAction("user tried to create character with invalid name: " + msgObj.name);
		returnMsg.infoMessage = "Invalid character name.";
	}

	server.queueMessageForSending(returnMsg, pCurClient, true);	
	return true;
}

/**
 * Client requests to delete the given character (co). Note that deleting will merely set the status flag to deleted, such that the user cannot use this character anymore but it remains in the database.
 */
bool ModuleCharacters::handleMessageGameUserCharacterDeleteRequest(FWClient* pCurClient, MsgGameUserCharacterDeleteRequest& msgObj) {
	if (!pCurClient->hasValidatedUser() || pCurClient->hasSelectedCharacter()) {
		server.handleClientError(pCurClient, "handleMessageGameUserCharacterDeleteRequest");
		return false;
	}
	
	MsgGameUserCharacterDeleteResult returnMsg;
	returnMsg.success = 0;

	User* u = pCurClient->getUser();

	if (!u->hasCharacter(msgObj.characterId)) {
		// user tried to delete a character which he doesn't possess, drop this client
		u->getUserSession().logSuspiciousAction("user tried to delete a character which he doesn't possess: " + fwutil::Common::intToString(msgObj.characterId));
		server.removeClient(pCurClient, true);
		return false;
	} else {
		// set the deleted flag for this character
		u->setCharacterDeleted(msgObj.characterId);
		returnMsg.success = 1;
		returnMsg.characterId = msgObj.characterId;	// store character id so client can identify which client to delete
	}

	server.queueMessageForSending(returnMsg, pCurClient, true);
	
	return true;
}

/**
* Client requests to select character for active participation in the game world (co).
*/
bool ModuleCharacters::handleMessageGameUserCharacterSelectRequest(FWClient* pCurClient, MsgGameUserCharacterSelectRequest& msgObj) {
	if (!pCurClient->hasValidatedUser() || pCurClient->hasSelectedCharacter()) {
		server.handleClientError(pCurClient, "handleMessageGameUserCharacterSelectRequest");
		return false;
	}

	// check has character
	User* u = pCurClient->getUser();
	if (!u->hasCharacter(msgObj.characterId)) {
		u->logSuspiciousAction("user tried to select a character which he doesn't own: " + fwutil::Common::intToString(msgObj.characterId));
		server.removeClient(pCurClient, true);
		return false;
	}

	// set selected character for user
	Character* selectedCharacter = u->selectCharacter(msgObj.characterId);

	if (selectedCharacter==0) {
		u->logSuspiciousAction("character of user could not be selected: " + fwutil::Common::intToString(msgObj.characterId));
		server.removeClient(pCurClient, true);		
		return false;
	}

	// if we came this far the character was successfully loaded

	// todo:
	// load quests
	// load visited playfields
	// load friend list
	// load clan list
	// ... etc. 
	// this could be done in the character class (onSelectCharacterByUser)
	selectedCharacter->loadInventory();

	// check playfield
	if (selectedCharacter->getPlayfieldId() == 0) {
		// no playfield set, set initial starting point and respawn pos
		selectedCharacter->setRespawnX(Character::s_initialPlayfieldX);
		selectedCharacter->setRespawnY(Character::s_initialPlayfieldY);
		selectedCharacter->setX(Character::s_initialPlayfieldX);
		selectedCharacter->setY(Character::s_initialPlayfieldY);
		selectedCharacter->setPlayfieldId(Character::s_initialPlayfieldId);
		selectedCharacter->storeToDB();
	}
	// this is the standard direction as we do not currently store the direction to the db
	selectedCharacter->setDirection(Direction::DOWN);

	// todo: later: check if premium and user can access playfield, otherwise teleport to suitable non-premium location (nearest non-premium town)
	// in this case also add a message for the user's account which will be displayed when the world loads on the client 
	// (character inbox -> concept + implement)

	// note we do not add to live characters yet, this is done when the character actually enters the playfield	

	// by convention, after selecting the character, the inventory is sent to the client
	// this is to save sending another message back and forth (select char ok - request inventory)

	server.getModuleItems()->sendInventoryForCharacter(*selectedCharacter);
	return true;
}


/**
 * Request highscores from server for the highscore list (co).
 */
bool ModuleCharacters::handleMessageGameCharacterHighscoreRequest(FWClient* pCurClient, MsgGameCharacterHighscoreRequest& msgObj) {
	std::vector<HighscoreEntry> hsList;
	// currently limit to 10
	// todo: define limit centralized elsewhere
	if (msgObj.numRanks > 10 || !Character::getHighscores(world, msgObj.startRank, msgObj.numRanks, hsList)) {
		server.onClientSuspiciousAction(pCurClient, "Error on highscore request " + fwutil::Common::intToString(msgObj.startRank) + ", " +  fwutil::Common::intToString(msgObj.numRanks) );
		return false;
	}
	
	int size = (int)(hsList.size());
	for(int i=0; i<size; i++) {
		HighscoreEntry hs = hsList[i];
		MsgGameCharacterHighscoreListEntry hsMsg;
		hsMsg.rank = hs.rank;
		hsMsg.experience = hs.experience;
		hsMsg.listIndex = i;
		hsMsg.listLength = size;
		hsMsg.name = hs.characterName;
		server.queueMessageForSending(hsMsg, pCurClient, true);
	}

	return true;

}



/**
 * Client requests to respawn (co).
 */
bool ModuleCharacters::handleMessageGameCharacterRespawnRequest(FWClient* pCurClient, MsgGameCharacterRespawnRequest& msg) {
	if (	!pCurClient->conditionApplies(ClientCondition::character_active)
		||	pCurClient->conditionApplies(ClientCondition::character_alive))	// must be dead
	{
		server.handleClientError(pCurClient, "handleMessageGameCharacterRespawnRequest");
		return false;
	}

	Character* c = pCurClient->getUser()->getSelectedCharacter();
	c->setDead(false);
	
	Playfield* playfield = c->getPlayfield();
	server.getModulePlayfields()->placeCharacterOnPlayfield(c, playfield, true);
	return true;
}
// %%GENERATOR_END%%HANDLER_IMPL%%


void ModuleCharacters::checkAutomaticVitalityRefill(Character* c) {
	clock_ms_t curTime = world.clock.getCachedTimestampMS();
	bool changed = false;
	// todo: do not hard code time interval
	if (curTime - c->getLastVitalityRefill() > 20000) {
		c->setLastVitalityRefill(curTime);

		int curMana = c->getManaCurrent();
		int maxMana = c->getMaxMana();
		int curHealth = c->getHealthCurrent();
		int maxHealth = c->getMaxHealth();
		
		if (curMana < maxMana) {
			// todo: use manaregenerate value
			curMana += 1;
			if (curMana > maxMana)
				curMana = maxMana;
			changed = true;
		}
		if (curHealth < maxHealth) {
			// todo: use Healthregenerate value
			curHealth += 8;
			if (curHealth > maxHealth)
				curHealth = maxHealth;
			changed = true;
		}

		if (changed) {
			MsgGameCharacterIncreaseVitality vitalityMsg;
			c->setHealthCurrent(curHealth);
			c->setManaCurrent(curMana);
			vitalityMsg.curHealth = curHealth;
			vitalityMsg.curMana = curMana;
			server.queueMessageForSending(vitalityMsg, c->getUser()->getClient(), true);
		}
	}
}


void ModuleCharacters::removeCharacterFromWorld(Character* c) {	
	server.getModulePlayfields()->removeCharacterFromPlayfield(c);
	// todo: notify friends, clans etc. that character is no longer online
	// todo: do the same notifications when character goes online
	c->removeFromWorld();
}