#include <QtSql/../../src/sql/models/qsqlrelationaldelegate.h>
#include "AppMainWindow.h"
#include "../MessageGeneratorApp.h"
#include "MessageFieldDelegate.h"
#include "MessageEditDelegate.h"
#include "AppGenerateDialog.h"
#include <QtCore/QList>
#include <QtCore/QSettings>
#include <QtCore/QVariant>
#include <QtCore/QString>
#include <QtGui/QSortFilterProxyModel>
#include <QtSql/QSqlRelation>
#include <QtSql/QSqlRelationalDelegate>
#include <QtSql/QSqlRecord>
#include <QtSql/QSqlQuery>
#include <QtGui/QAbstractItemView>
#include <QtGui/QItemSelection>
#include <QtGui/QHeaderView>
#include <QtGui/QScrollBar>
#include <iostream>
#include <Windows.h>
#include <QtCore/QDebug>
#include <QtCore/QIODevice>
#include <QtCore/QFile>
#include <QtCore/QDir>
#include <QtCore/QTextStream>
#include <QtCore/QRegExp>
#include "../src/sql/kernel/qsqlerror.h"

AppMainWindow::AppMainWindow(QWidget* parent /* = 0 */) : QMainWindow(parent), 
selectedMessageID(0),
coreMessageModel(0),
fieldsModel(0),
mapper(0)
{
	setupUi(this);
	readSettings();

	// the business logic object is owned by the applications main window
	// that ensures lifetime of GUI corresponds to business logic lifetime
	qGenApp = new MessageGeneratorApp(this);

	setupUiCustom(qGenApp);
	setupActionsAndMenus(qGenApp);
	messageTable->setCurrentIndex(coreMessageModel->index(0, 0));
	messageTable->setFocus();

	/*	
	QRegExp rx("[\\s]*bool.*::(.*)\\(.*\\)");
	QString s = "bool FWServer::handleMessageSystemChat(FWClient* pCurClient, const unsigned char* msg, unsigned int length) {";
	int pos = rx.indexIn(s);
	if (pos > -1) {
		QString value = rx.cap(1); // "189"
		//QString unit = rxlen.cap(2);  // "cm"
		qDebug() << value;
		// ...
	} 
	*/
	/*
	if (s.contains(rx)) {
		std::cout << "contains" << std::endl;
	} else {
		std::cout << "contains NOT" << std::endl;
	}*/
}

AppMainWindow::~AppMainWindow() {
	// store settings on exit
	storeSettings();
}

