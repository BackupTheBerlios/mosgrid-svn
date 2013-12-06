package de.mosgrid.exceptions;

/**
 * Exception if removing of an imported workflow fails
 * 
 * @author Andreas Zink
 * 
 */
public class RemovingFailedException extends Exception {
	private static final long serialVersionUID = -1752253824262893720L;

	public RemovingFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public RemovingFailedException(String message) {
		super(message);
	}
}
