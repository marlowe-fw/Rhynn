#include "MessageGeneratorApp.h"

#include <QtCore/QVariant>
#include <QtCore/QDebug>
#include <QtCore/QString>
#include <QtCore/QIODevice>
#include <QtCore/QFile>
#include <QtCore/QTextStream>
#include <QtCore/QRegExp>
#include <QtCore/QList>

#include <iostream>
#include <Windows.h>

#include <QtSql/QSqlDatabase>
#include <QtSql/QSqlError>
#include <QtSql/QSqlQuery>
#include <QtSql/QSqlRecord>
#include <QtSql/QSqlTableModel>
#include <QtSql/QSqlRelation>

#include <QtGui/QApplication>
#include <QtGui/QTableView>


MessageGeneratorApp::MessageGeneratorApp(QObject* guiParent) : QObject(guiParent) {
}

MessageGeneratorApp::~MessageGeneratorApp() {
	if (db.isOpen()) {
		db.close();
	}
}

bool MessageGeneratorApp::connectToDB(QString host, QString username, QString password, QString databaseName) {
	db = (QSqlDatabase::addDatabase("QMYSQL"));
	
	db.setHostName(host);
	db.setDatabaseName(databaseName);
	db.setUserName(username);
	db.setPassword(password);

	if (!db.open()) {
		qDebug() << db.lastError();
		return false;
	} else {
		std::cout << "connection to db is up." << std::endl;
		return true;
	}
}

/**
 * Cut the given file into 3 parts:
 * 1. Content up to the start tag (including the start tag)
 * 2. Content between start and end tag
 * 3. Content from the end tag onwards (including the end tag)
 */
void MessageGeneratorApp::splitFileContentsByTag(QFile& srcFile, const QString& startTag, const QString& endTag, QStringList& splitParts) {
	bool opened = false;
	if (!srcFile.isOpen()) {
		if (!srcFile.open(QIODevice::ReadWrite|QIODevice::Text)) {
			splitParts.append("");
			splitParts.append("");
			splitParts.append("");
			return;
		} else {
			opened = true;
		}
	}

	QTextStream in(&srcFile);
	in.seek(0);

	QString line;
	QString contentsBefore = "";
	QString contentsAfter = "";
	QString contentsTag = "";
	int writePart = 0;
	
	while (!in.atEnd()) {
		line = in.readLine();
		
		if  (!in.atEnd()) {
			line +=  "\n";
		}
		

		switch (writePart) {
			case 0:
				// content before start tag
				contentsBefore += line;
				if (line.contains(startTag)) {
					// found start tag, now comes the content between start tag and end tag
					writePart = 1;
				}
				break;
			case 1:
				// content between start tag and end tag
				if (line.contains(endTag)) {
					// found end tag, now comes the content after the end tag
					contentsAfter += line;
					writePart = 2;
				} else {			
					contentsTag += line;
				}
				break;
			case 2:
				contentsAfter += line;
				break;
		}		
	}
	
	splitParts.append(contentsBefore);
	splitParts.append(contentsTag);
	splitParts.append(contentsAfter);

	if (opened) {
		srcFile.close();
	}
}

/**
 * Insert given content to a file, insert between the tags provided.
 */
void MessageGeneratorApp::insertToFile(QFile& srcFile, const QString& startTag, const QString& endTag, const QString& insert, bool append /*=false*/) {
	// simple write content with tags if file does not yet exist
	QString newContent = "";
	
	if (!srcFile.exists()) {
		newContent = "// " + startTag + "\n\n" + insert + "\n\n// " + endTag + "\n";
		srcFile.open(QIODevice::WriteOnly | QIODevice::Text | QIODevice::Truncate);
		QTextStream out(&srcFile);
		out << newContent; 
		srcFile.close();
		return;
	}
	
	QStringList splitParts;
	
	splitFileContentsByTag(srcFile, startTag, endTag, splitParts);

	srcFile.resize(0);
	
	newContent = splitParts.at(0);
	if (append) {
		// if we should append, be sure to include current content between tags
		newContent += splitParts.at(1);
	}
	newContent += insert + splitParts.at(2);

	QTextStream out(&srcFile);
	out.seek(0);
	out << newContent; 
}

/**
* Retrieve content from a file which is between the tags provided.
*/
QString MessageGeneratorApp::extractTagContentsFromFile(QFile& srcFile, const QString& startTag, const QString& endTag) {
	QStringList splitParts;
	splitFileContentsByTag(srcFile, startTag, endTag, splitParts);
	QString tagContents = splitParts.at(1);
	return tagContents;
}

