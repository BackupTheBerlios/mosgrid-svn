package de.mosgrid.md.specials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.gui.inputmask.AbstractInputMask;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.util.wrapper.Job;

/**
 * Special task which sets the water model file of genbox corresponding to the selected water model of pdb2gmx
 * 
 * @author Andreas Zink
 * 
 */
public class WaterModelTask implements IAfterValidationTask {
	private final Logger LOGGER = LoggerFactory.getLogger(WaterModelTask.class);

	public static final String ID_PDB2GMX_WATER = "pdb2gmx.Watermodel";
	public static final String ID_GENBOX_WATER = "genbox.Watermodel";

	private ParameterType pdb2gmxWaterModelProperty;
	private boolean taskDone;

	public WaterModelTask(ParameterType property) {
		this.pdb2gmxWaterModelProperty = property;
	}

	@Override
	public void execute(AbstractInputMask inputMask) {
		for (Job job : inputMask.getTemplate().getJobListElement().getJobs()) {
			if (job.getInitialization() != null && job.getInitialization().getParamList() != null) {
				for (ParameterType parameter : job.getInitialization().getParamList().getParameter()) {
					if (parameter.getDictRef().contains(ID_GENBOX_WATER)) {
						String selWaterModel = pdb2gmxWaterModelProperty.getScalar().getValue().trim();
						String newValue = getCorrectWaterFileName(selWaterModel);
						if (newValue != null) {
							LOGGER.trace(inputMask.getPortlet().getUser() + " Adapting water model [" + selWaterModel
									+ " (pdb2gmx) -> " + newValue + " (genbox)]");
							parameter.getScalar().setValue(newValue);
							taskDone = true;
						} else {
							LOGGER.warn(inputMask.getPortlet().getUser()
									+ "Could not find any water model file (genbox) for selected water model (pdb2gmx)!");
						}
						break;
					}
				}
			}
			if (taskDone) {
				break;
			}
		}

	}

	/**
	 * Returns the correct water model file for given water model
	 */
	private String getCorrectWaterFileName(String waterModel) {
		if (waterModel.equals("spc") || waterModel.equals("spce")) {
			return "spc216.gro";
		} else if (waterModel.equals("tip3p") || waterModel.equals("f3c")) {
			return "spc.gro";
		} else if (waterModel.equals("tip4p")) {
			return "tip4p.gro";
		} else if (waterModel.equals("tip5p")) {
			return "tip5p.gro";
		}

		return null;
	}

}
