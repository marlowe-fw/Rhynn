#include "ModuleItems.h"
#include "FWServer.h"
#include "FWClient.h"
#include "FWWorld.h"
#include "Random.h"
#include "world_objects/Playfield.h"
#include "world_objects/Item.h"
#include "world_objects/Character.h"
#include "Geometry.h"
#include "messages/MsgGameItemInventoryAdd.h"
#include "messages/MsgGameItemInventoryAddFail.h"
#include "messages/MsgGameItemRemove.h"
#include "messages/MsgGameItemAdd.h"
#include "messages/MsgGameItemInventoryEnd.h"
#include "messages/MsgGameCharacterIncreaseVitality.h"

#include "state/ClientCondition.h"

using namespace fws;
using namespace fwworld;
using namespace fwutil;

ModuleItems::ModuleItems(FWServer& newServer, FWWorld& newWorld)
: server(newServer), world(newWorld)
{}


// %%GENERATOR_START%%HANDLER_IMPL%%


/**
 * Client requests to pickup an item from a playfield (co).
 */
bool ModuleItems::handleMessageGameItemPickupRequest(FWClient* pCurClient, MsgGameItemPickupRequest& msg) {
	if (!pCurClient->conditionApplies(ClientCondition::character_alive)) {
		if (!pCurClient->conditionApplies(ClientCondition::character_active))
			server.handleClientError(pCurClient, "handleMessageGameItemPickupRequest");
		return false;
	}

	std::cout << "got pickup item" << std::endl;
	Character* c = pCurClient->getUser()->getSelectedCharacter();
	if (c->isDead())
		return false;

	Inventory& inventory = c->getInventory();
	if (inventory.itemCount() >= inventory.getMaxSlots()) {
		server.onClientSuspiciousAction(pCurClient, "Client tried to pick up item although inventory is full.", false);
		return false;
	}

	Playfield* pf = c->getPlayfield();

	bool ok = false;
	SPItem curItem = pf->getItem(msg.itemId);

	// todo: might need to check for premium?

	if (curItem.get() != 0) {
		// check in range
		if (!fwutil::Geometry::isWithinRadius(c->getXCenter(), c->getYCenter(), curItem->getXCenter(),curItem->getYCenter(), Character::s_ItemPickupRadius)) {
			server.onClientSuspiciousAction(pCurClient, "Client tried to pick up item which was not within valid pick up radius.", false);
			return false;
		}

		if (curItem->getScheduledTime() > 0 && curItem->getScheduleType() == Item::st_respawn) {
			server.onClientSuspiciousAction(pCurClient, "Client tried to pickup item which was scheduled for respawn.", false);
			return false;
		}

		// at this point, pickup is ok

		// inform all in range that item was removed
		std::vector<Character*> charactersInRange;
		pf->appendObjectsInVisRangePx(curItem->getX(), curItem->getY(), &charactersInRange, 0);
		MsgGameItemRemove removeMsg;
		removeMsg.itemId = curItem->getId();
		server.broadCastMessageToCharacters(charactersInRange, removeMsg);

		bool respawn = false;
		if (curItem->getRespawn() == YesNoEnum::yes) {
			pf->scheduleItem(curItem->getId(), Item::st_respawn);
			// remove the template item from the cell so it won't show up until respawn
			pf->removeItemFromCell(*curItem);
			// since this item needs respawn, return an instance
			curItem = SPItem(curItem->instanciate());
			respawn = true;
		} else {
			// item needs no respawn, so just detach from playfield
			// watch out to NOT remove this item from the live items at this point, ownership will just be transferred
			// maybe use a flag for the removeItem function
			pf->removeItem(curItem, false, false);	// will not drop the item pointer as we still hold the curItem reference
		}

		// adjust item values to reflect the pickup
		curItem->setRespawn(YesNoEnum::no);
		curItem->setRespawnDelay(0);
		curItem->setScheduledTime(0);
		curItem->setScheduleType(Item::st_unknown);
		curItem->setPlayfieldId(0);
		curItem->setX(0);
		curItem->setY(0);
		curItem->setOwnerId(c->getId());

		curItem->storeToDB();

		if (respawn) {
			// since we created a new item, add it to the world
			world.addLiveItem(curItem.get());
		}

		inventory.addItem(curItem);

		ok = true;
	}

	if (ok)
	{
		MsgGameItemInventoryAdd msgAddItem;
		msgAddItem.fromItem(*curItem);
		server.queueMessageForSending(msgAddItem, pCurClient, false);
	}
	else
	{
		// add item fails
		MsgGameItemInventoryAddFail msgFail;
		msgFail.infoMessage = "Could not pick up item.";
		server.queueMessageForSending(msgFail, pCurClient, true);
	}

	return true;
}

