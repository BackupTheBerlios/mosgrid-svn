package de.mosgrid.chemdoodle.widget;

/**
 * Listener on some important canvas events
 * 
 * @author Andreas Zink
 * 
 */
public interface ICanvasListener {

	/**
	 * Gets called if no support for HTML5 canvas or WebGl is detected
	 */
	public void noWebGlSupportDetected();

}
