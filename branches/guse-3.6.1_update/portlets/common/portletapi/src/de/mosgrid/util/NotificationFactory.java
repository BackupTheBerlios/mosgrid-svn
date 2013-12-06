package de.mosgrid.util;

import com.vaadin.ui.Window.Notification;

/**
 * This is just a small helper class which shall provide common Notification Objects. This shall provide a common look
 * for notification layout and display time.
 * 
 * @author Andreas Zink
 * 
 */
public class NotificationFactory {
	/* display time contants (-1= user must click) */
	public static final int DISPLAY_TIME_SHORT = 1000;
	public static final int DISPLAY_TIME_NORMAL = 3000;
	public static final int DISPLAY_TIME_LONG = 5000;
	public static final int DISPLAY_TIME_INF = -1;

	/* text constants */
	public static final String BREAK_LINE = "<br>";
	public static final String MSG_SUPPORT = "Please contact the support.";
	// validation
	public static final String CAPTION_VALIDATION_FAILED = "Validation failed!";
	public static final String MSG_VALIDATIOIN_FAILED = "Some of your input seems to be incorrect.";

	public static final String CAPTION_SUCCEEDED = "Succeeded!";
	public static final String CAPTION_FAILED = "Failed!";

	private NotificationFactory() {
		// no instance needed, make singleton if initialization needed
	}

	/**
	 * Creates a Notification instance which can be displayed by a window
	 * 
	 * @param caption
	 *            The caption
	 * @param msg
	 *            The description or message content (separated with 'break-line' automatically)
	 * @param displayTime
	 *            Time the notification is shown in msec
	 * @param notifType
	 *            Notification type (use constants of Notification class)
	 * @return
	 */
	public static synchronized Notification create(String caption, String msg, int displayTime, int notifType) {
		Notification notif = new Notification(caption, BREAK_LINE + msg, notifType);
		notif.setHtmlContentAllowed(true);
		notif.setDelayMsec(displayTime);

		return notif;
	}

	/**
	 * Creates an error notification the user has to click on to disappear. Additionally appends the given msg by
	 * "Please contact the support"
	 */
	public static synchronized Notification createErrorNotification(String caption, String msg) {
		return create(caption, msg + BREAK_LINE + MSG_SUPPORT, DISPLAY_TIME_INF, Notification.TYPE_ERROR_MESSAGE);
	}

	/**
	 * Creates a warning notification which is shown NORMAL_TIME long
	 */
	public static synchronized Notification createWarningNotification(String caption, String msg) {
		return create(caption, msg, DISPLAY_TIME_NORMAL, Notification.TYPE_WARNING_MESSAGE);
	}

	/**
	 * Creates a normal notification which is shown NORMAL_TIME long
	 */
	public static synchronized Notification createNormalNotification(String caption, String msg) {
		return create(caption, msg, DISPLAY_TIME_NORMAL, Notification.TYPE_HUMANIZED_MESSAGE);
	}

	/**
	 * Creates a tray notification which is shown NORMAL_TIME long in the corner of the screen
	 */
	public static synchronized Notification createTrayNotification(String caption, String msg) {
		return create(caption, msg, DISPLAY_TIME_NORMAL, Notification.TYPE_TRAY_NOTIFICATION);
	}

	/**
	 * Creates a notification for failed validation of user input
	 */
	public static synchronized Notification createValidationFailedNotification() {
		return createWarningNotification(CAPTION_VALIDATION_FAILED, MSG_VALIDATIOIN_FAILED);
	}

	/**
	 * Creates an error notification with given caption and optional message
	 */
	public static synchronized Notification createFailedNotification(String msg) {
		return createErrorNotification(CAPTION_FAILED, msg);
	}

	/**
	 * Creates normal notification with given caption and optional message
	 */
	public static synchronized Notification createSucceededNotification(String msg) {
		return createNormalNotification(CAPTION_SUCCEEDED, msg);
	}

}
