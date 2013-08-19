package de.mosgrid.msml.converter.pdb;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.msml.converter.IFromMSMLConverter;
import de.mosgrid.msml.converter.exceptions.MSMLConversionException;
import de.mosgrid.msml.jaxb.bindings.AtomArrayType;
import de.mosgrid.msml.jaxb.bindings.AtomType;
import de.mosgrid.msml.jaxb.bindings.MoleculeClassType;
import de.mosgrid.msml.jaxb.bindings.MoleculeType;
import de.mosgrid.msml.jaxb.bindings.OriginType;

/**
 * This is a MSML -> PDB converter
 * 
 * @author Andreas Zink
 * 
 */
public class MSML2PDBConverter implements IFromMSMLConverter {
	private final Logger LOGGER = LoggerFactory.getLogger(MSML2PDBConverter.class);
	// desired pdb coordinate format
	private static final DecimalFormat d3 = (DecimalFormat) NumberFormat.getInstance(java.util.Locale.UK);
	static {
		d3.setMaximumIntegerDigits(3);
		d3.setMinimumFractionDigits(3);
		d3.setMaximumFractionDigits(3);
	}

	@Override
	public String convert(MoleculeType moleculeElement, OutputStream os) throws MSMLConversionException {
		OutputStreamWriter writer = new OutputStreamWriter(os);
		BufferedWriter bufWriter = new BufferedWriter(writer);
		return convert(moleculeElement, bufWriter);
	}

	/**
	 * Converts a given MSML molecule element to PDB format
	 * 
	 * @param writer
	 *            Optional outputstream e.g. to a file
	 * @return The PDB formatted MSML content
	 */
	public String convert(MoleculeType moleculeElement, BufferedWriter writer) throws MSMLConversionException {
		LOGGER.trace("Trying to convert MSML molecule element to PDB format");

		if (moleculeElement.getOrigin() == null) {
			throw new MSMLConversionException("Can not create PDB from MSML without known origin attribute");
		}

		PDBBuilder pdbBuilder = new PDBBuilder(writer);
		try {
			// set pdb title if given
			String title = moleculeElement.getTitle();
			if (title != null) {
				PDBLineBuilder titleLine = new PDBLineBuilder();
				titleLine.insert("TITLE", 0, 5, ValueAlignment.Left);
				titleLine.insert(title, 10, 10 + title.length(), ValueAlignment.Left);
				pdbBuilder.append(titleLine);
			}
			if (moleculeElement.getOrigin() == OriginType.PDB) {
				parseFromPdbOrigin(moleculeElement, pdbBuilder);
			} else if (moleculeElement.getOrigin() == OriginType.GRO) {
				parseFromGroOrigin(moleculeElement, pdbBuilder);
			} else {
				throw new MSMLConversionException("The given molecule origin " + moleculeElement.getOrigin()
						+ " is not supported");
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new MSMLConversionException("Could not reconvert PDB from MSML", e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					LOGGER.info("Could not close outputstream.", e);
				}
			}
		}
		return pdbBuilder.toString();
	}

