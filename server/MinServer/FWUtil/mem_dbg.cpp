#include "mem_dbg.h"

typedef std::map<void*, custom_mem::MemoryBlock*> MemoryMap;
MemoryMap* custom_mem::MemoryInfo::memoryBlocks = 0;
bool custom_mem::MemoryInfo::isActive = true;
