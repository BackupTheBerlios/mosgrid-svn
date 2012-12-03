package de.mosgrid.ukoeln.templatedesigner;

import de.mosgrid.portlet.MosgridUser;
import de.mosgrid.ukoeln.templatedesigner.document.TDDocumentBase;
import de.mosgrid.ukoeln.templatedesigner.document.TDDocumentBaseNonGeneric;
import de.mosgrid.ukoeln.templatedesigner.document.TDMainDocument;
import de.mosgrid.ukoeln.templatedesigner.gui.TDInitialMinSizePanel;
import de.mosgrid.ukoeln.templatedesigner.gui.TDMainTab;
import de.mosgrid.ukoeln.templatedesigner.gui.TDMainView;
import de.mosgrid.ukoeln.templatedesigner.gui.TDMainWindow;
import de.mosgrid.ukoeln.templatedesigner.gui.TDViewBase;
import de.mosgrid.ukoeln.templatedesigner.gui.TDViewBaseNonGeneric;

/**
 * The DocumentManager creates views and corresponding documents. For each document there is exactly one view.
 * The view is the GUI and the document contains all data and handling logic.
 *  
 * @author mkruse0
 *
 */
public class TDDocumentManager {

	private TDMainWindow _mainWindow;
	private TDMainView _tdMainView;
	private MosgridUser _user;
	private boolean _isInit = false;
	private TDMainDocument _mainDoc;
	private TemplateDesignerApplication _app;

	public TDDocumentManager(MosgridUser user, TemplateDesignerApplication app) {
		if (_isInit)
			throw new UnsupportedOperationException("This method may only be called once!");
		init(user, app);
	}

	/**
	 * Initializes the manager and stores user and reference to main application.
	 * Instantly creates the main view, which contains domain selection, templates etc. 
	 * 
	 * @param user The user of the current session.
	 * @param app The main application.
	 */
	private void init(MosgridUser user, TemplateDesignerApplication app) {
		_app = app;
		_mainWindow = new TDMainWindow();
		_tdMainView = new TDMainView();
		_user = user;
		
		// create main view. other invokes of this kind are done when opening a template
		_mainDoc = createAndAddView(new TDViewParam<TDMainTab, TDMainDocument>() {

			@Override
			public TDMainDocument createDoc(TDDocumentManager man) {
				return new TDMainDocument(man.getMainWindow(), _app);
			}

			@Override
			public TDMainTab doCreateView() {
				return new TDMainTab();
			}
		});
		_mainWindow.setMainView(_tdMainView);
	}

	public TDMainWindow getMainWindow() {
		return _mainWindow;
	}

	public TDMainDocument getMainDocument() {
		return _mainDoc;
	}

	public <T extends TDViewBase<?>, D extends TDDocumentBase<?>> D createAndAddView(
			TDViewParam<T, D> param) {
		D doc = param.createDoc(this);
		TDInitialMinSizePanel<T> wrappedView = param.createWrappedView();
		T view = param.getView();
		doc.init(view, _user);
		view.init(doc);
		_tdMainView.addTab(wrappedView, param.getIsClosable());
		return doc;
	}

	/**
	 * This class is the base of parameters needed to create view and document.
	 * 
	 * @author mkruse0
	 *
	 * @param <T> Type of view.
	 * @param <D> Type of document.
	 */
	public abstract class TDViewParam<T extends TDViewBaseNonGeneric, D extends TDDocumentBaseNonGeneric> {
	
		private T _view;
	
		public abstract D createDoc(TDDocumentManager man);
	
		protected T createView() {
			_view = doCreateView();
			return _view;
		}
	
		public abstract T doCreateView();
	
		public TDInitialMinSizePanel<T> createWrappedView() {
			return new TDInitialMinSizePanel<T>(createView());
		}
	
		public T getView() {
			return _view;
		}
	
		public boolean getIsClosable() {
			return false;
		}
	}
}
