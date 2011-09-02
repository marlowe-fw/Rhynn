#ifndef WorldObjectContainer_h__
#define WorldObjectContainer_h__

#include <vector>

namespace fwworld {

class Character;
class Item;

class WorldObjectContainer {
private:

	std::vector<Character*> characters;
	std::vector<Item*> items;
};

}

#endif // WorldObjectContainer_h__