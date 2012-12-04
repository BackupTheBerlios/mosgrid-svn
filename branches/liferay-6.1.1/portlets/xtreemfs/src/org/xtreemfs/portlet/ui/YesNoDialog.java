package org.xtreemfs.portlet.ui;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Ein einfacher Ja-Nein-Dialog für Vaadin
 */
public class YesNoDialog extends Window implements Button.ClickListener {

  private static final long serialVersionUID = -1369782896432973816L;

  Callback callback;
  Button yes = new Button("Yes", this);
  Button no = new Button("No", this);

  public YesNoDialog(String caption, String question, Callback callback) {
    super(caption);

    setModal(true);

    this.callback = callback;

    if (question != null) {
      addComponent(new Label(question));
    }

    HorizontalLayout hl = new HorizontalLayout();
    hl.addComponent(yes);
    hl.addComponent(no);
    hl.setComponentAlignment(yes, Alignment.MIDDLE_CENTER);
    hl.setComponentAlignment(no, Alignment.MIDDLE_CENTER);
    hl.setSizeUndefined();
    addComponent(hl);
  }

  public void buttonClick(ClickEvent event) {
    if (getParent() != null) {
      ((Window) getParent()).removeWindow(this);
    }
    
    // Callback aufrufen und das Ergebnis der Usereingabe übergeben.
    callback.onDialogResult(event.getSource() == yes);
  }

  /**
   * Das Interface muss implementiert werden. 
   * Dort muss die Antwort ausgewertet werden.
   */
  public interface Callback {
    public void onDialogResult(boolean resultIsYes);
  }

}
