#include "PlayfieldGraphic.h"
#include "Graphic.h"

using namespace fwworld;

PlayfieldGraphic::PlayfieldGraphic(FWWorld& world) : DBSynchObject(world),
playfieldId(0), graphicId(0),
systemStatus(SystemStatus::normal), wbLastChanged()
{
	objectTypeId = WorldObject::otPlayfieldGraphic;
}

PlayfieldGraphic::~PlayfieldGraphic() {
	std::cout << "playfield graphic " << playfieldId << ", " << graphicId << " destroyed." << std::endl;
}

/**
* Save this object to the DB.
* @return true on success, false on failure.
*/
bool PlayfieldGraphic::storeToDB() {
	mysqlpp::Query query = world.conn.query();
	if (id == 0) {
		// insert new record
		query	<< "insert into playfield_graphics (graphic_id, playfield_id, system_status, wb_last_changed )"
				   << "values (" 
				   << graphicId << "," 
				   << playfieldId << "," 
				   << mysqlpp::quote << systemStatus.str() << "," 
				   << mysqlpp::quote << wbLastChanged.strSQL()
				   << ")";
	} else {
		// update record
		query	<< "update playfields set "
			<< " graphic_id = " << graphicId << ", playfield_id = " << playfieldId
			<< ", system_status = " << mysqlpp::quote << systemStatus.str()
			<< ", wb_last_changed = " << mysqlpp::quote << wbLastChanged.strSQL()
			<< " where id = " << id;
	}

	return storeByQuery(query, "playfield graphic store");
}


/**
* Populate the object values by loading them from the DB.
* @param existingId The database id of the object which identifies the associated DB record.
* @return true on success, false otherwise
*/
bool PlayfieldGraphic::loadFromDB(unsigned int existingId) {
	mysqlpp::Query query = world.conn.query();
	query << "select * from playfield_graphics where id = " << existingId;
	mysqlpp::StoreQueryResult res;

	fwutil::DBHelper::select(query, &res, "Playfield graphic load");

	if (res && res.num_rows() > 0) {
		return (loadFromResultRow(res[0]));
	}
	return false;
}

/** 
* A helper function, retrieve object values from a mysqlpp result row.
* @param row The result row to read the value from
* @param loadGraphicObject Whether or not to load the data from the graphics table (not including the actual image data)
* @return true on success, false otherwise
*/
bool PlayfieldGraphic::loadFromResultRow(const mysqlpp::Row& row, bool loadGraphicObject /*= false*/) {
	id = row["id"];
	graphicId = row["graphic_id"];
	playfieldId = row["playfield_id"];
	systemStatus = row["system_status"].c_str();
	wbLastChanged.setFromSQL(row["wb_last_changed"].c_str());

	if (loadGraphicObject) {
		graphicObject  = SPGraphic(_TRACK_NEW(new Graphic(world)));
		graphicObject->loadFromDB(graphicId);	// will NOT load the binary image data
	}

	onSuccessfulLoad();

	return true;
}


/**
* Remove the DB record which is associated with this object.
* @return true on success, false otherwise
*/
bool PlayfieldGraphic::removeFromDB() {
	if (id > 0) {
		mysqlpp::Query query = world.conn.query();
		query << "delete from playfield_graphics where id = " << mysqlpp::quote << id;
		return removeByQuery(query, "remove playfield graphic");
	}
	return false;
}