void AppMainWindow::setupActionsAndMenus(MessageGeneratorApp* qGenApp) {
	// connect signals / slots here
	//messageTable->selectionModel()->
	//connect(messageTable->selectionModel(), SIGNAL(currentChanged ( const QModelIndex&, const QModelIndex&) ), this, SLOT(handleMessageSelected(const QModelIndex&, const QModelIndex&)));
	connect(messageTable->selectionModel(), SIGNAL(currentRowChanged ( const QModelIndex&, const QModelIndex&) ), this, SLOT(handleMessageSelected(const QModelIndex&, const QModelIndex&)));
	connect(messageTable->selectionModel(), SIGNAL(currentRowChanged ( const QModelIndex&, const QModelIndex&) ), mapper, SLOT(setCurrentModelIndex(const QModelIndex&)));
	connect(coreMessageModel, SIGNAL(dataChanged( const QModelIndex &, const QModelIndex &)), this, SLOT(handleDataChanged( const QModelIndex &, const QModelIndex &)));
	connect(btnSaveChanges, SIGNAL(clicked()), this, SLOT(handleSaveChanges()));
	connect(messageTable, SIGNAL(clicked(const QModelIndex&)), this, SLOT(handleMessageClicked(const QModelIndex&)));
	connect(btnAdd, SIGNAL(clicked()), this, SLOT(handleTriggerAddMessage()));
	connect(btnRemove, SIGNAL(clicked()), this, SLOT(handleTriggerRemoveMessage()));
	connect(btnRefresh, SIGNAL(clicked()),  this, SLOT(handleTriggerRefresh()));
	//connect(messageModel, SIGNAL(primeInsert ( int, QSqlRecord &)), this, SLOT(handleMessageInsert ( int, QSqlRecord &)));
	connect(btnSetFilter, SIGNAL(clicked()),  this, SLOT(handleTriggerSetFilter()));
	connect(lineEditFilter, SIGNAL(returnPressed()), btnSetFilter, SLOT(click()));
	connect(messageFieldDelegate, SIGNAL(sequenceOrderChanged()), this, SLOT(updateFieldsTable()));
	connect(btnAddField, SIGNAL(clicked()), this, SLOT(handleTriggerAddField()));
	connect(btnRemoveField, SIGNAL(clicked()), this, SLOT(handleTriggerRemoveField()));
	connect(btnGenerate, SIGNAL(clicked()), this, SLOT(handleTriggerGenerate()));
	//connect(btnGenerateSingle, SIGNAL(clicked()), this, SLOT(handleTriggerGenerateSingle()));
	connect(fieldTable, SIGNAL(clicked(const QModelIndex&)), this, SLOT(handleFieldTableActivated(const QModelIndex&)));
	connect(fieldTable, SIGNAL(activated(const QModelIndex&)), this, SLOT(handleFieldTableActivated(const QModelIndex&)));
	connect(messageFieldDelegate, SIGNAL(closeEditor( QWidget*, QAbstractItemDelegate::EndEditHint)), this, SLOT(handleMessageFieldEdited()));
	//connect(messageFieldDelegate, SIGNAL(commitData( QWidget*)), this, SLOT(handleMessageFieldEdited()));
	//connect(btnTest, SIGNAL(clicked()), this, SLOT(test()));
	connect(messageFieldDelegate, SIGNAL(beforeEdit(const QModelIndex&)), this, SLOT(handleFieldTableActivated(const QModelIndex&)));
		
	
	// context menu
	contextActionCloneMessage = new QAction(tr("Clone message .."), this);
	connect(messageTable, SIGNAL(customContextMenuRequested(const QPoint &)), this, SLOT(showMessageTableContextMenu(const QPoint &)));
	connect(contextActionCloneMessage, SIGNAL(triggered()), this, SLOT(cloneContextMessage()));


	
}




void AppMainWindow::showMessageTableContextMenu(const QPoint &position) {
	QList<QAction *> actions;
	if (messageTable->indexAt(position).isValid()) {
		actions.append(contextActionCloneMessage);
	}
	if (actions.count() > 0) {
		contextMessageIndex = messageTable->indexAt(position);
		QMenu::exec(actions, messageTable->mapToGlobal(position));
	}

}

void AppMainWindow::cloneContextMessage() {
	if (contextMessageIndex.isValid()) {
		
		QSqlRecord curRecord = coreMessageModel->record(contextMessageIndex.row());
		//selectedMessageID = curRecord.value("id").toInt();

		/*
		QSqlRecord newRecord(curRecord);
		newRecord.setValue("id" , QVariant(0));
		coreMessageModel->insertRecord(contextMessageIndex.row()+1, curRecord);
		*/
		
		int row = contextMessageIndex.row()+1;
		/*
		//qDebug() << curRecord.value("name").toString();
		
		QSqlTableModel* rModel = coreMessageModel->relationModel(coreMessageModel->fieldIndex("category_id"));

		rModel->

		QModelIndex rIndex = rModel->index(row, rModel->fieldIndex("category_id"));
		if (rIndex.isValid()) {
			QSqlRecord val = rModel->record(rIndex.row());
			//qDebug() << val.toString();
		} else {
			qDebug() << "not valid";
		}
		*/

		int originalId = curRecord.value("id").toInt();

		//-- use below

		QSqlQuery insertMessageQuery; 
		insertMessageQuery.prepare("insert into messages (category_id, group_id, symbolic_id, symbolic_name, description, generate_handler, long_message) values (:category_id, :group_id, :symbolic_id, :symbolic_name, :description, :generate_handler, :long_message)");
		insertMessageQuery.bindValue(":category_id", 1);
		insertMessageQuery.bindValue(":group_id", 1);
		insertMessageQuery.bindValue(":symbolic_id", curRecord.value("symbolic_id").toInt());
		insertMessageQuery.bindValue(":symbolic_name", curRecord.value("symbolic_name").toString() + "_copy");
		insertMessageQuery.bindValue(":description", curRecord.value("description").toString());
		insertMessageQuery.bindValue(":ganerate_handler", curRecord.value("generate_handler").toString());
		insertMessageQuery.bindValue(":long_message", curRecord.value("long_message").toString());
		insertMessageQuery.exec();

		QVariant vCopyId = insertMessageQuery.lastInsertId();

		if (vCopyId.isValid()) {
			int copyId = vCopyId.toInt();

			if (copyId > 0) {
				QSqlQuery query("select * from message_fields where message_id = " + QString::number(originalId));
				QSqlRecord fieldRecord = query.record();
				int nameIndex = fieldRecord.indexOf("name");

				QSqlQuery insertQuery;
				insertQuery.prepare("insert into message_fields (message_id, type, length, max_length, name, description, sequence_order) values (:message_id, :type, :length, :max_length, :name, :description, :sequence_order)");

				while (query.next()) {
					QString type = query.value(fieldRecord.indexOf("type")).toString();
					int length = query.value(fieldRecord.indexOf("length")).toInt();
					int max_length = query.value(fieldRecord.indexOf("max_length")).toInt();
					QString name = query.value(fieldRecord.indexOf("name")).toString();
					QString description = query.value(fieldRecord.indexOf("description")).toString();
					int sequence_order = query.value(fieldRecord.indexOf("sequence_order")).toInt();

					insertQuery.bindValue(":message_id", copyId);
					insertQuery.bindValue(":type", type);
					insertQuery.bindValue(":length", length);
					insertQuery.bindValue(":max_length", max_length);
					insertQuery.bindValue(":name", name);
					insertQuery.bindValue(":description", description);
					insertQuery.bindValue(":sequence_order", sequence_order);
					insertQuery.exec();
				}

				coreMessageModel->submitAll();
				coreMessageModel->select();

			}
		}
	}
}


