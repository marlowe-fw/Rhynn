#ifndef AppGenerateDialog_h__
#define AppGenerateDialog_h__

#include <QtGui/QDialog>
#include "ui_GenerateDialog.h"

class AppGenerateDialog : public QDialog, private Ui::GenerateDialog {
	Q_OBJECT

	public:
		AppGenerateDialog(QWidget* parent = 0);
		~AppGenerateDialog();
		inline QString getMessageIDFile() {return messageIDFile;}
		inline QString getMessageIDClientFile() {return messageIDClientFile;}
		inline QString getMessageRegistryFile() {return messageRegistryFile;}
		inline QString getModulePath() {return modulePath;}
		inline QString getModulePrefix() {return modulePrefix;}
		inline QString getMessageClassesPath() {return messageClassesPath;}
		
		void presetFileEntries(QString IDFile, QString IDClientFile, QString registryFile, QString modulePath, QString modulePrefix, QString classesPath);
		QString chooseFile(QString& curFile);
		QString choosePath(QString& curPath);
		void chooseFileForLineEdit(QLineEdit* le);		
		void choosePathForLineEdit(QLineEdit* le);


	private:
		QString messageIDFile;
		QString messageIDClientFile;
		QString messageRegistryFile;
		QString messageClassesPath;
		QString modulePath;
		QString modulePrefix;

	private slots:
		void handleTriggerGenerate();
		void handleTriggerCancel();
		void handleChooseMessageIDFile();
		void handleChooseMessageIDClientFile();
		void handleChooseMessageRegistryFile();
		void handleChooseModulePath();
		void handleChooseMessageHandlerClassesPath();
};

#endif // AppGenerateDialog_h__