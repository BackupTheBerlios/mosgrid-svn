package de.ukoeln.msml.genericparser.parameterhandler;

import java.io.File;
import java.io.IOException;

import de.mosgrid.msml.editors.MSMLEditor;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.ukoeln.msml.genericparser.GenericParserMainDocument;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper.ON_EXCEPTION;
import de.ukoeln.msml.genericparser.worker.StringH;

public class OutputLocationHandler extends AbstractOutputHandler {

	public OutputLocationHandler() {
		super();
	}

	public OutputLocationHandler(ParameterType param) {
		super(param);
	}

	@Override
	public void handle(GenericParserMainDocument doc) {
		MSMLEditor edit = doc.getCurrentHandleInfo().getCurEditor();
		String filename = calcOutputFileName(doc);

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
		} else {
			edit.marshall();
		}
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
