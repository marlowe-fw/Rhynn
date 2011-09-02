#include "Inventory.h"
#include "../Item.h"
#include "../Character.h"

using namespace fwworld;

const unsigned int Inventory::s_DefaultMaxSlots = 22;

Inventory::Inventory(Character* newOwnerCharacter)
: ownerCharacter(newOwnerCharacter), maxSlots(s_DefaultMaxSlots)
{}

Inventory::~Inventory() {}



bool Inventory::equipItem(unsigned int itemId) {
	if (ownerCharacter == 0) {
		return false;
	}
	std::map<unsigned int, SPItem>::iterator it = items.find(itemId);
	if (it != items.end()) {
		SPItem item = it->second;
		if (equipment.equip(item, ownerCharacter->getSkillBase(), ownerCharacter->getMagicBase(), true)) {
			updateCharacterEffectValues();
			checkStoreItems();
			return true;
		}
	}
	return false;
}

bool Inventory::unequipItem(unsigned int itemId) {
	if (ownerCharacter == 0) {
		return false;
	}
	std::map<unsigned int, SPItem>::iterator it = items.find(itemId);
	if (it != items.end()) {
		SPItem item = it->second;
		if(equipment.unequip(item, ownerCharacter->getSkillBase(), ownerCharacter->getMagicBase(), false)) {
			updateCharacterEffectValues();
			checkStoreItems();
			return true;
		}
	}
	return false;
	
}

void Inventory::updateCharacterEffectValues() {
	if (ownerCharacter == 0) {
		return;
	}

	ownerCharacter->setHealthEffectsExtra(equipment.getEffect(Item::at_health));
	ownerCharacter->setManaEffectsExtra(equipment.getEffect(Item::at_mana));
	ownerCharacter->setAttackEffectsExtra(equipment.getEffect(Item::at_attack));
	ownerCharacter->setDefenseEffectsExtra(equipment.getEffect(Item::at_defense));
	ownerCharacter->setDamageEffectsExtra(equipment.getEffect(Item::at_damage));
	ownerCharacter->setSkillEffectsExtra(equipment.getEffect(Item::at_skill));
	ownerCharacter->setMagicEffectsExtra(equipment.getEffect(Item::at_magic));
	ownerCharacter->setHealthregenerateEffectsExtra(equipment.getEffect(Item::at_healthregenerate));
	ownerCharacter->setManaregenerateEffectsExtra(equipment.getEffect(Item::at_manaregenerate));
	
	// -- ownerCharacter->storeToDBIfNotSynchronized();
}

void Inventory::initializeEquipment() {
	if (ownerCharacter == 0) {
		return;
	}
	for (std::map<unsigned int, SPItem>::iterator it = items.begin(); it!=items.end(); it++) {	
		SPItem item = it->second;
		if (item->getEquippedStatus() == ItemEquippedStatus::equipped) {
			// todo: later also consider belt
			equipment.equip(item, ownerCharacter->getSkillBase(), ownerCharacter->getMagicBase(), false);
		}
	}
	// do sanity check, unequip all items not supported
	equipment.checkUnequipUnsupportedItems(ownerCharacter->getSkillBase(), ownerCharacter->getMagicBase(), false);
	updateCharacterEffectValues();
}

void Inventory::checkStoreItems() {
	for (std::map<unsigned int, SPItem>::iterator it = items.begin(); it!=items.end(); it++) {
		(it->second)->storeToDBIfNotSynchronized();
	}
}

void Inventory::clear() {
	items.clear();
	equipment.clear();
}

void Inventory::removeItemsFromWorld() {
	for (std::map<unsigned int, SPItem>::iterator it = items.begin(); it!=items.end(); it++) {
		(it->second)->removeFromWorld();
	}
}




bool Inventory::addItem(SPItem item) {
	if (itemCount() < maxSlots) {
		items.insert(std::pair<unsigned int, SPItem>(item->getId(), item));
		return true;
	}
	return false;
}



SPItem Inventory::getItem(unsigned int id) {
	std::map<unsigned int, SPItem>::iterator it = items.find(id);
	if (it!=items.end()) {
		return it->second;
	}
	return SPItem();
}

SPItem Inventory::removeItem(unsigned int id, bool removeFromWorld) {	
	std::map<unsigned int, SPItem>::iterator it = items.find(id);
	if (it!=items.end()) {
		SPItem item = it->second;
		if (item->getEquippedStatus() != ItemEquippedStatus::not_equipped) {
			equipment.unequip(item, ownerCharacter->getSkillBase(), ownerCharacter->getMagicBase(), false);
			updateCharacterEffectValues();
		}
		if (removeFromWorld) {
			(item)->removeFromWorld();
		}
		items.erase(it);
		return item;
	}
	return SPItem();
}