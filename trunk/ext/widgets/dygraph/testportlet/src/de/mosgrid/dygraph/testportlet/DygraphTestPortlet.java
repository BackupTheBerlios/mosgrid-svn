package de.mosgrid.dygraph.testportlet;

import java.io.IOException;

import java.io.OutputStream;

import com.vaadin.Application;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window.Notification;

import de.mosgrid.dygraph.ui.SimpleGraphViewer;
import de.mosgrid.dygraph.util.SimpleData;
import de.mosgrid.dygraph.util.parser.SimpleXVGParser;
import de.mosgrid.dygraph.widget.IData;

@SuppressWarnings("serial")
public class DygraphTestPortlet extends Application implements Upload.Receiver {
	private SimpleXVGParser parser;
	private SimpleGraphViewer viewer;
	private SimpleData demoData1;

	@Override
	public void init() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		Window mainWindow = new Window("Dygraph Demo", mainLayout);
		// msg
		Label msg = new Label(
				"Hello,<br>this portlet demonstrates the integration of Dygraph into MoSGrid. Please upload a *.xvg file or load the demo data to create a graph.");
		msg.setContentMode(Label.CONTENT_RAW);
		mainWindow.addComponent(msg);
		// buttons
		buildButtonContainer(mainWindow);

		// viewer
		viewer = new SimpleGraphViewer();
		mainWindow.addComponent(viewer);

		setMainWindow(mainWindow);
	}

	private void buildButtonContainer(Window mainWindow) {
		HorizontalLayout buttonContainer = new HorizontalLayout();
		buttonContainer.setSpacing(true);

		// upload button
		final Upload xvgUpload = new Upload(null, this);
		xvgUpload.setImmediate(true);
		xvgUpload.setButtonCaption("Upload XVG");
		xvgUpload.setDescription("Please upload a *.xvg file");
		xvgUpload.addListener(new Upload.SucceededListener() {

			@Override
			public void uploadSucceeded(SucceededEvent event) {
				xvgUpload.setComponentError(null);
				IData data = parser.getData();
				viewer.getGraph().setData(data);
			}
		});
		xvgUpload.addListener(new Upload.FailedListener() {

			@Override
			public void uploadFailed(FailedEvent event) {
				Notification notif = new Notification("Please upload .xvg files only!", Notification.TYPE_ERROR_MESSAGE);
				getMainWindow().showNotification(notif);
				xvgUpload.setComponentError(null);

			}
		});
		buttonContainer.addComponent(xvgUpload);

		// build demo data
		demoData1 = new DemoData1();
		Button demo1Btn = new Button("Demo");
		demo1Btn.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				demoData1.markAllAttributesDirty();
				viewer.getGraph().setData(demoData1);
			}
		});
		buttonContainer.addComponent(demo1Btn);

		mainWindow.addComponent(buttonContainer);

	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		OutputStream os = null;
		if (filename.endsWith(".xvg")) {
			parser = new SimpleXVGParser();
			os = new OutputStream() {
				private StringBuilder lineBuffer = new StringBuilder();
				private static final int newLineByte = '\n';

				@Override
				public void write(int b) throws IOException {
					if (b == newLineByte) {
						parser.parseLine(lineBuffer.toString());
						lineBuffer = new StringBuilder();
					} else {
						lineBuffer.append((char) b);
					}
				}
			};
		}
		return os;
	}

}
