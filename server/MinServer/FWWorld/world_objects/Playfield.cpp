#include "Playfield.h"
#include "Character.h"
#include "Item.h"
#include <boost/bind.hpp>

using namespace fwworld;

int Playfield::s_NetChunkSize = 512;



Playfield::Playfield(FWWorld& world) : DBSynchObject(world),
iconGraphicId(0), worldPartId(0), worldPartX(0), worldPartY(0),
name(""), description(""), width(0), height(0), availableStatus(AvailableStatus::all),
createdDate(),
systemStatus(SystemStatus::normal), wbLastChanged(),
cells(0)
{
}

Playfield::~Playfield() {
	// in scheduled items remove from db those tagged to be cleared
	for (std::vector<SPItem>::iterator it = scheduledItems.begin(); it!=scheduledItems.end(); ) {
		SPItem item = (*it);
		if (item->getScheduleType() == Item::st_cleanup) {
			item->removeFromDB();
			it = scheduledItems.erase(it);
		} else {
			it++;
		}
	}

	dropCells();
	for(std::map<unsigned int, SPItem>::iterator it = allPlayfieldItems.begin(); it!=allPlayfieldItems.end();it++) {
		world.removeLiveItem(it->second->getId());
	}
	allPlayfieldItems.clear();
	scheduledItems.clear();

	std::cout << "playfield " << name << " destroyed." << std::endl;
}

/**
* Save this object to the DB.
* @return true on success, false on failure.
*/
bool Playfield::storeToDB() {
	mysqlpp::Query query = world.conn.query();

	cellsToData();

	if (id == 0) {
		// insert new record
		query	<< "insert into playfields (icon_graphic_id, world_part_id, world_part_x, world_part_y, name, description, \
				   width, height, available_status, created_date, `data`, system_status, wb_last_changed \
				   )"
				   << "values ("
				   << iconGraphicId << ","
				   << worldPartId << ","
				   << worldPartX << ","
				   << worldPartY << ","
				   << mysqlpp::quote << name << ","
				   << mysqlpp::quote << description << ","
				   << width << ","
				   << height << ","
				   << mysqlpp::quote << availableStatus.str() << ","
				   << mysqlpp::quote << createdDate.strSQL() << ","
				   << "\"" << mysqlpp::escape << data.strSQL() << "\","
				   << mysqlpp::quote << systemStatus.str() << ","
				   << mysqlpp::quote << wbLastChanged.strSQL()
				   << ")";
	} else {
		// update record
		query	<< "update playfields set "
			<< " icon_graphic_id = " << iconGraphicId << ", world_part_id = " << worldPartId
			<< ", world_part_x = " << worldPartX << ", world_part_y = " << worldPartY
			<< ", name = " << mysqlpp::quote << name << ", description = " << mysqlpp::quote << description
			<< ", width = " << width << ", height = " << height
			<< ", available_status = " << mysqlpp::quote << availableStatus.str()
			<< ", created_date = " << mysqlpp::quote << createdDate.strSQL()
			<< ", `data` = '" << mysqlpp::escape << data.strSQL() << "'"
			<< ", system_status = " << mysqlpp::quote << systemStatus.str()
			<< ", wb_last_changed = " << mysqlpp::quote << wbLastChanged.strSQL()
			<< " where id = " << id;
	}

	return storeByQuery(query, "playfield store");
}

/**
* Populate the object values by loading them from the DB.
* @param existingId The database id of the object which identifies the associated DB record.
* @return true on success, false otherwise
*/
bool Playfield::loadFromDB(unsigned int existingId) {
	mysqlpp::Query query = world.conn.query();
	query << "select * from playfields where id = " << existingId;
	mysqlpp::StoreQueryResult res;
	fwutil::DBHelper::select(query, &res, "Playfield load");

	if (res && res.num_rows() > 0) {
		return (loadFromResultRow(res[0]));
	}
	return false;
}

/**
 * Load the data field into this playfield object. Note that this will overwrite the data which might have been previously.
 * @return true on success, false otherwise
 */
