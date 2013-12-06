package de.ukoeln.msml.genericparser.classes.visitors;

import java.util.List;

import de.ukoeln.msml.genericparser.MSMLTreeValueBase;

public class LayerExtensionVisitor extends MSMLExtensionVisitor {

	private String _seperator;
	private List<String> _splitParts;
	
	public LayerExtensionVisitor(MSMLTreeValueBase val) {
		super(val);
	}

	public String getSeperator() {
		return _seperator;
	}

	public void setSeperator(String newSep) {
		_seperator = newSep;
	}

	public List<String> getPartsToSplit() {
		return _splitParts;
	}

	public void setPartsToSplit(List<String> splitParts) {
		_splitParts = splitParts;
	}
}
