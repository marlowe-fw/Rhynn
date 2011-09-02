#ifndef ConfigSetting_h__
#define ConfigSetting_h__

#include "CommonUtils.h"
#include <iostream>
#include <map>

namespace fwutil {

class ConfigSetting {
	
	public:
		ConfigSetting() {}
		ConfigSetting(std::string fileName);
		
		virtual ~ConfigSetting();

		//std::string getEntry(const std::string& key);
		int getEntryValueInt(const std::string& key, int defaultValue = 0);
		std::string getEntryValueString(const std::string& key, const std::string& defaultValue = "");
		bool getEntryValueBool(const std::string& key, bool defaultValue = false);

		inline bool hasError() {
			return error;
		}

	private:
		// class representing a config value for a config entry
		class ConfigSettingValue {		
			public:
				enum dataType {e_type_string, e_type_int, e_type_bool};

				dataType type;
				std::string stringValue;
				int intValue;
				bool boolValue;

				ConfigSettingValue(const std::string& newValueStr, const std::string& typeStr) {
					if (typeStr.compare("int") == 0) {
						type = e_type_int;
						intValue = Common::stringToInt(newValueStr);
						stringValue = "";
					} else if (typeStr.compare("bool") == 0) {
						if (newValueStr.compare("true") == 0) {
							boolValue = true;
						} else {
							boolValue = false;
						}
						intValue = 0;
						stringValue = "";
					} else {
						type = e_type_string;
						intValue = 0;
						stringValue = newValueStr;
					}
					//value = newValue;
				}
		};


		bool error;
		std::map<std::string, ConfigSettingValue> entries;
		void trimSpaces( std::string& str);
		bool readAndInsertEntryFromLine(const std::string& sectionName, const std::string& line);
		void doInsertEntry(const std::string& sectionName, const std::string& typeName, const std::string& entryName, ConfigSettingValue& value);



};

}

#endif // ConfigSetting_h__