package de.mosgrid.msml.converter.exceptions;

/**
 * Exception if conversion to or from MSML failed
 * 
 * @author Andreas Zink
 * 
 */
public class MSMLConversionException extends Exception {
	private static final long serialVersionUID = 5957167783777367390L;

	public MSMLConversionException(String message, Throwable cause) {
		super(message, cause);
	}

	public MSMLConversionException(String message) {
		super(message);
	}

}
