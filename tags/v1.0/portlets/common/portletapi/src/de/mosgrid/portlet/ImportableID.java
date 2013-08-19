package de.mosgrid.portlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.msml.editors.MSMLTemplate;
import hu.sztaki.lpds.pgportal.services.asm.beans.ASMRepositoryItemBean;

/**
 * Helper class which links MSML templates to ASM workflow beans. The class can be used as key in Maps. The ID consists
 * of 'workflow name':'workflow notes'.
 * 
 * @author krm
 */
public class ImportableID {
	private final Logger LOGGER = LoggerFactory.getLogger(ImportableID.class);

	private String realID;

	public ImportableID(MSMLTemplate template) {
		String workflowID = template.getJobListElement().getId();
		String workflowNotes = template.getJobListElement().getWorkflowNotes();
		if (workflowID == null || workflowNotes == null) {
			LOGGER.warn("Missing workflow-ID or notes! Both may be needed in order to identify a workflow correctly.");
		}
		if (workflowNotes == null) {
			workflowNotes = "";
		}
		init(workflowID.trim(), workflowNotes.trim());
	}

	public ImportableID(ASMRepositoryItemBean workflow) {
		String workflowID = workflow.getItemID();
		String workflowNotes = workflow.getExportText();
		if (workflowID == null || workflowNotes == null) {
			LOGGER.warn("Missing workflow-ID or notes! Both may be needed in order to identify a workflow correctly.");
		}
		if (workflowNotes == null) {
			workflowNotes = "";
		}
		init(workflowID.trim(), workflowNotes.trim());
	}

	private void init(String workflowID, String notes) {
		if (notes == null)
			notes = "";

		notes = escapeNotes(notes);

		this.realID = workflowID + ":" + notes;
	}

	private String escapeNotes(String notes) {
		notes = notes.replace("<", "");
		notes = notes.replace(">", "");
		notes = notes.replace("/", "");
		return notes;
	}

	@Override
	public int hashCode() {
		return realID.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() == this.getClass()) {
			ImportableID other = (ImportableID) obj;
			if (realID.equals(other.realID))
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return realID.toString();
	}
}
