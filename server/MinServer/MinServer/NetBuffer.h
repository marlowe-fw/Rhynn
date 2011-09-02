#ifndef NetBuffer_h__
#define NetBuffer_h__

#include "NetBufferLane.h"

namespace min {

	class NetBuffer {
		public:
			NetBuffer(unsigned long laneSize, unsigned int newMaxLanes);
			~NetBuffer();
			unsigned char* requestBytes(unsigned long numBytes);
			const unsigned char* shiftHeadCursor(unsigned long numBytes);
			inline unsigned long getRemainingFill() {return totalFill - bufferHeadCursorPos;}
			inline const unsigned char* getHeadCurosor() {return headCursorLane->getHeadCursor();}
			inline unsigned long getNextContiguousDataSize() {return headCursorLane->getRemainingFill();}

		private:

			unsigned long laneSize;
			unsigned int maxLanes;
			unsigned long totalSize;
			unsigned long totalFill;
			NetBufferLane* headCursorLane;

			unsigned long bufferHeadCursorPos;

			unsigned int numLanes;


			NetBufferLane* firstLane;
			NetBufferLane* writeLane;


			//unsigned char* firstBufferLane;
			/*
			unsigned long currentFill;

			unsigned int numBufferLanes;
			unsigned char* firstBufferLane;
			unsigned char* startPointer;
			unsigned char* endPointer;

			vector<char*> bufferLanes;
			*/
	};

}	// end namespace

#endif // NetBuffer_h__
