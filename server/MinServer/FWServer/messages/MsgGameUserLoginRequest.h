#ifndef MsgGameUserLoginRequest_h__
#define MsgGameUserLoginRequest_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Client requests  login (co).
 */

class MsgGameUserLoginRequest : public min::MinMessage {


	public:
		/** Length of the user name. */
		unsigned int userNameLength;
		/** The provided user name. */
		std::string userName;
		/** Length of the password. */
		unsigned int userPasswordLength;
		/** The provided password. */
		std::string userPassword;


		MsgGameUserLoginRequest(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_LOGIN_REQUEST;
			_msgMinLength = 6;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameUserLoginRequest(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_LOGIN_REQUEST;
			_msgMinLength = 6;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameUserLoginRequest(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_LOGIN_REQUEST;
			_msgMinLength = 6;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameUserLoginRequest() {}

		inline void initDefaultValues() {
			userNameLength = 0;
			userName = "";
			userPasswordLength = 0;
			userPassword = "";
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
			userNameLength = min::NetPort::uintFromByte(bytes, 4);
			if(5 + userNameLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (userNameLength > 10) {
				_msgIsValid = false;
				return false;
			}
			userName = min::NetPort::stringFromBytes(bytes, userNameLength, 5);
			unsigned int curIndex = 5 + userNameLength;
			if(curIndex + 1 > length) {
				_msgIsValid = false;
				return false;
			}
			userPasswordLength = min::NetPort::uintFromByte(bytes, curIndex);
			curIndex += 1;
			if(curIndex + userPasswordLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (userPasswordLength > 10) {
				_msgIsValid = false;
				return false;
			}
			userPassword = min::NetPort::stringFromBytes(bytes, userPasswordLength, curIndex);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintToByte(userNameLength, bytes, 4);
			min::NetPort::stringToBytes(userName, bytes, userNameLength, 5);
			unsigned int curIndex = 5 + userNameLength;
			min::NetPort::uintToByte(userPasswordLength, bytes, curIndex);
			curIndex += 1;
			min::NetPort::stringToBytes(userPassword, bytes, userPasswordLength, curIndex);
			return true;
		}

		inline bool checkValidateLength() {
			userNameLength = (int)userName.size();
			if (userNameLength > 10) {
				_msgIsValid = false; 
				return false;
			}
			userPasswordLength = (int)userPassword.size();
			if (userPasswordLength > 10) {
				_msgIsValid = false; 
				return false;
			}
			length = _msgMinLength  + userNameLength + userPasswordLength;
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
