#include "Item.h"
#include "../FWWorld.h"
#include "DBHelper.h"
#include "null.h"

using namespace fwworld;

unsigned int Item::s_defaultCleanupSeconds = 40;


/**
* Create a new object and initialize it to default values.
* @param world The world this object belongs to (and therefore has access to)
*/
Item::Item(FWWorld& world) : DBSynchObject(world),
templateId(0),
clientTypeId(0),
ownerId(0),
setId(0),
graphicId(0),
playfieldId(0),
x(0),
y(0),
graphicsX(0),
graphicsY(0),
name(""),
description(""),
availableStatus(AvailableStatus::all),
canSell(YesNoEnum::yes),
canDrop(YesNoEnum::yes),
units(0),
unitsSell(0),
price(0),
respawn(YesNoEnum::yes),
respawnDelay(0),
equippedStatus(ItemEquippedStatus::not_equipped),
healthEffect(0),
manaEffect(0),
attackEffect(0),
defenseEffect(0),
damageEffect(0),
skillEffect(0),
magicEffect(0),
healthregenerateEffect(0),
manaregenerateEffect(0),
actionEffect1(0),
actionEffect2(0),
effectDuration(0),
requiredSkill(0),
requiredMagic(0),
frequency(0),
range(0),
scheduledTime(0),
scheduleType(Item::st_unknown),
usageType(ItemUsageType::unknown)
{
	objectTypeId = WorldObject::otItem;
}

/** Destruction of object. */
Item::~Item() {
	//std::cout << " -------------------  removing item: " << name << ", " << getId() << std::endl;
}

/**
* Save this object to the DB.
* @return true on success, false on failure.
*/
bool Item::storeToDB() {
	mysqlpp::Query query = world.conn.query();

	if (id == 0) {

		query << "insert into items( \
				 template_id, \
				 client_type_id, \
				 owner_id, \
				 set_id, \
				 graphic_id, \
				 playfield_id, \
				 x, \
				 y, \
				 graphics_x, \
				 graphics_y, \
				 name, \
				 description, \
				 available_status, \
				 can_sell, \
				 can_drop, \
				 units, \
				 units_sell, \
				 price, \
				 respawn, \
				 respawn_delay, \
				 equipped_status, \
				 health_effect, \
				 mana_effect, \
				 attack_effect, \
				 defense_effect, \
				 damage_effect, \
				 skill_effect, \
				 magic_effect, \
				 healthregenerate_effect, \
				 manaregenerate_effect, \
				 action_effect_1, \
				 action_effect_2, \
				 effect_duration, \
				 required_skill, \
				 required_magic, \
				 frequency, \
				 `range` \
				 ) values ( "
				 << templateId << ","
				 << clientTypeId << ","
				 << ownerId << ","
				 << setId << ","
				 << graphicId << ","
				 << playfieldId << ","
				 << x << ","
				 << y << ","
				 << graphicsX << ","
				 << graphicsY << ","
				 <<  mysqlpp::quote << name << ","
				 <<  mysqlpp::quote << description << ","
				 <<  mysqlpp::quote << availableStatus.str() << ","
				 <<  mysqlpp::quote << canSell.str() << ","
				 <<  mysqlpp::quote << canDrop.str() << ","
				 << units << ","
				 << unitsSell << ","
				 << price << ","
				 <<  mysqlpp::quote << respawn.str() << ","
				 << respawnDelay << ","
				 <<  mysqlpp::quote << equippedStatus.str() << ","
				 << healthEffect << ","
				 << manaEffect << ","
				 << attackEffect << ","
				 << defenseEffect << ","
				 << damageEffect << ","
				 << skillEffect << ","
				 << magicEffect << ","
				 << healthregenerateEffect << ","
				 << manaregenerateEffect << ","
				 << actionEffect1 << ","
				 << actionEffect2 << ","
				 << effectDuration << ","
				 << requiredSkill << ","
				 << requiredMagic << ","
				 << frequency << ","
				 << range
				 << ")";
	} else {
		query << "update items set "
			<< "template_id = " << templateId << ","
			<< "client_type_id = " << clientTypeId << ","
			<< "owner_id = " << ownerId << ","
			<< "set_id = " << setId << ","
			<< "graphic_id = " << graphicId << ","
			<< "playfield_id = " << playfieldId << ","
			<< "x = " << x << ","
			<< "y = " << y << ","
			<< "graphics_x = " << graphicsX << ","
			<< "graphics_y = " << graphicsY << ","
			<< "name = " <<  mysqlpp::quote << name << ","
			<< "description = " <<  mysqlpp::quote << description << ","
			<< "available_status = " << mysqlpp::quote << availableStatus.str() << ","
			<< "can_sell = " <<  mysqlpp::quote << canSell.str() << ","
			<< "can_drop = " <<  mysqlpp::quote << canDrop.str() << ","
			<< "units = " << units << ","
			<< "units_sell = " << unitsSell << ","
			<< "price = " << price << ","
			<< "respawn = " <<  mysqlpp::quote << respawn.str() << ","
			<< "respawn_delay = " << respawnDelay << ","
			<< "equipped_status = " <<  mysqlpp::quote << equippedStatus.str() << ","
			<< "health_effect = " << healthEffect << ","
			<< "mana_effect = " << manaEffect << ","
			<< "attack_effect = " << attackEffect << ","
			<< "defense_effect = " << defenseEffect << ","
			<< "damage_effect = " << damageEffect << ","
			<< "skill_effect = " << skillEffect << ","
			<< "magic_effect = " << magicEffect << ","
			<< "healthregenerate_effect = " << healthregenerateEffect << ","
			<< "manaregenerate_effect = " << manaregenerateEffect << ","
			<< "action_effect_1 = " << actionEffect1 << ","
			<< "action_effect_2 = " << actionEffect2 << ","
			<< "effect_duration = " << effectDuration << ","
			<< "required_skill = " << requiredSkill << ","
			<< "required_magic = " << requiredMagic << ","
			<< "frequency = " << frequency << ","
			<< "`range` = " << range
			<< " where id = " << id;
	}

	return storeByQuery(query, "items store");
}

