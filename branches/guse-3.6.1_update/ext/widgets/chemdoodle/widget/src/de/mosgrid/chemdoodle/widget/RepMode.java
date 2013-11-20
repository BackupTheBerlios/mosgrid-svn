package de.mosgrid.chemdoodle.widget;

/**
 * Enum for supported representation modes of molecules
 * 
 * @author Andreas Zink
 * 
 */
public enum RepMode {
	BALL_AND_STICK, STICK, WIREFRAME, VAN_DER_VAALS_SPHERES, LINE;

	@Override
	public String toString() {
		switch (this) {
		case STICK:
			return "Stick";
		case WIREFRAME:
			return "Wireframe";
		case VAN_DER_VAALS_SPHERES:
			return "van der Waals Spheres";
		case LINE:
			return "Line";
		default:
			return "Ball and Stick";
		}
	}
}
