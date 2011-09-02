#ifndef memory_debug_custom2_h__
#define memory_debug_custom2_h__

// Custom memory leak tracker by Jochai Papke

#ifdef _DEBUG
	//#define _MEM_DEBUG_CUSTOM 1
#endif

/*
To report memory leaks, #define _MEM_DEBUG_CUSTOM in this file, include this file in your project, and call:
custom_mem::MemoryInfo::printRemainingMemoryBlocks();
custom_mem::MemoryInfo::shutDown();
at the end of your program.
Note that depending on your program design not all objects have been destroyed at the time you call the two above lines.
In such a case you should restructure by constructing objects only in a helper function which you call in turn from your main function
where you place the above statements at the very end.
*/

// --------------------------
#ifdef _MEM_DEBUG_CUSTOM
	// replace call to new to go through a tracker function which keeps track of allocated memory blocks, for leak detection
	#define _TRACK_NEW(newStatement) custom_mem::track(newStatement, __FILE__, __LINE__)
#else
	// _TRACK_NEW will just result in the existing new statement being called
	#define _TRACK_NEW(newStatement) newStatement
#endif


#include <iostream>
#include <map>

#ifndef _WIN32
    #include "string.h"
    #include "malloc.h"
#endif
namespace custom_mem {



// --------------------------


	class MemoryBlock {

	public:
		MemoryBlock(void* newAddress, unsigned long newSize, const char* newFilename, int line) :
		  next(0), prev(0),
			  address(newAddress), size(newSize), lineNumber(line)
		  {
			  #ifdef _WIN32
			  strncpy_s(filename, newFilename, 255);
			  #else
			  strncpy(filename, newFilename, 255);
			  #endif

			  //std::cout << "ctor new block : " << newAddress << std::endl;

		  }

		  ~MemoryBlock() {
			  //std::cout << "destructing block" << std::endl;
		  }

		  void print() {
			  std::cout << "Memory block at " << address << std::endl;
			  std::cout << "----------------------- " << std::endl;
			  //std::cout << "Size: " << size << std::endl;
			  std::cout << "File: " << filename << std::endl;
			  std::cout << "Line: " << lineNumber << std::endl;
		  }

		  MemoryBlock* next;
		  MemoryBlock* prev;

		  void* address;
		  unsigned long size;
		  char	filename[256];
		  int lineNumber;

	};


	class MemoryInfo {
		typedef std::map<void*, custom_mem::MemoryBlock*> MemoryMap;

	public:
		~MemoryInfo() {
		}

		static bool isActive;

		static void addMemoryBlock(MemoryBlock* newBlock) {
			if (memoryBlocks == 0) {
				memoryBlocks = new MemoryMap;
			}
			memoryBlocks->insert(std::pair<void*, MemoryBlock*>(newBlock->address, newBlock));
		}

		static void shutDown() {
			//std::cout << "shutdown 1" << std::endl;
			isActive = false;
			if (memoryBlocks != 0) {
				for(MemoryMap::iterator it = memoryBlocks->begin(); it!=memoryBlocks->end(); it++) {
					delete (*it).second;
				}
				delete memoryBlocks;
				memoryBlocks = 0;
			}
			//std::cout << "shutdown 2" << std::endl;
		}

		static void removeMemoryBlock(void* address) {
			if (memoryBlocks == 0) {
				return;
			}

			MemoryMap::iterator it = memoryBlocks->find(address);
			if (it != memoryBlocks->end()) {
				memoryBlocks->erase(it);
			}
		}


		static void printRemainingMemoryBlocks() {
			std::cout << "-------------------------------" << std::endl;
			std::cout << "MEMORY INFO: Remaining Blocks" << std::endl;
			std::cout << "-------------------------------" << std::endl;

			if (memoryBlocks!=0 && memoryBlocks->size() > 0) {
				for (MemoryMap::iterator it = memoryBlocks->begin(); it!=memoryBlocks->end(); it++) {
					((*it).second)->print();
				}
			} else {
				std::cout << "NO BLOCKS REMAINING" << std::endl;
			}


			std::cout << "-------------------------------" << std::endl;
		}
	private:
		MemoryInfo() {
		}
		unsigned long numAllocations;
		unsigned long numBytesAllocated;

		static MemoryMap* memoryBlocks;

	};

	// this is the tracker function which will be called for every use of the _TRACK_NEW macro
	template <class T>
	T track(T ptr, const char* filename, int line) {
		//void* v = ptr;
		//std::cout << "tracking allocation: " << v << std::endl;
		custom_mem::MemoryBlock* block = new custom_mem::MemoryBlock(ptr, 0, filename, line);
		custom_mem::MemoryInfo::addMemoryBlock(block);

		return ptr;
	}



}


#ifdef _MEM_DEBUG_CUSTOM

// ---------------------
#ifdef _WIN32
inline void __cdecl operator delete(void *p)
#else
inline void operator delete(void *p)
#endif
{
	if (custom_mem::MemoryInfo::isActive) {
		//std::cout << "custom delete, addr: " << p << std::endl;
		custom_mem::MemoryInfo::removeMemoryBlock(p);
	}
	free(p);
};

#ifdef _WIN32
inline void __cdecl operator delete[](void *p)
#else
inline void operator delete[](void *p)
#endif
{
	operator delete(p);
};

#endif


/*
the block list is not currently used, std::map used instead

class MemoryBlockList {
private:
	int numElements;
	MemoryBlock* head;
	MemoryBlock* tail;

public:

	MemoryBlockList() : head(0), tail(0), numElements(0) {}
	~MemoryBlockList() {
		MemoryBlock* cur = head;
		MemoryBlock* tmp = head;
		while (cur != 0) {
			//std::cout << "deleting in blocklist dtor" << std::endl;
			tmp = cur;
			cur = cur->next;
			delete tmp;
			//std::cout << "deleting in blocklist done" << std::endl;
		}

	}

	inline int size() {return numElements;}
	inline MemoryBlock* getHead() {return head;}

	void add(MemoryBlock* block) {
		if (head == 0) {
			head = tail = block;
		} else {
			tail->next = block;
			block->prev = tail;
			tail = block;
		}
		numElements++;
	}

	bool remove(void* address) {
		bool found = false;
		MemoryBlock* curBlock = head;
		while (!found && curBlock != 0) {
			if (curBlock->address == address) {
				found = true;

				if (curBlock->next != 0) {
					curBlock->next->prev = curBlock->prev;
				} else {
					// was tail
					tail = curBlock->prev;
				}

				if (curBlock->prev != 0) {
					curBlock->prev->next = curBlock->next;
				} else {
					// was head
					head = curBlock->next;
				}

				numElements--;

				//std::cout << "remaining elements: " << numElements << std::endl;
				delete curBlock;
			} else {
				// next element
				curBlock = curBlock->next;
			}
		}
		return found;
	}

};
*/

#endif // memory_debug_custom2_h__
