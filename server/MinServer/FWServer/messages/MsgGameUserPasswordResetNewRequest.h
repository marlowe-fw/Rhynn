#ifndef MsgGameUserPasswordResetNewRequest_h__
#define MsgGameUserPasswordResetNewRequest_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * User is setting a new password for his account using the reset code that he has been sent via e-mail (co).
 */

class MsgGameUserPasswordResetNewRequest : public min::MinMessage {


	public:
		/** Length of the user name that is sent. */
		unsigned int nameLength;
		/** The user name identifying this account. */
		std::string name;
		/** Length of the reset-code. */
		unsigned int codeLength;
		/** The reset code to verify this as a valid reset. */
		std::string resetCode;
		/** Length of the new password. */
		unsigned int passwordLength;
		/** The new password to set. */
		std::string password;


		MsgGameUserPasswordResetNewRequest(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_PASSWORD_RESET_NEW_REQUEST;
			_msgMinLength = 7;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameUserPasswordResetNewRequest(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_PASSWORD_RESET_NEW_REQUEST;
			_msgMinLength = 7;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameUserPasswordResetNewRequest(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_PASSWORD_RESET_NEW_REQUEST;
			_msgMinLength = 7;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameUserPasswordResetNewRequest() {}

		inline void initDefaultValues() {
			nameLength = 0;
			name = "";
			codeLength = 0;
			resetCode = "";
			passwordLength = 0;
			password = "";
		}

		bool valuesFromBytes(const unsigned char* bytes, bool readLength = true) {
			if (readLength) {
				if (length < _msgMinLength) {
					_msgIsValid = false;
					return false;
				}
			}
			//msgId = min::NetPort::uintFrom3Bytes(bytes, 1);
			nameLength = min::NetPort::uintFromByte(bytes, 4);
			if(5 + nameLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (nameLength > 12) {
				_msgIsValid = false;
				return false;
			}
			name = min::NetPort::stringFromBytes(bytes, nameLength, 5);
			unsigned int curIndex = 5 + nameLength;
			if(curIndex + 1 > length) {
				_msgIsValid = false;
				return false;
			}
			codeLength = min::NetPort::uintFromByte(bytes, curIndex);
			curIndex += 1;
			if(curIndex + codeLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (codeLength > 6) {
				_msgIsValid = false;
				return false;
			}
			resetCode = min::NetPort::stringFromBytes(bytes, codeLength, curIndex);
			curIndex += codeLength;
			if(curIndex + 1 > length) {
				_msgIsValid = false;
				return false;
			}
			passwordLength = min::NetPort::uintFromByte(bytes, curIndex);
			curIndex += 1;
			if(curIndex + passwordLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (passwordLength > 12) {
				_msgIsValid = false;
				return false;
			}
			password = min::NetPort::stringFromBytes(bytes, passwordLength, curIndex);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintToByte(nameLength, bytes, 4);
			min::NetPort::stringToBytes(name, bytes, nameLength, 5);
			unsigned int curIndex = 5 + nameLength;
			min::NetPort::uintToByte(codeLength, bytes, curIndex);
			curIndex += 1;
			min::NetPort::stringToBytes(resetCode, bytes, codeLength, curIndex);
			curIndex += codeLength;
			min::NetPort::uintToByte(passwordLength, bytes, curIndex);
			curIndex += 1;
			min::NetPort::stringToBytes(password, bytes, passwordLength, curIndex);
			return true;
		}

		inline bool checkValidateLength() {
			nameLength = (int)name.size();
			if (nameLength > 12) {
				_msgIsValid = false; 
				return false;
			}
			codeLength = (int)resetCode.size();
			if (codeLength > 6) {
				_msgIsValid = false; 
				return false;
			}
			passwordLength = (int)password.size();
			if (passwordLength > 12) {
				_msgIsValid = false; 
				return false;
			}
			length = _msgMinLength  + nameLength + codeLength + passwordLength;
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
