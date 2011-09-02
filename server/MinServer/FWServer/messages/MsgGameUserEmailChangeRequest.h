#ifndef MsgGameUserEmailChangeRequest_h__
#define MsgGameUserEmailChangeRequest_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Client requests to change the e-mail address (co).
 */

class MsgGameUserEmailChangeRequest : public min::MinMessage {


	public:
		/** Length of the first part of the address. */
		unsigned int firstPartLength;
		/** First part of the e-mail (before the @ sign). */
		std::string firstPart;
		/** Length of the second part of the address. */
		unsigned int secondPartLength;
		/** Second part of the e-mail (after the @ sign). */
		std::string secondPart;


		MsgGameUserEmailChangeRequest(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_EMAIL_CHANGE_REQUEST;
			_msgMinLength = 6;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameUserEmailChangeRequest(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_EMAIL_CHANGE_REQUEST;
			_msgMinLength = 6;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameUserEmailChangeRequest(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_EMAIL_CHANGE_REQUEST;
			_msgMinLength = 6;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameUserEmailChangeRequest() {}

		inline void initDefaultValues() {
			firstPartLength = 0;
			firstPart = "";
			secondPartLength = 0;
			secondPart = "";
		}

		bool valuesFromBytes(const unsigned char* bytes, bool readLength = true) {
			if (readLength) {
				length = min::NetPort::uintFromByte(bytes, 0);
				if (length < _msgMinLength) {
					_msgIsValid = false;
					return false;
				}
			}
			//msgId = min::NetPort::uintFrom3Bytes(bytes, 1);
			firstPartLength = min::NetPort::uintFromByte(bytes, 4);
			if(5 + firstPartLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (firstPartLength > 31) {
				_msgIsValid = false;
				return false;
			}
			firstPart = min::NetPort::stringFromBytes(bytes, firstPartLength, 5);
			unsigned int curIndex = 5 + firstPartLength;
			if(curIndex + 1 > length) {
				_msgIsValid = false;
				return false;
			}
			secondPartLength = min::NetPort::uintFromByte(bytes, curIndex);
			curIndex += 1;
			if(curIndex + secondPartLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (secondPartLength > 32) {
				_msgIsValid = false;
				return false;
			}
			secondPart = min::NetPort::stringFromBytes(bytes, secondPartLength, curIndex);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintToByte(firstPartLength, bytes, 4);
			min::NetPort::stringToBytes(firstPart, bytes, firstPartLength, 5);
			unsigned int curIndex = 5 + firstPartLength;
			min::NetPort::uintToByte(secondPartLength, bytes, curIndex);
			curIndex += 1;
			min::NetPort::stringToBytes(secondPart, bytes, secondPartLength, curIndex);
			return true;
		}

		inline bool checkValidateLength() {
			firstPartLength = (int)firstPart.size();
			if (firstPartLength > 31) {
				_msgIsValid = false; 
				return false;
			}
			secondPartLength = (int)secondPart.size();
			if (secondPartLength > 32) {
				_msgIsValid = false; 
				return false;
			}
			length = _msgMinLength  + firstPartLength + secondPartLength;
			return true;
		}

		inline bool isValid() {
			return _msgIsValid;
		}

	// %%GENERATOR_START%%MSG_IMPL_USER_CONTENT%%
	// %%GENERATOR_END%%MSG_IMPL_USER_CONTENT%%

};

}

#endif
