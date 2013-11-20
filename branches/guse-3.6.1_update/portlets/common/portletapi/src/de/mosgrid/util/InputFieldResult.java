package de.mosgrid.util;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;

import com.vaadin.ui.Field;

public class InputFieldResult {
	
	private Collection<Field> _fields = new ArrayList<Field>();
	private String _error = "";
	
	public void addField(Field field) {
		_fields.add(field);
	}
	
	public void addError(Logger logger, String string) {
		logger.warn(string);
		_error += string;
	}
	
	public String getError() {
		return _error;
	}
	
	public boolean hasError() {
		return _error != null && !"".equals(_error);
	}
	
	public Collection<Field> getFields() {
		return _fields;
	}
}
