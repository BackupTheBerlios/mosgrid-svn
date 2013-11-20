package de.mosgrid.msml.util.wrapper;

import java.util.ArrayList;
import java.util.List;

import de.mosgrid.msml.editors.MSMLEditor;
import de.mosgrid.msml.enums.Namespaces;
import de.mosgrid.msml.jaxb.bindings.DescriptionType;
import de.mosgrid.msml.jaxb.bindings.ModuleType;
import de.mosgrid.msml.jaxb.bindings.ParserConfigType;
import de.mosgrid.msml.util.ObjectFactorySingelton;
import de.mosgrid.msml.util.helper.XmlHelper;

/**
 * Wrapper for a module element carrying the dictRef="compchem:jobList" attribute.
 * 
 * @author Andreas Zink
 * 
 */
public class JobList extends AbstractWrapper {
	public static final String ELEMENT_ID = "jobList";

	private List<Job> jobs;

	private JobListParserConfig _parserConfig;

	private String _workflowGraph;
	private DescriptionType _description;

	public JobList(ModuleType moduleElement, MSMLEditor editor) {
		super(moduleElement, ELEMENT_ID, editor);
		_workflowGraph = moduleElement.getWorkflowGraph();

		init();
	}

	private void init() {
		this.jobs = new ArrayList<Job>();

		for (Object child : getChildElements()) {
			// if child is a module element
			if (child instanceof ModuleType) {
				ModuleType module = (ModuleType) child;
				String dictRef = module.getDictRef();
				String elementId = XmlHelper.getInstance().getSuffix(dictRef);
				// check dictRef and wrap child module
				if (elementId.equals(Job.ELEMENT_ID)) {
					Job job = new Job(module, getEditor());
					jobs.add(job);
				}
			} else if (child instanceof ParserConfigType) {
				_parserConfig = new JobListParserConfig(getEditor(), (ParserConfigType) child);
			} else if (child instanceof DescriptionType) {
				_description = (DescriptionType) child;
			}
		}
	}

	/**
	 * @return Jobs of this jobList (can be empty if no jobs specified)
	 */
	public List<Job> getJobs() {
		return jobs;
	}

	private void addJob(Job job) {
		job.addSelfToList(getChildElements());
		jobs.add(job);
	}

	public void removeJobById(Job job) {
		String id = job.getId();
		Job foundJob = null;
		for (Job elem : jobs) {
			if (elem != null && elem.getId() != null && elem.getId().equals(id))
				foundJob = elem;
		}
		if (foundJob == null)
			return;
		getChildElements().remove(foundJob.getJaxBElement());
		jobs.remove(foundJob);
	}

	/**
	 * @return The parser config child element or null if not given
	 */
	public JobListParserConfig getParserConfig() {
		return _parserConfig;
	}

	/**
	 * @return Additional workflow notes of the gUSE workflow
	 */
	public String getWorkflowNotes() {
		return getModuleElement().getWorkflowNotes();
	}

	/**
	 * @return The display name for the template
	 */
	public String getDisplayName() {
		return getModuleElement().getDisplayName();
	}

	public void setWorkflowNotes(String notes) {
		if (notes == null || "".equals(notes))
			getModuleElement().setWorkflowNotes(notes);
		else
			getModuleElement().setWorkflowNotes(notes);
	}

	public void setDisplayName(String displayName) {
		if (displayName == null || "".equals(displayName))
			getModuleElement().setDisplayName(null);
		else
			getModuleElement().setDisplayName(displayName);
	}
	
	public void setTitle(String description) {
		getModuleElement().setTitle(description);
	}

	public void setID(String id) {
		getModuleElement().setId(id);
	}

	public Job addNewJob() {
		ModuleType mod = ObjectFactorySingelton.getFactory().createModuleType();
		String prefix = getEditor().getPrefixToNamespace(Namespaces.COMPCHEM.getNamespace());
		mod.setDictRef(prefix + ":" + Job.ELEMENT_ID);
		Job newJob = new Job(mod, getEditor());
		addJob(newJob);
		return newJob;
	}

	public void addNewParserConfig() {
		if (_parserConfig != null)
			removeParserConfig();

		ParserConfigType jaxbConf = new ParserConfigType();
		JobListParserConfig conf = new JobListParserConfig(getEditor(), jaxbConf);
		conf.addSelfToList(getChildElements());
		_parserConfig = conf;
	}

	public void removeParserConfig() {
		if (_parserConfig == null)
			return;
		getChildElements().remove(_parserConfig.getJaxBElement());
		_parserConfig = null;		
	}

	/**
	 * @return The name of the workflow graph image or null if not given
	 */
	public String getWorkflowGraphName() {
		return _workflowGraph;
	}

	/**
	 * @return The description child element or null if not given
	 */
	public DescriptionType getDescription() {
		return _description;
	}

	public DescriptionType createDescriptionIfNotExisting() {
		if (getDescription() == null) {
			_description = ObjectFactorySingelton.getFactory().createDescriptionType();
			getChildElements().add(_description);
		}
		return getDescription();
	}

}
