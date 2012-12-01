package org.xtreemfs.portlet.ui;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Dialog zur Eingabe eines Textes
 */
public class TextInputDialog extends Window implements Button.ClickListener {

  private static final long serialVersionUID = -1369782896432973816L;

  Callback callback;
  Button ok = new Button("Ok", this);
  Button cancel = new Button("Cancel", this);

  private final TextField editor = new TextField("");

  
  public TextInputDialog(String caption, String question, String defaultValue, Callback callback) {
    super(caption);

    editor.setWidth("260px");
    setWidth("300px");
    setModal(true);

    this.callback = callback;

    if (question != null) {
      addComponent(new Label(question));
    }

    addComponent(editor);
    
    if (defaultValue != null) {
      editor.setValue(defaultValue);
    }
    
    HorizontalLayout hl = new HorizontalLayout();
    hl.addComponent(ok);
    hl.addComponent(cancel);
    hl.setComponentAlignment(ok, Alignment.MIDDLE_CENTER);
    hl.setComponentAlignment(cancel, Alignment.MIDDLE_CENTER);
    hl.setSizeUndefined();
    addComponent(hl);
  }

  public void buttonClick(ClickEvent event) {
    if (getParent() != null) {
      ((Window) getParent()).removeWindow(this);
    }
    
    // Callback aufrufen und die Texteingabe übergeben.
    callback.onDialogResult(event.getSource() == ok, (String) editor.getValue());
  }

  /**
   * Das Interface muss implementiert werden. 
   * Dort muss die Eingabe verarbeitet werden.
   */
  public interface Callback {
    public void onDialogResult(boolean resultIsYes, String input);
  }

}
