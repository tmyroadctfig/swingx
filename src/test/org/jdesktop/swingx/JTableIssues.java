/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Point;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import junit.framework.TestCase;

import org.jdesktop.swingx.util.ListSelectionReport;

/**
 * @author Jeanette Winzenburg, Berlin
 */
public class JTableIssues extends TestCase {

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
        // JW: this was pre-1.5u5 (?), changed (1.5u6?) to return - 1
//        assertEquals("", rowIndex, table.getSelectionModel().getAnchorSelectionIndex());
        assertEquals("", -1, table.getSelectionModel().getAnchorSelectionIndex());
        ListSelectionReport report = new ListSelectionReport();
        table.getSelectionModel().addListSelectionListener(report);
        
    }
}
