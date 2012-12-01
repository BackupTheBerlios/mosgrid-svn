package de.mosgrid.chemdoodle.ui;

import java.awt.Color;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Select;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Slider.ValueOutOfBoundsException;
import com.vaadin.ui.VerticalLayout;

import de.mosgrid.chemdoodle.widget.ICanvasListener;
import de.mosgrid.chemdoodle.widget.MoleculeCanvas;
import de.mosgrid.chemdoodle.widget.RepMode;

/**
 * MoleculeViewer wraps a MoleculeCanvas and provides fields for setting properties of the canvas.
 * 
 * @author Andreas Zink
 * 
 */
public class PdbViewer extends CustomComponent implements ICanvasListener {
	/*constants*/
	private static final long serialVersionUID = 7492546239733286913L;
	private static final int WIDTH_SETTINGS_LAYOUT = 230;
	private static final String CAPTION_PROTEIN = "Protein";
	private static final String CAPTION_ATOMS = "Atoms";
	private static final String CAPTION_COLORING = "Coloring";
	private static final String CAPTION_SIZE = "Size";
	private static final int CANVAS_MIN = 320;
	private static final int CANVAS_MAX = 800;
	private static final int CANVAS_START_SIZE = 500;
	

	private HorizontalLayout mainLayout;
	private VerticalLayout settingLayout;
	private MoleculeCanvas canvas;

	public PdbViewer() {
		init();
	}

	private void init() {
		setImmediate(true);
		buildMainLayout();
		buildCanvas();
		buildSettingsLayout();

		setCompositionRoot(mainLayout);
	}

	private void buildMainLayout() {
		mainLayout = new HorizontalLayout();
		mainLayout.setWidth("100%");
		mainLayout.setHeight("-1px");
		mainLayout.setSpacing(true);

		setWidth("100%");
		setHeight("-1px");
	}

	private void buildCanvas() {
		// create canvas
		canvas = new MoleculeCanvas(CANVAS_START_SIZE);
		canvas.addCanvasListener(this);
		mainLayout.addComponent(canvas);
		mainLayout.setExpandRatio(canvas, 0f);
	}

	private void buildSettingsLayout() {
		settingLayout = new VerticalLayout();
		settingLayout.setWidth(WIDTH_SETTINGS_LAYOUT,UNITS_PIXELS);
		settingLayout.setHeight("-1px");
		settingLayout.setSpacing(true);

		settingLayout.addComponent(buildProteinForm());
		settingLayout.addComponent(buildAtomForm());
		settingLayout.addComponent(buildColorForm());
		settingLayout.addComponent(buildResizePanel());
		mainLayout.addComponent(settingLayout);
	}

	private OptionPanel buildProteinForm() {
		// set properties to show
		String[] propIDs = new String[] { "useCartoonStyle", "showBackbone", "hideRibbons" };
		String[] desc = new String[] { "Cartoonize protein", "Draw the protein backbone",
				"Hide secondary structures and show atoms of residues (if any)" };
		OptionPanel panel = new OptionPanel(canvas, propIDs, desc);
		panel.setCaption(CAPTION_PROTEIN);
		return panel;
	}

	private OptionPanel buildAtomForm() {
		// set properties to show
		String[] propIDs = new String[] { "representationMode", "useStars", "showWater", "showNonPolymers" };
		String[] desc = new String[] { "Change representation mode of atoms and bonds",
				"Represent non-bondend atoms as stars", "Show water molecules", "Show all non-polymer atoms" };
		OptionPanel panel = new OptionPanel(canvas, propIDs, desc);
		panel.setCaption(CAPTION_ATOMS);

		Form form = panel.getForm();
		Select modeSelect = form.replaceWithSelect("representationMode", RepMode.values(), RepMode.values());
		modeSelect.setImmediate(true);
		modeSelect.setNullSelectionAllowed(false);
		modeSelect.setDescription(desc[0]);
		modeSelect.setWidth("100%");

		return panel;
	}

