package de.mosgrid.exceptions;

/**
 * Exception that may be thrown if the creation for an input field for input mask fails
 * 
 * @author Andreas Zink
 * 
 */
public class InputFieldCreationException extends Exception {
	private static final long serialVersionUID = -4189687531769086673L;

	public InputFieldCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InputFieldCreationException(String message) {
		super(message);
	}

}
