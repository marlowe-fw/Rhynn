#ifndef Security_h__
#define Security_h__

#include "Random.h"
#include <string>


namespace fwutil {

class Security {

	private:
		static const int simpleCryptKey = 44217;

	public:

		/** 
		 * Legacy function ported from the old Java server used for very simple encryption (obfuscation).
		 */
		static void simpleCrypt(unsigned char* text, unsigned int length) {
			for(unsigned int i = 0; i<length; i++) {
				text[i] = ((text[i])^(simpleCryptKey>>8));
				i++;
				if(i < length) {
					text[i] = ((text[i])^(simpleCryptKey));
				}
			}
		}

		/**
		 * Legacy function ported from the old Java server used create a challenge number (a string sequence) which the client must decrypt later and send back a response.
		 */
		static std::string createChallengeNumber(unsigned int size) {
			std::string number = "";
			for(unsigned int i = 0; i < size; i++) {
				number += (char)('a' + Random::nextInt(25));
			}
			return number;
		}



};


} // end namespace fwutil

#endif // Security_h__