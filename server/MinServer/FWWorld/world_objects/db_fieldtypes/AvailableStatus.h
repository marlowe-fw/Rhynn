#ifndef CharacterClassAvailableStatus_h__
#define CharacterClassAvailableStatus_h__

#include <iostream>
#include <string>

namespace fwworld {

	// macros for simple enum to string and string to enum conversion
#define __E2S_MAP(mcEnumVal) case mcEnumVal: return #mcEnumVal;
#define __S2E_MAP_ASSIGN(mcEnumVal) if (newValue ==  #mcEnumVal ) {eValue = mcEnumVal; return *this;}


	class AvailableStatus {

	public:
		// only need to adjust below (apart from class name):
		// ---------------------------
		enum E {none, all, bots_only, premium_only, testing_only, __count};

		/** Conversion to string. */
		operator std::string() const {
			switch (eValue) {
				__E2S_MAP(none)
				__E2S_MAP(all)
				__E2S_MAP(bots_only)
				__E2S_MAP(premium_only)
				__E2S_MAP(testing_only)
				default:break;
			}
			// only gets here if none of the above were matching
			return "none";
		}

		/** Assignment from string. */
		inline const AvailableStatus& operator=(const std::string& newValue) {
			__S2E_MAP_ASSIGN(none)
			__S2E_MAP_ASSIGN(all)
			__S2E_MAP_ASSIGN(bots_only)
			__S2E_MAP_ASSIGN(premium_only)
			__S2E_MAP_ASSIGN(testing_only)
			// only gets here if none of the above were matching
			eValue = none;
			return *this;
		}
		// ---------------------------
		// (end adjustment)

		/** Constructor, initialize with c++ enum value. */
		AvailableStatus(E newValue) : eValue(newValue) {}

		/** Construct directly by converting from string value. */
		AvailableStatus(const std::string& newValue) {
			*this = newValue;
		}

		/** Assign the c++ enum type directly. */
		inline const AvailableStatus& operator=(const E& newValue) {
			eValue = newValue;
			return *this;
		}

		/** Compare with other AvailableStatus object. */
		inline bool operator==(const AvailableStatus& other) const {return eValue == other.eValue;}
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
	inline std::ostream& operator <<(std::ostream& os, const AvailableStatus& ps) {
		return os << static_cast<std::string>(ps);
	}



#undef __E2S_MAP
#undef __S2E_MAP_ASSIGN

} // end namespace

#endif // CharacterClassAvailableStatus_h__
