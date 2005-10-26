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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;

/**
 * Added to the Toggle type buttons and menu items so that various components
 * which have been created from a single StateChangeAction can be in synch
 */
class ToggleActionPropertyChangeListener implements PropertyChangeListener {

    // XXX - Should be a WeakRef since it's unreachable! 
    // this is a potential memory leak but we don't really have to
    // worry about it since the most of the time the buttons will be
    // loaded for the lifetime of the application. Should make it
    // weak referenced for a general purpose toolkit.
    private AbstractButton button; 

    public ToggleActionPropertyChangeListener(AbstractButton button) {
	this.button = button;
    }

    public void propertyChange(PropertyChangeEvent evt) {
	String propertyName = evt.getPropertyName();

	if (propertyName.equals("selected")) {
	    Boolean selected = (Boolean)evt.getNewValue();
	    button.setSelected(selected.booleanValue());
	}
    }
}
