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

import org.jdesktop.swingx.JXTable;

/**
 * Defines the interactive sort control for a table. All sort gesture requests
 * from a {@link JXTable} will be routed through the SortController returned
 * from {@link FilterPipeline}.
 * <p>
 * 
 * 
 *  
 * <p>
 * The GlazedLists project (http://publicobject.com/glazedlists/) 
 * has an example about how to replace the SwingX'
 * internal (view based) by an external 
 * (model-decoration based) sort/filter mechanism. 
 *  
 * <p>
 * This interface is inspired by a Java 1.6 class RowSorter, extracting
 *  the sort control part - change notification and index mapping is left to the 
 *  enclosing FilterPipeline. 
 * 
 *  @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 * 
 */
public interface SortController {

    /**
     * Reverses the sort order of the specified column. 
     * It is up to implementating classes to provide the exact behavior when invoked.
     *
     * @param column the model index of the column to toggle
     */
    void toggleSortOrder(int column);

 
    /**
     * Get the sort order of the specified column.
     * 
     * 
     * @return one of {@link SortOrder#ASCENDING},
     *     {@link SortOrder#DESCENDING} or {@link SortOrder#UNSORTED}.
     */
    SortOrder getSortOrder(int column);

    void setSortOrder(int column, SortOrder sortOrder);
    
    void removeAll();
}