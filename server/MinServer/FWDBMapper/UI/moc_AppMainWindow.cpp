/****************************************************************************
** Meta object code from reading C++ file 'AppMainWindow.h'
**
** Created: Fri 2. Sep 13:32:11 2011
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
static const uint qt_meta_data_fwmapper__AppMainWindow[] = {

 // content:
       4,       // revision
       0,       // classname
       0,    0, // classinfo
       5,   14, // methods
       0,    0, // properties
       0,    0, // enums/sets
       0,    0, // constructors
       0,       // flags
       1,       // signalCount

 // signals: signature, parameters, type, tag, flags
      45,   25,   24,   24, 0x05,

 // slots: signature, parameters, type, tag, flags
      86,   24,   24,   24, 0x0a,
     108,  101,   24,   24, 0x0a,
     165,  157,   24,   24, 0x0a,
     196,   24,   24,   24, 0x0a,

       0        // eod
};

static const char qt_meta_stringdata_fwmapper__AppMainWindow[] = {
    "fwmapper::AppMainWindow\0\0tableName,className\0"
    "triggerGenerate(std::string,std::string)\0"
    "onTryConnect()\0tables\0"
    "onSchemaTablesChanged(std::vector<std::string>&)\0"
    "mapping\0onMappingCreated(std::string&)\0"
    "onGenerateClicked()\0"
};

const QMetaObject fwmapper::AppMainWindow::staticMetaObject = {
    { &QMainWindow::staticMetaObject, qt_meta_stringdata_fwmapper__AppMainWindow,
      qt_meta_data_fwmapper__AppMainWindow, 0 }
};

#ifdef Q_NO_DATA_RELOCATION
const QMetaObject &fwmapper::AppMainWindow::getStaticMetaObject() { return staticMetaObject; }
#endif //Q_NO_DATA_RELOCATION

const QMetaObject *fwmapper::AppMainWindow::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->metaObject : &staticMetaObject;
}

void *fwmapper::AppMainWindow::qt_metacast(const char *_clname)
{
    if (!_clname) return 0;
    if (!strcmp(_clname, qt_meta_stringdata_fwmapper__AppMainWindow))
        return static_cast<void*>(const_cast< AppMainWindow*>(this));
    return QMainWindow::qt_metacast(_clname);
}

int fwmapper::AppMainWindow::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = QMainWindow::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    if (_c == QMetaObject::InvokeMetaMethod) {
        switch (_id) {
        case 0: triggerGenerate((*reinterpret_cast< std::string(*)>(_a[1])),(*reinterpret_cast< std::string(*)>(_a[2]))); break;
        case 1: onTryConnect(); break;
        case 2: onSchemaTablesChanged((*reinterpret_cast< std::vector<std::string>(*)>(_a[1]))); break;
        case 3: onMappingCreated((*reinterpret_cast< std::string(*)>(_a[1]))); break;
        case 4: onGenerateClicked(); break;
        default: ;
        }
        _id -= 5;
    }
    return _id;
}

// SIGNAL 0
void fwmapper::AppMainWindow::triggerGenerate(std::string _t1, std::string _t2)
{
    void *_a[] = { 0, const_cast<void*>(reinterpret_cast<const void*>(&_t1)), const_cast<void*>(reinterpret_cast<const void*>(&_t2)) };
    QMetaObject::activate(this, &staticMetaObject, 0, _a);
}
QT_END_MOC_NAMESPACE
