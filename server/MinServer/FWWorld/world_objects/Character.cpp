#include "Character.h"
#include "CharacterClass.h"
#include "Item.h"
#include "DBHelper.h"
#include <boost/regex.hpp>
#include <mysql++.h>

using namespace fwworld;

unsigned int Character::s_initialPlayfieldId = 100019;
unsigned int Character::s_initialPlayfieldX = 0;
unsigned int Character::s_initialPlayfieldY = 0;
unsigned int Character::s_ItemPickupRadius = 32;


Character::Character(FWWorld& world) : DBSynchObject(world),
userId(0),classId(0),clanId(0),playfieldId(0),graphicId(0),graphicsX(0),graphicsY(0),graphicsDim(0),
x(0),respawnX(0),y(0),respawnY(0),level(0),levelPoints(0),experience(0),gold(0),
createdDate(),name(""),
healthBase(0),healthEffectsExtra(0),healthCurrent(0),manaBase(0),manaEffectsExtra(0),manaCurrent(0),
attackBase(0),attackEffectsExtra(0),defenseBase(0),defenseEffectsExtra(0),damageBase(0),damageEffectsExtra(0),
skillBase(0),skillEffectsExtra(0),magicBase(0),magicEffectsExtra(0),
healthregenerateBase(0),healthregenerateEffectsExtra(0),manaregenerateBase(0),manaregenerateEffectsExtra(0),
characterClass(0),
customStatusMsg(""),
systemStatus(SystemStatus::normal),
wbLastChanged(),
activeStatus(Character::as_inactive),
owningUser(0),
playfield(0),
direction(Direction::UP),
dead(false),
lastVitalityRefill(0),
inventory(this)
{
	objectTypeId = WorldObject::otCharacter;
}


Character::~Character() {
	std::cout << world.clock.getTimestampStr(true) << "Character destroyed: " << getId() << " remaining items: " << inventory.itemCount() << std::endl;
}

/**
 * Save this object to the DB.
 * @return true on success, false on failure.
 */
