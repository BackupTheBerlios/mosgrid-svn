package de.mosgrid.gui.inputmask;

import java.util.Iterator;

import com.vaadin.ui.Component;

import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.util.UploadCollector;

/**
 * A default implementation of an Input-Mask. Can be filled with InputMaskComponents.
 * 
 * @author Andreas Zink
 * 
 */
public class DefaultInputMask extends AbstractInputMask {
	private static final long serialVersionUID = 3111407728776937430L;

	public DefaultInputMask(DomainPortlet portlet, ImportedWorkflow wkfImport) {
		super(portlet, wkfImport);
	}

	/**
	 * Adds a input mask componenten to this Input-Mask
	 */
	public void addComponent(IInputMaskComponent c) {
		if (c!=null) {
			getComponentContainer().addComponent(c);
		}
	}

	/**
	 * Removes all added Job-Forms
	 */
	public void clear() {
		getComponentContainer().removeAllComponents();
	}

	@Override
	protected void beforeSubmitHook() {
		Iterator<Component> componentIterator = getComponentContainer().getComponentIterator();
		// iterate over all child components
		while (componentIterator.hasNext()) {
			Component child = componentIterator.next();
			if (child instanceof IInputMaskComponent) {
				IInputMaskComponent component = (IInputMaskComponent) child;
				component.beforeSubmit(this);
			}
		}
	}

	@Override
	public boolean commitAndValidate() {
		boolean isValid = true;
		Iterator<Component> componentIterator = getComponentContainer().getComponentIterator();
		// iterate over all child components
		while (componentIterator.hasNext()) {
			Component child = componentIterator.next();
			// validate all Job-Form components
			if (child instanceof IInputMaskComponent) {
				IInputMaskComponent validatableComponent = (IInputMaskComponent) child;
				if (!validatableComponent.commitAndValidate()) {
					isValid = false;
				}
			}
		}
		return isValid;
	}

	@Override
	protected void afterCommitAndValidateHook() {
		Iterator<Component> componentIterator = getComponentContainer().getComponentIterator();
		// iterate over all child components
		while (componentIterator.hasNext()) {
			Component child = componentIterator.next();
			if (child instanceof IInputMaskComponent) {
				IInputMaskComponent component = (IInputMaskComponent) child;
				component.afterCommitAndValidate(this);
			}
		}
	}

	@Override
	protected void collectUploads(UploadCollector collector) {
		Iterator<Component> componentIterator = getComponentContainer().getComponentIterator();
		// iterate over all child components
		while (componentIterator.hasNext()) {
			Component child = componentIterator.next();
			if (child instanceof IInputMaskComponent) {
				IInputMaskComponent component = (IInputMaskComponent) child;
				component.collectUploads(collector);
			}
		}
	}

	@Override
	protected void beforeRemoveHook() {
		Iterator<Component> componentIterator = getComponentContainer().getComponentIterator();
		// iterate over all child components
		while (componentIterator.hasNext()) {
			Component child = componentIterator.next();
			if (child instanceof IInputMaskComponent) {
				IInputMaskComponent component = (IInputMaskComponent) child;
				component.beforeRemove(this);
			}
		}

	}

}
