#ifndef Playfield_h__
#define Playfield_h__

#include "DBSynchObject.h"
#include "DBSynchObjectMacros.h"
#include "WorldObjectTypeDefs.h"
#include "db_fieldtypes/SystemStatus.h"
#include "db_fieldtypes/AvailableStatus.h"
#include "Rect.h"
#include "PlayfieldGraphic.h"
#include "BinaryDataWrapper.h"
#include "components/TileInfo.h"
#include "components/PlayfieldCellFunction.h"
#include "components/PlayfieldCell.h"

#include <boost/function.hpp>
#include <string>
#include <map>
#include "Item.h"

namespace fwworld {

class Character;

class Playfield : public DBSynchObject {

	private:
		unsigned int iconGraphicId;
		unsigned int worldPartId;
		unsigned int worldPartX;
		unsigned int worldPartY;

		std::string name;
		std::string description;
		unsigned int width;
		unsigned int height;


		AvailableStatus availableStatus;

		fwutil::DateTime createdDate;

		SystemStatus systemStatus;
		fwutil::DateTime wbLastChanged;

		fwutil::BinaryDataWrapper data;

		// non-db members or dependencies
		PlayfieldCell** cells;

		std::vector<SPPlayfieldGraphic> backgroundGraphics;
		std::vector<SPPlayfieldGraphic> characterGraphics;

		std::map<unsigned int, Character*> allPlayfieldCharacters;
		std::map<unsigned int, SPItem> allPlayfieldItems;
		std::vector<SPItem> scheduledItems;

		void graphicsByTypeResultSet(const std::string gfxType, mysqlpp::StoreQueryResult& res);
		void loadBackgroundGraphics(bool loadGraphicObjects = false);
		void loadCharacterGraphics(bool loadGraphicObjects = false);
		bool loadData();
		void loadItems();

		void dataToCells();
		void cellsToData();
		void dropCells();

		unsigned int cellsItemCount();
		unsigned int cellsCharacterCount();

		/*
		PlayfieldCellFunction getFunctionFromRaw(unsigned int cellX, unsigned int cellY) const;
		TileInfo getTileInfoFromRaw(unsigned int cellX, unsigned int cellY) const;
		*/

	public:
		static const unsigned int bytesPerCell = 2;
		static const unsigned int blockTolerance = 3;

		static int s_NetChunkSize;

		Playfield(FWWorld& world);
		virtual ~Playfield();

		virtual bool storeToDB();
		virtual bool loadFromDB(unsigned int existingId);
		virtual bool removeFromDB();
		bool loadFromResultRow(const mysqlpp::Row& row, bool includeData = true);

		void loadDependenciesWorldBuilder();
		void loadDependenciesLive();

		// ---
		// get and set data / cells
		// ---
		void setEmptyData();
		void setEmptyDataAndCells();

		inline PlayfieldCell** getCells() const {
			return cells;
		}

		inline PlayfieldCell& cellAt(unsigned int cellX, unsigned int cellY) const {
			return cells[cellX][cellY];
		}

		bool validPositionForCharacter(Character* c, unsigned int xPosPx, unsigned int yPosPx);
		// ---

		// ---
		// Characters
		// ---
		fwutil::Rect getVisibilityRectAt(unsigned int cellX, unsigned int cellY);

		void addCharacter(Character* c);
		void removeCharacter(Character* c);
		void addItem(SPItem i);
		void removeItem(SPItem i, bool removeFromWorld, bool allowRemoveDB = true);
		void removeItemFromCell(Item& it);

		void appendObjectsInVisRangePx(unsigned int xPx, unsigned int yPx, std::vector<Character*>* characters, std::vector<Item*>* items);
		void appendObjectsInVisRange(unsigned int curCellX, unsigned int curCellY, std::vector<Character*>* characterContainer, std::vector<Item*>* items);
		void appendObjectsInRect(fwutil::Rect rect, fwutil::Rect ignoreRect, std::vector<Character*>* characterContainer, std::vector<Item*>*itemContainer);
		void appendObjectsAffectedByCellChange(unsigned int curCellX, unsigned int curCellY, unsigned int newCellX, unsigned int newCellY,
												std::vector<Character*>* charsToAdd, std::vector<Character*>* charsToRemove,
												std::vector<Item*>* itemsToAdd, std::vector<Item*>* itemsToRemove
												);
		// --

		// --
		// Items
		// --
		SPItem getItem(unsigned int id);
		Character* getCharacter(unsigned int id);
		void scheduleItem(unsigned int itemId, Item::ScheduleType scheduleType);
		void checkScheduledItems(clock_ms_t curTime, std::vector<Item*>& respawnedItems, std::vector<Item*>& clearedItems);

		inline const std::vector<SPPlayfieldGraphic>& getBackgroundGraphics() const {
			return backgroundGraphics;
		}

		inline const std::vector<SPPlayfieldGraphic>& getCharacterGraphics() const {
			return characterGraphics;
		}



		static void getAll(
			FWWorld& world, mysqlpp::StoreQueryResult&,
			bool includeData = true ,
			const std::string& whereFilter = "", const std::string& order = "name asc"
			);

		static void getAll(
			FWWorld& world, std::vector<SPPlayfield>& containerAll,
			bool includeData = true,
			const std::string& whereFilter = "", const std::string& order = "name asc"
			);

		static void getAll(
			FWWorld& world, std::map<unsigned int, SPPlayfield>& containerAll,
			bool includeData = true,
			const std::string& whereFilter = "", const std::string& order = "name asc"
			);

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, iconGraphicId, IconGraphicId, playfields, icon_graphic_id, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, worldPartId, WorldPartId, playfields, world_part_id, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, worldPartX, WorldPartX, playfields, world_part_x, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, worldPartY, WorldPartY, playfields, world_part_y, )

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string&, name, Name, playfields, name, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string&, description, Description, playfields, description, )

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, width, Width, playfields, width, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, height, Height, playfields, height, )

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const AvailableStatus&, availableStatus, AvailableStatus, playfields, available_status, .str())

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const fwutil::DateTime&, createdDate, CreatedDate, playfields, created_date, .strSQL())

		// actual playfield data
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const fwutil::BinaryDataWrapper&, data, Data, playfields, data, .strSQL())

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const SystemStatus&, systemStatus, SystemStatus, playfields, system_status, .str())
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const fwutil::DateTime&, wbLastChanged, WbLastChanged, playfields, wb_last_changed, .strSQL())


};

}

#endif // Playfield_h__
