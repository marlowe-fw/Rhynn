#ifndef MsgGameCharacterRespawnResult_h__
#define MsgGameCharacterRespawnResult_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Server informs client whether or not respawning is ok (so).
 */

class MsgGameCharacterRespawnResult : public min::MinMessage {


	public:
		/** Whether or not respawn is ok. */
		unsigned int success;
		/** -. */
		unsigned int respawnX;
		/** -. */
		unsigned int respawnY;
		/** -. */
		unsigned int curHealth;
		/** -. */
		unsigned int curMana;
		/** -. */
		unsigned int infoLength;
		/** Info message to indicate possible errors. */
		std::string infoMessage;


		MsgGameCharacterRespawnResult(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_RESPAWN_RESULT;
			_msgMinLength = 14;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameCharacterRespawnResult(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_RESPAWN_RESULT;
			_msgMinLength = 14;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameCharacterRespawnResult(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_RESPAWN_RESULT;
			_msgMinLength = 14;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameCharacterRespawnResult() {}

		inline void initDefaultValues() {
			success = 0;
			respawnX = 0;
			respawnY = 0;
			curHealth = 0;
			curMana = 0;
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
			respawnX = min::NetPort::uintFrom2Bytes(bytes, 5);
			respawnY = min::NetPort::uintFrom2Bytes(bytes, 7);
			curHealth = min::NetPort::uintFrom2Bytes(bytes, 9);
			curMana = min::NetPort::uintFrom2Bytes(bytes, 11);
			infoLength = min::NetPort::uintFromByte(bytes, 13);
			if(14 + infoLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (infoLength > 96) {
				_msgIsValid = false;
				return false;
			}
			infoMessage = min::NetPort::stringFromBytes(bytes, infoLength, 14);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintToByte(success, bytes, 4);
			min::NetPort::uintTo2Bytes(respawnX, bytes, 5);
			min::NetPort::uintTo2Bytes(respawnY, bytes, 7);
			min::NetPort::uintTo2Bytes(curHealth, bytes, 9);
			min::NetPort::uintTo2Bytes(curMana, bytes, 11);
			min::NetPort::uintToByte(infoLength, bytes, 13);
			min::NetPort::stringToBytes(infoMessage, bytes, infoLength, 14);
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
