/*
 * $Id$
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
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
 *
 */
package org.jdesktop.swingx.sort;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * A SortController to use for a JXTable.<p>
 * 
 * PENDING JW: should move up to DefaultRowSorter for re-use in ListRowSorter, but as long
 * as we don't have the latter ... 
 * 
 * @author Jeanette Winzenburg
 */
public class TableSortController<M extends TableModel> extends TableRowSorter<M> implements
        SortController {

    private final static SortOrder[] DEFAULT_CYCLE = new SortOrder[] {SortOrder.ASCENDING, SortOrder.DESCENDING};
    
    private SortOrder[] sortCycle = DEFAULT_CYCLE;

    private boolean sortable = true;
    
    public TableSortController() {
        this(null);
    }
    
    /**
     * @param model
     */
    public TableSortController(M model) {
        super(model);
    }

    @Override
    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }
    
    @Override
    public boolean isSortable() {
        return sortable;
    }
    
    @Override
    public void setSortable(int column, boolean sortable) {
        super.setSortable(column, sortable);
    }
    
    @Override
    public boolean isSortable(int column) {
        if (!isSortable()) return false;
        return super.isSortable(column);
    }
    
    /**
     * {@inheritDoc} <p>
     * 
     */
    @Override
    public void toggleSortOrder(int column) {
        super.toggleSortOrder(column);
    }

    /**
     * {@inheritDoc} <p>
     * 
     * PENDING JW: toggle has two effects: makes the column the primary sort column, 
     * and cycle through. So here we something similar. Should we?
     *   
     */
    @Override
    public void setSortOrder(int column, SortOrder sortOrder) {
        if (!isSortable(column)) return;
        SortKey replace = new SortKey(column, sortOrder);
        List<SortKey> keys = new ArrayList<SortKey>(getSortKeys());
        SortUtils.removeFirstSortKeyForColumn(keys, column);
        keys.add(0, replace);
        // PENDING max sort keys, respect here?
        setSortKeys(keys);
    }
    
    @Override
    public SortOrder getSortOrder(int column) {
        SortKey key = SortUtils.getFirstSortKeyForColumn(getSortKeys(), column);
        return key != null ? key.getSortOrder() : SortOrder.UNSORTED;
    }

    @Override
    public void resetSortOrders() {
        if (!isSortable()) return;
        List<SortKey> keys = new ArrayList<SortKey>(getSortKeys());
        for (int i = keys.size() -1; i >= 0; i--) {
            SortKey sortKey = keys.get(i);
            if (isSortable(sortKey.getColumn())) {
                keys.remove(sortKey);
            }
            
        }
        setSortKeys(keys);
        
    }
    

    /**
     * Not yet functional (unused internally).
     */
    @Override
    public SortOrder[] getSortOrderCycle() {
        // PENDING JW: ensure immutable or go enumeration?
        // 
        return sortCycle;
    }

    /**
     * Not yet functional (does nothing).
     */
    @Override
    public void setSortOrderCycle(SortOrder... cyle) {
        // TODO Auto-generated method stub
        
    }

}
