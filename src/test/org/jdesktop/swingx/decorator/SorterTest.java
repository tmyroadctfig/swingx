/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.text.Collator;
import java.util.Locale;

import junit.framework.TestCase;

public class SorterTest extends TestCase {

    /**
     * initial addition.
     *
     */
    public void testSortKey() {
        int column = 3;
        SortOrder sortOrder = SortOrder.ASCENDING;
        SortKey sortKey = new SortKey(sortOrder, column);
        assertEquals(column, sortKey.getColumn());
        assertEquals(sortOrder, sortKey.getSortOrder());
    }
    
    /**
     * sanity - SortOrders convenience method state.
     *
     */
    public void testSortOrderConvenience() {
        assertTrue(SortOrder.ASCENDING.isSorted());
        assertTrue(SortOrder.ASCENDING.isAscending());
        assertTrue(SortOrder.DESCENDING.isSorted());
        assertFalse(SortOrder.DESCENDING.isAscending());
        assertFalse(SortOrder.UNSORTED.isSorted());
        assertFalse(SortOrder.UNSORTED.isAscending());
    }
    /**
     * test new method sorter.getSortOrder(), must be in synch with 
     * sorter.isAscending()
     *
     */
    public void testSortOrder() {
        Sorter sorter = new ShuttleSorter();
        assertSame(SortOrder.ASCENDING, sorter.getSortOrder());
        Sorter other = new ShuttleSorter(0, false);
        assertSame(SortOrder.DESCENDING, other.getSortOrder());
        other.setAscending(true);
        assertSame(SortOrder.ASCENDING, other.getSortOrder());
    }
    
    /**
     * Issue #179: make sure to use the correct default collator.
     * 
     */
    public void testCollator() {
        Locale defaultLocale = Locale.getDefault();
        Locale western = Locale.GERMAN;
        Locale eastern = Locale.CHINESE;
        Collator westernCol = Collator.getInstance(western);
        Collator easternCol = Collator.getInstance(eastern);
        // sanity assert: collators are different
        assertFalse(westernCol.equals(easternCol));
        Locale.setDefault(western);
        // sanity assert: default collator is western
        assertEquals(westernCol, Collator.getInstance());
        Sorter sorter = new ShuttleSorter();
        assertEquals("sorter must use collator default locale",
                Collator.getInstance(), sorter.getCollator());
        Locale.setDefault(eastern);
        // sanity assert: default collator is eastern
        assertEquals(easternCol, Collator.getInstance());
        sorter.toggle();
        assertEquals("collator must use default locale",
                Collator.getInstance(), sorter.getCollator());
        Locale.setDefault(defaultLocale);
        
    }
}
