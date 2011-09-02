#ifndef MsgGameUserCharacterDeleteRequest_h__
#define MsgGameUserCharacterDeleteRequest_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Client requests to delete the given character (co). Note that deleting will merely set the status flag to deleted, such that the user cannot use this character anymore but it remains in the database.
 */

class MsgGameUserCharacterDeleteRequest : public min::MinMessage {


	public:
		/** Id of the character which should be deleted, server will check if this is a valid charater fo the given user. */
		unsigned int characterId;


		MsgGameUserCharacterDeleteRequest(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHARACTER_DELETE_REQUEST;
			_msgMinLength = 8;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameUserCharacterDeleteRequest(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHARACTER_DELETE_REQUEST;
			_msgMinLength = 8;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameUserCharacterDeleteRequest(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHARACTER_DELETE_REQUEST;
			_msgMinLength = 8;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameUserCharacterDeleteRequest() {}

		inline void initDefaultValues() {
			characterId = 0;
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
			characterId = min::NetPort::uintFrom4Bytes(bytes, 4);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo4Bytes(characterId, bytes, 4);
			return true;
		}

		inline bool checkValidateLength() {
			length = _msgMinLength ;
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
