/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListModel;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlighterPipeline;


/**
 * JXList
 *
 * @author Ramesh Gupta
 */
public class JXList extends JList {
    /**
     * Array of {@link Highlighter} objects that will be used to highlight
     * the cell renderer for this component.
     */
    protected FilterPipeline filters = null;
    protected HighlighterPipeline highlighters = null;

    // MUST ALWAYS ACCESS dataAdapter through accessor method!!!
    private final ComponentAdapter dataAdapter = new ListAdapter(this);

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

    public void setHighlighters(HighlighterPipeline pipeline) {
        highlighters = pipeline;
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
            throw new UnsupportedOperationException(
                "Method getFilteredValueAt() not yet implemented.");
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
