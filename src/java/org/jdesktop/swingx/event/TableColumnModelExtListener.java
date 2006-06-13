/*
 * Created on 13.06.2006
 *
 */
package org.jdesktop.swingx.event;

import java.beans.PropertyChangeEvent;

import javax.swing.event.TableColumnModelListener;

/**
 * A TableColumnModelListener which is interested in propertyChanges of
 * contained TableColumns.
 * 
 * @author Jeanette Winzenburg
 */
public interface TableColumnModelExtListener extends TableColumnModelListener {

    /**
     * 
     * @param event a <code>PropertyChangeEvent</code> fired by a TableColumn
     *   contained in the TableColumnModel
     */
    void columnPropertyChange(PropertyChangeEvent event);
}
