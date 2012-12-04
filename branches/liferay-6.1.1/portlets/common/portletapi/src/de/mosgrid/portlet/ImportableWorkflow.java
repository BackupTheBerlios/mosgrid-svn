package de.mosgrid.portlet;

import hu.sztaki.lpds.pgportal.services.asm.beans.ASMRepositoryItemBean;
import de.mosgrid.msml.editors.MSMLTemplate;

/**
 * Represents an importable workflow instance i.e. a container for MSML template and corresponding ASM workflow
 * instance.
 * 
 * @author Andreas Zink
 * 
 */
public class ImportableWorkflow {
	private DomainId domainId;
	private MSMLTemplate template;
	private ASMRepositoryItemBean workflow;

	/**
	 * @param domainId
	 *            The domain under which importable was created
	 * @param template
	 *            The workflow template to be used
	 * @param workflow
	 *            The corresponding workflow to be used
	 */
	public ImportableWorkflow(DomainId domainId, MSMLTemplate template, ASMRepositoryItemBean workflow) {
		this.domainId = domainId;
		this.template = template;
		this.workflow = workflow;
	}

	/**
	 * @return The MSML template of this importable
	 */
	public MSMLTemplate getTemplate() {
		return template;
	}

	/**
	 * @return The workflow instance of this importable
	 */
	public ASMRepositoryItemBean getWorkflowBean() {
		return workflow;
	}

	/**
	 * @return The corresponding domain
	 */
	public DomainId getDomainId() {
		return domainId;
	}

	@Override
	public String toString() {
		if (template.getJobListElement().getTitle() != null) {
			return template.getJobListElement().getTitle();
		} else {
			return template.getJobListElement().getId();
		}
	}

}
