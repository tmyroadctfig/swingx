/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import junit.framework.TestCase;

import org.jdesktop.swingx.util.ComponentTreeTableModel;
import org.jdesktop.swingx.util.TreeSelectionReport;


public class JXTreeTableIssues extends TestCase {

    /**
     * Issue #4-swingx: duplicate notification
     * 
     * starting from unselected, the event count is 1 as expected 
     */
    public void testSelectionEvents() {
        JXTreeTable treeTable = new JXTreeTable(new ComponentTreeTableModel(new JXFrame()));
        treeTable.setRootVisible(true);
        // sanity: assert that we have at least two rows to change selection
        assertTrue(treeTable.getRowCount() > 1);
        TreeSelectionReport report = new TreeSelectionReport();
        treeTable.getTreeSelectionModel().addTreeSelectionListener(report);
        treeTable.setRowSelectionInterval(1, 1);
        assertEquals(1, report.getEventCount());
    }


    /**
     * Issue #4-swingx: duplicate notification
     * 
     * Hmm... unexpected: the eventCount (2) is not effected by 
     * catching isAdjusting listSelectionEvents. The reason is 
     * an intermediate clearSelection which fires the additional.
     */
    public void testSelectionChangedEvents() {
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

    /**
     * Issue #4-swingx: duplicate notification
     * 
     * Hmm... unexpected: the eventCount (2) is not effected by 
     * catching isAdjusting listSelectionEvents. The reason is 
     * an intermediate clearSelection which fires the additional.
     */
    public void testSelectionChangedHasFirstOldPath() {
        JXTreeTable treeTable = new JXTreeTable(new ComponentTreeTableModel(new JXFrame()));
        treeTable.setRootVisible(true);
        // sanity: assert that we have at least two rows to change selection
        assertTrue(treeTable.getRowCount() > 1);
        treeTable.setRowSelectionInterval(0, 0);
        TreeSelectionReport report = new TreeSelectionReport();
        treeTable.getTreeSelectionModel().addTreeSelectionListener(report);
        treeTable.setRowSelectionInterval(1, 1);
        TreeSelectionEvent event = report.getLastEvent();
        assertEquals(treeTable.getPathForRow(1), event.getNewLeadSelectionPath());
        assertEquals(treeTable.getPathForRow(0), event.getOldLeadSelectionPath());
    }

    /**
     * Issue #4-swingx: duplicate notification
     * 
     * sanity: check if there's only one event fired if selection is 
     * set directly via the treeSelectionModel.
     */
    public void testSelectionChangedOnTreeSelection() {
        JXTreeTable treeTable = new JXTreeTable(new ComponentTreeTableModel(new JXFrame()));
        treeTable.setRootVisible(true);
        // sanity: assert that we have at least two rows to change selection
        assertTrue(treeTable.getRowCount() > 1);
        TreePath oldSelected = treeTable.getPathForRow(0);
        treeTable.getTreeSelectionModel().setSelectionPath(oldSelected);
        TreeSelectionReport report = new TreeSelectionReport();
        treeTable.getTreeSelectionModel().addTreeSelectionListener(report);
        TreePath newSelected = treeTable.getPathForRow(1);
        treeTable.getTreeSelectionModel().setSelectionPath(newSelected);
        assertEquals(1, report.getEventCount());
        // check the paths
        TreeSelectionEvent event = report.getLastEvent();
        assertEquals(oldSelected, event.getOldLeadSelectionPath());
        assertEquals(newSelected, event.getNewLeadSelectionPath());
    }
    

}
