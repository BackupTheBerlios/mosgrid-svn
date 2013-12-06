package de.mosgrid.chemdoodle.converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a chain of residues. A pdb file may contain several chains.
 * 
 * @author Andreas Zink
 * 
 */
public class JsonChain {

	private List<JsonResidue> residues = new ArrayList<JsonResidue>();
	private String name;
	private int modelID;

	public JsonChain(int modelID, String chainName) {
		this.name = chainName.trim();
		this.modelID = modelID;
	}

	public String getName() {
		return name;
	}

	public int getModelID() {
		return modelID;
	}

	public List<JsonResidue> getResidues() {
		return residues;
	}

	public void addResidue(JsonResidue residue) {
		residues.add(residue);
	}

	/**
	 * Iterates over residues and sets the arrow field for all next-to-last residues
	 */
	public void setArrowFields() {
		boolean isInSecStruc = false;
		for (int i = 0; i < residues.size(); i++) {
			JsonResidue residue = residues.get(i);
			if (residue.isInHelix() || residue.isInSheet()) {
				isInSecStruc = true;
			} else if (isInSecStruc) {
				JsonResidue previousResidue = residues.get(i - 2);
				previousResidue.setAtArrow(true);
				isInSecStruc = false;
			}
		}
	}

	@Override
	public int hashCode() {
		return modelID + name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof JsonChain) {
			JsonChain other = (JsonChain) obj;
			if (modelID == other.getModelID() && name.equals(other.getName())) {
				return true;
			}

		}
		return false;
	}

	@Override
	public String toString() {
		final String COMMA = ",";
		StringBuilder stringBuilder = new StringBuilder(250);
		stringBuilder.append("[");
		for (JsonResidue r : residues) {
			stringBuilder.append(r.toString() + COMMA);
		}
		// remove last comma
		if (residues.size() > 0) {
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

}
