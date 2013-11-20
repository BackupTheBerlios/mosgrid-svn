package de.mosgrid.chemdoodle.converter;

import org.biojava.bio.structure.Atom;

/**
 * Represents an atom which may be ATOM or HETATM
 * 
 * @author Andreas Zink
 * 
 */
public class JsonAtom {
	private boolean isHetatm;
	private boolean isWater;
	private String elementSymbol;
	private String atomName;
	private float x, y, z;
	private int serialNumber;

	public JsonAtom() {

	}

	public JsonAtom(Atom atom) {
		setX((float) atom.getX());
		setY((float) atom.getY());
		setZ((float) atom.getZ());
		setAtomName(atom.getFullName().trim());
		setElementSymbol(atom.getElement().toString().trim());
		setSerialNumber(atom.getPDBserial());
	}

	public int getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}

	public boolean isHetatm() {
		return isHetatm;
	}

	public void setHetatm(boolean hetatm) {
		this.isHetatm = hetatm;
	}

	public boolean isWater() {
		return isWater;
	}

	public void setWater(boolean water) {
		this.isWater = water;
	}

	public String getElementSymbol() {
		return elementSymbol;
	}

	public void setElementSymbol(String symbol) {
		if (symbol != null && !symbol.equals("") && !symbol.equals("R")) {
			this.elementSymbol = symbol;
		} else {
			if (atomName != null) {
				predictElementSymbolFromAtomName();
			}
		}
	}

	public void predictElementSymbolFromAtomName() {
		if (atomName.contains("NA")) {
			setElementSymbol("Na");
		} else if (atomName.contains("CL")) {
			setElementSymbol("Cl");
		} else {
			setElementSymbol(String.valueOf(atomName.charAt(0)));
		}
	}

	public String getAtomName() {
		return atomName;
	}

	public void setAtomName(String atomName) {
		this.atomName = atomName;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	@Override
	public int hashCode() {
		return this.serialNumber;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof JsonAtom) {
			if (hashCode() == obj.hashCode()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(75);
		builder.append("{'p_h':");
		builder.append(isHetatm);
		if (isWater) {
			builder.append(",'p_w':true");
		}
		if (elementSymbol != null) {
			builder.append(",'l':'");
			builder.append(elementSymbol);
			builder.append("'");
		}
		builder.append(",'z':");
		builder.append(z);
		builder.append(",'y':");
		builder.append(y);
		builder.append(",'x':");
		builder.append(x);
		builder.append("}");

		return builder.toString();
	}
}
