package de.mosgrid.ukoeln.templatedesigner.document;

import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.ukoeln.templatedesigner.document.TDTemplateDocument.TemplateBeanBase;
import de.mosgrid.ukoeln.templatedesigner.gui.TDViewBaseNonGeneric;

public abstract class TDTemplateDocumentBase<T extends TDViewBaseNonGeneric> extends TDDocumentBase<T> {

	private final MSMLTemplate _template;
	private final TDTemplateDocument _mainDoc;
	
	public TDTemplateDocumentBase(TemplateBeanBase tb) {
		super(tb.getTdoc().getMainWindow());
		
		_template = tb.getTdoc().getTemplateBean().getTemplate();
		_mainDoc = tb.getTdoc();
	}
	
	protected TDTemplateDocument getMainDoc() {
		return _mainDoc;
	}

	protected MSMLTemplate getTemplate() {
		return _template;
	}
}
