package de.mosgrid.gui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * The idea to this layout comes from java.awt.CardLayout. This ComponentContainer can be imagined as a deck of cards
 * (child components) where only one card can be shown at a time and lies on top. This is useful when switching
 * fast/often between child components to be shown. Furthermore it is much faster than replaceComponent(). This layout
 * is realized by a TabSheet which just hides its tabs. Bringing cards (components) to front thus means to change the
 * selected tab.
 * 
 * @author Andreas Zink
 * 
 */
public class CardLayout extends CustomComponent {
	/* constants */
	private static final long serialVersionUID = 8094689449590827480L;

	/* instance variables */
	private Component emptyComponent;
	private VerticalLayout mainLayout;
	private Panel mainPanel;
	private VerticalLayout panelLayout;
	private TabSheet tabSheet;
	private Map<Component, String> component2CaptionMap;
	// indicates whether each child keeps its caption or caption given in constructor is used
	private boolean useSingleCaption;
	// the fixed caption if specified
	private String fixedCaption;
	// indicates whether the empty component shall have a caption or not
	private boolean captionForEmptyComponent;

	public CardLayout() {
		init();
	}

	public CardLayout(String caption) {
		init();
		setCaption(caption);
	}

	/**
	 * Shows a border around layout i.e. switches between normal-panel and light-panel style. The light style is used by
	 * default.
	 */
	public void setBorder(boolean enabled) {
		if (enabled) {
			this.mainPanel.setStyleName(null);
		} else {
			this.mainPanel.setStyleName(Reindeer.PANEL_LIGHT);
		}
	}

	/**
	 * Enable layout margin. Off by default.
	 */
	public void setMargin(boolean enabled) {
		this.mainLayout.setMargin(enabled);
	}

	/**
	 * Enable layout margin. Off by default.
	 */
	public void setMargin(boolean topEnabled, boolean rightEnabled, boolean bottomEnabled, boolean leftEnabled) {
		this.mainLayout.setMargin(topEnabled, rightEnabled, bottomEnabled, leftEnabled);
	}

	/**
	 * Enable margin between caption and content. Off by default.
	 */
	public void setInnerMargin(boolean enabled) {
		this.panelLayout.setMargin(enabled);
	}

	/**
	 * Enable margin between caption and content. Off by default.
	 */
	public void setInnerMargin(boolean topEnabled, boolean rightEnabled, boolean bottomEnabled, boolean leftEnabled) {
		this.panelLayout.setMargin(topEnabled, rightEnabled, bottomEnabled, leftEnabled);
	}

	/**
	 * If CardLayouts caption is set, it will be shown permanently for all child components
	 */
	public void setCaption(String caption) {
		this.fixedCaption = caption;
		this.useSingleCaption = true;
		mainPanel.setCaption(caption);
	}

	public boolean isCaptionForEmptyComponent() {
		return captionForEmptyComponent;
	}

	public void setCaptionForEmptyComponent(boolean captionForEmptyComponent) {
		this.captionForEmptyComponent = captionForEmptyComponent;
	}

	private void init() {
		setImmediate(true);
		// build outer layout
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		mainLayout.setImmediate(true);
		// build main panel for caption
		panelLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		panelLayout.setImmediate(true);
		mainPanel = new Panel(panelLayout);
		mainPanel.setImmediate(true);
		mainPanel.setSizeFull();
		setBorder(false);
		mainLayout.addComponent(mainPanel);

		// child component caption mapping
		component2CaptionMap = new HashMap<Component, String>();

		initTabSheet();
		mainPanel.addComponent(tabSheet);

		setSizeFull();

		setCompositionRoot(mainLayout);
	}

	private void initTabSheet() {
		tabSheet = new TabSheet();
		tabSheet.hideTabs(true);
		tabSheet.setImmediate(true);
		tabSheet.setStyleName(Reindeer.TABSHEET_MINIMAL);
		tabSheet.setSizeFull();

		initEmptyLabel();
	}

	private void initEmptyLabel() {
		emptyComponent = new Label("");
		emptyComponent.setHeight("10px");
		addComponent(emptyComponent);
	}

	@Override
	public void addComponent(Component c) {
		if (c != null && !contains(c)) {
			// save caption and delete from c
			component2CaptionMap.put(c, c.getCaption());
			c.setCaption(null);
			// add c to tabsheet
			tabSheet.addTab(c);
		}
	}

	@Override
	public void removeAllComponents() {
		for (Component c : component2CaptionMap.keySet()) {
			removeComponent(c);
		}
	}

	@Override
	public void removeComponent(Component c) {
		if (c != emptyComponent) {
			String caption = component2CaptionMap.get(c);
			// give back caption
			c.setCaption(caption);
			tabSheet.removeComponent(c);
		}
	}

	/**
	 * Brings the given component to front. If c is not contained, the EMPTY_LABEL is shown
	 * 
	 * @param c
	 *            The component which shall be brought to front
	 */
	public void showComponent(Component c) {
		if (c != null && contains(c)) {
			tabSheet.setSelectedTab(c);
			tabSheet.setSizeFull();
			updateCaption(c);
		} else {
			tabSheet.setSelectedTab(emptyComponent);
			updateCaption(emptyComponent);
		}

	}

	/**
	 * Checks wether given component is contained
	 * 
	 * @return 'true' if c is contained
	 */
	public boolean contains(Component c) {
		if (c != null) {
			Iterator<Component> it = tabSheet.getComponentIterator();
			while (it.hasNext()) {
				Component next = it.next();
				if (next == c) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Sets the caption of this component to the caption of current shown component
	 */
	private void updateCaption(Component c) {
		if (!useSingleCaption) {
			// use components caption
			String caption = component2CaptionMap.get(c);
			this.mainPanel.setCaption(caption);
		} else {
			// set the fixed caption
			if (this.mainPanel.getCaption() == null) {
				this.mainPanel.setCaption(fixedCaption);
			}
		}
		if (c == emptyComponent && !captionForEmptyComponent) {
			// delete caption for empty component
			this.mainPanel.setCaption(null);
		}
	}

	/**
	 * Show next Component in container
	 */
	public void showNext() {
		Component selectedComponent = tabSheet.getSelectedTab();
		Tab selectedTab = tabSheet.getTab(selectedComponent);
		int selectedIndex = tabSheet.getTabPosition(selectedTab);
		int nextIndex = computeNextIndex(selectedIndex);
		Component nextComponent = tabSheet.getTab(nextIndex).getComponent();
		showComponent(nextComponent);
	}

	/**
	 * Calculates the index of the next component
	 */
	private int computeNextIndex(int selectedIndex) {
		int nextIndex = ++selectedIndex;
		if (nextIndex >= tabSheet.getComponentCount()) {
			nextIndex = 0;
		}
		return nextIndex;
	}

	/**
	 * Sets the empty label. The empty label is shown if one tries to show a component which is not contained in the
	 * layout.
	 */
	public void setEmptyComponent(Component c) {
		if (c != null) {
			this.tabSheet.removeComponent(emptyComponent);
			addComponent(c);
			showComponent(c);
			this.emptyComponent = c;
		}
	}

	/**
	 * @return The empty component
	 */
	public Component getEmptyComponent() {
		return this.emptyComponent;
	}

	/**
	 * @return The currently shown component
	 */
	public Component getComponent() {
		return tabSheet.getSelectedTab();
	}
}