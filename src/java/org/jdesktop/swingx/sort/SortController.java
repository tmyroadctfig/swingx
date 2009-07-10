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

package org.jdesktop.swingx.sort;

import javax.swing.SortOrder;

/**
 * Defines the interactive sort control for sortable collection components (like 
 * JXList, JXTable). All sort gesture requests from their sort api 
 * are routed through the SortController.
 * <p>
 * 
 * This is very-much work-in-progress: while moving from ol' SwingX sorting to 
 * core jdk6 sorting we need a hook for sorting api on the view. So in terms of
 * jdk6 classes, this is something like:<p>
 * 
 * <code><pre>
 * SortController == DefaultRowSorter - RowSorter + XX
 * </pre></code>
 * All methods which change sort state must respect per-controller and per-column 
 * sortable property, as follows
 * <ol>
 * <li> if per-controller sortable is false, do nothing
 * <li> if per-controller sortable is true, if per-column sortable is false, do nothing
 * <li> if both are true toggle the SortOrder of the given column
 * </ol>
 *
 * PENDING JW: add RowFilter support<p>
 * 
 * 
 *  @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 *  @author Jeanette Winzenburg
 * 
 */
public interface SortController {

    /**
     * Sets whether or not this controller is sortable.<p>
     * 
     * The default is true.<p>
     * 
     * PENDING JW: define behaviour if sortable is disabled while has sortOrders.
     * In this case JXTable resets all sorts.
     * 
     * @param sortable whether or not this controller is sortable
     * @see #isSortable()        
     */
    void setSortable(boolean sortable);
    
    /**
     * Returns true if this controller is sortable; otherwise, false.
     *
     * @return true if this controller is sortable
     *         
     * @see #isSortable()
     */
    boolean isSortable();
    
    /**
     * Sets whether or not the specified column is sortable.<p>
     * 
     * The default is true.<p>
     * 
     * PENDING JW: define behaviour if sortable is disabled while has sortOrders.
     * In this case JXTable removes the sort of the column.<p>
     * 
     * PENDING JW: decide whether or not this method should trigger a resort
     * DefaultRowSorter explicitly doesn't, JXTable does.
     * 
     * @param column the column to enable or disable sorting on, in terms
     *        of the underlying model
     * @param sortable whether or not the specified column is sortable
     * @throws IndexOutOfBoundsException if <code>column</code> is outside
     *         the range of the model
     * @see #isSortable(int)        
     * @see #toggleSortOrder(int)
     * @see #setSortOrder(int, SortOrder)
     */
    void setSortable(int column, boolean sortable);
    
    /**
     * Returns true if the specified column is sortable. <p>
     * This returns true if both the controller's sortable property and
     * the column's sortable property is true. Returns false if any of
     * them is false.
     *
     * @param column the column to check sorting for, in terms of the
     *        underlying model
     * @return true if the column is sortable
     * @throws IndexOutOfBoundsException if column is outside
     *         the range of the underlying model
     *         
     * @see #isSortable(int)
     */
    boolean isSortable(int column);

    
    /**
     * Reverses the sort order of the specified column. The exact behaviour is
     * up to implementations.<p>
     * 
     * Implementations must respect the per-controller and per-column-sortable 
     * property.
     * 
     * @param column the model index of the column to toggle
     * @see #isSortable(int) 
     * @see #isSortable()
     */
    void toggleSortOrder(int column);

    /**
     * Sets the sort order of the specified column. <p>
     * 
     * Implementations must respect the per-controller and per-column-sortable 
     * property.
     *
     * @param column the model index of the column to set
     * @param sortOrder the SortOrder to set for the column
     * 
     * @see #isSortable(int) 
     * @see #isSortable()
     */
    void setSortOrder(int column, SortOrder sortOrder);
 
    /**
     * Returns the sort order of the specified column.
     * 
     * 
     * @return one of {@link SortOrder#ASCENDING},
     *     {@link SortOrder#DESCENDING} or {@link SortOrder#UNSORTED}.
     */
    SortOrder getSortOrder(int column);

    
    /**
     * Resets all interactive sorting. <p>
     * 
     * Implementations must respect the per-controller and per-column-sortable 
     * property.
     * 
     */
    void resetSortOrders();
    
    /**
     * Sets the cycle of sort ordes to toggle through.
     * 
     * @param cyle
     */
    void setSortOrderCycle(SortOrder... cyle);
    
    /**
     * Returns the cycle of sort orders to cycle through.
     * 
     * @return
     */
    SortOrder[] getSortOrderCycle();
}