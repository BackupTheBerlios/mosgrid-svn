package de.mosgrid.ukoeln.templatedesigner.document;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import de.mosgrid.msml.enums.DictDir;
import de.mosgrid.msml.enums.Namespaces;
import de.mosgrid.msml.util.DictionaryFactory;
import de.mosgrid.msml.util.IDictionary;

public abstract class TDDocumentBaseNonGeneric {

	private static Hashtable<DictDir, Hashtable<String, List<String>>> _dict2namespace2Entries = new Hashtable<DictDir, Hashtable<String, List<String>>>();

	private void initDictDir(DictDir dir) {
		if (!_dict2namespace2Entries.containsKey(dir)) {
			Hashtable<String, List<String>> namespace2Entries = new Hashtable<String, List<String>>();
			for (IDictionary dict : DictionaryFactory.getInstance().getDictionaries(dir)) {
				ArrayList<String> entries = new ArrayList<String>();
				entries.addAll(dict.getEntryIDs());
				namespace2Entries.put(dict.getNamespace(), entries);
			}
			_dict2namespace2Entries.put(dir, namespace2Entries);
		}
	}

	List<String> getDictionaryEntries(DictDir dir) {
		initDictDir(dir);
		List<String> res = new ArrayList<String>();
		for (List<String> entries : _dict2namespace2Entries.get(dir).values()) {
			res.addAll(entries);
		}
		return res;
	}

	List<String> getDictionaryEntries(DictDir dir, Namespaces adapter) {
		initDictDir(dir);
		return _dict2namespace2Entries.get(dir).get(adapter.getNamespace());
	}
	
	public abstract void onSave();
}
