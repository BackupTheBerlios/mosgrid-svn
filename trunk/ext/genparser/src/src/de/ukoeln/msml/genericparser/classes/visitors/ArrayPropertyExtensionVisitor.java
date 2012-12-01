package de.ukoeln.msml.genericparser.classes.visitors;

import de.ukoeln.msml.genericparser.MSMLTreeValueBase;

public class ArrayPropertyExtensionVisitor extends PropertyExtensionVisitor {

	private String _textForArray;
	private String _delimiter;

	public ArrayPropertyExtensionVisitor(MSMLTreeValueBase val) {
		super(val);
	}

	public void setTextForArray(String combinedParts) {
		_textForArray = combinedParts;
	}

	public String getTextForArray() {
		return _textForArray;
	}
	

	public void setDelimiter(String delimiter) {
		_delimiter = delimiter;
	}
	
	public String getDelimiter() {
		return _delimiter;
	}
}
