package de.mosgrid.msml.util;

public class MSMLException extends RuntimeException {

	private static final long serialVersionUID = -7067559798950368964L;

	public MSMLException(String message, Throwable cause) {
		super(message, cause);
	}

	public MSMLException(String message) {
		super(message);
	}
}
