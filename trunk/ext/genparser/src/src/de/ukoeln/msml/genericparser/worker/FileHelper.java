package de.ukoeln.msml.genericparser.worker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.ukoeln.msml.genericparser.worker.StackTraceHelper.ON_EXCEPTION;

public class FileHelper {
	
	private FileHelper() {}
	
	public static String getLine(String pattern, InputStream stream) {
		InputStreamReader isr = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		Pattern p = Pattern.compile(pattern);
		try {
			while (true) {
				line = br.readLine();
				if (p.matcher(line).matches())
					return line;
				if (line == null)
					break;
			}
		} catch (IOException e) {
			StackTraceHelper.handleException(e, ON_EXCEPTION.QUIT);
		}
		return null;		
	}
	
	public static String[] getLines(InputStream stream) {
		InputStreamReader isr = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(isr);
		List<String> lines = new ArrayList<String>();
		String line = null;
		try {
			while (true) {
					line = br.readLine();
				if (line != null)
					lines.add(line);
				else
					break;
			}
		} catch (IOException e) {
			StackTraceHelper.handleException(e, ON_EXCEPTION.QUIT);
		}
		String[] result = new String[lines.size()];
		result = lines.toArray(result);
		return result;
	}
}
