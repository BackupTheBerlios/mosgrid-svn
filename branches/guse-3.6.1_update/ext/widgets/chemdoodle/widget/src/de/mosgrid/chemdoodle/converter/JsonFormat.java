package de.mosgrid.chemdoodle.converter;

/**
 * Represents molecules in json format.
 * 
 * @author Andreas Zink
 * 
 */
public class JsonFormat {
	private JsonRibbonsElement ribbons = new JsonRibbonsElement();
	private JsonMolElement molecules = new JsonMolElement();

	public JsonRibbonsElement getRibbons() {
		return ribbons;
	}

	public void setRibbons(JsonRibbonsElement ribbons) {
		this.ribbons = ribbons;
	}

	public JsonMolElement getMolecues() {
		return molecules;
	}

	public void setMolecues(JsonMolElement molecues) {
		this.molecules = molecues;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(15000);
		// StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append(ribbons);
		builder.append(",");
		builder.append(molecules);
		builder.append("}");
		return builder.toString();
	}

}
