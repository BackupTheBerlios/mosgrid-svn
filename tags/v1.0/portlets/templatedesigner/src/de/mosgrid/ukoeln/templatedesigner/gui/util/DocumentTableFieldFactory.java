package de.mosgrid.ukoeln.templatedesigner.gui.util;

import com.vaadin.ui.TableFieldFactory;

import de.mosgrid.ukoeln.templatedesigner.document.TDTemplateJobDocument;

public abstract class DocumentTableFieldFactory implements TableFieldFactory {

	private static final long serialVersionUID = -3423225174749768501L;
	private TDTemplateJobDocument _doc;

	public DocumentTableFieldFactory(TDTemplateJobDocument doc) {
		_doc = doc;
	}
	
	protected TDTemplateJobDocument getDoc() {
		return _doc;
	}
}
