package de.ukoeln.msml.genericparser.parameterhandler;

import java.io.File;
import java.io.IOException;

import de.mosgrid.msml.editors.MSMLEditor;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.ukoeln.msml.genericparser.GenericParserMainDocument;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper.ON_EXCEPTION;
import de.ukoeln.msml.genericparser.worker.StringH;

public class OutputLocationHandler extends AbstractParamHandler {

	public OutputLocationHandler() {
		super();
	}

	public OutputLocationHandler(ParameterType param) {
		super(param);
	}

	@Override
	public String getParameterName() {
		return "outputLocation";
	}

	@Override
	public void handle(GenericParserMainDocument doc) {
		MSMLEditor edit = doc.getCurrentHandleInfo().getCurEditor();
		String filename = null;
		if (!StringH.isNullOrEmpty(doc.getParams().getOutput())) {
			filename = doc.getParams().getOutput();
		} else if (_param != null) {
			filename = GenericParserMainDocument.getBaseFolder() + "/" + _param.getScalar().getValue();
		} else {
			filename = "./parsed_msml.xml";
		}
		
		if (!StringH.isNullOrEmpty(filename)) {
			File output = new File(filename);
			if (!output.exists()) {
				try {
					output.createNewFile();
				} catch (IOException e) {
					StackTraceHelper.handleException(e, ON_EXCEPTION.QUIT);
				}				
			}
			edit.marshallTo(output);			
		}
		else
			edit.marshall();

	}

	@Override
	public float getWeight() {
		return 1000;
	}

	@Override
	public IParamHandler getInstance(ParameterType param) {
		return new OutputLocationHandler(param);
	}

}
