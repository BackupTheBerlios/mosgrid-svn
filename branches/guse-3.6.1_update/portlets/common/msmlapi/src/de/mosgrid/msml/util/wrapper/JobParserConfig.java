package de.mosgrid.msml.util.wrapper;

import java.util.List;

import de.mosgrid.msml.editors.MSMLEditor;
import de.mosgrid.msml.enums.Namespaces;
import de.mosgrid.msml.jaxb.bindings.ParameterListType;

public class JobParserConfig extends ParameterListWrapper {

	private final String CONFIGPATTERN = "parserConfig";
	private final String OUTPUTLOCATION = "outputLocation";

	public JobParserConfig(MSMLEditor ed, ParameterListType list) {
		super(ed, list);
	}

	public List<String> getConfigFilePattern() {
		return getAllScalarParameter(Namespaces.PARSER, CONFIGPATTERN);
	}

	public void addConfigFilePattern(String pattern) {
		addScalarParameter(Namespaces.PARSER, CONFIGPATTERN, pattern);
	}
	
	public String getOutputFilename() {
		return getScalarParameter(Namespaces.PARSER, OUTPUTLOCATION);
	}

	public void setOutputFilename(String loc) {
		setScalarParameter(Namespaces.PARSER, OUTPUTLOCATION, loc);
	}
	
	/**
	 * For GenericParser only!
	 */
	public ParameterListType getListForGenParser() {
		return getList();
	}
}
