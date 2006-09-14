/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.table;

import java.beans.PropertyChangeEvent;
import java.util.EventListener;
import java.util.logging.Logger;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import junit.framework.TestCase;

import org.jdesktop.swingx.event.TableColumnModelExtListener;
import org.jdesktop.swingx.util.ColumnModelReport;

/**
 * Skeleton to unit test DefaultTableColumnExt.
 * 
 * Incomplete list of issues to test: 
 *   fired added after setVisible(true)
 *   behaviour when adding/removing invisible columns
 *   selection state
 * 
 * 
 * @author  Jeanette Winzenburg
 */
public class TableColumnModelTest extends TestCase {
    private static final Logger LOG = Logger
            .getLogger(TableColumnModelTest.class.getName());
    protected static final int COLUMN_COUNT = 3;
 
    /**
     * Issue #253-swingx: hiding/showing columns changes column sequence.
     * 
     * The test is modelled after the example code as 
     * http://forums.java.net/jive/thread.jspa?threadID=7344.
     *
     */
    public void testHideShowColumns() {
        DefaultTableColumnModelExt model = (DefaultTableColumnModelExt) createColumnModel(10);   
        int[] columnsToHide = new int[] { 4, 7, 6, 8, };
        for (int i = 0; i < columnsToHide.length; i++) {
            model.getColumnExt(String.valueOf(columnsToHide[i])).setVisible(false);
        }
        // sanity: actually hidden
        assertEquals(model.getColumnCount(true) - columnsToHide.length, model.getColumnCount());
        for (int i = 0; i < columnsToHide.length; i++) {
            model.getColumnExt(String.valueOf(columnsToHide[i])).setVisible(true);
        }
        // sanity: all visible again
        assertEquals(10, model.getColumnCount());
        for (int i = 0; i < model.getColumnCount(); i++) {
            // the original sequence
            assertEquals(i, model.getColumn(i).getModelIndex());
        }
    
    }
    
    /**
     * test sequence of visible columns after hide/move/show.
     * 
     * Expected behaviour should be like in Thunderbird.
     *
     */
    public void testMoveColumns() {
        DefaultTableColumnModelExt model = (DefaultTableColumnModelExt) createColumnModel(COLUMN_COUNT);
        TableColumnExt columnExt = model.getColumnExt(1);
        columnExt.setVisible(false);
        model.moveColumn(1, 0);
        columnExt.setVisible(true);
        assertEquals(columnExt.getModelIndex(), model.getColumnExt(2).getModelIndex());
    }
    /**
     * test the columnPropertyChangeEvent is fired as expected.
     *
     */
    public void testColumnPropertyChangeNotification() {
        DefaultTableColumnModelExt model = (DefaultTableColumnModelExt) createColumnModel(COLUMN_COUNT);
        ColumnModelReport report = new ColumnModelReport();
        model.addColumnModelListener(report);
        TableColumn column = model.getColumn(0);
        column.setHeaderValue("somevalue");
        assertEquals(1, report.getColumnPropertyEventCount());
        PropertyChangeEvent event = report.getLastColumnPropertyEvent();
        assertEquals(column, event.getSource());
        assertEquals("headerValue", event.getPropertyName());
        assertEquals("somevalue", event.getNewValue());
    }
    /**
     * added TableColumnModelExtListener: test for add/remove extended listeners.
     *
     */
    public void testAddExtListener() {
        DefaultTableColumnModelExt model = (DefaultTableColumnModelExt) createColumnModel(COLUMN_COUNT);
        ColumnModelReport extListener = new ColumnModelReport();
        model.addColumnModelListener(extListener);
        // JW: getListeners returns the count of exactly the given class?
//        assertEquals(1, model.getListeners(TableColumnModelExtListener.class).length);
//        assertEquals(2, model.getListeners(EventListener.class).length);
//        model.removeColumnModelListener(extListener);
//        assertEquals(0, model.getListeners(TableColumnModelExtListener.class).length);
//        assertEquals(0, model.getListeners(EventListener.class).length);
        assertEquals(1, model.getEventListenerList().getListenerCount(TableColumnModelExtListener.class));
        assertEquals(2, model.getEventListenerList().getListenerCount());
        model.removeColumnModelListener(extListener);
        assertEquals(0, model.getEventListenerList().getListenerCount(TableColumnModelExtListener.class));
        assertEquals(0, model.getEventListenerList().getListenerCount());
        
    }
    /**
     * Issue #??-swingx: incorrect isRemovedToInvisible after
     * removing an invisible column. 
     *
     */
    public void testRemoveInvisibleColumn() {
        DefaultTableColumnModelExt model = (DefaultTableColumnModelExt) createColumnModel(COLUMN_COUNT);
        TableColumnExt tableColumnExt = ((TableColumnExt) model.getColumn(0));
        tableColumnExt.setVisible(false);
        model.removeColumn(tableColumnExt);
        assertEquals("visible column count must be reduced", COLUMN_COUNT - 1, model.getColumns(false).size());
        assertEquals("all columns count must be unchanged", COLUMN_COUNT - 1, model.getColumns(true).size());
        assertFalse("removing invisible must update event cache", model.isRemovedToInvisibleEvent(0));
    }

    
    public void testGetColumns() {
        TableColumnModelExt model = createColumnModel(COLUMN_COUNT);
        ((TableColumnExt) model.getColumn(0)).setVisible(false);
        assertEquals("visible column count must be reduced", COLUMN_COUNT - 1, model.getColumns(false).size());
        assertEquals("all columns count must be unchanged", COLUMN_COUNT, model.getColumns(true).size());
    }
    /**
     * column count must be changed on changing 
     * column visibility.
     *
     */
    public void testColumnCountOnSetInvisible() {
        TableColumnModel model = createColumnModel(COLUMN_COUNT);
        int columnCount = model.getColumnCount();
        TableColumnExt column = (TableColumnExt) model.getColumn(columnCount - 1);
        assertTrue(column.isVisible());
        column.setVisible(false);
        assertEquals("columnCount must be decremented", columnCount - 1, model.getColumnCount());
    }
    
