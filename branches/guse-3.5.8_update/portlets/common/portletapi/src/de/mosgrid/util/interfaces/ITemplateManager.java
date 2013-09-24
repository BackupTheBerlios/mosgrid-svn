package de.mosgrid.util.interfaces;



import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;
import hu.sztaki.lpds.pgportal.services.asm.beans.ASMRepositoryItemBean;

import java.util.Collection;

import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.msml.util.IDictionary;

/**
 * Interface for TemplateManagers
 * 
 * @author Andreas Zink
 * 
 */
public interface ITemplateManager {
	/**
	 * Checks for updated template files
	 */
	void update();
	/**
	 * @return All templates for the corresponding domain
	 */
	Collection<MSMLTemplate> getAllTemplates();

	/**
	 * @return All templates which can be used for given workflow bean (may be empty)
	 */
	Collection<MSMLTemplate> getTemplatesByWorkflow(ASMRepositoryItemBean wkfBean);
	
	/**
	 * @return The template for given asm instance (may return null)
	 */
	MSMLTemplate getTemplateByWorkflow(ASMWorkflow wkfInstance);

	/**
	 * @return All templates for given toolsuite
	 */
	Collection<MSMLTemplate> getTemplatesByToolSuite(String toolSuite);

	/**
	 * @return All templates which use given dictionary
	 */
	Collection<MSMLTemplate> getTemplatesByDictionary(IDictionary dict);

	/**
	 * @return All templates which use given namespace
	 */
	Collection<MSMLTemplate> getTemplatesByNamespace(String namespace);

	/**
	 * Trigger refresh of all templates.
	 */
	void refreshTemplates();
}
