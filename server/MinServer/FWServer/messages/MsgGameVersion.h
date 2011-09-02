#ifndef MsgGameVersion_h__
#define MsgGameVersion_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Game version required by the client (so).
 */

class MsgGameVersion : public min::MinMessage {


	public:
		/** Main version number. */
		unsigned int versionMajor;
		/** Minor version number. */
		unsigned int versionMinor;
		/** Sub version number. */
		unsigned int versionSub;


		MsgGameVersion(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_VERSION;
			_msgMinLength = 7;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameVersion(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_VERSION;
			_msgMinLength = 7;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameVersion(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_VERSION;
			_msgMinLength = 7;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameVersion() {}

		inline void initDefaultValues() {
			versionMajor = 0;
			versionMinor = 0;
			versionSub = 0;
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
			versionMajor = min::NetPort::uintFromByte(bytes, 4);
			versionMinor = min::NetPort::uintFromByte(bytes, 5);
			versionSub = min::NetPort::uintFromByte(bytes, 6);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintToByte(versionMajor, bytes, 4);
			min::NetPort::uintToByte(versionMinor, bytes, 5);
			min::NetPort::uintToByte(versionSub, bytes, 6);
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
