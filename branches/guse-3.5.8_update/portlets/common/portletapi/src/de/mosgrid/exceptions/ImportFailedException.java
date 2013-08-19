package de.mosgrid.exceptions;

/**
 * Exception for workflow importation fails
 * 
 */
public class ImportFailedException extends Exception {
	private static final long serialVersionUID = -911380265421857204L;

	public ImportFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ImportFailedException(String message) {
		super(message);
	}

}
