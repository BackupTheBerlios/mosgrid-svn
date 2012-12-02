package de.mosgrid.ukoeln.templatedesigner.helper;

public abstract class EventListener<T extends EventArgsNonGeneric> {

	public abstract void eventHappened(T param);
}