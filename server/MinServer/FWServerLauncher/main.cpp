#include "FWServer.h"
#include <stdlib.h>



int main() {

	fws::FWServer server(23179, "config.txt");
	server.start();

	//custom_mem::MemoryInfo::printRemainingMemoryBlocks();
	//custom_mem::MemoryInfo::shutDown();

	//std::cin.get();

#ifdef _DEBUG
	//std::cin.get();
#endif

	return 0;
}
