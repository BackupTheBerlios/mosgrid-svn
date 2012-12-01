package de.mosgrid.chemdoodle.converter;

/**
 * Simple Exception if conversion to json fails
 * 
 * @author Andreas Zink
 * 
 */
public class JsonConversionException extends Exception {
	private static final long serialVersionUID = 745090810115999623L;

	public JsonConversionException(String message, Throwable cause) {
		super(message, cause);
	}

	public JsonConversionException(String message) {
		super(message);
	}

	public JsonConversionException(Throwable cause) {
		super(cause);
	}

}
