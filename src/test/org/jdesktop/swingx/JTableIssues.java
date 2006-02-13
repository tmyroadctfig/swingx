/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import junit.framework.TestCase;

import org.jdesktop.swingx.util.ListSelectionReport;

/**
 * @author Jeanette Winzenburg
 */
public class JTableIssues extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JTableIssues.class
            .getName());
    
    public static void main(String args[]) {
//      setSystemLF(true);
      JTableIssues test = new JTableIssues();
      try {
        test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*ColumnControl.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }


    public void interactiveLeadAnchor() {
        final JTable table = new JTable(10, 3) {

            @Override
            public void tableChanged(TableModelEvent e) {
                super.tableChanged(e);
                if (isDataChanged(e) || isStructureChanged(e)) {
                    focusFirstCell();
                }
            }

            private void focusFirstCell() {
                if (getColumnCount() > 0) {
                    getColumnModel().getSelectionModel()
                            .removeSelectionInterval(0, 0);
                }
                if (getRowCount() > 0) {
                    getSelectionModel().removeSelectionInterval(0, 0);
                }

            }

            private boolean isDataChanged(TableModelEvent e) {
                return e.getType() == TableModelEvent.UPDATE
                        && e.getFirstRow() == 0
                        && e.getLastRow() == Integer.MAX_VALUE;
            }

            private boolean isStructureChanged(TableModelEvent e) {
                return e == null
                        || e.getFirstRow() == TableModelEvent.HEADER_ROW;
            }

        };
        JXFrame frame = wrapWithScrollingInFrame(table, "auto-lead");
        Action toggleAction = new AbstractAction("Toggle TableModel") {

            public void actionPerformed(ActionEvent e) {
                if (table.getRowCount() > 0) {
                    table.setModel(new DefaultTableModel());
                } else {
                    table.setModel(new DefaultTableModel(10, 3));
                }
            }

        };
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }

//---------------------- unit tests 
    
    /**
     * test contract: getColumn(int) throws ArrayIndexOutofBounds with 
     * invalid column index.
     * 
     * Subtle autoboxing issue: 
     * JTable has convenience method getColumn(Object) to access by 
     * identifier, but doesn't have delegate method to columnModel.getColumn(int)
     * Clients assuming the existence of a direct delegate no longer get a
     * compile-time error message in 1.5 due to autoboxing. 
     * Furthermore, the runtime exception is unexpected (IllegalArgument
     * instead of AIOOB).
     *
     */
    public void testTableColumnOffRange() {
        JTable table = new JTable(2, 1);
        try {
            table.getColumn(1);
            fail("accessing invalid column index must throw ArrayIndexOutofBoundExc");
        } catch (ArrayIndexOutOfBoundsException e) {
            // do nothing: contracted runtime exception
        } catch (Exception e) {
           fail("unexpected exception: " + e + "\n" +
              "accessing invalid column index must throw ArrayIndexOutofBoundExc");
        }
    }

    
    public void testTableRowAtNegativePoint() {
        JTable treeTable = new JTable(1, 4);
        int negativeYRowHeight = - treeTable.getRowHeight();
        int negativeYRowHeightPlusOne = negativeYRowHeight + 1;
        int negativeYMinimal = -1;
        // just outside of negative row before first row
        assertEquals("negative y location rowheight " + negativeYRowHeight + " must return row -1", 
                -1,  treeTable.rowAtPoint(new Point(-1, negativeYRowHeight)));
        // just inside of negative row before first row
        assertEquals("negative y location " + negativeYRowHeightPlusOne +" must return row -1", 
                -1,  treeTable.rowAtPoint(new Point(-1, negativeYRowHeightPlusOne)));
        // just outside of first row
        assertEquals("minimal negative y location must return row -1", 
                -1,  treeTable.rowAtPoint(new Point(-1, negativeYMinimal)));
        
    }

    public void testLeadSelectionAfterStructureChanged() {
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public void fireTableRowsDeleted(int firstRow, int lastRow) {
                fireTableStructureChanged();
            }
            
            
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i, i, 0);
        }
        JTable table = new JTable(model);
        int rowIndex = table.getRowCount() - 1;
        table.addRowSelectionInterval(rowIndex, rowIndex);
        model.removeRow(rowIndex);
        // JW: this was pre-1.5u5 (?), changed (1.5u6?) to return - 1
