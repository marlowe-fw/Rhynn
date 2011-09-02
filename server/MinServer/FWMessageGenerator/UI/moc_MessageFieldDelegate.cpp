/****************************************************************************
** Meta object code from reading C++ file 'MessageFieldDelegate.h'
**
** Created: Wed 31. Aug 19:37:49 2011
**      by: The Qt Meta Object Compiler version 62 (Qt 4.6.4)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#include "MessageFieldDelegate.h"
#if !defined(Q_MOC_OUTPUT_REVISION)
#error "The header file 'MessageFieldDelegate.h' doesn't include <QObject>."
#elif Q_MOC_OUTPUT_REVISION != 62
#error "This file was generated using the moc from 4.6.4. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

QT_BEGIN_MOC_NAMESPACE
static const uint qt_meta_data_MessageFieldDelegate[] = {

 // content:
       4,       // revision
       0,       // classname
       0,    0, // classinfo
       2,   14, // methods
       0,    0, // properties
       0,    0, // enums/sets
       0,    0, // constructors
       0,       // flags
       2,       // signalCount

 // signals: signature, parameters, type, tag, flags
      22,   21,   21,   21, 0x05,
      51,   45,   21,   21, 0x05,

       0        // eod
};

static const char qt_meta_stringdata_MessageFieldDelegate[] = {
    "MessageFieldDelegate\0\0sequenceOrderChanged()\0"
    "index\0beforeEdit(QModelIndex)\0"
};

const QMetaObject MessageFieldDelegate::staticMetaObject = {
    { &QItemDelegate::staticMetaObject, qt_meta_stringdata_MessageFieldDelegate,
      qt_meta_data_MessageFieldDelegate, 0 }
};

#ifdef Q_NO_DATA_RELOCATION
const QMetaObject &MessageFieldDelegate::getStaticMetaObject() { return staticMetaObject; }
#endif //Q_NO_DATA_RELOCATION

const QMetaObject *MessageFieldDelegate::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->metaObject : &staticMetaObject;
}

void *MessageFieldDelegate::qt_metacast(const char *_clname)
{
    if (!_clname) return 0;
    if (!strcmp(_clname, qt_meta_stringdata_MessageFieldDelegate))
        return static_cast<void*>(const_cast< MessageFieldDelegate*>(this));
    return QItemDelegate::qt_metacast(_clname);
}

int MessageFieldDelegate::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = QItemDelegate::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    if (_c == QMetaObject::InvokeMetaMethod) {
        switch (_id) {
        case 0: sequenceOrderChanged(); break;
        case 1: beforeEdit((*reinterpret_cast< const QModelIndex(*)>(_a[1]))); break;
        default: ;
        }
        _id -= 2;
    }
    return _id;
}

// SIGNAL 0
void MessageFieldDelegate::sequenceOrderChanged()
{
    QMetaObject::activate(this, &staticMetaObject, 0, 0);
}

// SIGNAL 1
void MessageFieldDelegate::beforeEdit(const QModelIndex & _t1)
{
    void *_a[] = { 0, const_cast<void*>(reinterpret_cast<const void*>(&_t1)) };
    QMetaObject::activate(this, &staticMetaObject, 1, _a);
}
QT_END_MOC_NAMESPACE
