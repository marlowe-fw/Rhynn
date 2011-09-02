#include "UI/AppMainWindow.h"
#include <QtGui/QApplication>

int main(int argc, char* argv[]) {
	QApplication app(argc, argv);
	AppMainWindow amw(0);
	amw.show();
	return app.exec();
}