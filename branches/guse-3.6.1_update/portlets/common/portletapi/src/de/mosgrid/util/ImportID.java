package de.mosgrid.util;

import hu.sztaki.lpds.pgportal.services.asm.beans.ASMRepositoryItemBean;
import de.mosgrid.msml.editors.MSMLTemplate;

public class ImportID {

	private final String _realID;
	
	private ImportID(String workflowID, String notes) {
		if (notes == null)
			notes = "";

		notes = escapeNotes(notes);

		_realID = workflowID + ":" + notes;
	}
	
	public ImportID(MSMLTemplate template) {
		this(template.getJobListElement().getId(), template.getJobListElement().getWorkflowNotes());
	}

	public ImportID(ASMRepositoryItemBean workflow) {
		this(workflow.getItemID(), workflow.getExportText());
	}

	public static String escapeNotes(String notes) {
		notes = notes.replace("<", "");
		notes = notes.replace(">", "");
		notes = notes.replace("/", "");
		return notes;
	}

	@Override
	public int hashCode() {
		return _realID.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() == this.getClass()) {
			ImportID other = (ImportID) obj;
			if (_realID.equals(other._realID))
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return _realID.toString();
	}
}
