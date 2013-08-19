package de.mosgrid.dygraph.util;

/**
 * Often you have a measurement and also a measure of its uncertainty: a standard deviation. 
 * Dygraphs will look for alternating value and standard deviation columns in your CSV data. Here's what it should look like:
 * 
 * "X,Y1,Y2\n" + "1,10,5,20,5\n" + "2,12,5,22,5\n",
 * 
 * The "5" values are standard deviations. When each point is plotted, a 2-standard deviation region around it is shaded, resulting in a 95% confidence interval.
 * NOTE that the 'labels' option does not work for this type of data.
 * @author Andreas Zink
 * 
 */
public class DataWithErrorBars extends SimpleData {

	public DataWithErrorBars() {
		super();
		setCustomOption("errorBars", "true");
	}

}