bool Character::storeToDB() {
	mysqlpp::Query query = world.conn.query();
	if (id == 0) {
		// insert new record
		query	<< "insert into characters (user_id, class_id, clan_id, playfield_id, graphic_id, graphics_x, graphics_y, graphics_dim, x, respawn_x, y, respawn_y, \
					level, level_points, experience, gold, created_date, name, custom_status_msg, \
					health_base, health_effects_extra, health_current, mana_base, mana_effects_extra, mana_current, \
					attack_base, attack_effects_extra, defense_base, defense_effects_extra, damage_base, damage_effects_extra, \
					skill_base, skill_effects_extra, magic_base, magic_effects_extra, \
					healthregenerate_base, healthregenerate_effects_extra, manaregenerate_base, manaregenerate_effects_extra, \
					system_status, wb_last_changed \
					)"
			<< "values ("
			<< userId << ","
			<< classId << ","
			<< clanId << ","
			<< playfieldId << ","
			<< graphicId << ","
			<< graphicsX << ","
			<< graphicsY << ","
			<< graphicsDim << ","
			<< x << "," << respawnX << "," << y << "," << respawnY << "," << level << "," << levelPoints << "," << experience << "," << gold << ","
			<< mysqlpp::quote << createdDate.strSQL() << "," << mysqlpp::quote << name << "," << mysqlpp::quote << customStatusMsg << ","
			<< healthBase << "," << healthEffectsExtra << "," << healthCurrent << "," << manaBase << "," << manaEffectsExtra << "," << manaCurrent << ","
			<< attackBase << "," << attackEffectsExtra << "," << defenseBase << "," << defenseEffectsExtra << "," << damageBase << "," << damageEffectsExtra << ","
			<< skillBase << "," << skillEffectsExtra << "," << magicBase << "," << magicEffectsExtra << ","
			<< healthregenerateBase << "," << healthregenerateEffectsExtra << "," << manaregenerateBase << "," << manaregenerateEffectsExtra << ","
			<< mysqlpp::quote << systemStatus.str() << "," << mysqlpp::quote << wbLastChanged.strSQL()
			<< ")";
	} else {
		// update record
		query << "update characters set "
			<< " user_id = " << userId
			<< ", class_id = " << classId
			<< ", clan_id = " << clanId
			<< ", playfield_id = " << playfieldId
			<< ", graphic_id = " << graphicId
			<< ", graphics_x = " << graphicsX << ", graphics_y = " << graphicsY << ", graphics_dim = " << graphicsDim
			<< ", x = " << x << ", respawn_x = " << respawnX << ", y = " << y << ", respawn_y = " << respawnY << ", level = " << level << ", level_points = " << levelPoints << ", experience = " << experience
			<< ", gold = " << gold << ", created_date = " << mysqlpp::quote << createdDate.strSQL()
			<< ", name = " << mysqlpp::quote << name << ", custom_status_msg = " << mysqlpp::quote << customStatusMsg
			<< ", health_base = " << healthBase << ", health_effects_extra = " << healthEffectsExtra << ", health_current = " << healthCurrent
			<< ", mana_base = " << manaBase << ", mana_effects_extra = " << manaEffectsExtra << ", mana_current = " << manaCurrent
			<< ", attack_base = " << attackBase << ", attack_effects_extra = " << attackEffectsExtra
			<< ", defense_base = " << defenseBase << ", defense_effects_extra = " << defenseEffectsExtra
			<< ", damage_base = " << damageBase << ", damage_effects_extra = " << damageEffectsExtra
			<< ", skill_base = " << skillBase << ", skill_effects_extra = " << skillEffectsExtra
			<< ", magic_base = " << magicBase << ", magic_effects_extra = " << magicEffectsExtra
			<< ", healthregenerate_base = " << healthregenerateBase << ", healthregenerate_effects_extra = " << healthregenerateEffectsExtra
			<< ", manaregenerate_base = " << manaregenerateBase << ", manaregenerate_effects_extra = " << manaregenerateEffectsExtra
			<< ", system_status = " << mysqlpp::quote << systemStatus.str()
			<< ", wb_last_changed = " << mysqlpp::quote << wbLastChanged.strSQL()
			<< " where id = " << id;
	}

	return storeByQuery(query, "character store");
}

/**
 * Populate the character object values by loading them from the DB.
 * @param existingId The database id of the object which identifies the associated DB record.
 * @return true on success, false otherwise
 */
bool Character::loadFromDB(unsigned int existingId) {
	mysqlpp::Query query = world.conn.query();
	query << "select * from characters where id = " << existingId;
	mysqlpp::StoreQueryResult res;

	fwutil::DBHelper::select(query, &res, "Character load");

	if (res && res.num_rows() > 0) {
		return (loadFromResultRow(res[0], true));
	}
	return false;
}


