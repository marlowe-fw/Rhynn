#ifndef UserSession_h__
#define UserSession_h__

#include "DBSynchObject.h"
#include "DBSynchObjectMacros.h"
#include "WorldObjectTypeDefs.h"
#include "DBHelper.h"
#include "DateTime.h"
#include "GenericClock.h"
#include "db_fieldtypes/UserSessionStatus.h"

namespace fwworld {

class FWWorld;

class UserSession : public DBSynchObject {

	protected:

	public:

		UserSession(FWWorld& world);
		virtual ~UserSession();

		virtual bool storeToDB();
		virtual bool loadFromDB(unsigned int existingId);
		virtual bool removeFromDB();

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, userId, UserId, user_sessions, user_id, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, characterId, CharacterId, user_sessions, character_id, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string&, keyActionLog, KeyActionLog, user_sessions, key_action_log, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string&, errorLog, ErrorLog, user_sessions, error_log, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string&, suspiciousLog, SuspiciousLog, user_sessions, suspicious_log, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string&, debugLog, DebugLog, user_sessions, debug_log, )
		//DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const fwutil::DateTime&, sessionStart, SessionStart, user_sessions, session_start, .strSQL())
		//DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const fwutil::DateTime&, sessionEnd, SessionEnd, user_sessions, session_end, .strSQL())
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const UserSessionStatus&, status, Status, users, status, .str())

		inline void startSession() {sessionStart = world.clock.getDateTime(); sessionStartTS = world.clock.getTimestampMS();}
		inline void endSession() {sessionEnd = world.clock.getDateTime(); sessionEndTS = world.clock.getTimestampMS(); sessionDuration = sessionEndTS - sessionStartTS;}
		inline void emptyLog() {keyActionLog = errorLog = suspiciousLog = debugLog = "";}

		void logKeyAction(const std::string& msg);
		void logError(const std::string& msg);
		void logSuspiciousAction(const std::string& msg);
		void logDebug(const std::string& msg);
		

	private:
		unsigned int userId;
		unsigned int characterId;
		UserSessionStatus status;
		std::string keyActionLog;
		std::string errorLog;
		std::string suspiciousLog;
		std::string debugLog;
		fwutil::DateTime sessionStart;
		fwutil::DateTime sessionEnd;
		clock_ms_t sessionDuration;
		// helper members to calculate the session duration
		clock_ms_t sessionStartTS;
		clock_ms_t sessionEndTS;

		void addLogMessage(std::string& msgBuffer, const std::string msg);

};

} // end namespace fwworld

#endif // UserSession_h__
