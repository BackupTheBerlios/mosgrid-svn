package de.ukoeln.msml.genericparser.gui.extension;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;

public class MSMLRegexExtensionComponent extends JPanel 
	implements IMSMLParserComponent {

	private static final long serialVersionUID = 1L;
	private ArrayList<MSMLRegexExtensionComponentPart> _parts = new ArrayList<MSMLRegexExtensionComponentPart>();
	private MSMLRegexExtension _ext;
	
	public MSMLRegexExtensionComponent(MSMLRegexExtension ext) {
		this();
		_ext = ext;
	}
	
	public MSMLRegexExtensionComponent() {
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setMaximumSize(new Dimension(32767, 105));
		setLayout(new GridLayout(0, 1, 0, 5));

		MSMLRegexExtensionComponentPart pnlRegexPart1 = new MSMLRegexExtensionComponentPart();
		add(pnlRegexPart1);
		_parts.add(pnlRegexPart1);
		
		MSMLRegexExtensionComponentPart pnlRegexPart2 = new MSMLRegexExtensionComponentPart();
		add(pnlRegexPart2);
		_parts.add(pnlRegexPart2);
		
		MSMLRegexExtensionComponentPart pnlRegexPart3 = new MSMLRegexExtensionComponentPart();
		add(pnlRegexPart3);
		_parts.add(pnlRegexPart3);
	}
//
//	@Override
//	public void clear() {
//		for (MSMLRegexExtensionComponentPart part : _parts) {
//			part.clear();
//		}
//	}

	@Override
	public void setChildComponentsEnabled(boolean enabled) {
		for (MSMLRegexExtensionComponentPart item : getPartArray()) {
			item.setChildComponentsEnabled(enabled);
		}
	}
	
	private MSMLRegexExtensionComponentPart[] getPartArray() {
		MSMLRegexExtensionComponentPart[] parts = new MSMLRegexExtensionComponentPart[_parts.size()];
		_parts.toArray(parts);
		return parts;
	}

	public int getCount() {
		return _parts.size();
	}

	public boolean isSet(int i) {
		return _parts.get(i).isSet();
	}

	public String getRegex(int i) {
		return _parts.get(i).getRegex();
	}

	public void setRegex(int i, String string) {
		_parts.get(i).setRegex(string);
	}

	@Override
	public void updateValuesInExtension() {
		_ext.updateValues();
	}
}
