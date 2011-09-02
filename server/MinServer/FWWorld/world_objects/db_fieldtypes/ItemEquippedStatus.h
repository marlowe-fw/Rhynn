#ifndef ItemEquippedStatus_h__
#define ItemEquippedStatus_h__

#include <iostream>
#include <string>

namespace fwworld {

	// macros for simple enum to string and string to enum conversion
#define __E2S_MAP(mcEnumVal) case mcEnumVal: return #mcEnumVal;
#define __S2E_MAP_ASSIGN(mcEnumVal) if (newValue ==  #mcEnumVal ) {eValue = mcEnumVal; return *this;}

	class ItemEquippedStatus {

	public:
		// only need to adjust below:
		// ---------------------------
		enum E {not_equipped, equipped, belt, __count};

		/** Conversion to string. */
		operator std::string() const {
			switch (eValue) {
					__E2S_MAP(not_equipped)
					__E2S_MAP(equipped)
					__E2S_MAP(belt)
					default:break;
			}
			// only gets here if none of the above were matching
			return "not_equipped";
		}

		/** Assignment from string. */
		inline const ItemEquippedStatus& operator=(const std::string& newValue) {
				__S2E_MAP_ASSIGN(not_equipped)
				__S2E_MAP_ASSIGN(equipped)
				__S2E_MAP_ASSIGN(belt)
				// only gets here if none of the above were matching
				eValue = not_equipped;
			return *this;
		}
		// ---------------------------
		// (end adjustment)


		/** Constructor, initialize with c++ enum value. */
		ItemEquippedStatus(E newValue) : eValue(newValue) {}

		/** Construct directly by converting from string value. */
		ItemEquippedStatus(const std::string& newValue) {
			*this = newValue;
		}

		/** Assign the c++ enum type directly. */
		inline const ItemEquippedStatus& operator=(const E& newValue) {
			eValue = newValue;
			return *this;
		}

		/** Compare with other ItemEquippedStatus object. */
		inline bool operator==(const ItemEquippedStatus& other) const {return eValue == other.eValue;}
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
	inline std::ostream& operator <<(std::ostream& os, const ItemEquippedStatus& ps) {
		return os << static_cast<std::string>(ps);
	}



#undef __E2S_MAP
#undef __S2E_MAP_ASSIGN

} // end namespace


#endif // ItemEquippedStatus_h__
