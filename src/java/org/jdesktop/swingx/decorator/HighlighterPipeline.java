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

package org.jdesktop.swingx.decorator;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableCellRenderer;

import org.jdesktop.swingx.decorator.Highlighter.UIHighlighter;

/**
 * A class which manages the lists of highlighters.
 *
 * @see Highlighter
 *
 * @author Ramesh Gupta
 * @author Jeanette Winzenburg
 * 
 */
public class HighlighterPipeline implements UIHighlighter {
    protected transient ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();

    protected List<Highlighter> highlighters;
    // JW: this is a hack to make JXTable renderers behave...
    private final static Highlighter resetDefaultTableCellRendererHighlighter = new Highlighter(null, null, true){

        @Override
        protected void applyBackground(Component renderer, ComponentAdapter adapter) {
            if (!adapter.isSelected()) {
                renderer.setBackground(null);
            }
        }

        @Override
        protected void applyForeground(Component renderer, ComponentAdapter adapter) {
            if (!adapter.isSelected()) {
                renderer.setForeground(null);
            }
        }
        
    };
    private ChangeListener highlighterChangeListener;

    public HighlighterPipeline() {
        highlighters = new ArrayList<Highlighter>();
    }
    
    /**
     * 
     * @param inList the array of highlighters to initially add to this.
     * @throws NullPointerException if array is null of array contains null values.
     */
    public HighlighterPipeline(Highlighter[] inList) {
        this();
        for (int i = 0; i < inList.length; i++) {
            addHighlighter(inList[i]);
        }
    }

    /**
     * Appends a highlighter to the pipeline.
     *
     * @param hl highlighter to add
      * @throws NullPointerException if highlighter is null.
    */
    public void addHighlighter(Highlighter hl) {
        addHighlighter(hl, false);
    }

    /**
     * Adds a highlighter to the pipeline.
     *
     * PENDING: Duplicate inserts?
     * 
     * @param hl highlighter to add
     * @param prepend prepend the highlighter if true; false will append
     * @throws NullPointerException if highlighter is null.
     */
    public void addHighlighter(Highlighter hl, boolean prepend) {
        if (prepend) {
            highlighters.add(0, hl);
        } else {
            highlighters.add(highlighters.size(), hl);
        }
        updateUI(hl);
        hl.addChangeListener(getHighlighterChangeListener());
        fireStateChanged();
    }


    private ChangeListener getHighlighterChangeListener() {
        if (highlighterChangeListener == null) {
            highlighterChangeListener = new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    fireStateChanged();
                    
                }
                
            };
        }
        return highlighterChangeListener;
    }

    /**
     * Removes a highlighter from the pipeline.
     *
     *  
     * @param hl highlighter to remove
     */
    public void removeHighlighter(Highlighter hl) {
        boolean success = highlighters.remove(hl);
        if (success) {
            // PENDING: duplicates?
            hl.removeChangeListener(getHighlighterChangeListener());
            fireStateChanged();
        }
        // should log if this didn't succeed. Maybe
    }

    public Highlighter[] getHighlighters() {
        return (Highlighter[])highlighters.toArray(new Highlighter[highlighters.size()]);
    }


    /**
     * Applies all the highlighters to the components.
     * 
     * @throws NullPointerException if either stamp or adapter is null.
     */
    public Component apply(Component stamp, ComponentAdapter adapter) {
        stamp = resetDefaultTableCellRenderer(stamp, adapter);
        for (Iterator<Highlighter> iter = highlighters.iterator(); iter.hasNext();) {
            stamp = iter.next().highlight(stamp, adapter);
            
        }
        return stamp;
    }

    /**
     * This is a hack around DefaultTableCellRenderer color "memory".
     * 
     * The issue is that the default has internal color management 
     * which is different from other types of renderers. The
     * consequence of the internal color handling is that there's
     * a color memory which must be reset somehow. The "old" hack around
     * reset the xxColors of all types of renderers to the adapter's
     * target XXColors, introducing #178-swingx (Highlighgters must not
     * change any colors except those for which their color properties are
     * explicitly set).
     * 
     * This hack limits the interference to renderers of type 
     * DefaultTableCellRenderer, applying a hacking highlighter which
     *  resets the renderers XXColors to null if unselected. Note that
     *  both hacks loose any colors previously set by clients (in 
     *  prepareRenderer before applying the pipeline). 
     * 
     * @param stamp
     * @param adapter
     * @return
     */
    private Component resetDefaultTableCellRenderer(Component stamp, ComponentAdapter adapter) {
        //JW
        // table renderers have different state memory as list/tree renderers
        // without the null they don't unstamp!
        // but... null has adversory effect on JXList f.i. - selection
        // color is changed. This is related to #178-swingx: 
        // highlighter background computation is weird.
        // 
        if (stamp instanceof DefaultTableCellRenderer) {    
        /** @todo optimize the following bug fix */
            stamp = resetDefaultTableCellRendererHighlighter.highlight(stamp, adapter); 
        }
        return stamp;
    }

    public void updateUI() {
        for (Highlighter highlighter : highlighters) {
            updateUI(highlighter);
        }
    }   

    /**
     * @param hl
     */
    private void updateUI(Highlighter hl) {
        if (hl instanceof UIHighlighter) {
            ((UIHighlighter) hl).updateUI();
        }
    }

    /**
     * Adds a <code>ChangeListener</code>.  The change listeners are run each
     * time any one of the Bounded Range model properties changes.
     *
     * @param l the ChangeListener to add
     * @see #removeChangeListener
     * @see BoundedRangeModel#addChangeListener
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    

    /**
     * Removes a <code>ChangeListener</code>.
     *
     * @param l the <code>ChangeListener</code> to remove
     * @see #addChangeListener
     * @see BoundedRangeModel#removeChangeListener
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }


    /**
     * Returns an array of all the change listeners
     * registered on this <code>DefaultBoundedRangeModel</code>.
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
     * @see #setRangeProperties
     * @see EventListenerList
     */
    protected void fireStateChanged() 
    {
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
