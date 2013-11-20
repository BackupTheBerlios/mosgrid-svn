package de.mosgrid.exceptions;

/**
 * Error for failed portlet initialization
 * 
 * @author Andreas Zink
 * 
 */
public class PortletInitializationException extends Exception {

	private static final long serialVersionUID = -7630465082273721550L;

	public PortletInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public PortletInitializationException(String message) {
		super(message);
	}

}
