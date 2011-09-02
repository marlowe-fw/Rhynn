#ifndef YesNoEnum_h__
#define YesNoEnum_h__

#include <iostream>
#include <string>

namespace fwworld {

	// macros for simple enum to string and string to enum conversion
#define __E2S_MAP(mcEnumVal) case mcEnumVal: return #mcEnumVal;
#define __S2E_MAP_ASSIGN(mcEnumVal) if (newValue ==  #mcEnumVal ) {eValue = mcEnumVal; return *this;}

	class YesNoEnum {

	public:
		// only need to adjust below:
		// ---------------------------
		enum E {yes, no,__count};

		/** Conversion to string. */
		operator std::string() const {
			switch (eValue) {
				__E2S_MAP(yes)
				__E2S_MAP(no)
                default:break;
			}
			return "yes";
		}

		/** Assignment from string. */
		inline const YesNoEnum& operator=(const std::string& newValue) {
			__S2E_MAP_ASSIGN(yes)
			__S2E_MAP_ASSIGN(no)
			// only gets here if none of the above were matching
			eValue = yes;
			return *this;
		}
		// ---------------------------
		// (end adjustment)


		/** Constructor, initialize with c++ enum value. */
		YesNoEnum(E newValue) : eValue(newValue) {}

		/** Construct directly by converting from string value. */
		YesNoEnum(const std::string& newValue) {
			*this = newValue;
		}


		/** Assign the c++ enum type directly. */
		inline const YesNoEnum& operator=(const E& newValue) {
			eValue = newValue;
			return *this;
		}

		/** Compare with other YesNoEnum object. */
		inline bool operator==(const YesNoEnum& other) const {return eValue == other.eValue;}
		/** Compare with C++ enum type directly. */
		inline bool operator==(const E& other) const {return eValue == other;}
		/** Compare with C++ enum type directly. */
		inline bool operator!=(const E& other) const {return !(*this == other);}
		/** Helper for explicit to string conversion. */
		inline std::string str() {return static_cast<std::string>(*this);}
		/** Helper for explicit to uint conversion. */
		inline unsigned int val() const {return static_cast<unsigned int>(eValue);}
		/** Special for yes no such that yes translates to 1 and no to 0. */
		inline unsigned int naturalVal() const {return (static_cast<unsigned int>(eValue) == 0) ? 1 : 0;}

	private:
		/** Actual c++ enum value. */
		E eValue;
	};

	/** Allow to be embedded in ostream. */
	inline std::ostream& operator <<(std::ostream& os, const YesNoEnum& ps) {
		return os << static_cast<std::string>(ps);
	}



#undef __E2S_MAP
#undef __S2E_MAP_ASSIGN

} // end namespace

#endif // YesNoEnum_h__
