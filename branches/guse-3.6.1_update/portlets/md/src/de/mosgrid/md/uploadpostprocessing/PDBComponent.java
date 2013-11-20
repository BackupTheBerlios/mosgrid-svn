package de.mosgrid.md.uploadpostprocessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.ChainImpl;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.Structure;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Field;
import com.vaadin.ui.Select;
import com.vaadin.ui.TwinColSelect;

import de.mosgrid.exceptions.PostprocessorValidationException;
import de.mosgrid.gui.inputmask.uploads.AbstractPostprocessorComponent;
import de.mosgrid.util.DefaultFieldFactory;

/**
 * This is a ui component for PDB postprocessing. Enables the selection if different models, chains, hetatms etc
 * 
 * @author Andreas Zink
 * 
 */
public class PDBComponent extends AbstractPostprocessorComponent {
	private static final long serialVersionUID = -8523547748435775077L;

	/* constants */
	public static final String CAPTION_MODEL_SELECT = "Structure Model";
	public static final String TOOLTIP_MODEL_SELECT = "Please select a model. Some pdb files may have more than one structure model.";
	public static final String CAPTION_GROUP_SELECT_LEFT = "Available Groups";
	public static final String CAPTION_GROUP_SELECT_RIGHT = "Selected Groups";
	public static final String TOOLTIP_GROUP_SELECT = "Please select all groups which shall be used in the simulation.";

	/* instance variables */
	protected PDBGroup[] allowedGroups;
	protected Set<SelectableGroupItem> selectedGroups = new HashSet<SelectableGroupItem>();
	protected List<Model> availableModels = new ArrayList<Model>();

	/* ui components */
	protected Select modelSelect;
	protected TwinColSelect groupSelect;

	public PDBComponent(Structure pdbStructure, PDBGroup[] allowedGroups) {
		this.allowedGroups = allowedGroups;
		for (int i = 0; i < pdbStructure.nrModels(); i++) {
			Model model = new Model(i, pdbStructure.getModel(i));
			availableModels.add(model);
		}
		buildContent();
	}

