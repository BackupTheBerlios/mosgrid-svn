package de.mosgrid.gui.panels;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.data.Validator;

import de.mosgrid.util.WorkflowHelper;


/**
 * Just a small validator for the import name which the user specifies in the workflow import panel
 * 
 * @author Andreas Zink
 * 
 */
public class ImportNameValidator implements Validator {
	private static final long serialVersionUID = 8731607013486722386L;
	// Forbid any non-word character, except ':','.' and '-'
	private final Pattern FORBIDDEN = Pattern.compile("[\\W&&[^:.-]]+");
	private String failedValidationMsg;

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (!isValid(value)) {
			throw new InvalidValueException(failedValidationMsg);
		}
	}

	@Override
	public boolean isValid(Object value) {
		boolean valid = false;
		if (value != null && value instanceof String) {
			// remove leading and trailing white spaces
			String stringValue = ((String) value).trim();

			// NOTE: call exists at last in order to prevent IOExceptions
			if (!isEmpty(stringValue) && !containsSeparator(stringValue) && !containsForbiddenChars(stringValue)
//					&& !importExists(stringValue) && !xfsDirExists(stringValue)
					) {
				valid = true;
			}
		}
		return valid;
	}

	/**
	 * Checks whether given import name is empty
	 */
	private boolean isEmpty(String importName) {
		if (importName.equals("")) {
			failedValidationMsg = "Name must not be empty!";
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks whether given import name contains the forbidden separator
	 */
	private boolean containsSeparator(String importName) {
		if (importName.contains(WorkflowHelper.SEPARATOR)) {
			failedValidationMsg = "Name must not contain '" + WorkflowHelper.SEPARATOR + "'";
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks whether given import name contains any forbidden chars
	 */
	private boolean containsForbiddenChars(String importName) {
		Matcher matchResult = FORBIDDEN.matcher(importName);
		if (matchResult.find()) {
			String forbiddenChar = matchResult.group();
			failedValidationMsg = "Name must not contain " + forbiddenChar;
			return true;
		} else {
			return false;
		}
	}

//	/**
//	 * Checks if there already is an import with equal import name
//	 */
//	private boolean importExists(String importName) {
//		Collection<ImportedWorkflow> importedList = portlet.getImportedWorkflowInstances();
//		for (ImportedWorkflow wkfImport : importedList) {
//			if (importName.equals(wkfImport.getUserImportName())) {
//				failedValidationMsg = "You already imported a workflow as '" + importName
//						+ "'. Please alter the name and try again.";
//				return true;
//			}
//		}
//		return false;
//	}

//	/**
//	 * Checks whether given import name already exists in xfs
//	 */
//	private boolean xfsDirExists(String importName) {
//		boolean exists = true;
//		XfsBridge xfsBridge = portlet.getXfsBridge();
//		try {
//			exists = xfsBridge.exists(xfsBridge.getUploadDir() + "/" + importName);
//			if (exists) {
//				failedValidationMsg = "The directory '" + importName
//						+ "' already exists in your file storage. Please alter the name and try again.";
//			}
//		} catch (IOException e) {
//			String msg = "Could not check if '" + importName + "' already exists.";
//			failedValidationMsg = msg + " Please try again or contact the support.";
//			LOGGER.error(portlet.getUser() + msg, e);
//		}
//		return exists;
//	}
}