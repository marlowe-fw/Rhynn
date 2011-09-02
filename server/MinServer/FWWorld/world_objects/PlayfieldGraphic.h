#ifndef PlayfieldGraphic_h__
#define PlayfieldGraphic_h__

#include "DBSynchObject.h"
#include "DBSynchObjectMacros.h"
#include "WorldObjectTypeDefs.h"
#include "Graphic.h"

#include "db_fieldtypes/SystemStatus.h"
#include "CommonUtils.h"

#include <string>
#include <map>

namespace fwworld {

	class PlayfieldGraphic : public DBSynchObject {

	public:
		PlayfieldGraphic(FWWorld& world);
		virtual ~PlayfieldGraphic();

		virtual bool storeToDB();
		virtual bool loadFromDB(unsigned int existingId);
		virtual bool removeFromDB();
		bool loadFromResultRow(const mysqlpp::Row& row, bool loadGraphicObject = false);
		
		inline SPGraphic getGraphicObject() {
			return graphicObject;
		}

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, graphicId, GraphicId, playfield_graphics, graphic_id, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, playfieldId, PlayfieldId, playfield_graphics, playfield_id, )

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const SystemStatus&, systemStatus, SystemStatus, playfield_graphics, system_status, .str())
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const fwutil::DateTime&, wbLastChanged, WbLastChanged, playfield_graphics, wb_last_changed, .strSQL())		

	private:
		unsigned int playfieldId;
		unsigned int graphicId;
		
		SystemStatus systemStatus;
		fwutil::DateTime wbLastChanged;
		
		// non-db or associated data members
		SPGraphic graphicObject;

	};

}

#endif // PlayfieldGraphic_h__