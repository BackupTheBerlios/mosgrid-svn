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

	private void init(MosgridUser user, TemplateDesignerApplication app) {
		_app = app;
		_mainWindow = new TDMainWindow();
		_tdMainView = new TDMainView();
		_user = user;
		
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
