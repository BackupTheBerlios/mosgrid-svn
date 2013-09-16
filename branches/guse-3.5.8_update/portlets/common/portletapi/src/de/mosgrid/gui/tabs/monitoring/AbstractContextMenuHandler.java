package de.mosgrid.gui.tabs.monitoring;

import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;
import hu.sztaki.lpds.pgportal.services.asm.constants.StatusConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;

import de.mosgrid.gui.tabs.monitoring.MonitoringTab.ItemProperty;
import de.mosgrid.gui.tabs.monitoring.MonitoringTab.ItemType;

public abstract class AbstractContextMenuHandler implements Handler {
	private static final long serialVersionUID = 7963526615406088821L;

	/* list of filetypes */
	// can be opened in raw text mode
	private final List<String> rawTextFormats = new ArrayList<String>();
	// can be opened witch chemdoodle
	private final List<String> chemdoodleFormats = new ArrayList<String>();
	// can be opened with jmol
	private final List<String> jmolFormats = new ArrayList<String>();
	// can be opened with dygraph
	private final List<String> graphFormats = new ArrayList<String>();
	// will be opened as Embedded instead of raw text
	private final List<String> imageFormats = new ArrayList<String>();

	/* right-click context menu */
	public static final Action ACTION_WKF_UPDATE = new Action("Update");
	public static final Action ACTION_WKF_DELETE = new Action("Delete");
	public static final Action ACTION_WKF_ABORT = new Action("Abort");

	public static final Action ACTION_FILE_OPEN = new Action("Open");
	public static final Action ACTION_FILE_DOWNLOAD = new Action("Download");
	public static final Action ACTION_FILE_CHEMDOODLE = new Action("Show Molecule");
	public static final Action ACTION_FILE_JMOL = new Action("Show Molecule (JMol)");
	public static final Action ACTION_FILE_DYGRAPH = new Action("Show Plot");

	private MonitoringTab monitoringTab;

	public void initialize(MonitoringTab tab) {
		this.monitoringTab = tab;
	}

	public MonitoringTab getMonitoringTab() {
		return monitoringTab;
	}

	@Override
	public void handleAction(Action action, Object sender, Object target) {
		if (monitoringTab != null) {
			Item targetItem = monitoringTab.getItem(target);
			if (targetItem != null) {
				ItemType itemType = (ItemType) targetItem.getItemProperty(ItemProperty.TYPE).getValue();

				if (itemType == ItemType.WORKFLOW) {
					if (action == ACTION_WKF_UPDATE) {
						monitoringTab.updateWorkflow(target);
					} else if (action == ACTION_WKF_ABORT) {
						monitoringTab.abortWorkflow(target);
					} else if (action == ACTION_WKF_DELETE) {
						monitoringTab.deleteWorkflow(target);
					}
				} else if (itemType == ItemType.FILE) {
					String filename = (String) targetItem.getItemProperty(ItemProperty.NAME).getValue();
					String fullFilePath = (String) targetItem.getItemProperty(ItemProperty.XFS_PATH).getValue();
					if (action == ACTION_FILE_OPEN) {
						boolean openAsImage = false;
						for (String filetype : imageFormats) {
							if (filename.endsWith(filetype)) {
								openAsImage = true;
								break;
							}
						}
						if (openAsImage) {
							monitoringTab.openAndShowImage(filename, fullFilePath);
						} else {
							monitoringTab.openAndShowFile(filename, fullFilePath);
						}
					} else if (action == ACTION_FILE_DOWNLOAD) {
						monitoringTab.download(filename, fullFilePath);
					} else if (action == ACTION_FILE_CHEMDOODLE) {
						monitoringTab.openWithChemdoodle(filename, fullFilePath);
					} else if (action == ACTION_FILE_JMOL) {
						monitoringTab.openWithJMOL(filename, fullFilePath);
					} else if (action == ACTION_FILE_DYGRAPH) {
						monitoringTab.openWithDygraph(filename, fullFilePath);
					}
				}
			}
		} else {
			throw new RuntimeException("Context handler of monitoring tab was not initialized!");
		}
	}

