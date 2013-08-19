package de.mosgrid.msml.util.wrapper;

import de.mosgrid.msml.editors.MSMLEditor;


public abstract class MSMLAbstractWrapperBase {
	
	private final MSMLEditor editor;
	
	public MSMLAbstractWrapperBase(MSMLEditor ed) {
		editor = ed;
	}
	
	MSMLEditor getEditor() {
		return editor;
	}
}
