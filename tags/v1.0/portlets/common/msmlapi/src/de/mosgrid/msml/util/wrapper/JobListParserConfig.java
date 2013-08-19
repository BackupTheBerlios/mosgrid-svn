package de.mosgrid.msml.util.wrapper;

import de.mosgrid.msml.editors.MSMLEditor;
import de.mosgrid.msml.enums.Namespaces;
import de.mosgrid.msml.jaxb.bindings.ParameterListType;

public class JobListParserConfig extends ParameterListWrapper {

	private final String JOBNAME = "parserJobId";
	private final String PORTNAME = "parserJobPort";

	public JobListParserConfig(MSMLEditor ed, ParameterListType list) {
		super(ed, list);
	}

	public String getJobName() {
		return getScalarParameter(Namespaces.PARSER, JOBNAME);
	}

	public void setJobName(String jobname) {
		setScalarParameter(Namespaces.PARSER, JOBNAME, jobname);
	}
	
	public String getPortName() {
		return getScalarParameter(Namespaces.PARSER, PORTNAME);
	}

	public void setPortName(String portName) {
		setScalarParameter(Namespaces.PARSER, PORTNAME, portName);
	}
}
