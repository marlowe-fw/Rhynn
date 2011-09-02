#ifndef Rect_h__
#define Rect_h__

namespace fwutil {

class Rect {
public:
	int x1;
	int x2;
	int y1;
	int y2;


	Rect()
	:x1(0), x2(0), y1(0), y2(0)
	{}

	Rect(int x1, int y1, int x2, int y2)
	:x1(x1), x2(x2), y1(y1), y2(y2)
	{}

	virtual ~Rect() {}

	inline bool intersectsWith(const Rect& other) const {
		return
			!(
				(x1 < other.x1 && x2 < other.x1) || (x1 > other.x2 && x2 > other.x2)
				||
				(y1 < other.y1 && y2 < other.y1) || (y1 > other.y2 && y2 > other.y2)
			);
	}

	inline int width() const {
		if (x1 > x2) return x1-x2;
		else return x2-x1;
	}

	inline int height() const {
		if (y1 > y2) return y1-y2;
		else return y2-y1;
	}

	bool operator==(const Rect& other) const {
		return
			x1 == other.x1 && x2 == other.x2 &&
			y1 == other.y1 && y2 == other.y2;
	}

	bool empty() const;

	void print();

};

}

#endif // Rect_h__
