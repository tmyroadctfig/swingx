/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import javax.swing.JComponent;

/**
 * Abstract base class for all component data adapter classes.
 * A <code>ComponentAdapter</code> allows a {@link Filter}, {@link Sorter},
 * or {@link Highlighter} to interact with a {@link #target} component through a
 * common API.
 *
 * @author Ramesh Gupta
 */
public abstract class ComponentAdapter {
    /** current row in view coordinates (?) */
    public int row = 0;
    /** current column in view coordinates */
    public int column = 0;
    protected final JComponent	target;

    /**
     * Constructs a ComponentAdapter, setting the specified component as the
     * target component.
     *
     * @param component target component for this adapter
     */
    public ComponentAdapter(JComponent component) {
        target = component;
    }

    public JComponent getComponent() {
        return target;
    }

    /**
     * JW: doc unclear - label or identifier? TableCA implements 
     * as column.headerValue (which is "label")... This wrecks
     * ColumnHighlighter because JNTable stores the column props with
     * column "logical name" as key. Boing :-(
     * 
     * Filter delegates to this in getColumnName. Filter.getColumnName is
     * used in JXSearchPanel to fill the field with the column name - this
     * looks as if it's meant to be the name...
     * 
     * @param columnIndex in view coordinates
     * @return column name
     */
    public abstract String getColumnName(int columnIndex);

    /**
     * returns the logical name (== identifier) of the column at 
     * columnIndex in view
     * coordinates.
     * 
     * JW: added to have a very clear contract without disturbing
     * current implementations.
     * 
     * @param columnIndex in view coordinates
     * @return the String value of the column identifier at columnIndex
     *   or null if no identifier set
     */
    public abstract String getColumnIdentifier(int columnIndex);
    /**
     * Returns the number of columns in the target component's view.
     *
     * @return the number of columns in the target component's view
     */
    public int getColumnCount() {
        return 1;	// default for combo-boxes, lists, and trees
    }

    /**
     * Returns the number of rows in the target component's view.
     *
     * @return the number of rows in the target component's view
     */
    public int getRowCount() {
        return 0;
    }

    /**
     * Returns the value of the cell identified by this adapter by invoking
     * {@link #getValueAt(int, int)}, passing in the {@link #row} and
     * {@link #column} values of this adapter. For target components that don't
     * support multiple columns, the value of <code>column</code> is always zero.
     *
     * @return the value of the cell identified by this adapter
     */
    public Object getValue() {
        return getValueAt(row, column);
    }

    /**
     * Returns the value of the target component's cell identified by the
     * specified row and column.
     *
     * @param row
     * @param column
     * @return the value of the target component's cell identified by the
     * specified row and column
     */
    public abstract Object getValueAt(int row, int column);
    public abstract Object getFilteredValueAt(int row, int column);
    public abstract void setValueAt(Object aValue, int row, int column);

    public abstract boolean isCellEditable(int row, int column);

    /**
     * Returns true if the cell identified by this adapter currently has focus;
     * Otherwise, it returns false.
     *
     * @return true if the cell identified by this adapter currently has focus;
     * 	Otherwise, return false
     */
    public abstract boolean hasFocus();

    /**
     * Returns true if the cell identified by this adapter is currently selected;
     * Otherwise, it returns false.
     *
     * @return true if the cell identified by this adapter is currently selected;
     * 	Otherwise, return false
     */
    public abstract boolean isSelected();

    /**
     * Returns true if the cell identified by this adapter is currently expanded;
     * Otherwise, it returns false. For components that do not support
     * hierarchical data, this method always returns true because the cells in
     * such components can never be collapsed.
     *
     * @return true if the cell identified by this adapter is currently expanded;
     * 	Otherwise, return false
     */
    public boolean isExpanded() {
        return true; // sensible default for JList and JTable
    }

    /**
     * Returns true if the cell identified by this adapter is a leaf node;
     * Otherwise, it returns false. For components that do not support
     * hierarchical data, this method always returns true because the cells in
     * such components can never have children.
     *
     * @return true if the cell identified by this adapter is a leaf node;
     * 	Otherwise, return false
     */
    public boolean isLeaf() {
        return true; // sensible default for JList and JTable
    }

    /**
     * Returns true if the cell identified by this adapter displays the hierarchical node;
     * Otherwise, it returns false. For components that do not support
     * hierarchical data, this method always returns false because the cells in
     * such components can never have children.
     *
     * @return true if the cell identified by this adapter displays the hierarchical node;
     * 	Otherwise, return false
     */
    public boolean isHierarchical() {
        return false; // sensible default for JList and JTable
    }

    /**
     * For target components that support multiple columns in their model,
     * along with column reordering in the view, this method transforms the
     * specified columnIndex from model coordinates to view coordinates. For all
     * other types of target components, this method returns the columnIndex
     * unchanged.
     *
     * @param columnIndex index of a column in model coordinates
     * @return index of the specified column in view coordinates
     */
    public int modelToView(int columnIndex) {
        return columnIndex; // sensible default for JList and JTree
    }

    /**
     * For target components that support multiple columns in their model,
     * along with column reordering in the view, this method transforms the
     * specified columnIndex from view coordinates to model coordinates. For all
     * other types of target components, this method returns the columnIndex
     * unchanged.
     *
     * @param columnIndex index of a column in view coordinates
     * @return index of the specified column in model coordinates
     */
    public int viewToModel(int columnIndex) {
        return columnIndex; // sensible default for JList and JTree
    }

    public void refresh() {
        target.revalidate();
        target.repaint();
    }
}