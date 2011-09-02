#include "Graphic.h"
#include "../FWWorld.h"
#include "DBHelper.h"
#include <sys/stat.h>

using namespace fwworld;

std::string Graphic::imageBasePath = "./graphics";
int Graphic::netChunkSize = 512;

/**
* Create a new object and initialize it to default values.
* @param world The world this object belongs to (and therefore has access to)
*/
Graphic::Graphic(FWWorld& world) : DBSynchObject(world),
type(GraphicType::generic),
filename(""),
loadForWorld(YesNoEnum::yes),
description(""),
imageData(0),
imageDataSize(0),
systemStatus(SystemStatus::normal),
wbLastChanged()
{
	objectTypeId = WorldObject::otGraphic;
}

/** Destruction of object. */
Graphic::~Graphic() {
	releaseImageData();
}

/**
* Store this object to the database. If the id of the object is non-zero, this will result
* in an update.
* @return true on success, false otherwise
*/
bool Graphic::storeToDB() {
	mysqlpp::Query query = world.conn.query();
	if (id == 0) {
		// insert new record
		query	<< "insert into graphics (type, filename, load_for_world, description, system_status, wb_last_changed )"
			<< "values ("
			<< mysqlpp::quote << type.str() << ","
			<< mysqlpp::quote << filename << ","
			<< mysqlpp::quote << loadForWorld.str() << ","
			<< mysqlpp::quote << description << ","
			<< mysqlpp::quote << systemStatus.str() << "," << mysqlpp::quote << wbLastChanged.strSQL()
			<< ")";
	} else {
		// update record
		query	<< "update graphics set "
			<< "type = " << mysqlpp::quote << type.str() << ","
			<< "filename = " << mysqlpp::quote << filename << ","
			<< "load_for_world = " << mysqlpp::quote << loadForWorld.str() << ","
			<< "description = " << mysqlpp::quote << description << ","
			<< ", system_status = " << mysqlpp::quote << systemStatus.str()
			<< ", wb_last_changed = " << mysqlpp::quote << wbLastChanged.strSQL()
			<< " where id = " << id;
	}
	return storeByQuery(query, "Graphic store");
}

/**
* Re-implemented virtual function to do the actual loading of the object straight from the database from the
* record which is identified by the id provided.
* @param id The database id used to retrieve the values of the object
* @return true on success, false otherwise
*/
bool Graphic::loadFromDB(unsigned int existingId) {
	mysqlpp::Query query = world.conn.query();
	query << "select * from graphics where id = " << existingId;
	mysqlpp::StoreQueryResult res;

	fwutil::DBHelper::select(query, &res, "graphic load");

	if (res && res.num_rows() > 0) {
		return loadFromResultRow(res[0]);
	}
	return false;
}

bool Graphic::loadFromDBWithImageData(unsigned int existingId) {
	if (loadFromDB(existingId) && loadImageData()) {
		return true;
	}
	return false;
}

/**
* A helper function, retrieve object values from a mysqlpp result row.
* @param row The result row to read the value from
* @return true on success, false otherwise
*/
bool Graphic::loadFromResultRow(const mysqlpp::Row& row) {
	id = row["id"];
	type = row["type"].c_str();
	filename = row["filename"].c_str();
	loadForWorld = row["load_for_world"].c_str();
	description = row["description"].c_str();
	systemStatus = row["system_status"].c_str();
	wbLastChanged.setFromSQL(row["wb_last_changed"].c_str());

	onSuccessfulLoad();
	return true;
}

/**
* Delete the object from the database.
* @return true on success, false otherwise
*/
bool Graphic::removeFromDB() {
	if (id > 0) {
		mysqlpp::Query query = world.conn.query();
		query << "delete from graphics where id = " << mysqlpp::quote << id;
		return removeByQuery(query, "Graphic remove");
	}
	return false;
}

/**
* Static function to retrieve all objects from the database as a container of smart pointers (boost::shared_ptr)
* to objects.
* @param world The world object the objects in the container will belong to
* @param containerAll The container which will hold the objects
* @param loadFileData Whether or not to load the file contents along with each record; if set to true the data is loaded
* from the file associated with the filename field of each graphic that is loaded - each graphic object in the container will
* then have a valid imageData pointer as long as the image for the given graphic could be loaded. You can access the raw image
* data by using getData() on the individual graphic object. The graphic object releases the image data in the destructor, or you
* may call releaseImageData
* @param order DB order statement (not including the ORDER BY keywords)
* @param whereFilter DB where statement (not including the WHERE keyword)
*/
void Graphic::getAll(FWWorld& world, std::map<unsigned int, SPGraphic>& containerAll, bool loadFiledata /* = true */, const std::string& order /*= "id asc"*/, const std::string& whereFilter /* = "" */) {
	mysqlpp::Query query = world.conn.query();
	query << "select * from graphics where system_status <> 'deleted'";

	if (!whereFilter.empty()) {query << " and (" << whereFilter << ") ";}
	if (!order.empty()) {query << " order by " << order;}
	mysqlpp::StoreQueryResult res;

	fwutil::DBHelper::select(query, &res, "graphic get all");

	if (res) {
		bool insert;
		for (size_t i = 0; i < res.num_rows(); ++i) {
			insert = true;
			// store to smart pointer
			SPGraphic spGraphic(_TRACK_NEW(new Graphic(world)));
			spGraphic->loadFromResultRow(res[i]);
			if (loadFiledata) {
				insert = spGraphic->loadImageData();
			}
			if (insert) {
				// store in container
				containerAll.insert(std::pair<unsigned int, SPGraphic>(spGraphic->getId(), spGraphic));
			}
		}
	}
}

/**
 * Get the full path to the file whith which this Graphic object is associated.
 * @return The full path.
 */
std::string Graphic::getFullPath() {
	return imageBasePath + "/" + type.str() + "/" + filename;
}

/**
 * Load the image data which is associated with the filename associated with the graphic object.
 * If image data is already loaded when calling this function, this data will be freed.
 * @return true on success, false otherwise
 */
bool Graphic::loadImageData() {
	releaseImageData();

	std::ifstream imgFile;

	imgFile.exceptions( std::ifstream::eofbit | std::ifstream::failbit | std::ifstream::badbit );
	try {
		std::string fullFilename = getFullPath();
		std::cout << "loading: " << fullFilename << " .. ";

		imgFile.open (fullFilename.c_str(), std::ios::in | std::ios::binary);
		// get file size
		imgFile.seekg(0, std::ios::end);
		int size = (int)imgFile.tellg();
		imgFile.seekg(0, std::ios::beg);
		// actually read in the image data and set the size
		imageData = _TRACK_NEW(new unsigned char[size]);
		imgFile.read((char*)imageData, size);
		imageDataSize = (int)imgFile.gcount();
		std::cout << "OK, " << imgFile.gcount() << " bytes" << std::endl;
		imgFile.close();

		return true;
	} catch (std::ifstream::failure e) {
		std::cout << std::endl << "Exception opening/reading file: " + filename << ": " << e.what() << std::endl;
	}

	return false;
}

/**
 * Free the image data which is held in memory (if any).
 * @return true on success, false otherwise
 */
bool Graphic::releaseImageData() {
	if (imageData != 0) {
		std::cout << "releasing image data: " << filename << std::endl;
		delete[] imageData;
		imageData = 0;
		imageDataSize = 0;
	}
	return true;

}