    /**
     * Issue #156: must update internal state after setting invisible.
     * Here: the cached totalWidth. Expect similar inconsistency
     * with selection.
     *
     */
    public void testTotalColumnWidth() {
        TableColumnModel model = createColumnModel(COLUMN_COUNT);
        int totalWidth = model.getTotalColumnWidth();
        TableColumnExt column = (TableColumnExt) model.getColumn(0);
        int columnWidth = column.getWidth();
        column.setVisible(false);
        assertEquals("new total width must be old minus invisible column width " + columnWidth,
                totalWidth - columnWidth, model.getTotalColumnWidth());
        
    }
    
    /** 
     * Issue #157: must fire columnRemoved after setting to invisible.
     *
     */
    public void testRemovedFired() {
        TableColumnModel model = createColumnModel(COLUMN_COUNT);
        ColumnModelReport l = new ColumnModelReport();
        model.addColumnModelListener(l);
        TableColumnExt column = (TableColumnExt) model.getColumn(0);
        column.setVisible(false);
        assertTrue("must have fired columnRemoved", l.hasRemovedEvent());
    }

    /** 
     * Issue #157: must fire columnAdded after setting to invisible.
     *
     */
    public void testAddedFired() {
        TableColumnModel model = createColumnModel(COLUMN_COUNT);
        ColumnModelReport l = new ColumnModelReport();
        TableColumnExt column = (TableColumnExt) model.getColumn(0);
        column.setVisible(false);
        model.addColumnModelListener(l);
        column.setVisible(true);
        assertTrue("must have fired columnRemoved", l.hasAddedEvent());
    }

    /**
     * columnAdded: event.getToIndex must be valid columnIndex.
     * 
     * 
     */
     public void testAddInvisibleColumn()  {
         TableColumnModel model = createColumnModel(COLUMN_COUNT);
         TableColumnModelListener l = new TableColumnModelListener() {

            public void columnAdded(TableColumnModelEvent e) {
                assertTrue("toIndex must be positive", e.getToIndex() >= 0);
                ((TableColumnModel) e.getSource()).getColumn(e.getToIndex());
            }

            public void columnRemoved(TableColumnModelEvent e) {
                // TODO Auto-generated method stub
                
            }

            public void columnMoved(TableColumnModelEvent e) {
                // TODO Auto-generated method stub
                
            }

            public void columnMarginChanged(ChangeEvent e) {
                // TODO Auto-generated method stub
                
            }

            public void columnSelectionChanged(ListSelectionEvent e) {
                // TODO Auto-generated method stub
                
            }
             
         };
         model.addColumnModelListener(l);
         // add invisible column
         TableColumnExt invisibleColumn = new TableColumnExt(0);
         invisibleColumn.setVisible(false);
         model.addColumn(invisibleColumn);
         // sanity check: add visible column
         model.addColumn(createTableColumnExt(0));
    }
    /**
     * columnAt must work on visible columns.
     *
     */
    public void testColumnAt() {
        TableColumnModel model = createColumnModel(COLUMN_COUNT);
        int totalWidth = model.getTotalColumnWidth();
        int lastColumn = model.getColumnIndexAtX(totalWidth - 10);
        assertEquals("lastColumn detected", model.getColumnCount() - 1, lastColumn);
        TableColumnExt column = (TableColumnExt) model.getColumn(lastColumn);
        column.setVisible(false);
        assertEquals("out of range", -1, model.getColumnIndexAtX(totalWidth - 10));
    }

//------------------  factory methods
    
    /**
     * creates and returns a TableColumnModelExt with the given number
     * of configured columns of type <code>TableColumnExt</code>.
     * 
     * @param columns the number of columns to create and add to the model
     * @return a <code>TableColumnModelExt</code> filled with columns.
     *    
     * @see createTableColumnExt
     */
    protected TableColumnModelExt createColumnModel(int columns) {
        TableColumnModelExt model = new DefaultTableColumnModelExt();
        for (int i = 0; i < columns; i++) {
            model.addColumn(createTableColumnExt(i));
      
        }
        return model;
    }

    /**
     * Creates and returns a TableColumnExt with simple standard configuration.
     * 
     * <pre><code>
     * column.getModelIndex() == modelIndex
     * column.getIdentifier() == String.valueOf(modelIndex);
     * </code></pre>
     *  
     * @param modelIndex the model column index to use for config
     * @return a <code>TableColumnExt</code> with standard configuration
     */
    protected TableColumnExt createTableColumnExt(int modelIndex) {
        TableColumnExt column = new TableColumnExt(modelIndex);
        column.setIdentifier(String.valueOf(modelIndex));
        return column;
    }
    

}
