package de.ukoeln.msml.genericparser.parameterhandler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.mosgrid.msml.editors.JobListEditor;
import de.mosgrid.msml.editors.MSMLEditor;
import de.mosgrid.msml.jaxb.bindings.Cml;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.msml.util.wrapper.JobFinalization;
import de.ukoeln.msml.genericparser.DictionaryDocument;
import de.ukoeln.msml.genericparser.GenericParserMainDocument;
import de.ukoeln.msml.genericparser.gui.interfaces.swingimpl.DictTableModelSwingImpl.DictRefData;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper.ON_EXCEPTION;

public class ConfigHandler extends AbstractParamHandler {

	public ConfigHandler() {
		super();
	}

	public ConfigHandler(ParameterType param) {
		super(param);
	}

	@Override
	public String getParameterName() {
		return "parserConfig";
	}

	@Override
	public void handle(GenericParserMainDocument doc) {

		if (!doc.isMSMLTestdrive())
			loadConfig(doc);
		
		Cml cml = doc.getCml();
		JobListEditor jle = new JobListEditor(cml);
		
		Job curJob = doc.getCurrentHandleInfo().getCurJob();
		if (curJob.getFinalization() == null) {
			curJob.setFinalization(new JobFinalization(jle));
		}

		JobFinalization parsedFinal = jle.getJobListElement().getJobs().get(0).getFinalization();
		curJob.getFinalization().setPropertyList(parsedFinal.getPropertyList());
		if (parsedFinal.getMolecule() != null)
			curJob.getFinalization().setMolecule(parsedFinal.getMolecule());
		
		// add dictionaries not contained in the original msml file
		MSMLEditor edit = doc.getCurrentHandleInfo().getCurEditor(); 
		for (DictRefData dict : doc.getDictDoc().getData().getActiveDicts()) {
			if (edit.hasNamespace(dict.getNamespace()))
				continue;
			edit.addNamespace(dict.getPrefix(), dict.getNamespace());
		}
	}

	private void loadConfig(GenericParserMainDocument doc) {
		List<String> matches = new ArrayList<String>();
		Pattern pat = Pattern.compile(_param.getScalar().getValue());
		try {
			List<String> lists = ResourcesHelper.getResourceListing(DictionaryDocument.class, "configfiles/", "xml");
			for (String file : lists) {
				if (pat.matcher(file).find())
					matches.add(file);
			}
		} catch (URISyntaxException e) {
			StackTraceHelper.handleException(e, ON_EXCEPTION.QUIT);
		} catch (IOException e) {
			StackTraceHelper.handleException(e, ON_EXCEPTION.QUIT);
		}
		
		if (matches.size() > 1)
			throw new UnsupportedOperationException("Found more than one config that matches pattern.");
		else if (matches.size() < 1)
			throw new UnsupportedOperationException("Found no config that matches pattern.");
		doc.loadConfig(ResourcesHelper.getStream(matches.get(0)));
		
//		FileMatchWalker fmw = new FileMatchWalker(GenericParserMainDocument.getBaseFolder());
//		List<File> files = null;
//		try {
//			files = fmw.getMatchingFiles(Pattern.compile(_param.getScalar().getValue()));
//		} catch (IOException e) {
//			StackTraceHelper.handleException(e, ON_EXCEPTION.QUIT);
//		}
//
//		if (files == null || files.size() != 1)
//			StackTraceHelper.handleException(new Throwable("There is not exactly one file" +
//					" for parser configuration. Found: " + 
//					(files == null ? "<null>" : files.size()) + files.size()), ON_EXCEPTION.QUIT);
//		doc.loadConfig(files.get(0));
	}

	@Override
	public float getWeight() {
		return -1000;
	}

	@Override
	public IParamHandler getInstance(ParameterType param) {
		return new ConfigHandler(param);
	}
	
}
