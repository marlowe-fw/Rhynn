#include "DBHelper.h"

/**
 * @param mc_dataType The c++ type of the attribute to be used in the functions (e.g. std::string or int)
 * note that use of const std::string& will usually result in better code for string attributes
 * @param mc_attrName The name of the c++ object attribute (e.g. password)
 * @param mc_attrNameCapitalized The capitalized name of the attribute to use for function naming
 * (e.g. Password will result in setPassword, getPassword, and storePasseord functions)
 * @param mc_ConversionForQuote (a suffix / function call to convert the attribute into a type suitable for mysqlpp quoting, e.g. .str())
 * leave blank for no conversion
 * This macro generates the setAttr, getAttr, and storeAttr functions
 * where Attr is the capitalized name of the attribute
 * Place this macro in your class in the public section below, e.g. below the actual attribute declaration
 * This effectively generates the source to synchronize a given attribute with the DB
 * setAttr(newValue, false) - just sets the value in the object and flags the object to indicate it needs synchronization with the DB
 * setAttr(newValue, true) - sets the value in the object and immediately afterwards stores the value to the db (calling storeAttr())
 * storeAttr() This stores the given attribute to the database for the given object
 * getAttr() just retrieves the current object's value
 * SEE THE EXAMPLE BELOW THE MACRO!
 */
#define DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS(mc_dataType, mc_attrName, mc_attrNameCapitalized, mc_tableName, mc_columnName, mc_ConversionForQuote) \
	void set##mc_attrNameCapitalized(mc_dataType new##mc_attrNameCapitalized, bool store = false) {\
		mc_attrName = new##mc_attrNameCapitalized; \
		if (store) { \
			if (!store##mc_attrNameCapitalized()) {\
				synchronized = false;\
			} else if (synchronized) {\
				/** was synchronized before and is now in synch still because field was saved */ \
				lastSynchronizedTime = world.clock.getTimestampMS();\
			}\
		} else {\
			synchronized = false;\
		}\
	}\
	\
	inline mc_dataType get##mc_attrNameCapitalized() const {\
		return mc_attrName;\
	}\
	\
	bool store##mc_attrNameCapitalized() {\
		if (!isLoaded()) {\
			return storeToDB();\
		} else {\
			mysqlpp::Query query = world.conn.query();\
			query << "update " << #mc_tableName << " set " << #mc_columnName << " = " << mysqlpp::quote << (mc_attrName)mc_ConversionForQuote << " where id = " << id;\
			return fwutil::DBHelper::exec(query, "attribute store");\
		}\
	}\
	\



/*
EXAMPLE (note that conversion param is empty):

placing this macro call in the class:
DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS(const std::string&, password, Password, users, password, )

will result in this code being generated:

void setPassword(const std::string& newPassword, bool store = false) {
	password = newPassword;
	if (id != 0 && store) {
		if (!storePassword()) {
			synchronized = false;
		} else if (synchronized) {
			lastSynchronizedTime = world.clock.getTimestampMS();
		}
	} else {
		synchronized = false;
	}
}

inline const std::string& getPassword() const {
	return password;
}

bool storePassword() {
	mysqlpp::Query query = world.conn.query();
	query << "update " << "users" << " set " << "password" << " = " << mysqlpp::quote << password << " where id = " << id;
	bool result;
	try { result = query.exec();}
	catch (const mysqlpp::BadQuery& er) {
		std::cout << "*** MySQL query error: " << er.what() << std::endl;
		std::cout << "*** " << "attribute store" << std::endl;
		std::cout << "*** " << query.str() << std::endl;
	}
	return result;
}
*/