	private OptionPanel buildColorForm() {
		// set properties to show
		String[] propIDs = new String[] { "background", "colorByChain", "colorByResidue" };
		String[] desc = new String[] { "Change background color of canvas", "Color ribbons by chain",
				"Color ribbons by residue" };
		OptionPanel panel = new OptionPanel(canvas, propIDs, desc);
		panel.setCaption(CAPTION_COLORING);

		Form form = panel.getForm();
		Select colorSelect = form.replaceWithSelect("background", new Object[] { Color.BLACK, Color.WHITE,
				Color.DARK_GRAY }, new Object[] { "Black", "White", "Gray" });
		colorSelect.setImmediate(true);
		colorSelect.setNullSelectionAllowed(false);
		colorSelect.setDescription(desc[0]);
		colorSelect.setWidth("100%");
		return panel;
	}

	private Panel buildResizePanel() {
		HorizontalLayout panelLayout = new HorizontalLayout();
		panelLayout.setWidth("100%");
		panelLayout.setMargin(true);
		panelLayout.setSpacing(true);

		final Label sizeLabel = new Label("(" + CANVAS_START_SIZE + "x" + CANVAS_START_SIZE + ")");
		sizeLabel.setWidth("100%");
		final Slider slider = new Slider("Canvas Size", CANVAS_MIN, CANVAS_MAX);
		slider.setWidth("100%");
		slider.setDescription("Set canvas edge length in pixels");
		try {
			slider.setValue(CANVAS_START_SIZE);
		} catch (ValueOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		slider.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Object value = event.getProperty().getValue();
				if (value instanceof Double) {
					Double doubleValue = (Double) value;
					canvas.setCanvasEdgeLength(doubleValue.floatValue());
					Integer intValue = doubleValue.intValue();
					sizeLabel.setValue("(" + intValue + "x" + intValue + ")");
				}

			}
		});
		slider.setImmediate(true);
		panelLayout.addComponent(slider);
		panelLayout.addComponent(sizeLabel);
		panelLayout.setExpandRatio(slider, 0.7f);
		panelLayout.setExpandRatio(sizeLabel, 0.3f);
		panelLayout.setComponentAlignment(sizeLabel, Alignment.BOTTOM_LEFT);

		Panel sizePanel = new Panel(panelLayout);
		sizePanel.setCaption(CAPTION_SIZE);
		
		return sizePanel;
	}

	public MoleculeCanvas getCanvas() {
		return canvas;
	}

	@Override
	public void noWebGlSupportDetected() {
		settingLayout.setEnabled(false);
	}

	private class OptionPanel extends CustomComponent {
		private static final long serialVersionUID = -1317243592793801265L;
		private Panel panel;
		private Form form;

		public OptionPanel(MoleculeCanvas canvas, String[] propIDs, String[] desc) {
			super();
			buildContent(canvas, propIDs, desc);
			setCompositionRoot(panel);
		}

		private void buildContent(MoleculeCanvas canvas, String[] propIDs, String[] desc) {
			// wrap canvas in bean item
			BeanItem<MoleculeCanvas> canvasItem = new BeanItem<MoleculeCanvas>(canvas, propIDs);

			// build form
			VerticalLayout formLayout = new VerticalLayout();
			form = new Form(formLayout);
			form.setImmediate(true);
			form.setItemDataSource(canvasItem);
			setDescriptions(propIDs, desc);
			panel = new Panel();
			panel.addComponent(form);
		}

		private void setDescriptions(String[] propIDs, String[] desc) {
			for (int i = 0; i < propIDs.length; i++) {
				Field field = this.form.getField(propIDs[i]);
				if (field != null) {
					field.setDescription(desc[i]);
				}
			}
		}

		public void setCaption(String caption) {
			panel.setCaption(caption);
		}

		public Form getForm() {
			return this.form;
		}

	}

}
