package de.ukoeln.msml.genericparser;

import de.ukoeln.msml.genericparser.classes.ClassInfoBase;
import de.ukoeln.msml.genericparser.gui.configuration.ConfigAdvType;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;

public class MSMLAdvTreeRootValue extends MSMLTreeValue {

	public MSMLAdvTreeRootValue(String propName, ClassInfoBase info, GenericParserDocumentBase doc) {
		super(propName, info, doc);
	}

	@Override
	protected GenericParserConfig createConfig() {
		return new ConfigAdvType();
	}

	@Override
	public boolean isRoot() {
		return true;
	}
}
