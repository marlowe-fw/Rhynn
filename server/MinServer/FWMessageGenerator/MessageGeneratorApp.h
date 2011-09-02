#ifndef MessageGeneratorApp_h__
#define MessageGeneratorApp_h__

#include <QtCore/QObject>
#include <QtSql/QSqlRelationalTableModel>

class QFile;
class QStringList;


class MessageGeneratorApp : public QObject {
	// derived from QObject since the business logic will need to handle
	// signals from the gui
	Q_OBJECT
	public:
		MessageGeneratorApp(QObject* guiParent);
		~MessageGeneratorApp();

		bool connectToDB(QString host, QString username, QString password, QString databaseName);
		inline QSqlDatabase& getDb() {return db;}

		void generateFilesFromMessages(int specificMessageId, QString messageIDFile, QString messageIDClientFile, QString messageRegistryFile, QString messageHandlerImplPath, QString messageHandlerImplPrefix, QString messageClassesPath);

	private:
		QSqlDatabase db;

		QString camelizeByUnderScores(const QString& input);
		QString camelizeByDelimiter(const QString& input, const char delimiter);

		void splitFileContentsByTag(QFile& srcFile, const QString& startTag, const QString& endTag, QStringList& splitParts);
		void insertToFile(QFile& srcFile, const QString& startTag, const QString& endTag, const QString& insert, bool append = false);		
		QString extractTagContentsFromFile(QFile& srcFile, const QString& startTag, const QString& endTag);

		QString getMessageClassImpl(int messageId, const QString& camelizedMessageName, const QString& msgIdConstant, const QString& classDescription, const QString& messagesClassPath, bool longMessage);
		QStringList getFieldStatements(const QString& fieldName, const QString& fieldType, const QString& fieldDescription, int fieldLength, const QString& fieldLengthStr, int maxFieldLength, const QString& indexStr, const QString& lastFieldName, int fieldCounter, int initialFieldOffset, bool curFieldHasDynamicLength);
		void getExistingHandlerImplNames(QFile& fileImpl, QStringList& handlers);
		void getAllMessageHandlerImplementationNames(QString handlerImplPath, QString handlerPrefix, QStringList& allHandlerNames);
		QString getModuleImplSceleton(QString fullModuleName, QString handlerImplementations);
		QString getModuleDeclSceleton(QString fullModuleName, QString contentMessageIncludes, QString handlerDeclarations);
};

#endif // MessageGeneratorApp_h__