QString MessageGeneratorApp::getModuleImplSceleton(QString fullModuleName, QString handlerImplementations) {
	QString content = "#include \"" + fullModuleName +".h\"\n#include \"FWClient.h\"\n#include \"FWServer.h\"\n#include \"FWWorld.h\"\n";
	content +=
		"\n\nusing namespace fws;\nusing namespace fwworld;\nusing namespace fwutil;\n\n";
	content +=
		fullModuleName + "::" + fullModuleName + "(FWServer& newServer, FWWorld& newWorld) \n"
		+": server(newServer), world(newWorld)\n"
		+"{}\n\n";
	content += "// %%GENERATOR_START%%HANDLER_IMPL%%\n" + handlerImplementations + "\n\n// %%GENERATOR_END%%HANDLER_IMPL%%\n";
	
	return content;
}

QString MessageGeneratorApp::getModuleDeclSceleton(QString fullModuleName, QString contentMessageIncludes, QString handlerDeclarations) {
	QString content = 
			"#ifndef " + fullModuleName +  "_h__\n"
		+	"#define " + fullModuleName +  "_h__\n\n"
		+	"// %%GENERATOR_START%%MESSAGE_INCLUDES%%\n"
		+	contentMessageIncludes
		+	"// %%GENERATOR_END%%MESSAGE_INCLUDES%%\n"
		+ "\n\nnamespace fwworld {\n"
		+ "\tclass FWWorld;\n"
		+"}\n\n"
		+"using namespace fwworld;\n\n"
		+"namespace fws {\n\n"
		+"class FWServer;\n"
		+"class FWClient;\n\n"
		+"class " + fullModuleName +  " {\n\n"
		+"public:\n"
			+ "\t" + fullModuleName +  "(FWServer& server, FWWorld& world);\n\n"
			+ "\t" + "// %%GENERATOR_START%%HANDLER_DECL%%\n"
			+ "\t" + handlerDeclarations
			+ "\t" + "// %%GENERATOR_END%%HANDLER_DECL%%\n\n"
		+"private:\n"
			+ "\t" + "FWServer& server;\n"
			+ "\t" + "FWWorld& world;\n"
		+"};\n\n"
	+"}\n\n"
	+"#endif // " + fullModuleName + "_h__\n";

	return content;
}


/**
* Extract the names of all implemented message handler routines (to prevent them from being overwritten later).
*/
void MessageGeneratorApp::getAllMessageHandlerImplementationNames(QString modulePath, QString modulePrefix, QStringList& allHandlerNames) {

	QString sql = "SELECT * from modules ORDER BY module_name ASC";

	QSqlQuery query(sql);
	QSqlRecord record = query.record();
	while (query.next()) {
		QString moduleName = query.value(record.indexOf("module_name")).toString();
		moduleName = camelizeByDelimiter(moduleName, ' ');
		QString filenameCPP = modulePath + "/" + modulePrefix + moduleName + ".cpp";
		QFile fileImpl(filenameCPP);
		if (!fileImpl.exists()) {
			// add module implementation file if it doesn't exist yet
			QString content = getModuleImplSceleton(modulePrefix + moduleName, "");
			fileImpl.open(QIODevice::WriteOnly | QIODevice::Text);
			QTextStream out(&fileImpl);
			out << content; 
			fileImpl.close();
		} else {
			getExistingHandlerImplNames(fileImpl, allHandlerNames);	
		}
	}
}


/**
 * Extract the names of the implemented message handler routines in a given file (to prevent them from being overwritten later).
 */
void MessageGeneratorApp::getExistingHandlerImplNames(QFile& fileImpl, QStringList& handlers) {
	bool opened = false;
	if (!fileImpl.isOpen()) {
		if (!fileImpl.open(QIODevice::ReadWrite|QIODevice::Text)) {
			return;
		} else {
			opened = true;
		}
	}

	QTextStream in(&fileImpl);
	in.seek(0);
	QString line;	
	QRegExp rx("[\\s]*bool.*::(.*)\\(.*\\)");
	while (!in.atEnd()) {
		line = in.readLine();
		int pos = rx.indexIn(line);
		if (pos > -1) {
			QString handlerName = rx.cap(1);
			handlers.append(handlerName);
			//qDebug() << "found " << handlerName;
		} 
	}

	if (opened) {
		fileImpl.close();
	}
}

/**
 * Put a string into camel case, using the given character as the separation mark.
 * E.g. an input of new_air_wave results in NewAirWave.
 */
QString MessageGeneratorApp::camelizeByDelimiter(const QString& input, const char delimiter) {
	QString camelizedName = "";
	QStringList nameParts = input.split(delimiter);
	for (int i=0; i<nameParts.size(); i++) {
		camelizedName += nameParts.at(i).left(1).toUpper() + nameParts.at(i).mid(1);
	}

	return camelizedName;
}



