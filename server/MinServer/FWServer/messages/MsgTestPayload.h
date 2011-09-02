#ifndef MsgTestPayload_h__
#define MsgTestPayload_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * -.
 */

class MsgTestPayload : public min::MinMessage {


	public:
		/** client index. */
		unsigned int index;
		/** -. */
		double d1;
		/** -. */
		double d2;
		/** -. */
		double d3;
		/** -. */
		unsigned int i1;
		/** -. */
		unsigned int i2;
		/** -. */
		unsigned int i3;


		MsgTestPayload(bool init = true) {
			msgId = FWSMessageIDs::MSGID_TEST_PAYLOAD;
			_msgMinLength = 21;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgTestPayload(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_TEST_PAYLOAD;
			_msgMinLength = 21;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgTestPayload(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_TEST_PAYLOAD;
			_msgMinLength = 21;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgTestPayload() {}

		inline void initDefaultValues() {
			index = 0;
			d1 = 0;
			d2 = 0;
			d3 = 0;
			i1 = 0;
			i2 = 0;
			i3 = 0;
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
			index = min::NetPort::uintFrom2Bytes(bytes, 4);
			d1 = min::NetPort::doubleFrom4BytesP2(bytes, 6);
			d2 = min::NetPort::doubleFrom4BytesP2(bytes, 10);
			d3 = min::NetPort::doubleFrom4BytesP2(bytes, 14);
			i1 = min::NetPort::uintFromByte(bytes, 18);
			i2 = min::NetPort::uintFromByte(bytes, 19);
			i3 = min::NetPort::uintFromByte(bytes, 20);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo2Bytes(index, bytes, 4);
			min::NetPort::doubleTo4BytesP2(d1, bytes, 6);
			min::NetPort::doubleTo4BytesP2(d2, bytes, 10);
			min::NetPort::doubleTo4BytesP2(d3, bytes, 14);
			min::NetPort::uintToByte(i1, bytes, 18);
			min::NetPort::uintToByte(i2, bytes, 19);
			min::NetPort::uintToByte(i3, bytes, 20);
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
