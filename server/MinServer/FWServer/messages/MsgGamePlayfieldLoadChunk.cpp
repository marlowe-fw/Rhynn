#include "MsgGamePlayfieldLoadChunk.h"
#include "../FWClient.h"
#include "world_objects/Playfield.h"

using namespace fws;

// todo: this could be optimized to copy the messages directly to the message queue (raw copy) instead of using smart pointers to the chunk messages
void MsgGamePlayfieldLoadChunk::getChunkMessagesFromPlayfieldData(FWClient* pCurClient, const Playfield& playfield, unsigned int chunkSize, std::vector<SPMsgGamePlayfieldLoadChunk>& allMessages) {
	// get image data and size
	const fwutil::BinaryDataWrapper& pfDataBw = playfield.getData();

	unsigned char* playfielData = pfDataBw.getData();
	unsigned int totalSize = pfDataBw.getSize();

	if (playfielData!=0 && totalSize > 0) {
		// get number of chunks to send
		unsigned int numChunks = (int)(totalSize / chunkSize);
		if (totalSize % chunkSize > 0) {
			++numChunks;
		}
		allMessages.reserve(numChunks);

		unsigned int curChunkNum = 0;
		unsigned int curChunkSize = chunkSize;
		unsigned int curOffset = 0;

		while (curOffset < totalSize) {
			++curChunkNum;
			if (totalSize - curOffset < chunkSize) {
				// only possible for last chunk
				curChunkSize = totalSize - curOffset;
			}

			// create and fill message
			SPMsgGamePlayfieldLoadChunk msg(_TRACK_NEW(new MsgGamePlayfieldLoadChunk()));
			msg->chunkCount = numChunks;
			msg->chunkNum = curChunkNum;
			msg->chunkLength = curChunkSize;
			msg->chunkData.fromBuffer(playfielData, curChunkSize, curOffset);
			msg->checkValidateLength();

			// add to vector
			allMessages.push_back(msg);

			// increase offset
			curOffset += curChunkSize;
		}
	}
}
