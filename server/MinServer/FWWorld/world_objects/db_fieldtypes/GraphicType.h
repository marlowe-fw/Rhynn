#ifndef GraphicType_h__
#define GraphicType_h__

#include <iostream>
#include <string>

namespace fwworld {

// macros for simple enum to string and string to enum conversion
#define __E2S_MAP(mcEnumVal) case mcEnumVal: return #mcEnumVal;
#define __S2E_MAP_ASSIGN(mcEnumVal) if (newValue ==  #mcEnumVal ) {eValue = mcEnumVal; return *this;}

	class GraphicType {

	public:
		// only need to adjust below:
		// ---------------------------
		enum E {generic, background, character, item, __count};

		/** Conversion to string. */
		operator std::string() const {
			switch (eValue) {
				__E2S_MAP(generic)
				__E2S_MAP(background)
				__E2S_MAP(character)
				__E2S_MAP(item)
                default:break;
			}
			// only gets here if none of the above were matching
			return "generic";
		}

		/** Assignment from string. */
		inline const GraphicType& operator=(const std::string& newValue) {
			__S2E_MAP_ASSIGN(generic)
			__S2E_MAP_ASSIGN(background)
			__S2E_MAP_ASSIGN(character)
			__S2E_MAP_ASSIGN(item)
			// only gets here if none of the above were matching
			eValue = generic;
			return *this;
		}
		// ---------------------------
		// (end adjustment)


		/** Constructor, initialize with c++ enum value. */
		GraphicType(E newValue) : eValue(newValue) {}

		/** Construct directly by converting from string value. */
		GraphicType(const std::string& newValue) {
			*this = newValue;
		}

		/** Assign the c++ enum type directly. */
		inline const GraphicType& operator=(const E& newValue) {
			eValue = newValue;
			return *this;
		}

		/** Compare with other GraphicType object. */
		inline bool operator==(const GraphicType& other) const {return eValue == other.eValue;}
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
	inline std::ostream& operator <<(std::ostream& os, const GraphicType& ps) {
		return os << static_cast<std::string>(ps);
	}



#undef __E2S_MAP
#undef __S2E_MAP_ASSIGN

} // end namespace


#endif // GraphicType_h__
