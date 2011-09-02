#include "UI/AppMainWindow.h"
#include <QtGui/QApplication>

#include <iostream>

using namespace fwmapper;


int main(int argc, char* argv[]) {
	QApplication app(argc, argv);
	AppMainWindow amw(0);
	amw.show();
	return app.exec();
}
