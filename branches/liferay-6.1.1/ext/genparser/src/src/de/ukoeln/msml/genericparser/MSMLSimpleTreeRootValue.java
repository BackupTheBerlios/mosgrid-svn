package de.ukoeln.msml.genericparser;

import de.ukoeln.msml.genericparser.classes.ClassInfoBase;

public class MSMLSimpleTreeRootValue extends MSMLSimpleTreeValue {

	public MSMLSimpleTreeRootValue(String propName, ClassInfoBase info, GenericParserDocumentBase doc) {
		super(propName, info, doc);
	}

	@Override
	public boolean isRoot() {
		return true;
	}
}
