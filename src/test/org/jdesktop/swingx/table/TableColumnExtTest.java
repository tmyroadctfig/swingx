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

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

import junit.framework.TestCase;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.UIDependent;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.SerializableSupport;

/**
 * Unit test of enhanced <code>TableColumnExt</code>.
 * 
 * @author Jeanette Winzenburg
 */
public class TableColumnExtTest extends TestCase {

    /**
     * api change: let TableColumnExt implement UIDependent.
     */
    public void testUIDependent() {
        TableColumnExt columnExt = new TableColumnExt();
        assertTrue(columnExt instanceof UIDependent);
    }
    
    /**
     * Issue #822-swingx: replace cloneable by copy constructor.
     * Here: test base properties copied.
     */
    public void testCopyConstructor() {
        TableColumnExt columnExt = new TableColumnExt(10, 200, 
                new DefaultTableRenderer(), new DefaultCellEditor(new JTextField(20)));
        TableColumnExt copy = new TableColumnExt(columnExt);
        assertEquals(columnExt.getModelIndex(), copy.getModelIndex());
        assertEquals(columnExt.getWidth(), copy.getWidth());
        assertEquals(columnExt.getCellRenderer(), copy.getCellRenderer());
        assertEquals(columnExt.getCellEditor(), copy.getCellEditor());
    }
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
        // again - for going into the last untested line
        // but what to test?
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
        TableColumnExt copy = new TableColumnExt(columnExt);
        assertEquals("tooltip property must be cloned", columnExt.getToolTipText(),
                copy.getToolTipText());
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
        TableColumnExt copy = new TableColumnExt(columnExt);
        assertEquals("sortable property must be cloned", columnExt.isSortable(),
                copy.isSortable());
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
    public void testCopyComparator() {
        TableColumnExt tableColumn = new TableColumnExt();
        Comparator comparator = Collator.getInstance();
        tableColumn.setComparator(comparator);
        TableColumnExt clone = new TableColumnExt(tableColumn);
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
        TableColumnExt copy = new TableColumnExt(column);
        // sanity
        assertEquals("min-max of clone", copy.getMinWidth(), copy.getMaxWidth());
        assertFalse("must not be resizable with equal min-max", copy.getResizable());
        copy.setMinWidth(0);
        //sanity assert
        assertTrue("min < max", copy.getMinWidth() < copy.getMaxWidth());
        assertTrue("cloned base resizable", copy.getResizable());
    }

    /**
     * Issue #39-swingx:
     * Client properties not preserved when cloning.
     *
     */
    public void testCopyClientProperty() {
        TableColumnExt column = new TableColumnExt(0);
        String key = "property";
        Object value = new Object();
        column.putClientProperty(key, value);
        TableColumnExt copy = new TableColumnExt(column);
        assertEquals("client property must be in cloned", value, copy.getClientProperty(key));
        
        key = "single";
        column.putClientProperty(key, value);
        //sanity check
        assertSame(value, column.getClientProperty(key));
        
        assertNull("cloned client properties must be in independant",
                copy.getClientProperty(key));
    }
    //begin SwingX Issue #770 checks
    // PENDING JW: consider to remove the  "HighlighterClient"
    // related testing here - that part is done in TableColumnExtAsHighlighterClient in package
    // swingx.
    /**
     * Check for setHighlighters portion of #770.
     */
    public void testSetHighlighters() {
        TableColumnExt column = new TableColumnExt(0);
        PropertyChangeReport hcl = new PropertyChangeReport();
        column.addPropertyChangeListener(hcl);
        
        Highlighter h1 = new ColorHighlighter();
        Highlighter h2 = new ColorHighlighter();
        
        //sanity check
        assertEquals(0, hcl.getEventCount());
        
        //base case no highlighters
        assertSame(CompoundHighlighter.EMPTY_HIGHLIGHTERS, column.getHighlighters());
        
        column.setHighlighters(h1);
        assertEquals(1, hcl.getEventCount());
        assertEquals("highlighters", hcl.getLastProperty());
        assertEquals(1, column.getHighlighters().length);
        assertSame(h1, column.getHighlighters()[0]);
        
        //reset state
        hcl.clear();
        
        column.removeHighlighter(h1);
        assertEquals(1, hcl.getEventCount());
        assertEquals("highlighters", hcl.getLastProperty());
        //we have a compound, but empty highlighter
        assertEquals(0, column.getHighlighters().length);
        // JW: changed CompoundHighlighter to return its EMPTY_HIGHLIGHTERS if empty
        assertSame(CompoundHighlighter.EMPTY_HIGHLIGHTERS, column.getHighlighters());
        
        //reset state
        hcl.clear();
        
        column.setHighlighters(h1, h2);
        assertEquals(1, hcl.getEventCount());
        assertEquals("highlighters", hcl.getLastProperty());
        assertEquals(2, column.getHighlighters().length);
        assertSame(h1, column.getHighlighters()[0]);
        assertSame(h2, column.getHighlighters()[1]);
    }
    