void AppMainWindow::handleTriggerSetFilter() {
	QString& filterString = lineEditFilter->text();
	QString searchString = "";
	QString categoryString = "";
	QString groupString = "";
	QString conditionString = "";

	// search string
	if (!filterString.isEmpty()) {
		if (cbFilterApplySymID->isChecked()) {
			searchString += "symbolic_id like '%" + filterString + "%'";
		}
		if (cbFilterApplySymName->isChecked()) {
			if (!searchString.isEmpty()) searchString += " OR ";
			searchString += "symbolic_name like '%" + filterString + "%'";
		}
		if (cbFilterApplyDescription->isChecked()) {
			if (!searchString.isEmpty()) searchString += " OR ";
			searchString += "description like '%" + filterString + "%'";
		}
	}
	// category filter
	if (cbCategoryFilterSystem->isChecked()) {
		categoryString += "category_id = 1";
	}
	if (cbCategoryFilterAdmin->isChecked()) {
		if (!categoryString.isEmpty()) categoryString += " OR ";
		categoryString += "category_id = 2";
	}
	if (cbCategoryFilterTest->isChecked()) {
		if (!categoryString.isEmpty()) categoryString += " OR ";
		categoryString += "category_id = 3";
	}
	if (cbCategoryFilterGame->isChecked()) {
		if (!categoryString.isEmpty()) categoryString += " OR ";
		categoryString += "category_id = 4";
	}

	if (!searchString.isEmpty() && !categoryString.isEmpty()) {
		conditionString = "(" + categoryString + ") AND (" + searchString + ")";
	} else if (!searchString.isEmpty()) {
		conditionString = searchString;
	} else if (!categoryString.isEmpty()) {
		conditionString = categoryString;
	} 

	if (!conditionString.isEmpty()) {
		coreMessageModel->submitAll();
		coreMessageModel->setFilter(conditionString);
	} else {
		coreMessageModel->submitAll();
		coreMessageModel->select();
	}
	btnSaveChanges->setEnabled(false);
	lblChanges->setText("");
}

