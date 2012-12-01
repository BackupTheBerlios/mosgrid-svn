package de.mosgrid.md.specials;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Field;

import de.mosgrid.exceptions.InputFieldCreationException;
import de.mosgrid.exceptions.InputMaskCreationException;
import de.mosgrid.gui.inputmask.AbstractInputMask;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.jaxb.bindings.PropertyType;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.util.DefaultInputMaskFactory;

/**
 * Custom input mask factory for some special MD cases
 * 
 * @author Andreas Zink
 * 
 */
public class MDInputMaskFactory extends DefaultInputMaskFactory {
	private final Logger LOGGER = LoggerFactory.getLogger(MDInputMaskFactory.class);

	/* dict refs to fix maxh vs. walltime */
	public static final String DICT_REF_MAXH = "mdrun.MaxH";
	public static final String DICT_REF_WALLTIME = "walltime";
	private Map<String, Field> job2field;

	private MDInputMask currentInputMask;

	public MDInputMaskFactory(DomainPortlet portlet) {
		super(portlet);
	}

	@Override
	protected AbstractInputMask createEmptyInputMask(ImportedWorkflow wkfImport) throws InputMaskCreationException {
		this.job2field = new HashMap<String, Field>();
		this.currentInputMask = new MDInputMask(portlet, wkfImport);
		return currentInputMask;
	}

	@Override
	protected Field createInitInputField(ImportedWorkflow wkfImport, Job job, ParameterType parameter)
			throws InputFieldCreationException {
		Field field = super.createInitInputField(wkfImport, job, parameter);

		// check for maxh param
		if (parameter.getDictRef().contains(DICT_REF_MAXH)) {
			job2field.put(job.getId(), field);
		}

		// check for water model
		if (parameter.getDictRef().contains(WaterModelTask.ID_PDB2GMX_WATER)) {
			LOGGER.trace(portlet.getUser()+ " Found pdb2gmx.WaterModel parameter - adding special task");
			this.currentInputMask.addTask(new WaterModelTask(parameter));
		}
		return field;
	}

	@Override
	protected Field createEnvironInputField(ImportedWorkflow wkfImport, Job job, PropertyType property)
			throws InputFieldCreationException {
		Field field = super.createEnvironInputField(wkfImport, job, property);
		if (property.getDictRef().contains(DICT_REF_WALLTIME)) {
			if (job2field.containsKey(job.getId())) {
				LOGGER.trace(portlet.getUser()+ " Found maxh and walltime parameter - adding special walltime validator");
				field.addValidator(new WalltimeValidator(job2field.get(job.getId())));
			}
		}
		return field;
	}
}