bool Playfield::loadData() {
	mysqlpp::Query query = world.conn.query();
	query << "select data from playfields where id = " << getId();
	mysqlpp::StoreQueryResult res;
	fwutil::DBHelper::select(query, &res, "Playfield load data");
	if (res && res.num_rows() > 0) {
		mysqlpp::Row row = res[0];
		data.fromBuffer((const unsigned char*)(row["data"].data()), (unsigned int)row["data"].length());
		dataToCells();
		return true;
	}
	return false;
}


/**
* A helper function, retrieve object values from a mysqlpp result row.
* @param row The result row to read the value from
* @return true on success, false otherwise
*/
bool Playfield::loadFromResultRow(const mysqlpp::Row& row, bool includeData /*=true*/) {
	id = row["id"];
	iconGraphicId = row["icon_graphic_id"];
	worldPartId = row["world_part_id"];
	worldPartX = row["world_part_x"];
	worldPartY = row["world_part_y"];
	name = row["name"].c_str();
	description = row["description"].c_str();
	width = row["width"];
	height = row["height"];
	availableStatus = row["available_status"].c_str();
	createdDate.setFromSQL(row["created_date"].c_str());
	if (includeData) {
		data.fromBuffer((const unsigned char*)(row["data"].data()), (unsigned int)row["data"].length());
		dataToCells();
	}
	systemStatus = row["system_status"].c_str();
	wbLastChanged.setFromSQL(row["wb_last_changed"].c_str());

	onSuccessfulLoad();
	return true;
}

/**
* Remove the DB record which is associated with this object.
* @return true on success, false otherwise
*/
bool Playfield::removeFromDB() {
	if (id > 0) {
		mysqlpp::Query query = world.conn.query();
		query << "delete from playfields where id = " << mysqlpp::quote << id;
		return removeByQuery(query, "remove playfield");
	}
	return false;
}

/**
 * Set the data array to empty.
 */
void Playfield::setEmptyData() {
	data.resetNew(width * height * bytesPerCell);
}

/**
 * Reset both cells and data.
 */
void Playfield::setEmptyDataAndCells() {
	data.resetNew(width * height * bytesPerCell);
	dataToCells();
}

/**
 * Write the cells' values to the raw data array.
 */
void Playfield::cellsToData() {
	setEmptyData();
	if (width == 0 || height == 0) return;

	for (unsigned int x=0; x<width; x++) {
		for (unsigned int y=0; y<height; y++) {
			int index = (y*width*bytesPerCell) + (x*bytesPerCell);
			data[index] = static_cast<unsigned char>(cells[x][y].getFunction().toInt());
			data[index+1] = static_cast<unsigned char>(cells[x][y].getTileInfo().toInt());
		}
	}
}

/**
 * Populate the cells' values from the raw data array.
 */
void Playfield::dataToCells() {
	dropCells();
	if (width == 0 || height == 0) return;

	cells = new PlayfieldCell*[width];

	for (unsigned int x=0; x<width; x++) {
		cells[x] = new PlayfieldCell[height];
		for (unsigned int y=0; y<height; y++) {
			int index = (y*width*bytesPerCell) + (x*bytesPerCell);
			PlayfieldCellFunction cellFunction(data[index]);
			TileInfo tileInfo(data[index + 1]);
			cells[x][y].assign(cellFunction, tileInfo);
		}
	}
}

/**
 * Clean up the cells (normally called on destruction) or whenever replacing all cells.
 */
void Playfield::dropCells() {
	if (cells != 0) {
		for(unsigned int x=0; x<width; x++) {
			delete[] cells[x];	// delete entire column
		}
		delete[] cells;	// delete empty column array
	}
}

/**
 * Add the given character to this playfield.
 * This will actually add the character to the appropriate cell determined by character position.
 * @param c Pointer to the character object
 */
void Playfield::addCharacter(Character* c) {
	std::map<unsigned int, Character*>::iterator it = allPlayfieldCharacters.find(c->getId());
	if (it == allPlayfieldCharacters.end()) {
		allPlayfieldCharacters.insert(std::pair<unsigned int, Character*>(c->getId(), c));
	}

	int cellX = static_cast<int>(c->getX() / PlayfieldCell::c_DefaultWidth);
	int cellY = static_cast<int>(c->getY() / PlayfieldCell::c_DefaultHeight);
	cells[cellX][cellY].addCharacter(c);

}

