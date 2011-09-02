#ifndef NetBufferLane_h__
#define NetBufferLane_h__

#include <stdlib.h>
#include <iostream>

#include "mem_dbg.h"

namespace min {

	class NetBufferLane {

		public:
			NetBufferLane(unsigned long maxSize);
			~NetBufferLane();
			inline unsigned char* getData() {return data;}
			inline unsigned long getNumBytesFree() {return maxSize - totalFill;}
			inline const unsigned char* getHeadCursor() {return cursorPointer;}
			inline unsigned long getTotalFill() {return totalFill;}
			inline unsigned long getRemainingFill() {return totalFill-cursorPos;}
			unsigned char* requestBytes(unsigned long numBytes);
			const unsigned char* shiftHeadCursor(unsigned long numBytes);
			inline NetBufferLane* getNextLane() {return nextLane;}
			inline void setNextLane(NetBufferLane* newNextLane) {nextLane = newNextLane;}

		private:
			const unsigned long maxSize;
			unsigned char* const data;
			unsigned long totalFill;
			unsigned long cursorPos;
			NetBufferLane *nextLane;

			unsigned char* endPointer;
			unsigned char* cursorPointer;
	};

}

#endif // NetBufferLane_h__
