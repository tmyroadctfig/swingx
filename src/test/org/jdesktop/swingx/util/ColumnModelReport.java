/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.util;

import java.beans.PropertyChangeEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;

import org.jdesktop.swingx.event.TableColumnModelExtListener;

/**
 * A TableColumnListener that stores the received TableColumnEvents.
 */
public class ColumnModelReport implements TableColumnModelExtListener {

    /**
     * Holds a list of all received ValueChangeEvents.
     */
    private List<TableColumnModelEvent> removedEvents = new LinkedList<TableColumnModelEvent>();
    private List<TableColumnModelEvent> addedEvents = new LinkedList<TableColumnModelEvent>();
    private List<TableColumnModelEvent> movedEvents = new LinkedList<TableColumnModelEvent>();
    
    private List<ListSelectionEvent> selectionEvents = new LinkedList<ListSelectionEvent>();
    private List<ChangeEvent> changeEvents = new LinkedList<ChangeEvent>();
    
    private List<PropertyChangeEvent> columnPropertyEvents = new LinkedList<PropertyChangeEvent>();

//------------------------ implement TableColumnModelListener    
    public void columnAdded(TableColumnModelEvent e) {
        addedEvents.add(0, e);

    }
    public void columnMarginChanged(ChangeEvent e) {
        changeEvents.add(0, e);

    }
    public void columnMoved(TableColumnModelEvent e) {
        movedEvents.add(0, e);

    }
    public void columnRemoved(TableColumnModelEvent e) {
        removedEvents.add(0, e);

    }
    public void columnSelectionChanged(ListSelectionEvent e) {
        selectionEvents.add(0, e);

    }
    //---------------------- implement TableColumnModelExtListener


    public void columnPropertyChange(PropertyChangeEvent e) {
        columnPropertyEvents.add(0, e);
        
    }

    public boolean hasEvents() {
        return getEventCount() > 0;
    }


    public void clear() {
        addedEvents.clear();
        removedEvents.clear();
        movedEvents.clear();
        changeEvents.clear();
        selectionEvents.clear();
        columnPropertyEvents.clear();
    }

    public int getEventCount() {
        return addedEvents.size() + removedEvents.size() + movedEvents.size() +
          changeEvents.size() + selectionEvents.size() + columnPropertyEvents.size();
    }

    // -------------- access reported TableModelEvents
    
    public boolean hasRemovedEvent() {
        return !removedEvents.isEmpty();
    }
    
    public TableColumnModelEvent getLastRemoveEvent() {
        return removedEvents.isEmpty() ? null : removedEvents.get(0);
     }

    public boolean hasAddedEvent() {
        return !addedEvents.isEmpty();
    }
    
    public TableColumnModelEvent getLastAddEvent() {
        return addedEvents.isEmpty() ? null : addedEvents.get(0);
     }

    //--------------- access reported propertyChangeEvent
    
    public boolean hasColumnPropertyEvent() {
        return !columnPropertyEvents.isEmpty();
    }
    
    public int getColumnPropertyEventCount() {
        return columnPropertyEvents.size();
    }
    public PropertyChangeEvent getLastColumnPropertyEvent() {
        return columnPropertyEvents.isEmpty() ? null :
            columnPropertyEvents.get(0);
    }
    
}