/**
* Add the given item to this playfield.
* This will actually add the item to the appropriate cell determined by item position.
* @param c Pointer to the item object
*/
void Playfield::addItem(SPItem i) {
	std::map<unsigned int, SPItem>::iterator it = allPlayfieldItems.find(i->getId());
	if (it == allPlayfieldItems.end()) {
		allPlayfieldItems.insert(std::pair<unsigned int, SPItem>(i->getId(), i));
	}

	int cellX = static_cast<int>(i->getX() / PlayfieldCell::c_DefaultWidth);
	int cellY = static_cast<int>(i->getY() / PlayfieldCell::c_DefaultHeight);
	cells[cellX][cellY].addItem(i.get());

}

unsigned int Playfield::cellsItemCount() {
	unsigned int aTotal = 0;
	for (unsigned int ax=0; ax < getWidth(); ax++) {
		for (unsigned int ay=0; ay < getHeight(); ay++) {
			std::vector<Item*>& its = cells[ax][ay].getItems();
			aTotal += (unsigned int)its.size();
		}
	}
	return aTotal;
}

unsigned int Playfield::cellsCharacterCount() {
	unsigned int aTotal = 0;
	for (unsigned int ax=0; ax < getWidth(); ax++) {
		for (unsigned int ay=0; ay < getHeight(); ay++) {
			std::vector<Character*>& chars = cells[ax][ay].getCharacters();
			aTotal += (unsigned int)chars.size();
		}
	}
	return aTotal;
}



/**
* Remove the given character from this playfield.
* This will actually remove the character from the cell determined by character position.
* @param c Pointer to the character object
*/
void Playfield::removeCharacter(Character* c) {
	std::cout << world.clock.getTimestampStr() << " Removing character from playfield: " << c->getId() << std::endl;

	int cellX = static_cast<int>(c->getX() / PlayfieldCell::c_DefaultWidth);
	int cellY = static_cast<int>(c->getY() / PlayfieldCell::c_DefaultHeight);
	cells[cellX][cellY].removeCharacterById(c->getId());
	allPlayfieldCharacters.erase(c->getId());

	std::cout << world.clock.getTimestampStr() << " Remaining characters on playfield: " << allPlayfieldCharacters.size() << " remaining characters in cells: " << cellsCharacterCount() << std::endl;
}

/**
* Remove the given item from this playfield.
* This will actually remove the item from the cell determined by item position.
* @param c Pointer to the character object
*/
void Playfield::removeItem(SPItem i, bool removeFromWorld, bool allowRemoveDB /*=true*/) {
	removeItemFromCell(*i);
	// check if scheduled for respawn
	if (i->getScheduledTime() > 0) {
		scheduledItems.erase(std::remove_if(scheduledItems.begin(), scheduledItems.end(), boost::bind(&Item::getId, _1) == i->getId()), scheduledItems.end());
		if (i->getScheduleType() == Item::st_cleanup && allowRemoveDB) {
			i->removeFromDB();
		}
	}
	if (removeFromWorld) {
		world.removeLiveItem(i->getId());
	}
	allPlayfieldItems.erase(i->getId());

}

void Playfield::removeItemFromCell(Item& it) {
	int cellX = static_cast<int>(it.getX() / PlayfieldCell::c_DefaultWidth);
	int cellY = static_cast<int>(it.getY() / PlayfieldCell::c_DefaultHeight);
	cells[cellX][cellY].removeItemById(it.getId());
}


SPItem Playfield::getItem(unsigned int id) {
	std::map<unsigned int, SPItem>::iterator it = allPlayfieldItems.find(id);
	if (it != allPlayfieldItems.end()) {
		return it->second;
	}
	return SPItem();
}

