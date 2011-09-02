#ifndef Graphic_h__
#define Graphic_h__

#include "DBSynchObject.h"
#include "DBSynchObjectMacros.h"
#include "WorldObjectTypeDefs.h"
#include "db_fieldtypes/GraphicType.h"
#include "db_fieldtypes/YesNoEnum.h"
#include "db_fieldtypes/SystemStatus.h"
#include <string>
#include <map>

namespace fwworld {

	class FWWorld;

	/**
	 * Represents a graphical object used as part of the game.
	 * In addition to the DB fields this object can also hold the actual image data loaded from disk.
	 */
	class Graphic : public DBSynchObject {

		protected:
			bool loadFromResultRow(const mysqlpp::Row& row);
			bool loadFromDBWithImageData(unsigned int existingId);

		public:
			Graphic(FWWorld& world);
			virtual ~Graphic();

			virtual bool storeToDB();
			virtual bool loadFromDB(unsigned int existingId);
			virtual bool removeFromDB();

			DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const GraphicType&, type, Type, graphics, type, .str())
			DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string&, filename, Filename, graphics, filename, )
			DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const YesNoEnum&, loadForWorld, LoadForWorld, graphics, load_for_world, .str())
			DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string&, description, Description, graphics, description, )

			DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const SystemStatus&, systemStatus, SystemStatus, graphics, system_status, .str())
			DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const fwutil::DateTime&, wbLastChanged, WbLastChanged, graphics, wb_last_changed, .strSQL())

			unsigned char* getImageData() const {return imageData;};
			unsigned int getImageDataSize() const {return imageDataSize;};

			bool loadImageData();
			bool releaseImageData();

			std::string getFullPath();

			static void getAll(FWWorld& world, std::map<unsigned int, SPGraphic>& containerAll, bool loadFiledata = true, const std::string& order = "id asc", const std::string& whereFilter = "");

			static std::string imageBasePath;
			static int netChunkSize;

		private:
			GraphicType type;
			std::string filename;
			YesNoEnum loadForWorld;
			std::string description;

			unsigned char* imageData;
			unsigned int imageDataSize;

			SystemStatus systemStatus;
			fwutil::DateTime wbLastChanged;

	};

} // end namespace fwworld

#endif // Graphic_h__
