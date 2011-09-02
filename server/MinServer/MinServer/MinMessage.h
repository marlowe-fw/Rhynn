#ifndef MinMessage_h__
#define MinMessage_h__

#include "net_basics.h"

namespace min {

	class MinMessage {

		protected:
			bool _msgIsValid;
			unsigned int _msgMinLength;

		public:
			int msgId;
			unsigned int length;

			virtual ~MinMessage() {}

			virtual bool valuesFromBytes(const unsigned char* bytes, bool readLength = true) = 0;
			virtual bool valuesToBytes(unsigned char* bytes, bool validateLength = true) = 0;
			virtual bool checkValidateLength() = 0;

			bool storeToBuffer(unsigned char* buf, bool validateLength = true) {
				if ((validateLength && !checkValidateLength()) || buf == 0) {
					return false;
				}
				return valuesToBytes(buf, false);
			}

			inline bool isValid() {
				return _msgIsValid;
			}

	};

}

#endif // MinMessage_h__
