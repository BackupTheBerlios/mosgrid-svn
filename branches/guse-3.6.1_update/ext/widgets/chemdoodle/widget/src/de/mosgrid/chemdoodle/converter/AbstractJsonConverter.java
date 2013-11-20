package de.mosgrid.chemdoodle.converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Abstract base class for json converters
 * 
 * @author Andreas Zink
 * 
 */
public abstract class AbstractJsonConverter implements IJsonConverter {

	@Override
	public String convert(InputStream is, OutputStream os) throws JsonConversionException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		BufferedWriter writer = null;
		if (os != null) {
			writer = new BufferedWriter(new OutputStreamWriter(os));
		}
		return convert(reader, writer);
	}

	@Override
	public String convert(InputStreamReader reader, OutputStreamWriter writer) throws JsonConversionException,
			IOException {
		BufferedReader bufReader = new BufferedReader(reader);
		BufferedWriter bufWriter = null;
		if (writer != null) {
			bufWriter = new BufferedWriter(writer);
		}
		return convert(bufReader, bufWriter);
	}

}
