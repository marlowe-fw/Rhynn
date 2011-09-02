#ifndef MsgTestObjectMove_h__
#define MsgTestObjectMove_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * -.
 */

class MsgTestObjectMove : public min::MinMessage {


	public:
		/** -. */
		unsigned int listenerIndex;
		/** -. */
		double xPos;
		/** -. */
		double yPos;
		/** -. */
		double angle;
		/** -. */
		unsigned int red;
		/** -. */
		unsigned int green;
		/** -. */
		unsigned int blue;


		MsgTestObjectMove(bool init = true) {
			msgId = FWSMessageIDs::MSGID_TEST_OBJECT_MOVE;
			_msgMinLength = 21;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgTestObjectMove(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_TEST_OBJECT_MOVE;
			_msgMinLength = 21;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgTestObjectMove(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_TEST_OBJECT_MOVE;
			_msgMinLength = 21;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgTestObjectMove() {}

		inline void initDefaultValues() {
			listenerIndex = 0;
			xPos = 0;
			yPos = 0;
			angle = 0;
			red = 0;
			green = 0;
			blue = 0;
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
			listenerIndex = min::NetPort::uintFrom2Bytes(bytes, 4);
			xPos = min::NetPort::doubleFrom4BytesP2(bytes, 6);
			yPos = min::NetPort::doubleFrom4BytesP2(bytes, 10);
			angle = min::NetPort::doubleFrom4BytesP2(bytes, 14);
			red = min::NetPort::uintFromByte(bytes, 18);
			green = min::NetPort::uintFromByte(bytes, 19);
			blue = min::NetPort::uintFromByte(bytes, 20);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo2Bytes(listenerIndex, bytes, 4);
			min::NetPort::doubleTo4BytesP2(xPos, bytes, 6);
			min::NetPort::doubleTo4BytesP2(yPos, bytes, 10);
			min::NetPort::doubleTo4BytesP2(angle, bytes, 14);
			min::NetPort::uintToByte(red, bytes, 18);
			min::NetPort::uintToByte(green, bytes, 19);
			min::NetPort::uintToByte(blue, bytes, 20);
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