Character* Playfield::getCharacter(unsigned int id) {
	std::map<unsigned int, Character*>::iterator it = allPlayfieldCharacters.find(id);
	if (it != allPlayfieldCharacters.end()) {
		return it->second;
	}
	return 0;
}


void Playfield::scheduleItem(unsigned int itemId, Item::ScheduleType scheduleType) {
	SPItem curItem = getItem(itemId);
	if (curItem.get() != 0) {
		clock_ms_t rtime = world.clock.getTimestampMS();
		if (scheduleType == Item::st_respawn) {
			rtime += curItem->getRespawnDelay() * 1000;
		} else {
			rtime += Item::s_defaultCleanupSeconds * 1000;
		}

		curItem->setScheduledTime(rtime);
		curItem->setScheduleType(scheduleType);

		std::vector<SPItem>::iterator insertIt = scheduledItems.end();
		for (std::vector<SPItem>::iterator it = scheduledItems.begin(); it!=scheduledItems.end(); it++)
		{
			SPItem otherItem = *it;
			if (otherItem->getScheduledTime() > rtime) {
				insertIt = it;
				break;
			}
		}

		scheduledItems.insert(insertIt, curItem);
	}
}

void Playfield::checkScheduledItems(clock_ms_t curTime, std::vector<Item*>& respawnedItems, std::vector<Item*>& clearedItems) {
	for (std::vector<SPItem>::iterator it = scheduledItems.begin(); it!=scheduledItems.end(); )
	{
		bool remove = false;
		SPItem otherItem = *it;
		if (otherItem->getScheduledTime() > curTime) {
			break;
		} else {
			if (otherItem->getScheduleType() == Item::st_respawn) {
				addItem(otherItem);
				respawnedItems.push_back(otherItem.get());
			} else {
				remove = true;
				clearedItems.push_back(otherItem.get());
			}
			otherItem->setScheduledTime(0);
			otherItem->setScheduleType(Item::st_unknown);

			it = scheduledItems.erase(it);

			if (remove) {
				// clean up item at this point when the item is no longer in the scheduledItems vector (to avoid iterator invalidation)
				removeItem(otherItem, true, true);
			}
		}
	}
}




bool Playfield::validPositionForCharacter(Character* c, unsigned int xPosPx, unsigned int yPosPx) {
	unsigned int x1 = (xPosPx+blockTolerance) / PlayfieldCell::c_DefaultWidth;
	unsigned int x2 = (xPosPx+c->getGraphicsDim()-1-blockTolerance) / PlayfieldCell::c_DefaultWidth;
	unsigned int y1 = (yPosPx+blockTolerance) / PlayfieldCell::c_DefaultHeight;
	unsigned int y2 = (yPosPx+c->getGraphicsDim()-1-blockTolerance) / PlayfieldCell::c_DefaultHeight;

	unsigned int widthPx = static_cast<int>(width*PlayfieldCell::c_DefaultWidth);
	unsigned int heightPx = static_cast<int>(height*PlayfieldCell::c_DefaultHeight);

	//std::cout << "x: " << xPosPx << " y: " << yPosPx << " x1: " << x1 << " y1: " << y1 << " x2: " << x2 << " y2: " << y2 << std::endl;

	//std::cout << "newX: " << (xPosPx+blockTolerance) << ", " << (xPosPx + c->getGraphicsDim()-1-blockTolerance) << std::endl;
	//std::cout << "newY: " << (yPosPx+blockTolerance) << ", " << (yPosPx + c->getGraphicsDim()-1-blockTolerance) << std::endl;


	return
		xPosPx + c->getGraphicsDim() <= widthPx &&
		yPosPx + c->getGraphicsDim() <= heightPx &&
		xPosPx >= 0 && yPosPx >= 0 &&
		!(cells[x1][y1].hasMainFunction(PlayfieldCellFunction::function_blocked)) &&
		!(cells[x1][y2].hasMainFunction(PlayfieldCellFunction::function_blocked)) &&
		!(cells[x2][y1].hasMainFunction(PlayfieldCellFunction::function_blocked)) &&
		!(cells[x2][y2].hasMainFunction(PlayfieldCellFunction::function_blocked));
}


