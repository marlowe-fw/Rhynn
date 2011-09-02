#ifndef UserType_h__
#define UserType_h__

#include <iostream>
#include <string>

namespace fwworld {

// macros for simple enum to string and string to enum conversion
#define __E2S_MAP(mcEnumVal) case mcEnumVal: return #mcEnumVal;
#define __S2E_MAP_ASSIGN(mcEnumVal) if (newValue ==  #mcEnumVal ) {eValue = mcEnumVal; return *this;}

class UserType {

public:
	// only need to adjust below:
	// ---------------------------
	enum E {user, moderator, observer, admin,__count};

	/** Conversion to string. */
	operator std::string() const {
		switch (eValue) {
			__E2S_MAP(user)
			__E2S_MAP(moderator)
			__E2S_MAP(observer)
			__E2S_MAP(admin)
			default:break;
		}
		// only gets here if none of the above were matching
		return "user";
	}

	/** Assignment from string. */
	inline const UserType& operator=(const std::string& newValue) {
		__S2E_MAP_ASSIGN(user)
		__S2E_MAP_ASSIGN(moderator)
		__S2E_MAP_ASSIGN(observer)
		__S2E_MAP_ASSIGN(admin)
		// only gets here if none of the above were matching
		eValue = user;
		return *this;
	}
	// ---------------------------
	// (end adjustment)


	/** Constructor, initialize with c++ enum value. */
	UserType(E newValue) : eValue(newValue) {}

	/** Construct directly by converting from string value. */
	UserType(const std::string& newValue) {
		*this = newValue;
	}

	/** Assign the c++ enum type directly. */
	inline const UserType& operator=(const E& newValue) {
		eValue = newValue;
		return *this;
	}

	/** Compare with other UserType object. */
	inline bool operator==(const UserType& other) const {return eValue == other.eValue;}
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
inline std::ostream& operator <<(std::ostream& os, const UserType& ps) {
	return os << static_cast<std::string>(ps);
}



#undef __E2S_MAP
#undef __S2E_MAP_ASSIGN

} // end namespace

#endif // UserType_h__
