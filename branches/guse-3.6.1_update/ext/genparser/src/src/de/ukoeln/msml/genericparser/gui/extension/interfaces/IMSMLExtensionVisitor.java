package de.ukoeln.msml.genericparser.gui.extension.interfaces;

import de.ukoeln.msml.genericparser.MSMLTreeValueBase;

public interface IMSMLExtensionVisitor {

	void setText(String text);

	String getText();

	MSMLTreeValueBase getMSMLTreeValue();

}