void Playfield::appendObjectsInRect(fwutil::Rect rect, fwutil::Rect ignoreRect, std::vector<Character*>* characterContainer, std::vector<Item*>*itemContainer) {
	if (rect.x1 < 0) {rect.x1 = 0;}
	if (rect.y1 < 0) {rect.y1 = 0;}
	if (rect.x2 >= static_cast<int>(width)) {rect.x2 = static_cast<int>(width)-1;}
	if (rect.y2 >= static_cast<int>(height)) {rect.y2 = static_cast<int>(height)-1;}

	bool nothingToIgnore = ignoreRect.empty();

	for (int x=rect.x1; x<=rect.x2; ++x) {
		for (int y=rect.y1; y<=rect.y2; ++y) {
			if (!nothingToIgnore && x>=ignoreRect.x1 && x <= ignoreRect.x2 &&
				y>=ignoreRect.y1 && y <= ignoreRect.y2)
				{
					continue;
				}
			if (characterContainer!=0) {
				std::vector<Character*>& cellCharacters = cells[x][y].getCharacters();
				characterContainer->insert(characterContainer->end(), cellCharacters.begin(), cellCharacters.end());
			}
			if (itemContainer!=0) {
				std::vector<Item*>& cellItems = cells[x][y].getItems();
				itemContainer->insert(itemContainer->end(), cellItems.begin(), cellItems.end());
			}

		}
	}
}


/**
* Given a certain look-at position on the playfield measured in cells, get all characters visible within the cell range.
* Append the characters to the vector passed by reference.
* @param characterContainer The container to which the characters should be appended.
* @param xPx The x coordinate in pixels
* @param yPx The y coordinate in pixels
*/
void Playfield::appendObjectsInVisRange(unsigned int curCellX, unsigned int curCellY, std::vector<Character*>* characterContainer, std::vector<Item*>*itemContainer) {
	// define the top left and bottom right for the visibility rectangle
	appendObjectsInRect(getVisibilityRectAt(curCellX, curCellY), fwutil::Rect(), characterContainer, itemContainer);
}

/**
 * Given a certain look-at position on the playfield in pixels, get all characters visible within the cell range of the cell
 * at the given position. Append the characters to the vector passed by reference.
 * @param characterContainer The container to which the characters should be appended.
 * @param xPx The x coordinate in pixels
 * @param yPx The y coordinate in pixels
 */
void Playfield::appendObjectsInVisRangePx(unsigned int xPx, unsigned int yPx, std::vector<Character*>* characterContainer, std::vector<Item*>* itemContainer) {
	unsigned int curCellX = static_cast<int>(xPx / PlayfieldCell::c_DefaultWidth);
	unsigned int curCellY = static_cast<int>(yPx / PlayfieldCell::c_DefaultHeight);
	appendObjectsInRect(getVisibilityRectAt(curCellX, curCellY), fwutil::Rect(), characterContainer, itemContainer);
}

fwutil::Rect Playfield::getVisibilityRectAt(unsigned int cellX, unsigned int cellY) {
	int cellXStart = cellX - FWWorld::s_VisibilityCellRange;
	int cellXEnd = cellX + FWWorld::s_VisibilityCellRange;
	int cellYStart = cellY - FWWorld::s_VisibilityCellRange;
	int cellYEnd = cellY + FWWorld::s_VisibilityCellRange;

	if (cellXStart < 0) {
		cellXEnd += -cellXStart;
		cellXStart = 0;
	} else if (cellX < FWWorld::s_VisibilityCellRangeWSize) {
		cellXStart = 0;	// snap to left border
	}

	if (static_cast<unsigned int>(cellXEnd) >= width) {
		cellXStart -= (cellXEnd-width+1);
		cellXEnd = width-1;
		if (cellXStart < 0) {cellXStart = 0;}
	} else if (width - 1 - cellX < FWWorld::s_VisibilityCellRangeWSize) {
		cellXEnd = width-1;
	}

	if (cellYStart < 0) {
		cellYEnd += -cellYStart;
		cellYStart = 0;
	} else if (cellY < FWWorld::s_VisibilityCellRangeWSize) {
		cellYStart = 0;	// snap to top border
	}

	if (static_cast<unsigned int>(cellYEnd) >= height) {
		cellYStart -= (cellYEnd-height+1);
		cellYEnd = height-1;
		if (cellYStart < 0) {cellYStart = 0;}
	} else if (height - 1 - cellY < FWWorld::s_VisibilityCellRangeWSize) {
		cellYEnd = height - 1;
	}

	return fwutil::Rect(cellXStart, cellYStart, cellXEnd, cellYEnd);
}

