package de.ukoeln.msml.genericparser.gui.extension;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.EventListener;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;
import de.ukoeln.msml.genericparser.worker.StringH;

public class MSMLRegexExtensionComponentPart extends JPanel
	implements IMSMLParserComponent{
	
	private static final long serialVersionUID = 1L;
	
	private JTextField txtRegex;
	public MSMLRegexExtensionComponentPart() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[]{30};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0};
		gridBagLayout.rowWeights = new double[]{0.0};
		setLayout(gridBagLayout);
		
		JLabel lblRegex = new JLabel("Reg-Ex:");
		GridBagConstraints gbc_lblRegex = new GridBagConstraints();
		gbc_lblRegex.fill = GridBagConstraints.BOTH;
		gbc_lblRegex.insets = new Insets(0, 0, 0, 5);
		gbc_lblRegex.gridx = 0;
		gbc_lblRegex.gridy = 0;
		add(lblRegex, gbc_lblRegex);
		
		txtRegex = new JTextField();
		GridBagConstraints gbc_txtRegex = new GridBagConstraints();
		gbc_txtRegex.fill = GridBagConstraints.BOTH;
		gbc_txtRegex.insets = new Insets(0, 0, 0, 5);
		gbc_txtRegex.gridx = 1;
		gbc_txtRegex.gridy = 0;
		add(txtRegex, gbc_txtRegex);
		txtRegex.setColumns(10);
		
	    JButton btnNewPart = new JButton("+");
		btnNewPart.setEnabled(false);
		GridBagConstraints gbc_btnNewPart = new GridBagConstraints();
		gbc_btnNewPart.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewPart.fill = GridBagConstraints.BOTH;
		gbc_btnNewPart.gridx = 2;
		gbc_btnNewPart.gridy = 0;
		btnNewPart.setPreferredSize(new Dimension(50, 30));
		add(btnNewPart, gbc_btnNewPart);

	    JButton btnRemovePart = new JButton("-");
	    btnRemovePart.setEnabled(false);
	    GridBagConstraints gbc_btnRemovePart = new GridBagConstraints();
		gbc_btnRemovePart.insets = new Insets(0, 0, 0, 5);
		gbc_btnRemovePart.fill = GridBagConstraints.BOTH;
		gbc_btnRemovePart.gridx = 3;
		gbc_btnRemovePart.gridy = 0;
		btnRemovePart.setPreferredSize(new Dimension(50, 30));
		add(btnRemovePart, gbc_btnRemovePart);
	}

    protected javax.swing.event.EventListenerList listenerList =
	        new javax.swing.event.EventListenerList();

	public class AddComponentRequestedEvent extends EventObject {

		private static final long serialVersionUID = 1L;

		public AddComponentRequestedEvent(Object source) {
	        super(source);
	    }
	}
	
	public interface AddComponentRequestedEventListener extends EventListener {
	    public void addComponentRequested(AddComponentRequestedEvent e);
	}

    public void addComponentRequestedEventListener(AddComponentRequestedEventListener listener) {
        listenerList.add(AddComponentRequestedEventListener.class, listener);
    }

    // This methods allows classes to unregister for MyEvents
    public void removeComponentRequestedEventListener(AddComponentRequestedEventListener listener) {
        listenerList.remove(AddComponentRequestedEventListener.class, listener);
    }

    void fireMyEvent(AddComponentRequestedEvent e) {
        Object[] listeners = listenerList.getListenerList();
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i].equals(AddComponentRequestedEventListener.class)) {
                ((AddComponentRequestedEventListener)listeners[i+1]).addComponentRequested(e);
            }
        }
    }
	
	public void setIsLastPast(boolean isLast) {
//		btnNewPart.setVisible(isLast);
	}

	@Override
	public void setChildComponentsEnabled(boolean enabled) {
		txtRegex.setEnabled(enabled);
	}

	public boolean isSet() {
		return !StringH.isNullOrEmpty(txtRegex.getText());
	}

	public String getRegex() {
		return txtRegex.getText();
	}

	public void setRegex(String string) {
		txtRegex.setText(string);
	}

	@Override
	public void updateValuesInExtension() {
	}
}
