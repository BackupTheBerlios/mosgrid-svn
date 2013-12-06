package de.mosgrid.chemdoodle.converter;

/**
 * A bond in json format
 * 
 * @author Andreas Zink
 * 
 */
public class JsonBond {

	private int from, to;
	private boolean doubleBond = false;

	public JsonBond(int from, int to) {
		super();
		this.from = from;
		this.to = to;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	public boolean isDoubleBond() {
		return doubleBond;
	}

	public void setDoubleBond(boolean doubleBond) {
		this.doubleBond = doubleBond;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(30);
		builder.append("{'e':");
		builder.append(from);
		builder.append(",'b':");
		builder.append(to);
		if (doubleBond) {
			builder.append(",'o':2");
		}
		builder.append("}");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return from * to;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof JsonBond) {
			JsonBond other = (JsonBond) obj;
			if ((from == other.getFrom()) && (to == other.getTo())) {
				return true;
			}
			if ((from == other.getTo()) && (to == other.getFrom())) {
				return true;
			}
		}
		return false;
	}

}
