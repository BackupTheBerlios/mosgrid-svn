package de.mosgrid.ukoeln.templatedesigner.gui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class TDInitialMinSizePanel<T extends CustomComponent & IInitialMinSizePane> extends TDInitialMinSizePanelBase {
	
	@AutoGenerated
	private VerticalLayout mainLayout;

	@AutoGenerated
	private Panel pnlRoot;

	@AutoGenerated
	private VerticalLayout vlRoot;

	private T _wrapped;

	private static final long serialVersionUID = -5161789779936643780L;

	private static final int MINSIZE = 400;

	/**
	 * The constructor should first build the main layout, set the composition
	 * root and then do any custom initialization.
	 * 
	 * The constructor will not be automatically regenerated by the visual
	 * editor.
	 */
	public TDInitialMinSizePanel() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	public TDInitialMinSizePanel(T wrapped) {
		this();
		_wrapped = wrapped;
		pnlRoot.addComponent(_wrapped);
		setCaption(_wrapped.getCaption());
		wrapped.setCaption(null);
		String wrappedHeight = getWrappedHeight();
		wrapped.setHeight(wrappedHeight);
		wrapped.getMainLayout().setHeight(wrappedHeight);
		vlRoot.setHeight(wrappedHeight);
	}

	private String getWrappedHeight() {
		String h = TDMainWindow.MAINVIEW_HEIGHT;
		Matcher m = Pattern.compile("(\\d+)(.+)").matcher(h);
		if (!m.find())
			System.out.println("Something is seriously wrong.");
		int ih = Integer.parseInt(m.group(1));
		return Math.max(((int) (ih - (ih * 0.1))), MINSIZE) + m.group(2);
	}
	
	protected T getWrapped() {
		return _wrapped;
	}

	@Override
	public void onClose() {
		if (_wrapped instanceof TDCloseListener)
			((TDCloseListener)_wrapped).onClose();
	}

	@AutoGenerated
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");

		// pnlRoot
		pnlRoot = buildPnlRoot();
		mainLayout.addComponent(pnlRoot);

		return mainLayout;
	}

	@AutoGenerated
	private Panel buildPnlRoot() {
		// common part: create layout
		pnlRoot = new Panel();
		pnlRoot.setImmediate(false);
		pnlRoot.setWidth("100.0%");
		pnlRoot.setHeight("100.0%");

		// vlRoot
		vlRoot = new VerticalLayout();
		vlRoot.setImmediate(false);
		vlRoot.setWidth("100.0%");
		vlRoot.setHeight("100.0%");
		vlRoot.setMargin(false);
		pnlRoot.setContent(vlRoot);

		return pnlRoot;
	}
}
