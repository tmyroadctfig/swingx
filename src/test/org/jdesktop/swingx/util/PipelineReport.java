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
    protected List<PipelineEvent> events = new LinkedList<PipelineEvent>();
    protected List<PipelineEvent> orderChanged = new LinkedList<PipelineEvent>();
    protected List<PipelineEvent> contentsChanged = new LinkedList<PipelineEvent>();
    protected Map<Object, PipelineEvent> eventMap = new HashMap<Object, PipelineEvent>();
    
//------------------------ implement PropertyChangeListener
    
    public void contentsChanged(PipelineEvent evt) {
       events.add(0, evt);
       if (PipelineEvent.SORT_ORDER_CHANGED == evt.getType()) {
           orderChanged.add(0, evt);
       } else {
           contentsChanged.add(0, evt);
       }
       
        if (evt.getSource() != null) {
            eventMap.put(evt.getSource(), evt);
        }
    }
    
    public int getEventCount() {
        return events.size();
    }
 
    public int getEventCount(int type) {
        if (PipelineEvent.SORT_ORDER_CHANGED == type) {
            return orderChanged.size();
        } else if (PipelineEvent.CONTENTS_CHANGED == type){
            return contentsChanged.size();
        }
        return events.size();
    }

    public void clear() {
        events.clear();
        contentsChanged.clear();
        orderChanged.clear();
        eventMap.clear();
    }
    
    public boolean hasEvents() {
        return !events.isEmpty();
    }
 
     public PipelineEvent getLastEvent() {
        return getLastEvent(events); 
    }

     public PipelineEvent getLastEvent(int type) {
         List<PipelineEvent> list = events;
         if (PipelineEvent.CONTENTS_CHANGED == type) {
             list = contentsChanged;
         } else if (PipelineEvent.SORT_ORDER_CHANGED == type) {
             list = orderChanged;
         }
         return getLastEvent(list); 
     }
     private PipelineEvent getLastEvent(List<PipelineEvent> events) {
         return events.isEmpty() ? null : events.get(0);
         
     }
     public PipelineEvent getEvent(Object source) {
         return eventMap.get(source);
     }


}
