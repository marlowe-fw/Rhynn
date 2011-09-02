#include "CharacterClass.h"
#include "../FWWorld.h"
#include "DBHelper.h"
#include <boost/regex.hpp>

using namespace fwworld;

CharacterClass::CharacterClass(FWWorld& world) : DBSynchObject(world),
graphicId(0),availableStatus(AvailableStatus::none),graphicsX(0),graphicsY(0),graphicsDim(0),
systemName(""),displayName(""),
healthBase(0),healthModifier(0),manaBase(0),manaModifier(0),
attackBase(0),attackModifier(0),defenseBase(0),defenseModifier(0),damageBase(0),damageModifier(0),
skillBase(0),skillModifier(0),magicBase(0),magicModifier(0),
healthregenerateBase(0),healthregenerateModifier(0),manaregenerateBase(0),manaregenerateModifier(0),
systemStatus(SystemStatus::normal),
wbLastChanged()
{
	objectTypeId = WorldObject::otCharacterClass;
}


CharacterClass::~CharacterClass() {
}

/**
* Save this object to the DB.
* @return true on success, false on failure.
*/
bool CharacterClass::storeToDB() {
	mysqlpp::Query query = world.conn.query();
	if (id == 0) {
		// insert new record
		query	<< "insert into character_classses (graphic_id, available_status, graphics_x, graphics_y, graphics_dim, \
				   systemName, displayName, \
				   health_base, health_modifier, mana_base, mana_modifier, \
				   attack_base, attack_modifier, defense_base, defense_modifier, damage_base, damage_modifier, \
				   skill_base, skill_modifier, magic_base, magic_modifier, \
				   healthregenerate_base, healthregenerate_modifier, manaregenerate_base, manaregenerate_modifier, \
				   system_status, wb_last_changed \
				   )"
				   << "values (" 
				   << graphicId << "," 
				   << mysqlpp::quote << availableStatus.str() << "," 
				   << graphicsX << "," 
				   << graphicsY << "," 
				   << graphicsDim << "," 
				   << mysqlpp::quote << systemName << "," << mysqlpp::quote << displayName << ","
				   << healthBase << "," << healthModifier << "," << manaBase << "," << manaModifier << "," 
				   << attackBase << "," << attackModifier << "," << defenseBase << "," << defenseModifier << "," << damageBase << "," << damageModifier << "," 
				   << skillBase << "," << skillModifier << "," << magicBase << "," << magicModifier << ","
				   << healthregenerateBase << "," << healthregenerateModifier << "," << manaregenerateBase << "," << manaregenerateModifier << ","
				   << mysqlpp::quote << systemStatus.str() << "," << mysqlpp::quote << wbLastChanged.strSQL()					
				   << ")";
	} else {
		// update record
		query	<< "update character_classes set "
			<< "graphic_id = " << graphicId
			<< ", available_status = " << mysqlpp::quote << availableStatus.str()
			<< ", graphics_x = " << graphicsX << ", graphics_y = " << graphicsY << ", graphics_dim = " << graphicsDim
			<< ", system_name = " << mysqlpp::quote << systemName << ", display_name = " << mysqlpp::quote << displayName
			<< ", health_base = " << healthBase << ", health_modifier = " << healthModifier
			<< ", mana_base = " << manaBase << ", mana_modifier = " << manaModifier
			<< ", attack_base = " << attackBase << ", attack_modifier = " << attackModifier
			<< ", defense_base = " << defenseBase << ", defense_modifier = " << defenseModifier
			<< ", damage_base = " << damageBase << ", damage_modifier = " << damageModifier
			<< ", skill_base = " << skillBase << ", skill_modifier = " << skillModifier
			<< ", magic_base = " << magicBase << ", magic_modifier = " << magicModifier
			<< ", healthregenerate_base = " << healthregenerateBase << ", healthregenerate_modifier = " << healthregenerateModifier
			<< ", manaregenerate_base = " << manaregenerateBase << ", manaregenerate_modifier = " << manaregenerateModifier
			<< ", system_status = " << mysqlpp::quote << systemStatus.str() 
			<< ", wb_last_changed = " << mysqlpp::quote << wbLastChanged.strSQL()
			<< " where id = " << id;
	}
	return storeByQuery(query, "CharacterClass store");
}

/**
* Populate the object values by loading them from the DB.
* @param existingId The database id of the object which identifies the associated DB record.
* @return true on success, false otherwise
*/
bool CharacterClass::loadFromDB(unsigned int existingId) {
	mysqlpp::Query query = world.conn.query();
	query << "select * from character_classes where id = " << existingId;
	mysqlpp::StoreQueryResult res;

	fwutil::DBHelper::select(query, &res, "CharacterClass load");

	if (res && res.num_rows() > 0) {
		return loadFromResultRow(res[0]);
	}
	return false;
}

