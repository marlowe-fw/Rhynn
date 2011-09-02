#ifndef Equipment_h__
#define Equipment_h__

#include "../Item.h"

namespace fwworld {

class Item;

class Equipment {
	

public: 
	enum EquipmentType {et_weapon1, et_shield1, et_armor, et_helmet, et_boots, et_gloves, et_count, et_invalid};

	Equipment();
	virtual ~Equipment();

	bool equip(SPItem item, unsigned int skillBase, unsigned int manaBase, bool checkCanEquip);
	bool unequip(SPItem item, unsigned int skillBase, unsigned int magicBase, bool removeOnly);
	bool checkUnequipUnsupportedItems(unsigned int skillBase, unsigned int magicBase, bool removeOnly);
	unsigned int getEffect(Item::AttributeType at);

	void clear();

private:
	SPItem items[et_count];

	unsigned int healthEffectCached;
	unsigned int manaEffectCached;

	unsigned int attackEffectCached;
	unsigned int defenseEffectCached;
	unsigned int damageEffectCached;
	unsigned int skillEffectCached;
	unsigned int magicEffectCached;

	unsigned int healthRegEffectCached;
	unsigned int manaRegEffectCached;
	
	EquipmentType getEquipmentType(SPItem item);

	bool canEquip(SPItem item, unsigned int skillBase, unsigned int magicBase);
	void calculateEffects();
};

}

#endif // Equipment_h__