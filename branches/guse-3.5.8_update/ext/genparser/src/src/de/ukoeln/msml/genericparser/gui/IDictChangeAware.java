package de.ukoeln.msml.genericparser.gui;

import java.util.List;

import de.ukoeln.msml.genericparser.gui.interfaces.swingimpl.DictTableModelSwingImpl.DictRefData;

public interface IDictChangeAware {
	void dictsChanged(List<DictRefData> data);
}
