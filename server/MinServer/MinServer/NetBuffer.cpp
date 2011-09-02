#include "NetBuffer.h"
//#define ML_DEBUG 0
#ifdef ML_DEBUG
	#define DOUT(x) x
#else
	#define DOUT(x)
#endif

using namespace min;

NetBuffer::NetBuffer(unsigned long newLaneSize, unsigned int newMaxLanes) :
laneSize(newLaneSize), maxLanes(newMaxLanes),
totalSize(newLaneSize), totalFill(0), bufferHeadCursorPos(0)
{
	firstLane = writeLane = headCursorLane = _TRACK_NEW(new NetBufferLane(laneSize));
	numLanes = 1;
}

NetBuffer::~NetBuffer() {
	int i;
	i=0;
	// remove all lanes
	NetBufferLane* curLane = firstLane;
	while (curLane != 0) {
		DOUT(std::cout << "removing lane " << (++i) << ", fill: " << (curLane->getRemainingFill()) << std::endl;)
		NetBufferLane* delLane = curLane;
		curLane = curLane->getNextLane();
		delete delLane;
	}
}

unsigned char* NetBuffer::requestBytes(unsigned long numBytes) {
	unsigned char* writePointer = writeLane->requestBytes(numBytes);

	DOUT(std::cout << "requesting " << numBytes << " bytes .." << std::endl;)

	if (writePointer != 0) {
		DOUT(std::cout << "fits into write lane, fill: " << (writeLane->getTotalFill()) << std::endl;)
		totalFill += numBytes;
		DOUT(std::cout << "new lane fill: " << (writeLane->getTotalFill()) << std::endl;)
	} else if (laneSize >= numBytes) {
		DOUT(std::cout << "does NOT fit into write lane, fill: " << (writeLane->getTotalFill()) << std::endl;)
		NetBufferLane* nextLane = writeLane->getNextLane();

		if (nextLane == 0 && (maxLanes == 0 || numLanes < maxLanes)) {
			DOUT(std::cout << "add new lane, num Lanes: " << (numLanes+1) << std::endl;)
			// add a new lane if conditions allow
			nextLane = _TRACK_NEW(new NetBufferLane(laneSize));
			writeLane->setNextLane(nextLane);
			numLanes++;
			totalSize += laneSize;
		}
		if (nextLane != 0) {
			writeLane = nextLane;
			writePointer = writeLane->requestBytes(numBytes);
			totalFill += numBytes;
			DOUT(std::cout << "next write lane fill: " << (writeLane->getTotalFill()) << std::endl;)
		} else {
			DOUT(std::cout << "does NOT fit into write lane, and no new lane can be added, num lanes: " << (numLanes) << std::endl;)
		}
	} else {
		DOUT(std::cout << "lane size not sufficient! ignoring operation " << std::endl;)
	}

	DOUT(std::cout << "new totalFill: " << totalFill << std::endl;)

	return writePointer;
}

const unsigned char* NetBuffer::shiftHeadCursor(unsigned long numBytes) {
	DOUT(std::cout << "shifting head cursor: " << numBytes << ".." << std::endl;)

	while (numBytes > 0) {
		unsigned long curLaneFill = headCursorLane->getRemainingFill();
		unsigned long maxShift = numBytes > curLaneFill ? curLaneFill : numBytes;

		DOUT(std::cout << "cursor lane fill: " << curLaneFill << ", shift: " << maxShift << std::endl;)

		headCursorLane->shiftHeadCursor(maxShift);
		DOUT(std::cout << "cursor lane fill after shift: " << (headCursorLane->getRemainingFill()) << std::endl;)

		numBytes -= maxShift;
		//totalFill -= maxShift;
		bufferHeadCursorPos += maxShift;
		long remainingFill = totalFill - bufferHeadCursorPos;

		DOUT(std::cout << "total buffer fill after shift: " << totalFill << std::endl;)
		DOUT(std::cout << "absolute cursor pos after shift: " << bufferHeadCursorPos << std::endl;)

		if (remainingFill > 0 && headCursorLane->getRemainingFill() == 0) {
			DOUT(std::cout << "more bytes in buffer and lane is empty " << std::endl;)
			// lane is empty
			if (headCursorLane->getNextLane() != 0) {
				headCursorLane = headCursorLane->getNextLane();
				DOUT(std::cout << "head cursor lane -> next lane " << std::endl;)
			} else {
				DOUT(std::cout << "error: buffer indicates more bytes but is missing next lane" << std::endl;)
				totalFill = 0;
				remainingFill = 0;
			}
		}

		if (remainingFill <= 0) {
			DOUT(std::cout << "no more bytes in buffer, resetting cursor, total fill: " << totalFill << std::endl;)
			bufferHeadCursorPos = 0;
			totalFill = 0;
			// all writes will append at the head now too, so reset write lane as well
			headCursorLane = writeLane = firstLane;
			break;
		}

		DOUT(std::cout << "head cursor lane remaining fill: " << headCursorLane->getRemainingFill() << std::endl;)
	}

	return headCursorLane->getHeadCursor();

}
