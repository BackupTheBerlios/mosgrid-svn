package de.mosgrid.util.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.vaadin.data.Property;

import de.mosgrid.msml.jaxb.bindings.EntryType;
import de.mosgrid.msml.jaxb.bindings.Restriction;
import de.mosgrid.msml.jaxb.bindings.ScalarType;
import de.mosgrid.msml.util.helper.XmlHelper;

/**
 * A Vaadin Property which wraps a CML Scalar Element. This Property can be bound to Vaadin Fields which allows direct
 * editing of CML.
 * 
 * @author Andreas Zink
 * 
 */
public class ScalarProperty implements Property, Property.ValueChangeNotifier {
	private static final long serialVersionUID = 4587856414061364185L;
	@SuppressWarnings("unused")
	private final Logger LOGGER = LoggerFactory.getLogger(ScalarProperty.class);

	private ScalarType scalarElement;
	private EntryType dictEntry;

	// is this parameter editable?
	private boolean readOnly;
	// is this parameter optional (can be left empty)?
	private boolean optional;
	private Class<?> valueClassType;
	private String unitSymbol;
	private String description;
	private BoxedValue min, max;
	private boolean isMinInclusive, isMaxInclusive;
	private Map<String, String> restrictionsMap;

	private List<ValueChangeListener> listenerList;

	public ScalarProperty(ScalarType scalarElement, EntryType dictEntry) {
		this.scalarElement = scalarElement;
		this.dictEntry = dictEntry;
		init();
	}

	public ScalarProperty(ScalarType scalarElement, EntryType dictEntry, boolean optional) {
		this.scalarElement = scalarElement;
		this.dictEntry = dictEntry;
		this.optional = optional;
		init();
	}

	public ScalarProperty(ScalarType scalarElement, EntryType dictEntry, boolean readOnly, boolean optional) {
		this.scalarElement = scalarElement;
		this.dictEntry = dictEntry;
		this.readOnly = readOnly;
		this.optional = optional;
		init();
	}

	private void init() {
		if (dictEntry.isFlag() != null && dictEntry.isFlag()) {
			this.optional = false;
		}
		listenerList = new ArrayList<Property.ValueChangeListener>();
		valueClassType = resolveClassType();
		unitSymbol = resolveUnitSymbol();
		description = resolveDescription();
		restrictionsMap = resolveRestrictions();
		resolveMinMaxValues();
		setDefaultValue();
	}

	/**
	 * @return The corresponding Java Class to the specified dataType
	 */
	private Class<?> resolveClassType() {
		String dataTypeRef = dictEntry.getDataType();
		String dataTypeId = XmlHelper.getInstance().getSuffix(dataTypeRef);
		if (dataTypeId.equals("integer")) {
			return Integer.class;
		} else if (dataTypeId.equals("double")) {
			return Double.class;
		} else {
			return String.class;
		}
	}

	/**
	 * @return The symbol of the unit, if any
	 */
	private String resolveUnitSymbol() {
		String unitRef = dictEntry.getUnits();
		String unitId = XmlHelper.getInstance().getSuffix(unitRef);
		if (!unitId.equals("none")) {
			return unitId;
		}
		return null;
	}

	/**
	 * @return The description text of this parameter, if any
	 */
	private String resolveDescription() {
		StringBuilder descriptionBuilder = new StringBuilder();
		for (Element child : dictEntry.getDescription().getAny()) {
			descriptionBuilder.append(child.getTextContent().trim());
		}
		return descriptionBuilder.toString();
	}

	/**
	 * @return The value restrictions on this parameter, if any. Creates value restrictions 'on' and 'off' for flag
	 *         parameters.
	 */
	private Map<String, String> resolveRestrictions() {
		HashMap<String, String> restrictions = new HashMap<String, String>();
		if (dictEntry.isFlag() != null && dictEntry.isFlag()) {
			restrictions.put("On", "on");
			restrictions.put("Off", "off");
		} else {
			if (dictEntry.getRestriction() != null) {
				for (Restriction restr : dictEntry.getRestriction()) {
					// put title as key as this is shown in the selection element
					restrictions.put(restr.getTitle(), restr.getValue());
				}
			}
		}
		return restrictions;
	}

	private void resolveMinMaxValues() {
		if (dictEntry.getMaxExclusive() != null) {
			max = new BoxedValue(getType(), dictEntry.getMaxExclusive());
			isMaxInclusive = false;
		} else if (dictEntry.getMaxInclusive() != null) {
			max = new BoxedValue(getType(), dictEntry.getMaxInclusive());
			isMaxInclusive = true;
		}

		if (dictEntry.getMinExclusive() != null) {
			min = new BoxedValue(getType(), dictEntry.getMinExclusive());
			isMinInclusive = false;
		} else if (dictEntry.getMinInclusive() != null) {
			min = new BoxedValue(getType(), dictEntry.getMinInclusive());
			isMinInclusive = true;
		}

	}