void Playfield::appendObjectsAffectedByCellChange(unsigned int curCellX, unsigned int curCellY, unsigned int newCellX, unsigned int newCellY,
												  std::vector<Character*>* charsToAdd, std::vector<Character*>* charsToRemove,
												  std::vector<Item*>* itemsToAdd, std::vector<Item*>* itemsToRemove
												  ) {

	fwutil::Rect curRect = getVisibilityRectAt(curCellX, curCellY);
	fwutil::Rect newRect = getVisibilityRectAt(newCellX, newCellY);

	//curRect.print();
	//newRect.print();

	if (curRect == newRect) {
		return;
	}

	bool isIntersection = curRect.intersectsWith(newRect);

	if (!isIntersection) {
		// no intersection of old visibility window with new visibility window, add and remove entirely
		appendObjectsInRect(newRect, fwutil::Rect(), charsToAdd, itemsToAdd);
		appendObjectsInRect(curRect, fwutil::Rect(), charsToRemove, itemsToRemove);
	} else {

		int xDistance = newCellX - curCellX;
		int yDistance = newCellY - curCellY;

		if (xDistance != 0 && yDistance != 0) {
			// moving x and y causing a more complex intersection
			fwutil::Rect intersection;

			// get intersection
			if (xDistance < 0) {
				intersection.x1 = curRect.x1;
				intersection.x2 = newRect.x2;
			} else {
				intersection.x1 = newRect.x1;
				intersection.x2 = curRect.x2;
			}
			if (yDistance < 0) {
				intersection.y1 = curRect.y1;
				intersection.y2 = newRect.y2;
			} else {
				intersection.y1 = newRect.y1;
				intersection.y2 = curRect.y2;
			}

			appendObjectsInRect(curRect, intersection, charsToRemove, itemsToRemove);
			appendObjectsInRect(newRect, intersection, charsToAdd, itemsToAdd);

		} else if (xDistance != 0 || yDistance != 0) {
			// note: it could be that both are 0 - happens when the view window doesn't change because the view range is blocked at the edges of the playfield
			// moving only x or y causing a 'one-dimensional' intersection

			bool skipAdd = true;
			bool skipRemove = true;

			fwutil::Rect addRect;
			fwutil::Rect remRect;

			if (xDistance != 0) {
				addRect.y1 = remRect.y1 = newRect.y1;
				addRect.y2 = remRect.y2 = newRect.y2;

				if (xDistance < 0) {
					if (newRect.x1 < curRect.x1) {
						addRect.x1 = newRect.x1;
						addRect.x2 = curRect.x1-1;
						skipAdd = false;
					}
					if (newRect.x2 < curRect.x2) {
						remRect.x1 = newRect.x2+1;
						remRect.x2 = curRect.x2;
						skipRemove = false;
					}
				} else {
					if (newRect.x2 > curRect.x2) {
						addRect.x1 = curRect.x2+1;
						addRect.x2 = newRect.x2;
						skipAdd = false;
					}
					if (newRect.x1 > curRect.x1) {
						remRect.x1 = curRect.x1;
						remRect.x2 = newRect.x1-1;
						skipRemove = false;
					}
				}
			} else if (yDistance != 0) {
				addRect.x1 = remRect.x1 = newRect.x1;
				addRect.x2 = remRect.x2 = newRect.x2;

				if (yDistance < 0) {
					if (newRect.y1 < curRect.y1) {
						addRect.y1 = newRect.y1;
						addRect.y2 = curRect.y1-1;
						skipAdd = false;
					}
					if (newRect.y2 < curRect.y2) {
						remRect.y1 = newRect.y2+1;
						remRect.y2 = curRect.y2;
						skipRemove = false;
					}
				} else {
					if (newRect.y2 > curRect.y2) {
						addRect.y1 = curRect.y2+1;
						addRect.y2 = newRect.y2;
						skipAdd = false;
					}
					if (newRect.y1 > curRect.y1) {
						remRect.y1 = curRect.y1;
						remRect.y2 = newRect.y1-1;
						skipRemove = false;
					}
				}

			}

			if (!skipAdd) {
				appendObjectsInRect(addRect, fwutil::Rect(), charsToAdd, itemsToAdd);
			}
			if (!skipRemove) {
				appendObjectsInRect(remRect, fwutil::Rect(), charsToRemove, itemsToRemove);
			}
		}
	}
}


