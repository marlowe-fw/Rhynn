#ifndef MsgGameItemDropRequest_h__
#define MsgGameItemDropRequest_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Client drops an item to the playfield (co)
.
 */

class MsgGameItemDropRequest : public min::MinMessage {


	public:
		/** -. */
		unsigned int itemId;
		/** -. */
		unsigned int units;


		MsgGameItemDropRequest(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_ITEM_DROP_REQUEST;
			_msgMinLength = 10;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameItemDropRequest(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_ITEM_DROP_REQUEST;
			_msgMinLength = 10;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameItemDropRequest(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_ITEM_DROP_REQUEST;
			_msgMinLength = 10;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameItemDropRequest() {}

		inline void initDefaultValues() {
			itemId = 0;
			units = 0;
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
			itemId = min::NetPort::uintFrom4Bytes(bytes, 4);
			units = min::NetPort::uintFrom2Bytes(bytes, 8);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo4Bytes(itemId, bytes, 4);
			min::NetPort::uintTo2Bytes(units, bytes, 8);
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
