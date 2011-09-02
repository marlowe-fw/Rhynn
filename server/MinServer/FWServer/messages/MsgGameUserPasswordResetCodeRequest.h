#ifndef MsgGameUserPasswordResetCodeRequest_h__
#define MsgGameUserPasswordResetCodeRequest_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * User is requesting that a reset code be sent to his e-mail address, provided the user has entered a correct e-mail address for his account (co).
 */

class MsgGameUserPasswordResetCodeRequest : public min::MinMessage {


	public:
		/** Length of the user name that is sent. */
		unsigned int nameLength;
		/** The user name identifying this account. */
		std::string name;


		MsgGameUserPasswordResetCodeRequest(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_PASSWORD_RESET_CODE_REQUEST;
			_msgMinLength = 5;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameUserPasswordResetCodeRequest(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_PASSWORD_RESET_CODE_REQUEST;
			_msgMinLength = 5;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameUserPasswordResetCodeRequest(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_PASSWORD_RESET_CODE_REQUEST;
			_msgMinLength = 5;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameUserPasswordResetCodeRequest() {}

		inline void initDefaultValues() {
			nameLength = 0;
			name = "";
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
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintToByte(nameLength, bytes, 4);
			min::NetPort::stringToBytes(name, bytes, nameLength, 5);
			return true;
		}

		inline bool checkValidateLength() {
			nameLength = (int)name.size();
			if (nameLength > 12) {
				_msgIsValid = false; 
				return false;
			}
			length = _msgMinLength  + nameLength;
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
