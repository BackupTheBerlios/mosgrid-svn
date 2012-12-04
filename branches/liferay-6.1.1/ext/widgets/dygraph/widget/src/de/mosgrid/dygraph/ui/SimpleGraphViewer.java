package de.mosgrid.dygraph.ui;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Slider.ValueOutOfBoundsException;
import com.vaadin.ui.VerticalLayout;

import de.mosgrid.dygraph.widget.SimpleGraph;

public class SimpleGraphViewer extends CustomComponent {
	/* constants */
	private static final long serialVersionUID = -2809565038685144206L;
	private static final int WIDTH_SETTINGS = 250;
	private static final String CAPTION_SIZE = "Size";
	private static final String CAPTION_SLIDER_WIDTH = "Width";
	private static final String CAPTION_SLIDER_HEIGHT = "Height";
	private static final String DESC_SLIDER_WIDTH = "Set graph width in pixel";
	private static final String DESC_SLIDER_HEIGHT = "Set graph height in pixel";
	private static final int GRAPH_MIN_WIDTH = SimpleGraph.DEF_WIDTH;
	private static final int GRAPH_MAX_WIDTH = 800;
	private static final int GRAPH_MIN_HEIGHT = SimpleGraph.DEF_HEIGHT;
	private static final int GRAPH_MAX_HEIGHT = 600;

	/* instance variables */
	private HorizontalLayout mainLayout;
	private VerticalLayout settingsLayout;
	private SimpleGraph graph;

	public SimpleGraphViewer() {
		super();
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	public SimpleGraph getGraph() {
		return graph;
	}

	private void buildMainLayout() {
		mainLayout = new HorizontalLayout();
		mainLayout.setSpacing(true);
//		mainLayout.setWidth("100%");

		buildGraph();
		buildSettingsLayout();

	}

	private void buildGraph() {
		graph = new SimpleGraph();
		mainLayout.addComponent(graph);
		mainLayout.setExpandRatio(graph, 0f);
	}

	private void buildSettingsLayout() {
		settingsLayout = new VerticalLayout();
		settingsLayout.setWidth(WIDTH_SETTINGS,UNITS_PIXELS);
		settingsLayout.setSpacing(true);
		buildResizePanel();

		mainLayout.addComponent(settingsLayout);
	}

	private void buildResizePanel() {
		Panel sizePanel = new Panel();
		sizePanel.setCaption(CAPTION_SIZE);

		sizePanel.addComponent(createWidthSlider());
		sizePanel.addComponent(createHeightSlider());

		settingsLayout.addComponent(sizePanel);
	}

	private Layout createWidthSlider() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth("100%");
		layout.setSpacing(true);

		final Label widthLabel = new Label(createLabelText(SimpleGraph.DEF_WIDTH));
		Slider widthSlider = buildSlider(CAPTION_SLIDER_WIDTH, DESC_SLIDER_WIDTH, SimpleGraph.DEF_WIDTH,
				GRAPH_MIN_WIDTH, GRAPH_MAX_WIDTH);
		widthSlider.setImmediate(true);
		widthSlider.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Object value = event.getProperty().getValue();
				if (value instanceof Double) {
					Double doubleValue = (Double) value;
					graph.setWidth(doubleValue.floatValue(), UNITS_PIXELS);
					Integer intValue = doubleValue.intValue();
					widthLabel.setValue(createLabelText(intValue));
				}
			}
		});
		layout.addComponent(widthSlider);
		layout.addComponent(widthLabel);
		layout.setExpandRatio(widthSlider, 0.8f);
		layout.setExpandRatio(widthLabel, 0.2f);
		layout.setComponentAlignment(widthLabel, Alignment.BOTTOM_LEFT);

		return layout;
	}

	private Layout createHeightSlider() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth("100%");
		layout.setSpacing(true);

		final Label heightLabel = new Label(createLabelText(SimpleGraph.DEF_HEIGHT));
		Slider heightSlider = buildSlider(CAPTION_SLIDER_HEIGHT,DESC_SLIDER_HEIGHT, SimpleGraph.DEF_HEIGHT,
				GRAPH_MIN_HEIGHT, GRAPH_MAX_HEIGHT);
		heightSlider.setImmediate(true);
		heightSlider.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Object value = event.getProperty().getValue();
				if (value instanceof Double) {
					Double doubleValue = (Double) value;
					graph.setHeight(doubleValue.floatValue(), UNITS_PIXELS);
					Integer intValue = doubleValue.intValue();
					heightLabel.setValue(createLabelText(intValue));
				}
			}
		});
		layout.addComponent(heightSlider);
		layout.addComponent(heightLabel);
		layout.setExpandRatio(heightSlider, 0.8f);
		layout.setExpandRatio(heightLabel, 0.2f);
		layout.setComponentAlignment(heightLabel, Alignment.BOTTOM_LEFT);

		return layout;
	}

	private Slider buildSlider(String caption, String description, int startValue, int min, int max) {
		final Slider slider = new Slider(caption, min, max);
		slider.setWidth("100%");
		slider.setDescription(description);
		try {
			slider.setValue(startValue);
		} catch (ValueOutOfBoundsException e) {
		}
		slider.setImmediate(true);
		return slider;
	}
	
	private String createLabelText(int size){
		return "(" + size + "px)";
	}

}
