package de.mosgrid.msml.converter.gro;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
 * A msml to gro converter
 * 
 * @author Andreas Zink
 * 
 */
public class MSML2GROConverter implements IFromMSMLConverter {
	private final Logger LOGGER = LoggerFactory.getLogger(MSML2GROConverter.class);
	// desired gro coordinate format
	private static final DecimalFormat d3 = (DecimalFormat) NumberFormat.getInstance(java.util.Locale.UK);
	// desired gro velocity format
	private static final DecimalFormat d4 = (DecimalFormat) NumberFormat.getInstance(java.util.Locale.UK);
	static {
		d3.setMaximumIntegerDigits(3);
		d3.setMinimumFractionDigits(3);
		d3.setMaximumFractionDigits(3);

		d4.setMaximumIntegerDigits(4);
		d4.setMinimumFractionDigits(4);
		d4.setMaximumFractionDigits(4);
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
		LOGGER.trace("Trying to convert MSML molecule element to GRO format");

		GROBuilder groBuilder = new GROBuilder(writer);
		try {
			// set gro title if given
			String title = moleculeElement.getTitle();
			groBuilder.append(title);
			
			if (moleculeElement.getOrigin() == OriginType.GRO) {
				parseFromGroOrigin(moleculeElement, groBuilder);
			} else {
				throw new MSMLConversionException("The given molecule origin " + moleculeElement.getOrigin()
						+ " is not supported");
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new MSMLConversionException("Could not reconvert GRO from MSML", e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					LOGGER.info("Could not close outputstream.", e);
				}
			}
		}
		return groBuilder.toString();
	}

	/**
	 * creates gro from gro format
	 */
	private void parseFromGroOrigin(MoleculeType moleculeElement, GROBuilder groBuilder) throws Exception {

		groBuilder.append(" " + moleculeElement.getCount().intValue());
		// parse residues
		List<MoleculeType> residues = moleculeElement.getMolecule();
		for (MoleculeType residue : residues) {
			MoleculeClassType type = residue.getMoleculeClass();
			if (type == MoleculeClassType.AMINO_ACID || type == MoleculeClassType.HETERO
					|| type == MoleculeClassType.NUCLEOTIDE) {
				AtomArrayType atoms = residue.getAtomArray();
				for (AtomType atom : atoms.getAtom()) {
					GROLineBuilder lineBuilder = new GROLineBuilder(residue, atom);
					groBuilder.append(lineBuilder);
				}
			}
		}
	}

	/**
	 * Helper which builds the gro content and writes each new line to an outputstream (if given)
	 * 
	 */
	private class GROBuilder {
		private final String NEW_LINE = System.getProperty("line.separator");
		private StringBuilder builder = new StringBuilder();
		private BufferedWriter writer;

		protected GROBuilder(BufferedWriter writer) {
			this.writer = writer;
		}

		public void append(Object line) throws IOException {
			if (line != null) {
				append(line.toString());
			}
		}

		public void append(String line) throws IOException {
			if (!line.endsWith(NEW_LINE)) {
				line += NEW_LINE;
			}
			builder.append(line);
			if (writer != null) {
				writer.write(line);
			}
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
	private class GROLineBuilder {
		// 80 spaces for FREE!
		private static final String FREE_SPACES = "                                                                                ";
		private StringBuilder builder;

		protected GROLineBuilder() {
			super();
			builder = new StringBuilder();
		}

		protected GROLineBuilder(MoleculeType residue, AtomType atom) {
			this();
			insert(residue.getCustomId(), 0, 4, ValueAlignment.Right);
			insert(residue.getTitle(), 5, 9, ValueAlignment.Left);
			insert(atom.getTitle(), 10, 14, ValueAlignment.Right);
			insert(atom.getCustomId(), 15, 19, ValueAlignment.Right);

			// insert coordinates
			insert(d3.format(atom.getX3()), 20, 27, ValueAlignment.Right);
			insert(d3.format(atom.getY3()), 28, 35, ValueAlignment.Right);
			insert(d3.format(atom.getZ3()), 36, 43, ValueAlignment.Right);
			// insert velocities
			insert(d4.format(atom.getVX()), 44, 51, ValueAlignment.Right);
			insert(d4.format(atom.getVY()), 52, 59, ValueAlignment.Right);
			insert(d4.format(atom.getVZ()), 60, 67, ValueAlignment.Right);
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

		// /**
		// * replace with empty string
		// */
		// public void clear(int from, int to) {
		// builder.replace(from, to, FREE_SPACES.substring(0, to - from));
		// }
		//
		// public int length() {
		// return builder.length();
		// }

		@Override
		public String toString() {
			return builder.toString();
		}
	}
}
