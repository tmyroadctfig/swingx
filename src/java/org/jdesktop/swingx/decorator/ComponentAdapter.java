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

import javax.swing.JComponent;

/**
 * Abstract base class for all component data adapter classes.
 * A <code>ComponentAdapter</code> allows a {@link Filter}, {@link Sorter},
 * or {@link Highlighter} to interact with a {@link #target} component through a
 * common API.
 * 
 * It has two aspects:
 * <ul>
 * <li> interact with the data of the component. The methods for this are those 
 * taking row/column indices as parameters. The coordinates
 * are in model coordinate system. Typical clients of are Filters.
 * <li> interact with the view state for a given data element. The row/cloumn fields and the
 * parameterless methods service this aspect. The coordinates are in view coordinate system.
 * Typical clients are the highlighting part of Highlighters.
 * </ul>
 * 
 * The adapter is responsible for mapping column coordinates. 
 * 
 * All input column 
 * indices are in model coordinates with exactly two exceptions:
 * <ul>
 * <li> {@link #column} in column view coordinates
 * <li> the mapping method {@link #viewToModel(int)} in view coordinates
 * </ul>
 * 
 * All input row indices are in model coordinates with exactly two exceptions:
 * <ul>
 * <li> {@link #row} in row view coordinates
 * <li> the getter for the filtered value {@link #getFilteredValueAt(int, int)} 
 * takes the row in view coordinates.
 * </ul>
 *  
 * 
 * @author Ramesh Gupta
 * @author Karl Schaefer
 */
public abstract class ComponentAdapter {
    /** current row in view coordinates. */
    public int row = 0;
    /** current column in view coordinates. */
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

//---------------------------- accessing the target's model
    
    /**
     * returns the column's label (= headerValue).
     * 
     * Used f.i. in SearchPanel to fill the field with the 
     * column name.
     * 
     * Note: it's up to the implementation to decide for which
     * columns it returns a name - most will do so for the
     * subset with isTestable = true.
     * 
     * @param columnIndex in model coordinates
     * @return column name or null if not found/not testable.
     */
    public abstract String getColumnName(int columnIndex);

    /**
     * returns the logical name (== identifier) of the column at 
     * columnIndex in model coordinates.
     * 
     * Used f.i. JNTable to store and apply column properties by identifier.
     * 
     * Note: it's up to the implementation to decide for which
     * columns it returns a name - most will do so for the
     * subset with isTestable = true.
     * 
     * @param columnIndex in model coordinates
     * @return the String value of the column identifier at columnIndex
     *   or null if no identifier set
     */
    public abstract String getColumnIdentifier(int columnIndex);

    /**
     * Returns the number of columns in the target's data model.
     *
     * @return the number of columns in the target's data model.
     */
    public int getColumnCount() {
        return 1;	// default for combo-boxes, lists, and trees
    }

    /**
     * Returns the number of rows in the target's data model.
     *
     * @return the number of rows in the target's data model.
     */
    public int getRowCount() {
        return 0;
    }

    /**
     * Returns the value of the target component's cell identified by the
     * specified row and column in model coordinates.
     *
     * @param row in model coordinates
     * @param column in model coordinates
     * @return the value of the target component's cell identified by the
     * specified row and column
     */
    public abstract Object getValueAt(int row, int column);
    public abstract void setValueAt(Object aValue, int row, int column);

    /**
	 * Determines whether this cell is editable.
	 * <p>
	 * This method is for use with {@code Filter}s.
	 * 
	 * @param row
	 *            the row to query
	 * @param column
	 *            the column to query
	 * @return <code>true</code> if the cell is editable, <code>false</code>
	 *         otherwise
	 */
    public abstract boolean isCellEditable(int row, int column);

    /**
     * returns true if the column should be included in testing.
     * Here: returns true if visible (that is modelToView gives a valid
     * view column coordinate).
     * 
     * @param column in model coordinates
     * @return true if the column should be included in testing
     */
    public  boolean isTestable(int column) {
        return modelToView(column) >= 0;
    }
    
//----------------------- accessing the target's view state
    
    /**
     * Returns the value of the cell identified by this adapter by invoking
     * {@link #getValueAt(int, int)}, passing in the {@link #row} and
     * {@link #column} values of this adapter. For target components that don't
     * support multiple columns, the value of <code>column</code> is always zero.
     *
     * PENDING: needs clarification/cleanup - getValueAt(row, column) expects 
     * model coordinates!.
     * 
     * @return the value of the cell identified by this adapter
     */
    public Object getValue() {
        return getValueAt(row, column);
    }

    /**
     * returns the filtered value of the cell identified by the row
     * in view coordinate and the column in model coordinates.
     * 
     * Note: the asymetry of the coordinates is intentional - clients like
     * Highlighters are interested in view values but might need to access
     * non-visible columns for testing.
     * 
     * @param row
     * @param column
     * @return the filtered value of the cell identified by the row
     * in view coordinate and the column in model coordiantes
     */
    public abstract Object getFilteredValueAt(int row, int column);

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
     * Returns {@code true} if the cell identified by this adapter is editable,
     * {@code false} otherwise.
     * 
     * @return {@code true} if the cell is editable, {@code false} otherwise
     */
    public abstract boolean isEditable();
    
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
	 * Returns the depth of this row in the hierarchy where the root is 0. For
	 * components that do not contain hierarchical data, this method returns 1.
	 * 
	 * @return the depth for this adapter
	 */
    public int getDepth() {
        return 1; // sensible default for JList and JTable
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