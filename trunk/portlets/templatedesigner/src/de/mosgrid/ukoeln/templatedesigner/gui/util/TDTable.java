package de.mosgrid.ukoeln.templatedesigner.gui.util;

import com.vaadin.data.Container;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;

import de.mosgrid.ukoeln.templatedesigner.document.TDTemplateJobDocument;

public abstract class TDTable extends Table {

	private static final long serialVersionUID = 7064691497569729988L;

	public void init(Container dataSource, TDTemplateJobDocument doc, Object[] visProps) {
		setContainerDataSource(dataSource);
		setTableFieldFactory(getTableFieldFactory_internal(doc));
		setEditable(true);
		setSelectable(true);
		setImmediate(true);

		setHeaders();

		setPageLength(size());
		setVisibleColumns(visProps);
	}
	
	protected abstract void setHeaders();

	protected abstract TableFieldFactory getTableFieldFactory_internal(TDTemplateJobDocument doc);
}
