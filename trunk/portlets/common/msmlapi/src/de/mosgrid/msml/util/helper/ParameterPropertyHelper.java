package de.mosgrid.msml.util.helper;

import java.math.BigInteger;
import java.util.regex.Pattern;

import de.mosgrid.msml.editors.MSMLEditor;
import de.mosgrid.msml.enums.Namespaces;
import de.mosgrid.msml.jaxb.bindings.ArrayType;
import de.mosgrid.msml.jaxb.bindings.EntryType;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.jaxb.bindings.PropertyType;
import de.mosgrid.msml.jaxb.bindings.ScalarType;
import de.mosgrid.msml.util.IDictionary;

public class ParameterPropertyHelper {
	
	public enum ENTRYTYPE {
		DATATYPE, UNITS;
	}
	
	public static void setScalar(MSMLEditor template, ParameterType toSet, IDictionary dict, String ref, String val) {
		if (val == null) {
			toSet.setScalar(null);
			return;
		}
		
		if (toSet.getScalar() == null)
			toSet.setScalar(new ScalarType());
		if (dict != null && ref != null && !"".equals(ref)) {
			
			String dictRef = getDictRef(template, dict, ref);
			toSet.setDictRef(dictRef);

			String dataType = exchangePrefix(template, dict, ref, ENTRYTYPE.DATATYPE);
			toSet.getScalar().setDataType(dataType);
			
			String units = exchangePrefix(template, dict, ref, ENTRYTYPE.UNITS);
			toSet.getScalar().setUnits(units);
		}
		
		toSet.getScalar().setValue(val);
	}
	
	public static void setScalar(MSMLEditor template, PropertyType toSet, IDictionary dict, String ref, String val) {
		if (val == null) {
			toSet.setScalar(null);
			return;
		}
		
		if (toSet.getScalar() == null)
			toSet.setScalar(new ScalarType());
		if (dict != null && ref != null && !"".equals(ref)) {
			
			String dictRef = getDictRef(template, dict, ref);
			toSet.setDictRef(dictRef);
			
			String dataType = exchangePrefix(template, dict, ref, ENTRYTYPE.DATATYPE);
			toSet.getScalar().setDataType(dataType);
			
			String units = exchangePrefix(template, dict, ref, ENTRYTYPE.UNITS);
			toSet.getScalar().setUnits(units);
		}
		
		toSet.getScalar().setValue(val);
	}
	
	/**
	 * This helpermethod sets the array-value of the provided property to the value specified.
	 * It preserves validty of the template by looking up the value in the dictionary to set the units and unittype.
	 * It also splits the provided value according to the elementCountPattern to calculate the 
	 * element count.
	 * 
	 * @param template The template that the property will be added to. This object is needed to identify the correct prefixes.
	 * @param toSet The property whose array-value will be set.
	 * @param dict dict-Part of the dictRef.
	 * @param ref ref-Part of the dictRef.
	 * @param val The actual value for the property.
	 * @param delimiter The value will be split according to this delimiter to determine the elementcount.
	 */
	public static void setArray(MSMLEditor template, PropertyType toSet, IDictionary dict, String ref, String val, String delimiter) {
		if (val == null) {
			toSet.setArray(null);
			return;
		}
		
		if (toSet.getArray() == null)
			toSet.setArray(new ArrayType());
		if (dict != null && ref != null && !"".equals(ref)) {
			
			String dictRef = getDictRef(template, dict, ref);
			toSet.setDictRef(dictRef);

			String dataType = exchangePrefix(template, dict, ref, ENTRYTYPE.DATATYPE);
			toSet.getArray().setDataType(dataType);
			
			String units = exchangePrefix(template, dict, ref, ENTRYTYPE.UNITS);
			toSet.getArray().setUnits(units);
		}
		
		toSet.getArray().setValue(val);
		toSet.getArray().setDelimiter(delimiter);
		
		if (delimiter == null || "".equals(delimiter))
			throw new UnsupportedOperationException("Delimiter is needed to determine size of array. Current delimiter: " + delimiter);
		toSet.getArray().setSize(BigInteger.valueOf((long)val.split(Pattern.quote(delimiter)).length));
	}

	private static String getDictRef(MSMLEditor template, IDictionary dict, String ref) {
		String pref = template.getPrefixToNamespace(dict.getNamespace());
		if (pref == null)
			template.addNamespace(dict.getDictPrefix(), dict.getNamespace());
		
		String dictRef = template.getPrefixToNamespace(dict.getNamespace()) + ":" + ref;
		return dictRef;
	}

	private static String exchangePrefix(MSMLEditor template, IDictionary dict, String ref, ENTRYTYPE type) {
		EntryType entry = dict.getEntryById(ref);
		String entryInDict;
		switch (type) {
			case DATATYPE:
				entryInDict = entry.getDataType();
				break;
			case UNITS:
				entryInDict = entry.getUnits();
				break;
			default:
				throw new UnsupportedOperationException("Need new type.");
		}
		
		String entryPrefixInDict = XmlHelper.getInstance().getPrefix(entryInDict);
		String entrySuffix = XmlHelper.getInstance().getSuffix(entryInDict);
		String entryNamespaceInDict = dict.getNamespaceToPrefix(entryPrefixInDict);
		if (!template.hasNamespace(entryNamespaceInDict))
			template.addNamespace(Namespaces.getNamespaceValue(entryNamespaceInDict));
		String entryPrefixInTemplate = template.getPrefixToNamespace(entryNamespaceInDict);
		
		return entryPrefixInTemplate + ":" + entrySuffix;
	}
}
