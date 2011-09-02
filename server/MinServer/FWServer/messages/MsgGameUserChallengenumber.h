#ifndef MsgGameUserChallengenumber_h__
#define MsgGameUserChallengenumber_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"
#include "BinaryDataWrapper.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * The decrypted challenge number which is sent back to server to validate the client (co).
Note that the challenge number is first sent by the server in encrypted form as part of the login result message.
 */

class MsgGameUserChallengenumber : public min::MinMessage {


	public:
		/** The challenge number which was sent crypted by the server, decrypted by the client, the server will read and compare to the one originally sent.. */
		fwutil::BinaryDataWrapper challengeNumber;


		MsgGameUserChallengenumber(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHALLENGENUMBER;
			_msgMinLength = 12;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameUserChallengenumber(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHALLENGENUMBER;
			_msgMinLength = 12;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameUserChallengenumber(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHALLENGENUMBER;
			_msgMinLength = 12;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameUserChallengenumber() {}

		inline void initDefaultValues() {
		}

		bool valuesFromBytes(const unsigned char* bytes, bool readLength = true) {
			if (readLength) {
				if (length < _msgMinLength) {
					_msgIsValid = false;
					return false;
				}
			}
			//msgId = min::NetPort::uintFrom3Bytes(bytes, 1);
			challengeNumber = fwutil::BinaryDataWrapper(bytes, 8, 4);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			challengeNumber.copyToBuffer(bytes, 4);
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
