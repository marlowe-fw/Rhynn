#ifndef MsgGameCharacterChatAllInfo_h__
#define MsgGameCharacterChatAllInfo_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Server broadcasts a public chat message to all clients in range (so).
 */

class MsgGameCharacterChatAllInfo : public min::MinMessage {


	public:
		/** Id of the character sending the chat message. */
		unsigned int objectId;
		/** -. */
		unsigned int chatMsgLength;
		/** -. */
		std::string chatMsg;


		MsgGameCharacterChatAllInfo(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_CHAT_ALL_INFO;
			_msgMinLength = 9;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameCharacterChatAllInfo(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_CHAT_ALL_INFO;
			_msgMinLength = 9;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameCharacterChatAllInfo(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_CHAT_ALL_INFO;
			_msgMinLength = 9;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameCharacterChatAllInfo() {}

		inline void initDefaultValues() {
			objectId = 0;
			chatMsgLength = 0;
			chatMsg = "";
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
			chatMsgLength = min::NetPort::uintFromByte(bytes, 8);
			if(9 + chatMsgLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (chatMsgLength > 24) {
				_msgIsValid = false;
				return false;
			}
			chatMsg = min::NetPort::stringFromBytes(bytes, chatMsgLength, 9);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo4Bytes(objectId, bytes, 4);
			min::NetPort::uintToByte(chatMsgLength, bytes, 8);
			min::NetPort::stringToBytes(chatMsg, bytes, chatMsgLength, 9);
			return true;
		}

		inline bool checkValidateLength() {
			chatMsgLength = (int)chatMsg.size();
			if (chatMsgLength > 24) {
				_msgIsValid = false; 
				return false;
			}
			length = _msgMinLength  + chatMsgLength;
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
