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

package org.jdesktop.swingx.table;

import java.util.List;
import java.util.Set;

import javax.swing.table.TableColumnModel;

/**
 * An extension to the TableColumnModel that adds logic dealing with the
 * "visibilty" property of the TableColumnExt. If a column is made invisible,
 * then the ColumnModel must not return that column in subsequent calls to
 * <code>getColumnXXX</code>. In other words, it is "silently" removed from
 * the column model until it is again made visible. However, a change in the
 * visibility status of a column will trigger an event so that the underlying
 * JTable can repaint/relayout itself.
 *
 * The semantics behind removing a column and making it invisible are two
 * different things. When a column is removed, it is no longer associated
 * with the model in any way, whereas an invisible column is simply not
 * displayed. This is somewhat similar to the idea of column ordering being
 * different in the model from the view.
 *
 * @author Richard Bair
 */
public interface TableColumnModelExt extends TableColumnModel {
    /**
     * Returns a list containing all of the columns that are invisible. If
     * no columns in this model are invisible, then this will be an empty
     * list.
     */
    public Set getInvisibleColumns();
    
    
    /**
     * Returns all of the columns in the TableColumnModel, including invisible
     * ones if the parameter is true.
     * @param includeHidden if true the returned list contains all columns 
     *  otherwise only the subset of visible columns.
     * 
     */
    public List getColumns(boolean includeHidden);
    
    /**
     * returns the first TableColumnExt with the given identifier or null
     * if none is found. The returned column may be visible or hidden.
     *  
     * @param identifier
     * @return first <code>TableColumnExt</code> with the given identifier
     * or null if none is found
     */
    public TableColumnExt getColumnExt(Object identifier);
    
    /**
     * returns the number of contained columns including invisible 
     *  if the parameter is true.
     * 
     * @param includeHidden 
     * @return the number of contained columns including the invisible
     * ones if specified
     */
    public int getColumnCount(boolean includeHidden);
}
