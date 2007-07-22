/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */


package org.jdesktop.swingx.test;

import java.util.LinkedList;
import java.util.List;

import org.jdesktop.swingx.DateSelectionListener;
import org.jdesktop.swingx.event.DateSelectionEvent;

/**
 * A ChangeListener that stores the received ChangeEvents.
 * 
 */
public class DateSelectionReport implements DateSelectionListener {
    
    /**
     * Holds a list of all received PropertyChangeEvents.
     */
    protected List<DateSelectionEvent> events = new LinkedList<DateSelectionEvent>();
    
//------------------------ implement PropertyChangeListener
    
    public void valueChanged(DateSelectionEvent evt) {
        events.add(0, evt);
    }
    
    public int getEventCount() {
        return events.size();
    }
 
    public void clear() {
        events.clear();
    }
    
    public boolean hasEvents() {
        return !events.isEmpty();
    }
 
     public DateSelectionEvent getLastEvent() {
        return hasEvents() ? events.get(0) : null;
    }

}
