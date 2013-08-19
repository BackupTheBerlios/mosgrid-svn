package de.ukoeln.msml.genericparser.classes.visitors;

import java.util.Hashtable;

import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionTextRetriever;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionVisitor;

public class MSMLExtensionVisitor implements IMSMLExtensionVisitor {
	
	private String _text = "";
	private MSMLTreeValueBase _val;
	private static Hashtable<String, Object> _tags = new Hashtable<String, Object>();
	
	public MSMLExtensionVisitor(MSMLTreeValueBase val) {
		_val = val;
	}
	
	public String getText() {
		return _text;
	}
	
	public void setText(String _text) {
		this._text = _text;
	}
	
	public static Object getTag(IMSMLExtensionTextRetriever extension) {
		String className = extension.getClass().getCanonicalName();
		if (!_tags.containsKey(className))
			_tags.put(className, extension.getTag());
		return _tags.get(className);
	}

	public MSMLTreeValueBase getMSMLTreeValue() {
		return _val;
	}	
}