void AppMainWindow::setupUiCustom(MessageGeneratorApp* qGenApp) {	
	messageFieldDelegate = new MessageFieldDelegate(fieldTable);
	
	QString username = NULL;
	QString password = NULL;

	QFile dbConfig("database.config");
	if (dbConfig.open(QIODevice::ReadOnly|QIODevice::Text)) {
		QTextStream in(&dbConfig);

		QString line = in.readLine();
		QStringList values = line.split(":");
		
		if (values.length() == 2) {
			username = values[0].trimmed();
			password = values[1].trimmed();
		}
	}

	if (username == NULL || password == NULL) {
		std::cout << "Could not read database user name and password. Make sure you have created the database.config file in the same folder as the application executable, containing database access in the form: username:password" << std::endl;
		username = "root";
		password = "xypass12";
	}

	qGenApp->connectToDB("localhost", username, password, "fws_messages");

	// setup model	
	coreMessageModel = new QSqlRelationalTableModel(messageTable);
	coreMessageModel->setEditStrategy(QSqlTableModel::OnManualSubmit);
	coreMessageModel->setTable("messages");
	int idIndex = coreMessageModel->fieldIndex("id");
	int categoryIndex = coreMessageModel->fieldIndex("category_id");
	int groupIndex = coreMessageModel->fieldIndex("group_id");
	coreMessageModel->setRelation(categoryIndex, QSqlRelation("categories", "id", "name"));
	coreMessageModel->setRelation(groupIndex, QSqlRelation("modules", "id", "module_name"));
	coreMessageModel->setHeaderData(categoryIndex, Qt::Horizontal, tr("category"));
	coreMessageModel->setHeaderData(groupIndex, Qt::Horizontal, tr("module"));
	coreMessageModel->setHeaderData(coreMessageModel->fieldIndex("generate_handler"), Qt::Horizontal, tr("handler"));
	//coreMessageModel->setHeaderData(coreMessageModel->fieldIndex("long_message"), Qt::Horizontal, tr("long"));
	coreMessageModel->setHeaderData(coreMessageModel->fieldIndex("symbolic_id"), Qt::Horizontal, tr("sym. id"));

	//messageModel = new QSortFilterProxyModel(messageTable);
	//messageModel->setSourceModel(coreMessageModel);

	coreMessageModel->select();

	fieldsModel = new QSqlTableModel(fieldTable);
	fieldsModel->setEditStrategy(QSqlTableModel::OnFieldChange);
	fieldsModel->setTable("message_fields");
	
	fieldsModel->setHeaderData(fieldsModel->fieldIndex("sequence_order"), Qt::Horizontal, tr(""));
	fieldsModel->setHeaderData(fieldsModel->fieldIndex("max_length"), Qt::Horizontal, tr("max. len."));


	fieldTable->setModel(fieldsModel);
	fieldTable->setItemDelegate(messageFieldDelegate);
	fieldTable->hideColumn(fieldsModel->fieldIndex("id"));
	fieldTable->hideColumn(fieldsModel->fieldIndex("message_id"));
	//fieldTable->hideColumn(fieldsModel->fieldIndex("sequence_order"));
	
	fieldTable->setColumnWidth(fieldsModel->fieldIndex("type"), 100);
	fieldTable->setColumnWidth(fieldsModel->fieldIndex("length"), 55);
	fieldTable->setColumnWidth(fieldsModel->fieldIndex("max_length"), 55);
	fieldTable->setColumnWidth(fieldsModel->fieldIndex("name"), 120);
	fieldTable->setColumnWidth(fieldsModel->fieldIndex("description"), 207);
	fieldTable->setColumnWidth(fieldsModel->fieldIndex("sequence_order"), 32);
	//fieldTable->setColumnWidth(4, 55);
	
	
	
	// setup message table
	messageTable->setContextMenuPolicy(Qt::CustomContextMenu);

	messageTable->setModel(coreMessageModel);
	messageTable->setItemDelegate(new QSqlRelationalDelegate(messageTable));
	messageTable->setSelectionBehavior(QAbstractItemView::SelectRows);
	messageTable->setSelectionMode(QAbstractItemView::SingleSelection);
	
	messageTable->horizontalHeader()->setClickable(true);
	messageTable->setSortingEnabled(true);
	messageTable->horizontalHeader()->setSortIndicator(coreMessageModel->fieldIndex("symbolic_id"), Qt::AscendingOrder);


	messageTable->sortByColumn(coreMessageModel->fieldIndex("symbolic_id"), Qt::AscendingOrder);
	
	
	editCategory->setModel(coreMessageModel->relationModel(categoryIndex));
	editCategory->setModelColumn(coreMessageModel->relationModel(categoryIndex)->fieldIndex("name"));
	
	editGroup->setModel(coreMessageModel->relationModel(groupIndex));
	editGroup->setModelColumn(coreMessageModel->relationModel(groupIndex)->fieldIndex("module_name"));


	mapper = new QDataWidgetMapper(this);
	mapper->setModel(coreMessageModel);
	mapper->setItemDelegate(new QSqlRelationalDelegate(messageTable));
	//mapper->addMapping(editID, coreMessageModel->fieldIndex("id"));
	mapper->addMapping(editCategory, categoryIndex);
	mapper->addMapping(editGroup, groupIndex);
	mapper->addMapping(editSymID, coreMessageModel->fieldIndex("symbolic_id"));
	mapper->addMapping(editSymName, coreMessageModel->fieldIndex("symbolic_name"));
	mapper->addMapping(editDescription, coreMessageModel->fieldIndex("description"));
	mapper->addMapping(editGenHandler, coreMessageModel->fieldIndex("generate_handler"));
	mapper->addMapping(editLongMessage, coreMessageModel->fieldIndex("long_message"));
	mapper->setSubmitPolicy(QDataWidgetMapper::AutoSubmit);

	messageTable->hideColumn(coreMessageModel->fieldIndex("id"));
	messageTable->hideColumn(coreMessageModel->fieldIndex("long_message"));
	messageTable->hideColumn(coreMessageModel->fieldIndex("description"));
	messageTable->setColumnWidth(coreMessageModel->fieldIndex("name"), 70);
	messageTable->setColumnWidth(coreMessageModel->fieldIndex("module_name"), 180);
	messageTable->setColumnWidth(coreMessageModel->fieldIndex("symbolic_name"), 250);
	messageTable->setColumnWidth(coreMessageModel->fieldIndex("symbolic_id"), 75);
	messageTable->setColumnWidth(coreMessageModel->fieldIndex("description"), 150);

	messageTable->setColumnWidth(coreMessageModel->fieldIndex("generate_handler"), 55);
	//messageTable->setColumnWidth(coreMessageModel->fieldIndex("id"), 45);
	//messageTable->setColumnWidth(coreMessageModel->fieldIndex("category_id"), 55);
	//messageTable->setColumnWidth(coreMessageModel->fieldIndex("symbolic_id"), 55);


}

