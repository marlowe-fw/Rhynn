#ifndef PlayfieldCellFunction_h__
#define PlayfieldCellFunction_h__

namespace fwworld {

class PlayfieldCellFunction {

public:
	static const int function_none = 0x0;
	static const int function_blocked = 0x1;
	static const int function_peaceful = 0x2;
	static const int function_reserved1 = 0x4;
	static const int function_reserved2 = 0x8;

	static const int triggerfunction_none = 0x0;
	static const int triggerfunction_default = 0x1;


	PlayfieldCellFunction(int mainFunctionVal, int triggerFunctionVal) 
		: mainVal(mainFunctionVal), triggerVal(triggerFunctionVal)
	{}

	PlayfieldCellFunction(int intValue) {
		fromInt(intValue);
	}

	inline void fromInt(int intValue) {
		mainVal = intValue & 0x0F;
		triggerVal = ((intValue & 0xF0) >> 4);
	}

	inline int toInt() const {
		return (mainVal & 0x0F) | ((triggerVal & 0x0F) << 4);
	}

	inline void setMainAndTriggerFunction(int mainFunctionVal, int triggerFunctionVal) {
		mainVal = mainFunctionVal;
		triggerVal = triggerFunctionVal;
	}

	inline void setMainFunction(int mainFunctionVal) {
		mainVal = mainFunctionVal;
	}

	inline void addMainFunction(int functionVal) {
		mainVal |= functionVal;
	}

	inline void removeMainFunction(int functionVal) {
		mainVal &= ~functionVal;
	}


	inline void setTriggerFunction(int triggerFunctionVal) {
		triggerVal = triggerFunctionVal;
	}

	inline bool hasMainFunction(int fVal) {
		return (mainVal & fVal) == fVal;
	}

	inline bool hasTriggerFunction(int fVal) {
		return (triggerVal & fVal) == fVal;
	}


private:
	int mainVal;
	int triggerVal;

};

}

#endif // PlayfieldCellFunction_h__