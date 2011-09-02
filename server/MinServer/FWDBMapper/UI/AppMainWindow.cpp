#include "AppMainWindow.h"
#include "../FWMapperApp.h"

#include <qt/qdebug.h>
#include <QtGui/QHeaderView>

#include <map>
#include <string>

using namespace fwmapper;

AppMainWindow::AppMainWindow(QWidget* parent /* = 0 */) 
: QMainWindow(parent)
{
	setupUi(this);

	// the business logic object is owned by the applications main window
	// that ensures lifetime of gui corresponds to business logic lifetime
	qCurApp = new FWMapperApp(this);

	setupUiCustom();
	setupCommonActions();

	qCurApp->onUIReady();
	//showMaximized();
	show();

	/*
	std::map<unsigned int, std::string> stringMap;
	fwutil::Common::CountedEnumToStringMap<fwworld::AvailableStatus>(stringMap);

	for (std::map<unsigned int, std::string>::iterator it = stringMap.begin(); it !=stringMap.end(); it++) {
	std::cout << "val: " << (*it).first << " str: " << (*it).second << std::endl;

	}
	*/
}

AppMainWindow::~AppMainWindow() {
}

void AppMainWindow::setupUiCustom() {
}

void AppMainWindow::setupCommonActions() {
	// connect common signals / slots here
	connect(btConnect, SIGNAL(pressed()), this, SLOT(onTryConnect()));
	connect(btGenerate, SIGNAL(pressed()), this, SLOT(onGenerateClicked()));
	connect(this, SIGNAL(triggerGenerate(std::string, std::string)), qCurApp, SLOT(createMap(std::string, std::string)));
	connect(qCurApp, SIGNAL(schemaTablesChanged(std::vector<std::string>&)), this, SLOT(onSchemaTablesChanged(std::vector<std::string>&)));
	connect(qCurApp, SIGNAL(mappingCreated(std::string&)), this, SLOT(onMappingCreated(std::string&)));
	/*
	//connect(cbShowGrid, SIGNAL(toggled(bool)), &playfieldView, SLOT(setGridEnabled(bool)));
	connect(btAddPlayfield, SIGNAL(pressed()), this, SLOT(showPlayfieldCreateDialog()));
	connect(btRefreshPlayfields, SIGNAL(pressed()), pm, SLOT(refreshPlayfieldList()));
	connect(this, SIGNAL(playfieldCreated(SPPlayfield)), pm, SLOT(insertPlayfield(SPPlayfield)));
	connect(btLoadPlayfield, SIGNAL(pressed()), this, SLOT(requestLoadSelectedPlayfield()));
	connect(this, SIGNAL(triggerLoadSelectedPlayfield(const QModelIndex&)), pm, SLOT(loadPlayfieldFromList(const QModelIndex&)));
	connect(tvPlayfieldList, SIGNAL(activated(const QModelIndex&)), this, SLOT(loadPlayfieldInList(const QModelIndex&)));
	connect(pm, SIGNAL(playfieldLoaded(SPPlayfield)), this, SLOT(onPlayfieldLoaded(SPPlayfield)));
	connect(pm, SIGNAL(playfieldAttemptToLoadTwice(SPPlayfield)), this, SLOT(onPlayfieldAttemptToLoadTwice(SPPlayfield)));
	connect(pm, SIGNAL(playfieldHasUnsavedChanges(unsigned int)), this, SLOT(onPlayfieldHasUnsavedChanges(unsigned int)));
	connect(pm, SIGNAL(playfieldSaved(unsigned int)), this, SLOT(onPlayfieldSaved(unsigned int)));
	connect(pm, SIGNAL(notifyProgress(unsigned int, unsigned int, const QString&)), this, SLOT(showProgress(unsigned int, unsigned int, const QString&)));
	*/
}

void AppMainWindow::onSchemaTablesChanged(std::vector<std::string>& tables) {
	QStringList tList;
	cbTable->clear();
	for(std::vector<std::string>::iterator it = tables.begin(); it!=tables.end(); it++) {
		cbTable->addItem(QString::fromStdString(*it));
	}
}

void AppMainWindow::onMappingCreated(std::string& mapping) {
	teCode->clear();
	teCode->appendPlainText(QString::fromStdString(mapping));
}

void AppMainWindow::onGenerateClicked() {
	emit triggerGenerate(cbTable->currentText().toAscii().constData(), leClass->text().toAscii().constData());
}

void AppMainWindow::onTryConnect() {
	teLog->appendPlainText("Connecting to DB ...");
	gbMapping->setEnabled(false);
	std::string server = leServer->text().toAscii().constData();
	std::string user = leUser->text().toAscii().constData();
	std::string pass = lePass->text().toAscii().constData();
	std::string schema = leSchema->text().toAscii().constData();
	
	if (qCurApp->connectToDB(server, user, pass, schema)) {
		gbMapping->setEnabled(true);
		teLog->appendPlainText("Connection established.");
	} else {
		teLog->appendPlainText("Connection to server failed:\n " + QString::fromStdString(qCurApp->getLastDBError()) + ".\n");
	}
}