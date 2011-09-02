#ifndef Random_h__
#define Random_h__

#include <stdlib.h>
#include <time.h>

namespace fwutil {

class Random {
	
	public:

		static int nextInt() {
			if (!initialized) {
				srand((unsigned int)time(0));
				initialized = true;
			}
			return rand();
		}

		static int nextInt(int limit) {
			if (!initialized) {
				srand((unsigned int)time(0));
				initialized = true;
			}
			return rand() % (limit+1);
		}

	private:
		Random() {};
		static bool initialized;

};


} // end namespace fwutil

#endif // Random_h__