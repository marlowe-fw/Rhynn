#ifndef MsgGameServerEntry_h__
#define MsgGameServerEntry_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Server list entry to send to the client (so).
 */

class MsgGameServerEntry : public min::MinMessage {


	public:
		/** -. */
		unsigned int ipTextLength;
		/** -. */
		std::string ip;
		/** -. */
		unsigned int serverNameTextLength;
		/** -. */
		std::string serverName;


		MsgGameServerEntry(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_SERVER_ENTRY;
			_msgMinLength = 6;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameServerEntry(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_SERVER_ENTRY;
			_msgMinLength = 6;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameServerEntry(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_SERVER_ENTRY;
			_msgMinLength = 6;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameServerEntry() {}

		inline void initDefaultValues() {
			ipTextLength = 0;
			ip = "";
			serverNameTextLength = 0;
			serverName = "";
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
			ipTextLength = min::NetPort::uintFromByte(bytes, 4);
			if(5 + ipTextLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (ipTextLength > 24) {
				_msgIsValid = false;
				return false;
			}
			ip = min::NetPort::stringFromBytes(bytes, ipTextLength, 5);
			unsigned int curIndex = 5 + ipTextLength;
			if(curIndex + 1 > length) {
				_msgIsValid = false;
				return false;
			}
			serverNameTextLength = min::NetPort::uintFromByte(bytes, curIndex);
			curIndex += 1;
			if(curIndex + serverNameTextLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (serverNameTextLength > 45) {
				_msgIsValid = false;
				return false;
			}
			serverName = min::NetPort::stringFromBytes(bytes, serverNameTextLength, curIndex);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintToByte(ipTextLength, bytes, 4);
			min::NetPort::stringToBytes(ip, bytes, ipTextLength, 5);
			unsigned int curIndex = 5 + ipTextLength;
			min::NetPort::uintToByte(serverNameTextLength, bytes, curIndex);
			curIndex += 1;
			min::NetPort::stringToBytes(serverName, bytes, serverNameTextLength, curIndex);
			return true;
		}

		inline bool checkValidateLength() {
			ipTextLength = (int)ip.size();
			if (ipTextLength > 24) {
				_msgIsValid = false; 
				return false;
			}
			serverNameTextLength = (int)serverName.size();
			if (serverNameTextLength > 45) {
				_msgIsValid = false; 
				return false;
			}
			length = _msgMinLength  + ipTextLength + serverNameTextLength;
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
