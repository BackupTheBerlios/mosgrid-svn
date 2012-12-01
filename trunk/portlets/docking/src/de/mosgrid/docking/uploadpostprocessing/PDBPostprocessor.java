package de.mosgrid.docking.uploadpostprocessing;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.io.FileParsingParameters;
import org.biojava.bio.structure.io.PDBFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.exceptions.PostprocessorCreationException;
import de.mosgrid.exceptions.PostprocessorException;
import de.mosgrid.exceptions.PostprocessorValidationException;
import de.mosgrid.gui.inputmask.uploads.IPostprocessorComponent;
import de.mosgrid.msml.converter.exceptions.MSMLConversionException;
import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.msml.jaxb.bindings.EntryType;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.jaxb.bindings.ScalarType;
import de.mosgrid.msml.util.wrapper.JobInitialization;
import de.mosgrid.util.UploadCollector;
import de.mosgrid.util.interfaces.IUploadPostprocessor;

public class PDBPostprocessor implements IUploadPostprocessor {
	private final Logger LOGGER = LoggerFactory.getLogger(PDBPostprocessor.class);

	private static final String DICT_REF_LIGAND = "PDBCutter.LigName";
	private static final String DICT_REF_CHAIN = "PDBCutter.Chain";

	private ParameterType chainParameterElement;
	private EntryType chainDictEntry;
	private ParameterType ligandParameterElement;
	private EntryType ligandDictEntry;
	private PDBFileReader pdbReader;
	private File pdbFile;
	private Structure pdbStructure;
	private PDBComponent component;

	protected PDBPostprocessor(MSMLTemplate template, JobInitialization initializationElement)
			throws PostprocessorCreationException {
		pdbReader = new PDBFileReader();
		FileParsingParameters parsingParameter = new FileParsingParameters();
		pdbReader.setFileParsingParameters(parsingParameter);
		// parse dict prefix of caddsuite
		String dictPrefix = template.getPrefixToNamespace("http://www.xml-cml.org/dictionary/docking-input/");
		// create new parameter elements
		this.chainParameterElement = createParameterElement(dictPrefix + ":" + DICT_REF_CHAIN);
		this.ligandParameterElement = createParameterElement(dictPrefix + ":" + DICT_REF_LIGAND);

		// get dictionary entries
		this.chainDictEntry = template.getDictEntry(chainParameterElement.getDictRef());
		this.ligandDictEntry = template.getDictEntry(ligandParameterElement.getDictRef());
	}

	private ParameterType createParameterElement(String dictRef) {
		ParameterType paramElement = new ParameterType();
		paramElement.setDictRef(dictRef);
		paramElement.setScalar(new ScalarType());
		paramElement.getScalar().setDataType("xsd:string");

		return paramElement;
	}

	@Override
	public void readUploadedFile(File file) throws PostprocessorException {
		this.pdbFile = file;

		readStructure();
		validateStructure();
		createPostprocessorComponent();
	}

	/**
	 * Reads the pdb file and parses its content
	 * 
	 * @throws PostprocessorCreationException
	 */
	private void readStructure() throws PostprocessorException {
		try {
			this.pdbStructure = pdbReader.getStructure(pdbFile);
		} catch (Exception e) {
			String msg = "Could not read " + pdbFile;
			LOGGER.error(msg, e);
			throw new PostprocessorException(msg, e);
		}
	}

	/**
	 * Validates the parsed pdb structure
	 * 
	 * @throws PostprocessorCreationException
	 */
	private void validateStructure() throws PostprocessorException {
		if (pdbStructure.nrModels() == 0) {
			String msg = "The file " + pdbFile + " does not contain any structure models.";
			LOGGER.error(msg);
			throw new PostprocessorException(msg);
		}
	}

	protected void createPostprocessorComponent() {
		this.component = new PDBComponent(pdbStructure, chainParameterElement, chainDictEntry, ligandParameterElement,
				ligandDictEntry);
	}

	@Override
	public IPostprocessorComponent getUIComponent() {
		return component;
	}

	@Override
	public boolean isValidAndReady() throws PostprocessorValidationException {
		return component.isValid();
	}

	@Override
	public void start() throws PostprocessorException {
		// Nothing to do. PDB file stays as it is. Ligand and Chain is set by select fields.

	}

	@Override
	public File getOriginalFile() {
		return pdbFile;
	}

	@Override
	public Collection<File> getPostprocessedFiles() {
		return Arrays.asList(new File[] { pdbFile });
	}

	@Override
	public void collectUploads(UploadCollector collector, String jobID, String portID) {
		collector.addUpload(pdbFile, portID, jobID);
	}

	@Override
	public void doTemplateIntegrations(JobInitialization initialization) throws MSMLConversionException {
		// add chain and ligand parameter
		initialization.getParamList().getParameter().add(chainParameterElement);
		initialization.getParamList().getParameter().add(ligandParameterElement);

		// TODO: integrate pdb structure
	}

}
