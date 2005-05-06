/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.util;

import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

/**
 * A TableColumnListener that stores the received TableColumnEvents.
 */
public class ColumnModelReport implements TableColumnModelListener {

    /**
     * Holds a list of all received ValueChangeEvents.
     */
    private List removedEvents = new LinkedList();
    private List addedEvents = new LinkedList();
    private List movedEvents = new LinkedList();
    
    private List selectionEvents = new LinkedList();
    private List changeEvents = new LinkedList();

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
    //---------------------- implement ValueChangeListener


    public int getEventCount() {
        return addedEvents.size() + removedEvents.size() + movedEvents.size() +
          changeEvents.size() + selectionEvents.size();
    }

    public boolean hasRemovedEvent() {
        return !removedEvents.isEmpty();
    }
    
    public TableColumnModelEvent getLastRemoveEvent() {
        return removedEvents.isEmpty() ? null : (TableColumnModelEvent) removedEvents.get(0);
     }

    public boolean hasAddedEvent() {
        return !addedEvents.isEmpty();
    }
    
    public TableColumnModelEvent getLastAddEvent() {
        return addedEvents.isEmpty() ? null : (TableColumnModelEvent) addedEvents.get(0);
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
    }
}