/**
* Populate the object values by loading them from the DB.
* @param existingId The database id of the object which identifies the associated DB record.
* @return true on success, false otherwise
*/
bool Item::loadFromDB(unsigned int existingId) {
	mysqlpp::Query query = world.conn.query();
	query << "select i.*, ict.usage_type from items i left join item_client_types ict on ict.id = i.client_type_id  where id = " << existingId;

	mysqlpp::StoreQueryResult res;
	fwutil::DBHelper::select(query, &res, "items load");
	if (res && res.num_rows() > 0) {
		return (loadFromResultRow(res[0]));
	}
	return false;
}

/**
* A helper function, retrieve object values from a mysqlpp result row.
* @param row The result row to read the value from
* @return true on success, false otherwise
*/
bool Item::loadFromResultRow(const mysqlpp::Row& row) {
	id = row["id"];
	templateId = row["template_id"];
	clientTypeId = row["client_type_id"];
	//std::cout << "got client define: " << clientDefineId << std::endl;
	ownerId = row["owner_id"];
	setId = row["set_id"];
	graphicId = row["graphic_id"];
	playfieldId = row["playfield_id"];
	x = row["x"];
	y = row["y"];
	graphicsX = row["graphics_x"];
	graphicsY = row["graphics_y"];
	name = row["name"].c_str();
	description = row["description"].c_str();
	availableStatus = row["available_status"].c_str();
	usageType = row["usage_type"].c_str();
	canSell = row["can_sell"].c_str();
	canDrop = row["can_drop"].c_str();
	units = row["units"];
	unitsSell = row["units_sell"];
	price = row["price"];
	respawn = row["respawn"].c_str();
	respawnDelay = row["respawn_delay"];
	equippedStatus = row["equipped_status"].c_str();
	healthEffect = row["health_effect"];
	manaEffect = row["mana_effect"];
	attackEffect = row["attack_effect"];
	defenseEffect = row["defense_effect"];
	damageEffect = row["damage_effect"];
	skillEffect = row["skill_effect"];
	magicEffect = row["magic_effect"];
	healthregenerateEffect = row["healthregenerate_effect"];
	manaregenerateEffect = row["manaregenerate_effect"];
	actionEffect1 = row["action_effect_1"];
	actionEffect2 = row["action_effect_2"];
	effectDuration = row["effect_duration"];
	requiredSkill = row["required_skill"];
	requiredMagic = row["required_magic"];
	frequency = row["frequency"];
	range = row["range"];

	onSuccessfulLoad();
	return true;
}

/**
* Remove the DB record which is associated with this object.
* @return true on success, false otherwise
*/
bool Item::removeFromDB() {
	if (id > 0) {
		mysqlpp::Query query = world.conn.query();
		query << "delete from items where id = " << mysqlpp::quote << id;
		return removeByQuery(query, "remove items");
	}

	return false;
}


Item* Item::instanciate() {
	Item* instance = _TRACK_NEW(new Item(*this));
	instance->id = 0;
	instance->templateId = id;
	return instance;
}

void Item::removeFromWorld() {
	world.removeLiveItem(getId());
}

// ---
// static
// ---

void Item::getAllForInventory(FWWorld& world, unsigned int characterId, std::vector<SPItem>& inventoryItems) {
	mysqlpp::Query query = world.conn.query();
	query << "select i.*, ict.usage_type from items i left join item_client_types ict on ict.id = i.client_type_id where owner_id = " << characterId;

	mysqlpp::StoreQueryResult res;
	fwutil::DBHelper::select(query, &res, "Get all or inventory");

	if (res) {
		for (size_t i = 0; i < res.num_rows(); ++i) {
			SPItem nextItem(_TRACK_NEW(new Item(world)));
			nextItem->loadFromResultRow(res[i]);
			inventoryItems.push_back(nextItem);
		}

	}

}
