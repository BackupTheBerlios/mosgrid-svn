package de.mosgrid.util.properties;

import java.math.BigDecimal;


/**
 * Somewhat crappy class to hold Strings, Doubles and Integers and compare them against
 * eachother.
 * This class is used as Min- and Maximum values of the scalars where the value is
 * most likely a double but it should be able to be printed and handled as integer.
 * 
 * @author krm
 *
 */
public class BoxedValue implements Comparable<Object>{

	private Class<?> _type;
	private Object _val;

	public BoxedValue(Class<?> type, Object val) {
		_type = type;
		if (val instanceof Number) {
			_val = new BigDecimal(val.toString());
		} else {
			_val = val.toString();
		}
	}

	public String getFormattedValue() {
		if (_val instanceof BigDecimal) {
			if (Integer.class.equals(_type)) {
				Integer val = Integer.parseInt(((BigDecimal) _val).toBigInteger().toString());
				return val.toString();
			} else if (Double.class.equals(_type)) {
				Double val = Double.parseDouble(((BigDecimal) _val).toString());
				return val.toString();
			}
		}
		return _val.toString();
	}

	public Object getValue() {
		return _val;
	}

	@Override
	public int compareTo(Object o) {
		// Other is most likely the string representation of an integer or
		// double.
		
		// -1 this < o
		// 0 this == o
		// 1 this > o

		Object other = o;
		if (o instanceof BoxedValue)
			other = ((BoxedValue) o)._val;

		if (_val == null && other == null)
			return 0;
		if (_val == null && other != null)
			return -1;
		if (_val != null && other == null)
			return 1;
		
		if (String.class.equals(_type) && _val instanceof Number) {
			// if the other value is a number and this not,
			// then declare this as greater. May violate rules for total order?
			return 1;
		}
		
		if (_val instanceof Number) {
			try {
				other = new BigDecimal(other.toString());				
			} catch (NumberFormatException e) {
				// if this value is a number and the other not,
				// then declare this as lesser. May violate rules for total order?
				return -1;
			}
		}
		
		// From here on we know that we either have to numbers or two strings to compare.
		if (String.class.equals(_type)) {
			return ((String) _val).compareTo((String) o);
		} else if (_val instanceof Number)
			return new BigDecimal(_val.toString()).compareTo(new BigDecimal(other.toString()));
		return 0;
	}
	
	public boolean lesserEqualAs(Object o) {
		return compareTo(o) <= 0;
	}
	
	public boolean lesserAs(Object o) {
		return compareTo(o) < 0;
	}
	
	public boolean greaterEqualAs(Object o) {
		return compareTo(o) >= 0;
	}

	public boolean greaterAs(Object o) {
		return compareTo(o) > 0;
	}

	@Override
	public String toString() {
		return _val != null ? _val.toString() : "<null>";
	}
}
