#ifndef MsgGameUserLoginResult_h__
#define MsgGameUserLoginResult_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"
#include "BinaryDataWrapper.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Response sent back after a login attempt, success or failure (so).
 */

class MsgGameUserLoginResult : public min::MinMessage {


	public:
		/** Success (1) or failure (0). */
		unsigned int success;
		/** Id of the user as found in the DB. */
		unsigned int userId;
		/** The counter value to use for message encryption on the client from here onwards, this counter is increased with every message sent and is used for the encryption algorithm.. */
		unsigned int counterValue;
		/** Encrypted challenge number client will have to re-encrypt and send back to verify as a valid client. */
		fwutil::BinaryDataWrapper challengeNumberData;
		/** Length of the info message. */
		unsigned int infoLength;
		/** Info message detailing the success status for, so the client can make sense of it. */
		std::string infoMessage;


		MsgGameUserLoginResult(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_LOGIN_RESULT;
			_msgMinLength = 22;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameUserLoginResult(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_LOGIN_RESULT;
			_msgMinLength = 22;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameUserLoginResult(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_LOGIN_RESULT;
			_msgMinLength = 22;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameUserLoginResult() {}

		inline void initDefaultValues() {
			success = 0;
			userId = 0;
			counterValue = 0;
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
			userId = min::NetPort::uintFrom4Bytes(bytes, 5);
			counterValue = min::NetPort::uintFrom4Bytes(bytes, 9);
			challengeNumberData = fwutil::BinaryDataWrapper(bytes, 8, 13);
			infoLength = min::NetPort::uintFromByte(bytes, 21);
			if(22 + infoLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (infoLength > 96) {
				_msgIsValid = false;
				return false;
			}
			infoMessage = min::NetPort::stringFromBytes(bytes, infoLength, 22);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintToByte(success, bytes, 4);
			min::NetPort::uintTo4Bytes(userId, bytes, 5);
			min::NetPort::uintTo4Bytes(counterValue, bytes, 9);
			challengeNumberData.copyToBuffer(bytes, 13);
			min::NetPort::uintToByte(infoLength, bytes, 21);
			min::NetPort::stringToBytes(infoMessage, bytes, infoLength, 22);
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
