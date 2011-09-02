#ifndef MsgGameCharacterMoveInfo_h__
#define MsgGameCharacterMoveInfo_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * This message notifies one or more clients that another character has moved (so).
 */

class MsgGameCharacterMoveInfo : public min::MinMessage {


	public:
		/** -. */
		unsigned int x;
		/** -. */
		unsigned int y;
		/** -. */
		unsigned int direction;
		/** -. */
		unsigned int objectId;


		MsgGameCharacterMoveInfo(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_MOVE_INFO;
			_msgMinLength = 13;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameCharacterMoveInfo(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_MOVE_INFO;
			_msgMinLength = 13;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameCharacterMoveInfo(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_MOVE_INFO;
			_msgMinLength = 13;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameCharacterMoveInfo() {}

		inline void initDefaultValues() {
			x = 0;
			y = 0;
			direction = 0;
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
			x = min::NetPort::uintFrom2Bytes(bytes, 4);
			y = min::NetPort::uintFrom2Bytes(bytes, 6);
			direction = min::NetPort::uintFromByte(bytes, 8);
			objectId = min::NetPort::uintFrom4Bytes(bytes, 9);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo2Bytes(x, bytes, 4);
			min::NetPort::uintTo2Bytes(y, bytes, 6);
			min::NetPort::uintToByte(direction, bytes, 8);
			min::NetPort::uintTo4Bytes(objectId, bytes, 9);
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