/**
* A helper function, retrieve object values from a mysqlpp result row.
* @param row The result row to read the value from
* @return true on success, false otherwise
*/
bool Character::loadFromResultRow(const mysqlpp::Row& row, bool addCharacterClass) {
	id = row["id"];
	userId = row["user_id"];
	classId = row["class_id"];
	playfieldId = row["playfield_id"];
	graphicId = row["graphic_id"];
	graphicsX = row["graphics_x"];
	graphicsY = row["graphics_y"];
	graphicsDim = row["graphics_dim"];
	x = row["x"];
	respawnX = row["respawn_x"];
	y = row["y"];
	respawnY = row["respawn_y"];
	level = row["level"];
	levelPoints = row["level_points"];
	experience = row["experience"];
	gold = row["gold"];
	name = row["name"].c_str();
	createdDate.setFromSQL(row["created_date"].c_str());
	healthBase = row["health_base"];
	healthEffectsExtra = row["health_effects_extra"];
	healthCurrent = row["health_current"];
	manaBase = row["mana_base"];
	manaEffectsExtra = row["mana_effects_extra"];
	manaCurrent = row["mana_current"];
	attackBase = row["attack_base"];
	attackEffectsExtra = row["attack_effects_extra"];
	defenseBase = row["defense_base"];
	defenseEffectsExtra = row["defense_effects_extra"];
	damageBase = row["damage_base"];
	damageEffectsExtra = row["damage_effects_extra"];
	skillBase = row["skill_base"];
	skillEffectsExtra = row["skill_effects_extra"];
	magicBase = row["magic_base"];
	magicEffectsExtra = row["magic_effects_extra"];
	healthregenerateBase = row["healthregenerate_base"];
	healthregenerateEffectsExtra = row["healthregenerate_effects_extra"];
	manaregenerateBase = row["manaregenerate_base"];
	manaregenerateEffectsExtra = row["manaregenerate_effects_extra"];
	customStatusMsg = row["custom_status_msg"].c_str();
	systemStatus = row["system_status"].c_str();
	wbLastChanged.setFromSQL(row["wb_last_changed"].c_str());

	if (addCharacterClass) {
		// also load the character class which is known by the world
		characterClass = world.getCharacterClass(classId);
	}

	onSuccessfulLoad();

	return true;
}

/**
 * Remove the DB record which is associated with this object.
 * @return true on success, false otherwise
 */
bool Character::removeFromDB() {
	if (id > 0) {
		mysqlpp::Query query = world.conn.query();
		query << "delete from characters where id = " << mysqlpp::quote << id;
		return removeByQuery(query, "remove character");
	}
	return false;
}

/**
 * Store the character itself and save all the records which are associated with this character and might need updating too
 * (items of the character ..)
 * @return true on success, false otherwise
 */
bool Character::storeWithDependencies() {
	// todo: store items, quests, etc.
	inventory.checkStoreItems();
	if (!isSynchronized()) {
		return storeToDB();
	}
	return true;
}

/**
 * In addition to removing the associated character record this will also remove associated DB records
 * like items owned by the character etc.
 * This function should only be used by the administrative tools as the normal way of character deletion is by just
 * setting the system_status in the DB to deleted (call setSystemStatus(SystemStatus::deleted))
 * @return true on success, false otherwise
 */
bool Character::removeWithDependencies() {
	// todo: remove items, stats, questelements etc.
	return removeFromDB();
}

void Character::removeFromWorld() {
	storeWithDependencies();

	inventory.clear();
	inventory.removeItemsFromWorld();
	
	world.removeLiveCharacter(getId());
}

/**
* Set a given character class for this character, note that this should only be done when creating a new character
* as the character class is otherwise set when loading from the DB.
* @param newClass The character class object to set, a copy will be used (usually obtained from the character classes held by the world object)
* @param setBaseValues Whether or not to set the character's values to those defined in the class (base attribute values attack, defense, ..)
*/
void Character::setCharacterClass(CharacterClass* newClass, bool setBaseValues /*= true*/) {
	characterClass = newClass;
	if  (setBaseValues) {
		setClassId(newClass->getId());
		setHealthBase(newClass->getHealthBase());
		setManaBase(newClass->getManaBase());
		setAttackBase(newClass->getAttackBase());
		setDefenseBase(newClass->getDefenseBase());
		setDamageBase(newClass->getDamageBase());
		setSkillBase(newClass->getSkillBase());
		setMagicBase(newClass->getMagicBase());
		setHealthregenerateBase(newClass->getHealthregenerateBase());
		setManaregenerateBase(newClass->getManaregenerateBase());
		setGraphicId(newClass->getGraphicId());
		setGraphicsX(newClass->getGraphicsX());
		setGraphicsY(newClass->getGraphicsY());
		setGraphicsDim(newClass->getGraphicsDim());
	}
}

