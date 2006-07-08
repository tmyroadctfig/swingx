/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import javax.swing.tree.TreePath;

import junit.framework.TestCase;

import org.jdesktop.swingx.util.ComponentTreeTableModel;



public class JXTreeTableIssues extends TestCase {

    /**
     * sanity: toggling select/unselect via mouse the lead is
     * always painted, doing unselect via model (clear/remove path) 
     * seems to clear the lead?
     *
     */
    public void testBasicTreeLeadSelection() {
        JXTree tree = new JXTree();
        TreePath path = tree.getPathForRow(0);
        tree.setSelectionPath(path);
        assertEquals(0, tree.getSelectionModel().getLeadSelectionRow());
        assertEquals(path, tree.getLeadSelectionPath());
        tree.removeSelectionPath(path);
        assertNotNull(tree.getLeadSelectionPath());
        assertEquals(0, tree.getSelectionModel().getLeadSelectionRow());
    }
    /**
     * Issue #341-swingx: missing synch of lead.  
     * test lead after remove selection via tree.
     *
     */
    public void testLeadAfterRemoveSelectionFromTree() {
        JXTreeTable treeTable = prepareTreeTable(true);
        treeTable.getTreeSelectionModel().removeSelectionPath(
                treeTable.getTreeSelectionModel().getLeadSelectionPath());
        assertEquals(treeTable.getSelectionModel().getLeadSelectionIndex(), 
                treeTable.getTreeSelectionModel().getLeadSelectionRow());
        
    }
    
    /**
     * Issue #341-swingx: missing synch of lead.  
     * test lead after clear selection via table.
     *
     */
    public void testLeadAfterClearSelectionFromTable() {
        JXTreeTable treeTable = prepareTreeTable(true);
        treeTable.clearSelection();
        assertEquals(treeTable.getSelectionModel().getLeadSelectionIndex(), 
                treeTable.getTreeSelectionModel().getLeadSelectionRow());
        
    }

    /**
     * Issue #341-swingx: missing synch of lead.  
     * test lead after clear selection via table.
     *
     */
    public void testLeadAfterClearSelectionFromTree() {
        JXTreeTable treeTable = prepareTreeTable(true);
        treeTable.getTreeSelectionModel().clearSelection();
        assertEquals(treeTable.getSelectionModel().getLeadSelectionIndex(), 
                treeTable.getTreeSelectionModel().getLeadSelectionRow());
        
    }
    /**
     * Issue #341-swingx: missing synch of lead.  
     * test lead after setting selection via table.
     *
     */
    public void testLeadSelectionFromTable() {
        JXTreeTable treeTable = prepareTreeTable(false);
        assertEquals(-1, treeTable.getSelectionModel().getLeadSelectionIndex());
        assertEquals(-1, treeTable.getTreeSelectionModel().getLeadSelectionRow());
        treeTable.setRowSelectionInterval(0, 0);
        assertEquals(treeTable.getSelectionModel().getLeadSelectionIndex(), 
                treeTable.getTreeSelectionModel().getLeadSelectionRow());
    }
    
    /**
     * Issue #341-swingx: missing synch of lead.  
     * test lead after setting selection via treeSelection.
     *
     */
    public void testLeadSelectionFromTree() {
        JXTreeTable treeTable = prepareTreeTable(false);
        assertEquals(-1, treeTable.getSelectionModel().getLeadSelectionIndex());
        assertEquals(-1, treeTable.getTreeSelectionModel().getLeadSelectionRow());
        treeTable.getTreeSelectionModel().setSelectionPath(treeTable.getPathForRow(0));
        assertEquals(treeTable.getSelectionModel().getLeadSelectionIndex(), 
                treeTable.getTreeSelectionModel().getLeadSelectionRow());
        assertEquals(0, treeTable.getTreeSelectionModel().getLeadSelectionRow());

    }

    /**
     * creates and configures a treetable for usage in selection tests.
     * 
     * @param selectFirstRow boolean to indicate if the first row should
     *   be selected.
     * @return
     */
    protected JXTreeTable prepareTreeTable(boolean selectFirstRow) {
        JXTreeTable treeTable = new JXTreeTable(new ComponentTreeTableModel(new JXFrame()));
        treeTable.setRootVisible(true);
        // sanity: assert that we have at least two rows to change selection
        assertTrue(treeTable.getRowCount() > 1);
        if (selectFirstRow) {
            treeTable.setRowSelectionInterval(0, 0);
        }
        return treeTable;
    }


    public void testDummy() {
        
    }
}
