#include "Rect.h"
#include <iostream>

using namespace fwutil;

bool Rect::empty() const {
	return x1==x2 && y1==y2;
}

void Rect::print() {
	std::cout << "Rect: " << x1 << "," << y1 << " - " << x2 << "," << y2 << std::endl;
}