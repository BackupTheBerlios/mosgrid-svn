package de.mosgrid.exceptions;

/**
 * Exception which can get thrown if the creation of a Postprocessor fails. Postprocessors may read/parse an uploaded
 * file in the constructor which may lead to erros.
 * 
 * @author Andreas Zink
 * 
 */
public class PostprocessorCreationException extends Exception {
	private static final long serialVersionUID = 5924977239762133684L;

	public PostprocessorCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public PostprocessorCreationException(String message) {
		super(message);
	}

}
