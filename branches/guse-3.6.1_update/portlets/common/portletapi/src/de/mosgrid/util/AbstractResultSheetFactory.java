package de.mosgrid.util;

import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

import de.mosgrid.msml.editors.JobListEditor;
import de.mosgrid.msml.jaxb.bindings.EntryType;
import de.mosgrid.msml.jaxb.bindings.PropertyType;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.util.interfaces.IResultSheetFactory;

public abstract class AbstractResultSheetFactory implements IResultSheetFactory {

	/**
	 * Creates a list of result labels for a job. Each label represents one property of the propertylist in the
	 * finalization element
	 */
	protected Collection<Component> createResultFields(JobListEditor msmlEditor, Job job) {
		Collection<Component> resultFields = new ArrayList<Component>();
		if (job.getFinalization() != null && job.getFinalization().getPropertyList() != null) {
			// iterate over properties in finalization element
			for (PropertyType property : job.getFinalization().getPropertyList().getProps()) {
				EntryType dictEntry = msmlEditor.getDictEntry(property.getDictRef());
				String value = null;
				// parse value of child element
				if (property.getScalar() != null) {
					value = property.getScalar().getValue();
				} else if (property.getArray() != null) {
					value = property.getArray().getValue();
				} else if (property.getMatrix() != null) {
					value = property.getMatrix().getValue();
				}
				// create a label for each result property
				Label resultLabel = new Label(dictEntry.getTitle() + ": " + value);
				resultFields.add(resultLabel);
			}
		}

		return resultFields;
	}

}