	/**
	 * Fills the wrapped scalarElement with a default value if non is set. If the value is restricted, the first value
	 * of available restrictions is set. Otherwise an empty String or zero is set.
	 */
	private void setDefaultValue() {
		// no default value set in template?
		if (scalarElement.getValue() == null || scalarElement.getValue().trim().equals("")) {
			if (isOptional()) {
				// value can be left empty if parameter is optional
				scalarElement.setValue("");
			} else {
				if (isRestricted()) {
					// choose first restriction value as default
					String newDefault = restrictionsMap.values().iterator().next();
					scalarElement.setValue(newDefault);
				} else {
					if (getType() == Double.class || getType() == Integer.class) {
						// set to zero for numeric types
						scalarElement.setValue("0");
					} else {
						// empty String for all others
						scalarElement.setValue("");
					}
				}
			}
		}
	}

	public String formattedMin() {
		if (!hasMinimum())
			return "-\u221E";
		return getMinimum().getFormattedValue();
	}

	public String formattedMax() {
		if (!hasMaximum())
			return "\u221E";
		return getMaximum().getFormattedValue();
	}

	@Override
	public Object getValue() {
		if (scalarElement.getValue().equals("")) {
			if (getType() == Double.class) {
				return null;
			} else if (getType() == Integer.class) {
				return null;
			} else {
				return scalarElement.getValue();
			}
		} else {
			if (getType() == Double.class) {
				return new Double(scalarElement.getValue());
			} else if (getType() == Integer.class) {
				return new Integer(scalarElement.getValue());
			} else {
				return scalarElement.getValue();
			}
		}
	}

	@Override
	public String toString() {
		/*
		 * In some cases the 'toString()' method is used to represent Properties. Thus, this method can have the same
		 * meaning as 'getValue()'.
		 */
		return scalarElement.getValue();
	}

	@Override
	public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
		if (isReadOnly()) {
			throw new ReadOnlyException();
		}

		if (newValue == null) {
			// If value is null just set to empty string. The validators should already have checked if this is allowed
			scalarElement.setValue("");
		} else {
			// Otherwise set value. DataType check should have been done by validators
			scalarElement.setValue(newValue.toString());
		}
		// inform listeners of change (needed if more than one Editor)
		fireChangedEvent();
	}

	@Override
	public Class<?> getType() {
		return valueClassType;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public void setReadOnly(boolean newStatus) {
		throw new UnsupportedOperationException("Not allowed to change 'readOnly' state");
		// Remember to implement ChangeListeners if ever activated, but actually this setter won't be needed
	}

	/**
	 * @return 'true' if scalar has a maximum
	 */
	public boolean hasMaximum() {
		return max != null;
	}

	/**
	 * @return The maximum value, if any
	 */
	public BoxedValue getMaximum() {
		return max;
	}

	/**
	 * @return 'true' if maximum value is included in allowed interval
	 */
	public boolean isMaxInclusive() {
		return isMaxInclusive;
	}

	/**
	 * @return 'true' if scalar has a minimum
	 */
	public boolean hasMinimum() {
		return min != null;
	}

	/**
	 * @return The minimum value, if any
	 */
	public BoxedValue getMinimum() {
		return min;
	}

	/**
	 * @return 'true' if minimum value is included in allowed interval
	 */
	public boolean isMinInclusive() {
		return isMinInclusive;
	}

	/**
	 * @return 'true' if allowed values are restricted
	 */
	public boolean isRestricted() {
		return (restrictionsMap.size() > 0);
	}

	/**
	 * @return The possible values for this scalar
	 */
	public Map<String, String> getRestrictions() {
		return restrictionsMap;
	}

	/**
	 * @return 'true' if value has a unit
	 */
	public boolean hasUnit() {
		return unitSymbol != null && !"".equals(unitSymbol);
	}

	/**
	 * @return The unit symbol if this scalar or 'null' if not given (check with hasUnit before)
	 */
	public String getUnitSymbol() {
		return unitSymbol;
	}

	/**
	 * @return 'true' if this scalar has a description
	 */
	public boolean hasDescription() {
		return description != null;
	}

	/**
	 * @return The description of this scalar in the dictionary or 'null' if not given (check with hasDescription
	 *         before)
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return The term of this scalar in the dictionary
	 */
	public String getTerm() {
		return dictEntry.getTerm();
	}

	/**
	 * @return The title of this scalar in the dictionary
	 */
	public String getTitle() {
		return dictEntry.getTitle();
	}

	public boolean isOptional() {
		return optional;
	}

	@Override
	public void addListener(ValueChangeListener listener) {
		listenerList.add(listener);

	}

	@Override
	public void removeListener(ValueChangeListener listener) {
		if (listenerList.contains(listener)) {
			listenerList.remove(listener);
		}
	}

	private void fireChangedEvent() {
		ValueChangeEvent changeEvent = new ScalarValueChangeEvent(this);
		for (ValueChangeListener l : listenerList) {
			l.valueChange(changeEvent);
		}
	}

	/**
	 * Small helper class to fire ValueChangeEvents. Needed if property gets edited by more than one Editor.
	 * 
	 * @author Andreas Zink
	 * 
	 */
	private class ScalarValueChangeEvent implements ValueChangeEvent {
		private static final long serialVersionUID = -7864221347207332548L;

		private Property prop;

		public ScalarValueChangeEvent(Property p) {
			this.prop = p;
		}

		@Override
		public Property getProperty() {
			return prop;
		}

	}

}
