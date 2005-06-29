/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;


/**
 * JXList
 *
 * Enabled Rollover/LinkModel handling.
 * Enabled Highlighter support.
 * 
 * @author Ramesh Gupta
 * @author Jeanette Winzenburg
 */
public class JXList extends JList {
    /**
     * Array of {@link Highlighter} objects that will be used to highlight
     * the cell renderer for this component.
     */
    protected FilterPipeline filters = null;
    protected HighlighterPipeline highlighters = null;
    /** listening to changeEvents from highlighterPipeline. */
    private ChangeListener highlighterChangeListener;

    // MUST ALWAYS ACCESS dataAdapter through accessor method!!!
    private final ComponentAdapter dataAdapter = new ListAdapter(this);

    /**
     * Mouse/Motion/Listener keeping track of mouse moved in
     * cell coordinates.
     */
    private RolloverProducer rolloverProducer;

    /**
     * RolloverController: listens to cell over events and
     * repaints entered/exited rows.
     */
    private LinkController linkController;
    
    private DelegatingRenderer delegatingRenderer;
    

    public JXList() {
    }

    public JXList(ListModel dataModel) {
        super(dataModel);
    }

    public JXList(Object[] listData) {
        super(listData);
    }

    public JXList(Vector listData) {
        super(listData);
    }
    /**
     * Property to enable/disable rollover support. This can be enabled
     * to show "live" rollover behaviour, f.i. the cursor over LinkModel cells. 
     * Default is disabled.
     * @param rolloverEnabled
     */
    public void setRolloverEnabled(boolean rolloverEnabled) {
        boolean old = isRolloverEnabled();
        if (rolloverEnabled == old) return;
        if (rolloverEnabled) {
            rolloverProducer = new RolloverProducer();
            addMouseListener(rolloverProducer);
            addMouseMotionListener(rolloverProducer);
            linkController = new LinkController();
            addPropertyChangeListener(linkController);
        } else {
            removeMouseListener(rolloverProducer);
            removeMouseMotionListener(rolloverProducer);
            rolloverProducer = null;
            removePropertyChangeListener(linkController);
            linkController = null;
        }
        firePropertyChange("rolloverEnabled", old, isRolloverEnabled());
    }

    /**
     * returns the rolloverEnabled property.
     * @return
     */
    public boolean isRolloverEnabled() {
        return rolloverProducer != null;
    }

    public void setLinkVisitor(ActionListener linkVisitor) {
        if (linkVisitor != null) {
            setRolloverEnabled(true);
            getDelegatingRenderer().setLinkVisitor(linkVisitor);
        } else {
            // JW: think - need to revert?
        }
            
    }
    
    private DelegatingRenderer getDelegatingRenderer() {
        if (delegatingRenderer == null) {
            // only called once... to get hold of the default?
            delegatingRenderer = new DelegatingRenderer(super.getCellRenderer());
        }
        return delegatingRenderer;
    }

    public ListCellRenderer getCellRenderer() {
        return getDelegatingRenderer();
    }

    public void setCellRenderer(ListCellRenderer renderer) {
        // JW: Pending - probably fires propertyChangeEvent with wrong newValue?
        // how about fixedCellWidths? 
        // need to test!!
        getDelegatingRenderer().setDelegateRenderer(renderer);
        super.setCellRenderer(delegatingRenderer);
    }
 
    
    private class DelegatingRenderer implements ListCellRenderer {

        private LinkRenderer linkRenderer;
        private ListCellRenderer delegateRenderer;
        
        public DelegatingRenderer(ListCellRenderer delegate) {
            setDelegateRenderer(delegate);
        }

        public void setDelegateRenderer(ListCellRenderer delegate) {
            if (delegate == null) {
                delegate = new DefaultListCellRenderer();
            }
            delegateRenderer = delegate;
        }
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component comp = null;
          
            if (value instanceof LinkModel) {
                comp =  getLinkRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            } else { 
                comp = delegateRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            } 
            if (highlighters != null) {
                ComponentAdapter adapter = getComponentAdapter();
                adapter.column = 0;
                adapter.row = index;
                comp = highlighters.apply(comp, adapter);
            }
            return comp;
        }


