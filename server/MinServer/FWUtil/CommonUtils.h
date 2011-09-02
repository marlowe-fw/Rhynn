#ifndef CommonUtils_h__
#define CommonUtils_h__

#include <stdio.h>  /* defines FILENAME_MAX */
#ifdef WIN32
	#include <direct.h>
	#define GetCurrentDir _getcwd
#else
	#include <unistd.h>
	#define GetCurrentDir getcwd
#endif

#include <vector>
#include <map>
#include <sstream>

namespace fwutil {

class Common {

	public:

		static int stringToInt(const std::string& stringValue) {
			std::stringstream ssStream(stringValue);
			int iReturn;
			ssStream >> iReturn;
			return iReturn;
		}

		static std::string intToString(int iValue) {
			std::stringstream ssStream;
			ssStream << iValue;
			return ssStream.str();
		}

		static void padString(std::string &str, const size_t num, const char paddingChar = ' ') {
			if(num > str.size()) {
				str.insert(0, num - str.size(), paddingChar);
			}
		}

		static void tokenize(const std::string& str, std::vector<std::string>& tokens, const std::string& delimiters = " ") {
			// Skip delimiters at beginning.
			std::string::size_type lastPos = str.find_first_not_of(delimiters, 0);
			// Find first "non-delimiter".
			std::string::size_type pos     = str.find_first_of(delimiters, lastPos);

			while (std::string::npos != pos || std::string::npos != lastPos)
			{
				// Found a token, add it to the vector.
				tokens.push_back(str.substr(lastPos, pos - lastPos));
				// Skip delimiters.  Note the "not_of"
				lastPos = str.find_first_not_of(delimiters, pos);
				// Find next "non-delimiter"
				pos = str.find_first_of(delimiters, lastPos);
			}
		}



		static const std::string getCWD() {
			char cwd[FILENAME_MAX];
			if (GetCurrentDir(cwd, sizeof(cwd))) {
				return std::string(cwd);
			}
			return "";

			//cwd[sizeof(cCurrentPath) - 1] = '/0'; /* not really required */

			//printf ("The current working directory is %s", cCurrentPath);
		}

	private:
		Common();

};

}

#endif // Common_h__
