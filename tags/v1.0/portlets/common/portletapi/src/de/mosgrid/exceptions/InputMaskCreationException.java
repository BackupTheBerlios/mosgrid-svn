package de.mosgrid.exceptions;

/**
 * Exception for failed input mask creation
 * 
 * @author Andreas Zink
 * 
 */
public class InputMaskCreationException extends Exception {
	private static final long serialVersionUID = 7917041083970308310L;

	public InputMaskCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InputMaskCreationException(String message) {
		super(message);
	}
}
