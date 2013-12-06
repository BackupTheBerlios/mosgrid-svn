package de.ukoeln.msml.genericparser.parameterhandler;

import java.io.File;

import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.ukoeln.msml.genericparser.GenericParserMainDocument;
import de.ukoeln.msml.genericparser.worker.StringH;

public abstract class AbstractOutputHandler extends AbstractParamHandler {
	
	public AbstractOutputHandler() {
		super();
	}
	
	public AbstractOutputHandler(ParameterType param) {
		super(param);
	}
	
	@Override
	public String getParameterName() {
		return "outputLocation";
	}

	protected String calcOutputFileName(GenericParserMainDocument doc) {
		String filename = null;
		if (!StringH.isNullOrEmpty(doc.getParams().getOutput())) {
			filename = doc.getParams().getOutput();
		} else if (_param != null) {
			String baseFolder = GenericParserMainDocument.getBaseFolder();
			if (!baseFolder.endsWith(File.separator))
				baseFolder += File.separator;
			filename = baseFolder + _param.getScalar().getValue();
		} else {
			filename = "." + File.separator + "parsed_msml.xml";
		}
		return filename;
	}
}
