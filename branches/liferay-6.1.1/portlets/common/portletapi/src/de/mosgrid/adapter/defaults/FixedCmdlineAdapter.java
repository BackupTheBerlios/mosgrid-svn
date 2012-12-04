package de.mosgrid.adapter.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mosgrid.adapter.AdapterException;
import de.mosgrid.adapter.AdapterForMSMLWithDictionary;
import de.mosgrid.adapter.InputInfoForFixedCmdLine;
import de.mosgrid.adapter.base.InputInfo;
import de.mosgrid.msml.jaxb.bindings.AdapterConfigType;
import de.mosgrid.msml.jaxb.bindings.EntryType;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.util.DictionaryFactory;
import de.mosgrid.msml.util.IDictionary;
import de.mosgrid.msml.util.helper.XmlHelper;
import de.mosgrid.msml.util.wrapper.Job;

public class FixedCmdlineAdapter extends AdapterForMSMLWithDictionary {

	private static final String FPSUFFIX = "fixedpos.dictref";

	@Override
	protected String getNamespace() {
		return "http://www.xml-cml.org/dictionary/adapter/cmdlinefixed/";
	}

	@Override
	public void doInit() {
	}

	@Override
	public InputInfo calculateAdaption() throws AdapterException {
		Job job = this.getInfo().getJob();
		AdapterConfigType conf = this.getInfo().getConfig();
		String fixedAdapPrefix = this.getInfo().getMSML().getPrefixToNamespace(getNamespace());
		String dictRefToLookFor = fixedAdapPrefix + ":" + FPSUFFIX;

		List<ParameterType> filteredParams = new ArrayList<ParameterType>();
		for (ParameterType e : conf.getParameter()) {
			if (e.getDictRef() != null && e.getDictRef().equals(dictRefToLookFor))
				filteredParams.add(e);
		}

		List<String> args = new ArrayList<String>();
		for (ParameterType e : filteredParams) {
			if (e.getScalar() == null || e.getScalar().getValue() == null) {
				args.add(null);
				continue;
			}

			String val = e.getScalar().getValue();
			String dictPrefix = XmlHelper.getInstance().getPrefix(val);
			String entryID = XmlHelper.getInstance().getSuffix(val);

			// we have a valid prefix => find value within parameters in
			// initialization corresponding
			// to dictref. If dictionary could not be found, then use existing
			// commandline-args.
			if (dictPrefix != null && !"".equals(dictPrefix)) {
				IDictionary foundDict = null;
				Collection<IDictionary> dicts = DictionaryFactory.getInstance().getAllDictionaries();
				for (IDictionary dict : dicts) {
					if (dict.getDictPrefix().equals(dictPrefix)) {
						foundDict = dict;
						break;
					}
				}
				if (foundDict == null) {
					args.add(null);
					continue;
				}

				// here we have a parameter referring to a parameter in the initialization section.
				// If corresponding dictEntry is flag, then use term. Otherwise search for
				// corresponding parameter in initialization and use its value.
				EntryType entry = foundDict.getEntryById(entryID);
				if (entry == null)
					throw new AdapterException("Entry " + entryID + " in Dictionary " + 
							foundDict.getDictPrefix() + " not found.");
				
				if (entry.isFlag() != null && entry.isFlag()) {
					args.add(entry.getTerm());
					continue;
				}

				String prefInTemplate = getInfo().getMSML().getPrefixToNamespace(foundDict.getNamespace());
				String dictRefToLookForInInit = prefInTemplate + ":" + entryID;

				List<ParameterType> params = job.getInitialization().getParamList().getParameter();
				for (ParameterType param : params) {
					if (param.getDictRef() != null && 
							param.getDictRef().equals(dictRefToLookForInInit)
							&& param.getScalar() != null) {
						// we have found the correct parameter and can add this to the
						// args to be used.
						args.add(param.getScalar().getValue());
						break;
					}	
				}

			} else { // no valid prefix => use existing commandline-arg.
				args.add(null);
				continue;
			}
		}
		InputInfoForFixedCmdLine res = new InputInfoForFixedCmdLine(args, getInfo());
		return res;
	}
}
