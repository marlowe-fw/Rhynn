#ifndef MsgGameUserCharacterCreateResult_h__
#define MsgGameUserCharacterCreateResult_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Response to client request for new character creation (so).
 */

class MsgGameUserCharacterCreateResult : public min::MinMessage {


	public:
		/** 1 on success, 0 on failure. */
		unsigned int success;
		/** Length of the info message. */
		unsigned int infoLength;
		/** Message containing some description for success / failure. */
		std::string infoMessage;


		MsgGameUserCharacterCreateResult(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHARACTER_CREATE_RESULT;
			_msgMinLength = 6;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameUserCharacterCreateResult(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHARACTER_CREATE_RESULT;
			_msgMinLength = 6;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameUserCharacterCreateResult(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHARACTER_CREATE_RESULT;
			_msgMinLength = 6;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameUserCharacterCreateResult() {}

		inline void initDefaultValues() {
			success = 0;
			infoLength = 0;
			infoMessage = "";
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
			success = min::NetPort::uintFromByte(bytes, 4);
			infoLength = min::NetPort::uintFromByte(bytes, 5);
			if(6 + infoLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (infoLength > 116) {
				_msgIsValid = false;
				return false;
			}
			infoMessage = min::NetPort::stringFromBytes(bytes, infoLength, 6);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintToByte(success, bytes, 4);
			min::NetPort::uintToByte(infoLength, bytes, 5);
			min::NetPort::stringToBytes(infoMessage, bytes, infoLength, 6);
			return true;
		}

		inline bool checkValidateLength() {
			infoLength = (int)infoMessage.size();
			if (infoLength > 116) {
				_msgIsValid = false; 
				return false;
			}
			length = _msgMinLength  + infoLength;
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
