#ifndef FWMapperApp_h__
#define FWMapperApp_h__

#include <QtCore/QObject>
#include "UI/AppMainWindow.h"
#include "FieldInfo.h"

#include "mysql++.h"
#include <vector>

namespace fwmapper {

class FWMapperApp : public QObject {
	// derived from QObject since the business logic will need to handle
	// signals from the gui
	Q_OBJECT


public:
	FWMapperApp(AppMainWindow* guiParent);
	~FWMapperApp();

	bool connectToDB(const std::string& server, const std::string& user, const std::string& pass, const std::string& schema);
	void onUIReady();
	std::string getLastDBError();


	public slots:
		std::string createMap(std::string table, std::string className);


private:

	bool init();
	void FWMapperApp::connectUI();
	void clearConnection();
	std::vector<std::string> FWMapperApp::getSchemaTables();
	std::vector<FieldInfo> columnInfo;

	AppMainWindow* appMainWindow;

	/** Connection object to actually work on the database level. */
	mysqlpp::Connection* conn;

	std::string lastDBError;

signals:
	void schemaTablesChanged(std::vector<std::string>& tables);
	void mappingCreated(std::string& mapping);
	
};

}

#endif
