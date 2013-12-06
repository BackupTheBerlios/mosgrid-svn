package de.mosgrid.md.specials;

import java.util.ArrayList;
import java.util.List;

import de.mosgrid.gui.inputmask.DefaultInputMask;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.portlet.ImportedWorkflow;

/**
 * Custom input mask for MD which allows to add 'after validation tasks'
 * 
 * @author Andreas Zink
 * 
 */
public class MDInputMask extends DefaultInputMask {
	private static final long serialVersionUID = 3898176413756534667L;
	// private final Logger LOGGER = LoggerFactory.getLogger(CustomWaterModelInputMask.class);

	private List<IAfterValidationTask> taskList;

	public MDInputMask(DomainPortlet portlet, ImportedWorkflow wkfImport) {
		super(portlet, wkfImport);
		taskList = new ArrayList<IAfterValidationTask>();
	}

	public void addTask(IAfterValidationTask task) {
		taskList.add(task);
	}

	@Override
	protected void afterCommitAndValidateHook() {
		for (IAfterValidationTask task : taskList) {
			task.execute(this);
		}
		super.afterCommitAndValidateHook();
	}
}
