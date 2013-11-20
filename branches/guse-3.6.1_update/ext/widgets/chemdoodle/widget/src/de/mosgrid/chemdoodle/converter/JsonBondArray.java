package de.mosgrid.chemdoodle.converter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Bond array in json notation
 * 
 * @author Andreas Zink
 * 
 */
public class JsonBondArray {
	private Set<JsonBond> bonds = new HashSet<JsonBond>(150);

	public Set<JsonBond> getBonds() {
		return bonds;
	}

	public boolean addBond(JsonBond b) {
		return this.bonds.add(b);
	}

	public void addBonds(Collection<JsonBond> bonds) {
		this.bonds.addAll(bonds);
	}

	public boolean isEmpty() {
		return bonds.isEmpty();
	}

	@Override
	public String toString() {
		final String COMMA = ",";
		 StringBuilder stringBuilder = new StringBuilder(35);
		stringBuilder.append("'b':[");
		for (JsonBond b : bonds) {
			stringBuilder.append(b + COMMA);
		}
		// remove last comma
		if (bonds.size() > 0) {
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

}