/** 
* A helper function, retrieve object values from a mysqlpp result row.
* @param row The result row to read the value from
* @return true on success, false otherwise
*/
bool CharacterClass::loadFromResultRow(const mysqlpp::Row& row) {
	id = row["id"];
	graphicId = row["graphic_id"];
	availableStatus = row["available_status"].c_str();
	graphicsX = row["graphics_x"];
	graphicsY = row["graphics_y"];
	graphicsDim = row["graphics_dim"];
	systemName = row["system_name"].c_str();
	displayName = row["display_name"].c_str();
	healthBase = row["health_base"];
	healthModifier = row["health_modifier"];
	manaBase = row["mana_base"];
	manaModifier = row["mana_modifier"];
	attackBase = row["attack_base"];
	attackModifier = row["attack_modifier"];
	defenseBase = row["defense_base"];
	defenseModifier = row["defense_modifier"];
	damageBase = row["damage_base"];
	damageModifier = row["damage_modifier"];
	skillBase = row["skill_base"];
	skillModifier = row["skill_modifier"];
	magicBase = row["magic_base"];
	magicModifier = row["magic_modifier"];
	healthregenerateBase = row["healthregenerate_base"];
	healthregenerateModifier = row["healthregenerate_modifier"];
	manaregenerateBase = row["manaregenerate_base"];
	manaregenerateModifier = row["manaregenerate_modifier"];
	systemStatus = row["system_status"].c_str();
	wbLastChanged.setFromSQL(row["wb_last_changed"].c_str());

	onSuccessfulLoad();

	return true;
}

/**
* Remove the DB record which is associated with this object.
* @return true on success, false otherwise
*/
bool CharacterClass::removeFromDB() {
	if (id > 0) {
		mysqlpp::Query query = world.conn.query();
		query << "delete from character_classes where id = " << mysqlpp::quote << id;
		return removeByQuery(query, "CharacterClass remove");
	}
	return false;
}



// -------------------
// static
// -------------------

/**
* Static helper function to retrieve all character_classes records into a mysqlpp result set.
* @param world The world object used to setup the db query
* @param res The result set object which will be filled with the records found
* @param order DB order statement without the ORDER BY keywords
*/
void CharacterClass::getAll(FWWorld& world, mysqlpp::StoreQueryResult& res, const std::string& order /*= "id asc"*/) {
	mysqlpp::Query query = world.conn.query();
	query << "select * from character_classes where system_status <> 'deleted'";

	if (!order.empty()) {query << " order by " << order;}
	fwutil::DBHelper::select(query, &res, "CharacterClass::getAll");
}

/**
* Static function to retrieve all objects from the database as a container of smart pointers (boost::shared_ptr) 
* @param world The world object the objects in the container will belong to
* @param containerAll The container which will hold the objects, passed by reference
* @param order DB order statement without the ORDER BY keywords
* @return A container of smart pointers (boost::shared_ptr) pointing to the newed objects
*/
void CharacterClass::getAll(FWWorld& world, std::vector<SPCharacterClass>& containerAll, const std::string& order /*= "name asc"*/) {
	mysqlpp::StoreQueryResult res;
	getAll(world, res, order);

	if (res) {
		for (size_t i = 0; i < res.num_rows(); ++i) {
			// store to smart pointer
			SPCharacterClass spCharacterClass(_TRACK_NEW(new CharacterClass(world)));
			spCharacterClass->loadFromResultRow(res[i]);
			// store in vector
			containerAll.push_back(spCharacterClass);
		}
	}
}

/**
* Static function to retrieve all objects from the database into a container of smart pointers (boost::shared_ptr) 
* @param world The world object the objects in the container will belong to
* @param containerAll The container which will hold the objects, passed by reference
* @param order DB order statement without the ORDER BY keywords
* @return A container of smart pointers (boost::shared_ptr) pointing to the newed objects
*/
void CharacterClass::getAll(FWWorld& world, std::map<unsigned int, SPCharacterClass>& containerAll, const std::string& order /*= "id asc"*/) {
	mysqlpp::StoreQueryResult res;
	getAll(world, res, order);
	
	if (res) {
		for (size_t i = 0; i < res.num_rows(); ++i) {
			SPCharacterClass spCharacterClass(_TRACK_NEW(new CharacterClass(world)));
			spCharacterClass->loadFromResultRow(res[i]);
			std::cout << "character class " << spCharacterClass->displayName << " loaded" << std::endl;
			containerAll.insert(std::pair<unsigned int, SPCharacterClass>(spCharacterClass->getId(), spCharacterClass));
		}
	}
}