/**
* Do actual code generation.
* @param specificMessageId If > 0 will generate code only for given message, otherwise for all existing messages - not yet working needs adjustment
*/
void MessageGeneratorApp::generateFilesFromMessages(int specificMessageId, QString messageIDFile, QString messageIDClientFile, QString messageRegistryFile, QString modulePath, QString modulePrefix, QString messageClassesPath) {	
	
	if (db.isOpen()) {
		QString textID;
		QString textIDClient;
		QString textReg;
		QString textHandlerMessageIncludes;
		QString textHandlerDecl;
		QString textHandlerImpl;
		QHash<QString, QString> textClassImplHash;

		QHash<QString, QVector<QString>> textHandlerHash;


		// get available handler implementation (function names)
		QStringList handlers;
		getAllMessageHandlerImplementationNames(modulePath, modulePrefix, handlers);

		QString sql = "SELECT messages.id, modules.module_name, messages.symbolic_id, messages.symbolic_name, messages.description, messages.generate_handler, messages.long_message FROM messages LEFT JOIN categories on categories.id = messages.category_id LEFT JOIN modules on modules.id = messages.group_id";
		if (specificMessageId > 0) {
			sql += " WHERE messages.id = " + QString::number(specificMessageId);
		}
		sql +=  " ORDER BY modules.module_name, messages.category_id ASC, symbolic_id ASC";

		QSqlQuery query(sql);
		QSqlRecord record = query.record();
		while (query.next()) {
			int recordID = query.value(record.indexOf("id")).toInt();
			QString moduleName = query.value(record.indexOf("module_name")).toString();
			moduleName = camelizeByDelimiter(moduleName, ' ');
			QString fullModuleName = modulePrefix + moduleName;
			int symbolicID = query.value(record.indexOf("symbolic_id")).toInt();			
			QString symbolicName = query.value(record.indexOf("symbolic_name")).toString();
			QString description = query.value(record.indexOf("description")).toString();
			QString sGenerateHandler = query.value(record.indexOf("generate_handler")).toString();
			bool generateHandler = false;
			if (sGenerateHandler == "yes") {
				generateHandler = true;
			}
			QString sLongMessage = query.value(record.indexOf("long_message")).toString();
			bool longMessage = (sLongMessage == "yes");

			QString varMsgID = "MSGID_" + symbolicName.toUpper();
			QString camelizedName = camelizeByDelimiter(symbolicName, '_');
			QString handlerName = "handleMessage" + camelizedName; // also the class name

			// message ids declaration
			textID += "\t\tstatic const unsigned int " + varMsgID + " = " + QString::number(symbolicID) + ";\n";
			textIDClient += "\tpublic static final int " + varMsgID + " = " + QString::number(symbolicID) + ";\n";

			// message class implementation
			QString messageClassName = "Msg" + camelizedName;
			QString messageClassImpl = getMessageClassImpl(recordID, messageClassName, varMsgID, description, messageClassesPath, longMessage);
			textClassImplHash.insert("Msg" + camelizedName, messageClassImpl);

			if (generateHandler) {
				// message handler registry
				textReg += "\tmessageHandlers[FWSMessageIDs::" + varMsgID + "] = _TRACK_NEW((new MessageHandler<"+ fullModuleName +", " + "Msg" + camelizedName + ">(server"+fullModuleName+", &"+fullModuleName+"::" + handlerName + ")));\n";
				// message handler declaration

				textHandlerMessageIncludes = "#include \"messages/Msg" + camelizedName + ".h\"\n";
				textHandlerDecl = "\tbool handleMessage" + camelizedName + "(FWClient* pCurClient, Msg" + camelizedName + "& msg);\n";

				// message handler implementation, add commented code to demonstrate use of the message
				if (handlers.contains(handlerName)) {
					textHandlerImpl = "";
				} else {
					textHandlerImpl = "\n/**\n * " + description + ".\n */";
					textHandlerImpl += "\nbool " + fullModuleName + "::" + handlerName + "(FWClient* pCurClient, Msg" + camelizedName + "& msg) {";
					textHandlerImpl += "\n\t//queueMessageForSending(returnMsg, pCurClient, true)";
					textHandlerImpl += "\n\t\n\treturn true;\n}\n";
				}
				// append to proper handler for the given module -> to insert to the correct handler module file
				if (textHandlerHash.contains(moduleName)) {
						QHash<QString, QVector<QString>>::iterator i = textHandlerHash.find(moduleName);
						i.value()[0] += textHandlerMessageIncludes;
						i.value()[1] += textHandlerDecl;
						i.value()[2] += textHandlerImpl;
						
				} else {
					QVector<QString> v; 
					v.push_back(textHandlerMessageIncludes); v.push_back(textHandlerDecl); v.push_back(textHandlerImpl);
					textHandlerHash.insert(moduleName, v);
				}
			}

		}

		// write to files
		QFile fileID(messageIDFile);
		if (fileID.open(QIODevice::ReadWrite|QIODevice::Text)) {
			insertToFile(fileID, QString("%%GENERATOR_START%%MSG_IDS%%"), QString("%%GENERATOR_END%%MSG_IDS%%"), textID);
			fileID.close();
		}

		QFile fileIDClient(messageIDClientFile);
		if (fileIDClient.open(QIODevice::ReadWrite|QIODevice::Text)) {
			insertToFile(fileIDClient, QString("%%GENERATOR_START%%MSG_IDS%%"), QString("%%GENERATOR_END%%MSG_IDS%%"), textIDClient);
			fileIDClient.close();
		}


		QFile fileReg(messageRegistryFile);
		if (fileReg.open(QIODevice::ReadWrite|QIODevice::Text)) {
			insertToFile(fileReg, QString("%%GENERATOR_START%%REGISTER_HANDLERS%%"), QString("%%GENERATOR_END%%REGISTER_HANDLERS%%"), textReg);
			fileReg.close();
		}
		/*
		QFile fileDecl(messageHandlerDeclFile);
		if (fileDecl.open(QIODevice::ReadWrite|QIODevice::Text)) {
			insertToFile(fileDecl, QString("%%GENERATOR_START%%HANDLER_DECL%%"), QString("%%GENERATOR_END%%HANDLER_DECL%%"), textHandlerDecl);
			fileDecl.close();
		}
		*/
		QHashIterator<QString, QVector<QString>> ih(textHandlerHash);
		while (ih.hasNext()) {
			ih.next();
			QString moduleName = ih.key();
			QString filenameCPP = modulePrefix + moduleName + ".cpp";
			QString filenameH = modulePrefix + moduleName + ".h";
			
			//qDebug() << "would open for write: " << filenameCPP;
			QVector<QString> contentV = ih.value();

			QString messageIncludes = contentV[0];
			QString handlerDeclarations = contentV[1];
			QString handlerImplementations = contentV[2];
			QFile outFileCPP(modulePath + "/" + filenameCPP);
			
			if (!outFileCPP.exists()) {
				// add tags if file does not exist yet
				QString fullContentCPP = getModuleImplSceleton(modulePrefix + moduleName, handlerImplementations);
				outFileCPP.open(QIODevice::WriteOnly | QIODevice::Text | QIODevice::Truncate);
				QTextStream out(&outFileCPP);
				out << fullContentCPP; 
				outFileCPP.close();
			} else {
				outFileCPP.open(QIODevice::ReadWrite|QIODevice::Text);
				insertToFile(outFileCPP, QString("%%GENERATOR_START%%HANDLER_IMPL%%"), QString("%%GENERATOR_END%%HANDLER_IMPL%%"), handlerImplementations, true);
				outFileCPP.close();
			}

			QFile outFileH(modulePath + "/" + filenameH);
			if (!outFileH.exists()) {
				// add module class sceleton if file does not exist yet
				QString fullContent = getModuleDeclSceleton(modulePrefix + moduleName, messageIncludes, handlerDeclarations);
				outFileH.open(QIODevice::WriteOnly | QIODevice::Text | QIODevice::Truncate);
				QTextStream out(&outFileH);
				out << fullContent; 
				outFileH.close();
			} else {
				outFileH.open(QIODevice::ReadWrite|QIODevice::Text);
				insertToFile(outFileH, QString("%%GENERATOR_START%%MESSAGE_INCLUDES%%"), QString("%%GENERATOR_END%%MESSAGE_INCLUDES%%"), messageIncludes);
				insertToFile(outFileH, QString("%%GENERATOR_START%%HANDLER_DECL%%"), QString("%%GENERATOR_END%%HANDLER_DECL%%"), handlerDeclarations);
				outFileH.close();
			}

			/*
			QFile fileDecl(messageHandlerDeclFile);
			if (fileDecl.open(QIODevice::ReadWrite|QIODevice::Text)) {
				insertToFile(fileDecl, QString("%%GENERATOR_START%%HANDLER_DECL%%"), QString("%%GENERATOR_END%%HANDLER_DECL%%"), textHandlerDecl);
				fileDecl.close();
			}*/


			//QTextStream (MessageClassesPath + "/" + filenameCPP, QIODevice::ReadWrite);
			//cout << i.key() << ": " << i.value() << endl;
		} 


		QHashIterator<QString, QString> ic(textClassImplHash);
		while (ic.hasNext()) {
			ic.next();
			QString filenameC = ic.key() + ".h";
			QString content = ic.value();
			QFile classFile(messageClassesPath + "/" + filenameC);
			classFile.open(QIODevice::WriteOnly | QIODevice::Text | QIODevice::Truncate);
			QTextStream out(&classFile);
			out << content; 
			classFile.close();
			//QTextStream (MessageClassesPath + "/" + filenameCPP, QIODevice::ReadWrite);

			//cout << i.key() << ": " << i.value() << endl;
		} 

	}

}

