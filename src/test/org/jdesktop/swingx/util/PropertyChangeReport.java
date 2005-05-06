/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */


package org.jdesktop.swingx.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A PropertyChangeListener that stores the received PropertyChangeEvents.
 * 
 * modified ("beanified") from JGoodies PropertyChangeReport.
 * 
 */
public class PropertyChangeReport implements PropertyChangeListener {
    
    /**
     * Holds a list of all received PropertyChangeEvents.
     */
    protected List events = new LinkedList();
    
//------------------------ implement PropertyChangeListener
    
    public void propertyChange(PropertyChangeEvent evt) {
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
 
    public int getEventCount(String property) {
        if (property == null) return getMultiCastEventCount();
        int count = 0;
        for (Iterator iter = events.iterator(); iter.hasNext();) {
            PropertyChangeEvent event = (PropertyChangeEvent) iter.next();
            if (property.equals(event.getPropertyName())) {
                count++;
            }
        }
        return count;
    }
    public int getMultiCastEventCount() {
        int count = 0;
        for (Iterator i = events.iterator(); i.hasNext();) {
            PropertyChangeEvent event = (PropertyChangeEvent) i.next();
            if (event.getPropertyName() == null)
                count++;
        }
        return 0;
    }
    
    public int getNamedEventCount() {
        return getEventCount() - getMultiCastEventCount();
    }
    
    public PropertyChangeEvent getLastEvent() {
        return events.isEmpty()
            ? null
            : (PropertyChangeEvent) events.get(0);
    }
    
    public Object getLastOldValue() {
        return getLastEvent().getOldValue();
    }
    
    public Object getLastNewValue() {
        return getLastEvent().getNewValue();
    }
    
    public boolean getLastOldBooleanValue() {
        return ((Boolean) getLastOldValue()).booleanValue();
    }

    public boolean getLastNewBooleanValue() {
        return ((Boolean) getLastNewValue()).booleanValue();
    }

}
