#ifndef MsgGameCharacterHitInfo_h__
#define MsgGameCharacterHitInfo_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Server informs clients in range that a character has been hit (so).
 */

class MsgGameCharacterHitInfo : public min::MinMessage {


	public:
		/** -. */
		unsigned int attackerId;
		/** -. */
		unsigned int targetId;
		/** -. */
		unsigned int hitValue;
		/** -. */
		unsigned int curHealth;


		MsgGameCharacterHitInfo(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_HIT_INFO;
			_msgMinLength = 16;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameCharacterHitInfo(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_HIT_INFO;
			_msgMinLength = 16;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameCharacterHitInfo(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_HIT_INFO;
			_msgMinLength = 16;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameCharacterHitInfo() {}

		inline void initDefaultValues() {
			attackerId = 0;
			targetId = 0;
			hitValue = 0;
			curHealth = 0;
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
			attackerId = min::NetPort::uintFrom4Bytes(bytes, 4);
			targetId = min::NetPort::uintFrom4Bytes(bytes, 8);
			hitValue = min::NetPort::uintFrom2Bytes(bytes, 12);
			curHealth = min::NetPort::uintFrom2Bytes(bytes, 14);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo4Bytes(attackerId, bytes, 4);
			min::NetPort::uintTo4Bytes(targetId, bytes, 8);
			min::NetPort::uintTo2Bytes(hitValue, bytes, 12);
			min::NetPort::uintTo2Bytes(curHealth, bytes, 14);
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
