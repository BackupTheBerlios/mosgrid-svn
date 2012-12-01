package de.ukoeln.msml.genericparser.classes;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.mosgrid.msml.jaxb.bindings.Cml;
import de.ukoeln.msml.genericparser.GenericParserDocumentBase;
import de.ukoeln.msml.genericparser.MSMLSimpleTreeValue;
import de.ukoeln.msml.genericparser.MSMLTreeValue;
import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.classes.visitors.MSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.gui.extension.MSMLFileSelectorExtension;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtension;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtensionTextBased;
import de.ukoeln.msml.genericparser.worker.ClassDiscoverer;
import de.ukoeln.msml.genericparser.worker.ConfigHelper;

public abstract class ClassInfo extends ClassInfoBaseGeneric<MSMLExtensionVisitor, Object> {

	private Class<?> _class;

	protected ClassInfo(Class<?> clazz) {
		super(clazz.getSimpleName());
		_class = clazz;

	}


	public Class<?> getClassObj() {
		return _class;
	}

	@Override
	protected MSMLExtensionVisitor getVisitorInstance(MSMLTreeValueBase val) {
		return new MSMLExtensionVisitor(val);
	}
	
	

	@Override
	public MSMLExtensionVisitor runVisitor(GenericParserDocumentBase doc,
			MSMLTreeValueBase value) {
		if (value instanceof MSMLSimpleTreeValue)
			return runVisitorForSimpleVal(doc, (MSMLSimpleTreeValue) value);
		return runVisitorForTreeVal(doc, (MSMLTreeValue) value);
	}

	private MSMLExtensionVisitor runVisitorForTreeVal(GenericParserDocumentBase doc, MSMLTreeValue val) {
		MSMLExtensionVisitor visit = getVisitorInstance(val);
		if (val.isDynamicsListChild()) {
			String[] texts = val.getParentsTextAsDynamicList();
			if (texts.length > 0)
				visit.setText(texts[0]);
		}
		
		for (IMSMLParserExtension ext : doc.getExtensionsToVal(val)) {
			// TODO do this some other way. this is to skip file selector as
			// this component is allready
			// done by "getParentsTextAsDynamicList".
			if (val.isDynamicsListChild() && ext.getClass().getCanonicalName() == MSMLFileSelectorExtension.CLASSNAME)
				continue;
			HashMap<String, String> conf = ConfigHelper.getExtensionConfigAsHashMap(val.getConfig(), ext);
			if (conf.containsKey("version")) {
				((IMSMLParserExtensionTextBased) ext).getTextRetriever(conf.get("version")).getTextToConfig(visit, conf);
			}
		}
		return visit;
	}


