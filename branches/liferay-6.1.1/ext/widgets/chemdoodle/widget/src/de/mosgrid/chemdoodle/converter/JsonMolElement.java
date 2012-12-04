package de.mosgrid.chemdoodle.converter;

/**
 * 'mol' element in json notation. This element consists of a bond and atom array.
 * 
 * @author Andreas Zink
 * 
 */
public class JsonMolElement {

	private JsonBondArray bonds = new JsonBondArray();
	private JsonAtomArray atoms = new JsonAtomArray();

	public JsonBondArray getBonds() {
		return bonds;
	}

	public void setBonds(JsonBondArray bonds) {
		this.bonds = bonds;
	}

	public void appendBonds(JsonBondArray bonds) {
		this.bonds.addBonds(bonds.getBonds());
	}

	public JsonAtomArray getAtoms() {
		return atoms;
	}

	public void setAtoms(JsonAtomArray atoms) {
		this.atoms = atoms;
	}
	
	public void appendAtoms(JsonAtomArray atoms){
		this.atoms.addAtoms(atoms.getAtoms());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(250);
		// StringBuilder builder = new StringBuilder();
		builder.append("'mol':{");
		builder.append(bonds);
		builder.append(",");
		builder.append(atoms);
		builder.append("}");
		return builder.toString();
	}

}
