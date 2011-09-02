#ifndef MsgGameCharacterChatAllRequest_h__
#define MsgGameCharacterChatAllRequest_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Client sends request to deliver a public chat message to all in range (co).
 */

class MsgGameCharacterChatAllRequest : public min::MinMessage {


	public:
		/** -. */
		unsigned int chatMsgLength;
		/** -. */
		std::string chatMsg;


		MsgGameCharacterChatAllRequest(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_CHAT_ALL_REQUEST;
			_msgMinLength = 5;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameCharacterChatAllRequest(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_CHAT_ALL_REQUEST;
			_msgMinLength = 5;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameCharacterChatAllRequest(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_CHAT_ALL_REQUEST;
			_msgMinLength = 5;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameCharacterChatAllRequest() {}

		inline void initDefaultValues() {
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
			chatMsgLength = min::NetPort::uintFromByte(bytes, 4);
			if(5 + chatMsgLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (chatMsgLength > 24) {
				_msgIsValid = false;
				return false;
			}
			chatMsg = min::NetPort::stringFromBytes(bytes, chatMsgLength, 5);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintToByte(chatMsgLength, bytes, 4);
			min::NetPort::stringToBytes(chatMsg, bytes, chatMsgLength, 5);
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
