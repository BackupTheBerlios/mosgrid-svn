package de.ukoeln.msml.genericparser.gui.extension;

import de.ukoeln.msml.genericparser.GenericParserMainDocument;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPuroposeVistorMode;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPurposeVisitor;
import de.ukoeln.msml.genericparser.classes.visitors.VisitorCallBack;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtension;
import de.ukoeln.msml.genericparser.worker.MSMLExtensionHelper;

public abstract class MSMLParserExtensionBase implements IMSMLParserExtension {
	
	private MSMLExtensionHelper _helper;
	private IMSMLParserComponent _component;

	public MSMLParserExtensionBase(MSMLExtensionHelper helper) {
		_helper = helper;
	}
	
	protected GenericParserMainDocument getDoc() {
		return _helper.getDoc();
	}

	public void triggerGeneralPurposeVisitor(GeneralPuroposeVistorMode mode, VisitorCallBack initCallback) {
		_helper.triggerGeneralPurposeVisitor(mode, initCallback);
	}

	public void handleGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
		doHandleGeneralPurposeVisitor(visitor);
	}
	
	public void clear() {
		doClear();
		updateValuesInComponent();
	}

	public void updateValuesInComponent() {
		if (GenericParserMainDocument.useX())
			doUpdateValuesInComponent();
	}

	protected abstract void doUpdateValuesInComponent();

	protected abstract void doClear();

	protected abstract void doHandleGeneralPurposeVisitor(GeneralPurposeVisitor visitor);

	public void initGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
		doInitGeneralPurposeVisitor(visitor);
	}

	protected abstract void doInitGeneralPurposeVisitor(GeneralPurposeVisitor visitor);

	public final IMSMLParserComponent getComponent() {
		if (!GenericParserMainDocument.useX())
			return null;
		if (_component == null) {
			_component = doGetComponent();
		}
		return _component;
	}

	protected abstract IMSMLParserComponent doGetComponent();

	@Override
	public String getName() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
