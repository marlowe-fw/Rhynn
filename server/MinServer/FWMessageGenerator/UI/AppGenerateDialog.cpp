#include "AppGenerateDialog.h"
#include <QtCore/QFile>
#include <QtCore/QDir>
#include <QtGui/QMessageBox>
#include <QtGui/QFileDialog>

AppGenerateDialog::AppGenerateDialog(QWidget* parent) : QDialog(parent) {
	setupUi(this);

	connect(btnGenerate, SIGNAL(clicked()), this, SLOT(handleTriggerGenerate()));
	connect(btnCancel, SIGNAL(clicked()), this, SLOT(handleTriggerCancel()));
	connect(btnBrowseID, SIGNAL(clicked()), this, SLOT(handleChooseMessageIDFile()));
	connect(btnBrowseID_Client, SIGNAL(clicked()), this, SLOT(handleChooseMessageIDClientFile()));
	connect(btnBrowseReg, SIGNAL(clicked()), this, SLOT(handleChooseMessageRegistryFile()));
	connect(btnBrowseModulePath, SIGNAL(clicked()), this, SLOT(handleChooseModulePath()));
	connect(btnBrowseClassesPath, SIGNAL(clicked()), this, SLOT(handleChooseMessageHandlerClassesPath()));
}

AppGenerateDialog::~AppGenerateDialog() {

}

void AppGenerateDialog::presetFileEntries(QString IDFile, QString IDClientFile, QString newRegistryFile, QString newModulePath, QString newModulePrefix, QString newClassesPath) {
	messageIDFile = IDFile;
	messageIDClientFile = IDClientFile;
	messageRegistryFile = newRegistryFile;
	modulePath = newModulePath;
	modulePrefix = newModulePrefix;
	messageClassesPath = newClassesPath;

	leMessageID->setText(messageIDFile);
	leMessageID_Client->setText(messageIDClientFile);
	leMessageReg->setText(messageRegistryFile);
	leModulePath->setText(modulePath);
	leModulePrefix->setText(modulePrefix);
	leMessageClassesPath->setText(messageClassesPath);
}

QString AppGenerateDialog::chooseFile(QString& curFile) {
	QString path;
	if (QFile::exists(curFile)) {
		QDir dir;
		path = dir.absoluteFilePath(curFile);
	}
	
	return QFileDialog::getOpenFileName(this, tr("Choose File"), path, tr("c++ files (*.h *.cpp);;All files (*.*)"));
}

QString AppGenerateDialog::choosePath(QString& curPath) {
	QString path;
	if (QFile::exists(curPath)) {
		QDir dir;
		path = dir.absoluteFilePath(curPath);
	}
	return QFileDialog::getExistingDirectory(this, tr("Open Directory"), path, QFileDialog::ShowDirsOnly | QFileDialog::DontResolveSymlinks);
}

void AppGenerateDialog::chooseFileForLineEdit(QLineEdit* le) {
	QString newFile = chooseFile(le->text());
	if (!newFile.isEmpty()) {
		le->setText(newFile);
	}
}

void AppGenerateDialog::choosePathForLineEdit(QLineEdit* le) {
	QString newPath = choosePath(le->text());
	if (!newPath.isEmpty()) {
		le->setText(newPath);
	}
}


void AppGenerateDialog::handleChooseMessageIDFile() {
	chooseFileForLineEdit(leMessageID);
}

void AppGenerateDialog::handleChooseMessageIDClientFile() {
	chooseFileForLineEdit(leMessageID_Client);
}

void  AppGenerateDialog::handleChooseMessageRegistryFile() {
	chooseFileForLineEdit(leMessageReg);
}

void  AppGenerateDialog::handleChooseModulePath() {
	choosePathForLineEdit(leModulePath);
}

void AppGenerateDialog::handleChooseMessageHandlerClassesPath() {
	choosePathForLineEdit(leMessageClassesPath);
}


void AppGenerateDialog::handleTriggerCancel() {
	reject();
}

void AppGenerateDialog::handleTriggerGenerate() {
	bool valid = false;
	
	// check valid files
	messageIDFile = leMessageID->text();
	messageIDClientFile = leMessageID_Client->text();
	messageRegistryFile = leMessageReg->text();
	modulePath = leModulePath->text();
	modulePrefix = leModulePrefix->text();
	messageClassesPath = leMessageClassesPath->text();
	
	if (!QFile::exists(messageIDFile)) {
		QMessageBox::critical(this, tr("Invalid File"), tr("The message ID file is not valid!"));
	} else if (!messageIDClientFile.isEmpty() && !QFile::exists(messageIDClientFile)) {
		QMessageBox::critical(this, tr("Invalid File"), tr("The message ID file for the client is not valid! You may leave it blank for no message generation on the client application."));
	} else if (!QFile::exists(messageRegistryFile)) {
		QMessageBox::critical(this, tr("Invalid File"), tr("The message registry file is not valid!"));
	} else if (!QFile::exists(modulePath)) {
		QMessageBox::critical(this, tr("Invalid Path"), tr("The module path is not valid!"));
	} else if (!QFile::exists(messageClassesPath)) {
		QMessageBox::critical(this, tr("Invalid Path"), tr("The path for the message classes is not valid!"));
	} else {
		accept();
	}
}