package de.ukoeln.msml.genericparser.classes;

import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionVisitor;


public abstract class MSMLCustomClassInfo<T extends IMSMLExtensionVisitor, M> 
	extends CustomClassInfo<T, M> {

	private Class<?> _class;

	public MSMLCustomClassInfo(String name, Class<? extends M> _class) {
		super(name);
		this._class = _class;
	}

	@SuppressWarnings("unchecked")
	protected void initObject() {
		setObject((M)MSMLFactory.createInstance(getClassObj()));
	}

	private Class<?> getClassObj() {
		return _class;
	}
}
