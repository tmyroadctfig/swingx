/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */


package org.jdesktop.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A ChangeListener that stores the received ChangeEvents.
 * 
 */
public class ActionReport implements ActionListener {
    
    /**
     * Holds a list of all received PropertyChangeEvents.
     */
    protected List<ActionEvent> events = new LinkedList<ActionEvent>();
    protected Map<Object, ActionEvent> eventMap = new HashMap<Object, ActionEvent>();
    
//------------------------ implement PropertyChangeListener
    
    public void actionPerformed(ActionEvent evt) {
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
 
     public ActionEvent getLastEvent() {
        return hasEvents()
            ? null :  events.get(0);
    }

     public ActionEvent getEvent(Object source) {
         return eventMap.get(source);
     }


}
