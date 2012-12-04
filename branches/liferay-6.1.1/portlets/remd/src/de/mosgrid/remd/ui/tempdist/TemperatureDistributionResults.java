package de.mosgrid.remd.ui.tempdist;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.StreamResource.StreamSource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.mosgrid.dygraph.widget.IData;
import de.mosgrid.dygraph.widget.SimpleGraph;
import de.mosgrid.remd.util.DataCreator;
import de.mosgrid.remd.util.TemperaturePredictor.IReplica;

public class TemperatureDistributionResults extends CustomComponent {
	/* constants */
	private static final long serialVersionUID = -8282492523510442168L;
	private static final float GRAPH_WIDTH = 600f;
	private static final float GRAPH_HEIGHT = 375f;
	private static final String CSV_SEP = ";";

	private static final String CAPTION_BTN_E_DIST = "Energy Distribution";
	private static final String TOOLTIP_BTN_E_DIST = "Shows the distribution of potential energy.";
	private static final String CAPTION_BTN_T_DIST = "Temperature Distribution";
	private static final String TOOLTIP_BTN_T_DIST = "Shows the distribution of temperature.";
	private static final String CAPTION_BTN_EXPORT = "Export CSV";
	private static final String TOOLTIP_BTN_EXPORT = "Download results in csv format.";

	private static final String DESC_E_DIST = "This plot shows the approximated distribution of potential energy for all replicas.";
	private static final String DESC_T_DIST = "This plot shows the temperature distribution of replicas. The shaded area shows the deviation from a linear distribution.";

	private static final Character C_MU = new Character('\u03BC');
	private static final Character C_SIGMA = new Character('\u03C3');
	private static final String C_ID = "ID";
	private static final String C_TEMP = "T (K)";
	private static final String C_POT_E_MEAN = C_MU + " (kJ/mol)";
	private static final String C_POT_E_SD = C_SIGMA + " (kJ/mol)";
	private static final String C_E_DIFF_MEAN = C_MU + "<sub>12</sub> (kJ/mol)";
	private static final String C_E_DIFF_SD = C_SIGMA + "<sub>12</sub> (kJ/mol)";
	private static final String C_P_EX = "p<sub>exchange</sub>";
	private static final String FILENAME_CSV = "RemdDistribution.csv";

	/* instance variables */
	private List<IReplica> replicas;
	private DataCreator creator = new DataCreator();
	/* ui components */
	private VerticalLayout mainLayout;
	private HorizontalLayout buttonContainer;

	public TemperatureDistributionResults(List<IReplica> replicas) {
		this.replicas = replicas;
		buildMainLayout();
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		mainLayout.setSizeUndefined();

		buildSummary();
		buildButtonContainer();

		setCompositionRoot(mainLayout);
	}

	private void buildSummary() {
		Label summaryLabel = new Label(buildSummaryString(), Label.CONTENT_XHTML);
		summaryLabel.setWidth(40f, UNITS_EM);

		mainLayout.addComponent(summaryLabel);
	}

