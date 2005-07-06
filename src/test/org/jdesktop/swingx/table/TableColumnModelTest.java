/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.table;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumnModel;

import junit.framework.TestCase;

import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
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

    private static final int COLUMN_COUNT = 3;
 
    
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

//------------------ private factory methods
    
    private TableColumnModelExt createColumnModel(int columns) {
        TableColumnModelExt model = new DefaultTableColumnModelExt();
        for (int i = 0; i < columns; i++) {
            model.addColumn(createTableColumnExt(i));
      
        }
        return model;
    }

    private TableColumnExt createTableColumnExt(int i) {
        TableColumnExt column = new TableColumnExt(i);
        column.setIdentifier("" + i);
        return column;
    }
    

}
