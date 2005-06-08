package org.jdesktop.swingx.autocomplete;

import javax.swing.JList;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

/**
 * An implementation of the CommonModel that is suitable for a JList in
 * conjunction with a JTextComponent.
 * @author Thomas Bierhance
 */
public class ListAdaptor extends AbstractComponentAdaptor implements ListSelectionListener {
    
    /** the list containing the items */
    JList list;
    /** the text component that is used for automatic completion*/
    JTextComponent textComponent;
    
    /**
     * Creates a new JListAdaptor for the given list and text component.
     * @param list the list that contains the items that are used for automatic
     * completion
     * @param textComponent the text component that will be used automatic
     * completion
     */
    public ListAdaptor(JList list, JTextComponent textComponent) {
        this.list = list;
        this.textComponent = textComponent;
        // when a new item is selected set and mark the text
        list.addListSelectionListener(this);
    }
    
    /**
     * Implementation side effect - do not invoke.
     * @param listSelectionEvent -
     */
    // ListSelectionListener (listening to list)
    public void valueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) {
        // set the text to the currently selected item
        getTextComponent().setText(list.getSelectedValue().toString());
        // mark the entire text
        markEntireText();
    }
    
    public Object getSelectedItem() {
        return list.getSelectedValue();
    }
    
    public int getItemCount() {
        return list.getModel().getSize();
    }
    
    public Object getItem(int index) {
        return list.getModel().getElementAt(index);
    }
    
    public void setSelectedItem(Object item) {
        list.setSelectedValue(item, true);
    }
    
    public JTextComponent getTextComponent() {
        return textComponent;
    }
}