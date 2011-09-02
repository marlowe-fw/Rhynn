#ifndef MessageEditDelegate_h__
#define MessageEditDelegate_h__


#include <QtCore/QModelIndex>
#include <QtCore/QMetaProperty>
#include <QtGui/QItemDelegate>



class MessageEditDelegate : public QItemDelegate {
	Q_OBJECT
public:
	//MessageEditDelegate(QObject* parent);
	//void paint(QPainter *painter, const QStyleOptionViewItem &option, const QModelIndex &index) const;
	//QSize sizeHint(const QStyleOptionViewItem &option, const QModelIndex &index) const;
	//bool editorEvent(QEvent *event, QAbstractItemModel *model, const QStyleOptionViewItem &option, const QModelIndex &index);
	//QWidget *createEditor(QWidget *parent, const QStyleOptionViewItem &option, const QModelIndex &index) const;

	void MessageEditDelegate::setEditorData(QWidget *editor, const QModelIndex &index) const {
		if (!editor->metaObject()->userProperty().isValid()) {
			if (editor->property("currentIndex").isValid()) {
				editor->setProperty("currentIndex", index.data());
				return;
			}
		}
		QItemDelegate::setEditorData(editor, index);
	}

	void MessageEditDelegate::setModelData(QWidget *editor, QAbstractItemModel *model, const QModelIndex &index) const {
		if (!editor->metaObject()->userProperty().isValid()) {
			QVariant value = editor->property("currentIndex");
			if (value.isValid()) {
				model->setData(index, value);
				return;
			}
		}
		QItemDelegate::setModelData(editor, model, index);
	}



private:
	QPixmap upDownArrow;	

signals:
	void sequenceOrderChanged();
};


#endif // MessageEditDelegate_h__