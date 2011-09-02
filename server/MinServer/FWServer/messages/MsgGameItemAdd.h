#ifndef MsgGameItemAdd_h__
#define MsgGameItemAdd_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
#include "world_objects/Item.h"
using namespace fwworld;
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Server informs a client that an item is now in his interest zone, i.e. visible and relevant (so)
.
 */

class MsgGameItemAdd : public min::MinMessage {


	public:
		/** -. */
		unsigned int itemId;
		/** -. */
		unsigned int graphicId;
		/** -. */
		unsigned int graphicsX;
		/** -. */
		unsigned int graphicsY;
		/** -. */
		unsigned int usageType;
		/** -. */
		unsigned int xPos;
		/** -. */
		unsigned int yPos;


		MsgGameItemAdd(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_ITEM_ADD;
			_msgMinLength = 21;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameItemAdd(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_ITEM_ADD;
			_msgMinLength = 21;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameItemAdd(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_ITEM_ADD;
			_msgMinLength = 21;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameItemAdd() {}

		inline void initDefaultValues() {
			itemId = 0;
			graphicId = 0;
			graphicsX = 0;
			graphicsY = 0;
			usageType = 0;
			xPos = 0;
			yPos = 0;
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
			graphicId = min::NetPort::uintFrom4Bytes(bytes, 8);
			graphicsX = min::NetPort::uintFrom2Bytes(bytes, 12);
			graphicsY = min::NetPort::uintFrom2Bytes(bytes, 14);
			usageType = min::NetPort::uintFromByte(bytes, 16);
			xPos = min::NetPort::uintFrom2Bytes(bytes, 17);
			yPos = min::NetPort::uintFrom2Bytes(bytes, 19);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo4Bytes(itemId, bytes, 4);
			min::NetPort::uintTo4Bytes(graphicId, bytes, 8);
			min::NetPort::uintTo2Bytes(graphicsX, bytes, 12);
			min::NetPort::uintTo2Bytes(graphicsY, bytes, 14);
			min::NetPort::uintToByte(usageType, bytes, 16);
			min::NetPort::uintTo2Bytes(xPos, bytes, 17);
			min::NetPort::uintTo2Bytes(yPos, bytes, 19);
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

		void fromItem(const Item& item) {
			itemId = item.getId();
			graphicId = item.getGraphicId();
			graphicsX = item.getGraphicsX();
			graphicsY = item.getGraphicsY();
			usageType = (item.getUsageType()).val();
			xPos = item.getX();
			yPos = item.getY();
			// checkValidateLength();
		}

	// %%GENERATOR_END%%MSG_IMPL_USER_CONTENT%%

};

}

#endif