/**
 * Client drops an item to the playfield (co)
.
 */
bool ModuleItems::handleMessageGameItemDropRequest(FWClient* pCurClient, MsgGameItemDropRequest& msg) {

	if (!pCurClient->conditionApplies(ClientCondition::character_alive)) {
		if (!pCurClient->conditionApplies(ClientCondition::character_active))
			server.handleClientError(pCurClient, "handleMessageGameItemPickupRequest");
		return false;
	}

	std::cout << "got drop item" << std::endl;

	Character* c = pCurClient->getUser()->getSelectedCharacter();
	Inventory& inventory = c->getInventory();

	SPItem curItem = inventory.getItem(msg.itemId);
	if (curItem.get() == 0) {
		server.onClientSuspiciousAction(pCurClient, "Client tried to drop item which he does not own.", false);
		return false;
	}

	Playfield* pf = c->getPlayfield();
	SPItem itemToDrop = curItem;

	if (curItem->getUsageType() == ItemUsageType::use && msg.units < curItem->getUnits()) {
		// for usable items, this requires creation of a new instance if not all units are dropped
		// take care of units and units for sale
		unsigned int remainingUnits = curItem->getUnits() - msg.units;
		curItem->setUnits(remainingUnits);
		if (remainingUnits < curItem->getUnitsSell()) {
			curItem->setUnitsSell(remainingUnits);
		}
		curItem->storeToDB();

		itemToDrop = SPItem(curItem->instanciate());
		itemToDrop->setUnits(msg.units);
		// populate item for the world
		world.addLiveItem(itemToDrop.get());
	} else {
		c->getInventory().removeItem(itemToDrop->getId(), false);
	}

	itemToDrop->setUnitsSell(0);
	itemToDrop->setPrice(0);
	itemToDrop->setOwnerId(0);
	itemToDrop->setPlayfieldId(pf->getId());
	// set pf + pos, add a little random
	itemToDrop->setPlayfieldId(pf->getId());
	int newX = c->getXCenter() - 10 + fwutil::Random::nextInt(20) - (fwworld::Item::c_defaultWidth / 2);
	int newY = c->getYCenter() - 10 + fwutil::Random::nextInt(20) - (fwworld::Item::c_defaultHeight / 2);

	if (newX < 0)
        newX = 0;
    if (newY < 0)
        newY = 0;

    if (newX + Item::c_defaultWidth > pf->getWidth() * PlayfieldCell::c_DefaultWidth)
        newX =  (pf->getWidth() * PlayfieldCell::c_DefaultWidth) - Item::c_defaultWidth;

    if (newY + Item::c_defaultHeight > pf->getHeight() * PlayfieldCell::c_DefaultHeight)
        newY =  (pf->getHeight() * PlayfieldCell::c_DefaultHeight) - Item::c_defaultHeight;

	itemToDrop->setX(newX);
	itemToDrop->setY(newY);

	itemToDrop->storeToDB();

	// add to playfield items
	pf->addItem(itemToDrop);
	// add to dropped items
	pf->scheduleItem(itemToDrop->getId(), Item::st_cleanup);

	// add to playfield / notify clients in range
	MsgGameItemAdd msgAdd;
	msgAdd.fromItem(*itemToDrop);
	server.getModulePlayfields()->broadCastMessageToCharactersInVisRangePx(itemToDrop->getX(), itemToDrop->getY(), *pf, msgAdd);

	return true;
}

/**
 * Client requests to equip an item (co).
 */
