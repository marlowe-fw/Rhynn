#ifndef Inventory_h__
#define Inventory_h__

#include "../WorldObjectTypeDefs.h"
#include <map>
#include "Equipment.h"

namespace fwworld {

class Inventory {
public:
	static const unsigned int s_DefaultMaxSlots;

	Inventory(Character* newOwnerCharacter);
	virtual ~Inventory();

	bool addItem(SPItem item);
	SPItem getItem(unsigned int id);
	SPItem removeItem(unsigned int id, bool removeFromWorld);

	void clear();
	void removeItemsFromWorld();

	void checkStoreItems();

	inline std::map<unsigned int, SPItem>& getItems() {return items;}
	inline unsigned int getMaxSlots() {return maxSlots;}
	inline unsigned int itemCount() {return (unsigned int)(items.size());}

	bool equipItem(unsigned int id);
	bool unequipItem(unsigned int id);

	void updateCharacterEffectValues();
	void initializeEquipment();


private:
	Character* ownerCharacter;
	unsigned int maxSlots;
	std::map<unsigned int, SPItem> items;

	Equipment equipment;

	

};

}

#endif // Inventory_h__