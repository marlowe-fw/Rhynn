#ifndef MsgGameUserCharacterCreateRequest_h__
#define MsgGameUserCharacterCreateRequest_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Client requests to create a new character (co). Note that the number of characters allowed differs for premium users and non-premium users.
 */

class MsgGameUserCharacterCreateRequest : public min::MinMessage {


	public:
		/** -. */
		unsigned int classId;
		/** Length of the character name. */
		unsigned int nameLength;
		/** Name of the new character. */
		std::string name;


		MsgGameUserCharacterCreateRequest(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHARACTER_CREATE_REQUEST;
			_msgMinLength = 9;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameUserCharacterCreateRequest(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHARACTER_CREATE_REQUEST;
			_msgMinLength = 9;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameUserCharacterCreateRequest(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHARACTER_CREATE_REQUEST;
			_msgMinLength = 9;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameUserCharacterCreateRequest() {}

		inline void initDefaultValues() {
			classId = 0;
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
			classId = min::NetPort::uintFrom4Bytes(bytes, 4);
			nameLength = min::NetPort::uintFromByte(bytes, 8);
			if(9 + nameLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (nameLength > 12) {
				_msgIsValid = false;
				return false;
			}
			name = min::NetPort::stringFromBytes(bytes, nameLength, 9);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo4Bytes(classId, bytes, 4);
			min::NetPort::uintToByte(nameLength, bytes, 8);
			min::NetPort::stringToBytes(name, bytes, nameLength, 9);
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
