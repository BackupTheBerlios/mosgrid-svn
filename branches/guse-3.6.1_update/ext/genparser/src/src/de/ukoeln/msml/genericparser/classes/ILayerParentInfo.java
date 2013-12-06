package de.ukoeln.msml.genericparser.classes;

import de.ukoeln.msml.genericparser.GenericParserDocumentBase;
import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.classes.visitors.LayerExtensionVisitor;

public interface ILayerParentInfo {

	void fillStringsToSplit(LayerExtensionVisitor vis, MSMLTreeValueBase parVal, GenericParserDocumentBase doc);

	boolean canAddChild(MSMLTreeValueBase value);
}
