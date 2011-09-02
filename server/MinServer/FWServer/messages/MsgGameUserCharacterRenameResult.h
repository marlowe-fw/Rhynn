#ifndef MsgGameUserCharacterRenameResult_h__
#define MsgGameUserCharacterRenameResult_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Response from server to notify the client whether or not the renaming was successful.
 */

class MsgGameUserCharacterRenameResult : public min::MinMessage {


	public:
		/** -. */
		unsigned int success;
		/** Id the of the character which was renamed or failed to rename. */
		unsigned int characterId;
		/** Lengfth of the info message. */
		unsigned int infoLength;
		/** Info message to indicate possible errors. */
		std::string infoMessage;


		MsgGameUserCharacterRenameResult(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHARACTER_RENAME_RESULT;
			_msgMinLength = 10;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameUserCharacterRenameResult(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHARACTER_RENAME_RESULT;
			_msgMinLength = 10;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameUserCharacterRenameResult(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHARACTER_RENAME_RESULT;
			_msgMinLength = 10;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameUserCharacterRenameResult() {}

		inline void initDefaultValues() {
			success = 0;
			characterId = 0;
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
			characterId = min::NetPort::uintFrom4Bytes(bytes, 5);
			infoLength = min::NetPort::uintFromByte(bytes, 9);
			if(10 + infoLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (infoLength > 96) {
				_msgIsValid = false;
				return false;
			}
			infoMessage = min::NetPort::stringFromBytes(bytes, infoLength, 10);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintToByte(success, bytes, 4);
			min::NetPort::uintTo4Bytes(characterId, bytes, 5);
			min::NetPort::uintToByte(infoLength, bytes, 9);
			min::NetPort::stringToBytes(infoMessage, bytes, infoLength, 10);
			return true;
		}

		inline bool checkValidateLength() {
			infoLength = (int)infoMessage.size();
			if (infoLength > 96) {
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