	/**
	 * creates pdb from pdb format
	 */
	private void parseFromPdbOrigin(MoleculeType moleculeElement, PDBBuilder pdbBuilder) throws IOException {
		// iterate over models
		List<MoleculeType> models = moleculeElement.getMolecule();
		for (int modelID = 0; modelID < models.size(); modelID++) {
			// create MODEL line
			PDBLineBuilder modelLine = new PDBLineBuilder();
			modelLine.insert("MODEL", 0, 5, ValueAlignment.Left);
			modelLine.insert((modelID + 1), 10, 14, ValueAlignment.Right);
			pdbBuilder.append(modelLine);

			MoleculeType model = (MoleculeType) models.get(modelID);
			// iterate over chains
			List<MoleculeType> chains = model.getMolecule();
			for (MoleculeType chain : chains) {
				// iterate over residues
				List<MoleculeType> residues = chain.getMolecule();
				List<MoleculeType> hetAtoms = new ArrayList<MoleculeType>();
				for (MoleculeType residue : residues) {
					if (residue.getMoleculeClass() == MoleculeClassType.AMINO_ACID) {
						AtomArrayType atoms = residue.getAtomArray();
						for (AtomType atom : atoms.getAtom()) {
							AtomLineBuilder lineBuilder = new AtomLineBuilder("ATOM", chain, residue, atom);
							String atomLine = lineBuilder.toString();
							pdbBuilder.append(atomLine);
						}
					} else if (residue.getMoleculeClass() == MoleculeClassType.NUCLEOTIDE) {
						AtomArrayType atoms = residue.getAtomArray();
						for (AtomType atom : atoms.getAtom()) {
							AtomLineBuilder lineBuilder = new AtomLineBuilder("ATOM", chain, residue, atom);
							String atomLine = lineBuilder.toString();
							pdbBuilder.append(atomLine);
						}
					} else if (residue.getMoleculeClass() == MoleculeClassType.HETERO) {
						hetAtoms.add(residue);
					}
				}
				// create TER line
				if (pdbBuilder.getLastLine() != null) {
					PDBLineBuilder terLine = new PDBLineBuilder(pdbBuilder.getLastLine());
					terLine.insert("TER", 0, 5, ValueAlignment.Left);
					terLine.clear(11, 16);
					terLine.clear(27, terLine.length());
					pdbBuilder.append(terLine);
				}

				for (MoleculeType hetatm : hetAtoms) {
					AtomArrayType atoms = hetatm.getAtomArray();
					for (AtomType atom : atoms.getAtom()) {
						AtomLineBuilder atomLine = new AtomLineBuilder("HETATM", chain, hetatm, atom);
						pdbBuilder.append(atomLine);
					}
				}
			}
			// end of model
			pdbBuilder.append("ENDMDL");
		}
	}

	/**
	 * Creates pdb from gro format
	 */
	private void parseFromGroOrigin(MoleculeType moleculeElement, PDBBuilder pdbBuilder) throws IOException {
		// iterate over residues
		List<MoleculeType> residues = moleculeElement.getMolecule();
		List<MoleculeType> hetAtoms = new ArrayList<MoleculeType>();
		for (MoleculeType residue : residues) {
			if (residue.getMoleculeClass() == MoleculeClassType.AMINO_ACID
					|| residue.getMoleculeClass() == MoleculeClassType.NUCLEOTIDE) {
				AtomArrayType atoms = residue.getAtomArray();
				for (AtomType atom : atoms.getAtom()) {
					AtomLineBuilder lineBuilder = new AtomLineBuilder("ATOM", null, residue, atom);
					String atomLine = lineBuilder.toString();
					pdbBuilder.append(atomLine);
				}

			} else if (residue.getMoleculeClass() == MoleculeClassType.HETERO) {
				hetAtoms.add(residue);
			}
		}
		// create TER line
		if (pdbBuilder.getLastLine() != null) {
			PDBLineBuilder terLine = new PDBLineBuilder(pdbBuilder.getLastLine());
			terLine.insert("TER", 0, 5, ValueAlignment.Left);
			terLine.clear(11, 16);
			terLine.clear(27, terLine.length());
			pdbBuilder.append(terLine);
		}

		for (MoleculeType hetatm : hetAtoms) {
			AtomArrayType atoms = hetatm.getAtomArray();
			for (AtomType atom : atoms.getAtom()) {
				AtomLineBuilder atomLine = new AtomLineBuilder("HETATM", null, hetatm, atom);
				pdbBuilder.append(atomLine);
			}
		}

	}

	/**
	 * Helper which builds the pdb content and writes each new line to an outputstream (if given)
	 * 
	 */
	private class PDBBuilder {
		private final String NEW_LINE = System.getProperty("line.separator");
		private String lastLine;
		private StringBuilder builder = new StringBuilder();
		private BufferedWriter writer;

		protected PDBBuilder(BufferedWriter writer) {
			this.writer = writer;
		}

		public void append(Object line) throws IOException {
			if (line != null) {
				append(line.toString());
			}
		}

