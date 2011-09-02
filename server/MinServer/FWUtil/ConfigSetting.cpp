#include "ConfigSetting.h"
#include <fstream>
#include <string>

using namespace fwutil;

ConfigSetting::ConfigSetting(std::string fileName) : error(false) {

	//std::map<std::string, std::string> leafEntries;
	//std::map<std::string, ConfigSetting> sectionEntries;

	std::string line;
	std::string sectionName = "";
	std::ifstream cfgFile(fileName.c_str());

	if (cfgFile.is_open()) {
		while (!cfgFile.eof() ) {
			getline (cfgFile,line);
			trimSpaces(line);
			if (line.length() >= 3 && line.at(0) != '#') {
			    // strip off trailing carriage return (happens for windows file on Linux)
			    if (line.at(line.length()-1) == '\r') {
                    line = line.substr(0, line.length() - 1);
                }
				if (line.at(0) == '[' && line.at(line.length()-1) == ']') {
					sectionName	= line.substr(1, line.length() - 2);
					trimSpaces(sectionName);
				} else {
					// found a leaf entry
					if (sectionName.empty()) {
						std::cout << "Warning: entry without section name found: " << line << std::endl;
					}
					readAndInsertEntryFromLine(sectionName, line);
				}
			}
			//std::cout << line << std::endl;
		}
		cfgFile.close();
	} else {
		error = true;
	}
}


bool ConfigSetting::readAndInsertEntryFromLine(const std::string& sectionName, const std::string& line) {
	// split into assignment parts
	std::string::size_type splitIndex = line.find_first_of("=");
	if (splitIndex == std::string::npos || splitIndex == 0 || splitIndex >= line.length()-1) {
		std::cout << "Invalid config entry assignment: " << line << std::endl;
		return false;
	}

	std::string leftHandPart = line.substr(0,splitIndex);
	std::string entryValue = line.substr(splitIndex+1);

	trimSpaces(leftHandPart);
	trimSpaces(entryValue);

	// now, inside the left hand part read out the type and entry name
	std::string::size_type leftSplitIndex = leftHandPart.find_first_of(" \t");

	// check invalid length

	if (leftSplitIndex >= leftHandPart.length()-1) {
		std::cout << "--> " << leftHandPart.length() << " vs " << leftSplitIndex << std::endl;
	}

	if (leftSplitIndex == std::string::npos || leftSplitIndex == 0 || leftSplitIndex >= leftHandPart.length()-1) {
		std::cout << "Invalid left-hand part of entry assignment, required format is <TYPE> <ENTRYNAME> = <VALUE>" << std::endl << "Are you missing the type?: " << line << std::endl;
		//std::cout
		return false;
	}

	std::string type = leftHandPart.substr(0,leftSplitIndex);
	std::string entryName = leftHandPart.substr(leftSplitIndex+1,splitIndex);
	trimSpaces(type);
	trimSpaces(entryName);

	ConfigSettingValue csv(entryValue, type);
	entries.insert(std::pair< std::string, ConfigSettingValue >(sectionName + "." + entryName, csv));
	return true;
}


ConfigSetting::~ConfigSetting() {
}

int ConfigSetting::getEntryValueInt(const std::string& key, int defaultValue /*=0*/) {
	std::map<std::string, ConfigSettingValue>::iterator it = entries.find(key);
	if (it != entries.end()) {
		return ((*it).second).intValue;
	}
	return defaultValue;
}

std::string ConfigSetting::getEntryValueString(const std::string& key, const std::string& defaultValue /*=""*/) {
	std::map<std::string, ConfigSettingValue>::iterator it = entries.find(key);
	if (it != entries.end()) {
		return ((*it).second).stringValue;
	}
	return defaultValue;
}

bool ConfigSetting::getEntryValueBool(const std::string& key, bool defaultValue /*= false*/) {
	std::map<std::string, ConfigSettingValue>::iterator it = entries.find(key);
	if (it != entries.end()) {
		return ((*it).second).boolValue;
	}
	return defaultValue;
}


/*
std::string ConfigSetting::getEntry(std::string key) {
	std::map<std::string, std::string>::iterator it = entries.find(key);
	if (it != entries.end()) {
		return (*it).second;
	}
	return "";
}*/


void ConfigSetting::trimSpaces( std::string& str) {
	// Trim Both leading and trailing spaces
	size_t startpos = str.find_first_not_of(" \t"); // Find the first character position after excluding leading blank spaces
	size_t endpos = str.find_last_not_of(" \t"); // Find the first character position from reverse

	// if all spaces or empty return an empty string
	if(( std::string::npos == startpos ) || ( std::string::npos == endpos)) {
		str = "";
	} else {
		str = str.substr( startpos, endpos-startpos+1 );
	}
}
