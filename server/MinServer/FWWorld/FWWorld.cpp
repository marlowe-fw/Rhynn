#include "FWWorld.h"
#include "world_objects/User.h"
#include "world_objects/Gameserver.h"
#include "world_objects/CharacterClass.h"
#include "world_objects/Character.h"
#include "world_objects/Graphic.h"
#include "world_objects/Playfield.h"
#include "world_objects/Item.h"
#include "CommonUtils.h"
#include "DBHelper.h"
#include <vector>
#include <boost/bind.hpp>

using namespace fwworld;

unsigned int FWWorld::s_VisibilityCellRange = 5;
unsigned int FWWorld::s_VisibilityCellRangeWSize = FWWorld::s_VisibilityCellRange*2 + 1;


FWWorld::FWWorld(mysqlpp::Connection& newConn, fwutil::GenericClock& newWorldClock, const std::string& logFilename, const std::string configFilename) 
: conn(newConn), clock(newWorldClock), log(logFilename), configSettings(configFilename)
{
	init();
}

FWWorld::~FWWorld() {
	playfields.clear();

	if (liveCharacters.size() > 0) {
		std::cout << "on world destroy, remaining characters: " << liveCharacters.size() << std::endl;
		//liveCharacters.clear();
	}
	
	if (liveItems.size() > 0) {
		std::cout << "on world destroy, remaining items: " << liveItems.size() << std::endl;
		//liveItems.clear();
	}

	if (liveUsers.size() > 0) {
		std::cout << "on world destroy, remaining users: " << liveUsers.size() << std::endl;
		//liveUsers.clear();
	}

	std::cout << "World destroyed." << std::endl;
}

/**
 * Default world initialization which can be used by the derived classes to initialize the world to in the way the server 
 * would do it for the base class data of the world.
 */
void FWWorld::init() {
	// character classes
	std::cout << "loading character classes" << std::endl;
	CharacterClass::getAll(*this, characterClasses);
	// game servers
	std::cout << "loading game servers" << std::endl;
	Gameserver::getAll(*this, gameServers);
	
	// graphics
	if (configSettings.getEntryValueBool("graphics.load", false)) {
		std::cout << "loading graphics" << std::endl;
		std::string gFilter = configSettings.getEntryValueString("graphics.loadFilter", "");
		Graphic::getAll(*this, worldGraphics, true, "id asc", gFilter);
	} else {
		std::cout << "skipping graphics loading" << std::endl;
	}
	
	// playfields
	if (configSettings.getEntryValueBool("playfields.load", false)) {
		std::cout << "loading playfields" << std::endl;
		std::string pfFilter = configSettings.getEntryValueString("playfields.loadFilter", "");
		Playfield::getAll(*this, playfields, true, pfFilter, "id asc");
		for(std::map<unsigned int, SPPlayfield>::iterator it = playfields.begin(); it != playfields.end(); it++) {
			it->second->loadDependenciesLive();
		}
	} else {
		std::cout << "skipping playfield loading" << std::endl;
	}
}


/**
* Get a graphic object (with loaded image data) associated with the given id.
* @param characterId The id of the character to retrieve
* @return A pointer to the graphic object or 0 if none is found
*/
Graphic* FWWorld::getGraphic(unsigned int graphicId) {
	Graphic* obj = 0;
	std::map<unsigned int, SPGraphic>::iterator it = worldGraphics.find(graphicId);
	if (it != worldGraphics.end()) {
		obj = ((*it).second).get();
	}
	return obj;
}

/**
* Get the character class object for the given id. This is just a map lookup, as the classes are loaded in memory.
* @param classId The DB id of the character class
* @return A pointer to the character class which may be empty / null if no object 
* was found for the given id
*/
CharacterClass* FWWorld::getCharacterClass(unsigned int classId) {
	CharacterClass* obj = 0;
	std::map<unsigned int, SPCharacterClass>::iterator it = characterClasses.find(classId);
	if (it != characterClasses.end()) {
		obj = ((*it).second).get();
	}
	return obj;
}

/**
* Get a playfield object associated with the given id.
* @param playfieldId The id of the playfield to retrieve
* @return A pointer to the playfield object or 0 if none is found
*/
Playfield* FWWorld::getPlayfield(unsigned int playfieldId) {
	Playfield* obj = 0;
	std::map<unsigned int, SPPlayfield>::iterator it = playfields.find(playfieldId);
	if (it != playfields.end()) {
		obj = ((*it).second).get();
	}
	return obj;
}


