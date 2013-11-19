package de.mosgrid.portlet;

import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;
import de.mosgrid.gui.inputmask.AbstractInputMask;
import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.util.WorkflowHelper;

/**
 * This class represents an workflow import. It clusters the ASM workflow instance, workflow template
 * 
 * @author Andreas Zink
 * 
 */
public class ImportedWorkflow {
	private static int importCounter = 0;
	private DomainId domainId;
	private ASMWorkflow asmInstance;
	private MSMLTemplate template;
	private AbstractInputMask inputMask;
	private int importID;

	/**
	 * Creates a new imported workflow instance. Note, that the input mask has to be set manually.
	 * 
	 * @param domainId
	 *            The domain under which import was created
	 * @param asmInstance
	 *            The imported workflow instance
	 * @param template
	 *            The used template
	 */
	public ImportedWorkflow(DomainId domainId, ASMWorkflow asmInstance, MSMLTemplate template) {
		this.domainId = domainId;
		this.asmInstance = asmInstance;
		this.template = template;
		init();
	}

	private void init() {
		// Reset import counter. This can be done safely as old instances are surely deleted by garbage collector.
		synchronized(ImportedWorkflow.class) {
			if (importCounter == Integer.MAX_VALUE) {
				importCounter = 0;
			}
			this.importID = importCounter++;
		}
	}

	public ASMWorkflow getAsmInstance() {
		return asmInstance;
	}

	public MSMLTemplate getTemplate() {
		return template;
	}

	public DomainId getDomainId() {
		return domainId;
	}

	public AbstractInputMask getInputMask() {
		return inputMask;
	}

	public void setInputMask(AbstractInputMask inputMask) {
		this.inputMask = inputMask;
	}

	/**
	 * @return The user given import name
	 */
	public String getUserImportName() {
		return WorkflowHelper.getInstance().getUserChosenName(asmInstance);
	}
	
	@Override
	public int hashCode() {
		return importID;
	}

	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj != null && obj instanceof ImportedWorkflow) {
			ImportedWorkflow other = (ImportedWorkflow) obj;

			isEqual = (this.hashCode() == other.hashCode());
		}
		return isEqual;
	}

	@Override
	public String toString() {
		return "WorkflowImport-" + importID;
	}

}
