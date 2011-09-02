#ifndef net_basics_h__
#define net_basics_h__

#include "mem_dbg.h"

#if defined(_WIN32)
	#define FD_SETSIZE 2048
	#include "platform/w32/w32_net_basics.h"
#else
	#include "platform/unix/unix_net_basics.h"
#endif



#endif // net_basics_h__