/**
 * Note that this only loads the playfield_graphics records, it does not load any image data.
 * The actual binary image data is not loaded, even if loadGraphicObjects is set to true.
 * loadGraphicObjects Set to true if the information from the graphics table (which is associated with
 * this entry from the playfield_graphics table) should be loaded as well.
 */
void Playfield::loadBackgroundGraphics(bool loadGraphicObjects /*=false*/) {
	mysqlpp::StoreQueryResult res;
	graphicsByTypeResultSet("background", res);

	if (res && res.num_rows() > 0) {
		size_t numRows = res.num_rows();
		for (size_t i=0; i<numRows; i++) {
			SPPlayfieldGraphic pg(_TRACK_NEW(new PlayfieldGraphic(world)));
			if (pg->loadFromResultRow(res[i], loadGraphicObjects)) {
				std::cout << name <<  ": adding background graphic: " << pg->getGraphicId() << std::endl;
				backgroundGraphics.push_back(pg);
			}
		}
	}
}

/**
* Note that this only loads the playfield_graphics records, it does not load any image data.
* The actual binary image data is not loaded, even if loadGraphicObjects is set to true.
* loadGraphicObjects Set to true if the information from the graphics table (which is associated with
* this entry from the playfield_graphics table) should be loaded as well.
*/
void Playfield::loadCharacterGraphics(bool loadGraphicObjects /*=false*/) {
	mysqlpp::StoreQueryResult res;
	graphicsByTypeResultSet("character", res);

	if (res && res.num_rows() > 0) {
		size_t numRows = res.num_rows();
		for (size_t i=0; i<numRows; i++) {
			SPPlayfieldGraphic pg(_TRACK_NEW(new PlayfieldGraphic(world)));
			if (pg->loadFromResultRow(res[i], loadGraphicObjects)) {
				std::cout << name <<  ": adding character graphic: " << pg->getId() << std::endl;
				characterGraphics.push_back(pg);
			}
		}
	}
}

/**
 * Get the PlayfieldGraphics for the current playfield and the defined type, store into a database result set.
 * @param gfxType The type as a string (e.g. "background" or "character")
 * @param res The result set which is filled by reference.
 */
void Playfield::graphicsByTypeResultSet(const std::string gfxType, mysqlpp::StoreQueryResult& res) {
	mysqlpp::Query query = world.conn.query();
	query <<	"select pg.* from playfield_graphics pg left join graphics g on pg.graphic_id = g.id \
				where pg.playfield_id = " << id << " and g.type = " << mysqlpp::quote << gfxType
				<< " and pg.system_status <> 'deleted' and g.system_status <> 'deleted'  \
				order by pg.id asc";

	fwutil::DBHelper::select(query, &res, "playfield load graphics by type");
}

void Playfield::loadItems() {
	mysqlpp::Query query = world.conn.query();
	query << "select i.*, ict.usage_type from items i left join item_client_types ict on ict.id = i.client_type_id where playfield_id = " << id << " and respawn = 'yes'";

	mysqlpp::StoreQueryResult res;
	fwutil::DBHelper::select(query, &res, "playfield load items");

	if (res && res.num_rows() > 0) {
		size_t numRows = res.num_rows();
		for (size_t i=0; i<numRows; i++) {
			SPItem it(_TRACK_NEW(new Item(world)));
			if (it->loadFromResultRow(res[i])) {
				addItem(it);
				world.addLiveItem(it.get());
			}
		}
	}

}

