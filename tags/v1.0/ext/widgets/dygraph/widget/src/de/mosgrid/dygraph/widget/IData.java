package de.mosgrid.dygraph.widget;

import java.util.Map;
import java.util.Set;

/**
 * Simple data interface for data to be shown by a graph
 * 
 * @author Andreas Zink
 * 
 */
/*
 * TEMPERATURE EXAMPLE Date,High,Low 20070101,62,39 ...
 */
public interface IData {
	/**
	 * @return The title of the graph
	 */
	public String getTitle();

	/**
	 * @return The label of the x axis
	 */
	public String getxLabel();

	/**
	 * @return The label of the y axis
	 */
	public String getyLabel();

	/**
	 * @return The labels for the data series
	 */
	public String[] getSeriesLabels();

	/**
	 * @return The date values with a header containing the series names. See CSV specification http://dygraphs.com/data.html 
	 */
	public String getValues();

	/**
	 * @return A set of attribute names which have been changed
	 */
	public Set<String> getDirtyAttributes();

	/**
	 * Clears the set of dirty attributes
	 */
	public void clearDirtyAttributes();

	/**
	 * @return A map which specifies custom options. See http://dygraphs.com/options.html for allowed key/value pairs
	 */
	public Map<String, String> getCustomOptions();

	/**
	 * Shall mark all attributes as dirty
	 */
	public void markAllAttributesDirty();

}
