#include "FWMapperApp.h"
#include "DBHelper.h"

using namespace fwmapper;

FWMapperApp::FWMapperApp(AppMainWindow* guiParent) : 
QObject(guiParent), appMainWindow(guiParent), lastDBError("No error."),
conn(0)
{
	init();
}

FWMapperApp::~FWMapperApp() {
	clearConnection();
}


bool FWMapperApp::init() {
	return true;
}


void FWMapperApp::onUIReady() {
	connectUI();
}

void FWMapperApp::connectUI() {

}

std::string FWMapperApp::getLastDBError() {
	return lastDBError;
}

void FWMapperApp::clearConnection() {
	if (conn!= 0) {
		if (conn->connected()) { 
			conn->disconnect();
		}
		delete conn;
		conn = 0;
	}
}

bool FWMapperApp::connectToDB(const std::string& server, const std::string& user, const std::string& pass, const std::string& schema) {
	bool success = false;
	
	clearConnection();

	std::cout << "connecting to the database .. " << std::endl;
	try {
		conn = new mysqlpp::Connection(schema.c_str(), server.c_str(), user.c_str(), pass.c_str(), 3306);
		std::cout << "OK" << std::endl;
		getSchemaTables();
		success= true;
	} catch (const mysqlpp::Exception& er) {
		lastDBError = er.what();
		std::cout << "FAILED" << std::endl;
		std::cout << "*** MySQL Error: " << lastDBError << std::endl;
		
	}

	return success;
}

std::vector<std::string> FWMapperApp::getSchemaTables() {
	std::vector<std::string> tableNames;
	
	mysqlpp::Query query = conn->query("SHOW TABLES;");
	mysqlpp::StoreQueryResult res;
	//std::cout << "load, num: " << res.num_rows() << std::endl;

	fwutil::DBHelper::select(query, &res, "get schema tables");

	if (res && res.num_rows() > 0) {
		size_t numRows = res.num_rows();
		for (size_t i=0; i<numRows; i++) {
			tableNames.push_back(res[i][0].c_str());
		}
	}

	emit schemaTablesChanged(tableNames);

	return tableNames;
}

