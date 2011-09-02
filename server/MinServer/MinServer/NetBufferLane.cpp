#include "NetBufferLane.h"
//#define ML_DEBUG 1
#ifdef ML_DEBUG
	#define DOUT(x) x
#else
	#define DOUT(x)
#endif

using namespace min;

NetBufferLane::NetBufferLane(unsigned long newMaxSize) 
: 
maxSize(newMaxSize),
//data((unsigned char* const)malloc(newMaxSize)),
data(_TRACK_NEW(new unsigned char[newMaxSize])),
totalFill(0), cursorPos(0), nextLane(0)
{
	cursorPointer = endPointer = data;
	DOUT(std::cout << "constructed lane with " << maxSize << std::endl;)
}

NetBufferLane::~NetBufferLane() {
	//free(data);
	delete[] data;
	DOUT(std::cout << "destroyed lane with " << maxSize << std::endl;)
}

unsigned char* NetBufferLane::requestBytes(unsigned long numBytes) {
	if (maxSize - totalFill < numBytes) {
		return 0;
	} else {
		unsigned char* retPointer = endPointer;
		endPointer += numBytes;
		totalFill += numBytes;
		return retPointer;
	}
}

const unsigned char* NetBufferLane::shiftHeadCursor(unsigned long numBytes) {
	if (numBytes > 0) {
		if (cursorPos + numBytes < totalFill) {
			cursorPointer += numBytes;
			cursorPos += numBytes;
		} else {
			// effectively reset this lane
			cursorPointer = endPointer = data;
			cursorPos = 0;
			totalFill = 0;
		}
	}
	return cursorPointer;
}
