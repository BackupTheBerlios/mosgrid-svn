package de.mosgrid.util.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.data.Validator;

import de.mosgrid.util.properties.ScalarProperty;

/**
 * Validator for String parameters. String parameters usually must not contain any non-word characters i.e. only
 * [a-zA-Z_0-9] is allowed
 * 
 * @author Andreas Zink
 * 
 */
public class StringParameterValidator implements Validator {
	private static final long serialVersionUID = -4009221900090285032L;

	private ScalarProperty property;
	// Forbid any non-word character, except '-'
	private final Pattern FORBIDDEN = Pattern.compile("[\\W&&[^-]]+");
	private String failedValidationMsg;

	public StringParameterValidator(ScalarProperty property) {
		super();
		this.property = property;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (!isValid(value)) {
			throw new InvalidValueException(failedValidationMsg);
		}

	}

	@Override
	public boolean isValid(Object value) {
		boolean isValid = false;
		if (value != null && value instanceof String && !value.equals("")) {
			Matcher matchResult = FORBIDDEN.matcher((String) value);
			if (matchResult.find()) {
				String forbiddenChar = matchResult.group();
				failedValidationMsg = "Must not contain " + forbiddenChar;
			} else {
				isValid = true;
			}
		} else if (property.isOptional()) {
			// value is null or empty but param is optional, thus valid
			isValid = true;
		}
		return isValid;
	}

}
