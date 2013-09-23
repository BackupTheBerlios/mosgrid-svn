package de.mosgrid.util;

import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;
import hu.sztaki.lpds.pgportal.services.asm.beans.ASMRepositoryItemBean;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.msml.enums.DictDir;
import de.mosgrid.msml.enums.TemplateDir;
import de.mosgrid.msml.util.DictionaryFactory;
import de.mosgrid.msml.util.IDictionary;
import de.mosgrid.portlet.DomainId;
import de.mosgrid.portlet.ImportableID;
import de.mosgrid.portlet.ImportedID;
import de.mosgrid.util.interfaces.ITemplateManager;

/**
 * Helper class for loading and managing all domain specific templates
 * 
 * @author Andreas Zink
 * 
 */
public class DefaultTemplateManager implements ITemplateManager {
	private final Logger LOGGER = LoggerFactory.getLogger(DefaultTemplateManager.class);
	private static HashMap<DomainId, DefaultTemplateManager> instances = new HashMap<DomainId, DefaultTemplateManager>();
	/* mappings */
	// Maps toolsuite names to corresponding dictionary
	private Hashtable<String, IDictionary> toolsuite2Dict = new Hashtable<String, IDictionary>();
	// Maps namespaces to templates which use them
	private Hashtable<String, Set<MSMLTemplate>> namespace2Templates = new Hashtable<String, Set<MSMLTemplate>>();
	// Maps an ImportedID to a template. Can be used to identify templates of already imported ASM instances.
	private Hashtable<ImportedID, MSMLTemplate> wkfInstance2Template = new Hashtable<ImportedID, MSMLTemplate>();
	// Maps ImportableID to a list of templates. Can be used to identify all templates which can be used for a workflow.
	private Hashtable<ImportableID, Set<MSMLTemplate>> wkfBean2Template = new Hashtable<ImportableID, Set<MSMLTemplate>>();
	// Maps filename to last modified timestemp
	private Map<String, Long> filename2lastModified = new HashMap<String, Long>();

	private Map<String, MSMLTemplate> filename2template;
	private Collection<IDictionary> allDicts;

	// update lock
	private final Object updateLock = new Object();
	private DomainId _domainID;

	public static synchronized ITemplateManager getInstance(DomainId domainId) {
		if (!instances.containsKey(domainId)) {
			instances.put(domainId, new DefaultTemplateManager(domainId));
		}
		return instances.get(domainId);
	}

	/**
	 * This is a singleton
	 */
	private DefaultTemplateManager(DomainId domainId) {
		LOGGER.debug("Initializing TemplateManager for " + domainId.getName());
		_domainID = domainId;
		initDictionaries(domainId);
		initTemplates(domainId);
	}

	/**
	 * Loads domain specific dictionaries
	 */
	private void initDictionaries(DomainId domainId) {
		DictDir dictDir = domainId.getDictDir();
		if (dictDir == null) {
			LOGGER.warn("Could not find dictionary directory for " + domainId.getName());
		}
		this.allDicts = DictionaryFactory.getInstance().getDictionaries(dictDir);
	}

