package de.mosgrid.chemdoodle.converter;

import java.util.ArrayList;
import java.util.List;

import org.biojava.bio.structure.AminoAcid;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.NucleotideImpl;
import org.biojava.bio.structure.ResidueNumber;

/**
 * Represents a residue. Residues can build chains.
 * 
 * @author Andreas Zink
 * 
 */
public class JsonResidue {
	private boolean isInSheet;
	private boolean isInHelix;
	private boolean isAtArrow;
	private List<JsonAtom> atoms = new ArrayList<JsonAtom>();
	private String name;
	private int sequenceNumber;
	private String chainID;

	public JsonResidue(Group group) {
		init(group);
	}

	public JsonResidue(AminoAcid amino) {
		if (amino.getSecStruc().containsValue("HELIX")) {
			this.isInHelix = true;
		} else if (amino.getSecStruc().containsValue("STRAND")) {
			this.isInSheet = true;
		}
		String pdbName = amino.getPDBName().trim();
		this.name =  pdbName.substring(0, 1).toUpperCase() + pdbName.substring(1).toLowerCase();
		init(amino);
	}

	public JsonResidue(NucleotideImpl nucleotide) {
		String pdbName = nucleotide.getPDBName().trim();
		this.name =  pdbName.substring(1).toUpperCase();
		init(nucleotide);
	}

	private void init(Group group) {
		ResidueNumber rn = group.getResidueNumber();
		this.sequenceNumber = rn.getSeqNum();
		this.chainID = rn.getChainId().trim();
	}

	public boolean isInSheet() {
		return isInSheet;
	}

	public void setInSheet(boolean isInSheet) {
		this.isInSheet = isInSheet;
	}

	public boolean isInHelix() {
		return isInHelix;
	}

	public void setInHelix(boolean isInHelix) {
		this.isInHelix = isInHelix;
	}

	public boolean isAtArrow() {
		return isAtArrow;
	}

	public void setAtArrow(boolean isAtArrow) {
		this.isAtArrow = isAtArrow;
	}

	public List<JsonAtom> getAtoms() {
		return atoms;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return sequenceNumber + chainID.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof JsonResidue) {
			if (hashCode() == obj.hashCode()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder jsonBuilder = new StringBuilder("{'s':" + isInSheet + ",'a':" + isAtArrow + ",'h':" + isInHelix);
		jsonBuilder.append(name != null ? ",'n':'" + name +"'": "");
		for (int i = 0; i < atoms.size();) {
			JsonAtom a = atoms.get(i++);
			jsonBuilder.append(",'x" + i + "':" + a.getX() + ",'y" + i + "':" + a.getY() + ",'z" + i + "':" + a.getZ());
		}
		jsonBuilder.append("}");
		return jsonBuilder.toString();
	}

}
