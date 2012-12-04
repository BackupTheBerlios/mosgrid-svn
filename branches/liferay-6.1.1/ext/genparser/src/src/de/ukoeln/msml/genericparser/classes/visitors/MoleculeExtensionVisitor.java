package de.ukoeln.msml.genericparser.classes.visitors;

import java.math.BigInteger;

import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.gui.extension.MSMLMoleculeExtensionComponent.ParseMethod;
import de.ukoeln.msml.genericparser.worker.StringH;

public class MoleculeExtensionVisitor extends MSMLExtensionVisitor {

	private String _title;
	private BigInteger _spin;
	private BigInteger _charge;
	private String _id;
	private ParseMethod _method;

	public MoleculeExtensionVisitor(MSMLTreeValueBase val) {
		super(val);
	}

	public void setTitle(String text) {
		_title = text;
	}

	public void setID(String text) {
		_id = text;
	}

	public void setSpin(String text) {
		_spin = StringH.toBigInteger(text);
	}

	public void setCharge(String text) {
		_charge = StringH.toBigInteger(text);
	}
	
	public void setMethod(ParseMethod method) {
		_method = method;
	}

	public BigInteger getCharge() {
		return _charge;
	}

	public String getId() {
		return _id;
	}

	public String getTitle() {
		return _title;
	}

	public BigInteger getSpin() {
		return _spin;
	}

	public ParseMethod getMethod() {
		return _method;
	}
}
