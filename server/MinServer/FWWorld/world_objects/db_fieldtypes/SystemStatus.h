#ifndef SystemStatus_h__
#define SystemStatus_h__

#include <iostream>
#include <string>

namespace fwworld {

	// macros for simple enum to string and string to enum conversion
#define __E2S_MAP(mcEnumVal) case mcEnumVal: return #mcEnumVal;
#define __S2E_MAP_ASSIGN(mcEnumVal) if (newValue ==  #mcEnumVal ) {eValue = mcEnumVal; return *this;}

	class SystemStatus {

	public:
		// only need to adjust below:
		// ---------------------------
		enum E {normal, deleted, wb_new, __count};

		/** Conversion to string. */
		operator std::string() const {
			switch (eValue) {
				__E2S_MAP(normal)
				__E2S_MAP(deleted)
				__E2S_MAP(wb_new)
				default:break;
			}
			// only gets here if none of the above were matching
			return "normal";
		}

		/** Assignment from string. */
		inline const SystemStatus& operator=(const std::string& newValue) {
			__S2E_MAP_ASSIGN(normal)
			__S2E_MAP_ASSIGN(deleted)
			__S2E_MAP_ASSIGN(wb_new)
			// only gets here if none of the above were matching
			eValue = normal;
			return *this;
		}
		// ---------------------------
		// (end adjustment)


		/** Constructor, initialize with c++ enum value. */
		SystemStatus(E newValue) : eValue(newValue) {}

		/** Construct directly by converting from string value. */
		SystemStatus(const std::string& newValue) {
			*this = newValue;
		}


		/** Assign the c++ enum type directly. */
		inline const SystemStatus& operator=(const E& newValue) {
			eValue = newValue;
			return *this;
		}

		/** Compare with other SystemStatus object. */
		inline bool operator==(const SystemStatus& other) const {return eValue == other.eValue;}
		/** Compare with C++ enum type directly. */
		inline bool operator==(const E& other) const {return eValue == other;}
		/** Compare with C++ enum type directly. */
		inline bool operator!=(const E& other) const {return !(*this == other);}
		/** Helper for explicit to string conversion. */
		inline std::string str() {return static_cast<std::string>(*this);}
		/** Helper for explicit to uint conversion. */
		inline unsigned int val() const {return static_cast<unsigned int>(eValue);}

	private:
		/** Actual c++ enum value. */
		E eValue;
	};

	/** Allow to be embedded in ostream. */
	inline std::ostream& operator <<(std::ostream& os, const SystemStatus& ps) {
		return os << static_cast<std::string>(ps);
	}



#undef __E2S_MAP
#undef __S2E_MAP_ASSIGN

} // end namespace

#endif // SystemStatus_h__
