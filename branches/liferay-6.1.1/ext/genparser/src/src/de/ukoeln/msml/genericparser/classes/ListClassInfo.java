package de.ukoeln.msml.genericparser.classes;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import de.ukoeln.msml.genericparser.worker.StackTraceHelper;

public class ListClassInfo extends ClassInfo {

	private List<ClassInfoBase> _componentClasses = new ArrayList<ClassInfoBase>();

	public ListClassInfo(Field field) {
		super(field.getType());
		
		Type lstType = field.getGenericType();
		Type[] args = ((ParameterizedType) lstType).getActualTypeArguments();
		if (args.length != 1) {
			JOptionPane.showMessageDialog(null, "Generic getter with more than one argument detected: " +
					StackTraceHelper.getTrace(new Throwable()), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		// if it is a list with component type Object we need to look for annotations to
		// tell what types this list may actually contain.
		Class<?> componentType = (Class<?>) args[0];
		if (componentType.getCanonicalName().equals(Object.class.getCanonicalName())) {
			XmlElements xmlElement = field.getAnnotation(XmlElements.class);
			XmlAnyElement xmlAnyElement = field.getAnnotation(XmlAnyElement.class);
			if (xmlElement == null && xmlAnyElement == null) {
				JOptionPane.showMessageDialog(null, "Object-List without XMLElements or XMLAnyElement Annotation: " +
						StackTraceHelper.getTrace(new Throwable()), "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
			
			if (xmlElement != null) {
				for (XmlElement elem : xmlElement.value()) {
					_componentClasses.add(ClassInfo.ClassInfoFactory.getClassInfo(elem.type()));
				}				
			} else if (xmlAnyElement != null) {
				if (xmlAnyElement.lax())
					_componentClasses.add(new LAXClassInfo(Object.class));
				else {
					JOptionPane.showMessageDialog(null, "New type of AnyElement needed: " +
							StackTraceHelper.getTrace(new Throwable()), "Error", JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}
			}			
		} else {
			_componentClasses.add(ClassInfo.ClassInfoFactory.getClassInfo(componentType));			
		}	
	}
	

	@Override
	public CLASSTYPE getType() {
		CLASSTYPE t = CLASSTYPE.LIST;
		for (ClassInfoBase c : _componentClasses) {
			t = CLASSTYPE.or(t, c.getType());
		}
		return t;
	}

	public List<ClassInfoBase> getPossibleTypes() {
		return _componentClasses;
	}

	@Override
	public Hashtable<String, Field> getProperties() {
		if (_componentClasses.size() != 1)
			return null;
		return ((ClassInfo)_componentClasses.get(0)).getProperties();
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public boolean isListType() {
		return true;
	}

	@Override
	public boolean isUntypedNonAnyList() {
		return _componentClasses.size() > 1;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void clearObject() {
		((List)_object).clear();
	}

	@Override
	public void updateObject(String textToVal) {}
}
