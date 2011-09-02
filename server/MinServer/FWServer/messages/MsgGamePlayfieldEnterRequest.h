#ifndef MsgGamePlayfieldEnterRequest_h__
#define MsgGamePlayfieldEnterRequest_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Character requests to step on a playfield. This is not to be confused with the request enter world message which is only issued at the very beginning of the game. The enter playfield message is sent each time before the client character is actually added to the playfield (co).
 */

class MsgGamePlayfieldEnterRequest : public min::MinMessage {


	public:


		MsgGamePlayfieldEnterRequest(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_PLAYFIELD_ENTER_REQUEST;
			_msgMinLength = 4;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGamePlayfieldEnterRequest(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_PLAYFIELD_ENTER_REQUEST;
			_msgMinLength = 4;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGamePlayfieldEnterRequest(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_PLAYFIELD_ENTER_REQUEST;
			_msgMinLength = 4;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGamePlayfieldEnterRequest() {}

		inline void initDefaultValues() {
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
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
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
