/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.swingx.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jdesktop.swingx.LinkModel;

/**
 * @author Jeanette Winzenburg
 */
public class LinkModelAction extends AbstractAction {
    
    private LinkModel link;
    private ActionListener delegate;
    public static final String VISIT_ACTION = "visit";
    public static final String VISITED_PROPERTY = LinkModel.VISITED_PROPERTY;
    private PropertyChangeListener linkListener;
    
    public LinkModelAction(LinkModel link) {
        setLink(link);
    }

    public void setLink(LinkModel link) {
        uninstallLinkListener();
        this.link = link;
        installLinkListener();
        updateFromLink();
    }

    public LinkModel getLink() {
        return link;
    }

    public void setVisitingDelegate(ActionListener delegate) {
        this.delegate = delegate;
    }
    
    public void actionPerformed(ActionEvent e) {
        if ((delegate != null) && (link != null)) {
            delegate.actionPerformed(new ActionEvent(link, ActionEvent.ACTION_PERFORMED, VISIT_ACTION));
        }
        
    }

    private void uninstallLinkListener() {
        if (link == null) return;
        link.removePropertyChangeListener(getLinkListener());
     
    }

    private void updateFromLink() {
        if (link != null) {
            putValue(Action.NAME, link.getText());
            putValue(Action.SHORT_DESCRIPTION, link.getURL().toString());
            putValue(VISITED_PROPERTY, new Boolean(link.getVisited()));
        } else {
            Object[] keys = getKeys();
            if (keys == null) return;
            for (int i = 0; i < keys.length; i++) {
               putValue(keys[i].toString(), null); 
            }
        }
    }

    private void installLinkListener() {
        if (link == null) return;
        link.addPropertyChangeListener(getLinkListener());
    }

    private PropertyChangeListener getLinkListener() {
        if (linkListener == null) {
         linkListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                updateFromLink();
            }
            
        };
        }
        return linkListener;
    }

    
}
