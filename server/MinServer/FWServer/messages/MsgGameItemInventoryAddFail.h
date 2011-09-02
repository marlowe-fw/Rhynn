#ifndef MsgGameItemInventoryAddFail_h__
#define MsgGameItemInventoryAddFail_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Server informs client that item could not be picked up / added to the inventory (so).
 */

class MsgGameItemInventoryAddFail : public min::MinMessage {


	public:
		/** Length of the info message specifying failure. */
		unsigned int infoLength;
		/** The info message detailing the failure. */
		std::string infoMessage;


		MsgGameItemInventoryAddFail(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_ITEM_INVENTORY_ADD_FAIL;
			_msgMinLength = 5;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameItemInventoryAddFail(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_ITEM_INVENTORY_ADD_FAIL;
			_msgMinLength = 5;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameItemInventoryAddFail(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_ITEM_INVENTORY_ADD_FAIL;
			_msgMinLength = 5;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameItemInventoryAddFail() {}

		inline void initDefaultValues() {
			infoLength = 0;
			infoMessage = "";
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
			infoLength = min::NetPort::uintFromByte(bytes, 4);
			if(5 + infoLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (infoLength > 96) {
				_msgIsValid = false;
				return false;
			}
			infoMessage = min::NetPort::stringFromBytes(bytes, infoLength, 5);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintToByte(infoLength, bytes, 4);
			min::NetPort::stringToBytes(infoMessage, bytes, infoLength, 5);
			return true;
		}

		inline bool checkValidateLength() {
			infoLength = (int)infoMessage.size();
			if (infoLength > 96) {
				_msgIsValid = false; 
				return false;
			}
			length = _msgMinLength  + infoLength;
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
