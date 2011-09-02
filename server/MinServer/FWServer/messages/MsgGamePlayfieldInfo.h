#ifndef MsgGamePlayfieldInfo_h__
#define MsgGamePlayfieldInfo_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Send playfield information to the client, like name and world coordinates (so).
 */

class MsgGamePlayfieldInfo : public min::MinMessage {


	public:
		/** -. */
		unsigned int playfieldId;
		/** Playfield width in cells. */
		unsigned int width;
		/** Playfield height in cells. */
		unsigned int height;
		/** -. */
		unsigned int nameLength;
		/** -. */
		std::string name;


		MsgGamePlayfieldInfo(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_PLAYFIELD_INFO;
			_msgMinLength = 13;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGamePlayfieldInfo(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_PLAYFIELD_INFO;
			_msgMinLength = 13;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGamePlayfieldInfo(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_PLAYFIELD_INFO;
			_msgMinLength = 13;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGamePlayfieldInfo() {}

		inline void initDefaultValues() {
			playfieldId = 0;
			width = 0;
			height = 0;
			nameLength = 0;
			name = "";
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
			playfieldId = min::NetPort::uintFrom4Bytes(bytes, 4);
			width = min::NetPort::uintFrom2Bytes(bytes, 8);
			height = min::NetPort::uintFrom2Bytes(bytes, 10);
			nameLength = min::NetPort::uintFromByte(bytes, 12);
			if(13 + nameLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (nameLength > 32) {
				_msgIsValid = false;
				return false;
			}
			name = min::NetPort::stringFromBytes(bytes, nameLength, 13);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo4Bytes(playfieldId, bytes, 4);
			min::NetPort::uintTo2Bytes(width, bytes, 8);
			min::NetPort::uintTo2Bytes(height, bytes, 10);
			min::NetPort::uintToByte(nameLength, bytes, 12);
			min::NetPort::stringToBytes(name, bytes, nameLength, 13);
			return true;
		}

		inline bool checkValidateLength() {
			nameLength = (int)name.size();
			if (nameLength > 32) {
				_msgIsValid = false; 
				return false;
			}
			length = _msgMinLength  + nameLength;
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
