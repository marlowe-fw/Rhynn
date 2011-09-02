#ifndef MsgGamePlayfieldLoadChunk_h__
#define MsgGamePlayfieldLoadChunk_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"
#include "BinaryDataWrapper.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
#include "boost/shared_ptr.hpp"
#include <vector>

namespace fws {
	class FWClient;
}

namespace fwworld {
	//class FWWorld;
	class Playfield;
}
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Send playfield data to the client (so).
 */

class MsgGamePlayfieldLoadChunk : public min::MinMessage {


	private:
		unsigned int long_message_padding;


	public:
		/** Number of chunks which make up the complete playfield data. */
		unsigned int chunkCount;
		/** The current chunk number out of the total chunks. */
		unsigned int chunkNum;
		/** Length of this data chunk. */
		unsigned int chunkLength;
		/** The actual data chunk. */
		fwutil::BinaryDataWrapper chunkData;


		MsgGamePlayfieldLoadChunk(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_PLAYFIELD_LOAD_CHUNK;
			_msgMinLength = 14;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			long_message_padding = 0;
			_msgIsValid = true;
		}

		MsgGamePlayfieldLoadChunk(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_PLAYFIELD_LOAD_CHUNK;
			_msgMinLength = 14;
			long_message_padding = 0;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGamePlayfieldLoadChunk(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_PLAYFIELD_LOAD_CHUNK;
			_msgMinLength = 14;
			long_message_padding = 0;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGamePlayfieldLoadChunk() {}

		inline void initDefaultValues() {
			chunkCount = 0;
			chunkNum = 0;
			chunkLength = 0;
		}

		bool valuesFromBytes(const unsigned char* bytes, bool readLength = true) {
			long_message_padding = min::NetPort::uintFromByte(bytes, 0);
			if (readLength) {
				length = min::NetPort::uintFrom2Bytes(bytes, 1);
				if (length < _msgMinLength) {
					_msgIsValid = false;
					return false;
				}
			}
			//msgId = min::NetPort::uintFrom3Bytes(bytes, 3);
			chunkCount = min::NetPort::uintFrom2Bytes(bytes, 6);
			chunkNum = min::NetPort::uintFrom2Bytes(bytes, 8);
			chunkLength = min::NetPort::uintFrom4Bytes(bytes, 10);
			if(14 + chunkLength > length) {
				_msgIsValid = false;
				return false;
			}
			chunkData = fwutil::BinaryDataWrapper(bytes, chunkLength, 14);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(long_message_padding, bytes, 0);
			min::NetPort::uintTo2Bytes(length, bytes, 1);
			min::NetPort::uintTo3Bytes(msgId, bytes, 3);
			min::NetPort::uintTo2Bytes(chunkCount, bytes, 6);
			min::NetPort::uintTo2Bytes(chunkNum, bytes, 8);
			min::NetPort::uintTo4Bytes(chunkLength, bytes, 10);
			chunkData.copyToBuffer(bytes, 14);
			return true;
		}

		inline bool checkValidateLength() {
			chunkLength = chunkData.getSize();
			length = _msgMinLength  + chunkLength;
			return true;
		}

		inline bool isValid() {
			return _msgIsValid;
		}

	// %%GENERATOR_START%%MSG_IMPL_USER_CONTENT%%
		typedef boost::shared_ptr<MsgGamePlayfieldLoadChunk> SPMsgGamePlayfieldLoadChunk;

		// todo: this could be optimized to copy the messages directly to the message queue (raw copy) instead of using smart pointers to the chunk messages
		static void getChunkMessagesFromPlayfieldData(FWClient* pCurClient, const fwworld::Playfield& playfield, unsigned int chunkSize, std::vector<SPMsgGamePlayfieldLoadChunk>& allMessages);
	// %%GENERATOR_END%%MSG_IMPL_USER_CONTENT%%

};

}

#endif
