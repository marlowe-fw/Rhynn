#ifndef MsgGameGraphicsLoadInfo_h__
#define MsgGameGraphicsLoadInfo_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Notifies the client what the total size of the image is, will send 0 as the size if the image cannot be loaded (so).
 */

class MsgGameGraphicsLoadInfo : public min::MinMessage {


	public:
		/** Size of the requested image in bytes. */
		unsigned int imageSize;


		MsgGameGraphicsLoadInfo(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_GRAPHICS_LOAD_INFO;
			_msgMinLength = 8;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameGraphicsLoadInfo(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_GRAPHICS_LOAD_INFO;
			_msgMinLength = 8;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameGraphicsLoadInfo(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_GRAPHICS_LOAD_INFO;
			_msgMinLength = 8;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameGraphicsLoadInfo() {}

		inline void initDefaultValues() {
			imageSize = 0;
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
			imageSize = min::NetPort::uintFrom4Bytes(bytes, 4);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo4Bytes(imageSize, bytes, 4);
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
