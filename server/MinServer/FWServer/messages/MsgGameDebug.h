#ifndef MsgGameDebug_h__
#define MsgGameDebug_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Client sends a generic debug message to the server (co).
 */

class MsgGameDebug : public min::MinMessage {


	public:
		/** Length of the msg. */
		unsigned int messageLength;
		/** msg. */
		std::string message;


		MsgGameDebug(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_DEBUG;
			_msgMinLength = 5;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameDebug(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_DEBUG;
			_msgMinLength = 5;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameDebug(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_DEBUG;
			_msgMinLength = 5;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameDebug() {}

		inline void initDefaultValues() {
			messageLength = 0;
			message = "";
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
			messageLength = min::NetPort::uintFromByte(bytes, 4);
			if(5 + messageLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (messageLength > 96) {
				_msgIsValid = false;
				return false;
			}
			message = min::NetPort::stringFromBytes(bytes, messageLength, 5);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintToByte(messageLength, bytes, 4);
			min::NetPort::stringToBytes(message, bytes, messageLength, 5);
			return true;
		}

		inline bool checkValidateLength() {
			messageLength = (int)message.size();
			if (messageLength > 96) {
				_msgIsValid = false; 
				return false;
			}
			length = _msgMinLength  + messageLength;
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
