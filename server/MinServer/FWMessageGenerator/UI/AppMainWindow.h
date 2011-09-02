#ifndef ML_AppMainWindow_h__
#define ML_AppMainWindow_h__

#include <QtGui/QMainWindow>
#include <QtCore/QModelIndex>

#include "ui_Mainwindow.h"
#include <QtSql/QSqlRelationalTableModel>
#include <QtGui/QDataWidgetMapper>

class MessageGeneratorApp;
class QSortFilterProxyModel;
class QFile;
//class QSqlDataWidgetMapper;
class QSqlRecord;
class MessageFieldDelegate;

class AppMainWindow : public QMainWindow, private Ui::MainWindow {
	Q_OBJECT
	public:
		AppMainWindow(QWidget* parent = 0);
		~AppMainWindow();

	private:
		void setupActionsAndMenus(MessageGeneratorApp* qGenApp);
		void setupUiCustom(MessageGeneratorApp* qGenApp);
		void storeSettings();
		void readSettings();
		void handleTriggerGenerateCommon(int specificMessageId);
		

		MessageGeneratorApp* qGenApp;
		//QSortFilterProxyModel* messageModel;
		QSqlRelationalTableModel* coreMessageModel;
		QSqlTableModel* fieldsModel;
		QDataWidgetMapper* mapper;
		int selectedMessageID;
		QModelIndex contextMessageIndex;
		QModelIndex selectedFieldIndex;
		QModelIndex topRowFieldIndex;
		MessageFieldDelegate* messageFieldDelegate;
		QString settingMessageIDFile;
		QString settingMessageIDClientFile;
		QString settingMessageRegistryFile;
		QString settingMessageHandlerDeclFile;
		QString settingModulePath;
		QString settingModulePrefix;
		QString settingMessageClassesPath;

		// context menus
		QAction* contextActionCloneMessage;

	// slots
	private slots:
		void handleMessageSelected(const QModelIndex&, const QModelIndex&);
		void handleMessageClicked(const QModelIndex& curSelection);
		void handleDataChanged ( const QModelIndex & topLeft, const QModelIndex & bottomRight );
		void handleSaveChanges();
		void handleTriggerAddMessage();
		void handleTriggerRemoveMessage();
		void handleTriggerRefresh();
		void handleTriggerSetFilter();
		void handleTriggerRemoveField();
		void updateFieldsTable();
		void handleTriggerAddField();
		void handleTriggerGenerate();
		void handleTriggerGenerateSingle();
		
		void handleMessageFieldEdited();
		void handleFieldTableActivated(const QModelIndex& curIndex);

		void showMessageTableContextMenu(const QPoint &position);
		void cloneContextMessage();

		void test();

		//void handleMessageInsert(int, QSqlRecord & record);
	// signals		
};

#endif // AppMainWindow_h__