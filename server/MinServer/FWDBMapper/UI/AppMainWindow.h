#ifndef AppMainWindow_h__
#define AppMainWindow_h__

#include <QtGui/QMainWindow>

#include "ui_Mainwindow.h"
#include <vector>

namespace fwmapper {

	class FWMapperApp;

	class AppMainWindow : public QMainWindow, private Ui::MainWindow {
		Q_OBJECT
	public:
		AppMainWindow(QWidget* parent = 0);
		~AppMainWindow();

	protected:
		void setupCommonActions();
		void setupUiCustom();
		void setupPlayfieldTabContainer();

	private:
		FWMapperApp* qCurApp;

		public slots:
			void onTryConnect();
			void onSchemaTablesChanged(std::vector<std::string>& tables);
			void onMappingCreated(std::string& mapping);
			void onGenerateClicked();
		signals:
			void triggerGenerate(std::string tableName, std::string className);

	};


}
#endif // AppMainWindow_h__
