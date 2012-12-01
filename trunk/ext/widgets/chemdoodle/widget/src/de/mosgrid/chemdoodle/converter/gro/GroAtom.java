package de.mosgrid.chemdoodle.converter.gro;

public class GroAtom {
	private boolean isValid = true;
	private int residueNumber;
	private int atomNumber;
	private String atomName;
	private float x, y, z;
	
	public GroAtom(String line){
		setAtomName(line);
		setAtomNumber(line);
		setX(line);
		setY(line);
		setZ(line);
	}
	
	private void setResidueNumber(String line) {
		try {
			String residueNumberString = line.substring(0, 5).trim();
			residueNumber = new Integer(residueNumberString);
		} catch (Exception e) {
			
		}
	}
	



	public int getResidueNumber() {
		return residueNumber;
	}

	public String getAtomName() {
		return atomName;
	}

	private void setAtomName(String line) {
		atomName = line.substring(10, 15).trim();
		if (atomName.length() == 0) {
			isValid = false;
		}

	}

	public int getAtomNumber() {
		return atomNumber;
	}

	private void setAtomNumber(String line) {
		try {
			String atomNumberString = line.substring(15, 20).trim();
			atomNumber = new Integer(atomNumberString);
		} catch (Exception e) {
			isValid = false;
		}
	}

	public float getX() {
		return x;
	}

	private void setX(String line) {
		try {
			String xString = line.substring(20, 28).trim();
			x = new Integer(xString);
		} catch (Exception e) {
			isValid = false;
		}
	}

	public float getY() {
		return y;
	}

	private void setY(String line) {
		try {
			String yString = line.substring(28, 36).trim();
			y = new Integer(yString);
		} catch (Exception e) {
			isValid = false;
		}
	}

	public float getZ() {
		return z;
	}

	private void setZ(String line) {
		try {
			String zString = line.substring(36, 44).trim();
			z = new Integer(zString);
		} catch (Exception e) {
			isValid = false;
		}
	}
}
