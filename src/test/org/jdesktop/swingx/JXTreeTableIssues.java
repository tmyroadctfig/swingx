/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import org.jdesktop.swingx.util.ComponentTreeTableModel;
import org.jdesktop.swingx.util.TreeSelectionReport;


public class JXTreeTableIssues extends JXTreeTableUnitTest {

    /**
     * Issue #4-swingx: duplicate notification
     * 
     * Hmm... unexpected: the eventCount (2) is not effected by 
     * catching isAdjusting listSelectionEvents. 
     */
    public void testSelectionEvents() {
        JXTreeTable treeTable = new JXTreeTable(new ComponentTreeTableModel(new JXFrame()));
        treeTable.setRootVisible(true);
        // sanity: assert that we have at least two rows to change selection
        assertTrue(treeTable.getRowCount() > 1);
        treeTable.setRowSelectionInterval(0, 0);
        TreeSelectionReport report = new TreeSelectionReport();
        treeTable.getTreeSelectionModel().addTreeSelectionListener(report);
        treeTable.setRowSelectionInterval(1, 1);
        assertEquals(1, report.getEventCount());
    }

    
}
