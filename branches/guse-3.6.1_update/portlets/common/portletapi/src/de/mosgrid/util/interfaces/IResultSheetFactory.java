package de.mosgrid.util.interfaces;

import de.mosgrid.gui.resultsheet.IResultSheet;
import de.mosgrid.msml.editors.JobListEditor;

public interface IResultSheetFactory {

	IResultSheet createResultSheet(JobListEditor jobListEditor);

}
