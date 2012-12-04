package de.mosgrid.remd.util;

import de.mosgrid.exceptions.InputMaskCreationException;
import de.mosgrid.gui.inputmask.AbstractInputMask;
import de.mosgrid.gui.inputmask.AbstractJobForm;
import de.mosgrid.gui.inputmask.uploads.DefaultUploadSelectionComponent;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.remd.properties.RemdProperties;
import de.mosgrid.remd.ui.tempdist.TemperatureDistributionCalculator;
import de.mosgrid.remd.ui.upload.AppendAutoSelect;
import de.mosgrid.remd.ui.upload.DemuxAutoSelect;
import de.mosgrid.util.DefaultInputMaskFactory;

/**
 * Custom input mask factory for REMD
 * 
 * @author Andreas Zink
 * 
 */
public class CustomInputmaskFactory extends DefaultInputMaskFactory {

	public CustomInputmaskFactory(DomainPortlet portlet) {
		super(portlet, new CustomFieldFactory());
	}

	@Override
	protected AbstractJobForm createJobForm(ImportedWorkflow wkfImport, AbstractInputMask inputMask, Job job)
			throws InputMaskCreationException {
		if (job.getId().equals(RemdProperties.get(RemdProperties.ID_REMD_TEMP_DIST))) {

			TemperatureDistributionCalculator tempDistCalculator = new TemperatureDistributionCalculator(portlet,
					wkfImport, job);

			DefaultUploadSelectionComponent lastProteinUpload = getFieldFactory().getLastProteinUpload();
			lastProteinUpload.addSelectionListener(tempDistCalculator);
			lastProteinUpload.addUploadListener(tempDistCalculator);

			tempDistCalculator.addComponentToRightColumn(createJobDescription(wkfImport, job));

			return tempDistCalculator;
		} else if (job.getId().equals(RemdProperties.get(RemdProperties.ID_REMD_APPEND_INPUT))) {
			AppendAutoSelect appendComponent = new AppendAutoSelect(portlet, wkfImport, job);
			appendComponent.addComponentToRightColumn(createJobDescription(wkfImport, job));

			return appendComponent;
		} else if (job.getId().equals(RemdProperties.get(RemdProperties.ID_REMD_DEMUX_INPUT))) {
			DemuxAutoSelect demuxComponent = new DemuxAutoSelect(portlet, wkfImport, job);
			demuxComponent.addComponentToRightColumn(createJobDescription(wkfImport, job));

			return demuxComponent;
		} else {
			return super.createJobForm(wkfImport, inputMask, job);
		}
	}

	@Override
	public CustomFieldFactory getFieldFactory() {
		return (CustomFieldFactory) super.getFieldFactory();
	}

}
