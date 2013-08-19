package de.mosgrid.exceptions;

/**
 * Exception which may get thrown if the creation of a posprocessed file fails
 * 
 * @author Andreas Zink
 * 
 */
public class PostprocessorException extends Exception {
	private static final long serialVersionUID = -4202147634159557583L;

	public PostprocessorException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public PostprocessorException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

}
