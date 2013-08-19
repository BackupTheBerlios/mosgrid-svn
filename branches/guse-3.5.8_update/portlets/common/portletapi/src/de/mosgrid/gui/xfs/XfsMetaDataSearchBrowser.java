package de.mosgrid.gui.xfs;

import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import de.mosgrid.metadatasearch.IMetaDataSearcher;

public class XfsMetaDataSearchBrowser extends XfsFileBrowser {
	private static final long serialVersionUID = 8263867159819171048L;
	
	private HorizontalLayout searchLayout;
	private TextField searchTextField;

	private IMetaDataSearcher searcher;

	public XfsMetaDataSearchBrowser(IMetaDataSearcher searcher) {
		this.searcher=searcher;
		buildMainLayout();
	}

	private void buildMainLayout() {

		searchLayout = new HorizontalLayout();
		searchLayout.setSpacing(true);
		
		searchTextField = new TextField("Search");
		searchLayout.addComponent(searchTextField);

		Button searchBtn = new Button("Start search");
		searchBtn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 834987454830080195L;

			@Override
			public void buttonClick(ClickEvent event) {
				clickedSearchButton();

			}
		});
		searchLayout.addComponent(searchBtn);
		mainLayout.addComponentAsFirst(searchLayout);
		
		setCompositionRoot(mainLayout);

	}

	protected void clickedSearchButton() {
		String searchString = searchTextField.getValue().toString();
		List<String> searchResults = searcher.searchFor(searchString);
		loadPaths(searchResults);
	}

}
