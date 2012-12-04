package de.ukoeln.msml.genericparser.parameterhandler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import de.mosgrid.msml.editors.MSMLEditor;
import de.mosgrid.msml.jaxb.bindings.MoleculeType;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.msml.util.wrapper.JobFinalization;
import de.ukoeln.msml.genericparser.GenericParserMainDocument;
import de.ukoeln.msml.genericparser.worker.FileMatchWalker;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper.ON_EXCEPTION;

public class MoleculeInfoHandler extends AbstractParamHandler {

	public MoleculeInfoHandler() {
		super();
	}

	public MoleculeInfoHandler(ParameterType param) {
		super(param);
	}

	@Override
	public String getParameterName() {
		return "moleculeInfo";
	}

	@Override
	public float getWeight() {
		return 0;
	}

	@Override
	public void handle(GenericParserMainDocument doc) {

		FileMatchWalker fmw = new FileMatchWalker(GenericParserMainDocument.getBaseFolder());
		List<File> files = null;
		try {
			files = fmw.getMatchingFiles(Pattern.compile(_param.getScalar().getValue()));
		} catch (IOException e) {
			StackTraceHelper.handleException(e, ON_EXCEPTION.QUIT);
		}

		if (files == null || files.size() != 1)
			StackTraceHelper.handleException(new Throwable("There is not exactly one file" +
					" with moleculeinformation. Found: " + 
					(files == null ? "<null>" : files.size()) + files.size()), ON_EXCEPTION.QUIT);
		// TODO total crap
		MSMLEditor anonObject = new MSMLEditor(files.get(0));
		MoleculeType molecule = (MoleculeType) anonObject.getRootElement();
		
		Job curJob = doc.getCurrentHandleInfo().getCurJob();
		if (curJob.getFinalization() == null) {
			curJob.setFinalization(new JobFinalization(anonObject));
		}
		curJob.getFinalization().setMolecule(molecule);
	}

	@Override
	public IParamHandler getInstance(ParameterType param) {
		return new MoleculeInfoHandler(param);
	}
}
