package de.mosgrid.msml.util.wrapper;

import de.mosgrid.msml.editors.MSMLEditor;
import de.mosgrid.msml.jaxb.bindings.ModuleType;
import de.mosgrid.msml.jaxb.bindings.PropertyListType;
import de.mosgrid.msml.jaxb.bindings.PropertyType;
import de.mosgrid.msml.util.ObjectFactorySingelton;
import de.mosgrid.msml.util.helper.XmlHelper;

/**
 * Wrapper for a module element carrying the dictRef="compchem:environment" attribute.
 * 
 * @author Andreas Zink
 * 
 */
public class JobEnvironment extends AbstractWrapper {
	
	private static final String CORES = "cores";
	private static final String WALLTIME = "walltime";
	private static final String NODES = "nodes";
	private static final String MEMORY = "memory";
	private static ModuleType _defaultJob;
	
//	private final Logger LOGGER = LoggerFactory.getLogger(JobEnvironment.class);

	public static final String ELEMENT_ID = "environment";

	private PropertyListType parameterList;

	private PropertyType _memory;

	private PropertyType _nodes;

	private PropertyType _walltime;

	private PropertyType _cores;

	static {
		_defaultJob = new ModuleType();
		_defaultJob.setDictRef("compchem:" + ELEMENT_ID); // dict is adjusted with job's dictref in constructor
		PropertyType prop = ObjectFactorySingelton.getFactory().createPropertyType();
		_defaultJob.getParserConfigurationAndAdapterConfigurationAndUploadList().add(prop);
	}
	
	/**
	 * Contructor used by TemplateDesigner
	 */
	public JobEnvironment(Job job) {
		super(_defaultJob, ELEMENT_ID, job.getEditor());
		String pref = XmlHelper.getInstance().getPrefix(job.getModuleElement().getDictRef());
		getModuleElement().setDictRef(pref + ":" + ELEMENT_ID);
		job.setEnvironment(this);
	}
	
	public JobEnvironment(ModuleType moduleElement, MSMLEditor editor) {
		super(moduleElement, ELEMENT_ID, editor);
		init();
	}

	private void init() {
		for (Object child : getChildElements()) {
			if (child instanceof PropertyListType) {
				this.parameterList = (PropertyListType) child;
			}
		}
		// PropertyList not found
		if (getProperties() == null) {
			parameterList = new PropertyListType();
			getChildElements().add(parameterList);
		}

		parseParameter();
	}

	private void parseParameter() {
		for (PropertyType param : getProperties().getProperty()) {
			String dictRef = param.getDictRef();
			if (dictRef == null || "".equals(dictRef))
				continue;

			dictRef = XmlHelper.getInstance().getSuffix(dictRef);
			if (dictRef.contains(MEMORY))
				_memory = param;
			else if (dictRef.contains(NODES))
				_nodes = param;
			else if (dictRef.contains(WALLTIME))
				_walltime = param;
			else if (dictRef.contains(CORES))
				_cores = param;
		}
	}

	/**
	 * @return PropertyList of this element or null if not given
	 */
	public PropertyListType getProperties() {
		return parameterList;
	}

	public Integer getMemoryValue() {
		if (_memory == null || _memory.getScalar() == null || _memory.getScalar().getValue() == null
				|| "".equals(_memory.getScalar().getValue()))
			return null;
		return Integer.parseInt(_memory.getScalar().getValue());
	}
	
	public Integer getNumberOfNodes() {
		if (_nodes == null || _nodes.getScalar() == null || _nodes.getScalar().getValue() == null
				|| "".equals(_nodes.getScalar().getValue()))
			return null;
		return Integer.parseInt(_nodes.getScalar().getValue());
	}
	
	public Integer getWalltimeValue() {
		if (_walltime == null || _walltime.getScalar() == null || _walltime.getScalar().getValue() == null
				|| "".equals(_walltime.getScalar().getValue()))
			return null;
		return Integer.parseInt(_walltime.getScalar().getValue());
	}
	