bool ModuleItems::handleMessageGameItemEquipRequest(FWClient* pCurClient, MsgGameItemEquipRequest& msg) {
	if (!pCurClient->conditionApplies(ClientCondition::character_alive)) {
		if (!pCurClient->conditionApplies(ClientCondition::character_active))
			server.handleClientError(pCurClient, "handleMessageGameItemEquipRequest");
		return false;
	}

	std::cout << "got equip item" << std::endl;

	Character* c = pCurClient->getUser()->getSelectedCharacter();
	Inventory& inventory = c->getInventory();

	if (!inventory.equipItem(msg.itemId)) {
		server.onClientSuspiciousAction(pCurClient, "Client tried to equip an item which wasn't possible, item id: " + fwutil::Common::intToString(msg.itemId), false);
	}

	return true;
}

/**
 * Client requests to unequip an item (co).
 */
bool ModuleItems::handleMessageGameItemUnequipRequest(FWClient* pCurClient, MsgGameItemUnequipRequest& msg) {
	if (!pCurClient->conditionApplies(ClientCondition::character_alive)) {
		if (!pCurClient->conditionApplies(ClientCondition::character_active))
			server.handleClientError(pCurClient, "handleMessageGameItemUnequipRequest");
		return false;
	}

	Character* c = pCurClient->getUser()->getSelectedCharacter();
	Inventory& inventory = c->getInventory();

	if (!inventory.unequipItem(msg.itemId)) {
		server.onClientSuspiciousAction(pCurClient, "Client tried to unequip an item which wasn't possible, item id: " + fwutil::Common::intToString(msg.itemId), false);
	}

	return true;
}

/**
 * Client sends use item in order to invoke an action on the server (co)..
 */
bool ModuleItems::handleMessageGameItemUseRequest(FWClient* pCurClient, MsgGameItemUseRequest& msg) {
	if (!pCurClient->conditionApplies(ClientCondition::character_alive)) {
		if (!pCurClient->conditionApplies(ClientCondition::character_active))
			server.handleClientError(pCurClient, "handleMessageGameItemUnequipRequest");
		return false;
	}

	unsigned int itemId = msg.itemId;
	std::cout << "Received use item: " << msg.itemId << std::endl;

	Character* c = pCurClient->getUser()->getSelectedCharacter();
	Inventory& inventory = c->getInventory();

	SPItem item = inventory.getItem(itemId);

	if (item.get() == 0) {
		server.onClientSuspiciousAction(pCurClient, "Client tried use item which he doesn't own.", false);
		return false;
	}

	if (item->getUsageType() != ItemUsageType::use) {
		server.onClientSuspiciousAction(pCurClient, "Client tried use item which cannot be used.", false);
		return false;
	}

	// todo: add logic to check which action to execute

	// for now, assume increase hp
	unsigned int curHealth = c->getHealthCurrent();
	unsigned int maxHealth = c->getMaxHealth();
	if (curHealth < maxHealth) {
		c->setHealthCurrent(maxHealth);
		
		MsgGameCharacterIncreaseVitality vitalityMsg;
		vitalityMsg.curHealth = c->getHealthCurrent();
		vitalityMsg.curMana = c->getManaCurrent();
		server.queueMessageForSending(vitalityMsg, pCurClient, true);
	} else {
		// todo: send health already max
	}

	// todo: reduce item units, remove item from inventory if units == 0


	return true;
}
// %%GENERATOR_END%%HANDLER_IMPL%%

/**
* For all items in the inventory of the given character send an add item message. When all items are sent send the inventory end
* message to indicate that no more items are to follow.
* @param pCurClient The active client
* @param selectedCharacter The character object holding the inventory
*/
void ModuleItems::sendInventoryForCharacter(Character& selectedCharacter) {
	User* u  = selectedCharacter.getUser();
	if (u!=0) {
		FWClient* pCurClient = u->getClient();

		typedef std::map<unsigned int, SPItem> itMap;
		itMap& items = selectedCharacter.getInventory().getItems();
		for (itMap::iterator it = items.begin(); it!=items.end(); it++) {
			MsgGameItemInventoryAdd addMsg;
			addMsg.fromItem(*((*it).second));
			server.queueMessageForSending(addMsg, pCurClient, false);
		}

		// send end of Inventory
		MsgGameItemInventoryEnd endMsg;
		server.queueMessageForSending(endMsg, pCurClient, true);

	}
}