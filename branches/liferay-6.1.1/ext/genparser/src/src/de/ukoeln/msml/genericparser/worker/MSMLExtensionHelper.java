package de.ukoeln.msml.genericparser.worker;

import de.ukoeln.msml.genericparser.GenericParserMainDocument;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPuroposeVistorMode;
import de.ukoeln.msml.genericparser.classes.visitors.VisitorCallBack;

public class MSMLExtensionHelper {

	GenericParserMainDocument doc;

	public MSMLExtensionHelper(GenericParserMainDocument doc) {
		super();
		this.doc = doc;
	}
	
	public void triggerGeneralPurposeVisitor(GeneralPuroposeVistorMode mode, VisitorCallBack initCallback) {
		doc.triggerGeneralPurposeVisitor(mode, initCallback);
	}

	public GenericParserMainDocument getDoc() {
		return doc;
	}
}
