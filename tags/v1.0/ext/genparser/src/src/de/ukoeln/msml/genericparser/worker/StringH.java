package de.ukoeln.msml.genericparser.worker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;

public class StringH {

	public static boolean isNullOrEmpty(String str) {
		return str == null || "".equals(str);
	}
	
	public static String withBr(String text, WithBufferedReaderDelegate del) {
		StringReader r = new StringReader(text);
		BufferedReader br = new BufferedReader(r);

		String result = "";
		try {
			result = del.Do(br);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static boolean isInteger(String val) {
		try {
			Integer.parseInt(val);
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	public static BigInteger toBigInteger(String text) {
		BigInteger val = null;
		try {
			val = new BigInteger(text);
		} catch(Exception e) {
			// do nothing;
		}
		return val;
	}
}
