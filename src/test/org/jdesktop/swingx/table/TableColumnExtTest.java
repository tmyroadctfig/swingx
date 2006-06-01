/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.table;

import java.text.Collator;
import java.util.Comparator;

import junit.framework.TestCase;

import org.jdesktop.swingx.util.PropertyChangeReport;

/**
 * @author Jeanette Winzenburg
 */
public class TableColumnExtTest extends TestCase {

    public void testSortable() {
        TableColumnExt columnExt = new TableColumnExt();
        boolean sortable = columnExt.isSortable();
        assertTrue("columnExt isSortable by default", sortable);
        PropertyChangeReport report = new PropertyChangeReport();
        columnExt.addPropertyChangeListener(report);
        columnExt.setSortable(!sortable);
        // sanity assert: the change was taken
        assertEquals(sortable, !columnExt.isSortable());
        assertEquals("must have fired one propertyChangeEvent for sortable ", 
                1, report.getEventCount("sortable"));
        TableColumnExt cloned = (TableColumnExt) columnExt.clone();
        assertEquals("sortable property must be cloned", columnExt.isSortable(),
                cloned.isSortable());
    }
    
    /**
     * Issue #273-swingx: make Comparator a bound property of TableColumnExt.
     * (instead of client property)
     *
     * test if comparator is taken initially. This test doesn't make much sense
     * after removing the sorter property because there's nothing to synch any 
     * more 
     * 
     * TODO remove!
     */
    public void testInitialComparator() {
        TableColumnExt tableColumn = new TableColumnExt();
        Comparator comparator = Collator.getInstance();
        tableColumn.setComparator(comparator);
        assertEquals(comparator, tableColumn.getComparator());
    }
    
    
    /**
     * Issue #273-swingx: make Comparator a bound property of TableColumnExt.
     * (instead of client property)
     *
     * test if setting comparator fires propertyChange. 
     */
    public void testComparatorBoundProperty() {
        TableColumnExt tableColumn = new TableColumnExt();
        PropertyChangeReport report = new PropertyChangeReport();
        tableColumn.addPropertyChangeListener(report);
        Comparator comparator = Collator.getInstance();
        tableColumn.setComparator(comparator);
        assertTrue(report.hasEvents());
        assertEquals(1, report.getEventCount("comparator"));
    }

    /**
     * Issue #273-swingx: make Comparator a bound property of TableColumnExt.
     * (instead of client property)
     *
     * test if comparator is cloned. 
     */
    public void testCloneComparator() {
        TableColumnExt tableColumn = new TableColumnExt();
        Comparator comparator = Collator.getInstance();
        tableColumn.setComparator(comparator);
        TableColumnExt clone = (TableColumnExt) tableColumn.clone();
        assertEquals(comparator, clone.getComparator());
    }

   /**
     * Issue #280-swingx: tableColumnExt doesn't fire propertyChange on
     * putClientProperty.
     * 
     */
    public void testClientPropertyNotification() {
        TableColumnExt tableColumn = new TableColumnExt();
        PropertyChangeReport report = new PropertyChangeReport();
        tableColumn.addPropertyChangeListener(report);
        Object value = new Integer(3);
        tableColumn.putClientProperty("somevalue", value);
        assertTrue(report.hasEvents());
        assertEquals(1, report.getEventCount("somevalue"));
    }
    
    /**
     * Issue #279-swingx: getTitle throws NPE.
     *
     */
    public void testTitle() {
        TableColumnExt tableColumn = new TableColumnExt();
        tableColumn.getTitle();
    }
    
    /**
     * user friendly resizable flag.
     * 
     */
    public void testResizable() {
        TableColumnExt column = new TableColumnExt(0);
        //sanity assert
        assertTrue("min < max", column.getMinWidth() < column.getMaxWidth());
        // sanity assert
        assertTrue("resizable default", column.getResizable());
        column.setMinWidth(column.getMaxWidth());
        assertFalse("must not be resizable with equal min-max", column.getResizable());
        TableColumnExt clone = (TableColumnExt) column.clone();
        // sanity
        assertEquals("min-max of clone", clone.getMinWidth(), clone.getMaxWidth());
        assertFalse("must not be resizable with equal min-max", clone.getResizable());
        clone.setMinWidth(0);
        //sanity assert
        assertTrue("min < max", clone.getMinWidth() < clone.getMaxWidth());
        assertTrue("cloned base resizable", clone.getResizable());
    }
}
