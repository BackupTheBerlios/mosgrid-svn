package de.mosgrid.msml.util.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton. TODO: please comment
 */
public class FileHelper {
	private final Logger LOGGER = LoggerFactory.getLogger(FileHelper.class);
	private static FileHelper instance = null;

	private FileHelper() {
	}

	public static synchronized FileHelper getInstance() {
		if (instance == null) {
			instance = new FileHelper();
		}
		return instance;
	}

	public synchronized String getLine(String pattern, InputStream stream) {
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
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	public synchronized String[] getLines(String pattern, InputStream stream) {
		Pattern pat = Pattern.compile(pattern);
		ArrayList<String> matchingLines = new ArrayList<String>();
		String[] lines = getLines(stream);
		for (String line : lines) {
			if (pat.matcher(line).matches())
				matchingLines.add(line);
		}
		String[] res = new String[matchingLines.size()];
		res = matchingLines.toArray(res);
		return res;
	}

	public synchronized String[] getLines(InputStream stream) {
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
			LOGGER.error(e.getMessage(), e);
		}
		String[] result = new String[lines.size()];
		result = lines.toArray(result);
		return result;
	}

	public synchronized String getConcatinatedLines(InputStream fis) {
		StringBuilder sb = new StringBuilder();
		String[] lines = getLines(fis);
		for (int i = 0; i < lines.length; i++) {
			sb.append(lines[i]);
		}
		return sb.toString();
	}
}
