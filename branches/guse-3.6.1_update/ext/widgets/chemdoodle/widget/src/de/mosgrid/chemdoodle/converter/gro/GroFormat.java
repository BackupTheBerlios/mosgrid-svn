package de.mosgrid.chemdoodle.converter.gro;

import java.util.HashMap;
import java.util.Map;

import de.mosgrid.chemdoodle.converter.gro.GroResidue.ResidueType;

public class GroFormat {

	private Map<Integer, GroResidue> aminos = new HashMap<Integer, GroResidue>();
	private Map<Integer, GroResidue> ions = new HashMap<Integer, GroResidue>();
	private Map<Integer, GroResidue> solvent = new HashMap<Integer, GroResidue>();

	public void addResidue(GroResidue residue) {
		if (residue.getType() == ResidueType.AA) {
			aminos.put(residue.getResidueNumber(), residue);
		} else if (residue.getType() == ResidueType.ION) {
			ions.put(residue.getResidueNumber(), residue);
		} else if (residue.getType() == ResidueType.SOL) {
			solvent.put(residue.getResidueNumber(), residue);
		}
	}

	public Map<Integer, GroResidue> getAminos() {
		return aminos;
	}

	public Map<Integer, GroResidue> getIons() {
		return ions;
	}

	public Map<Integer, GroResidue> getSolvent() {
		return solvent;
	}

	@Override
	public String toString() {
		return "GroFormat [aminos=" + aminos + ", ions=" + ions + ", solvent=" + solvent + "]";
	}
	
	

}
