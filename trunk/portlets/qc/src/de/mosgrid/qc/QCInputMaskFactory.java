package de.mosgrid.qc;

import de.mosgrid.exceptions.InputMaskCreationException;
import de.mosgrid.gui.inputmask.AbstractInputMask;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.util.helper.XmlHelper;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.qc.gui.inputmask.QCStructureInputForm;
import de.mosgrid.util.DefaultInputMaskFactory;

public class QCInputMaskFactory extends DefaultInputMaskFactory {

	public QCInputMaskFactory(DomainPortlet portlet) {
		super(portlet);
	}

	@Override
	public AbstractInputMask createInputMask(ImportedWorkflow imp) throws InputMaskCreationException {
		AbstractInputMask mask = super.createInputMask(imp);
		boolean addNoMolecule = imp == null ||
				imp.getTemplate() == null ||
				imp.getTemplate().getJobListElement() == null ||
				imp.getTemplate().getJobListElement().getJobs() == null ||
				imp.getTemplate().getJobListElement().getJobs().size() == 0;
		if (addNoMolecule)
			return mask;
		
		// We have at least one job. Now search for a job that contains the "nomolecule"-parameter.
		// If we find one that no moleculefield will be generated.
		for (Job job : imp.getTemplate().getJobListElement().getJobs()) {
			if (!job.hasParameterElements())
				continue;
			for (ParameterType param : job.getInitialization().getParamList().getParameter()) {
				if ("nomolecule".equals(XmlHelper.getInstance().getSuffix(param.getDictRef())) &&
						param.getScalar() != null &&
						"true".equals(param.getScalar().getValue())) {
					return mask;
				}
			}
		}
		
		// no job has the nomolecule-parameter set => add moleculefield
		mask.getComponentContainer().addComponent(QCStructureInputForm.createInstance());
		return mask;
	}
}
