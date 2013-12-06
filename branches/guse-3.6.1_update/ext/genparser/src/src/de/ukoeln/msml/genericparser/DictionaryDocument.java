package de.ukoeln.msml.genericparser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import de.mosgrid.msml.editors.JobListEditor;
import de.mosgrid.msml.util.DictionaryFactory;
import de.mosgrid.msml.util.IDictionary;
import de.mosgrid.msml.util.helper.XmlHelper;
import de.ukoeln.msml.genericparser.gui.IDictChangeAware;
import de.ukoeln.msml.genericparser.gui.interfaces.swingimpl.DictTableModelSwingImpl;
import de.ukoeln.msml.genericparser.gui.interfaces.swingimpl.DictTableModelSwingImpl.DictRefData;
import de.ukoeln.msml.genericparser.parameterhandler.ResourcesHelper;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper.ON_EXCEPTION;

public class DictionaryDocument {

	// private GenericParserMainDocument _doc;
	private DictTableModelSwingImpl _model;
	private String[] _dictDirs = new String[] { "md/", "qc/", "docking/", "remd/" };

	public DictionaryDocument(GenericParserMainDocument mainDoc) {
		// _doc = mainDoc;
	}

	public void Init(StartupParams params) {
		_model = new DictTableModelSwingImpl(getDicts());
	}

	public DictTableModelSwingImpl getData() {
		return _model;
	}

	private Collection<IDictionary> getDicts() {
		DictionaryFactory fact = DictionaryFactory.getInstance(false);
		Collection<IDictionary> dicts = new ArrayList<IDictionary>();

		for (String dir : _dictDirs) {
			List<String> resources = null;
			try {
				resources = ResourcesHelper.getResourceListing(DictionaryDocument.class, dir, "cml");
			} catch (URISyntaxException e) {
				StackTraceHelper.handleException(e, ON_EXCEPTION.QUIT);
				return null;
			} catch (IOException e) {
				StackTraceHelper.handleException(e, ON_EXCEPTION.QUIT);
				return null;
			}
			dicts.addAll(fact.getDictionaries(resources));
		}

		return dicts;
	}

	public void addDictRow() {
		_model.addData();
	}

	public void removeDictRow(int selectedRow) {
		_model.removeData(selectedRow);
	}

	private List<DictRefData> getActiveDicts() {
		return _model.getActiveDicts();
	}

	public void registerDictChange(final IDictChangeAware handler) {
		_model.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				handler.dictsChanged(getActiveDicts());
			}
		});
	}

	public void replacePrefixes(JobListEditor msmlEdit) {
		_model.replacePrefixes(msmlEdit);
	}

	public void restorePrefixes() {
		_model.restorePrefixes();
	}

	public String getNamespaceToDictref(String dictRef) {
		String dict = XmlHelper.getInstance().getPrefix(dictRef);
		for (DictRefData dictData : _model.getActiveDicts()) {
			if (dictData.getPrefix().equals(dict)) {
				return dictData.getNamespace();
			}
		}
		return null;
	}

	public IDictionary getIDictionaryToDict(String dict) {
		return getData().getIDictionaryToDict(dict);
	}
}
