#include "MessageFieldDelegate.h"
#include <iostream>
#include <QtCore/QEvent>
#include <QtGui/QMouseEvent>
#include <QtSql/QSqlTableModel>
//#include <QrCore/QtItemDataRole>

/*
MessageFieldDelegate::sequenceOrderChanged() {

}*/

#define ORDER_COL_INDEX 7

MessageFieldDelegate::MessageFieldDelegate(QObject* parent) 
: QItemDelegate(parent), upDownArrow(QPixmap(":img/img/spin.png"))
{
}

void MessageFieldDelegate::paint(QPainter *painter, const QStyleOptionViewItem &option, const QModelIndex &index) const {
	if (index.column() != ORDER_COL_INDEX) {
		QStyleOptionViewItemV3 opt = option;
		opt.rect.adjust(0, 0, -1, -1); // since we draw the grid ourselves
		QItemDelegate::paint(painter, opt, index);
	} else {
		const QAbstractItemModel *model = index.model();
		QPalette::ColorGroup cg = (option.state & QStyle::State_Enabled) ?
			(option.state & QStyle::State_Active) ? QPalette::Normal : QPalette::Inactive : QPalette::Disabled;

		if (option.state & QStyle::State_Selected)
			painter->fillRect(option.rect, option.palette.color(cg, QPalette::Highlight));

		int sequenceOrder = model->data(index, Qt::DisplayRole).toInt();
		int width = upDownArrow.width();
		int height = upDownArrow.height();
		int x = option.rect.x() + (option.rect.width() / 2) - (width / 2);
		int y = option.rect.y() + (option.rect.height() / 2) - (height / 2);
		painter->drawPixmap(x, y, upDownArrow);

		drawFocus(painter, option, option.rect.adjusted(0, 0, -1, -1)); // since we draw the grid ourselves
	}

	QPen pen = painter->pen();
	painter->setPen(option.palette.color(QPalette::Mid));
	painter->drawLine(option.rect.bottomLeft(), option.rect.bottomRight());
	painter->drawLine(option.rect.topRight(), option.rect.bottomRight());
	painter->setPen(pen);	
}

QSize MessageFieldDelegate::sizeHint(const QStyleOptionViewItem &option, const QModelIndex &index) const {
	//QItemDelegate::
	if (index.column() == ORDER_COL_INDEX) {
		return QSize(upDownArrow.width(), upDownArrow.height()) + QSize(1, 1);
	}

	return QItemDelegate::sizeHint(option, index) + QSize(1, 1); // since we draw the grid ourselves
}

bool MessageFieldDelegate::editorEvent(QEvent *event, QAbstractItemModel *model, const QStyleOptionViewItem &option, const QModelIndex &index) {

	lastSelectedIndex = index;
	//std::cout << "before: " << index.row() << std::endl;
	//emit beforeEdit(index);
	//std::cout << "after: " << index.row() << std::endl;
	//std::cout << "indexrow: " << index.row() << std::endl;
	if (index.column() != ORDER_COL_INDEX) {
		return QItemDelegate::editorEvent(event, model, option, index);
	} else {
		if (event->type() == QEvent::MouseButtonPress) {
			QMouseEvent *mouseEvent = static_cast<QMouseEvent*>(event);
			int relXPos =  mouseEvent->pos().x() - option.rect.x();
			int middle = option.rect.width() / 2;
			int orderColumn = ORDER_COL_INDEX;
			int order =  model->data(index).toInt();
			int id = model->data(model->index(index.row(), 0)).toInt();

			//std::cout << relXPos << std::endl;

			// we need to find the indexes by comparing values, since one change to the model might change row order
			if (relXPos > middle) {
				// one up
				if (order > 0 && index.row() > 0) {
					QModelIndex prevIndex = model->index(index.row()-1, orderColumn);
					if (prevIndex.isValid()) {
						model->setData(index, 9999);
						model->setData(prevIndex, order);
						model->setData(model->index(model->rowCount()-1, orderColumn), order-1);
					}
				}
			} else {
				// one down
				if (order >= 0 && index.row() < model->rowCount() - 1) {
					QModelIndex nextIndex = model->index(index.row()+1, orderColumn);
					if (nextIndex.isValid()) {
						model->setData(nextIndex, 9999);
						model->setData(index, order+1);
						model->setData(model->index(model->rowCount()-1, orderColumn), order);
					}
				}

			}
			return true;
		}
	}	
	return false;
}




QWidget* MessageFieldDelegate::createEditor(QWidget *parent, const QStyleOptionViewItem &option, const QModelIndex &index) const {
	if (index.column() != ORDER_COL_INDEX) {
		return QItemDelegate::createEditor(parent, option, index);
	} else {
		return 0;
	}
}