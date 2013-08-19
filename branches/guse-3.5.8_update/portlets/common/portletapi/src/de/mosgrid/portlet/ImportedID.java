package de.mosgrid.portlet;

import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;
import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.util.WorkflowHelper;

/**
 * Helper class which links MSML templates to ASM workflow instances. The class can be used as key in Maps. The ID
 * corresponds to the name the instance was imported i.e. the ID or DisplayName of the Template.
 * 
 * @author Andreas Zink
 * 
 */
public class ImportedID {
	private String realID;

	public ImportedID(ASMWorkflow wkfInstance) {
		this.realID = WorkflowHelper.getInstance().getRepName(wkfInstance);
	}

	public ImportedID(MSMLTemplate template) {
		this.realID = template.getName();
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
		if (obj instanceof ImportedID) {
			ImportedID other = (ImportedID) obj;
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
