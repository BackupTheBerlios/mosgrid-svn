package de.mosgrid.ukoeln.templatedesigner.helper;


public class StackTraceHelper {

	public enum ON_EXCEPTION {
		QUIT, CONTINUE
	}

	public static String getTrace(Exception e) {
		return getTrace(e.getStackTrace());
	}

	private static String getTrace(StackTraceElement[] stackTrace) {
		String res = "";
		for (StackTraceElement elem : stackTrace) {
			res += "\t" + elem.toString() + "\n";
		}
		return res;
	}

	public static String getTrace(Throwable throwable) {
		return getTrace(throwable.getStackTrace());
	}
}
