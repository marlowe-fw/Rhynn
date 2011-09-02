#ifndef ItemUsageType_h__
#define ItemUsageType_h__

#include <iostream>
#include <string>

namespace fwworld {

	// macros for simple enum to string and string to enum conversion
#define __E2S_MAP(mcEnumVal) case mcEnumVal: return #mcEnumVal;
#define __S2E_MAP_ASSIGN(mcEnumVal) if (newValue ==  #mcEnumVal ) {eValue = mcEnumVal; return *this;}

	class ItemUsageType {

	public:
		// only need to adjust below:
		// ---------------------------
		enum E {unknown, equip, use, gold, __count};

		/** Conversion to string. */
		operator std::string() const {
			switch (eValue) {
				__E2S_MAP(unknown)
					__E2S_MAP(equip)
					__E2S_MAP(use)
					__E2S_MAP(gold)
					default:break;
			}
			// only gets here if none of the above were matching
			return "unknown";
		}

		/** Assignment from string. */
		inline const ItemUsageType& operator=(const std::string& newValue) {
			__S2E_MAP_ASSIGN(unknown)
				__S2E_MAP_ASSIGN(equip)
				__S2E_MAP_ASSIGN(use)
				__S2E_MAP_ASSIGN(gold)
				// only gets here if none of the above were matching
				eValue = unknown;
			return *this;
		}
		// ---------------------------
		// (end adjustment)


		/** Constructor, initialize with c++ enum value. */
		ItemUsageType(E newValue) : eValue(newValue) {}

		/** Construct directly by converting from string value. */
		ItemUsageType(const std::string& newValue) {
			*this = newValue;
		}

		/** Assign the c++ enum type directly. */
		inline const ItemUsageType& operator=(const E& newValue) {
			eValue = newValue;
			return *this;
		}

		/** Compare with other ItemUsageType object. */
		inline bool operator==(const ItemUsageType& other) const {return eValue == other.eValue;}
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
	inline std::ostream& operator <<(std::ostream& os, const ItemUsageType& ps) {
		return os << static_cast<std::string>(ps);
	}



#undef __E2S_MAP
#undef __S2E_MAP_ASSIGN

} // end namespace


#endif // ItemUsageType_h__