void AppMainWindow::handleMessageClicked(const QModelIndex& curSelection) {
}


void AppMainWindow::test() {
	messageTable->setCurrentIndex(coreMessageModel->index(2, 0));
	messageTable->setFocus();
	messageTable->setEnabled(true);
	
}

void AppMainWindow::handleMessageSelected(const QModelIndex& newIndex, const QModelIndex& oldIndex) {
	int row = newIndex.row();
	int prow = oldIndex.row();
	
	if (newIndex.isValid()) {
		QSqlRecord curRecord = coreMessageModel->record(row);
		selectedMessageID = curRecord.value("id").toInt();
		//messageTable->selectionModel()->setCurrentIndex(newIndex, QItemSelectionModel::SelectCurrent);
	} else {
		selectedMessageID = 0;			
	}

	updateFieldsTable();
	
	messageTable->setFocus();
}

void AppMainWindow::updateFieldsTable() {
	if (selectedMessageID < 0) {
		selectedMessageID = 0;
	}
	
	QString condition = "message_id = " + QString::number(selectedMessageID);	
	fieldsModel->setFilter(condition);
	fieldsModel->setSort(fieldsModel->fieldIndex("sequence_order"), Qt::AscendingOrder);
	fieldsModel->select();
}

void AppMainWindow::handleFieldTableActivated(const QModelIndex& index) {
	QModelIndex topIndex = fieldTable->indexAt(QPoint(5, 5));
	if (topIndex.isValid()) {
		topRowFieldIndex = topIndex;
		selectedFieldIndex = index;
	}

	//std::cout << topRowFieldIndex.row() << std::endl;
}

void AppMainWindow::handleMessageFieldEdited() {
	if (topRowFieldIndex.isValid()) {
		//std::cout << "valid1" << std::endl;
		fieldTable->scrollTo(topRowFieldIndex, QAbstractItemView::PositionAtTop);
		if (selectedFieldIndex.isValid()) {
			//std::cout << "valid2" << std::endl;
			//messageTable->setCurrentIndex(selectedFieldIndex);
		}
	}
}

void AppMainWindow::handleDataChanged ( const QModelIndex & topLeft, const QModelIndex & bottomRight ) {
	//qDebug() << "changed";
	btnSaveChanges->setEnabled(true);
	lblChanges->setText("unsaved changes pending");
}

