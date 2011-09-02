#ifndef WorldObjectTypeDefs_h__
#define WorldObjectTypeDefs_h__

#include "boost/shared_ptr.hpp"

namespace fwworld {
	class FWWorld;
	typedef boost::shared_ptr<FWWorld> SPWorld;

	class Gameserver;
	typedef boost::shared_ptr<Gameserver> SPGameserver;

	class User;
	typedef boost::shared_ptr<User> SPUser;

	class UserSession;
	typedef boost::shared_ptr<UserSession> SPUserSession;

	class Character;
	typedef boost::shared_ptr<Character> SPCharacter;

	class Item;
	typedef boost::shared_ptr<Item> SPItem;

	class CharacterClass;
	typedef boost::shared_ptr<CharacterClass> SPCharacterClass;

	class Graphic;
	typedef boost::shared_ptr<Graphic> SPGraphic;

	class Playfield;
	typedef boost::shared_ptr<Playfield> SPPlayfield;

	class PlayfieldGraphic;
	typedef boost::shared_ptr<PlayfieldGraphic> SPPlayfieldGraphic;


}



#endif // WorldObjectTypeDefs_h__