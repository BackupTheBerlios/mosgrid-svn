package de.mosgrid.ukoeln.templatedesigner.helper;

import java.util.ArrayList;
import java.util.List;

public class ListenerSupport<T extends EventArgsNonGeneric> {

	private final List<EventListener<T>> listenerList = new ArrayList<EventListener<T>>();

	public void addListener(EventListener<T> l) {
		listenerList.add(l);
	}

	public void removeListener(EventListener<T> l) {
		listenerList.remove(l);
	}

	public void fireEvent(T args) {
		for (EventListener<T> l : listenerList) {
			l.eventHappened(args);
		}
	}

	public void fireEvent() {
		fireEvent(null);
	}

}
