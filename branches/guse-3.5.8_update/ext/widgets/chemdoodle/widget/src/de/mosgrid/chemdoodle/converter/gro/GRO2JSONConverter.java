package de.mosgrid.chemdoodle.converter.gro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import de.mosgrid.chemdoodle.converter.AbstractJsonConverter;
import de.mosgrid.chemdoodle.converter.IConverterParameters;
import de.mosgrid.chemdoodle.converter.JsonConversionException;
import de.mosgrid.chemdoodle.converter.JsonFormat;

public class GRO2JSONConverter extends AbstractJsonConverter {

	private GroConversionParameters parameters = new GroConversionParameters();
	
	@Override
	public String convert(BufferedReader reader, BufferedWriter writer) throws JsonConversionException, IOException {
		String jsonString = null;
		JsonFormat jsonFormat = new JsonFormat();

		String line = null;
		while ((line = reader.readLine()) != null) {
			
		}

		return jsonString;
	}
	
	@Override
	public IConverterParameters getConverterParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public class GroConversionParameters implements IConverterParameters{
		// parse water molecules
		private boolean parseWater = false;
		// parse non polymers (hetatm + water)
		private boolean parseNonPolymers = true;
		// parse all atoms of amino acids
		private boolean parseAminoAcidAtoms = true;

		protected GroConversionParameters() {
			super();
		}

		public boolean isParseWater() {
			return parseWater;
		}

		/**
		 * Decides if water molecules shall be parsed
		 */
		public void setParseWater(boolean parseWater) {
			this.parseWater = parseWater;
		}

		public boolean isParseNonPolymers() {
			return parseNonPolymers;
		}

		/**
		 * Decides if non polymers shall be parsed
		 */
		public void setParseNonPolymers(boolean parseNonPolymers) {
			this.parseNonPolymers = parseNonPolymers;
		}

		public boolean isParseAminoAcidAtoms() {
			return parseAminoAcidAtoms;
		}

		/**
		 * Decides if atoms of amino atoms shall be parsed
		 */
		public void setParseAminoAcidAtoms(boolean parseAminoAcidAtoms) {
			this.parseAminoAcidAtoms = parseAminoAcidAtoms;
		}

		@Override
		public void setPredictSecStructure(boolean predictSecStructure) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setModel(int model) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getModel() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isPredictSecStructure() {
			// TODO Auto-generated method stub
			return false;
		}

	}



}