/** Load the playfield data as required by the world builder. */
void Playfield::loadDependenciesWorldBuilder() {
	std::cout << name << ": loading playfield dependencies: " << std::endl;
	loadBackgroundGraphics(true);
	if (data.getSize() == 0) {
		loadData();
	}
	loadItems();
}

void Playfield::loadDependenciesLive() {
	std::cout << name << ": loading playfield dependencies: " << std::endl;
	loadBackgroundGraphics(true);
	loadCharacterGraphics(true);
	if (data.getSize() == 0) {
		loadData();
	}
	loadItems();
}


// --------------------
// static
// --------------------


/**
* Static function to retrieve all objects from the database as a container as provided.
* @param world The world object the objects in the container will belong to.
* @param containerAll The container which will hold the objects, passed by reference.
* @param includeData Whether or not to include data with the playfield objects (as this field may occupy substantially more memory).
* However, be aware that saving an object with no data loaded will result in overwriting the data field in the db with empty data!
* @param whereFilter DB where statement without the WHERE.
* @param order DB order statement without the ORDER BY.
*/
void Playfield::getAll(FWWorld& world, mysqlpp::StoreQueryResult& res,
			bool includeData /* = true */,
			const std::string& whereFilter /*= ""*/, const std::string& order /* = "name asc" */)
{
	mysqlpp::Query query = world.conn.query();
	query << "select * from playfields where system_status <> 'deleted'";
	if (!whereFilter.empty()) {query << " and (" << whereFilter << ") ";}
	if (!order.empty()) {query << " order by " << order;}

	fwutil::DBHelper::select(query, &res, "playfield get all");
}

/**
* Static function to retrieve all objects from the database as a container as provided.
* @param world The world object the objects in the container will belong to.
* @param containerAll The container which will hold the objects, passed by reference.
* @param includeData Whether or not to include data with the playfield objects (as this field may occupy substantially more memory).
* However, be aware that saving an object with no data loaded will result in overwriting the data field in the db with empty data!
* @param whereFilter DB where statement without the WHERE.
* @param order DB order statement without the ORDER BY.
*/
void Playfield::getAll(FWWorld& world, std::vector<SPPlayfield>& containerAll,
					   bool includeData /* = true */,
					   const std::string& whereFilter /*= ""*/, const std::string& order /* = "name asc" */)
{
	mysqlpp::StoreQueryResult res;
	getAll(world, res, includeData, whereFilter, order);
	if (res) {
		for (size_t i = 0; i < res.num_rows(); ++i) {
			SPPlayfield newPf(_TRACK_NEW(new Playfield(world)));
			newPf->loadFromResultRow(res[i], includeData);
			containerAll.push_back(newPf);
		}
	}

}

/**
* Static function to retrieve all objects from the database as a container as provided.
* @param world The world object the objects in the container will belong to.
* @param containerAll The container which will hold the objects, passed by reference.
* @param includeData Whether or not to include data with the playfield objects (as this field may occupy substantially more memory).
* However, be aware that saving an object with no data loaded will result in overwriting the data field in the db with empty data!
* @param whereFilter DB where statement without the WHERE.
* @param order DB order statement without the ORDER BY.
*/
void Playfield::getAll(FWWorld& world, std::map<unsigned int, SPPlayfield>& containerAll,
					   bool includeData /* = true */,
					   const std::string& whereFilter /*= ""*/, const std::string& order /* = "name asc" */)
{
	mysqlpp::StoreQueryResult res;
	getAll(world, res, includeData, whereFilter, order);
	if (res) {
		for (size_t i = 0; i < res.num_rows(); ++i) {
			SPPlayfield newPf(_TRACK_NEW(new Playfield(world)));
			newPf->loadFromResultRow(res[i], includeData);
			containerAll.insert(std::pair<unsigned int, SPPlayfield>(newPf->getId(), newPf));
		}
	}
}
