#ifndef GameServer_h__
#define GameServer_h__

#include "DBSynchObject.h"
#include "DBSynchObjectMacros.h"
#include "WorldObjectTypeDefs.h"
#include "db_fieldtypes/SystemStatus.h"
#include <string>
#include <vector>

namespace fwworld {

class FWWorld;

/**
 * A Gameserver object represents a server the user can connect to 
 * (just a name and an ip, for display in a selection list).
 * Note that Gameservers do not represent the server object itself.
 * Gameservers are just informational records in the database to indicate which
 * server machines the game is run on, so clients can connect to them.
 * The server logic itself is found in the FWServer class.
 */
class Gameserver : public DBSynchObject {

	protected:
		bool loadFromResultRow(const mysqlpp::Row& row);

	public:
		Gameserver(FWWorld& world);
		virtual ~Gameserver();

		virtual bool storeToDB();
		virtual bool loadFromDB(unsigned int existingId);
		virtual bool removeFromDB();

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string&, name, Name, gameservers, name, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string&, ip, Ip, gameservers, ip, )

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const SystemStatus&, systemStatus, SystemStatus, gameservers, system_status, .str())
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const fwutil::DateTime&, wbLastChanged, WbLastChanged, gameservers, wb_last_changed, .strSQL())


		static void getAll(FWWorld& world, std::vector<SPGameserver>& containerAll, const std::string& order = "name asc");

	private:
		std::string name;
		std::string ip;
		SystemStatus systemStatus;
		fwutil::DateTime wbLastChanged;
};

} // end namespace

#endif // GameServer_h__