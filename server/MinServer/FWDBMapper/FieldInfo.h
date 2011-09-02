#ifndef ColumnInfo_h__
#define ColumnInfo_h__

#include <boost/algorithm/string.hpp>

class FieldInfo {

enum FieldType {ct_unknown, ct_uint, ct_int, ct_text, ct_binary, ct_enum, ct_datetime};


public:
	std::string columnName;
	std::string camelizedName;
	std::string capCamelizedName;
	std::string tableName;
	FieldType fieldType;

	FieldInfo(std::string newColumnName, std::string typeString, std::string newTableName) : columnName(newColumnName), tableName(newTableName) {
		camelizedNamesFromNameString(newColumnName);
		columnTypeFromTypeString(typeString);
	}

	std::string generateDeclaration() {
		return generateCPPTypeName() + " " + camelizedName;
	}

	std::string generateCPPTypeName() {
		switch (fieldType) {
			case ct_unknown:
				return "unknwon??";
			case ct_uint:
				return "unsigned int";
			case ct_int:
				return "int";
			case ct_text:
				return "std::string";
			case ct_binary:
				return "fwutil::BinaryDataWrapper";
			case ct_enum:
				return capCamelizedName;
			case ct_datetime:
				return "fwutil::DateTime";

		}
		return "???";
	}

	std::string generateCustomConverison() {
		switch (fieldType) {
			case ct_unknown:			
			case ct_enum:
				return ".str()";
			case ct_binary:
			case ct_datetime:
				return ".strSQL()";
				
		}
		return "";

	}


	std::string generateMacroFunction() {
		return "DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const " + generateCPPTypeName()  + "," + camelizedName + "," + capCamelizedName + "," + tableName + "," + columnName + "," + generateCustomConverison() + ")";
	}

	std::string generateConstructorInit() {
		std::string res = camelizedName + "(";
		switch (fieldType) {
			case ct_unknown:
				res += "?";
				break;
			case ct_text:
				res += "\"\"";
				break;
			case ct_enum:
				res += capCamelizedName + "::?";
				break;
			case ct_datetime:
				res += "";
				break;
			default:
				res += "0";
				break;
		}

		res += ")";
		return res;
	}

	std::string generateSQLInsertFragment() {
		std::string fragment = camelizedName;
		std::string cc = generateCustomConverison();
		if (!cc.empty()) {
			fragment = " mysqlpp::quote << " + fragment + cc;
		}
		return fragment;
	}

	std::string generateSQLUpdateFragment() {
		std::string fragment = camelizedName;
		std::string cc = generateCustomConverison();
		if (!cc.empty()) {
			fragment = " mysqlpp::quote << " + fragment + cc;
		}
		return  "\"" + columnName + " = \" << " + fragment;
	}


	std::string generateSQLLoadFragment() {
		std::string fragment = "row[\"" + columnName + "\"]";
		if (fieldType == ct_datetime) {
			fragment = ".setFromSQL(" + fragment + ").c_str()";
		} 
		else 
		{
			std::string cc = generateCustomConverison();
			if (!cc.empty()) {
				fragment += ".c_str()";
			}
		}
		return camelizedName + " = " + fragment;
	}


private:
	void columnTypeFromTypeString(std::string typeString) {
		if (typeString.find("int") != std::string::npos) {
			if (typeString.find("unsigned") != std::string::npos) {
				fieldType = ct_uint;
			} else {
				fieldType = ct_int;
			}
		} else if (typeString.find("text")!= std::string::npos || typeString.find("char")!= std::string::npos) {
			fieldType = ct_text;
		} else if (typeString.find("blob") != std::string::npos) {
			fieldType = ct_binary;
		} else if (typeString.find("enum") != std::string::npos) {
			fieldType = ct_enum;
		} else if (typeString.find("date") != std::string::npos) {
			fieldType = ct_datetime;
		} else {
			fieldType = ct_unknown;
		}
	}

	void camelizedNamesFromNameString(const std::string nameStr) {
		
		std::vector<std::string> parts;
		boost::split(parts, nameStr, boost::is_any_of("_"));

		camelizedName = capCamelizedName = "";

		for (size_t i=0; i<parts.size(); i++) {
			std::string firstLetter = parts[i].substr(0,1);
			std::string firstLetterCap = boost::to_upper_copy(firstLetter);
			capCamelizedName += firstLetterCap + parts[i].substr(1);
		}
		
		camelizedName = nameStr[0] + capCamelizedName.substr(1);
	}
};


#endif // ColumnInfo_h__