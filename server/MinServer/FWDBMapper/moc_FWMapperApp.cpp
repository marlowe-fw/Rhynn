/****************************************************************************
** Meta object code from reading C++ file 'FWMapperApp.h'
**
** Created: Fri 2. Sep 13:32:11 2011
**      by: The Qt Meta Object Compiler version 62 (Qt 4.6.4)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#include "FWMapperApp.h"
#if !defined(Q_MOC_OUTPUT_REVISION)
#error "The header file 'FWMapperApp.h' doesn't include <QObject>."
#elif Q_MOC_OUTPUT_REVISION != 62
#error "This file was generated using the moc from 4.6.4. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

QT_BEGIN_MOC_NAMESPACE
static const uint qt_meta_data_fwmapper__FWMapperApp[] = {

 // content:
       4,       // revision
       0,       // classname
       0,    0, // classinfo
       3,   14, // methods
       0,    0, // properties
       0,    0, // enums/sets
       0,    0, // constructors
       0,       // flags
       2,       // signalCount

 // signals: signature, parameters, type, tag, flags
      30,   23,   22,   22, 0x05,
      85,   77,   22,   22, 0x05,

 // slots: signature, parameters, type, tag, flags
     142,  126,  114,   22, 0x0a,

       0        // eod
};

static const char qt_meta_stringdata_fwmapper__FWMapperApp[] = {
    "fwmapper::FWMapperApp\0\0tables\0"
    "schemaTablesChanged(std::vector<std::string>&)\0"
    "mapping\0mappingCreated(std::string&)\0"
    "std::string\0table,className\0"
    "createMap(std::string,std::string)\0"
};

const QMetaObject fwmapper::FWMapperApp::staticMetaObject = {
    { &QObject::staticMetaObject, qt_meta_stringdata_fwmapper__FWMapperApp,
      qt_meta_data_fwmapper__FWMapperApp, 0 }
};

#ifdef Q_NO_DATA_RELOCATION
const QMetaObject &fwmapper::FWMapperApp::getStaticMetaObject() { return staticMetaObject; }
#endif //Q_NO_DATA_RELOCATION

const QMetaObject *fwmapper::FWMapperApp::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->metaObject : &staticMetaObject;
}

void *fwmapper::FWMapperApp::qt_metacast(const char *_clname)
{
    if (!_clname) return 0;
    if (!strcmp(_clname, qt_meta_stringdata_fwmapper__FWMapperApp))
        return static_cast<void*>(const_cast< FWMapperApp*>(this));
    return QObject::qt_metacast(_clname);
}

int fwmapper::FWMapperApp::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = QObject::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    if (_c == QMetaObject::InvokeMetaMethod) {
        switch (_id) {
        case 0: schemaTablesChanged((*reinterpret_cast< std::vector<std::string>(*)>(_a[1]))); break;
        case 1: mappingCreated((*reinterpret_cast< std::string(*)>(_a[1]))); break;
        case 2: { std::string _r = createMap((*reinterpret_cast< std::string(*)>(_a[1])),(*reinterpret_cast< std::string(*)>(_a[2])));
            if (_a[0]) *reinterpret_cast< std::string*>(_a[0]) = _r; }  break;
        default: ;
        }
        _id -= 3;
    }
    return _id;
}

// SIGNAL 0
void fwmapper::FWMapperApp::schemaTablesChanged(std::vector<std::string> & _t1)
{
    void *_a[] = { 0, const_cast<void*>(reinterpret_cast<const void*>(&_t1)) };
    QMetaObject::activate(this, &staticMetaObject, 0, _a);
}

// SIGNAL 1
void fwmapper::FWMapperApp::mappingCreated(std::string & _t1)
{
    void *_a[] = { 0, const_cast<void*>(reinterpret_cast<const void*>(&_t1)) };
    QMetaObject::activate(this, &staticMetaObject, 1, _a);
}
QT_END_MOC_NAMESPACE
