package de.mosgrid.chemdoodle;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.mosgrid.chemdoodle.converter.JsonConversionException;
import de.mosgrid.chemdoodle.converter.pdb.PDB2JSONConverter;
import de.mosgrid.chemdoodle.test.Proteins;
import de.mosgrid.chemdoodle.ui.PdbViewer;
import de.mosgrid.chemdoodle.widget.DefType;

/**
 * This application is just for development & testing
 * 
 * @author Andreas Zink
 * 
 */
public class ChemdoodleDemo extends Application implements Upload.Receiver, Upload.SucceededListener,
		Upload.FailedListener {
	private static final long serialVersionUID = 9128122978064692054L;
	private Window mainWindow;
	private PdbViewer viewer;
	private PdbViewer viewer2;
	private File tempPDB;
	//test
	@Override
	public void init() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		mainWindow = new Window("Chemdoodle Demo", mainLayout);

		// welcome msg
		Label msg = new Label(
				"Hello,<br>this portlet demonstrates the integration of ChemDoodle into MoSGrid. Please upload a *.pdb file or load the demo data to view a molecule.");
		msg.setContentMode(Label.CONTENT_RAW);
		mainWindow.addComponent(msg);

		// button container
		buildButtonContainer(mainWindow);

		// molecule viewer
		this.viewer = new PdbViewer();
		mainWindow.addComponent(viewer);
		
		this.viewer2 = new PdbViewer();
		mainWindow.addComponent(viewer2);

		setMainWindow(mainWindow);
	}

	private void buildButtonContainer(Window mainWindow) {
		HorizontalLayout buttonContainer = new HorizontalLayout();
		buttonContainer.setSpacing(true);

		// pdb upload
		Upload uploadField = new Upload(null, this);
		uploadField.setButtonCaption("Upload PDB");
		uploadField.setDescription("Please upload a *.pdb file");
		uploadField.addListener((Upload.SucceededListener) this);
		uploadField.addListener((Upload.FailedListener) this);
		uploadField.setImmediate(true);
		buttonContainer.addComponent(uploadField);

		// pdb Buttons
		Button pdb1Btn = new Button("101M");
		pdb1Btn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -8349995224726116049L;

			@Override
			public void buttonClick(ClickEvent event) {
				viewer.getCanvas().setMoleculeDefinition(Proteins.PDB_101M, DefType.JSON);
			}
		});
		buttonContainer.addComponent(pdb1Btn);

		Button pdb2Btn = new Button("3N4B");
		pdb2Btn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -4486042344213428474L;

			@Override
			public void buttonClick(ClickEvent event) {
				viewer2.getCanvas().setMoleculeDefinition(Proteins.PDB_3N4B, DefType.JSON);
			}
		});
		buttonContainer.addComponent(pdb2Btn);

		Button pdb3Btn = new Button("1D66");
		pdb3Btn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -7436149547728134544L;

			@Override
			public void buttonClick(ClickEvent event) {
				viewer.getCanvas().setMoleculeDefinition(Proteins.PDB_1D66, DefType.JSON);
			}
		});
		buttonContainer.addComponent(pdb3Btn);

		mainWindow.addComponent(buttonContainer);
	}

	@Override
	public void uploadFailed(FailedEvent event) {
		this.getMainWindow().showNotification("Upload failed!");

	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {	
		FileInputStream fis;
		try {
			fis = new FileInputStream(tempPDB);
			PDB2JSONConverter c = new PDB2JSONConverter();
			String json = c.convert(fis, null);
			
			viewer.getCanvas().setMoleculeDefinition(json, DefType.JSON);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonConversionException e) {
			mainWindow.showNotification("Failed!", "<br>"+e.getMessage(), Notification.TYPE_ERROR_MESSAGE, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		if (filename.endsWith(".pdb")) {
			FileOutputStream fos = null;
			try {
				tempPDB = File.createTempFile("tempPDB",".pdb");
				fos = new FileOutputStream(tempPDB);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new BufferedOutputStream(fos);
		}
		// else {
		// this.getMainWindow().showNotification("Please select *.pdb files only!");
		// return new OutputStream() {
		// @Override
		// public void write(int b) throws IOException {
		//
		// }
		// };
		// }
		return null;
	}

	// @Override
	// public OutputStream receiveUpload(String filename, String mimeType) {
	// if (filename.endsWith(".pdb")) {
	// parser = new PdbParser();
	// OutputStream os = new OutputStream() {
	// private StringBuilder lineBuffer = new StringBuilder();
	// private static final int newLineByte = '\n';
	//
	// @Override
	// public void write(int b) throws IOException {
	// if (b == newLineByte) {
	// parser.parseLine(lineBuffer.toString());
	// lineBuffer = new StringBuilder();
	// } else {
	// lineBuffer.append((char) b);
	// }
	// }
	// };
	// return new BufferedOutputStream(os);
	// } else {
	// this.getMainWindow().showNotification("Please select *.pdb files only!");
	// return new OutputStream() {
	// @Override
	// public void write(int b) throws IOException {
	//
	// }
	// };
	// }
	// }

}
