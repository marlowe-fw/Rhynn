#include "Log.h"
#include "CommonUtils.h"

using namespace fwutil;

const std::string Log::logLevelNames[] = {"INFO", "DEBUG", "WARNING", "ERROR"};
const int Log::Info = 0;
const int Log::Debug = 1;
const int Log::Warning = 2;
const int Log::Error = 3;


Log::Log(const std::string& filename) : filterLogLevel(Log::Info) {
	logFile.open(filename.c_str());
}

Log::~Log() {
	if (logFile.is_open()) {
		logFile.close();
	}
}

void Log::write(const std::string& msg) {
	if (logFile.is_open()) {
		logFile << msg;
		logFile.flush();
	}
}

void Log::writeln(const std::string& msg) {
	if (logFile.is_open()) {
		logFile << msg << "\n";
		logFile.flush();
	}
}


void Log::log(const std::string& msg, int level) {
	if (level >= filterLogLevel) {
		curDateTime.setCurrent();
		writeln("[" + curDateTime.strSQL() + "] " + logLevelNames[level] + ": " + msg);
	}
}
