#ifndef MsgGamePlayfieldGraphicsInfo_h__
#define MsgGamePlayfieldGraphicsInfo_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"
#include "BinaryDataWrapper.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Send which graphics are required for a playfield (so).
 */

class MsgGamePlayfieldGraphicsInfo : public min::MinMessage {


	public:
		/** 0:background, 1:character. */
		unsigned int type;
		/** -. */
		unsigned int idBufferLength;
		/** Holds the graphics ids, 4 bytes each. */
		fwutil::BinaryDataWrapper idBuffer;


		MsgGamePlayfieldGraphicsInfo(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_PLAYFIELD_GRAPHICS_INFO;
			_msgMinLength = 6;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGamePlayfieldGraphicsInfo(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_PLAYFIELD_GRAPHICS_INFO;
			_msgMinLength = 6;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGamePlayfieldGraphicsInfo(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_PLAYFIELD_GRAPHICS_INFO;
			_msgMinLength = 6;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGamePlayfieldGraphicsInfo() {}

		inline void initDefaultValues() {
			type = 0;
			idBufferLength = 0;
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
			type = min::NetPort::uintFromByte(bytes, 4);
			idBufferLength = min::NetPort::uintFromByte(bytes, 5);
			if(6 + idBufferLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (idBufferLength > 120) {
				_msgIsValid = false;
				return false;
			}
			idBuffer = fwutil::BinaryDataWrapper(bytes, idBufferLength, 6);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintToByte(type, bytes, 4);
			min::NetPort::uintToByte(idBufferLength, bytes, 5);
			idBuffer.copyToBuffer(bytes, 6);
			return true;
		}

		inline bool checkValidateLength() {
			idBufferLength = idBuffer.getSize();
			if (idBufferLength > 120) {
				_msgIsValid = false; 
				return false;
			}
			length = _msgMinLength  + idBufferLength;
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