//        assertEquals("", rowIndex, table.getSelectionModel().getAnchorSelectionIndex());
        assertEquals("", -1, table.getSelectionModel().getAnchorSelectionIndex());
        ListSelectionReport report = new ListSelectionReport();
        table.getSelectionModel().addListSelectionListener(report);
    }

    /**
     * as of jdk1.5u6 the lead/anchor is no longer automatically set.
     * before (last code I saw is jdk1.5u4) - tableChanged would call
     * checkLeadAnchor after structureChanged. 
     * CheckLeadAnchor did set the lead/anchor
     * to the first row if count > 0.
     * 
     * Always: BasicTableUI repaints the lead-cell in focusGained.
     * 
     * Now: need to explicitly set _both_ anchor and lead to >= 0
     * need to set anchor first. Need to do so for both row/column selection model.
     * 
     */
    public void testInitialLeadAnchor() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run testLeadAnchorOnFocusGained - headless environment");
            return;
        }
        DefaultTableModel model = new DefaultTableModel(10, 2) {

            @Override
            public void fireTableRowsDeleted(int firstRow, int lastRow) {
                fireTableStructureChanged();
            }
            
            
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i, i, 0);
        }
        final JTable table = new JTable(model);
        JFrame frame = new JFrame("anchor on focus");
        frame.add(new JScrollPane(table));
        frame.setVisible(true);
        // JW: need to explicitly set _both_ anchor and lead to >= 0
        // need to set anchor first
        table.getSelectionModel().setAnchorSelectionIndex(0);
        table.getSelectionModel().setLeadSelectionIndex(0);
        table.getColumnModel().getSelectionModel().setAnchorSelectionIndex(0);
        table.getColumnModel().getSelectionModel().setLeadSelectionIndex(0);

        table.requestFocus();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                assertTrue("table is focused ", table.hasFocus());
                assertEquals("anchor must be 0", 0, table.getSelectionModel().getAnchorSelectionIndex());
                assertEquals("lead must be 0", 0, table.getSelectionModel().getLeadSelectionIndex());

            }
        });
    }

    /**
     * as of jdk1.5u6 the lead/anchor is no longer automatically set.
     * before (last code I saw is jdk1.5u4) - tableChanged would call
     * checkLeadAnchor after structureChanged. CheckLeadAnchor did set the lead/anchor
     * to the first row if count > 0.
     * 
     * Always: BasicTableUI repaints the lead-cell in focusGained.
     * 
     * Now: need to explicitly set _both_ anchor and lead to >= 0
     * need to set anchor first. Need to do so for both row/column selection model.
     * 
     */
    public void testLeadAnchorAfterStructureChanged() {
        final JTable table = new JTable(10, 2);
        // JW: need to explicitly set _both_ anchor and lead to >= 0
        // need to set anchor first
        table.getSelectionModel().setAnchorSelectionIndex(0);
        table.getSelectionModel().setLeadSelectionIndex(0);
        table.getColumnModel().getSelectionModel().setAnchorSelectionIndex(0);
        table.getColumnModel().getSelectionModel().setLeadSelectionIndex(0);
        // sanity...
        assertEquals("anchor must be 0", 0, table.getSelectionModel().getAnchorSelectionIndex());
        assertEquals("lead must be 0", 0, table.getSelectionModel().getLeadSelectionIndex());
        table.setModel(new DefaultTableModel(20, 3));
        // regression: lead/anchor unconditionally reset to -1 
        assertEquals("anchor must be 0", 0, table.getSelectionModel().getAnchorSelectionIndex());
        assertEquals("lead must be 0", 0, table.getSelectionModel().getLeadSelectionIndex());
        
    }

}
