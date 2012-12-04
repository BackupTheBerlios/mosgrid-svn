package de.mosgrid.chemdoodle.converter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Atom array in json notation
 * 
 * @author Andreas Zink
 * 
 */
public class JsonAtomArray {
	private List<JsonAtom> atoms = new ArrayList<JsonAtom>();
	private Map<Integer, JsonAtom> atomSerial2Atom = new HashMap<Integer, JsonAtom>();

	public Collection<JsonAtom> getAtoms() {
		return atoms;
	}

	public void addAtom(JsonAtom a) {
		this.atoms.add(a);
		this.atomSerial2Atom.put(a.getSerialNumber(), a);
	}

	public void addAtoms(Collection<JsonAtom> atoms) {
		for (JsonAtom a : atoms) {
			addAtom(a);
		}
	}

	public boolean contains(int atomSerial) {
		return atomSerial2Atom.containsKey(atomSerial);
	}

	public JsonAtom getAtom(int atomSerial) {
		return atomSerial2Atom.get(atomSerial);
	}

	public int indexOf(JsonAtom a) {
		return atoms.indexOf(a);
	}

	public boolean isEmpty() {
		return atoms.isEmpty() || atomSerial2Atom.isEmpty();
	}

	@Override
	public String toString() {
		final String COMMA = ",";
		StringBuilder stringBuilder = new StringBuilder(80);
		stringBuilder.append("'a':[");
		for (JsonAtom a : atoms) {
			stringBuilder.append(a + COMMA);
		}
		// remove last comma
		if (atoms.size() > 0) {
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}
}