	private MSMLExtensionVisitor runVisitorForSimpleVal(GenericParserDocumentBase doc, MSMLSimpleTreeValue val) {
		MSMLExtensionVisitor visit = getVisitorInstance(val);

		for (IMSMLParserExtension ext : doc.getExtensionsToVal(val)) {
			HashMap<String, String> conf = ConfigHelper.getExtensionConfigAsHashMap(val.getConfig(), ext);
			if (conf.containsKey("version")) {
				((IMSMLParserExtensionTextBased) ext).getTextRetriever(conf.get("version")).getTextToConfig(visit, conf);
			}
		}
		return visit;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setValue(String propName, ClassInfo classInfo) {
		initObject();
		
		if (classInfo.isListType()) {
			String methodName = "get" + propName.substring(0, 1).toUpperCase() + propName.substring(1);
			try {
				classInfo._object = _object.getClass().getMethod(methodName).invoke(_object);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			return;
		}

		if (isListType()) {
			((List)_object).add(classInfo._object);
			return;
		}

		if (!isPrimitive()) {
			String methodName = "set" + propName.substring(0, 1).toUpperCase() + propName.substring(1);
			try {
				_object.getClass().getMethod(methodName, classInfo.getClassObj()).invoke(_object, classInfo._object);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void initObject() {
		if (_object == null)
			setObject(MSMLFactory.createInstance(getClassObj()));
	}

	public abstract Hashtable<String, Field> getProperties();
	public abstract boolean isUntypedNonAnyList();
	public abstract void updateObject(String textToVal);

	public static class ClassInfoFactory {

		private static Hashtable<String, Class<?>> _msmlClasses = new Hashtable<String, Class<?>>();
		private static Hashtable<Class<?>, PrimitiveClassInfoGetter> _primitiveClasses = new Hashtable<Class<?>, PrimitiveClassInfoGetter>();
		private static ArrayList<Class<?>> _primTypes;
		private static ArrayList<Class<? extends ClassInfoBase>> _infoClasses = new ArrayList<Class<? extends ClassInfoBase>>();

		public static void initByJAR() {
			final JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileNameExtensionFilter("jar-Files", "jar"));
			int returnVal = fc.showOpenDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION) 
				return;

			ClassDiscoverer cd = new ClassDiscoverer();
			List<Class<?>> classes = cd.getClassesOfJar(fc.getSelectedFile().getAbsolutePath());
			init(classes);
		}

		public static void initFromClass(Class<?> clazz) {
			String pkgPath = clazz.getPackage().getName().replace(".", File.separator);
			String path = clazz.getProtectionDomain().getCodeSource().getLocation().getPath() + pkgPath;
			ClassDiscoverer cd = new ClassDiscoverer();
			List<Class<?>> classes = cd.getClassesOfPath(path);
			init(classes);
		}
		
		public static void initFromFactory(Class<?> clazz) {
			List<Class<?>> classes = new ArrayList<Class<?>>();
			for (Method m : clazz.getMethods()) {
				if (m.getName().startsWith("create"))
					classes.add(m.getReturnType());
			}

			// bit redundant as classes may be scanned more than once
			// maybe make a litte bit faster, but since this is only run once...
			List<Class<?>> classesToAdd;
			do {
				classesToAdd = new ArrayList<Class<?>>();
				for (Class<?> c : classes) {
					for (Field f : c.getDeclaredFields()) {
						Class<?> t = f.getType();
						if (!classes.contains(t) && 
								!t.getSimpleName().contains("List") &&
								!getPrimTypes().contains(t) &&
								!t.isArray()) {
							classesToAdd.add(f.getType());
						}
					}
				}
				classes.addAll(classesToAdd);
			} while (classesToAdd.size() > 0);
			init(classes);
		}

		private static List<Class<?>> getPrimTypes() {
			if (_primTypes == null) {
				_primTypes = new ArrayList<Class<?>>();
				Class<?>[] primTypes = {Boolean.class, Byte.class, Character.class, Double.class, Float.class, Integer.class, Long.class, Short.class, String.class, BigInteger.class};
				for (Class<?> c : primTypes) {
					_primTypes.add(c);
				}
			}
			return _primTypes;
		}

		private static void init(List<Class<?>> classes) {
			for (Class<?> clazz : classes) {
				_msmlClasses.put(clazz.getCanonicalName(), clazz);
			}
			_infoClasses.add(PropertyClassInfo.class);
			_infoClasses.add(MoleculeClassInfo.class);
			_infoClasses.add(SimpleRootInfo.class);
			_infoClasses.add(ArrayPropertyClassInfo.class);
			_infoClasses.add(LayerPropertyClassInfo.class);
			initPrimitives();
		}

		private static void initPrimitives() {
			_primitiveClasses.put(Boolean.class, new PrimitiveClassInfoGetter() {
				public ClassInfo get() {
					return new BooleanClassInfo(Boolean.class);
				}
			});
			_primitiveClasses.put(Byte.class, new PrimitiveClassInfoGetter() {
				public ClassInfo get() {
					return new ByteClassInfo(Byte.class);
				}
			});
			_primitiveClasses.put(Character.class, new PrimitiveClassInfoGetter() {
				public ClassInfo get() {
					return new CharacterClassInfo(Character.class);
				}
			});
			_primitiveClasses.put(Double.class, new PrimitiveClassInfoGetter() {
				public ClassInfo get() {
					return new DoubleClassInfo(Double.class);
				}
			});
			_primitiveClasses.put(Float.class, new PrimitiveClassInfoGetter() {
				public ClassInfo get() {
					return new FloatClassInfo(Float.class);
				}
			});
			_primitiveClasses.put(Integer.class, new PrimitiveClassInfoGetter() {
				public ClassInfo get() {
					return new IntegerClassInfo(Integer.class);
				}
			});
			_primitiveClasses.put(Long.class, new PrimitiveClassInfoGetter() {
				public ClassInfo get() {
					return new LongClassInfo(Long.class);
				}
			});
			_primitiveClasses.put(Short.class, new PrimitiveClassInfoGetter() {
				public ClassInfo get() {
					return new ShortClassInfo(Short.class);
				}
			});
			_primitiveClasses.put(String.class, new PrimitiveClassInfoGetter() {
				public ClassInfo get() {
					return new StringClassInfo(String.class);
				}
			});
			_primitiveClasses.put(BigInteger.class, new PrimitiveClassInfoGetter() {
				public ClassInfo get() {
					return new BigIntegerClassInfo(BigInteger.class);
				}
			});
		}

		public static ClassInfoBase getRootClassInfo() {
			return getClassInfo(_msmlClasses.get(Cml.class.getCanonicalName()));
		}

		public static ClassInfoBase getClassInfo(Class<?> clazz) {
			if (clazz.isEnum()) {
				return new EnumClassInfo(clazz);
			}
			if (_msmlClasses.containsKey(clazz.getCanonicalName())) {
				return new MSMLClassInfo(clazz);
			}
			if (_primitiveClasses.containsKey(clazz)) {
				return _primitiveClasses.get(clazz).get();
			}
			if (_infoClasses.contains(clazz)) {
				try {
					return (ClassInfoBase) clazz.getConstructor(new Class<?>[0]).newInstance(new Object[0]);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
			JOptionPane.showMessageDialog(null, "Need new class info type! " + new Throwable().getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
			System.exit(2);
			return null;
		}

		public static ClassInfo getClassInfo(Field field) {
			if (field.getType().getCanonicalName().equals(List.class.getCanonicalName())) {
				return new ListClassInfo(field);
			}
			return (ClassInfo) getClassInfo(field.getType());
		}

		private interface PrimitiveClassInfoGetter {
			ClassInfo get();
		}

		public static ClassInfo getClassInfo(String canonicalClassName) {
			return new MSMLClassInfo(_msmlClasses.get(canonicalClassName));
		}
	}
}
