#include "Gameserver.h"
#include "../FWWorld.h"
#include "DBHelper.h"

using namespace fwworld;

/**
 * Create a new Gameserver object and initialize it to default values.
 * @param world The world this object belongs to (and therefore has access to)
 */
Gameserver::Gameserver(FWWorld& world) : DBSynchObject(world),
name(""),
ip(""),
systemStatus(SystemStatus::normal),
wbLastChanged()
{
	objectTypeId = WorldObject::otGameServer;
}

/** Destruction of a Gameserver object. */
Gameserver::~Gameserver() {
}

/**
 * Store this object to the database. If the id of the object is non-zero, this will result 
 * in an update because the id for the object is set by the super class only if the call to
 * doLoadFromDB() succeeds (usually called from the public loadFromDB() function in the super class).
 * @return True on success, false otherwise
 */
bool Gameserver::storeToDB() {
	mysqlpp::Query query = world.conn.query();
	if (id == 0) {
		// insert new record
		query	<< "insert into gameservers (name, ip, system_status, wb_last_changed)"
			<< "values (" 
			<< mysqlpp::quote << name << "," 
			<< mysqlpp::quote << ip << ","
			<< mysqlpp::quote << systemStatus.str() << "," << mysqlpp::quote << wbLastChanged.strSQL()					
			<< ")";
	} else {
		// update user record
		query	<< "update gameservers set "
			<< "name = " << mysqlpp::quote << name << ","
			<< "ip = " << mysqlpp::quote << ip
			<< ", system_status = " << mysqlpp::quote << systemStatus.str() 
			<< ", wb_last_changed = " << mysqlpp::quote << wbLastChanged.strSQL()
			<< " where id = " << id;
	}

	return storeByQuery(query, "GameServer store");
}

/**
 * Re-implemented virtual function to do the actual loading of the object straight from the database from the
 * record which is identified by the id provided.
 * @param id The database id used to retrieve the values of the object
 * @return true on success, false otherwise
 */
bool Gameserver::loadFromDB(unsigned int existingId) {
	mysqlpp::Query query = world.conn.query();
	query << "select * from gameservers where id = " << existingId;
	mysqlpp::StoreQueryResult res;

	fwutil::DBHelper::select(query, &res, "gameserver load");

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
bool Gameserver::loadFromResultRow(const mysqlpp::Row& row) {
	id = row["id"];
	name = row["name"].c_str();
	ip = row["ip"].c_str();
	systemStatus = row["system_status"].c_str();
	wbLastChanged.setFromSQL(row["wb_last_changed"].c_str());

	onSuccessfulLoad();
	return true;
}

/**
 * Delete the object from the database. This is called from the publicly accessible
 * base class function removeFromDB(), clients should only call this base class function.
 * After removal from the db, the application should take care of also removing this object 
 * from memory to avoid trying to store the object in the database again or using the object
 * further although it has been removed in the DB.
 * @return true on success, false otherwise
 */
bool Gameserver::removeFromDB() {
	if (id > 0) {
		mysqlpp::Query query = world.conn.query();
		query << "delete from gameservers where id = " << mysqlpp::quote << id;
		return removeByQuery(query, "GameServer remove");	
	}
	return false;
}

/**
 * Static function to retrieve all game servers from the database as a vector of smart pointers (boost::shared_ptr) 
 * to Gameservers.
 * @param world The world object the objects in the container will belong to
 * @param containerAll The container which will hold the objects
 * @param order DB order statement without the ORDER BY keywords
 */
void Gameserver::getAll(FWWorld& world, std::vector<SPGameserver>& containerAll, const std::string& order /*= "name asc"*/) {
	mysqlpp::Query query = world.conn.query();
	query << "select * from gameservers where system_status <> 'deleted'";

	if (!order.empty()) {query << " order by " << order;}

	mysqlpp::StoreQueryResult res;

	fwutil::DBHelper::select(query, &res, "gameserver get all");

	if (res) {
		for (size_t i = 0; i < res.num_rows(); ++i) {
			// store to smart pointer
			SPGameserver spGameserver(_TRACK_NEW(new Gameserver(world)));
			spGameserver->loadFromResultRow(res[i]);
			// store in vector
			containerAll.push_back(spGameserver);
		}
	}
}