#include "Equipment.h"
//#include "../Item.h"

using namespace fwworld;

Equipment::Equipment()
:
healthEffectCached(0),
manaEffectCached(0),
attackEffectCached(0),
defenseEffectCached(0),
damageEffectCached(0),
skillEffectCached(0),
magicEffectCached(0),
healthRegEffectCached(0),
manaRegEffectCached(0)
{


	//std::cout << "Equipment created: " << ((items[0].get() == 0) ? " is null " : "is NOT null") << std::endl;
}

Equipment::~Equipment() {
}

void Equipment::clear() {
	for (int i=0; i<et_count; i++) {
		items[i] = SPItem();
	}
	calculateEffects();
}

void Equipment::calculateEffects() {
	healthEffectCached = 0;
	manaEffectCached = 0;

	attackEffectCached = 0;
	defenseEffectCached = 0;
	damageEffectCached = 0;
	skillEffectCached = 0;
	magicEffectCached = 0;

	healthRegEffectCached = 0;
	manaRegEffectCached = 0;

	for (int i=0; i<et_count; i++) {
		if (items[i].get() != 0) {
			SPItem it = items[i];
			healthEffectCached += it->getHealthEffect();
			manaEffectCached += it->getManaEffect();
			attackEffectCached += it->getAttackEffect();
			defenseEffectCached += it->getDefenseEffect();
			damageEffectCached += it->getDamageEffect();
			skillEffectCached += it->getSkillEffect();
			magicEffectCached += it->getMagicEffect();
			healthRegEffectCached += it->getHealthregenerateEffect();
			manaRegEffectCached += it->getManaregenerateEffect();
		}
	}
}

unsigned int Equipment::getEffect(Item::AttributeType at) {
	switch(at) {
			case Item::at_health: return healthEffectCached;
			case Item::at_mana: return manaEffectCached;
			case Item::at_attack: return attackEffectCached;
			case Item::at_defense: return defenseEffectCached;
			case Item::at_damage: return damageEffectCached;
			case Item::at_skill: return skillEffectCached;
			case Item::at_magic: return magicEffectCached;
			case Item::at_healthregenerate: return healthRegEffectCached;
			case Item::at_manaregenerate: return manaRegEffectCached;
	}
	return 0;
}


Equipment::EquipmentType Equipment::getEquipmentType(SPItem item) {
	unsigned int clientTypeId = item->getClientTypeId();
	Equipment::EquipmentType et = et_invalid;
	if (clientTypeId > 0 && clientTypeId <= et_count) {
		et = (Equipment::EquipmentType)(clientTypeId);
	}
	return et;
}

bool Equipment::canEquip(SPItem item, unsigned int skillBase, unsigned int magicBase) {
	EquipmentType et = getEquipmentType(item);
	unsigned int totalCurrentSkill = skillBase + getEffect(Item::at_skill);
	unsigned int totalCurrentMagic = magicBase + getEffect(Item::at_magic);

	if (items[et].get() != 0) {
		// already an iten equipped for this eq slot
		// we need to consider the effect values without this item as this one will be unequipped before equipping the new one
		// as this might also have cascading effects, we need to simulate the unequip, and decide afterwards
		// using a simple temp copy to simulate the unequip
		Equipment ghost(*this);
		ghost.unequip(ghost.items[et], skillBase, magicBase, true);
		return ghost.canEquip(item, skillBase, magicBase);
	} else {
		if (totalCurrentSkill >= item->getRequiredSkill() && totalCurrentMagic >= item->getRequiredMagic()) {
			return true;
		} else {
			return false;
		}
	}
}


bool Equipment::equip(SPItem item, unsigned int skillBase, unsigned int magicBase, bool checkCanEquip) {
	if (item->getUsageType() == ItemUsageType::equip && (!checkCanEquip || item->getEquippedStatus() == ItemEquippedStatus::not_equipped)) {
		// check required skill / magic, check unequip as on client
		EquipmentType et = getEquipmentType(item);
		//bool canEquip1 = canEquip(item, skillBase, magicBase);
		//std::cout << "eq type: " << et << (canEquip1 ? " e ok" : " enok ") << std::endl;
		if (et != et_invalid && (!checkCanEquip || canEquip(item, skillBase, magicBase))) {
			if (items[et].get() != 0) {
				unequip(item, skillBase, magicBase, false);
			}
			items[et] = item;
			item->setEquippedStatus(ItemEquippedStatus::equipped);
			calculateEffects();
			return true;
		}
	}
	return false;
}

bool Equipment::checkUnequipUnsupportedItems(unsigned int skillBase, unsigned int magicBase, bool removeOnly) {
	bool unequipped = false;
	unsigned int totalCurrentSkill = skillBase + getEffect(Item::at_skill);
	unsigned int totalCurrentMagic = magicBase + getEffect(Item::at_magic);
	for (int i=0; i<et_count; i++) {
		if (items[i].get() != 0 && (totalCurrentSkill < items[i]->getRequiredSkill() || totalCurrentMagic < items[i]->getRequiredMagic())) {
			// item is not supported
			unequip(items[i], skillBase, magicBase, removeOnly);
			unequipped = true;
			break;
		}
	}
	return unequipped;
}

bool Equipment::unequip(SPItem item, unsigned int skillBase, unsigned int magicBase, bool removeOnly) {
	if (item->getUsageType() == ItemUsageType::equip && item->getEquippedStatus() == ItemEquippedStatus::equipped) {
		// check required skill / magic, check unequip as on client
		EquipmentType et = getEquipmentType(item);
std::cout << "et: " << et << ((items[et].get()!=0) ? " not null" : "null") << std::endl;
		if (et != et_invalid && items[et].get()!=0 && items[et]->getId() == item->getId()) {
			if (!removeOnly) {
				items[et]->setEquippedStatus(ItemEquippedStatus::not_equipped);
			}
			items[et] = SPItem();	// null item
			// check if other items need to be unequipped as a consequence of this item being removed (skill / magic might have dropped)
			checkUnequipUnsupportedItems(skillBase, magicBase, removeOnly);
			calculateEffects();
			return true;
		}
	}
	return false;

}
