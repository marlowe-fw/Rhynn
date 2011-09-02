#ifndef MsgGameCharacterIncreaseVitality_h__
#define MsgGameCharacterIncreaseVitality_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Server informs client that vitality (health / mana) has been refilled by certain amounts such that those vitality values need an update (so).
 */

class MsgGameCharacterIncreaseVitality : public min::MinMessage {


	public:
		/** -. */
		unsigned int curHealth;
		/** -. */
		unsigned int curMana;


		MsgGameCharacterIncreaseVitality(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_INCREASE_VITALITY;
			_msgMinLength = 8;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameCharacterIncreaseVitality(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_INCREASE_VITALITY;
			_msgMinLength = 8;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameCharacterIncreaseVitality(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_INCREASE_VITALITY;
			_msgMinLength = 8;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameCharacterIncreaseVitality() {}

		inline void initDefaultValues() {
			curHealth = 0;
			curMana = 0;
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
			curHealth = min::NetPort::uintFrom2Bytes(bytes, 4);
			curMana = min::NetPort::uintFrom2Bytes(bytes, 6);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo2Bytes(curHealth, bytes, 4);
			min::NetPort::uintTo2Bytes(curMana, bytes, 6);
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
