#ifndef FWWorld_h__
#define FWWorld_h__

#include "world_objects/WorldObjectTypeDefs.h"
#include "Log.h"
#include "ConfigSetting.h"

#include <string>
#include <map>
#include <vector>


namespace fwutil {class GenericClock;}
namespace mysqlpp {class Connection;}

namespace fwworld {


/**
 * An object of type FWWorld holds globally accessible information.
 */
class FWWorld {
	
	public:

		static unsigned int s_VisibilityCellRange;
		static unsigned int s_VisibilityCellRangeWSize;

		/** Connection object to actually work on the database level. */
		mysqlpp::Connection& conn;
		/** World clock object to determine the time. */
		fwutil::GenericClock& clock;

		/** Log object to log messages to a log file. */
		fwutil::Log log;

		/** This object holds the world configuration which must be read from the world config file. */
		fwutil::ConfigSetting configSettings;

		/** Holds all existing character classes, retrieved in the constructor. */
		std::map<unsigned int, SPCharacterClass> characterClasses;
		/** Holds all the game servers, retrieved in the constructor. */
		std::vector<SPGameserver> gameServers;

		/** 
		* All playfields live on the server.
		*/
		std::map<unsigned int, SPPlayfield> playfields;
		

		/**
		* All graphics which are part of the game (and can thus be streamed to the client, or otherwise required for display).
		*/
		std::map<unsigned int, SPGraphic> worldGraphics;


		/** 
		* All user objects currently live on the server (i.e. considered online / logged in) 
		* but not necessarily validated. Note that a user is considered validated once the challenge number issued by the 
		* server is sent back correctly decrypted by the client.
		*/
		std::map<unsigned int, User*> liveUsers;

		/**
		* All characters currently active on the server (i.e. logged in and playing or characters of bots).
		*/
		std::map<unsigned int, Character*> liveCharacters;

		/**
		* All items currently active on the server (i.e. playfield items, inventory items, chest items, clan items etc.).
		*/
		std::map<unsigned int, Item*> liveItems;



		FWWorld(mysqlpp::Connection& newConn, fwutil::GenericClock& newWorldClock, const std::string& logFilename, const std::string configFilename);

		virtual ~FWWorld();

		CharacterClass* getCharacterClass(unsigned int classId);
		Graphic* getGraphic(unsigned int graphicId);
		Playfield* getPlayfield(unsigned int playfieldId);

		User* getLiveUser(unsigned int userId);
		void addLiveUser(User* u);
		void removeLiveUser(unsigned int userId);

		Character* getLiveCharacter(unsigned int characterId);
		void addLiveCharacter(Character* c);
		void removeLiveCharacter(unsigned int characterId);

		Item* getLiveItem(unsigned int id);
		void addLiveItem(Item* obj);
		void removeLiveItem(unsigned int id);

		

	protected:		
		void init();
};

}

#endif // FWWorld_h__