/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */


package org.jdesktop.swingx.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.decorator.PipelineEvent;
import org.jdesktop.swingx.decorator.PipelineListener;

/**
 * A ChangeListener that stores the received ChangeEvents.
 * 
 */
public class PipelineReport implements PipelineListener {
    
    /**
     * Holds a list of all received PropertyChangeEvents.
     */
    protected List<PipelineEvent> events = new LinkedList();
    protected Map<Object, PipelineEvent> eventMap = new HashMap();
    
//------------------------ implement PropertyChangeListener
    
    public void contentsChanged(PipelineEvent evt) {
       events.add(0, evt);
        if (evt.getSource() != null) {
            eventMap.put(evt.getSource(), evt);
        }
    }
    
    public int getEventCount() {
        return events.size();
    }
 
    public void clear() {
        events.clear();
        eventMap.clear();
    }
    
    public boolean hasEvents() {
        return !events.isEmpty();
    }
 
     public PipelineEvent getLastEvent() {
        return events.isEmpty() ? null : events.get(0);
    }

     public PipelineEvent getEvent(Object source) {
         return eventMap.get(source);
     }


}
