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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Convenience abstract <code>Highlighter</code> implementation which
 * managers listeners. 
 * 
 * @author Jeanette Winzenburg
 */
public abstract class AbstractHighlighter implements Highlighter {

    /**
     * Only one <code>ChangeEvent</code> is needed per model instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this".
     */
    protected transient ChangeEvent changeEvent = null;
    /** The listeners waiting for model changes. */
    protected EventListenerList listenerList = new EventListenerList();
    /** flag to indicate whether the Highlighter is immutable in every respect. */
    protected final boolean immutable;

    /**
     * Instantiates a mutable Highlighter.
     *
     */
    public AbstractHighlighter() {
        this(false);
    }
    /**
     * Instantiates a Highlighter with the given mutability.<p>
     * 
     * NOTE: Subclasses which declare themselves immutable must 
     * take care to really be so! The flag is a bit of a hack around
     * a memory leak with static pre-defined default highlighters.
     * 
     * @param immutable the immutable property.
     */
    public AbstractHighlighter(boolean immutable) {
        this.immutable = immutable;
    }

    /**
     * Returns immutable flag. If true, the Highlighter must not
     * change internal state in any way. In this case,
     * no listeners are added and no change events fired.
     * @return true if none of the setXX methods have any effect
     */
    public final boolean isImmutable() {
        return immutable;
    }

//------------------------ implement Highlighter change notification
    
    /**
     * Adds a <code>ChangeListener</code> if this is mutable. ChangeListeners are
     * notified after changes of any attribute. Does nothing if immutable. 
     *
     * @param l the ChangeListener to add
     * @see #removeChangeListener
     */
    public final void addChangeListener(ChangeListener l) {
        if (isImmutable()) return;
        listenerList.add(ChangeListener.class, l);
    }

    /**
     * Removes a <code>ChangeListener</code> if this is mutable. 
     * Does nothis if immutable.
     *
     * @param l the <code>ChangeListener</code> to remove
     * @see #addChangeListener
     */
    public final void removeChangeListener(ChangeListener l) {
        if (isImmutable()) return;
        listenerList.remove(ChangeListener.class, l);
    }

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
    public ChangeListener[] getChangeListeners() {
        return (ChangeListener[])listenerList.getListeners(
                ChangeListener.class);
    }

    /** 
     * Runs each <code>ChangeListener</code>'s <code>stateChanged</code> method.
     * 
     */
    protected final void fireStateChanged() {
        if (isImmutable()) return;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -=2 ) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }          
        }
    }


}
