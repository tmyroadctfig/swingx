/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoundedRangeModel;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * A class which manages the lists of highlighters.
 *
 * @see Highlighter
 *
 * @author Ramesh Gupta
 * @author Jeanette Winzenburg
 * 
 */
public class HighlighterPipeline {
    protected transient ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();

    protected List<Highlighter> highlighters;
    private final static Highlighter nullHighlighter = new Highlighter(null, null);
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
        // always returns a new copy of inList
        // XXX seems like there is too much happening here
        // JW: and probably not what's intended - the array
        // is cloned, not its content!
        // don't need to anyway - highlighters are shareable
        // between Pipelines - the order serves no purpose
//        List copy = Arrays.asList((Highlighter[])inList.clone());
//        highlighters = new ArrayList(copy.size());
//        highlighters.addAll(copy);
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
        //JW
        // table renderers have different state memory as renderers
        // without the null they don't unstamp!
        // but... null has adversory effect on JXList f.i. - selection
        // color is changed
        // 
        if (adapter.getComponent() instanceof JTable) {
        /** @todo optimize the following bug fix */
            stamp = nullHighlighter.highlight(stamp, adapter);      // fixed bug from M1
        }
        for (Iterator<Highlighter> iter = highlighters.iterator(); iter.hasNext();) {
            stamp = iter.next().highlight(stamp, adapter);
            
        }
//        Iterator iter = highlighters.iterator();
//        while (iter.hasNext()) {
//            Highlighter hl = (Highlighter)iter.next();
//            stamp = hl.highlight(stamp, adapter);
//        }
        return stamp;
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
