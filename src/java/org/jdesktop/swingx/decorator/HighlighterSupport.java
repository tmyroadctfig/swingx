/*
 * $Id$
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.util.Contract;

/**
 * Support class for Highlighter clients (like JXTable, JXList, JXTree).
 * 
 * Manages the highlighters registered with the highlighter client, listens
 * to highlighter changes and updates the target.
 * 
 * @author Jeanette Winzenburg
 */
public class HighlighterSupport implements UIDependent{
    
    private JComponent client;
    private ChangeListener highlighterChangeListener;
    private CompoundHighlighter compoundHighlighter;

    /**
     * Instantiates a HighlighterSupport for the given client.
     * 
     * @param client the highlighter client this class is supporting, must not be null.
     */
    public HighlighterSupport(JComponent client) {
        this.client = Contract.asNotNull(client, "highlighter client must not be null");
    }

    
    
    /**
     * Returns the CompoundHighlighter assigned to the table, null if none.
     * PENDING: open up for subclasses again?.
     * 
     * @return the CompoundHighlighter assigned to the table.
     * @see #setCompoundHighlighter(CompoundHighlighter)
     */
    private CompoundHighlighter getCompoundHighlighter() {
        return compoundHighlighter;
    }

    /**
     * Assigns a CompoundHighlighter to the table, maybe null to remove all
     * Highlighters.<p>
     * 
     * The default value is <code>null</code>. <p>
     * 
     * PENDING: open up for subclasses again?.
     * @param pipeline the CompoundHighlighter to use for renderer decoration. 
     * @see #getCompoundHighlighter()
     * @see #addHighlighter(Highlighter)
     * @see #removeHighlighter(Highlighter)
     * 
     */
    private void setCompoundHighlighter(CompoundHighlighter pipeline) {
        CompoundHighlighter old = getCompoundHighlighter();
        if (old != null) {
            old.removeChangeListener(getHighlighterChangeListener());
        }
        compoundHighlighter = pipeline;
        if (compoundHighlighter != null) {
            compoundHighlighter.addChangeListener(getHighlighterChangeListener());
        }
    }
    
    /**
     * Sets the <code>Highlighter</code>s to the table, replacing any old settings.
     * None of the given Highlighters must be null.<p>
     * 
     * Note: the implementation is lenient with a single null highighter
     * to ease the api change from previous versions.
     * 
     * PENDING: property change? 
     * 
     * @param highlighters zero or more not null highlighters to use for renderer decoration.
     * 
     * @see #getHighlighters()
     * @see #addHighlighter(Highlighter)
     * @see #removeHighlighter(Highlighter)
     * 
     */
    public void setHighlighters(Highlighter... highlighters) {
        CompoundHighlighter pipeline = null;
        if ((highlighters != null) && (highlighters.length > 0) && 
            (highlighters[0] != null)) {    
           pipeline = new CompoundHighlighter(highlighters);
        }
        setCompoundHighlighter(pipeline);
    }

    /**
     * Returns the <code>Highlighter</code>s used by this table.
     * Maybe empty, but guarantees to be never null.
     * @return the Highlighters used by this table, guaranteed to never null.
     * @see #setHighlighters(Highlighter[])
     */
    public Highlighter[] getHighlighters() {
        return getCompoundHighlighter() != null ? 
                getCompoundHighlighter().getHighlighters() : 
                    CompoundHighlighter.EMPTY_HIGHLIGHTERS;
    }
    /**
     * Adds a Highlighter. Appends to the end of the list of used
     * Highlighters.
     * <p>
     * 
     * @param highlighter the <code>Highlighter</code> to add.
     * @throws NullPointerException if <code>Highlighter</code> is null.
     * 
     * @see #removeHighlighter(Highlighter)
     * @see #setHighlighters(Highlighter[])
     */
    public void addHighlighter(Highlighter highlighter) {
        CompoundHighlighter pipeline = getCompoundHighlighter();
        if (pipeline == null) {
           setCompoundHighlighter(new CompoundHighlighter(highlighter)); 
        } else {
            pipeline.addHighlighter(highlighter);
        }
    }

    /**
     * Removes the given Highlighter. <p>
     * 
     * Does nothing if the Highlighter is not contained.
     * 
     * @param highlighter the Highlighter to remove.
     * @see #addHighlighter(Highlighter)
     * @see #setHighlighters(Highlighter...)
     */
    public void removeHighlighter(Highlighter highlighter) {
        if ((getCompoundHighlighter() == null)) return;
        getCompoundHighlighter().removeHighlighter(highlighter);
    }
    
    /**
     * Returns the <code>ChangeListener</code> to use with highlighters. Lazily 
     * creates the listener.
     * 
     * @return the ChangeListener for observing changes of highlighters, 
     *   guaranteed to be <code>not-null</code>
     */
    protected ChangeListener getHighlighterChangeListener() {
        if (highlighterChangeListener == null) {
            highlighterChangeListener = createHighlighterChangeListener();
        }
        return highlighterChangeListener;
    }

    /**
     * Creates and returns the ChangeListener observing Highlighters.
     * <p>
     * Here: repaints the table on receiving a stateChanged.
     * 
     * @return the ChangeListener defining the reaction to changes of
     *         highlighters.
     */
    protected ChangeListener createHighlighterChangeListener() {
        return new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                client.repaint();
            }
        };
    }

    //------------- implement UIDependent

    public void updateUI() {
        if (getCompoundHighlighter() == null)
            return;
        getCompoundHighlighter().updateUI();
    }

}