	public Integer getNumberOfCores() {
		if (_cores == null || _cores.getScalar() == null || _cores.getScalar().getValue() == null
				|| "".equals(_cores.getScalar().getValue()))
			return null;
		return Integer.parseInt(_cores.getScalar().getValue());
	}
	
	// TODO Rework set-mechanism to include tiny/large/medium-stuff
//	
//	public void setMemory(MSMLEditor ed, Integer mem, boolean editable) {
//		if (mem == null) {
//			delete(MEMORY);
//			_memory = null;
//			return;
//		}	
//		
//		
//		setProp(new PropDel() {
//			@Override
//			public void setProp(PropertyType val) {
//				_memory = val;
//			}
//			
//			@Override
//			public PropertyType getProp() {
//				return _memory;
//			}
//
//			@Override
//			public String getName() {
//				return MEMORY;
//			}
//		}, ed, mem, editable);
//
//	}
//	
//	public void setWalltime(MSMLEditor ed, Integer walltime, boolean editable) {
//		if (walltime == null) {
//			delete(WALLTIME);
//			_walltime = null;
//			return;
//		}	
//		
//		setProp(new PropDel() {
//			@Override
//			public void setProp(PropertyType val) {
//				_walltime = val;
//			}
//			
//			@Override
//			public PropertyType getProp() {
//				return _walltime;
//			}
//			
//			public String getName() {
//				return WALLTIME;
//			}
//		}, ed, walltime, editable);
//
//	}
//	
//	public void setNodes(MSMLEditor ed, Integer nodes, boolean editable) {
//		if (nodes == null) {
//			delete(NODES);
//			_nodes = null;
//			return;
//		}	
//		
//		setProp(new PropDel() {
//			@Override
//			public void setProp(PropertyType val) {
//				_nodes = val;
//			}
//			
//			@Override
//			public PropertyType getProp() {
//				return _nodes;
//			}
//
//			@Override
//			public String getName() {
//				return NODES;
//			}
//		}, ed, nodes, editable);
//
//	}
//	
//	public void setCores(MSMLEditor ed, Integer cores, boolean editable) {
//		if (cores == null) {
//			delete(CORES);
//			_cores = null;
//			return;
//		}	
//		
//		setProp(new PropDel() {
//			@Override
//			public void setProp(PropertyType val) {
//				_cores = val;
//			}
//			
//			@Override
//			public PropertyType getProp() {
//				return _cores;
//			}
//
//			@Override
//			public String getName() {
//				return CORES;
//			}
//		}, ed, cores, editable);
//
//	}

//	private void setProp(PropDel propDel, MSMLEditor ed, Integer mem, boolean editable) {
//		if (propDel.getProp() == null) {
//			propDel.setProp(new PropertyType());
//			propDel.getProp().setDictRef(ed.getPrefixToNamespace(Namespaces.ENVIRONMENT.getNamespace()) + ":" + propDel.getName());
//			propDel.getProp().setEditable(editable);
//			parameterList.getProperty().add(propDel.getProp());
//		}
//		if (propDel.getProp().getScalar() == null) {
//			ScalarType val = new ScalarType();
//			val.setDataType("xsd:integer");
//			propDel.getProp().setScalar(val);
//		}
//		propDel.getProp().getScalar().setValue(Integer.toString(mem));
//		
//	}

//	private void delete(String string) {
//		for (PropertyType param : getProperties().getProperty()) {
//			String dictRef = param.getDictRef();
//			if (dictRef == null || "".equals(dictRef))
//				continue;
//			
//			dictRef = XmlHelper.getInstance().getSuffix(dictRef);
//			if (dictRef.contains(string)) {
//				getProperties().getProperty().remove(param);
//				break;
//			}
//		}
//	}
	
//	private abstract class PropDel {
//		public abstract PropertyType getProp();
//		public abstract void setProp(PropertyType val);
//		public abstract String getName();
//	}

	public PropertyType createAndAddProperty() {
		if (getProperties() == null) {
			PropertyListType list = new PropertyListType();
			getChildElements().add(list);
			parameterList = list;			
		}
		PropertyType prop = new PropertyType();
		getProperties().getProperty().add(prop);
		return prop;
	}
}
