package de.mosgrid.dygraph.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.mosgrid.dygraph.widget.IData;
import de.mosgrid.dygraph.widget.client.ui.Constants;

/**
 * Simple implementation of the IData interface. Values can be given in simple CSV or ARRAY format (see
 * http://dygraphs.com/data.html). NOTE: take care that you add the correct number of series names, according to given
 * data values!
 * 
 * @author Andreas Zink
 * 
 */
public class SimpleData implements IData {
	/* constants */
	private static final String NEW_LINE = "\n";
	/* instance variables */
	private String title;
	private String xLabel;
	private String yLabel;
	private StringBuilder valuesBuilder;
	private List<String> seriesLabels;
	private Set<String> dirtyAttributes;
	private Map<String, String> customOptions;

	/**
	 * Default constructor
	 * 
	 * @param numberOfSeries
	 *            The number of series this data object will contain
	 */
	public SimpleData() {
		this.valuesBuilder = new StringBuilder();
		this.seriesLabels = new ArrayList<String>();
		this.dirtyAttributes = new HashSet<String>();
		this.customOptions = new HashMap<String, String>();
	}

	/**
	 * Sets the title of the graph
	 */
	public void setTitle(String title) {
		this.title = title;
		this.dirtyAttributes.add(Constants.TITLE);
	}

	/**
	 * Adds a series name
	 */
	public void addSeriesName(String name) {
		seriesLabels.add(name);
		this.dirtyAttributes.add(Constants.LABELS);

	}

	/**
	 * Sets all series names
	 * 
	 */
	public void addSeriesNames(String[] names) {
		seriesLabels.addAll(Arrays.asList(names));
		this.dirtyAttributes.add(Constants.LABELS);

	}

	@Override
	public String[] getSeriesLabels() {
		String[] labels = new String[seriesLabels.size()];
		return seriesLabels.toArray(labels);
	}

	/**
	 * Sets the label for the x-axis
	 */
	public void setxLabel(String xLabel) {
		this.xLabel = xLabel;
		this.dirtyAttributes.add(Constants.X_LABEL);
	}

	/**
	 * Sets the label for the y-axis
	 */
	public void setyLabel(String yLabel) {
		this.yLabel = yLabel;
		this.dirtyAttributes.add(Constants.Y_LABEL);
	}

	/**
	 * Adds a single value line to the value buffer. Please look at the CSV specification at http://dygraphs.com/data.html
	 */
	public void addValueLine(String line) {
		valuesBuilder.append(line);
		if (!line.endsWith(NEW_LINE)) {
			valuesBuilder.append(NEW_LINE);
		}
		this.dirtyAttributes.add(Constants.VALUES);
	}

	/**
	 * Sets some custom options to the graph. See http://dygraphs.com/options.html for allowed key/value pairs
	 */
	public void setCustomOption(String key, String value) {
		customOptions.put(key, value);
		dirtyAttributes.add(Constants.OPTIONS);
	}

	@Override
	public Map<String, String> getCustomOptions() {
		return customOptions;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getxLabel() {
		return xLabel;
	}

	@Override
	public String getyLabel() {
		return yLabel;
	}

	@Override
	public String getValues() {
		return buildSeriesHeader() + valuesBuilder.toString();
	}

	private String buildSeriesHeader() {
		StringBuilder headerBuilder = new StringBuilder();
		headerBuilder.append(getxLabel() != null ? getxLabel() : "X");
		for (String series : getSeriesLabels()) {
			headerBuilder.append("," + series);
		}
		headerBuilder.append("\n");
		return headerBuilder.toString();
	}

	@Override
	public Set<String> getDirtyAttributes() {
		return dirtyAttributes;
	}

	@Override
	public void clearDirtyAttributes() {
		dirtyAttributes.clear();
	}

	@Override
	public void markAllAttributesDirty() {
		this.dirtyAttributes.add(Constants.TITLE);
		this.dirtyAttributes.add(Constants.X_LABEL);
		this.dirtyAttributes.add(Constants.Y_LABEL);
		this.dirtyAttributes.add(Constants.VALUES);
		this.dirtyAttributes.add(Constants.LABELS);
		this.dirtyAttributes.add(Constants.OPTIONS);
	}

}
