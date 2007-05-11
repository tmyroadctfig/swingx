/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
 *
 */
package org.jdesktop.swingx.decorator;

import java.awt.Component;

import javax.swing.event.ChangeListener;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
public interface Highlighter {

    /**
     * Decorates the specified cell renderer component for the given component
     * data adapter using highlighters that were previously set for the component.
     * This method unconditionally invokes {@link #doHighlight doHighlight} with
     * the same arguments as were passed in.
     *
     * @param renderer the cell renderer component that is to be decorated
     * @param adapter the {@link ComponentAdapter} for this decorate operation
     * @return the decorated cell renderer component
     */
    Component highlight(Component renderer, ComponentAdapter adapter);

    /**
     * Adds a <code>ChangeListener</code> which are 
     * notified after changes of any attribute. 
     *
     * @param l the ChangeListener to add
     * @see #removeChangeListener
     */
    void addChangeListener(ChangeListener l);

    /**
     * Removes a <code>ChangeListener</code>.
     *
     * @param l the <code>ChangeListener</code> to remove
     * @see #addChangeListener
     */
    void removeChangeListener(ChangeListener l);

    /**
     * Returns an array of all the change listeners
     * registered on this <code>LegacyHighlighter</code>.
     *
     * @return all of this model's <code>ChangeListener</code>s 
     *         or an empty
     *         array if no change listeners are currently registered
     *
     * @see #addChangeListener
     * @see #removeChangeListener
     *
     * @since 1.4
     */
    ChangeListener[] getChangeListeners();

}