void AppMainWindow::handleSaveChanges() {	
	QModelIndex selIndex = messageTable->currentIndex();

	int symbolicIdToSelect = 0;
	if (selIndex.isValid() && selIndex.row() >= 0) {
		QSqlRecord curRecord = coreMessageModel->record(selIndex.row());
		symbolicIdToSelect = curRecord.value("symbolic_id").toInt();
	}

	coreMessageModel->submitAll();
	btnSaveChanges->setEnabled(false);
	lblChanges->setText("");

	if (symbolicIdToSelect > 0) {
		int curRow = 0;
		bool found = false;

		QModelIndex curIndex = coreMessageModel->index(0, 0);

		while (curIndex.isValid() && !found) {
			QSqlRecord curRecord = coreMessageModel->record(curIndex.row());
			if (symbolicIdToSelect == curRecord.value("symbolic_id").toInt()) {
				found = true;
				selIndex = curIndex;
			} else {
				curIndex = curIndex.sibling(curIndex.row() + 1, curIndex.column());
			}
		}

	}
	
	if (selIndex.isValid()) {
		messageTable->setCurrentIndex(selIndex);
		messageTable->setFocus();
		messageTable->scrollTo(selIndex);
	} else if (coreMessageModel->rowCount() > 0) {
		messageTable->setCurrentIndex(coreMessageModel->index(0, 0));
	}
}

void AppMainWindow::handleTriggerRefresh() {
	QModelIndex selIndex = messageTable->currentIndex();
	coreMessageModel->select();
	btnSaveChanges->setEnabled(false);
	lblChanges->setText("");
	// messageModel->index(0,0)
	messageTable->setCurrentIndex(selIndex);
	messageTable->scrollTo(selIndex);
	messageTable->setFocus();
}

void AppMainWindow::handleTriggerAddMessage() {
	coreMessageModel->insertRows(coreMessageModel->rowCount(), 1);
	int row = coreMessageModel->rowCount()-1;
	coreMessageModel->setData(coreMessageModel->index(row, coreMessageModel->fieldIndex("name")), 4);	// i.e. category name
	coreMessageModel->setData(coreMessageModel->index(row, coreMessageModel->fieldIndex("module_name")), 1);	// i.e. module name
	coreMessageModel->setData(coreMessageModel->index(row, coreMessageModel->fieldIndex("symbolic_id")), 0);
	coreMessageModel->setData(coreMessageModel->index(row, coreMessageModel->fieldIndex("symbolic_name")), "undefined");
	coreMessageModel->setData(coreMessageModel->index(row, coreMessageModel->fieldIndex("description")), "-");
	messageTable->setCurrentIndex(coreMessageModel->index(row, 0));
	messageTable->setFocus();
	btnSaveChanges->setEnabled(true);
	lblChanges->setText("unsaved changes pending");
}

void AppMainWindow::handleTriggerRemoveField() {
	QModelIndex selIndex = fieldTable->currentIndex();
	if (selIndex.isValid()) {
		int numRows = fieldsModel->rowCount();
		int selRow = selIndex.row();
		int colSequenceOrder = fieldsModel->fieldIndex("sequence_order");
		int sequenceOrder = fieldsModel->data(fieldsModel->index(selRow, colSequenceOrder)).toInt();
		fieldsModel->removeRow(selRow);
		for(int i=selRow; i<numRows-1; i++) {
			fieldsModel->setData(fieldsModel->index(i, colSequenceOrder), sequenceOrder);
			sequenceOrder++;
		}
	}
	fieldsModel->submitAll();
}

void AppMainWindow::handleTriggerAddField() {
	if (selectedMessageID <= 0) {
		return;
	}
	int numRows = fieldsModel->rowCount();
	int sequenceOrder = 0;
	
	if (numRows > 0) {
		// get max sequence_order
		sequenceOrder = fieldsModel->data(fieldsModel->index(numRows - 1, fieldsModel->fieldIndex("sequence_order"))).toInt();
		sequenceOrder++;
	}

	fieldsModel->insertRows(numRows, 1);
	int row = numRows;

	fieldsModel->setData(fieldsModel->index(row, fieldsModel->fieldIndex("message_id")), selectedMessageID);
	fieldsModel->setData(fieldsModel->index(row, fieldsModel->fieldIndex("type")), "unsigned int");
	fieldsModel->setData(fieldsModel->index(row, fieldsModel->fieldIndex("length")), 1);
	fieldsModel->setData(fieldsModel->index(row, fieldsModel->fieldIndex("max_length")), 0);
	fieldsModel->setData(fieldsModel->index(row, fieldsModel->fieldIndex("name")), "-");
	fieldsModel->setData(fieldsModel->index(row, fieldsModel->fieldIndex("description")), "-");
	

	fieldsModel->setData(fieldsModel->index(row, fieldsModel->fieldIndex("sequence_order")), sequenceOrder);
	fieldsModel->submitAll();
}


