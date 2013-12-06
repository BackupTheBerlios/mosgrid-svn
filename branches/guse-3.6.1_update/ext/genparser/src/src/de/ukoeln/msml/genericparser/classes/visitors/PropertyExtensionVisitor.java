package de.ukoeln.msml.genericparser.classes.visitors;

import de.mosgrid.msml.util.IDictionary;
import de.ukoeln.msml.genericparser.GenericParserMainDocument;
import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IPropertyExtensionVisitor;

public class PropertyExtensionVisitor extends MSMLExtensionVisitor implements IPropertyExtensionVisitor {

	private String _ref;
	private String _id;
	private String _title;
	private GenericParserMainDocument _doc;
	private IDictionary _dict;

	public PropertyExtensionVisitor(MSMLTreeValueBase val) {
		super(val);
	}
	
	public void setDictRef(String ref) {
		_ref = ref;
	}
	
	public String getDictRef() {
		return _ref;
	}

	public String getRef() {
		return _ref;
	}

	public void setRef(String ref) {
		_ref = ref;
	}

	public String getId() {
		return _id;
	}

	public void setId(String id) {
		_id = id;
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		_title = title;
	}

	@Override
	public GenericParserMainDocument getDoc() {
		return _doc;
	}

	@Override
	public void setDoc(GenericParserMainDocument doc) {
		_doc = doc;
	}

	@Override
	public IDictionary getDict() {
		return _dict;
	}

	@Override
	public void setDict(IDictionary dict) {
		_dict = dict;
	}
}
