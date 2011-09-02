#ifndef Log_h__
#define Log_h__

#include "DateTime.h"

#include <string>
#include <iostream>
#include <fstream>

namespace fwutil {

class Log {

	public:
		static const int Info;
		static const int Debug;
		static const int Warning;
		static const int Error;

		static const std::string logLevelNames[];

		Log(const std::string& filename);
		virtual ~Log();

		void write(const std::string& msg);
		void writeln(const std::string& msg);
		void log(const std::string& msg, int level);
		inline void setFilterLogLevel(int level) {
			filterLogLevel = level;
		}

	private:
		std::ofstream logFile;
		int filterLogLevel;
		DateTime curDateTime;



};

} // end namespace

#endif // Log_h__