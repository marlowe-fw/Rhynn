#ifndef MsgGameFriendListRequest_h__
#define MsgGameFriendListRequest_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Client requests the friendlist for the selected character (co).
 */

class MsgGameFriendListRequest : public min::MinMessage {


	public:


		MsgGameFriendListRequest(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_FRIEND_LIST_REQUEST;
			_msgMinLength = 4;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameFriendListRequest(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_FRIEND_LIST_REQUEST;
			_msgMinLength = 4;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameFriendListRequest(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_FRIEND_LIST_REQUEST;
			_msgMinLength = 4;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameFriendListRequest() {}

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
