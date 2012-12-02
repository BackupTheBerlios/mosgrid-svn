package de.mosgrid.ukoeln.templatedesigner.helper;

public class StringH {

	public static boolean isNullOrEmpty(String str) {
		return str == null || "".equals(str);
	}

	public static boolean isInteger(String val) {
		try {
			Integer.parseInt(val);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean equals(String one, String another) {
		if (one == null && another == null)
			return true;
		if (one != null)
			return one.equals(another);
		return false;
	}

}