std::string FWMapperApp::createMap(std::string table, std::string className) {
	std::string sClass = "";
	std::string sImpl = "";
	std::string memberDeclarations = "";
	std::string macroFunctions = "";
	std::string sConstructorInit = "";
	std::string sInsert1 = "";
	std::string sInsert2 = "";
	std::string sUpdate = "";
	std::string sDelete = "";
	std::string sLoad = "";
	columnInfo.clear();

	mysqlpp::Query query = conn->query("DESCRIBE " + table);
	mysqlpp::StoreQueryResult res;

	fwutil::DBHelper::select(query, &res, "create map");

	if (res && res.num_rows() > 0) {
		size_t numRows = res.num_rows();
		for (size_t i=0; i<numRows; i++) {
			FieldInfo ci(res[i][0].c_str(), res[i][1].c_str(), table);
			columnInfo.push_back(ci);
			std::cout << "got column: " << res[i][0].c_str() << "type: " << res[i][1].c_str();
		}
	}

	

	for(std::vector<FieldInfo>::iterator it = columnInfo.begin(); it != columnInfo.end(); it++) {
		if (!sInsert1.empty()) {sInsert1 += ", \\";}		
		sInsert1 += "\n\t\t" + (*it).columnName;
		if (!sInsert2.empty()) {sInsert2 += " << \",\"";}
		sInsert2 += "\n\t\t<< " + (*it).generateSQLInsertFragment();
		if ((*it).columnName != "id") {
			memberDeclarations += "\t" + (*it).generateDeclaration() + ";\n";
			macroFunctions += "\t" + (*it).generateMacroFunction() + "\n";
			if (!sUpdate.empty()) {sUpdate += " << \",\"";}
			sUpdate += "\n\t\t<< " + (*it).generateSQLUpdateFragment();
			if (!sConstructorInit.empty()) {sConstructorInit += ",\n";}
			sConstructorInit += (*it).generateConstructorInit();
		}
		sLoad += "\t" + (*it).generateSQLLoadFragment() + ";\n";
	}

	sInsert1 = "\t\tquery << \"insert into " + table + "( \\" + sInsert1 + " \\\n\t\t) values ( \"" + sInsert2 + "\n\t\t << \")\";";
	sUpdate = "\t\tquery << \"update "+table+" set \""+sUpdate+"\n\t\t<< \" where id = \" << id;";

	sClass =  macroFunctions + "\n\n" + "private:\n\n" + memberDeclarations + "\n\n";
	std::string classStart =  "#ifndef "+className+"_h__\n";
	classStart += "#define "+className+"_h__\n";
	classStart += "\n";
	classStart += "#include \"DBSynchObject.h\"\n";
	classStart += "#include \"DBSynchObjectMacros.h\"\n";
	classStart += "#include \"WorldObjectTypeDefs.h\"\n";
	classStart += "#include \"DBHelper.h\"\n";
	classStart += "#include \"DateTime.h\"\n";
	classStart += "#include \"GenericClock.h\"\n";
	classStart += "\n";
	classStart += "namespace fwworld {\n";
	classStart += "\n";
	classStart += "class FWWorld;\n";
	classStart += "\n";
	classStart += "class " + className + " : public DBSynchObject {\n";
	classStart += "\n";
	classStart += "public:\n";
	classStart += "\t"+className+"(FWWorld& world);\n";
	classStart += "\tvirtual ~"+className+"();\n";
	classStart += "\n";
	classStart += "\tvirtual bool storeToDB();\n";
	classStart += "\tvirtual bool loadFromDB(unsigned int existingId);\n";
	classStart += "\tvirtual bool removeFromDB();\n\n";
	classStart += "\tbool loadFromResultRow(const mysqlpp::Row& row);\n\n";


	std::string classEnd = "\n};\n\n}\n\n#endif // Item_h__\n";


	sClass = classStart + sClass + classEnd;


	sImpl += "#include \""+className+".h\"\n";
	sImpl += "#include \"../FWWorld.h\"\n";
	sImpl += "#include \"DBHelper.h\"\n";
	sImpl += "\n";
	sImpl += "using namespace fwworld;\n\n";
	sImpl += "/**\n";
	sImpl += "* Create a new object and initialize it to default values.\n";
	sImpl += "* @param world The world this object belongs to (and therefore has access to)\n";
	sImpl += "*/\n";
	sImpl += ""+className+"::"+className+"(FWWorld& world) : DBSynchObject(world),\n";
	sImpl += "" + sConstructorInit + "\n";
	sImpl += "{\n";
	sImpl += "objectTypeId = WorldObject::ot"+className+";\n";
	sImpl += "}\n";
	sImpl += "\n";
	sImpl += "/** Destruction of object. */\n";
	sImpl += "Item::~Item() {\n";
	sImpl += "}\n\n";


	sImpl += "/**\n";
	sImpl += "* Save this object to the DB.\n";
	sImpl += "* @return true on success, false on failure.\n";
	sImpl += "*/\n";
	sImpl += "bool "+className+"::storeToDB() {\n";
	sImpl += "\tmysqlpp::Query query = world.conn.query();\n\n";
	sImpl += "\tif (id == 0) {\n";
	sImpl += sInsert1 + "\n";
	sImpl += "\t} else {\n" + sUpdate+ "\n\t}\n\n"; 
	sImpl += "\treturn storeByQuery(query, \""+table+" store\");\n}\n\n";


	sImpl += "/**\n";
	sImpl += "* Populate the object values by loading them from the DB.\n";
	sImpl += "* @param existingId The database id of the object which identifies the associated DB record.\n";
	sImpl += "* @return true on success, false otherwise\n";
	sImpl += "*/\n";
	sImpl += "bool "+className+"::loadFromDB(unsigned int existingId) {\n";
	sImpl += "\tmysqlpp::Query query = world.conn.query();\n";
	sImpl += "\tquery << \"select * from "+table+" where id = \" << existingId;\n\n";
	sImpl += "\tmysqlpp::StoreQueryResult res;\n";
	sImpl += "\tfwutil::DBHelper::select(query, &res, \""+table+" load\");\n";
	sImpl += "\tif (res && res.num_rows() > 0) {\n";
	sImpl += "\t\t\treturn (loadFromResultRow(res[0]));\n";
	sImpl += "\t}\n";
	sImpl += "\treturn false;\n}\n\n";

	sImpl += "/**\n";
	sImpl += "* A helper function, retrieve object values from a mysqlpp result row.\n";
	sImpl += "* @param row The result row to read the value from\n";
	sImpl += "* @return true on success, false otherwise\n";
	sImpl += "*/\n";
	sImpl += "bool "+className+"::loadFromResultRow(const mysqlpp::Row& row) {\n";
	sImpl += sLoad;
	sImpl += "\tonSuccessfulLoad();\n";
	sImpl += "\treturn true;\n";
	sImpl += "}\n\n";

	sImpl += "/**\n";
	sImpl += "* Remove the DB record which is associated with this object.\n";
	sImpl += "* @return true on success, false otherwise\n";
	sImpl += "*/\n";
	sImpl += "bool "+className+"::removeFromDB() {\n";
	sImpl += "\tif (id > 0) {\n";
	sImpl += "\t\tmysqlpp::Query query = world.conn.query();\n";
	sImpl += "\t\tquery << \"delete from "+table+" where id = \" << mysqlpp::quote << id;\n";
	sImpl += "\t\treturn removeByQuery(query, \"remove from "+table+"\");\n";
	sImpl += "\t}\n\n";
	sImpl += "\treturn false;\n";
	sImpl += "}\n\n";


	//"\n\n" + sDelete+ "\n\n" + sLoad;
	
	emit mappingCreated(sClass + "\n\n\n\n" +  sImpl);
	return sClass;
}