/**
* Check if a given user is logged in, in which case the user resides in the liveUsers map.
* @param userId The id of the user to check
* @return A pointer to the user, null if not found
*/
User* FWWorld::getLiveUser(unsigned int userId) {
	User* u = 0;
	std::map<unsigned int, User*>::iterator it = liveUsers.find(userId);
	if (it != liveUsers.end()) {
		// user is logged in
		u = (*it).second;
	}
	return u;
}

/**
* Insert a user to the container of currently live / logged-in users.
* @param u A pointer to the User object
*/
void FWWorld::addLiveUser(User* u) {
	if (getLiveUser(u->getId()) == 0) {
		liveUsers.insert(std::pair<unsigned int, User*>(u->getId(), u));
		std:: cout << clock.getTimestampStr(true) << " Live user added: " << u->getId() << ", count: " << liveUsers.size() << std::endl;
	}
	else
		std::cout << clock.getTimestampStr(true) << " Warning: Tried to add a live user which was already in the world: " << u->getId() << std::endl;
}

/**
* Remove a user from the container of currently live / logged-in users.
* @param userId The id of the User object which should be removed
*/
void FWWorld::removeLiveUser(unsigned int userId) {
	if (getLiveUser(userId) != 0) {
		liveUsers.erase(userId);
		std:: cout << clock.getTimestampStr(true) << " Live user removed: " << userId << " remaining users in world: " << liveUsers.size() << std::endl;
		
	}
	else
		std::cout << clock.getTimestampStr(true) << " Warning: Live user could not be removed: " << userId << std::endl;
}


/**
* Insert a Character to the container of currently live characters.
* @param u A pointer to the character object
*/
void FWWorld::addLiveCharacter(Character* c) {
	if (getLiveCharacter(c->getId()) == 0) {
		liveCharacters.insert(std::pair<unsigned int, Character*>(c->getId(), c));
		std:: cout << clock.getTimestampStr(true) << " Live character added: " << c->getId() << ", count: " << liveCharacters.size() << std::endl;
	}
	else
		std::cout << clock.getTimestampStr(true) << " Warning: Tried to add a live character which was already in the world: " << c->getId() << std::endl;
}

/**
* Get an active (live, i.e. playing on the server or a belonging to an active bot) character associated with the 
* given id.
* @param characterId The id of the character to retrieve
* @return A pointer to the character object or 0 if none is found
*/
Character* FWWorld::getLiveCharacter(unsigned int characterId) {
	Character* obj = 0;
	std::map<unsigned int, Character*>::iterator it = liveCharacters.find(characterId);
	if (it != liveCharacters.end()) {
		obj = (*it).second;
	}
	return obj;
}

/**
* Remove a live character from the container of currently live characters.
* @param characterId The id of the character object which should be removed
*/
void FWWorld::removeLiveCharacter(unsigned int characterId) {
	std::map<unsigned int,Character*>::iterator it = liveCharacters.find(characterId);
	if (it==liveCharacters.end()) {
		std:: cout << clock.getTimestampStr(true) << " Warning: Tried to remove live character, but character was not found:" << characterId << std::endl;
	} else {
		liveCharacters.erase(it);
		std:: cout << clock.getTimestampStr(true) << " Live character removed: " << characterId << " remaining characters in world: " << liveCharacters.size() << std::endl;
	}
}

/**
* Insert an Item to the live container.
* @param obj A pointer to the object
*/
void FWWorld::addLiveItem(Item* obj) {
	if (getLiveItem(obj->getId()) == 0) {
		liveItems.insert(std::pair<unsigned int, Item*>(obj->getId(), obj));
		std:: cout << clock.getTimestampStr(true) << " Live item added: " << obj->getId() << ", count: " << liveItems.size() << std::endl;
	}
	else
		std::cout << clock.getTimestampStr(true) << " Warning: Tried to add a live item which was already in the world: " << obj->getId() << std::endl;
}

/**
* Get the active item associated with the 
* given id.
* @param if The id of the object to retrieve
* @return A pointer to the object or 0 if none is found
*/
Item* FWWorld::getLiveItem(unsigned int id) {
	Item* obj = 0;
	std::map<unsigned int, Item*>::iterator it = liveItems.find(id);
	if (it != liveItems.end()) {
		obj = (*it).second;
	}
	return obj;
}

/**
* Remove a live item from the live container.
* @param id The id of the object which should be removed
*/
void FWWorld::removeLiveItem(unsigned int id) {
	if (getLiveItem(id) != 0) {
		liveItems.erase(id);
		std:: cout << clock.getTimestampStr(true) << " Live item removed: " << id << " remaining items in world: " << liveItems.size() << std::endl;
	}	
	else
		std::cout << clock.getTimestampStr(true) << " Warning: Live item could not be removed: " << id << std::endl;

}