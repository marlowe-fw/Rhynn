#ifndef MessageFieldDelegate_h__
#define MessageFieldDelegate_h__

#include <QtCore/QModelIndex>
#include <QtGui/QPixmap>
#include <QtCore/QSize>
#include <QtGui/QItemDelegate>
#include <QtGui/QPainter>
#include <QtGui/QStyleOptionViewItem>

QT_FORWARD_DECLARE_CLASS(QPainter)


class MessageFieldDelegate : public QItemDelegate {
	Q_OBJECT
	public:
		MessageFieldDelegate(QObject* parent);
		void paint(QPainter *painter, const QStyleOptionViewItem &option, const QModelIndex &index) const;
		QSize sizeHint(const QStyleOptionViewItem &option, const QModelIndex &index) const;
		bool editorEvent(QEvent *event, QAbstractItemModel *model, const QStyleOptionViewItem &option, const QModelIndex &index);
		QWidget *createEditor(QWidget *parent, const QStyleOptionViewItem &option, const QModelIndex &index) const;

		inline const QModelIndex& getLastSelectedIndex() {return lastSelectedIndex;}

	private:
		QPixmap upDownArrow;
		QModelIndex lastSelectedIndex;

	signals:
		void sequenceOrderChanged();
		void beforeEdit(const QModelIndex& index);
};


#endif // MessageFieldDelegate_h__