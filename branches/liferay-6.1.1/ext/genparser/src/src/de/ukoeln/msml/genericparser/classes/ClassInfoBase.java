package de.ukoeln.msml.genericparser.classes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

import de.mosgrid.msml.jaxb.bindings.ObjectFactory;
import de.ukoeln.msml.genericparser.MSMLSimpleTreeValue;


public abstract class ClassInfoBase {

	private String _name;

	public ClassInfoBase(String name) {
		_name = name;
	}
	
	public String getName() {
		return _name;
	}

	@Override
	public String toString() {
		return getName();
	}

//
//	public void setObject(Object obj) {
//		_object = obj;
//	}
//	
//	public Object getObject() {
//		return _object;
//	}
//	
//	public abstract MSMLExtensionVisitor getVisitorInstance(MSMLTreeValueBase val);

	public abstract boolean isPrimitive();
	public abstract CLASSTYPE getType();

	public boolean isListType() {
		return false;
	}

	protected static class MSMLFactory {

		private static Hashtable<String, Method> _classToFactMethod;
		protected static ObjectFactory _fact = new ObjectFactory();
		
		static {
			_classToFactMethod = new Hashtable<String, Method>();
			for (Method m : _fact.getClass().getDeclaredMethods())
				_classToFactMethod.put(m.getReturnType().getCanonicalName(), m); 
		}

		@SuppressWarnings("unchecked")
		public static <T> T createInstance(Class<T> classObj) {
			try {
				return (T) _classToFactMethod.get(classObj.getCanonicalName()).invoke(_fact);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;				
		}
	}

	public void init(MSMLSimpleTreeValue res) {
	}
}
