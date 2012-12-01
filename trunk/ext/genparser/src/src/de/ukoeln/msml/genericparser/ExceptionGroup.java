package de.ukoeln.msml.genericparser;

import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ExceptionGroup extends ThreadGroup {
	public ExceptionGroup() {
		super("ExceptionGroup");
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		JOptionPane.showMessageDialog(findActiveFrame(), e.toString(), "Exception Occurred", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}

	/**
	 * I hate ownerless dialogs. With this method, we can find the currently
	 * visible frame and attach the dialog to that, instead of always attaching
	 * it to null.
	 */
	private Frame findActiveFrame() {
		Frame[] frames = JFrame.getFrames();
		for (int i = 0; i < frames.length; i++) {
			Frame frame = frames[i];
			if (frame.isVisible()) {
				return frame;
			}
		}
		return null;
	}
}