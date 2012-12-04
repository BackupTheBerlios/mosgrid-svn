package de.mosgrid.chemdoodle.ui;

import java.awt.Color;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;

import de.mosgrid.chemdoodle.widget.ICanvasListener;
import de.mosgrid.chemdoodle.widget.MoleculeCanvas;
import de.mosgrid.chemdoodle.widget.RepMode;

/**
 * Minimal molecule viewer which provides the most important options for a molecule canvas.
 * 
 * @author Andreas Zink
 * 
 */
public class BasicViewer extends CustomComponent implements ICanvasListener {
	private static final long serialVersionUID = -1584311646691337967L;
	/* constants */
	private static final int WIDTH_SETTINGS_LAYOUT = 230;
	private static final String CAPTION_ATOMS = "Atoms";
	private static final String CAPTION_COLORING = "Coloring";

	private HorizontalLayout mainLayout;
	private VerticalLayout settingLayout;
	private MoleculeCanvas canvas;

	public BasicViewer() {
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
		canvas = new MoleculeCanvas();
		canvas.addCanvasListener(this);
		mainLayout.addComponent(canvas);
		mainLayout.setExpandRatio(canvas, 0f);
	}

	private void buildSettingsLayout() {
		settingLayout = new VerticalLayout();
		settingLayout.setWidth(WIDTH_SETTINGS_LAYOUT, UNITS_PIXELS);
		settingLayout.setHeight("-1px");
		settingLayout.setSpacing(true);

		settingLayout.addComponent(buildAtomForm());
		settingLayout.addComponent(buildColorForm());
		mainLayout.addComponent(settingLayout);
	}

	private OptionPanel buildAtomForm() {
		// set properties to show
		String[] propIDs = new String[] { "representationMode", "showWater","hideRibbons", "showNonPolymers" };
		String[] desc = new String[] { "Change representation mode of atoms and bonds","Show water molecules",
				"Hide macromolecue ribbons and show atoms instead", "Show all non-macromolecule atoms" };
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
