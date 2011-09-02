#ifndef DBHelper_h__
#define DBHelper_h__

#include "mysql++.h"

/*
// todo: if compiling for max performance, redefine the macros to not catch exceptions
#define DB_SAFE_SELECT(query, res, hint) \
try {\
	res = query.store();\
} catch (const mysqlpp::BadQuery& er) {\
	std::cout << "*** MySQL query error: " << er.what() << std::endl;\
	std::cout << "*** " << hint << std::endl;\
	std::cout << "*** " << query.str() << std::endl;\
}

// todo: if compiling for max performance, redefine the macros to not catch exceptions
#define DB_SAFE_EXEC(query, res, hint) \
	try {\
	res = query.exec();\
} catch (const mysqlpp::BadQuery& er) {\
	std::cout << "*** MySQL query error: " << er.what() << std::endl;\
	std::cout << "*** " << hint << std::endl;\
	std::cout << "*** " << query.str() << std::endl;\
}
*/



namespace fwutil {
	class DBHelper {
		
		public:

			static bool select(mysqlpp::Query& query, mysqlpp::StoreQueryResult* storeResult, const std::string& hint) {
				bool result = false;
				try {
					if ((*storeResult) = query.store()) {
						result = true;
					}
				
				} catch (const mysqlpp::BadQuery& er) {
					std::cout << "*** MySQL query error: " << er.what() << std::endl;
					std::cout << "*** " << hint << std::endl;
					std::cout << "*** " << query.str() << std::endl;
					result = false;
				}
				return result;
			}

			static bool exec(mysqlpp::Query& query, const std::string& hint) {
				bool result = false;
				try {
					result = query.exec();
				} catch (const mysqlpp::BadQuery& er) {
					std::cout << "*** MySQL query error: " << er.what() << std::endl;
					std::cout << "*** " << hint << std::endl;
					std::cout << "*** " << query.str() << std::endl;
					result = false;
				}
				return result;
			}


	};

}

/*
#include "mysql++.h"

class DBHelper {

	static mysqlpp::safeSelect(mysqlpp::Query query, StoreQueryResult* result) {
		try {
		}
	}



};
*/

#endif // DBHelper_h__