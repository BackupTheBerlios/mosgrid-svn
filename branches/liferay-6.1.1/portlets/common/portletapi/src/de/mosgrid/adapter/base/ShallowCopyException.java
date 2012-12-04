package de.mosgrid.adapter.base;

public class ShallowCopyException extends Exception {
	private static final long serialVersionUID = 8228220771908002766L;

	public ShallowCopyException(String message, Throwable e) {
		super(message, e);
	}

}