        private LinkRenderer getLinkRenderer() {
            if (linkRenderer == null) {
                linkRenderer = new LinkRenderer();
            }
            return linkRenderer;
        }

        public void setLinkVisitor(ActionListener linkVisitor) {
            getLinkRenderer().setVisitingDelegate(linkVisitor);
            
        }


        public void updateUI() {
            updateRendererUI(linkRenderer);
            updateRendererUI(delegateRenderer);
        }


        private void updateRendererUI(ListCellRenderer renderer) {
            if (renderer instanceof JComponent) {
                ((JComponent) renderer).updateUI();
            } else if (renderer != null){
                Component comp = renderer.getListCellRendererComponent(JXList.this, null, 
                        -1, false, false);
                if (comp instanceof JComponent) {
                    ((JComponent) comp).updateUI();
                }
            }
            
        }
        
        
        
    }
    
    
    public void updateUI() {
        super.updateUI();
        updateRendererUI();
    }
    
    private void updateRendererUI() {
        if (delegatingRenderer != null) {
            delegatingRenderer.updateUI();
        } else {
            ListCellRenderer renderer = getCellRenderer();
            if (renderer instanceof JComponent) {
                ((JComponent) renderer).updateUI();
            }
        }
        
        
    }

    public FilterPipeline getFilters() {
        return filters;
    }

    public void setFilters(FilterPipeline pipeline) {
        /**@todo setFilters
        TableModel	model = getModel();
        adjustListeners(pipeline, model, model);
		*/
        filters = pipeline;
    }

    public HighlighterPipeline getHighlighters() {
        return highlighters;
    }

    /** Assigns a HighlighterPipeline to the table. */
    public void setHighlighters(HighlighterPipeline pipeline) {
        HighlighterPipeline old = getHighlighters();
        if (old != null) {
            old.removeChangeListener(getHighlighterChangeListener());
        }
        highlighters = pipeline;
        if (highlighters != null) {
            highlighters.addChangeListener(getHighlighterChangeListener());
        }
        firePropertyChange("highlighters", old, getHighlighters());
    }

    private ChangeListener getHighlighterChangeListener() {
        if (highlighterChangeListener == null) {
            highlighterChangeListener = new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    repaint();
                    
                }
                
            };
        }
        return highlighterChangeListener;
    }


    protected ComponentAdapter getComponentAdapter() {
        // MUST ALWAYS ACCESS dataAdapter through accessor method!!!
        return dataAdapter;
    }


    static class ListAdapter extends ComponentAdapter {
        private final JList	list;

        /**
         * Constructs a <code>ListDataAdapter</code> for the specified
         * target component.
         *
         * @param component the target component
         */
        public ListAdapter(JList component) {
            super(component);
            list = component;
        }

        /**
         * Typesafe accessor for the target component.
         *
         * @return the target component as a {@link javax.swing.JList}
         */
        public JList getList() {
            return list;
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasFocus() {
            /** @todo Think through printing implications */
            return list.isFocusOwner() && (row == list.getLeadSelectionIndex());
        }

        public int getRowCount() {
            return list.getModel().getSize();
        }

        /**
         * {@inheritDoc}
         */
        public Object getValueAt(int row, int column) {
            return list.getModel().getElementAt(row);
        }

        public Object getFilteredValueAt(int row, int column) {
            /** @todo Implement getFilteredValueAt */
            return getValueAt(row, column);
//            throw new UnsupportedOperationException(
//                "Method getFilteredValueAt() not yet implemented.");
        }

        public void setValueAt(Object aValue, int row, int column) {
            /** @todo Implement getFilteredValueAt */
            throw new UnsupportedOperationException(
                "Method getFilteredValueAt() not yet implemented.");
        }

        public boolean isCellEditable(int row, int column) {
            /** @todo Implement getFilteredValueAt */
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isSelected() {
            /** @todo Think through printing implications */
            return list.isSelectedIndex(row);
        }

    }
}