void AppMainWindow::handleTriggerRemoveMessage() {
	QList<QModelIndex> selectedIndexes = messageTable->selectionModel()->selectedIndexes();
	if (selectedIndexes.size() > 0) {
		QModelIndex curIndex = selectedIndexes.first();
		coreMessageModel->removeRow(curIndex.row());
		coreMessageModel->submitAll();
		
		int row = curIndex.row()-1;
		if (row < 0) {
			row = 0;
		}
		messageTable->setCurrentIndex(coreMessageModel->index(row, 0));
		btnSaveChanges->setEnabled(false);
		lblChanges->setText("");
		messageTable->setFocus();
	}
}


/** Handle click on generate ALL button. */
void AppMainWindow::handleTriggerGenerate() {
	handleTriggerGenerateCommon(0);
}

/** Handle click on generate SELECTED button. 
// not yet fully working, needs checking
*/
void AppMainWindow::handleTriggerGenerateSingle() {
	handleTriggerGenerateCommon(selectedMessageID);
}

void AppMainWindow::handleTriggerGenerateCommon(int specificMessageId) {

	AppGenerateDialog dialog(this);
	dialog.presetFileEntries(settingMessageIDFile, settingMessageIDClientFile, settingMessageRegistryFile, settingModulePath, settingModulePrefix, settingMessageClassesPath);

	int ret = dialog.exec();
	if (ret == QDialog::Accepted) {
		settingMessageIDFile = dialog.getMessageIDFile();
		settingMessageIDClientFile = dialog.getMessageIDClientFile();
		settingMessageRegistryFile = dialog.getMessageRegistryFile();
		settingModulePath = dialog.getModulePath();
		settingModulePrefix = dialog.getModulePrefix();
		settingMessageClassesPath = dialog.getMessageClassesPath();
		// generate all
		qGenApp->generateFilesFromMessages(specificMessageId, settingMessageIDFile, settingMessageIDClientFile, settingMessageRegistryFile, settingModulePath, settingModulePrefix, settingMessageClassesPath);
	}
}



void AppMainWindow::storeSettings() {
	QSettings settings("macrolutions Ltd.", "Message Generator");
	settings.beginGroup("mainWindow");	
	settings.setValue("splitter", splitter->saveState());
	settings.setValue("size", size());
	settings.endGroup();

	settings.beginGroup("generateDialog");
	settings.setValue("messageIDFile", settingMessageIDFile);
	settings.setValue("messageIDClientFile", settingMessageIDClientFile);
	settings.setValue("messageRegistryFile", settingMessageRegistryFile);
	settings.setValue("modulePath", settingModulePath);
	settings.setValue("modulePrefix", settingModulePrefix);
	settings.setValue("messageClassesPath", settingMessageClassesPath);
	settings.endGroup();
}

void AppMainWindow::readSettings() {
	QSettings settings("macrolutions Ltd.", "Message Generator");

	settings.beginGroup("mainWindow");
	if (settings.contains("splitter")) {
		splitter->restoreState(settings.value("splitter").toByteArray());
	} else {
		QList<int> defaultSizes;
		defaultSizes.push_back(400);
		defaultSizes.push_back(280);
		splitter->setSizes(defaultSizes);
	}
	resize(settings.value("size", QSize(800, 600)).toSize());
	settings.endGroup();

	settings.beginGroup("generateDialog");
	settingMessageIDFile = settings.value("messageIDFile").toString();
	settingMessageIDClientFile = settings.value("messageIDClientFile").toString();
	settingMessageRegistryFile = settings.value("messageRegistryFile").toString();
	settingModulePath = settings.value("modulePath").toString();
	settingModulePrefix = settings.value("modulePrefix").toString();
	settingMessageClassesPath = settings.value("messageClassesPath").toString();

	settings.endGroup();
}
