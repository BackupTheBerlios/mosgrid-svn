package de.mosgrid.msml.util.wrapper;

import java.util.List;

import de.mosgrid.msml.editors.MSMLEditor;

public abstract class MSMLAddableAbstractWrapperBase extends MSMLAbstractWrapperBase {
	
	public MSMLAddableAbstractWrapperBase(MSMLEditor ed) {
		super(ed);
	}

	public abstract Object getJaxBElement();
	
	public void addSelfToList(List<Object> list) {
		list.add(getJaxBElement());
	}
}
