#ifndef MsgGameCharacterRemove_h__
#define MsgGameCharacterRemove_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Server informs a client that a character is not in his interest zone anymore, i.e. invisible and irrelevant (so).
 */

class MsgGameCharacterRemove : public min::MinMessage {


	public:
		/** -. */
		unsigned int objectId;


		MsgGameCharacterRemove(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_REMOVE;
			_msgMinLength = 8;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameCharacterRemove(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_REMOVE;
			_msgMinLength = 8;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameCharacterRemove(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_REMOVE;
			_msgMinLength = 8;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameCharacterRemove() {}

		inline void initDefaultValues() {
			objectId = 0;
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
			objectId = min::NetPort::uintFrom4Bytes(bytes, 4);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo4Bytes(objectId, bytes, 4);
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
