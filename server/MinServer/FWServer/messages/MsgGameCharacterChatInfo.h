#ifndef MsgGameCharacterChatInfo_h__
#define MsgGameCharacterChatInfo_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Server sends chat message to client (so).
 */

class MsgGameCharacterChatInfo : public min::MinMessage {


	public:
		/** -. */
		unsigned int senderId;
		/** -. */
		unsigned int receiverId;
		/** -. */
		unsigned int senderNameLength;
		/** -. */
		std::string senderName;
		/** -. */
		unsigned int chatMsgLength;
		/** -. */
		std::string chatMsg;


		MsgGameCharacterChatInfo(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_CHAT_INFO;
			_msgMinLength = 14;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameCharacterChatInfo(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_CHAT_INFO;
			_msgMinLength = 14;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameCharacterChatInfo(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_CHAT_INFO;
			_msgMinLength = 14;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameCharacterChatInfo() {}

		inline void initDefaultValues() {
			senderId = 0;
			receiverId = 0;
			senderNameLength = 0;
			senderName = "";
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
			senderId = min::NetPort::uintFrom4Bytes(bytes, 4);
			receiverId = min::NetPort::uintFrom4Bytes(bytes, 8);
			senderNameLength = min::NetPort::uintFromByte(bytes, 12);
			if(13 + senderNameLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (senderNameLength > 12) {
				_msgIsValid = false;
				return false;
			}
			senderName = min::NetPort::stringFromBytes(bytes, senderNameLength, 13);
			unsigned int curIndex = 13 + senderNameLength;
			if(curIndex + 1 > length) {
				_msgIsValid = false;
				return false;
			}
			chatMsgLength = min::NetPort::uintFromByte(bytes, curIndex);
			curIndex += 1;
			if(curIndex + chatMsgLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (chatMsgLength > 32) {
				_msgIsValid = false;
				return false;
			}
			chatMsg = min::NetPort::stringFromBytes(bytes, chatMsgLength, curIndex);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo4Bytes(senderId, bytes, 4);
			min::NetPort::uintTo4Bytes(receiverId, bytes, 8);
			min::NetPort::uintToByte(senderNameLength, bytes, 12);
			min::NetPort::stringToBytes(senderName, bytes, senderNameLength, 13);
			unsigned int curIndex = 13 + senderNameLength;
			min::NetPort::uintToByte(chatMsgLength, bytes, curIndex);
			curIndex += 1;
			min::NetPort::stringToBytes(chatMsg, bytes, chatMsgLength, curIndex);
			return true;
		}

		inline bool checkValidateLength() {
			senderNameLength = (int)senderName.size();
			if (senderNameLength > 12) {
				_msgIsValid = false; 
				return false;
			}
			chatMsgLength = (int)chatMsg.size();
			if (chatMsgLength > 32) {
				_msgIsValid = false; 
				return false;
			}
			length = _msgMinLength  + senderNameLength + chatMsgLength;
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
