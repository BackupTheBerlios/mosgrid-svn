package de.ukoeln.msml.genericparser.gui.extension.interfaces;

import de.mosgrid.msml.util.IDictionary;
import de.ukoeln.msml.genericparser.GenericParserMainDocument;

public interface IPropertyExtensionVisitor extends IMSMLExtensionVisitor {

	String getDictRef();
	void setDictRef(String ref);
	String getId();
	void setId(String id);
	String getTitle();
	void setTitle(String title);
	GenericParserMainDocument getDoc();
	void setDoc(GenericParserMainDocument doc);
	IDictionary getDict();
	void setDict(IDictionary iDictionaryToDict);
}
