package org.xtreemfs.portlet.ui;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.ComboBox;

/**
 * Erstellt einen Clone der Items, damit Änderungen an diesen Items
 * nicht auch in der Tabelle angezeigt werden
 * @author pschaefe
 *
 */
public class Autocomplete extends ComboBox {
  private static final long serialVersionUID = -3525828514419008892L;

  public Autocomplete() {
    super();
  }

  public Autocomplete(String caption, Collection<?> options) {
    super(caption, options);
  }

  public Autocomplete(String caption, Container dataSource) {
    super(caption, dataSource);
  }

  public Autocomplete(String caption) {
    super(caption);
  }

  /**
   * Erstellt einen Clone der Items
   */
  @SuppressWarnings("deprecation")
@Override
  public void setContainerDataSource(Container newDataSource) {
    try {
      Container container = (Container) ((IndexedContainer)newDataSource).clone();
      super.setContainerDataSource(container);
    } catch (CloneNotSupportedException e) {
      super.setContainerDataSource(newDataSource);
    }
  }

  @Override
  public Object getValue() {
    Object value = super.getValue();
    if (value != null) {
      return getItemCaption(value);
    }
    return null;
  }

}
