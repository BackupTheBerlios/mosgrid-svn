package de.mosgrid.chemdoodle.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 'ribbons' element of json notation. This element consists of residue chains.
 * 
 * @author Andreas Zink
 * 
 */
public class JsonRibbonsElement {

	private List<JsonChain> chains = new ArrayList<JsonChain>();

	public void addChains(Collection<JsonChain> chains) {
		this.chains.addAll(chains);
	}

	public void addChain(JsonChain c) {
		this.chains.add(c);
	}

	public List<JsonChain> getChains() {
		return this.chains;
	}

	@Override
	public String toString() {
		final String COMMA = ",";
		 StringBuilder stringBuilder = new StringBuilder(250);
		stringBuilder.append("'ribbons':{'cs':[");
		for (JsonChain c : chains) {
			stringBuilder.append(c.toString() + COMMA);
		}
		// remove last comma
		if (chains.size() > 0) {
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		}
		stringBuilder.append("]}");
		return stringBuilder.toString();
	}

}
