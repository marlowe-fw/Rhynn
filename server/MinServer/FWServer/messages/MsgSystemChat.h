#ifndef MsgSystemChat_h__
#define MsgSystemChat_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
#include <iostream>
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * System chat message for internal use and testing.
 */

class MsgSystemChat : public min::MinMessage {


	public:
		/** Length of the chat msg. */
		unsigned int textLength;
		/** The chat text itself. */
		std::string chatText;


		MsgSystemChat(bool init = true) {
			msgId = FWSMessageIDs::MSGID_SYSTEM_CHAT;
			_msgMinLength = 5;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgSystemChat(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_SYSTEM_CHAT;
			_msgMinLength = 5;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgSystemChat(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_SYSTEM_CHAT;
			_msgMinLength = 5;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgSystemChat() {}

		inline void initDefaultValues() {
			textLength = 0;
			chatText = "";
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
			textLength = min::NetPort::uintFromByte(bytes, 4);
			if(5 + textLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (textLength > 200) {
				_msgIsValid = false;
				return false;
			}
			chatText = min::NetPort::stringFromBytes(bytes, textLength, 5);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintToByte(textLength, bytes, 4);
			min::NetPort::stringToBytes(chatText, bytes, textLength, 5);
			return true;
		}

		inline bool checkValidateLength() {
			textLength = (int)chatText.size();
			if (textLength > 200) {
				_msgIsValid = false; 
				return false;
			}
			length = _msgMinLength  + textLength;
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