    /**
     * Check for addHighlighter portion of #770.
     */
    public void testAddHighlighter() {
        TableColumnExt column = new TableColumnExt(0);
        PropertyChangeReport hcl = new PropertyChangeReport();
        column.addPropertyChangeListener(hcl);
        
        Highlighter h1 = new ColorHighlighter();
        Highlighter h2 = new ColorHighlighter();
        
        //sanity check
        assertEquals(0, hcl.getEventCount());
        
        //base case no highlighters
        assertSame(CompoundHighlighter.EMPTY_HIGHLIGHTERS, column.getHighlighters());
        
        column.addHighlighter(h1);
        assertEquals(1, hcl.getEventCount());
        assertEquals("highlighters", hcl.getLastProperty());
        assertEquals(1, column.getHighlighters().length);
        assertSame(h1, column.getHighlighters()[0]);
        
        //reset state
        hcl.clear();
        
        column.removeHighlighter(h1);
        assertEquals(1, hcl.getEventCount());
        assertEquals("highlighters", hcl.getLastProperty());
        //we have a compound, but empty highlighter
        assertEquals(0, column.getHighlighters().length);
        // JW: changed CompoundHighlighter to return its EMPTY_HIGHLIGHTERS if empty
        assertSame(CompoundHighlighter.EMPTY_HIGHLIGHTERS, column.getHighlighters());
        
        column.setHighlighters(h1);
        
        //reset state
        hcl.clear();
        
        column.addHighlighter(h2);
        assertEquals(1, hcl.getEventCount());
        assertEquals("highlighters", hcl.getLastProperty());
        assertEquals(2, column.getHighlighters().length);
        assertSame(h1, column.getHighlighters()[0]);
        assertSame(h2, column.getHighlighters()[1]);
    }
    
    /**
     * Check for removeHighlighter portion of #770.
     */
    public void testRemoveHighlighter() {
        TableColumnExt column = new TableColumnExt(0);
        PropertyChangeReport hcl = new PropertyChangeReport();
        column.addPropertyChangeListener(hcl);
        
        Highlighter h1 = new ColorHighlighter();
        Highlighter h2 = new ColorHighlighter();
        Highlighter h3 = new ColorHighlighter();
        
        //sanity check
        assertEquals(0, hcl.getEventCount());
        
        //ensure that nothing goes awry
        column.removeHighlighter(h1);
        assertEquals(0, hcl.getEventCount());
        
        column.setHighlighters(h1, h2, h3);
        
        //reset state
        hcl.clear();
        
        column.removeHighlighter(h2);
        assertEquals(1, hcl.getEventCount());
        assertEquals("highlighters", hcl.getLastProperty());
        assertEquals(2, column.getHighlighters().length);
        assertSame(h1, column.getHighlighters()[0]);
        assertSame(h3, column.getHighlighters()[1]);
    }
    
    /**
     * Check to ensure that the clone returns the highlighters correctly. Part of #770.
     */
    public void testCopyHighlighters() {
        TableColumnExt column = new TableColumnExt(0);
        Highlighter h1 = new ColorHighlighter();
        Highlighter h2 = new ColorHighlighter();
        Highlighter h3 = new ColorHighlighter();
        
        column.setHighlighters(h1, h2);
        
        TableColumnExt clone = new TableColumnExt(column);
        
        Highlighter[] columnHighlighters = column.getHighlighters();
        Highlighter[] cloneHighlighters = clone.getHighlighters();
        
        assertEquals(2, columnHighlighters.length);
        assertEquals(columnHighlighters.length, cloneHighlighters.length);
        assertSame(h1, columnHighlighters[0]);
        assertSame(columnHighlighters[0], cloneHighlighters[0]);
        assertSame(h2, columnHighlighters[1]);
        assertSame(columnHighlighters[1], cloneHighlighters[1]);
        
        column.addHighlighter(h3);
        
        columnHighlighters = column.getHighlighters();
        cloneHighlighters = clone.getHighlighters();
        
        assertEquals(3, columnHighlighters.length);
        assertEquals(columnHighlighters.length, cloneHighlighters.length + 1);
        assertSame(h1, columnHighlighters[0]);
        assertSame(columnHighlighters[0], cloneHighlighters[0]);
        assertSame(h2, columnHighlighters[1]);
        assertSame(columnHighlighters[1], cloneHighlighters[1]);
        assertSame(h3, columnHighlighters[2]);
    }
}
