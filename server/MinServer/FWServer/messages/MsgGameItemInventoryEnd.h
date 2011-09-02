#ifndef MsgGameItemInventoryEnd_h__
#define MsgGameItemInventoryEnd_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Server sends a message to signal that all items for the inventory request have now been sent (so). This is in response to the character being selected by the client, and after all items have been sent
.
 */

class MsgGameItemInventoryEnd : public min::MinMessage {


	public:


		MsgGameItemInventoryEnd(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_ITEM_INVENTORY_END;
			_msgMinLength = 4;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameItemInventoryEnd(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_ITEM_INVENTORY_END;
			_msgMinLength = 4;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameItemInventoryEnd(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_ITEM_INVENTORY_END;
			_msgMinLength = 4;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameItemInventoryEnd() {}

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
