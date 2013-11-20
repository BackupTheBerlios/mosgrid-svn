package de.mosgrid.ukoeln.templatedesigner.gui;

import de.mosgrid.ukoeln.templatedesigner.document.TDDocumentBaseNonGeneric;

public abstract class TDViewBase<T extends TDDocumentBaseNonGeneric> extends TDViewBaseNonGeneric implements
		IInitialMinSizePane {

	private static final long serialVersionUID = -7015037399508584157L;
	private T _doc;

	@SuppressWarnings("unchecked")
	public void init(TDDocumentBaseNonGeneric doc) {
		_doc = (T) doc;
		doInit();
	}

	protected abstract void doInit();

	protected T getDoc() {
		return _doc;
	}
	
	public void onSave() {
		getDoc().onSave();
	}

}