	private void buildContent() {
		createModelSelect();
		createGroupSelect();
	}

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
				if (groupSelect.getComponentError() != null) {
					groupSelect.setComponentError(null);
				}
				updateSelectableGroups();
			}
		});

		addComponent(modelSelect);
	}

	protected void createGroupSelect() {
		groupSelect = new TwinColSelect();
		groupSelect.setNullSelectionAllowed(true);
		groupSelect.setMultiSelect(true);
		groupSelect.setImmediate(true);
		groupSelect.setDescription(TOOLTIP_GROUP_SELECT);
		groupSelect.setLeftColumnCaption(CAPTION_GROUP_SELECT_LEFT);
		groupSelect.setRightColumnCaption(CAPTION_GROUP_SELECT_RIGHT);
		groupSelect.setWidth("100%");
		groupSelect.addListener(new ValueChangeListener() {
			private static final long serialVersionUID = -8638874898429728235L;

			@SuppressWarnings("unchecked")
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (groupSelect.getComponentError() != null) {
					groupSelect.setComponentError(null);
				}
				groupSelect.setComponentError(null);
				selectedGroups = (Set<SelectableGroupItem>) groupSelect.getValue();
			}
		});

		addComponent(groupSelect);
	}

	private void updateSelectableGroups() {
		// clear group select
		groupSelect.removeAllItems();
		if (modelSelect.getValue() != null) {
			// get current model and fill group select
			Model selectedModel = (Model) modelSelect.getValue();
			List<Chain> chains = selectedModel.getChains();
			for (Chain chain : chains) {
				String chainPrefix = "Chain " + chain.getChainID() + " - ";
				List<Group> aminos = chain.getAtomGroups("amino");
				if (aminos.size() > 0) {
					String name = chainPrefix + PDBGroup.AA;
					SelectableGroupItem selectItem = new SelectableGroupItem(name, aminos, PDBGroup.AA);
					groupSelect.addItem(selectItem);
				}
				List<Group> nucs = chain.getAtomGroups("nucleotide");
				if (nucs.size() > 0) {
					String name = chainPrefix + PDBGroup.NUC;
					SelectableGroupItem selectItem = new SelectableGroupItem(name, nucs, PDBGroup.NUC);
					groupSelect.addItem(selectItem);
				}
				List<Group> hetatms = chain.getAtomGroups("hetatm");
				if (hetatms.size() > 0) {
					List<Group> waterGroups = new ArrayList<Group>();
					for (Group hetatm : hetatms) {
						if (hetatm.getPDBName().equals("HOH")) {
							waterGroups.add(hetatm);
						} else {
							String name = chainPrefix + hetatm.getPDBName();
							SelectableGroupItem selectItem = new SelectableGroupItem(name, hetatm, PDBGroup.HETATM);
							groupSelect.addItem(selectItem);
						}
					}
					if (waterGroups.size() > 0) {
						String name = chainPrefix + PDBGroup.WATER;
						SelectableGroupItem selectItem = new SelectableGroupItem(name, waterGroups, PDBGroup.WATER);
						groupSelect.addItem(selectItem);
					}
				}
			}
		}
	}

	@Override
	public boolean isValid() throws PostprocessorValidationException {
		boolean isValid = true;
		if (!selectedGroups.isEmpty()) {
			Iterator<SelectableGroupItem> iterator = selectedGroups.iterator();
			while (iterator.hasNext()) {
				SelectableGroupItem groupItem = iterator.next();
				// check if selection is allowed e.g. selecting hetatms may be forbidden
				if (!isSelectedGroupItemAllowed(groupItem)) {
					isValid = false;
					String msg = "'" + groupItem + "' is not allowed to be selected for this workflow.";
					groupSelect.setComponentError(new UserError(msg));
					continue;
				}
				/*
				 * Check for atoms with alternate locations. Unfortunately BioJava does not parse ATOM lines with
				 * alternate locations but recognizes them.
				 */
				for (Group group : groupItem.getGroups()) {
					if (group.hasAltLoc()) {
						isValid = false;
						String msg = "'"
								+ groupItem
								+ "' contains atoms with alternate locations. Please remove them manually from your pdb file.";
						groupSelect.setComponentError(new UserError(msg));
						continue;
					}
				}
			}
		} else {
			isValid = false;
			groupSelect.setComponentError(new UserError("At least one group must be selected."));
		}
		return isValid;
	}

	private boolean isSelectedGroupItemAllowed(SelectableGroupItem groupItem) {
		for (PDBGroup group : allowedGroups) {
			if (group == groupItem.getPdbGroup()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return A collection of chains which contains all selected groups
	 */
	public Collection<Chain> getSelectedChains() {
		Map<String, Chain> chainId2chain = new HashMap<String, Chain>();
		Iterator<SelectableGroupItem> iterator = selectedGroups.iterator();
		Chain currentChain = null;

		while (iterator.hasNext()) {
			SelectableGroupItem groupItem = iterator.next();
			String chainID = groupItem.getChainId();

			if (currentChain == null || !currentChain.getChainID().equals(chainID)) {
				if (chainId2chain.containsKey(chainID)) {
					// if we already have seen this chain get it from map
					currentChain = chainId2chain.get(chainID);
				} else {
					// create new chain
					currentChain = new ChainImpl();
					chainId2chain.put(chainID, currentChain);
				}
			}
			for (Group group : groupItem.getGroups()) {
				// add all residues to chain
				currentChain.addGroup(group);
			}

		}
		return chainId2chain.values();
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

	}

	/**
	 * Helper class which represents a selectable group item
	 * 
	 */
	private class SelectableGroupItem {
		private PDBGroup pdbGroup;
		private List<Group> groups;
		private String name;

		public SelectableGroupItem(String name, List<Group> groups, PDBGroup pdbGroup) {
			this.name = name;
			this.groups = groups;
			this.pdbGroup = pdbGroup;
		}

		public SelectableGroupItem(String name, Group group, PDBGroup pdbGroup) {
			this.name = name;
			this.groups = new ArrayList<Group>();
			groups.add(group);
			this.pdbGroup = pdbGroup;
		}

		public List<Group> getGroups() {
			return groups;
		}

		public PDBGroup getPdbGroup() {
			return pdbGroup;
		}

		public String getChainId() {
			return groups.get(0).getChainId();
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public int hashCode() {
			return name.hashCode() + groups.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (obj instanceof SelectableGroupItem) {
				if (hashCode() == obj.hashCode()) {
					return true;
				}
			}
			return false;
		}

	}
}
