package de.mosgrid.msml.editors;

import java.io.File;

import de.mosgrid.msml.jaxb.bindings.DescriptionType;

/**
 * Adapter for editing msml templates.
 * 
 * @author Andreas Zink
 * 
 */
public class MSMLTemplate extends JobListEditor {

	private boolean _saveable;

	public MSMLTemplate(File msmlTemplate) {
		super(msmlTemplate);
	}

	@Override
	public MSMLTemplate copy() {
		return copy(false);
	}
	
	public MSMLTemplate copy(boolean saveable) {
		if (msmlFile != null) {
			MSMLTemplate res = new MSMLTemplate(msmlFile);
			res._saveable = saveable;
			return res;
		} else {
			throw new UnsupportedOperationException("Can't create copy because instance was not created from File");
		}		
	}

	@Override
	public boolean marshall() {
		if (_saveable)
			return super.marshall();
		else
			throw new UnsupportedOperationException("Not allowed to write back to template file!");
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * @return The name of the template which is used for identification. This name will be part of the name of an
	 *         imported workflow instance. It is thus possible to identify the used template for any already imported
	 *         workflow instance. Returns the displayname attribute or, if not given, the id attribute
	 */
	public String getName() {
		String displayName = getJobListElement().getDisplayName();
		if (displayName != null && !"".equals(displayName))
			return displayName;
		return getJobListElement().getId();
	}
	
	public boolean hasParserConfig() {
		return getJobListElement() != null &&
				getJobListElement().getParserConfig() != null &&
				getJobListElement().getParserConfig().hasParameter();
	}
	
	public boolean hasDisplayName() {
		return getJobListElement() != null &&
				getJobListElement().getDisplayName() != null &&
				!"".equals(getJobListElement().getDisplayName());
	}

	public boolean hasTitle() {
		return getJobListElement() != null &&
				getJobListElement().getTitle() != null &&
				!"".equals(getJobListElement().getTitle());
	}

	public boolean hasDescription() {
		return getJobListElement() != null &&
				getJobListElement().getDescription() != null &&
				getJobListElement().getDescription().getPlainText() != null &&
				!"".equals(getJobListElement().getDescription().getPlainText());
	}

	@Override
	public int hashCode() {
		return msmlFile.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof MSMLTemplate) {
			MSMLTemplate other = (MSMLTemplate) obj;
			if (hashCode() == other.hashCode() && getName().equals(other.getName())) {
				return true;
			}
		}
		return false;

	}

	public DescriptionType createDescriptionIfNotExisting() {
		if (getJobListElement() == null)
			return null;
		return getJobListElement().createDescriptionIfNotExisting();
	}
}