	/**
	 * Loads template files and wraps them with MSMLTemplate
	 */
	private void initTemplates(DomainId domainId) {
		filename2template = new HashMap<String, MSMLTemplate>();
		File[] files = getTemplateFiles(domainId);
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					createTemplate(file);
				}
			}
		}
		if (LOGGER.isDebugEnabled()) {
			StringBuilder msgBuilder = new StringBuilder();
			msgBuilder.append("ALL available templates:");
			for (MSMLTemplate template : filename2template.values()) {
				msgBuilder.append("\n\t" + template.getName());
			}
			LOGGER.debug(msgBuilder.toString());
			
			String logMessage = "Dumping ImportableIDs -> [Templates]\n";
			for (Entry<ImportableID, Set<MSMLTemplate>> id : wkfBean2Template.entrySet()) {
				logMessage += "\t" + id.getKey() + " ->\n";
				for (MSMLTemplate dumpTemplate : id.getValue()) {
					logMessage += "\t\t Name: " + dumpTemplate.getName() + 
							" Id: " + dumpTemplate.getJobListElement().getId() +
							" Notes: " + dumpTemplate.getJobListElement().getWorkflowNotes() + "\n";
				}
				logMessage += "\n";
			}
			LOGGER.debug(logMessage);
		}
	}

	/**
	 * Parse MSML template from given file
	 */
	private void createTemplate(File file) {
		try {
			filename2lastModified.put(file.getName(), file.lastModified());
			MSMLTemplate newTemplate = new MSMLTemplate(file);
			mapTemplate(newTemplate);
			filename2template.put(file.getName(), newTemplate);
		} catch (Exception e) {
			LOGGER.error("Parsing of: " + file.getAbsolutePath() + " to MSML Template failed: " + e.toString());
		}

	}

	/**
	 * Gets all template files contained in domain specific template dir
	 */
	private File[] getTemplateFiles(DomainId domainId) {
		// get temlate dir name for domain
		TemplateDir templateDir = domainId.getTemplateDir();
		if (templateDir == null) {
			LOGGER.warn("Could not find template directory for " + domainId.getName());
		}

		File templateFolder = new File(templateDir.getPath());
		File[] files = null;
		if (templateFolder.exists()) {
			LOGGER.trace("Searching for templates in " + templateFolder);
			files = templateFolder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File fileName) {
					String name = fileName.getName();
					return name.endsWith(".xml") || name.endsWith(".cml") || name.endsWith(".msml");
				}
			});
		} else {
			LOGGER.warn("The desired template directory " + templateFolder + "does not exist!");
		}
		return files;
	}

	@Override
	public void update() {
		synchronized (updateLock) {
			LOGGER.debug("Updating templates for " + _domainID.getName());
			Set<String> nonProcessedFiles = new HashSet<String>(filename2lastModified.keySet());
			File[] files = getTemplateFiles(_domainID);
			if (files != null) {
				for (File file : files) {
					String filename = file.getName();
					if (filename2lastModified.containsKey(filename)) {
						nonProcessedFiles.remove(filename);
						long lastModified = file.lastModified();
						if (lastModified != filename2lastModified.get(filename)) {
							LOGGER.trace("Updating changes in template " + filename);
							MSMLTemplate oldTemplate = filename2template.get(filename);
							filename2template.remove(filename);
							deleteTemplateMappings(oldTemplate);
							createTemplate(file);
						}
					} else {
						LOGGER.trace("Adding new template " + filename);
						createTemplate(file);
					}
				}
				// remove old templates which could not be assigned to any existing file
				for (String filename : nonProcessedFiles) {
					LOGGER.trace("Deleting importable instance because template " + filename
							+ " could not be found anymore.");
					MSMLTemplate oldTemplate = filename2template.get(filename);
					filename2template.remove(filename);
					deleteTemplateMappings(oldTemplate);
				}
			}
			clearEmptyMappings();
		}
	}

	public void refreshTemplates() {
		initTemplates(_domainID);
	}

	/**
	 * Clears empty maps duo to updates
	 */
	private void clearEmptyMappings() {
		// do not iterate and delete simultaneously

		// mark
		Set<String> markedNamespaces = new HashSet<String>();
		for (Map.Entry<String, Set<MSMLTemplate>> entry : namespace2Templates.entrySet()) {
			if (entry.getValue() == null || entry.getValue().size() == 0) {
				markedNamespaces.add(entry.getKey());
			}
		}
		// remove
		for (String key : markedNamespaces) {
			namespace2Templates.remove(key);
		}
		// mark
		Set<ImportableID> markedImportables = new HashSet<ImportableID>();
		for (Map.Entry<ImportableID, Set<MSMLTemplate>> entry : wkfBean2Template.entrySet()) {
			if (entry.getValue() == null || entry.getValue().size() == 0) {
				markedImportables.add(entry.getKey());
			}
		}
		// remove
		for (ImportableID key : markedImportables) {
			wkfBean2Template.remove(key);
		}
	}

	/**
	 * deletes old MSMLTemplate instances from mappings
	 */
	private void deleteTemplateMappings(MSMLTemplate oldTemplate) {
		// do not iterate and delete simultaneously!

		if (oldTemplate != null) {
			// mark
			Hashtable<String, Set<MSMLTemplate>> markedTemplates = new Hashtable<String, Set<MSMLTemplate>>();
			for (Entry<String, Set<MSMLTemplate>> mapEntry : namespace2Templates.entrySet()) {
				for (MSMLTemplate template : mapEntry.getValue()) {
					if (oldTemplate.equals(template)) {
						if (markedTemplates.get(mapEntry.getKey()) == null) {
							markedTemplates.put(mapEntry.getKey(), new HashSet<MSMLTemplate>());
						}
						markedTemplates.get(mapEntry.getKey()).add(oldTemplate);
					}
				}
			}
			// delete
			for (Entry<String, Set<MSMLTemplate>> mapEntry : markedTemplates.entrySet()) {
				for (MSMLTemplate template : mapEntry.getValue()) {
					namespace2Templates.get(mapEntry.getKey()).remove(template);
				}
			}

			// mark
			Set<ImportedID> markedImported = new HashSet<ImportedID>();
			for (Map.Entry<ImportedID, MSMLTemplate> mapEntry : wkfInstance2Template.entrySet()) {
				if (oldTemplate.equals(mapEntry.getValue())) {
					markedImported.add(mapEntry.getKey());
				}
			}
			// delete
			for (ImportedID key : markedImported) {
				wkfInstance2Template.remove(key);
			}

			// mark
			Hashtable<ImportableID, Set<MSMLTemplate>> markedTemps = new Hashtable<ImportableID, Set<MSMLTemplate>>();
			for (Entry<ImportableID, Set<MSMLTemplate>> mapEntry : wkfBean2Template.entrySet()) {
				for (MSMLTemplate template : mapEntry.getValue()) {
					if (oldTemplate.equals(template)) {
						if (markedTemps.get(mapEntry.getKey()) == null) {
							markedTemps.put(mapEntry.getKey(), new HashSet<MSMLTemplate>());
						}
						markedTemps.get(mapEntry.getKey()).add(oldTemplate);
					}
				}
			}
			// delete
			for (Entry<ImportableID, Set<MSMLTemplate>> mapEntry : markedTemps.entrySet()) {
				for (MSMLTemplate template : mapEntry.getValue()) {
					wkfBean2Template.get(mapEntry.getKey()).remove(template);
				}
			}
		}
	}

	/**
	 * Helper which creates mappings for a newly create template
	 */
	private void mapTemplate(MSMLTemplate template) {
		// Mapping for importables. ASM wkf bean can be mapped to more than one template
		ImportableID importableID = new ImportableID(template);
		if (!wkfBean2Template.containsKey(importableID)) {
			// create list and add template
			Set<MSMLTemplate> templatateSet = new HashSet<MSMLTemplate>();
			templatateSet.add(template);
			wkfBean2Template.put(importableID, templatateSet);
		} else {
			// just add template to list
			if (wkfBean2Template.get(importableID).contains(template)) {
				wkfBean2Template.get(importableID).remove(template);
			}
			wkfBean2Template.get(importableID).add(template);
		}
	
		// Mapping for imported. ASM wkf instance can be mapped to one template only
		ImportedID importedID = new ImportedID(template);
		wkfInstance2Template.put(importedID, template);

		// Namespace Mapping
		for (String namespace : template.getNamespaces()) {
			if (!namespace2Templates.containsKey(namespace)) {
				// create list and add template
				Set<MSMLTemplate> templateSet = new HashSet<MSMLTemplate>();
				templateSet.add(template);
				namespace2Templates.put(namespace, templateSet);
			} else {
				// just add template to list
				if (namespace2Templates.get(namespace).contains(template)) {
					namespace2Templates.get(namespace).remove(template);
				}
				namespace2Templates.get(namespace).add(template);
			}
		}
	}

	@Override
	public Collection<MSMLTemplate> getAllTemplates() {
		// wait for updates
		synchronized (updateLock) {
			return filename2template.values();
		}
	}

	@Override
	public MSMLTemplate getTemplateByWorkflow(ASMWorkflow wkfInstance) {
		// wait for updates
		synchronized (updateLock) {
			ImportedID searchID = new ImportedID(wkfInstance);
			if (wkfInstance2Template.containsKey(searchID)) {
				return wkfInstance2Template.get(searchID);
			}

			return null;
		}
	}

	@Override
	public Collection<MSMLTemplate> getTemplatesByWorkflow(ASMRepositoryItemBean wkfBean) {
		// wait for updates
		synchronized (updateLock) {
			LOGGER.trace("Searching for templates for " + wkfBean.getItemID());
			ImportableID searchID = new ImportableID(wkfBean);
			if (wkfBean2Template.containsKey(searchID)) {
				Set<MSMLTemplate> templates = wkfBean2Template.get(searchID);
				LOGGER.trace("Found " + templates.size() + " templates for " + wkfBean.getItemID());
				return templates;
			}
			return new ArrayList<MSMLTemplate>();
		}
	}

	@Override
	public synchronized Collection<MSMLTemplate> getTemplatesByToolSuite(String toolSuite) {
		// wait for updates
		synchronized (updateLock) {
			if (!toolsuite2Dict.containsKey(toolSuite)) {
				for (IDictionary dict : allDicts) {
					if (toolSuite.equals(dict.getToolSuite())) {
						toolsuite2Dict.put(toolSuite, dict);
						break;
					}
				}
				if (!toolsuite2Dict.containsKey(toolSuite)) {
					LOGGER.error("Could not resolve toosuite '" + toolSuite + "' to any dictionary.");
					return null;
				}
			}

			return getTemplatesByDicitonary(toolsuite2Dict.get(toolSuite));
		}
	}

	@Override
	public synchronized Collection<MSMLTemplate> getTemplatesByDicitonary(IDictionary dict) {
		return getTemplatesByNamespace(dict.getNamespace());
	}

	@Override
	public synchronized Collection<MSMLTemplate> getTemplatesByNamespace(String namespace) {
		// wait for updates
		synchronized (updateLock) {
			if (!namespace2Templates.containsKey(namespace)) {
				Set<MSMLTemplate> templates = new HashSet<MSMLTemplate>();
				for (MSMLTemplate template : filename2template.values()) {
					try {
						if (template.hasNamespace(namespace))
							templates.add(template);
					} catch (Exception e) {
						LOGGER.warn("Failed to evaluate key '" + namespace + "' to one of the templates.");
					}
				}
				namespace2Templates.put(namespace, templates);
			}
			return namespace2Templates.get(namespace);
		}
	}

}
