/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.table;

import java.io.IOException;
import java.text.Collator;
import java.util.Comparator;
import java.util.Date;

import junit.framework.TestCase;

import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.SerializableSupport;

/**
 * Unit test of enhanced <code>TableColumnExt</code>.
 * 
 * @author Jeanette Winzenburg
 */
public class TableColumnExtTest extends TestCase {

    /**
     * test remove
     *
     */
    public void testPutClientPropertyNullValue() {
        TableColumnExt columnExt = new TableColumnExt();
        Object value = new Object();
        String key = "some";
        columnExt.putClientProperty(key, value);
        // sanity: got it
        assertSame(value, columnExt.getClientProperty(key));
        columnExt.putClientProperty(key, null);
        assertNull(columnExt.getClientProperty(key));
    }
    /**
     * test doc'ed exceptions in putClientProperty.
     *
     */
    public void testPutClientPropertyExc() {
        TableColumnExt columnExt = new TableColumnExt();
        try {
            columnExt.putClientProperty(null, "somevalue");
            fail("put client property with null key must throw");
        } catch (IllegalArgumentException e) {
            // expected behaviour
        }        
    }
    /**
     * Sanity test Serializable.
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     * 
     */
    public void testSerializable() throws IOException, ClassNotFoundException {
        TableColumnExt columnExt = new TableColumnExt();
        Object value = new Date();
        columnExt.putClientProperty("date", value);
        TableColumnExt serialized = SerializableSupport.serialize(columnExt);
        assertTrue(serialized.isVisible());
        assertEquals(value, serialized.getClientProperty("date"));
        assertEquals(15, serialized.getMinWidth());
        assertTrue(serialized.getResizable());
    }

    /**
     * Issue #154-swingx.
     * 
     * added property headerTooltip. Test initial value, propertyChange
     * notification, cloned correctly.
     * 
     */
    public void testHeaderTooltip() {
        TableColumnExt columnExt = new TableColumnExt();
        columnExt.setTitle("mytitle");
        assertNull("tooltip is null initially", columnExt.getToolTipText());
        String toolTip = "some column text";
        PropertyChangeReport report = new PropertyChangeReport();
        columnExt.addPropertyChangeListener(report);
        columnExt.setToolTipText(toolTip);
        assertEquals(toolTip, columnExt.getToolTipText());
        assertEquals("must have fired one propertyChangeEvent for toolTipText ", 
                1, report.getEventCount("toolTipText"));
        TableColumnExt cloned = (TableColumnExt) columnExt.clone();
        assertEquals("tooltip property must be cloned", columnExt.getToolTipText(),
                cloned.getToolTipText());
    }
    
    /**
     * Test the sortable property: must fire propertyChange and
     * be cloned properly 
     *
     */
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
    
    /**
     * Issue #39-swingx:
     * Client properties not preserved when cloning.
     *
     */
    public void testClientPropertyClone() {
        TableColumnExt column = new TableColumnExt(0);
        String key = "property";
        Object value = new Object();
        column.putClientProperty(key, value);
        TableColumnExt cloned = (TableColumnExt) column.clone();
        assertEquals("client property must be in cloned", value, cloned.getClientProperty(key));
    }

}
