#ifndef GenericClock_h__
#define GenericClock_h__

#include "DateTime.h"
#include <iostream>
#include <sstream>

#if defined(_WIN32)
	#include <Windows.h>
	typedef LONGLONG clock_ms_t;
#else
	typedef unsigned long long int clock_ms_t;
#endif

#include <time.h>


namespace fwutil {



class GenericClock {
	private:
		/**
		 * The timestamp value which was last fetched.
		 */
		clock_ms_t cachedTimestampMS;

		/**
		 * Structure holding the current date and time.
		 */
		DateTime cachedDateTime;

		#if defined(_WIN32)
			/** Win32 helper variable to retrieve the current tick count into a specialized structure. */
			LARGE_INTEGER winCounterValue;
			/** Win32 helper variable to retrieve the current tick count frequency. */
			LONGLONG tickCounterFrequency;	// per second for windows
			/** Win32 helper variable to retrieve the current tick count as a 64 bit signed long. */
			LONGLONG curTicks;
		#else
			/** Linux helper variable to retrieve the time value into a specialized structure. */
			timespec currentTimeSpec;
		#endif


		/**
		 * Perform any required startup calibration of the clock.
		 */
		void calibrate() {
			#if defined(_WIN32)
				cachedTimestampMS = 0;
				LARGE_INTEGER ticksPerSecond;
				QueryPerformanceFrequency(&ticksPerSecond);
				tickCounterFrequency = ticksPerSecond.QuadPart;
				//std::cout << "f: " << tickCounterFrequency << std::endl;
			#endif
			// initialize the timestamps
			getTimestampMS();
		}


	public:
		GenericClock() {
			calibrate();
		}

		/**
		 * Retrieve the current timestamp in milliseconds and return it.
		 * Note that the timestamps is measured from some arbitrary starting point
		 * (which may or may not be the time of system startup)
		 * @return The current timestamp value in milliseconds
		 */
        inline clock_ms_t getTimestampMS() {
			#if defined(_WIN32)
				QueryPerformanceCounter(&winCounterValue);
				// don't care about wrapping of winCounterValue as this server is intended to run for longer periods on *nix systems only
				// it will work for roughly one month without wrapping (max working value: 0x7fffffffffffffff / 1000 because of the *1000 below)
				return (cachedTimestampMS = (clock_ms_t)((winCounterValue.QuadPart*1000) / tickCounterFrequency));
			#else
				// linux
				clock_gettime(CLOCK_MONOTONIC, &currentTimeSpec);
				// this uses nanoseconds, we should not have the issue of wrapping here as we use an unsigned 64 bit integer and everything stays in ms
				// 64 bit in ms equals more than 500 million years, my friend
				return (cachedTimestampMS = (clock_ms_t)(currentTimeSpec.tv_sec*1000 + currentTimeSpec.tv_nsec/1000000));
			#endif
		}


		/**
		 * Get the most recent timestamp value which was retrieved by calling getTimestampMS().
		 * @return The cached timestamp value
		 */
		inline clock_ms_t getCachedTimestampMS() const {
			return cachedTimestampMS;
		}


		/**
		 * Get the current date and time (up to seconds).
		 * @return A DateTime object holding the current date and time values.
		 */
		inline const DateTime& getDateTime() {
			cachedDateTime.setCurrent();
			return cachedDateTime;
		}

		/**
		 * Get the most recent date and time value which was retrieved by calling getDateTime().
		 * @return The cached DateTime object holding the last retrieved date and time values.
		 */
		inline const DateTime& getCachedDateTime() const {
			return cachedDateTime;
		}

		inline const std::string getTimestampStr(bool includeMs = false) {
			if (includeMs) {
				return getDateTime().strLog() + ":" + fwutil::Common::intToString((int)(getTimestampMS()%1000));
			} else {
				return getDateTime().strLog();
			}
		}


};

}

#endif // GenericClock_h__