		public void append(String line) throws IOException {
			lastLine = line;
			if (!line.endsWith(NEW_LINE)) {
				line += NEW_LINE;
			}
			builder.append(line);
			if (writer != null) {
				writer.write(line);
			}
		}

		/**
		 * @return The last line which was written
		 */
		public String getLastLine() {
			return lastLine;
		}

		@Override
		public String toString() {
			return builder.toString();
		}

	}

	/**
	 * PDB is a column separated format. Some values are left, other right aligned
	 */
	public enum ValueAlignment {
		Left, Right
	};

	/**
	 * Helper class for creating a pdb line which is 80 chars long. Provides a method for inserting values in desired
	 * column and with given alignment.
	 */
	private class PDBLineBuilder {
		// 80 spaces for FREE!
		private static final String FREE_SPACES = "                                                                                ";
		private StringBuilder builder;

		protected PDBLineBuilder() {
			super();
			builder = new StringBuilder();
		}

		protected PDBLineBuilder(String defContent) {
			super();
			builder = new StringBuilder(defContent);
		}

		/**
		 * @param value
		 *            This objects toString value will be inserted
		 * @param from
		 *            start index
		 * @param to
		 *            end index
		 * @param alignment
		 *            Align value at left or right side of column?
		 */
		public void insert(Object value, int from, int to, ValueAlignment alignment) {
			if (value != null) {
				String stringValue = value.toString().trim();
				if (stringValue.length() > 0) {
					int desiredLength = to - from + 1;
					if (stringValue.length() <= desiredLength) {
						int missingLength = desiredLength - stringValue.length();
						if (alignment == ValueAlignment.Left) {
							stringValue += FREE_SPACES.substring(0, missingLength);
						} else {
							stringValue = FREE_SPACES.substring(0, missingLength) + stringValue;
						}
						if (builder.length() < from) {
							builder.insert(builder.length(), FREE_SPACES.substring(0, from - builder.length()));
						}
						builder.replace(from, to + 1, stringValue);
					}
				}
			}
		}

		/**
		 * replace with empty string
		 */
		public void clear(int from, int to) {
			builder.replace(from, to, FREE_SPACES.substring(0, to - from));
		}

		public int length() {
			return builder.length();
		}

		@Override
		public String toString() {
			return builder.toString();
		}
	}

	private class AtomLineBuilder extends PDBLineBuilder {

		/**
		 * See http://www.wwpdb.org/documentation/format33/sect9.html#ATOM
		 * 
		 * @param prefix
		 *            'ATOM' or 'HETATM'
		 * @param chain
		 *            Parent chain
		 * @param residue
		 *            Parent residue
		 * @param atom
		 *            The Atom to create an entry for
		 */
		public AtomLineBuilder(String prefix, MoleculeType chain, MoleculeType residue, AtomType atom) {
			insert(prefix, 0, 5, ValueAlignment.Left);
			insert(atom.getCustomId(), 6, 10, ValueAlignment.Right);
			insert(atom.getTitle(), 12, 15, ValueAlignment.Left);
			insert(atom.getAltLoc(), 16, 16, ValueAlignment.Left);
			insert(residue.getTitle(), 17, 19, ValueAlignment.Left);
			if (chain != null) {
				insert(chain.getTitle(), 21, 21, ValueAlignment.Left);
			} else {
				insert("A", 21, 21, ValueAlignment.Left);
			}
			insert(residue.getCustomId(), 22, 25, ValueAlignment.Right);
			// TODO: no iCode needed by now
			insert(d3.format(atom.getX3()), 30, 37, ValueAlignment.Right);
			insert(d3.format(atom.getY3()), 38, 45, ValueAlignment.Right);
			insert(d3.format(atom.getZ3()), 46, 53, ValueAlignment.Right);
			// TODO: no occupancy needed by now
			// TODO: no temp factor needed by now
			insert(atom.getElementType(), 76, 77, ValueAlignment.Right);
			// TODO: no charge needed by now
		}
	}
}