	@Override
	public Action[] getActions(Object target, Object sender) {
		if (monitoringTab != null) {
			Item targetItem = monitoringTab.getItem(target);
			if (targetItem != null) {
				ItemType itemType = (ItemType) targetItem.getItemProperty(ItemProperty.TYPE).getValue();
				if (itemType == ItemType.WORKFLOW) {
					ArrayList<Action> actions = new ArrayList<Action>();
					actions.add(ACTION_WKF_UPDATE);

					ASMWorkflow wkfInstance = (ASMWorkflow) targetItem.getItemProperty(ItemProperty.DATA).getValue();
					if (wkfInstance != null) {
						String wkfStatus = wkfInstance.getStatusbean().getStatus();
						//make workflow deleteable for given statuses
						if (wkfStatus.equals(StatusConstants.getStatus(StatusConstants.FINISHED))
								|| wkfStatus.equals(StatusConstants.getStatus(StatusConstants.ERROR))
								|| wkfStatus.equals(StatusConstants.getStatus(StatusConstants.ABORTED))
								|| wkfStatus.equals(StatusConstants.getStatus(StatusConstants.CANCELLED))
								|| wkfStatus.equals(StatusConstants.getStatus(StatusConstants.SUSPENDED))
								|| wkfStatus.equals(StatusConstants.getStatus(StatusConstants.WORKFLOW_SUSPENDED))
								|| wkfStatus.equals(StatusConstants.getStatus(StatusConstants.WORKFLOW_SUSPENDING))) {
							actions.add(ACTION_WKF_DELETE);
						} else {
							//else abort wkf first
							actions.add(ACTION_WKF_ABORT);
						}
					}
					if (actions.size() <= 1) {
						// if something went wrong, give at least the possibility to delete
						actions.add(ACTION_WKF_DELETE);
					}
					
					return actions.toArray(new Action[actions.size()]);
				} else if (itemType == ItemType.FILE) {
					ArrayList<Action> actions = new ArrayList<Action>();

					String filename = (String) targetItem.getItemProperty(ItemProperty.NAME).getValue();
					if (filename != null) {
						for (String graphFileType : graphFormats) {
							if (filename.endsWith(graphFileType)) {
								actions.add(ACTION_FILE_DYGRAPH);
							}
						}
						for (String chemdoodleFileType : chemdoodleFormats) {
							if (filename.endsWith(chemdoodleFileType)) {
								actions.add(ACTION_FILE_CHEMDOODLE);
							}
						}
						// TODO reactivate jmol
						// for (String jmolFileType : jmolFormats) {
						// if (filename.endsWith(jmolFileType)) {
						// actions.add(ACTION_FILE_JMOL);
						// }
						// }
						for (String rawTextType : rawTextFormats) {
							if (filename.endsWith(rawTextType)) {
								actions.add(ACTION_FILE_OPEN);
							}
						}
						// if file has no file type at all, e.g. 'stderr' or 'stdout' allow to open it
						if (!actions.contains(ACTION_FILE_OPEN)) {
							if (!filename.contains(".")) {
								actions.add(ACTION_FILE_OPEN);
							}
						}
					}

					actions.add(ACTION_FILE_DOWNLOAD);

					return actions.toArray(new Action[actions.size()]);
				}
			}
		} else {
			throw new RuntimeException("Context handler of monitoring tab was not initialized!");
		}
		return new Action[] {};
	}
	
	protected void addRawTextFormats(final String... formats) {
		addRawTextFormats(Arrays.asList(formats));
	}
	
	protected void addRawTextFormats(final Collection<String> formats) {
		addFormats(rawTextFormats, formats);
	}
	
	protected void addChemdoodleFormats(final String... formats) {
		addChemdoodleFormats(Arrays.asList(formats));
	}
	
	protected void addChemdoodleFormats(final Collection<String> formats) {
		addFormats(chemdoodleFormats, formats);
	}
	
	protected void addJmolFormats(final String... formats) {
		addJmolFormats(Arrays.asList(formats));
	}
	
	protected void addJmolFormats(final Collection<String> formats) {
		addFormats(jmolFormats, formats);
	}
	
	protected void addGraphFormats(final String... formats) {
		addGraphFormats(Arrays.asList(formats));
	}
	
	protected void addGraphFormats(final Collection<String> formats) {
		addFormats(graphFormats, formats);
	}
	
	protected void addImageFormats(final String... formats) {
		addImageFormats(Arrays.asList(formats));
	}
	
	protected void addImageFormats(final Collection<String> formats) {
		addFormats(imageFormats, formats);
	}
	
	private void addFormats(final List<String> formatList, final Collection<String> formats) {
		for (final String format : formats) {
			formatList.add(format);
		}
	}
}
