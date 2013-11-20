package org.xtreemfs.portlet.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Der Upload-Dialog für Vaadin 
 */
public class UploadDialog  extends Window implements Button.ClickListener{
  private static final long serialVersionUID = 9175370535054503678L;
  
  public Button cancelProcessing;
  public Upload upload;
  
  public ProgressIndicator pi = new ProgressIndicator();
  public Label state = new Label();
  public Label fileName = new Label();
  public Label textualProgress = new Label();

  @SuppressWarnings("serial")
  public UploadDialog (final Upload u) {
    super("Upload");
    this.upload = u;
    
    // VerticalLayout Layout
    VerticalLayout layout = (VerticalLayout) getContent();
    layout.setMargin(true);
    layout.setSpacing(true);
    layout.setSizeUndefined();
    
    // Abbrech-Knöpfe
    cancelProcessing = new Button("Cancel");
    cancelProcessing.addListener(new Button.ClickListener() {
        public void buttonClick(ClickEvent event) {
            u.interruptUpload();
        }
    });
    cancelProcessing.setVisible(false);
    cancelProcessing.setStyleName("small");
     
    Panel p = new Panel("Status");
    p.setSizeUndefined();
    FormLayout l = new FormLayout();
    l.setMargin(true);
    p.setContent(l);
    HorizontalLayout stateLayout = new HorizontalLayout();
    stateLayout.setSpacing(true);
    stateLayout.addComponent(state);
    stateLayout.addComponent(cancelProcessing);    
    stateLayout.setCaption("Current state");
    state.setValue("Idle");
    l.addComponent(stateLayout);
    fileName.setCaption("File name");
    l.addComponent(fileName);
    pi.setCaption("Progress");
    pi.setVisible(false);
    l.addComponent(pi);
    textualProgress.setVisible(false);
    l.addComponent(textualProgress);
    
    addComponent(p);
  }
  
  @Override
  public void buttonClick(ClickEvent event) {

  }

}
