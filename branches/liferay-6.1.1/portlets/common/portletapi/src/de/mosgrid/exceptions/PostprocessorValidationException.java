package de.mosgrid.exceptions;

/**
 * Exception which may get thrown if the validation of a Postprocessor fails
 * 
 * @author Andreas Zink
 * 
 */
public class PostprocessorValidationException extends Exception {
	private static final long serialVersionUID = 1750507019470878700L;

	public PostprocessorValidationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public PostprocessorValidationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

}
