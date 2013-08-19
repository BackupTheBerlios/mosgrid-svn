package de.mosgrid.dygraph.util;

/**
 * Your CSV values should each be three numbers separated by semicolons ("low;mid;high") e.g.
 * 
 * "X,Y1,Y2\n" + "1,10;20;30,20;5;25\n" + "2,10;25;35,20;10;25\n",
 * 
 * This enables custom drawing of error bars.
 * NOTE that the 'labels' option does not work for this type of data.
 * @author Andreas Zink
 * 
 */
public class DataWithCustomErrorBars extends SimpleData {

	public DataWithCustomErrorBars() {
		super();
		setCustomOption("customBars", "true");
	}

}
