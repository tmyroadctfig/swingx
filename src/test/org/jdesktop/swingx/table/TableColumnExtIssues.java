/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.table;

import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.table.TableColumn;

import junit.framework.TestCase;

import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.SerializableSupport;
import org.jdesktop.test.TestUtils;

/**
 * Test to exposed known issues of <code>TableColumnExt</code>.
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 *  
 * @author Jeanette Winzenburg
 */
public class TableColumnExtIssues extends TestCase {

    /**
     * Issue #815-swingx: Listeners must not be cloned.
     * Sanity: test that listeners registered with the clone are not
     * notified when changing the original.
     */
    public void testListenerNotificationOrigChanged() {
        TableColumnCloneable column = new TableColumnCloneable();
        column.setPreferredWidth(column.getMinWidth());
        TableColumnCloneable clone = (TableColumnCloneable) column.clone();
        PropertyChangeReport report = new PropertyChangeReport();
        clone.addPropertyChangeListener(report);
        column.setPreferredWidth(column.getPreferredWidth() + 10);
        assertEquals(0, report.getEventCount());
    }
    
    /**
     * Issue #815-swingx: Listeners must not be cloned.
     * test that listeners registered with the original are not notified
     * when changing the clone.
     */
    public void testListenerNotificationCloneChanged() {
        TableColumnCloneable column = new TableColumnCloneable();
        column.setPreferredWidth(column.getMinWidth());
        PropertyChangeReport report = new PropertyChangeReport();
        column.addPropertyChangeListener(report);
        TableColumnCloneable clone = (TableColumnCloneable) column.clone();
        clone.setPreferredWidth(column.getPreferredWidth() + 10);
        assertEquals(0, report.getEventCount());
    }
    
    /**
     * TableColumn sub with a better-behaved clone - removes the cloned listeners
     */
    public static class TableColumnCloneable extends TableColumn implements Cloneable {

        @Override
        public Object clone()  {
            try {
                TableColumn column = (TableColumn) super.clone();
                for (int i = 0; i < getPropertyChangeListeners().length; i++) {
                    column.removePropertyChangeListener(getPropertyChangeListeners()[i]);
                }
                return column;
            } catch (CloneNotSupportedException e) { // don't expect
            }
            return null;
        }
        
        
    }
    /**
     * Issue #815-swingx: Listeners must not be cloned.
     */
    public void testChangeNofification() {
        TableColumnExt column = new TableColumnExt(0);
        column.setMaxWidth(1000);
        PropertyChangeReport r = new PropertyChangeReport();
        column.addPropertyChangeListener(r);
        TableColumnExt clone = (TableColumnExt) column.clone();
        // change the clone
        clone.setMinWidth(44);
        assertEquals("listener must not be notified on changes of the clone", 
                0, r.getEventCount());
    }

    /**
     * Issue #??-swingx: tableColumnExt does not fire propertyChange on resizable.
     * 
     * Happens, if property is changed indirectly by changing min/max value
     * to be the same.
     *
     */
    public void testResizableBoundProperty() {
        TableColumnExt columnExt = new TableColumnExt();
        // sanity: assert expected defaults of resizable, minWidth
        assertTrue(columnExt.getResizable());
        assertTrue(columnExt.getMinWidth() > 0);
        PropertyChangeReport report = new PropertyChangeReport();
        columnExt.addPropertyChangeListener(report);
        columnExt.setMaxWidth(columnExt.getMinWidth());
        if (!columnExt.getResizable()) {
            assertEquals("fixing column widths must fire resizable ", 
                    1, report.getEventCount("resizable"));
        } else {
           fail("resizable must respect fixed column width"); 
        }
        
    }
    
    /**
     * Sanity test Serializable: Listeners? Report not serializable?
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     * 
     */
    public void testSerializable() throws IOException, ClassNotFoundException {
        TableColumnExt columnExt = new TableColumnExt();
        PropertyChangeReport report = new PropertyChangeReport();
        columnExt.addPropertyChangeListener(report);
        TableColumnExt serialized = SerializableSupport.serialize(columnExt);
        PropertyChangeListener[] listeners = serialized
                .getPropertyChangeListeners();
        assertTrue(listeners.length > 0);
    }


    /**
     * Issue #??-swingx: must handle non-serializable client properties
     * gracefully.
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     * 
     * 
     */
    public void testNonSerializableClientProperties() throws IOException, ClassNotFoundException {
        TableColumnExt columnExt = new TableColumnExt();
        Object value = new Object();
        columnExt.putClientProperty("date", value );
        SerializableSupport.serialize(columnExt);
     }


    /**
     * Dummy stand-in test method, does nothing. 
     * without, the test would fail if there are no open issues.
     *
     */
    public void testDummy() {
    }
}
