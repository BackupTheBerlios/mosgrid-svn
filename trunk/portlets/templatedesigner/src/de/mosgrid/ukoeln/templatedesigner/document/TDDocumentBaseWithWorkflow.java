package de.mosgrid.ukoeln.templatedesigner.document;

import hu.sztaki.lpds.pgportal.services.asm.ASMService;
import hu.sztaki.lpds.pgportal.services.asm.beans.ASMRepositoryItemBean;
import hu.sztaki.lpds.pgportal.services.asm.constants.RepositoryItemTypeConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.vaadin.data.util.BeanItemContainer;

import de.mosgrid.ukoeln.templatedesigner.gui.TDMainWindow;
import de.mosgrid.ukoeln.templatedesigner.gui.TDViewBaseNonGeneric;
import de.mosgrid.util.ImportID;

public abstract class TDDocumentBaseWithWorkflow<T extends TDViewBaseNonGeneric> extends TDDocumentBase<T> {
	private final BeanItemContainer<String> _notes = new BeanItemContainer<String>(String.class);
	private static final BeanItemContainer<String> _repoWorkflows = new BeanItemContainer<String>(String.class);
	private static final Hashtable<String, List<ASMRepositoryItemBean>> _workflows = new Hashtable<String, List<ASMRepositoryItemBean>>();
	private static final List<ASMRepositoryItemBean> _allWorkflows = new ArrayList<ASMRepositoryItemBean>();
	private static boolean isInit = false;

	public TDDocumentBaseWithWorkflow(TDMainWindow mainWindow) {
		super(mainWindow);
	}

	@Override
	final void doInit() {
		
		if (isInit) {
			doOnInit();
			return;
		}

		updateRepoWorkflows();

		doOnInit();
		isInit = true;
	}

	protected void updateRepoWorkflows() {
		try {
			_workflows.clear();
			_allWorkflows.clear();
			_repoWorkflows.removeAllItems();
			Vector<ASMRepositoryItemBean> workflows = ASMService.getInstance().getWorkflowsFromRepository(
					getUser().getUserID(), RepositoryItemTypeConstants.Workflow);
			for (ASMRepositoryItemBean wkfl : workflows) {
				if (!_workflows.containsKey(wkfl.getItemID()))
					_workflows.put(wkfl.getItemID(), new ArrayList<ASMRepositoryItemBean>());
				_workflows.get(wkfl.getItemID()).add(wkfl);
				_allWorkflows.add(wkfl);
			}
			ArrayList<String> keys = new ArrayList<String>(_workflows.keySet());
			Collections.sort(keys);
			_repoWorkflows.addAll(keys);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	abstract void doOnInit();

	public BeanItemContainer<String> getNotesDataSource() {
		return _notes;
	}

	public BeanItemContainer<String> getAllRepoWorkflowItemIdsDataSource() {
		return _repoWorkflows;
	}
	
	public List<ASMRepositoryItemBean> getAllRepoWorkflowItemBeans() {
		return _allWorkflows;
	}

	public void updateNotes(String value) {
		_notes.removeAllItems();
		if (value == null)
			return;
		List<ASMRepositoryItemBean> wkfls = _workflows.get(value);
		if (wkfls == null)
			return;
		List<String> beans = new ArrayList<String>();
		for (ASMRepositoryItemBean wkfl : wkfls) {
			String note = ImportID.escapeNotes(wkfl.getExportText());
			if (!"".equals(note))
				beans.add(note);
		}
		_notes.addAll(beans);
	}
	
	@Override
	public void onSave() {
		
	}
}
