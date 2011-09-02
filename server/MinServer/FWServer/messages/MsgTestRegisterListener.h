#ifndef MsgTestRegisterListener_h__
#define MsgTestRegisterListener_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * -.
 */

class MsgTestRegisterListener : public min::MinMessage {


	public:
		/** index of the client used for proper identification of a client. */
		unsigned int listenerIndex;


		MsgTestRegisterListener(bool init = true) {
			msgId = FWSMessageIDs::MSGID_TEST_REGISTER_LISTENER;
			_msgMinLength = 6;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgTestRegisterListener(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_TEST_REGISTER_LISTENER;
			_msgMinLength = 6;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgTestRegisterListener(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_TEST_REGISTER_LISTENER;
			_msgMinLength = 6;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgTestRegisterListener() {}

		inline void initDefaultValues() {
			listenerIndex = 0;
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
			listenerIndex = min::NetPort::uintFrom2Bytes(bytes, 4);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo2Bytes(listenerIndex, bytes, 4);
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
