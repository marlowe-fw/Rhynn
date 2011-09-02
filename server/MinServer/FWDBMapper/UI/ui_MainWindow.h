/********************************************************************************
** Form generated from reading UI file 'MainWindow.ui'
**
** Created: Fri 2. Sep 13:30:35 2011
**      by: Qt User Interface Compiler version 4.6.4
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_MAINWINDOW_H
#define UI_MAINWINDOW_H

#include <QtCore/QVariant>
#include <QtGui/QAction>
#include <QtGui/QApplication>
#include <QtGui/QButtonGroup>
#include <QtGui/QComboBox>
#include <QtGui/QFormLayout>
#include <QtGui/QGridLayout>
#include <QtGui/QGroupBox>
#include <QtGui/QHBoxLayout>
#include <QtGui/QHeaderView>
#include <QtGui/QLabel>
#include <QtGui/QLineEdit>
#include <QtGui/QMainWindow>
#include <QtGui/QMenuBar>
#include <QtGui/QPlainTextEdit>
#include <QtGui/QPushButton>
#include <QtGui/QScrollArea>
#include <QtGui/QSpacerItem>
#include <QtGui/QStatusBar>
#include <QtGui/QVBoxLayout>
#include <QtGui/QWidget>

QT_BEGIN_NAMESPACE

class Ui_MainWindow
{
public:
    QWidget *centralwidget;
    QHBoxLayout *horizontalLayout;
    QGroupBox *groupBox;
    QVBoxLayout *verticalLayout;
    QFormLayout *formLayout;
    QLabel *label;
    QLineEdit *leServer;
    QLabel *label_2;
    QLineEdit *leUser;
    QLabel *label_3;
    QLineEdit *lePass;
    QLabel *label_4;
    QLineEdit *leSchema;
    QPushButton *btConnect;
    QPlainTextEdit *teLog;
    QGroupBox *gbMapping;
    QVBoxLayout *verticalLayout_3;
    QGridLayout *gridLayout_2;
    QLabel *label_5;
    QComboBox *cbTable;
    QLabel *label_6;
    QLineEdit *leClass;
    QSpacerItem *horizontalSpacer;
    QPushButton *btGenerate;
    QScrollArea *scrollArea;
    QWidget *scrollAreaWidgetContents;
    QVBoxLayout *verticalLayout_2;
    QPlainTextEdit *teCode;
    QMenuBar *menubar;
    QStatusBar *statusbar;

    void setupUi(QMainWindow *MainWindow)
    {
        if (MainWindow->objectName().isEmpty())
            MainWindow->setObjectName(QString::fromUtf8("MainWindow"));
        MainWindow->resize(1086, 600);
        centralwidget = new QWidget(MainWindow);
        centralwidget->setObjectName(QString::fromUtf8("centralwidget"));
        horizontalLayout = new QHBoxLayout(centralwidget);
        horizontalLayout->setObjectName(QString::fromUtf8("horizontalLayout"));
        groupBox = new QGroupBox(centralwidget);
        groupBox->setObjectName(QString::fromUtf8("groupBox"));
        verticalLayout = new QVBoxLayout(groupBox);
        verticalLayout->setObjectName(QString::fromUtf8("verticalLayout"));
        formLayout = new QFormLayout();
        formLayout->setObjectName(QString::fromUtf8("formLayout"));
        formLayout->setFieldGrowthPolicy(QFormLayout::AllNonFixedFieldsGrow);
        label = new QLabel(groupBox);
        label->setObjectName(QString::fromUtf8("label"));

        formLayout->setWidget(0, QFormLayout::LabelRole, label);

        leServer = new QLineEdit(groupBox);
        leServer->setObjectName(QString::fromUtf8("leServer"));

        formLayout->setWidget(0, QFormLayout::FieldRole, leServer);

        label_2 = new QLabel(groupBox);
        label_2->setObjectName(QString::fromUtf8("label_2"));

        formLayout->setWidget(1, QFormLayout::LabelRole, label_2);

        leUser = new QLineEdit(groupBox);
        leUser->setObjectName(QString::fromUtf8("leUser"));

        formLayout->setWidget(1, QFormLayout::FieldRole, leUser);

        label_3 = new QLabel(groupBox);
        label_3->setObjectName(QString::fromUtf8("label_3"));

        formLayout->setWidget(2, QFormLayout::LabelRole, label_3);

        lePass = new QLineEdit(groupBox);
        lePass->setObjectName(QString::fromUtf8("lePass"));
        lePass->setEchoMode(QLineEdit::Password);

        formLayout->setWidget(2, QFormLayout::FieldRole, lePass);

        label_4 = new QLabel(groupBox);
        label_4->setObjectName(QString::fromUtf8("label_4"));

        formLayout->setWidget(3, QFormLayout::LabelRole, label_4);

        leSchema = new QLineEdit(groupBox);
        leSchema->setObjectName(QString::fromUtf8("leSchema"));

        formLayout->setWidget(3, QFormLayout::FieldRole, leSchema);


        verticalLayout->addLayout(formLayout);

        btConnect = new QPushButton(groupBox);
        btConnect->setObjectName(QString::fromUtf8("btConnect"));
        QSizePolicy sizePolicy(QSizePolicy::Minimum, QSizePolicy::Fixed);
        sizePolicy.setHorizontalStretch(0);
        sizePolicy.setVerticalStretch(0);
        sizePolicy.setHeightForWidth(btConnect->sizePolicy().hasHeightForWidth());
        btConnect->setSizePolicy(sizePolicy);

        verticalLayout->addWidget(btConnect);

        teLog = new QPlainTextEdit(groupBox);
        teLog->setObjectName(QString::fromUtf8("teLog"));
        QSizePolicy sizePolicy1(QSizePolicy::Expanding, QSizePolicy::Expanding);
        sizePolicy1.setHorizontalStretch(0);
        sizePolicy1.setVerticalStretch(2);
        sizePolicy1.setHeightForWidth(teLog->sizePolicy().hasHeightForWidth());
        teLog->setSizePolicy(sizePolicy1);

        verticalLayout->addWidget(teLog);


        horizontalLayout->addWidget(groupBox);

        gbMapping = new QGroupBox(centralwidget);
        gbMapping->setObjectName(QString::fromUtf8("gbMapping"));
        gbMapping->setEnabled(false);
        QSizePolicy sizePolicy2(QSizePolicy::Preferred, QSizePolicy::Preferred);
        sizePolicy2.setHorizontalStretch(10);
        sizePolicy2.setVerticalStretch(0);
        sizePolicy2.setHeightForWidth(gbMapping->sizePolicy().hasHeightForWidth());
        gbMapping->setSizePolicy(sizePolicy2);
        verticalLayout_3 = new QVBoxLayout(gbMapping);
        verticalLayout_3->setObjectName(QString::fromUtf8("verticalLayout_3"));
        gridLayout_2 = new QGridLayout();
        gridLayout_2->setObjectName(QString::fromUtf8("gridLayout_2"));
        label_5 = new QLabel(gbMapping);
        label_5->setObjectName(QString::fromUtf8("label_5"));

        gridLayout_2->addWidget(label_5, 0, 0, 1, 1);

        cbTable = new QComboBox(gbMapping);
        cbTable->setObjectName(QString::fromUtf8("cbTable"));
        QSizePolicy sizePolicy3(QSizePolicy::Preferred, QSizePolicy::Fixed);
        sizePolicy3.setHorizontalStretch(2);
        sizePolicy3.setVerticalStretch(0);
        sizePolicy3.setHeightForWidth(cbTable->sizePolicy().hasHeightForWidth());
        cbTable->setSizePolicy(sizePolicy3);

        gridLayout_2->addWidget(cbTable, 0, 2, 1, 2);

        label_6 = new QLabel(gbMapping);
        label_6->setObjectName(QString::fromUtf8("label_6"));

        gridLayout_2->addWidget(label_6, 1, 0, 1, 1);

        leClass = new QLineEdit(gbMapping);
        leClass->setObjectName(QString::fromUtf8("leClass"));

        gridLayout_2->addWidget(leClass, 1, 2, 1, 1);

        horizontalSpacer = new QSpacerItem(40, 20, QSizePolicy::Expanding, QSizePolicy::Minimum);

        gridLayout_2->addItem(horizontalSpacer, 1, 3, 1, 1);


        verticalLayout_3->addLayout(gridLayout_2);

        btGenerate = new QPushButton(gbMapping);
        btGenerate->setObjectName(QString::fromUtf8("btGenerate"));

        verticalLayout_3->addWidget(btGenerate);

        scrollArea = new QScrollArea(gbMapping);
        scrollArea->setObjectName(QString::fromUtf8("scrollArea"));
        scrollArea->setWidgetResizable(true);
        scrollAreaWidgetContents = new QWidget();
        scrollAreaWidgetContents->setObjectName(QString::fromUtf8("scrollAreaWidgetContents"));
        scrollAreaWidgetContents->setGeometry(QRect(0, 0, 764, 423));
        verticalLayout_2 = new QVBoxLayout(scrollAreaWidgetContents);
        verticalLayout_2->setObjectName(QString::fromUtf8("verticalLayout_2"));
        teCode = new QPlainTextEdit(scrollAreaWidgetContents);
        teCode->setObjectName(QString::fromUtf8("teCode"));
        teCode->setLineWrapMode(QPlainTextEdit::NoWrap);
        teCode->setTabStopWidth(40);

        verticalLayout_2->addWidget(teCode);

        scrollArea->setWidget(scrollAreaWidgetContents);

        verticalLayout_3->addWidget(scrollArea);


        horizontalLayout->addWidget(gbMapping);

        MainWindow->setCentralWidget(centralwidget);
        menubar = new QMenuBar(MainWindow);
        menubar->setObjectName(QString::fromUtf8("menubar"));
        menubar->setGeometry(QRect(0, 0, 1086, 21));
        MainWindow->setMenuBar(menubar);
        statusbar = new QStatusBar(MainWindow);
        statusbar->setObjectName(QString::fromUtf8("statusbar"));
        MainWindow->setStatusBar(statusbar);

        retranslateUi(MainWindow);

        QMetaObject::connectSlotsByName(MainWindow);
    } // setupUi

    void retranslateUi(QMainWindow *MainWindow)
    {
        MainWindow->setWindowTitle(QApplication::translate("MainWindow", "MainWindow", 0, QApplication::UnicodeUTF8));
        groupBox->setTitle(QApplication::translate("MainWindow", "Connection", 0, QApplication::UnicodeUTF8));
        label->setText(QApplication::translate("MainWindow", "Server", 0, QApplication::UnicodeUTF8));
        leServer->setText(QApplication::translate("MainWindow", "localhost", 0, QApplication::UnicodeUTF8));
        label_2->setText(QApplication::translate("MainWindow", "User", 0, QApplication::UnicodeUTF8));
        leUser->setText(QApplication::translate("MainWindow", "root", 0, QApplication::UnicodeUTF8));
        label_3->setText(QApplication::translate("MainWindow", "Password", 0, QApplication::UnicodeUTF8));
        lePass->setText(QApplication::translate("MainWindow", "xypass12", 0, QApplication::UnicodeUTF8));
        label_4->setText(QApplication::translate("MainWindow", "Schema", 0, QApplication::UnicodeUTF8));
        leSchema->setText(QApplication::translate("MainWindow", "fw_core", 0, QApplication::UnicodeUTF8));
        btConnect->setText(QApplication::translate("MainWindow", "Connect", 0, QApplication::UnicodeUTF8));
        gbMapping->setTitle(QApplication::translate("MainWindow", "Mapping", 0, QApplication::UnicodeUTF8));
        label_5->setText(QApplication::translate("MainWindow", "Table", 0, QApplication::UnicodeUTF8));
        label_6->setText(QApplication::translate("MainWindow", "Class Name", 0, QApplication::UnicodeUTF8));
        btGenerate->setText(QApplication::translate("MainWindow", "Generate Mapping", 0, QApplication::UnicodeUTF8));
    } // retranslateUi

};

namespace Ui {
    class MainWindow: public Ui_MainWindow {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_MAINWINDOW_H
