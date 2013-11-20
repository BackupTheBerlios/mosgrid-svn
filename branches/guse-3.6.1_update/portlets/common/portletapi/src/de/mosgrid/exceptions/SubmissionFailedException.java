package de.mosgrid.exceptions;

/**
 * Exception for workflow submission fails
 * 
 * @author Andreas Zink
 * 
 */
public class SubmissionFailedException extends Exception {
	private static final long serialVersionUID = 7532383148105222753L;

	public SubmissionFailedException(String message, Throwable e) {
		super(message, e);
	}

	public SubmissionFailedException(String message) {
		super(message);
	}
}
