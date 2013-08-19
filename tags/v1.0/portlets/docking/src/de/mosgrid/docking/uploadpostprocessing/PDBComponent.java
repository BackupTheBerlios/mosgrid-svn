package de.mosgrid.docking.uploadpostprocessing;

import java.util.ArrayList;
import java.util.List;

import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.Structure;
import org.w3c.dom.Element;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Field;
import com.vaadin.ui.Select;

import de.mosgrid.exceptions.PostprocessorValidationException;
import de.mosgrid.gui.inputmask.uploads.AbstractPostprocessorComponent;
import de.mosgrid.msml.jaxb.bindings.EntryType;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.util.DefaultFieldFactory;

public class PDBComponent extends AbstractPostprocessorComponent {
	/* constants */
	private static final long serialVersionUID = -1228872861090336026L;
	public static final String CAPTION_MODEL_SELECT = "Structure Model";
	public static final String TOOLTIP_MODEL_SELECT = "Please select a model. Some pdb files may have more than one structure model.";

	protected List<Model> availableModels = new ArrayList<Model>();

	/* ui components */
	protected Select modelSelect;
	protected Select chainSelect;
	protected Select ligandSelect;

	public PDBComponent(Structure pdbStructure, ParameterType chainParameterElement, EntryType chainDictEntry,
			ParameterType ligandParameterElement, EntryType ligandDictEntry) {
		// parse pdb structure models
		for (int i = 0; i < pdbStructure.nrModels(); i++) {
			Model model = new Model(i, pdbStructure.getModel(i));
			availableModels.add(model);
		}

		createModelSelect();
		chainSelect = createSelect(chainParameterElement, chainDictEntry);
		chainSelect.addListener(new ValueChangeListener() {
			private static final long serialVersionUID = 6993434816187470412L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				updateSelectableLigands();
			}
		});
		ligandSelect = createSelect(ligandParameterElement, ligandDictEntry);
	}

	/**
	 * Creates a selection field for pdb structure models
	 */
	protected void createModelSelect() {
		modelSelect = new Select(CAPTION_MODEL_SELECT);
		modelSelect.setDescription(TOOLTIP_MODEL_SELECT);
		modelSelect.setImmediate(true);
		modelSelect.setRequired(true);
		// create items
		BeanItemContainer<Model> selectContainer = new BeanItemContainer<Model>(Model.class, availableModels);
		modelSelect.setContainerDataSource(selectContainer);
		modelSelect.setWidth(DefaultFieldFactory.DEFAULT_SELECT_WIDTH, Field.UNITS_PIXELS);
		modelSelect.addListener(new ValueChangeListener() {
			private static final long serialVersionUID = -605591105854514891L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (modelSelect.getComponentError() != null) {
					modelSelect.setComponentError(null);
				}
				if (chainSelect.getComponentError() != null) {
					chainSelect.setComponentError(null);
				}
				if (ligandSelect.getComponentError() != null) {
					ligandSelect.setComponentError(null);
				}
				updateSelectableChains();
			}
		});

		addComponent(modelSelect);
	}

	/**
	 * Updates the lists of selectable chains after selected model has changed
	 */
	private void updateSelectableChains() {
		// clear group select
		chainSelect.removeAllItems();
		ligandSelect.removeAllItems();
		if (modelSelect.getValue() != null) {
			// get current model and fill group select
			Model selectedModel = (Model) modelSelect.getValue();
			List<Chain> chains = selectedModel.getChains();
			for (Chain chain : chains) {
				// add chain to selection list
				chainSelect.addItem(chain.getChainID());
			}
		}
	}

	/**
	 * Updates the lists of selectable ligands after selected chain has changed
	 */
	private void updateSelectableLigands() {
		// clear group select
		ligandSelect.removeAllItems();
		if (modelSelect.getValue() != null && chainSelect.getValue() != null) {
			// get current model and fill group select
			Model selectedModel = (Model) modelSelect.getValue();
			String chainID = chainSelect.getValue().toString();
			Chain selectedChain = selectedModel.getChain(chainID);
			for (Group ligand : selectedChain.getAtomGroups("hetatm")) {
				ligandSelect.addItem(ligand.getPDBName());
			}
		}
	}

	/**
	 * Helper method for creating a select field
	 */
	private Select createSelect(final ParameterType parameterElement, EntryType dictEntry) {
		final Select selectField = new Select();
		selectField.setImmediate(true);
		selectField.setRequired(true);
		// selectField.setNullSelectionAllowed(false);
		selectField.setWidth(DefaultFieldFactory.DEFAULT_SELECT_WIDTH, Field.UNITS_PIXELS);

		// set caption and tool-tip
		selectField.setCaption(dictEntry.getTitle());
		selectField.setDescription(resolveDescription(dictEntry));

		// add a listener which sets the selected value in the template
		selectField.addListener(new ValueChangeListener() {
			private static final long serialVersionUID = 6993434816187470412L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Object selectedValue = selectField.getValue();
				if (selectedValue != null) {
					parameterElement.getScalar().setValue(selectedValue.toString());
				}
			}
		});

		addComponent(selectField);
		return selectField;
	}

	/**
	 * @return The description for a parameter from its dictionary entry
	 */
	private String resolveDescription(EntryType dictEntry) {
		StringBuilder descriptionBuilder = new StringBuilder();
		for (Element child : dictEntry.getDescription().getAny()) {
			descriptionBuilder.append(child.getTextContent().trim());
		}
		return descriptionBuilder.toString();
	}

	@Override
	public boolean isValid() throws PostprocessorValidationException {
		boolean isValid = true;

		if (modelSelect.getValue() == null) {
			isValid = false;
			modelSelect.setComponentError(new UserError("Please select a pdb structure model."));
		}
		if (chainSelect.getValue() == null) {
			isValid = false;
			chainSelect.setComponentError(new UserError("Please select a chain."));
		}
		if (ligandSelect.getValue() == null) {
			isValid = false;
			ligandSelect.setComponentError(new UserError("Please select a ligand."));
		}
		return isValid;
	}

	/**
	 * Helper class for pdb models
	 * 
	 */
	protected class Model {
		private final int id;
		private final String name;
		private final List<Chain> chains;

		private Model(int id, List<Chain> chains) {
			this.id = id;
			this.name = "Model " + id;
			this.chains = chains;
		}

		@Override
		public int hashCode() {
			return id;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (obj instanceof Model) {
				Model other = (Model) obj;
				if (hashCode() == other.hashCode()) {
					return true;
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return name;
		}

		public List<Chain> getChains() {
			return chains;
		}

		public Chain getChain(String chainID) {
			for (Chain c : chains) {
				if (c.getChainID().equals(chainID)) {
					return c;
				}
			}
			return null;
		}
	}

}
