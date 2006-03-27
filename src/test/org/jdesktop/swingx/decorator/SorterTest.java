/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import junit.framework.TestCase;

/**
 * Unit test for divers sort-related classes/issues.
 * 
 * <ul>
 * <li> SortKey
 * <li> SortOrder
 * <li> Sorter
 * </ul>
 * @author Jeanette Winzenburg
 */
public class SorterTest extends TestCase {

    /**
     * test that sorter returns SortKey 
     * synched to internal state.
     *
     */
    public void testSorterSynchedToSortKey() {
        int column = 1;
        Sorter sorter = new ShuttleSorter(column, false, Collator.getInstance());
        SortKey sortKey = sorter.getSortKey();
        assertSorterSortKeySynched(sortKey, sorter);
    }

    /**
     * test that sorter updates internal state from SortKey.
     *
     */
    public void testSorterSynchedFromSortKey() {
        // create a sorter for column 0, ascending, 
        // without explicit comparator
        Sorter sorter = new ShuttleSorter();
        SortKey sortKey = new SortKey(SortOrder.DESCENDING, 1, Collator.getInstance());
        sorter.setSortKey(sortKey);
        assertSorterSortKeySynched(sortKey, sorter);
    }
    
    public static void assertSorterSortKeySynched(SortKey sortKey, Sorter sorter) {
        assertNotNull(sorter);
        assertEquals(sortKey.getColumn(), sorter.getColumnIndex());
        assertTrue(sortKey.getSortOrder().isSorted(sorter.isAscending()));
        assertEquals(sortKey.getSortOrder().isAscending(), sorter.isAscending());
        assertSame(sortKey.getComparator(), sorter.getComparator());
        
    }
    /**
     * test that sorter.setSortKey(..) throws the documented exceptions.
     *
     */
    public void testSorterSortKeyExceptions() {
        Sorter sorter = new ShuttleSorter();
        try {
            sorter.setSortKey(null);
            fail("sorter must throw IllegalArgument for null SortKey");
        } catch (IllegalArgumentException e) {
            // this is documented behaviour
        } catch (Exception e) {
            fail("unexpected exception for null Sortkey" + e);
        }
        try {
            SortKey sortKey = new SortKey(SortOrder.UNSORTED, 0, Collator.getInstance());
            sorter.setSortKey(sortKey);
            fail("sorter must throw IllegalArgument for unsorted SortKey");
        } catch (IllegalArgumentException e) {
            // this is documented behaviour
        } catch (Exception e) {
            fail("unexpected exception for unsorted Sortkey" + e);
        }
        
    }
    /**
     * initial addition.
     * Testing exceptions thrown in constructors
     */
    public void testSortKeyConstructorExceptions() {
        try {
            new SortKey(null, 2);
            fail("SortKey must throw IllegalArgument for null SortOrder");
        } catch (IllegalArgumentException e) {
            // this is documented behaviour
        } catch (Exception e) {
            fail("unexpected exception in SortKey with null SortOrder" + e);
        }
        try {
            new SortKey(SortOrder.ASCENDING, -1);
            fail("SortKey must throw IllegalArgument for negative column");
        } catch (IllegalArgumentException e) {
            // this is documented behaviour
        } catch (Exception e) {
            fail("unexpected exception in SortKey with negative column" + e);
        }
    }

    /**
     * initial addition, test constructors parameters.
     */
    public void testSortKeyConstructor() {
        int column = 3;
        SortOrder sortOrder = SortOrder.ASCENDING;
        // two parameter constructor
        SortKey sortKey = new SortKey(sortOrder, column);
        assertEquals(column, sortKey.getColumn());
        assertEquals(sortOrder, sortKey.getSortOrder());
        assertNull(sortKey.getComparator());
        Comparator comparator = Collator.getInstance();
        // three parameter constructor
        sortKey = new SortKey(sortOrder, column, comparator);
        assertEquals(column, sortKey.getColumn());
        assertEquals(sortOrder, sortKey.getSortOrder());
        assertSame(comparator, sortKey.getComparator());
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
        // wanted: ascending sort
        assertEquals(false, SortOrder.UNSORTED.isSorted(true));
        assertEquals(false, SortOrder.DESCENDING.isSorted(true));
        assertEquals(true, SortOrder.ASCENDING.isSorted(true));
        // wanted: descending sort
        assertEquals(false, SortOrder.UNSORTED.isSorted(false));
        assertEquals(true, SortOrder.DESCENDING.isSorted(false));
        assertEquals(false, SortOrder.ASCENDING.isSorted(false));
        
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