/** 
 * For the given message (identified by its id, get all fields and generate the data class
 * (which include the toBytes and fromBytes methods and data members)
 */
QString MessageGeneratorApp::getMessageClassImpl(int messageId, const QString& camelizedMessageName, const QString& msgIdConstant, const QString& classDescription, const QString& messagesClassPath, bool longMessage) {
	// note that we use \n instead of \r\n because we will write the file in text mode and Qt will adapt newline to OS settings
	
	QString classImpl = "";	
	QString innerImpl = "";
	QString initialization = "";
	QString fromBytes = "";
	QString toBytes = "";
	QString toBytesAssignLengthFields = "";
	QString toBytesCalculateLength = "";
	QString validateLengthCode = "";
	QString additionalIncludes = "";


	// existing user defined handlerImplementations should be preserved, so extract it from the existing file first (if any)
	QString userHeaders = "";
	QString userContent = "";		
	QString classFileName = messagesClassPath + "/" + camelizedMessageName + ".h";

	if (QFile::exists(classFileName)) {
		// get user headers and user contents
		QFile f(classFileName);
		if (f.open(QIODevice::ReadWrite|QIODevice::Text)) {			
			userHeaders = extractTagContentsFromFile(f, "%%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%", "%%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%");
			userContent = extractTagContentsFromFile(f, "%%GENERATOR_START%%MSG_IMPL_USER_CONTENT%%", "%%GENERATOR_END%%MSG_IMPL_USER_CONTENT%%");		
			f.close();
		}
	}

	QSqlQuery fieldQuery = "SELECT * FROM message_fields where message_id = " + QString::number(messageId) + " order by sequence_order ASC";
	QSqlRecord fieldRecord = fieldQuery.record();

	QString lengthFieldName = "length";
	QString msgIdFieldName = "msgId";
	int initialFieldOffset = 0;
	bool needsIOStream = false;
	QString fieldType = "";
	QString fieldName = "";
	QString fieldDescription = "";
	int fieldLength = 0;
	int maxFieldLength = 0;
	QString defaultValue = "0";
	bool usesDynamicLength = false;
	bool curFieldHasDynamicLength = false;
	bool indexVarDeclared = false;
	

	QString indexStr = "0";
	int curLength = 0;
	QString fieldLengthStr = "";
	QString lastFieldLengthStr = "";
	QString lastFieldName = "";
	int fieldCounter = 0;

	if (longMessage) {
		initialFieldOffset = 1;	// for long messages byte 0 is just a field to signal a long message 
									// (value is set to 0 and read by the client to identify this)
									// thus, the bytes following this pad byte are the relevant ones
	}

	while (fieldQuery.next()) {
		curFieldHasDynamicLength = false;

		// for each field
		fieldCounter++;
		
		fieldType = fieldQuery.value(fieldRecord.indexOf("type")).toString();
		fieldName = fieldQuery.value(fieldRecord.indexOf("name")).toString();
		fieldLength = fieldQuery.value(fieldRecord.indexOf("length")).toInt();
		maxFieldLength = fieldQuery.value(fieldRecord.indexOf("max_length")).toInt();
		fieldDescription = fieldQuery.value(fieldRecord.indexOf("description")).toString();
		defaultValue = "0";

		// -----------
		// determine index, field length etc. for use in the pack / unpack statements
		// -----------

		// determine new index
		if (usesDynamicLength) {
			QString appendIndexInfo = "";
			// check if we need to declare the index var
			if (!indexVarDeclared) {
				appendIndexInfo = "\n\t\t\tunsigned int curIndex = " + indexStr + " + " + lastFieldLengthStr + ";";
				indexVarDeclared = true;
			} else {
				// use previous field length to determine new index
				appendIndexInfo += "\n\t\t\tcurIndex += " + lastFieldLengthStr + ";";
			}
			fromBytes += appendIndexInfo;
			toBytes += appendIndexInfo;
			
			// use the index var for writing from the appropriate index
			indexStr = "curIndex";
		} else {
			// the index will be plain numeric and is determined by the current sum of the field lengths
			indexStr = QString::number(curLength);
		}

		// determine field length
		if (fieldLength == 0) {
			curFieldHasDynamicLength = true;
			// dynamic field, length will be determined by the precious field's value
			fieldLengthStr = lastFieldName;
			if (!usesDynamicLength) {
				// we need to use an index variable from here onwards
				usesDynamicLength = true;
			}
		} else {
			fieldLengthStr = QString::number(fieldLength);
		}

		
		if (usesDynamicLength) {
			// we need to check length requirements as soon as we have encountered a dynamic length field
			// for all following fields including the first field with dynamic length
			fromBytes += "\n\t\t\tif(" + indexStr + " + " + fieldLengthStr + " > length) {\n\t\t\t\t_msgIsValid = false;\n\t\t\t\treturn false;\n\t\t\t}";
		}


		if (fieldCounter == 1 && longMessage) {
			// special field name for the signal field (first byte) in long messages
			fieldName = "long_message_padding";
		}

		// by convention the first field encodes the length, the second encodes the message id
		if (fieldCounter == 1+initialFieldOffset) {
			// always use "length"
			//lengthFieldName = fieldName;
			fieldName = lengthFieldName;
		} else if (fieldCounter == 2+initialFieldOffset) {
			// always use "msgId"
			//msgIdFieldName = fieldName;
			fieldName = msgIdFieldName;
		}

		// get the statements
		QStringList fieldStatements = getFieldStatements(fieldName, fieldType, fieldDescription, fieldLength, fieldLengthStr, maxFieldLength, indexStr, lastFieldName, fieldCounter, initialFieldOffset, curFieldHasDynamicLength);
		// append them
		if (fieldCounter > 2+initialFieldOffset) {
			// do not add declaration and initialization for the length field and also not for the msgId 
			// as these is already part of the abstract MinMessage base class
			innerImpl += fieldStatements.at(0);
			initialization += fieldStatements.at(1);
		}
		
		fromBytes += fieldStatements.at(2);
		toBytes += fieldStatements.at(3);
		toBytesAssignLengthFields += fieldStatements.at(4);
		toBytesCalculateLength += fieldStatements.at(5);
		additionalIncludes += fieldStatements.at(6);

		// prepare for next field
		curLength += fieldLength;
		lastFieldLengthStr = fieldLengthStr;
		lastFieldName = fieldName;
	}
	
	QString msgIdStatement = "\n\t\t\t" + msgIdFieldName + " = FWSMessageIDs::" + msgIdConstant + ";";
	QString minLengthStatement = "\n\t\t\t_msgMinLength = " + QString::number(curLength) + ";";
	QString initialLengthStatement = "\n\t\t\t" + lengthFieldName + " = _msgMinLength;";
	QString initDefaultValuesCall = "\n\t\t\tif (init) {initDefaultValues();}";
	QString additionalInitializer = "";

	if (longMessage) {
		additionalInitializer = "\n\t\t\tlong_message_padding = 0;";
	}

	// default constructor
	innerImpl += "\n\n\t\t" + camelizedMessageName + "(bool init = true) {" + msgIdStatement + minLengthStatement + initialLengthStatement + initDefaultValuesCall + additionalInitializer + "\n\t\t\t_msgIsValid = true;\n\t\t}";
	// constructor with buffer initializer
	innerImpl += "\n\n\t\t" + camelizedMessageName + "(const unsigned char* buf) {" + msgIdStatement + minLengthStatement + additionalInitializer + "\n\t\t\t_msgIsValid = true;\n\t\t\tvaluesFromBytes(buf);\n\t\t}";
	// constructor with buffer and length initializer
	innerImpl += "\n\n\t\t" + camelizedMessageName + "(const unsigned char* buf, unsigned int totalLength) {" + msgIdStatement + minLengthStatement + additionalInitializer + "\n\t\t\t_msgIsValid = true;\n\t\t\tlength = totalLength;";
	innerImpl +=  "\n\t\t\tif (_msgMinLength <= length) {";
	innerImpl +=  "\n\t\t\t\tvaluesFromBytes(buf, false);";
	innerImpl +=  "\n\t\t\t} else {\n\t\t\t\t_msgIsValid = false;\n\t\t\t}";
	innerImpl += "\n\t\t}";

	// destructor
	innerImpl += "\n\n\t\tvirtual ~" + camelizedMessageName + "() {}";
	
	// wrap initialization into function
	initialization = "\n\n\t\tinline void initDefaultValues() {" + initialization + "\n\t\t}";
	innerImpl += initialization;

	// unpack / fromBytes surround by function frame
	fromBytes = "\t\tbool valuesFromBytes(const unsigned char* bytes, bool readLength = true) {" + fromBytes + "\n\t\t\treturn true;\n\t\t}";
	innerImpl += "\n\n" + fromBytes;
	

	// add validate length check
	validateLengthCode = toBytesAssignLengthFields + "\n\t\t\t\t" + lengthFieldName + " = _msgMinLength " + toBytesCalculateLength + ";";
	toBytes = "\n\t\t\tif (validateLength && !checkValidateLength()) {\n\t\t\t\treturn false;\n\t\t\t}" + toBytes;
	// pack / toBytes surround by function frame
	toBytes = "\t\tbool valuesToBytes(unsigned char* bytes, bool validateLength = true) {" + toBytes + "\n\t\t\treturn true;\n\t\t}";
	innerImpl += "\n\n" + toBytes;

	// storeToClientBuffer convenience function
	/*
	innerImpl += "\n\n\t\tbool storeToClientOutBuffer(FWClient* pClient, bool validateLength = true) {";
	innerImpl += "\n\t\t\tif (validateLength && !checkValidateLength()) {\n\t\t\t\treturn false;\n\t\t\t}";
	innerImpl += "\n\t\t\tunsigned char * buf = pClient->requestBytesFromOutBuffer(" + lengthFieldName + ");";
	innerImpl += "\n\t\t\tif (buf != 0) {";
	innerImpl += "\n\t\t\t\treturn valuesToBytes(buf, false);";
	innerImpl += "\n\t\t\t}";
	innerImpl += "\n\t\t\treturn false;";
	innerImpl += "\n\t\t}";
	*/

	// validate length function
	validateLengthCode.replace("\t\t\t\t", "\t\t\t");	// less indention
	innerImpl += "\n\n\t\tinline bool checkValidateLength() {" + validateLengthCode + "\n\t\t\treturn true;\n\t\t}";
	
	// isValid function
	innerImpl += "\n\n\t\tinline bool isValid() {\n\t\t\treturn _msgIsValid;\n\t\t}";

	// add user implementation / handlerImplementations
	innerImpl += "\n\n\t// %%GENERATOR_START%%MSG_IMPL_USER_CONTENT%%\n" + userContent + "\t// %%GENERATOR_END%%MSG_IMPL_USER_CONTENT%%\n";
	// add public specifier for the normal fields
	innerImpl = "\n\n\tpublic:\n" + innerImpl;

	// if this is a long message add a private member for the first byte
	if (longMessage) {
		innerImpl = "\n\n\tprivate:\n\t\tunsigned int long_message_padding;\n" + innerImpl;
	}
	
	

	if (needsIOStream) {
		// include iostream header for string
		classImpl += "\n#include <string>";
	}
	// include base class header
	classImpl += "\n#include \"MinMessage.h\"";
	
	// include message ids
	classImpl += "\n#include \"../FWSMessageIDs.h\"";

	// additional headers
	classImpl += additionalIncludes;

	// add user headers
	classImpl += "\n\n// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%\n" + userHeaders + "// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%";


	// surround by namespace and class container (use brackets and add public specifier)
	classImpl += "\n\nnamespace fws {\n\n/**\n * " + classDescription + ".\n */\n\nclass " + camelizedMessageName + " : public min::MinMessage {\n" + innerImpl + "\n};\n\n}";


	// surround by ifdef header guard
	classImpl = "#ifndef " + camelizedMessageName + "_h__\n#define " + camelizedMessageName + "_h__\n\n" + classImpl + "\n\n#endif\n";

	
	return classImpl;
}

