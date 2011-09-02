/****************************************************************************
** Meta object code from reading C++ file 'AppMainWindow.h'
**
** Created: Wed 31. Aug 19:37:49 2011
**      by: The Qt Meta Object Compiler version 62 (Qt 4.6.4)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#include "AppMainWindow.h"
#if !defined(Q_MOC_OUTPUT_REVISION)
#error "The header file 'AppMainWindow.h' doesn't include <QObject>."
#elif Q_MOC_OUTPUT_REVISION != 62
#error "This file was generated using the moc from 4.6.4. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

QT_BEGIN_MOC_NAMESPACE
static const uint qt_meta_data_AppMainWindow[] = {

 // content:
       4,       // revision
       0,       // classname
       0,    0, // classinfo
      18,   14, // methods
       0,    0, // properties
       0,    0, // enums/sets
       0,    0, // constructors
       0,       // flags
       0,       // signalCount

 // slots: signature, parameters, type, tag, flags
      17,   15,   14,   14, 0x08,
      77,   64,   14,   14, 0x08,
     131,  111,   14,   14, 0x08,
     174,   14,   14,   14, 0x08,
     194,   14,   14,   14, 0x08,
     220,   14,   14,   14, 0x08,
     249,   14,   14,   14, 0x08,
     272,   14,   14,   14, 0x08,
     297,   14,   14,   14, 0x08,
     324,   14,   14,   14, 0x08,
     344,   14,   14,   14, 0x08,
     368,   14,   14,   14, 0x08,
     392,   14,   14,   14, 0x08,
     422,   14,   14,   14, 0x08,
     458,  449,   14,   14, 0x08,
     506,  497,   14,   14, 0x08,
     542,   14,   14,   14, 0x08,
     564,   14,   14,   14, 0x08,

       0        // eod
};

static const char qt_meta_stringdata_AppMainWindow[] = {
    "AppMainWindow\0\0,\0"
    "handleMessageSelected(QModelIndex,QModelIndex)\0"
    "curSelection\0handleMessageClicked(QModelIndex)\0"
    "topLeft,bottomRight\0"
    "handleDataChanged(QModelIndex,QModelIndex)\0"
    "handleSaveChanges()\0handleTriggerAddMessage()\0"
    "handleTriggerRemoveMessage()\0"
    "handleTriggerRefresh()\0handleTriggerSetFilter()\0"
    "handleTriggerRemoveField()\0"
    "updateFieldsTable()\0handleTriggerAddField()\0"
    "handleTriggerGenerate()\0"
    "handleTriggerGenerateSingle()\0"
    "handleMessageFieldEdited()\0curIndex\0"
    "handleFieldTableActivated(QModelIndex)\0"
    "position\0showMessageTableContextMenu(QPoint)\0"
    "cloneContextMessage()\0test()\0"
};

const QMetaObject AppMainWindow::staticMetaObject = {
    { &QMainWindow::staticMetaObject, qt_meta_stringdata_AppMainWindow,
      qt_meta_data_AppMainWindow, 0 }
};

#ifdef Q_NO_DATA_RELOCATION
const QMetaObject &AppMainWindow::getStaticMetaObject() { return staticMetaObject; }
#endif //Q_NO_DATA_RELOCATION

const QMetaObject *AppMainWindow::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->metaObject : &staticMetaObject;
}

void *AppMainWindow::qt_metacast(const char *_clname)
{
    if (!_clname) return 0;
    if (!strcmp(_clname, qt_meta_stringdata_AppMainWindow))
        return static_cast<void*>(const_cast< AppMainWindow*>(this));
    return QMainWindow::qt_metacast(_clname);
}

int AppMainWindow::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = QMainWindow::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    if (_c == QMetaObject::InvokeMetaMethod) {
        switch (_id) {
        case 0: handleMessageSelected((*reinterpret_cast< const QModelIndex(*)>(_a[1])),(*reinterpret_cast< const QModelIndex(*)>(_a[2]))); break;
        case 1: handleMessageClicked((*reinterpret_cast< const QModelIndex(*)>(_a[1]))); break;
        case 2: handleDataChanged((*reinterpret_cast< const QModelIndex(*)>(_a[1])),(*reinterpret_cast< const QModelIndex(*)>(_a[2]))); break;
        case 3: handleSaveChanges(); break;
        case 4: handleTriggerAddMessage(); break;
        case 5: handleTriggerRemoveMessage(); break;
        case 6: handleTriggerRefresh(); break;
        case 7: handleTriggerSetFilter(); break;
        case 8: handleTriggerRemoveField(); break;
        case 9: updateFieldsTable(); break;
        case 10: handleTriggerAddField(); break;
        case 11: handleTriggerGenerate(); break;
        case 12: handleTriggerGenerateSingle(); break;
        case 13: handleMessageFieldEdited(); break;
        case 14: handleFieldTableActivated((*reinterpret_cast< const QModelIndex(*)>(_a[1]))); break;
        case 15: showMessageTableContextMenu((*reinterpret_cast< const QPoint(*)>(_a[1]))); break;
        case 16: cloneContextMessage(); break;
        case 17: test(); break;
        default: ;
        }
        _id -= 18;
    }
    return _id;
}
QT_END_MOC_NAMESPACE
