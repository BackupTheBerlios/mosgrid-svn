package de.mosgrid.dygraph.util.parser;

import de.mosgrid.dygraph.util.SimpleData;
import de.mosgrid.dygraph.widget.IData;

/**
 * This is a rudimentary parser for xvg files
 * 
 * @author Andreas Zink
 * 
 */
/*XVG example
 
# This file was created Sat Mar 17 19:42:59 2012
# by the following command:
# /share/apps/gromacs-4.5.5/bin/g_energy -f EM.edr -o EMpotential.xvg 
#
# g_energy is part of G R O M A C S:
#
# Glycine aRginine prOline Methionine Alanine Cystine Serine
#
@    title "Gromacs Energies"
@    xaxis  label "Time (ps)"
@    yaxis  label "(kJ/mol)"
@TYPE xy
@ view 0.15, 0.15, 0.75, 0.85
@ legend on
@ legend box on
@ legend loctype view
@ legend 0.78, 0.8
@ legend length 2
@ s0 legend "Potential"
    0.000000  -282631.781250
    1.000000  -325651.156250
    2.000000  -358315.250000
    3.000000  -369302.812500
    5.000000  -376356.218750
    6.000000  -381311.062500
 */
public class SimpleXVGParser implements IParser {
	/* constants */
	private static final String GROMACS_COMMENT = "#";
	private static final String XMGRACE_COMMENT = "@";
	private static final String TITLE = "@    title";
	private static final String X_AXIS = "@    xaxis";
	private static final String Y_AXIS = "@    yaxis";
	private static final String LABEL = "label";
	private static final String LEGEND = "legend";
	private static final String SERIES_LABEL = "@ s";
	private static final String NEW_LINE = "\n";
	/* instance variables */
	private SimpleData data;

	public SimpleXVGParser() {
		this.data = new SimpleData();
	}

	public IData getData() {
		return data;
	}

	@Override
	public void parseLine(String line) {
		if (line.startsWith(GROMACS_COMMENT)) {
			// nothing to do by now
		} else if (line.startsWith(XMGRACE_COMMENT)) {
			parseXmGraceComment(line);
		} else {
			parseValueLine(line);
		}
	}

	/**
	 * Parse XmGrace comments (title, labels, series names, etc.)
	 */
	private void parseXmGraceComment(String line) {
		if (line.startsWith(TITLE)) {
			int beginIndex = line.indexOf('"', TITLE.length()) + 1;
			int endIndex = line.indexOf('"', beginIndex);
			String title = line.substring(beginIndex, endIndex);
			data.setTitle(title);
		} else if (line.startsWith(X_AXIS)) {
			int labelIndex = line.indexOf(LABEL, X_AXIS.length());
			int beginIndex = line.indexOf('"', labelIndex + LABEL.length()) + 1;
			int endIndex = line.indexOf('"', beginIndex);
			String xLabel = line.substring(beginIndex, endIndex);
			data.setxLabel(xLabel);
		} else if (line.startsWith(Y_AXIS)) {
			int labelIndex = line.indexOf(LABEL, Y_AXIS.length());
			int beginIndex = line.indexOf('"', labelIndex + LABEL.length()) + 1;
			int endIndex = line.indexOf('"', beginIndex);
			String yLabel = line.substring(beginIndex, endIndex);
			data.setyLabel(yLabel);
		} else if (line.startsWith(SERIES_LABEL)) {
			int legendIndex = line.indexOf(LEGEND, SERIES_LABEL.length());
			int beginIndex = line.indexOf('"', legendIndex + LEGEND.length()) + 1;
			int endIndex = line.indexOf('"', beginIndex);
			String seriesName = line.substring(beginIndex, endIndex);
			data.addSeriesName(seriesName);
		}
	}

	/**
	 * Parse a single line of values
	 */
	private void parseValueLine(String line) {
		String trimmed = line.trim();
		String csv = trimmed.replaceAll("\\s+", ",");

		data.addValueLine(csv + NEW_LINE);
	}

}