void Character::loadInventory() {
	std::vector<SPItem> inventoryItems;
	Item::getAllForInventory(world, id, inventoryItems);
	for (std::vector<SPItem>::iterator it = inventoryItems.begin(); it!=inventoryItems.end(); it++) {
		world.addLiveItem((*it).get());
		inventory.addItem((*it));
	}
	inventory.initializeEquipment();
}


// -------------------
// static
// -------------------

/**
 * Get the DB id of a character which has the given name. Note that the application must enforce that character names are unique
 * unless they are AI operated (bots).
 * @param world A reference to the global world object
 * @param chName The name serving to identify the character record in the DB
 */
unsigned int Character::getIdForNameOfUserCharacter(FWWorld& world, const std::string& chName) {
	unsigned int existingId = 0;

	mysqlpp::Query query = world.conn.query();
	// user_id > 0 => owned by a user
	query << "select id from characters where user_id > 0 AND system_status <> 'deleted' AND name = BINARY " << mysqlpp::quote << chName;

	mysqlpp::StoreQueryResult res;

	fwutil::DBHelper::select(query, &res, "character idForName");

	if (res && res.num_rows() > 0) {
		existingId = res[0]["id"];
	}
	return existingId;
}

/**
 * Check if the given string is a valid character name.
 * @param newName The name to validate.
 * @return true on success, false otherwise
 */
bool Character::validName(const std::string& newName) {
	// allow lower case for now only, otherwise use boost::regex_constants::icase as the second param
	static const boost::regex nameRegex("^([a-z0-9.\\-_:,;?!()+*/='\\\\#[\\]@הצü]){1,10}$");
	return boost::regex_match(newName, nameRegex);
}

/**
* Create a new Character object which will be initialized with the base values defined by the given class
* which in turn is identified by the classId passed.
* @param classId The class id of the character class which the newly created character should belong to
* @param userId The id of the owning user, set to 0 if none
* @param name The name to set for the character
* @param store Whether or not to store the object to the database before returning it
* @return A smart pointer to the newly created Character object which is 0 on failure
*/
SPCharacter Character::createNewCharacterForClass(FWWorld& world, unsigned int classId, unsigned int userId, const std::string& name, bool store) {
	SPCharacter newCharacter;

	// get character class
	CharacterClass* selClass = world.getCharacterClass(classId);
	if (selClass != 0) {
		newCharacter = SPCharacter(_TRACK_NEW(new Character(world)));
		// the following call also assigns the base values of the CharacteClass to the Character
		newCharacter->setCharacterClass(selClass, true);
		newCharacter->setCreatedDate(fwutil::DateTime(true));
		newCharacter->setName(name);
		newCharacter->setUserId(userId);
		newCharacter->setPlayfieldId(Character::s_initialPlayfieldId);
		newCharacter->setX(Character::s_initialPlayfieldX);
		newCharacter->setY(Character::s_initialPlayfieldY);
		newCharacter->setHealthCurrent(newCharacter->getMaxHealth());
		newCharacter->setManaCurrent(newCharacter->getMaxMana());
		if (store) {
			if (!newCharacter->storeToDB()) {
				SPCharacter nullCharacter;
				return nullCharacter;
			}
		}
	}
	return newCharacter;
}

/**
 * Get the highscores and stor them into the passed container.
 * @return true on success, false otherwise
 */
bool Character::getHighscores(FWWorld& world, unsigned int startRank, unsigned int numRanks, std::vector<HighscoreEntry>& hsList) {
	if (startRank < 1 || numRanks < 1)
		return false;

	mysqlpp::Query query = world.conn.query();
	query << "select name, experience from characters order by experience desc limit " << numRanks << " offset " << (startRank-1);

	mysqlpp::StoreQueryResult res;
	fwutil::DBHelper::select(query, &res, "Character load");

	if (res) {
		for (size_t i = 0; i < res.num_rows(); ++i) {
			mysqlpp::Row& row = res[i];
			hsList.push_back(HighscoreEntry(row["name"].c_str(), startRank+i, row["experience"]));
		}
		return true;
	}

	return false;

}
