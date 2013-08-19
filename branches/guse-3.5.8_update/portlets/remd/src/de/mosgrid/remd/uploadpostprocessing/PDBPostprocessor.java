package de.mosgrid.remd.uploadpostprocessing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.PDBHeader;
import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.StructureImpl;
import org.biojava.bio.structure.io.FileParsingParameters;
import org.biojava.bio.structure.io.PDBFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.exceptions.PostprocessorCreationException;
import de.mosgrid.exceptions.PostprocessorException;
import de.mosgrid.exceptions.PostprocessorValidationException;
import de.mosgrid.gui.inputmask.uploads.IPostprocessorComponent;
import de.mosgrid.msml.converter.exceptions.MSMLConversionException;
import de.mosgrid.msml.converter.pdb.PDB2MSMLConverter;
import de.mosgrid.msml.jaxb.bindings.MoleculeType;
import de.mosgrid.msml.util.wrapper.JobInitialization;
import de.mosgrid.util.TempFileHelper;
import de.mosgrid.util.UploadCollector;
import de.mosgrid.util.interfaces.IUploadPostprocessor;

/**
 * A postprocessor for PDB files
 * 
 * @author Andreas Zink
 * 
 */
public class PDBPostprocessor implements IUploadPostprocessor {
	private final Logger LOGGER = LoggerFactory.getLogger(PDBPostprocessor.class);

	private PDBFileReader pdbReader;
	private PDBGroup[] allowedGroups;
	private File pdbFile;
	private File postprocessedFile;
	// private File jsonFormattedFile;
	private Structure pdbStructure;
	private Structure postprocessedStructure;
	private PDBComponent component;

	/**
	 * @param groups
	 *            The groups which shall be parsed and available for selction
	 */
	public PDBPostprocessor(PDBGroup... groups) {
		this.allowedGroups = groups;
		pdbReader = new PDBFileReader();
		FileParsingParameters parsingParameter = new FileParsingParameters();
		parsingParameter.setParseSecStruc(true);
		pdbReader.setFileParsingParameters(parsingParameter);
	}

	@Override
	public void readUploadedFile(File file) throws PostprocessorException {
		this.pdbFile = file;
		readStructure();
		validateStructure();
		createPostprocessorComponent();
	}

	protected void createPostprocessorComponent() {
		this.component = new PDBComponent(pdbStructure, allowedGroups);
	}

	@Override
	public boolean isValidAndReady() throws PostprocessorValidationException {
		return component.isValid();
	}

	@Override
	public void start() throws PostprocessorException {
		postprocessedStructure = new StructureImpl();
		if (pdbStructure.getPDBHeader() != null) {
			postprocessedStructure.setPDBHeader(pdbStructure.getPDBHeader());
		}
		for (Chain chain : component.getSelectedChains()) {
			postprocessedStructure.addChain(chain);
		}

		String filename = TempFileHelper.removeTempSuffix(pdbFile);
		String filecontent = createFileContent();
		File file = null;
		FileWriter fw = null;
		try {
			file = TempFileHelper.createTempFile(filename);
			fw = new FileWriter(file);
			fw.write(filecontent);
		} catch (IOException e) {
			String msg = "Error while creating postprocessed file for " + pdbFile;
			LOGGER.error(msg, e);
			throw new PostprocessorException(msg, e);
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					LOGGER.warn("Could not close FileWriter for " + file);
				}
			}
		}
		this.postprocessedFile = file;
	}

	@Override
	public void collectUploads(UploadCollector collector, String jobID, String portID) {
		collector.addUpload(postprocessedFile, portID, jobID);
	}

	@Override
	public void doTemplateIntegrations(JobInitialization initialization) throws MSMLConversionException{
		PDB2MSMLConverter converter = new PDB2MSMLConverter();
			MoleculeType moleculeElement = converter.convert(postprocessedStructure);
			initialization.setMolecule(moleculeElement);
	}

	/**
	 * Reads the pdb file and parses its content
	 * 
	 * @throws PostprocessorCreationException
	 */
	private void readStructure() throws PostprocessorException {
		try {
			pdbStructure = pdbReader.getStructure(pdbFile);
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
			throw new PostprocessorException(msg);
		}
	}

	@Override
	public File getOriginalFile() {
		return this.pdbFile;
	}

	@Override
	public Collection<File> getPostprocessedFiles() {
		Collection<File> files = new ArrayList<File>();
		files.add(postprocessedFile);
		return files;
	}

	/**
	 * Creates the content of the postprocessed pdb file. Only selected chains are written to the new file.
	 */
	private String createFileContent() {
		StringBuilder pdbBuilder = new StringBuilder();
		addHeader(pdbBuilder);

		for (Chain chain : postprocessedStructure.getChains()) {
			for (Group group : chain.getAtomGroups()) {
				for (Atom atom : group.getAtoms()) {
					pdbBuilder.append(atom.toPDB());
				}
			}
		}
		return pdbBuilder.toString();
	}

	private void addHeader(StringBuilder pdbBuilder) {
		PDBHeader header = pdbStructure.getPDBHeader();
		if (header != null) {
			// use pdb header if available
			pdbBuilder.append(header.toPDB());
			pdbBuilder.append("REMARK 999 THIS IS A POSTPROCESSED VERSION OF THE ORIGINAL PDB FILE\n");
		} else {
			// create some title
			pdbBuilder.append("TITLE     AUTO GENERATED MOSGRID INPUT FILE\n");
		}

	}

	@Override
	public IPostprocessorComponent getUIComponent() {
		return component;
	}

}
