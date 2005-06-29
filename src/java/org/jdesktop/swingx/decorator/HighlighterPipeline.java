/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.EventListenerList;

/**
 * A class which manages the lists of highlighters.
 *
 * @see Highlighter
 *
 * @author Ramesh Gupta
 */
public class HighlighterPipeline {
    protected EventListenerList listenerList = new EventListenerList();

    private List highlighters;
    private final static Highlighter nullHighlighter = new Highlighter(null, null);

    public HighlighterPipeline() {
        highlighters = new ArrayList();
    }
    
    public HighlighterPipeline(Highlighter[] inList) {
        // always returns a new copy of inList
        // XXX seems like there is too much happening here
        // JW: and probably not what's intended - the array
        // is cloned, not its content!
        // don't need to anyway - highlighters are shareable
        // between Pipelines - the order serves no purpose
        List copy = Arrays.asList((Highlighter[])inList.clone());
        highlighters = new ArrayList(copy.size());
        highlighters.addAll(copy);

//        reindexHighlighters();
    }

//    private void reindexHighlighters() {
//        Iterator iter = highlighters.iterator();
//        int i = 0;
//        while (iter.hasNext()) {
//            Highlighter hl = (Highlighter)iter.next();
//            hl.order = i++;
//        }
//    }

    /**
     * Appends a highlighter to the pipeline.
     *
     * @param hl highlighter to add
     */
    public void addHighlighter(Highlighter hl) {
        addHighlighter(hl, false);
    }

    /**
     * Adds a highlighter to the pipeline.
     *
     * @param hl highlighter to add
     * @param prepend prepend the highlighter if true; false will append
     */
    public void addHighlighter(Highlighter hl, boolean prepend) {
        if (highlighters == null) {
            highlighters = new ArrayList();
        }
        if (prepend) {
            highlighters.add(0, hl);
        } else {
            highlighters.add(highlighters.size(), hl);
        }
//        reindexHighlighters();
    }

    /**
     * Removes a highlighter from the pipeline.
     *
     * @param hl highlighter to remove
     */
    public void removeHighlighter(Highlighter hl) {
        boolean success = highlighters.remove(hl);
        // should log if this didn't succeed. Maybe
    }

    public Highlighter[] getHighlighters() {
        return (Highlighter[])highlighters.toArray(new Highlighter[highlighters.size()]);
    }

    public EventListener[] getListeners(Class listenerType) {
        return listenerList.getListeners(listenerType);
    }

    /**
     * Adds a listener to the list that's notified each time there is a change
     * to the pipeline.
     *
     * @param l the <code>PipelineListener</code> to be added
     */
    public void addPipelineListener(PipelineListener l) {
        listenerList.add(PipelineListener.class, l);
    }

    /**
     * Removes a listener from the list that's notified each time there is a change
     * to the pipeline.
     *
     * @param l the <code>PipelineListener</code> to be removed
     */
    public void removePipelineListener(PipelineListener l) {
        listenerList.remove(PipelineListener.class, l);
    }

    /**
     * Returns an array of all the pipeline listeners
     * registered on this <code>HighlighterPipeline</code>.
     *
     * @return all of this pipeline's <code>PipelineListener</code>s,
     *         or an empty array if no pipeline listeners
     *         are currently registered
     *
     * @see #addPipelineListener
     * @see #removePipelineListener
     */
    public PipelineListener[] getPipelineListeners() {
        return (PipelineListener[]) listenerList.getListeners(
            PipelineListener.class);
    }

    protected void fireContentsChanged(Object source) {
        Object[] listeners = listenerList.getListenerList();
        PipelineEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == PipelineListener.class) {
                if (e == null) {
                    e = new PipelineEvent(source, PipelineEvent.CONTENTS_CHANGED);
                }
                ( (PipelineListener) listeners[i + 1]).contentsChanged(e);
            }
        }
    }

    /**
     * Applies all the highlighters to the components.
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
        Iterator iter = highlighters.iterator();
        while (iter.hasNext()) {
            Highlighter hl = (Highlighter)iter.next();
            stamp = hl.highlight(stamp, adapter);
        }
        return stamp;
    }
}