/**
 * Retrieve the statement which is used for unpacking the given field.
 */
QStringList MessageGeneratorApp::getFieldStatements(const QString& fieldName, const QString& fieldType, const QString& fieldDescription, int fieldLength, const QString& fieldLengthStr, int maxFieldLength, const QString& indexStr, const QString& lastFieldName, int fieldCounter, int initialFieldOffset, bool curFieldHasDynamicLength) {
	QStringList statements;
	QString decl = "";
	QString initialization = "";
	QString fromBytes = "";
	QString toBytes = "";
	QString toBytesAssignLengthFields = "";
	QString toBytesCalculateLength = "";
	QString additionalIncludes = "";
	QString newFieldType = fieldType;

	if (fieldType == "string") {
		initialization = "\"\"";
		newFieldType = "std::string";
		// length of the string comes from the value of the previous field
		// add check for field max length if required
		if (maxFieldLength > 0) {
			fromBytes += "\n\t\t\tif (" + fieldLengthStr + " > " + QString::number(maxFieldLength) + ") {\n\t\t\t\t_msgIsValid = false;\n\t\t\t\treturn false;\n\t\t\t}";			
		}
		fromBytes += "\n\t\t\t" + fieldName + " = min::NetPort::stringFromBytes(bytes, "  + fieldLengthStr + ", " + indexStr + ");";
		toBytes += "\n\t\t\tmin::NetPort::stringToBytes(" + fieldName + ", bytes, " + fieldLengthStr + ", " + indexStr + ");";
		if (curFieldHasDynamicLength) {
			// the previous field holds the length of the current field
			toBytesAssignLengthFields = "\n\t\t\t\t" + lastFieldName + " = (int)" + fieldName + ".size();";
			if (maxFieldLength > 0) {
				toBytesAssignLengthFields += "\n\t\t\t\tif (" + lastFieldName + " > " + QString::number(maxFieldLength) + ") {\n\t\t\t\t\t_msgIsValid = false; \n\t\t\t\t\treturn false;\n\t\t\t\t}";
			}
		} else {
			if (maxFieldLength > 0) {
				toBytesAssignLengthFields += "\n\t\t\t\tif (" + fieldName + ".size() > " + QString::number(maxFieldLength) + ") {\n\t\t\t\t\t_msgIsValid = false; \n\t\t\t\t\treturn false;\n\t\t\t\t}";
			}
		}
	} else if (fieldType == "binary" || fieldType.contains("char") || fieldType.contains("char*")) {
		// needs no initialization as the BinaryDataWrapper will have a default initialization of 0 for the data pointer
		if (!additionalIncludes.contains("#include \"BinaryDataWrapper.h\"")) {
			additionalIncludes += "\n#include \"BinaryDataWrapper.h\"";
		}

		// length of the char array comes from the value of the previous field
		// note: you will need to take care of allocating and deallocating the actual memory outside of the message object!
		newFieldType = "fwutil::BinaryDataWrapper";
		// add check for field maxlength if required
		if (maxFieldLength > 0) {
			fromBytes += "\n\t\t\tif (" + fieldLengthStr + " > " + QString::number(maxFieldLength) + ") {\n\t\t\t\t_msgIsValid = false;\n\t\t\t\treturn false;\n\t\t\t}";
		}
		fromBytes +=	"\n\t\t\t" + fieldName + " = " + "fwutil::BinaryDataWrapper(bytes, "  + fieldLengthStr + ", " + indexStr + ");";
		toBytes +=		"\n\t\t\t" + fieldName + ".copyToBuffer(bytes, " + indexStr + ");";
		// the previous field holds the length of the current field
		if (curFieldHasDynamicLength) {
			toBytesAssignLengthFields = "\n\t\t\t\t" + lastFieldName + " = " + fieldName + ".getSize();";
			if (maxFieldLength > 0) {
				toBytesAssignLengthFields += "\n\t\t\t\tif (" + lastFieldName + " > " + QString::number(maxFieldLength) + ") {\n\t\t\t\t\t_msgIsValid = false; \n\t\t\t\t\treturn false;\n\t\t\t\t}";
			}
		} else {
			if (maxFieldLength > 0) {
				toBytesAssignLengthFields += "\n\t\t\t\tif (" + fieldName + ".getSize() > " + QString::number(maxFieldLength) + ") {\n\t\t\t\t\t_msgIsValid = false; \n\t\t\t\t\treturn false;\n\t\t\t\t}";
			}
		}
	} else if (fieldType.contains("double") || fieldType.contains("float")) {
		initialization = "0";
		if (fieldLength == 4) {
			QString casting = "";
			if (fieldType != "double") {
				casting = "(" + fieldType + ")";
			}
			fromBytes += "\n\t\t\t" + fieldName + " = " + casting + "min::NetPort::doubleFrom4BytesP2(bytes, " + indexStr + ");";
			toBytes += "\n\t\t\tmin::NetPort::doubleTo4BytesP2(" + fieldName + ", bytes, " + indexStr + ");";
		} else {
			fromBytes += "error: " + fieldName + " is a floating point number, but has a length of " + fieldLengthStr + ", but only 4 bytes are supported.;";
			toBytes += "error: " + fieldName + " is a floating point number, but has a length of " + fieldLengthStr + ", but only 4 bytes are supported.;";
		}				
	} else if (fieldType.contains("int") || fieldType.contains("long") || fieldType.contains("short")) {
		initialization = "0";
		QString prefix = fieldType.startsWith("unsigned") ? "u" : "";
		if (fieldLength == 1) {
			fromBytes += "\n\t\t\t" + fieldName + " = min::NetPort::" + prefix + "intFromByte(bytes, " + indexStr + ");";
			toBytes += "\n\t\t\tmin::NetPort::" + prefix + "intToByte(" + fieldName + ", bytes, " + indexStr + ");";
		} else if (fieldLength < 5) {
			fromBytes += "\n\t\t\t" + fieldName + " = min::NetPort::" + prefix + "intFrom" + QString::number(fieldLength) + "Bytes(bytes, " + indexStr + ");";
			toBytes += "\n\t\t\tmin::NetPort::" + prefix + "intTo" + QString::number(fieldLength) + "Bytes(" + fieldName + ", bytes, " + indexStr + ");";
		} else {
			fromBytes += "\n\t\t\t" + fieldName + " = min::NetPort::" + prefix + "intFromNBytes(bytes, "  + fieldLengthStr + ", " + indexStr + ");";
			toBytes += "\n\t\t\tmin::NetPort::" + prefix + "intToNBytes(" + fieldName + ", bytes, " + fieldLengthStr + "," + indexStr + ");";
		}
	}
	
	// assign declaration here, in case the type / field name were changed above
	decl = "\t\t/** " + fieldDescription + ". */\n\t\t" + newFieldType + " " + fieldName + ";\n";

	if (!initialization.isEmpty()) {
		initialization = "\n\t\t\t" + fieldName + " = " + initialization + ";";
	}

	if (fieldLength == 0) {
		// field has dynamic length
		// for the total length we need to consider the dynamic length of the current field which is stored in the previous field
		toBytesCalculateLength = " + " + lastFieldName;
	}

	if (fieldCounter == 1+initialFieldOffset) {
		// first field - this is the length field
		fromBytes.replace("\t\t\t", "\t\t\t\t");	// indent more for if condition
		// add if condition
		fromBytes = "\n\t\t\tif (readLength) {" + fromBytes + "\n\t\t\t\tif (length < _msgMinLength) {\n\t\t\t\t\t_msgIsValid = false;\n\t\t\t\t\treturn false;\n\t\t\t\t}\n\t\t\t}";
	} else if (fieldCounter == 2+initialFieldOffset) {
		// second field - this is the msgId field, comment it out as the id is always the same
		fromBytes.replace("\n\t\t\t", "\n\t\t\t//");
	}


	statements.append(decl);
	statements.append(initialization);
	statements.append(fromBytes);
	statements.append(toBytes);
	statements.append(toBytesAssignLengthFields);
	statements.append(toBytesCalculateLength);
	statements.append(additionalIncludes);

	return statements;
}
