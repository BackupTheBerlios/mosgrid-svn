package de.mosgrid.msml.editors;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.msml.enums.Namespaces;
import de.mosgrid.msml.jaxb.bindings.Cml;
import de.mosgrid.msml.jaxb.bindings.EntryType;
import de.mosgrid.msml.jaxb.bindings.ModuleType;
import de.mosgrid.msml.util.DictionaryFactory;
import de.mosgrid.msml.util.IDictionary;
import de.mosgrid.msml.util.ObjectFactorySingelton;
import de.mosgrid.msml.util.helper.XmlHelper;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.msml.util.wrapper.JobList;

/**
 * Enables reading and writing to CML files which use the CompChem convention
 * 
 * @author Andreas Zink
 * 
 */
public class JobListEditor extends MSMLEditor {
	private final Logger LOGGER = LoggerFactory.getLogger(JobListEditor.class);

	private Cml cmlRootElement;
	private JobList jobListElement;

	public JobListEditor(File msmlTemplate) {
		super(msmlTemplate);
		init();
	}

	/**
	 * This constructor is needed for GenericParser and should not be used in general. Instantiating an editor in that
	 * way offers limited functionality to namespaces etc..
	 * 
	 * @param cml
	 *            A cml-Object parsed by the MSMLGenericParser.
	 */
	public JobListEditor(Cml cml) {
		super(cml);
		init();
	}
	
	/**
	 * This constructor is needed for GenericParser and should not be used in general.
	 * 
	 * @param content
	 *            The content of an msml-File.
	 */
	public JobListEditor(String content) {
		super(content);
		init();
	}

	@Override
	public JobListEditor copy() {
		if (msmlFile != null) {
			return new JobListEditor(msmlFile);
		} else if (cmlRootElement != null) {
			return new JobListEditor(cmlRootElement);
		} else {
			throw new UnsupportedOperationException(
					"Can't create copy because instance was not created from File or Cml");
		}
	}

	private void init() {
		if (getRootElement() instanceof Cml) {
			cmlRootElement = (Cml) getRootElement();
			initJobList();
		} else {
			throw new IllegalArgumentException("Given msml file has no cml root element!");
		}
	}

	private void initJobList() {
		// iterate over child elements
		for (ModuleType childElement : cmlRootElement.getModule()) {
			String dictRef = childElement.getDictRef();
			String elementId = XmlHelper.getInstance().getSuffix(dictRef);
			// find jobList module element
			if (elementId.equals(JobList.ELEMENT_ID)) {
				this.jobListElement = new JobList(childElement, this);
				break;
			}
		}
	}

	/**
	 * @return The JobList element
	 */
	public JobList getJobListElement() {
		return jobListElement;
	}
	
	
	public void addDefaultJobListIfNoneExists() {
		if (jobListElement != null)
			return;
		ModuleType mt = ObjectFactorySingelton.getFactory().createModuleType();
		addNamespace(Namespaces.COMPCHEM);
		mt.setDictRef(getPrefixToNamespace(Namespaces.COMPCHEM.getNamespace()) + ":" + JobList.ELEMENT_ID);
		mt.setTitle("TITLE");
		mt.setId("DUMMY");
		cmlRootElement.getModule().add(mt);
		jobListElement = new JobList(mt, this);
	}

	/**
	 * This method searches the joblist of the given MSML file for the job with the specified id.
	 * 
	 * @param id
	 *            The id of the job.
	 * @return This method returns the Job-Element-Wrapper of the specified job, if it has been found. null otherwise.
	 */
	public Job getJobWithID(String id) {
		Job foundJob = null;
		JobList joblist = getJobListElement();
		for (Job job : joblist.getJobs()) {
			if (id.equals(job.getId())) {
				foundJob = job;
				break;
			}
		}
		if (foundJob == null) {
			LOGGER.error("No job with ID '" + id + "' found.", new Throwable());
			return null;
		}
		return foundJob;
	}

	/**
	 * Helper method for finding dictionary entries for given dictRef
	 */
	public EntryType getDictEntry(String dictRef) {
		String prefix = XmlHelper.getInstance().getPrefix(dictRef);
		String namespace = getNamespaceToPrefix(prefix);
		IDictionary dict = DictionaryFactory.getInstance().getDictionary(namespace);
		if (dict == null) {
			String message = "Could not retrieve dictionary: " + namespace + " for "+dictRef+"\nNamespaces are:\n";
			for (String ns : getNamespaces())
				message += ns + "\n";
			LOGGER.error(message);
		}

		String entryId = XmlHelper.getInstance().getSuffix(dictRef);

		return dict.getEntryById(entryId);
	}
}
