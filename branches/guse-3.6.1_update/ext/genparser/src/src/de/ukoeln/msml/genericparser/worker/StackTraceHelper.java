package de.ukoeln.msml.genericparser.worker;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ukoeln.msml.genericparser.GenericParserMainDocument;


public class StackTraceHelper {
	
	private static Logger LOGGER = LoggerFactory.getLogger(StackTraceHelper.class);

	public enum ON_EXCEPTION {
		QUIT, CONTINUE, QUITONNONX
	}

	public static String getTrace(Exception e) {
		return getTrace(e.getStackTrace());
	}

	private static String getTrace(StackTraceElement[] stackTrace) {
		String res = "";
		for (StackTraceElement elem : stackTrace) {
			res += "\t" + elem.toString() + "\n";
		}
		return res;
	}

	public static String getTrace(Throwable throwable) {
		return getTrace(throwable.getStackTrace());
	}

	public static void handleException(Throwable e, ON_EXCEPTION onException) {
		handleException(e, onException, null);
	}
	
	public static void handleException(Throwable e, ON_EXCEPTION onException, String m) {
		String message = "";
		if (!StringH.isNullOrEmpty(m))
			message += m + "\n";
		message += "Critical failure! This text has been copied to clipboard. Please paste this text into an email and send it to me. Exiting: \n\n" + e.toString() + "\n\n" + 
				e.getMessage() + "\n\n" + StackTraceHelper.getTrace(e);
		e.printStackTrace();

		System.out.println(message);
		LOGGER.error(message);
		if (GenericParserMainDocument.useX()) {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents( new StringSelection(message), null);
			JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
		}

		if (onException == ON_EXCEPTION.QUIT || (onException == ON_EXCEPTION.QUITONNONX && !GenericParserMainDocument.useX()))
			System.exit(1);	
	}

	
}
