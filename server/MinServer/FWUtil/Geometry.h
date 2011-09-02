#ifndef Geometry_h__
#define Geometry_h__

namespace fwutil {

class Geometry {
public:
	static bool isWithinRadius(int obj_x, int obj_y, int cmp_x, int cmp_y, unsigned int radius) {
		int xDist = obj_x-cmp_x;
		int yDist = obj_y-cmp_y;
		return (unsigned int)(xDist*xDist+yDist*yDist) <= radius*radius;
	}


};

}

#endif // Geometry_h__