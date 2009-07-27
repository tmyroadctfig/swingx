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
import java.util.Arrays;
import java.util.List;

import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.util.Contract;

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

    private List<SortOrder> sortCycle;
    
    private boolean sortable;
    
    public TableSortController() {
        this(null);
    }
    
    /**
     * @param model
     */
    public TableSortController(M model) {
        super(model);
        setSortable(true);
        setSortOrderCycle(DEFAULT_CYCLE);
        setSortsOnUpdates(true);
    }

    /**
     * {@inheritDoc} <p>
     * 
     */
    @Override
    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }
    
    /**
     * {@inheritDoc} <p>
     * 
     */
    @Override
    public boolean isSortable() {
        return sortable;
    }
    
    /**
     * {@inheritDoc} <p>
     * 
     */
    @Override
    public void setSortable(int column, boolean sortable) {
        super.setSortable(column, sortable);
    }
    
    /**
     * {@inheritDoc} <p>
     * 
     */
    @Override
    public boolean isSortable(int column) {
        if (!isSortable()) return false;
        return super.isSortable(column);
    }
    
    /**
     * {@inheritDoc}
     * <p>
     * 
     * Overridden - that is completely new implementation - to get first/next SortOrder
     * from sort order cycle. Does nothing if the cycle is empty. 
     */
    @Override
    public void toggleSortOrder(int column) {
        checkColumn(column);
        if (!isSortable(column))
            return;
        SortOrder firstInCycle = getFirstInCycle();
        // nothing to toggle through
        if (firstInCycle == null)
            return;
        List<SortKey> keys = new ArrayList<SortKey>(getSortKeys());
        SortKey sortKey = SortUtils.getFirstSortKeyForColumn(keys, column);
        if (keys.indexOf(sortKey) == 0)  {
            //  primary key: in this case we'll use next sortorder in cylce
            keys.set(0, new SortKey(column, getNextInCycle(sortKey.getSortOrder())));
        } else {
            // all others: make primary with first sortOrder in cycle
            keys.remove(sortKey);
            keys.add(0, new SortKey(column, getFirstInCycle()));
        }
        if (keys.size() > getMaxSortKeys()) {
            keys = keys.subList(0, getMaxSortKeys());
        }
        setSortKeys(keys);
    }
    

    /**
     * Returns the next SortOrder relative to the current, or null
     * if the sort order cycle is empty. 
     * 
     * @param current the current SortOrder
     * @return the next SortOrder to use, may be null if the cycle is empty.
     */
    private SortOrder getNextInCycle(SortOrder current) {
        int pos = sortCycle.indexOf(current);
        if (pos < 0) {
            // not in cycle ... what to do?
            return getFirstInCycle();
        }
        pos++;
        if (pos >= sortCycle.size()) {
            pos = 0;
        }
        return sortCycle.get(pos);
    }

    /**
     * Returns the first SortOrder in the sort order cycle, or null if empty.
     * 
     * @return the first SortOrder in the sort order cycle or null if empty.
     */
    private SortOrder getFirstInCycle() {
        return sortCycle.size() > 0 ? sortCycle.get(0) : null;
    }

    private void checkColumn(int column) {
        if (column < 0 || column >= getModelWrapper().getColumnCount()) {
            throw new IndexOutOfBoundsException(
                    "column beyond range of TableModel");
        }
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
    
    /**
     * {@inheritDoc} <p>
     * 
     */
    @Override
    public SortOrder getSortOrder(int column) {
        SortKey key = SortUtils.getFirstSortKeyForColumn(getSortKeys(), column);
        return key != null ? key.getSortOrder() : SortOrder.UNSORTED;
    }

    /**
     * {@inheritDoc} <p>
     * 
     */
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
     * {@inheritDoc} <p>
     */
    @Override
    public SortOrder[] getSortOrderCycle() {
        return sortCycle.toArray(new SortOrder[0]);
    }

    /**
     * {@inheritDoc} <p>
     */
    @Override
    public void setSortOrderCycle(SortOrder... cycle) {
        Contract.asNotNull(cycle, "Elements of SortOrderCycle must not be null");
        // JW: not safe enough?
        sortCycle = Arrays.asList(cycle);
    }

}
