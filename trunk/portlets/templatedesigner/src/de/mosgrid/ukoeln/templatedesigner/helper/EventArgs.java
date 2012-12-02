package de.mosgrid.ukoeln.templatedesigner.helper;

public class EventArgs<A extends Object> extends EventArgsNonGeneric {

	private A _param;

	public EventArgs() {
		super();
	}

	public EventArgs(A param) {
		this();
		_param = param;
	}

	public A getParam() {
		return _param;
	}
}