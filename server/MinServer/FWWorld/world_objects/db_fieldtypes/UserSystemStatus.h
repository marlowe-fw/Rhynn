#ifndef UserSystemStatus_h__
#define UserSystemStatus_h__

#include <iostream>
#include <string>

namespace fwworld {

// macros for simple enum to string and string to enum conversion
#define __E2S_MAP(mcEnumVal) case mcEnumVal: return #mcEnumVal;
#define __S2E_MAP_ASSIGN(mcEnumVal) if (newValue ==  #mcEnumVal ) {eValue = mcEnumVal; return *this;}

class UserSystemStatus {

public:
	// only need to adjust below:
	// ---------------------------
	enum E {active, inactive, banned,__count};

	/** Conversion to string. */
	operator std::string() const {
		switch (eValue) {
			__E2S_MAP(active)
			__E2S_MAP(inactive)
			__E2S_MAP(banned)
			default:break;
		}
		// only gets here if none of the above were matching
		return "active";
	}

	/** Assignment from string. */
	inline const UserSystemStatus& operator=(const std::string& newValue) {
		__S2E_MAP_ASSIGN(active)
		__S2E_MAP_ASSIGN(inactive)
		__S2E_MAP_ASSIGN(banned)
		// only gets here if none of the above were matching
		eValue = active;
		return *this;
	}
	// ---------------------------
	// (end adjustment)


	/** Constructor, initialize with c++ enum value. */
	UserSystemStatus(E newValue) : eValue(newValue) {}

	/** Assign the c++ enum type directly. */
	inline const UserSystemStatus& operator=(const E& newValue) {
		eValue = newValue;
		return *this;
	}

	/** Construct directly by converting from string value. */
	UserSystemStatus(const std::string& newValue) {
		*this = newValue;
	}

	/** Compare with other UserSystemStatus object. */
	inline bool operator==(const UserSystemStatus& other) const {return eValue == other.eValue;}
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
inline std::ostream& operator <<(std::ostream& os, const UserSystemStatus& ps) {
	return os << static_cast<std::string>(ps);
}



#undef __E2S_MAP
#undef __S2E_MAP_ASSIGN

} // end namespace

#endif // SystemStatus_h__
