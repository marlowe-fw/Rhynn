#ifndef Direction_h__
#define Direction_h__


namespace fwworld {

class Direction {
private:
	unsigned int val;

	Direction() {};

public:

	static const Direction UP;
	static const Direction RIGHT;
	static const Direction DOWN;
	static const Direction LEFT;

	virtual ~Direction() {}

	inline unsigned int intVal() {return val;}

	Direction(unsigned int newVal) : val(newVal) {}

	Direction(const Direction& other) {
		val = other.val;
	}

	const Direction& operator=(const Direction& other) {
		if (&other == this) {
			return *this;
		}
		val = other.val;
		return *this;
	}


};

}


#endif // Direction_h__