	private void buildButtonContainer() {
		buttonContainer = new HorizontalLayout();
		buttonContainer.setSpacing(true);

		Button energyDistButton = new Button(CAPTION_BTN_E_DIST);
		energyDistButton.setDescription(TOOLTIP_BTN_E_DIST);
		energyDistButton.setStyleName("small");
		energyDistButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 9011300972090318584L;

			@Override
			public void buttonClick(ClickEvent event) {
				showEnergyDistributionPlot();
			}

		});
		buttonContainer.addComponent(energyDistButton);

		Button tempDistButton = new Button(CAPTION_BTN_T_DIST);
		tempDistButton.setDescription(TOOLTIP_BTN_T_DIST);
		tempDistButton.setStyleName("small");
		tempDistButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 2608477867210341683L;

			@Override
			public void buttonClick(ClickEvent event) {
				showTemperatureDistributionPlot();
			}

		});
		buttonContainer.addComponent(tempDistButton);

		Button exportButton = new Button(CAPTION_BTN_EXPORT);
		exportButton.setDescription(TOOLTIP_BTN_EXPORT);
		exportButton.setStyleName("small");
		exportButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 9011300972090318584L;

			@Override
			public void buttonClick(ClickEvent event) {
				exportCSV();
			}
		});
		buttonContainer.addComponent(exportButton);

		mainLayout.addComponent(buttonContainer);
		mainLayout.setComponentAlignment(buttonContainer, Alignment.BOTTOM_RIGHT);
	}

	/**
	 * Creates the content for the summary label
	 */
	private String buildSummaryString() {
		StringBuilder summaryBuilder = new StringBuilder();
		summaryBuilder
				.append("<table border=\"1\" rules=\"groups\" frame=\"void\" cellspacing=\"10\" cellpadding=\"10\" width=\"100%\">");
		// build header
		summaryBuilder.append("<thead>");
		summaryBuilder.append(createSummaryLine("<th style=\"white-space: nowrap\">", "</th>", C_ID, C_TEMP,
				C_POT_E_MEAN, C_POT_E_SD, C_E_DIFF_MEAN, C_E_DIFF_SD, C_P_EX));
		summaryBuilder.append("</thead>");

		// build summary
		summaryBuilder.append("<tbody>");
		for (int i = 0; i < replicas.size(); i++) {
			IReplica replica = replicas.get(i);
			// summaryBuilder.append(lineBreak);
			summaryBuilder.append(createSummaryLine("<td>", "</td>", String.valueOf(i + 1),
					replica.getTemperatureAsString(), replica.getPotentialEnergyMeanAsString(),
					replica.getPotentialEnergySDAsString(), replica.getEnergyDifferenceMeanAsString(),
					replica.getEnergyDifferenceSDAsString(), replica.getExchangeProbabilityAsString()));
		}
		summaryBuilder.append("</tbody>");
		summaryBuilder.append("</table>");
		return summaryBuilder.toString();
	}

	/**
	 * creates a single summary line
	 */
	private String createSummaryLine(String beginTag, String endTag, String id, String temp, String potEMean,
			String potESD, String eDiffMean, String eDiffSD, String pEX) {
		StringBuilder lineBuilder = new StringBuilder();
		lineBuilder.append("<tr>");

		lineBuilder.append(beginTag + id + endTag);
		lineBuilder.append(beginTag + temp + endTag);
		lineBuilder.append(beginTag + potEMean + endTag);
		lineBuilder.append(beginTag + potESD + endTag);
		lineBuilder.append(beginTag + eDiffMean + endTag);
		lineBuilder.append(beginTag + eDiffSD + endTag);
		lineBuilder.append(beginTag + pEX + endTag);

		lineBuilder.append("</tr>");
		return lineBuilder.toString();
	}

	/**
	 * Called when energy dist button was clicked
	 */
	private void showEnergyDistributionPlot() {
		IData energyData = creator.createPotentialEnergyDistribution(replicas);
		SimpleGraph potEnergyGraph = new SimpleGraph();
		potEnergyGraph.setData(energyData);
		potEnergyGraph.setWidth(GRAPH_WIDTH, UNITS_PIXELS);
		potEnergyGraph.setHeight(GRAPH_HEIGHT, UNITS_PIXELS);

		Window subsubWindow = new Window();
		subsubWindow.getContent().setSizeUndefined();
		subsubWindow.addComponent(potEnergyGraph);

		Label descLabel = new Label(DESC_E_DIST);
		subsubWindow.addComponent(descLabel);

		subsubWindow.setResizable(false);

		getApplication().getMainWindow().addWindow(subsubWindow);
		subsubWindow.center();
	}

	/**
	 * Called when temp dist button was clicked
	 */
	private void showTemperatureDistributionPlot() {
		IData tempData = creator.createTemperatureDistribution(replicas);
		SimpleGraph temperatureGraph = new SimpleGraph();
		temperatureGraph.setData(tempData);
		temperatureGraph.setWidth(GRAPH_WIDTH, UNITS_PIXELS);
		temperatureGraph.setHeight(GRAPH_HEIGHT, UNITS_PIXELS);

		Window subsubWindow = new Window();
		subsubWindow.getContent().setSizeUndefined();
		subsubWindow.addComponent(temperatureGraph);

		Label descLabel = new Label(DESC_T_DIST);
		subsubWindow.addComponent(descLabel);

		subsubWindow.setResizable(false);

		getApplication().getMainWindow().addWindow(subsubWindow);
		subsubWindow.center();
	}

	/**
	 * Called when export button was pressed. Opens a download stream in csv format.
	 */
	private void exportCSV() {
		final StringReader stringReader = new StringReader(buildCSV());
		final StreamSource streamSource = new StreamSource() {
			private static final long serialVersionUID = -7506139633621099566L;
			private InputStream stream = null;

			@Override
			public InputStream getStream() {
				if (stream == null) {
					stream = new InputStream() {
						@Override
						public int read() throws IOException {
							return stringReader.read();
						}

					};
				}
				return stream;
			}
		};
		StreamResource streamResource = new StreamResource(streamSource, FILENAME_CSV, getApplication());
		try {
			streamResource.setCacheTime(5000); // no cache (<=0) does not work with IE8

			getWindow().open(streamResource, "_top");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Builds the temperature distribution in CSV format
	 */
	private String buildCSV() {
		StringBuilder csvBuilder = new StringBuilder();
		// build header
		csvBuilder.append(createCsvLine(C_ID, C_TEMP, "mean potential energy (kJ/mol)",
				"sd of potential energy (kJ/mol)", "mean energy difference (kJ/mol)",
				"sd of energy difference (kJ/mol)", C_P_EX));

		String lineBreak = "\n";
		for (int i = 0; i < replicas.size(); i++) {
			IReplica replica = replicas.get(i);
			csvBuilder.append(lineBreak);
			csvBuilder.append(createCsvLine(String.valueOf(i + 1), replica.getTemperatureAsString(),
					replica.getPotentialEnergyMeanAsString(), replica.getPotentialEnergySDAsString(),
					replica.getEnergyDifferenceMeanAsString(), replica.getEnergyDifferenceSDAsString(),
					replica.getExchangeProbabilityAsString()));
		}
		return csvBuilder.toString();
	}

	/**
	 * Creates a CSV line
	 */
	private String createCsvLine(String id, String temp, String potEMean, String potESD, String eDiffMean,
			String eDiffSD, String pEX) {

		StringBuilder lineBuilder = new StringBuilder();
		lineBuilder.append(id + CSV_SEP);
		lineBuilder.append(temp + CSV_SEP);
		lineBuilder.append(potEMean + CSV_SEP);
		lineBuilder.append(potESD + CSV_SEP);
		lineBuilder.append(eDiffMean + CSV_SEP);
		lineBuilder.append(eDiffSD + CSV_SEP);
		lineBuilder.append(pEX + CSV_SEP);
		return lineBuilder.toString();
	}

}
