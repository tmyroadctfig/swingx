/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.TreePath;

import junit.framework.TestCase;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.util.ComponentTreeTableModel;

/**
 * Test to exposed known issues of <code>JXTreeTable</code>.
 * 
 * Ideally, there would be at least one failing test method per open
 * issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 * 
 * @author Jeanette Winzenburg
 */
public class JXTreeTableIssues extends TestCase {

    /**
     * Issue #??-swingx: ComponentAdapter not fully implemented - leaf always true.
     *
     */
    public void testComponentAdapterIsLeaf() {
        // build the test treeTableModel
        JPanel root = new JPanel();
        JLabel label = new JLabel();
        JPanel inner = new JPanel();
        // last row is leaf
        inner.add(label);
        // first row is folder
        root.add(inner);
        TreeTableModel model = new ComponentTreeTableModel(root);
        // sanity 
        assertTrue("label is leaf", model.isLeaf(label));
        JXTreeTable table = new JXTreeTable(model);
        table.expandAll();
        // sanity
        assertEquals("number of expanded rows", 2, table.getRowCount());
        
        // test leafness of last
        int lastRow = table.getRowCount() - 1;
        TreePath leafPath = table.getPathForRow(lastRow);
        assertEquals(label, leafPath.getLastPathComponent());
        assertEquals("adapter must report same leafness as model", 
                model.isLeaf(label), table.getComponentAdapter(lastRow, 0).isLeaf());
        // test folderness of first
        int firstRow = 0;
        TreePath folderPath = table.getPathForRow(firstRow);
        assertEquals(inner, folderPath.getLastPathComponent());
        assertEquals("adapter must report same leafness as model", 
                model.isLeaf(inner), table.getComponentAdapter(firstRow, 0).isLeaf());
    }
    
    /**
     * Issue #??-swingx: ComponentAdapter not fully implemented - expanded always true.
     *
     */
    public void testComponentAdapterIsExpanded() {
        // build the test treeTableModel
        JPanel root = new JPanel();
        JLabel label = new JLabel();
        JPanel inner = new JPanel();
        // last row is leaf
        inner.add(label);
        // first row is folder
        root.add(inner);
        TreeTableModel model = new ComponentTreeTableModel(root);
        // sanity 
        assertTrue("label is leaf", model.isLeaf(label));
        JXTreeTable table = new JXTreeTable(model);
        // sanity
        assertEquals("number of expanded rows", 1, table.getRowCount());
        
        // test folderness of first
        int firstRow = 0;
        TreePath folderPath = table.getPathForRow(firstRow);
        assertEquals(inner, folderPath.getLastPathComponent());
        assertEquals("adapter must report same expansion state as tree", table.isExpanded(firstRow), 
                table.getComponentAdapter(firstRow, 0).isExpanded());
    }
    
    /**
     * Issue #399-swingx: editing terminated by selecting editing row.
     *
     */
    public void testSelectionKeepsEditingWithExpandsTrue() {
        JXTreeTable treeTable = new JXTreeTable(new FileSystemModel()) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
            
        };
        // sanity: default value of expandsSelectedPath
        assertTrue(treeTable.getExpandsSelectedPaths());
        boolean canEdit = treeTable.editCellAt(1, 2);
        // sanity: editing started
        assertTrue(canEdit);
        // sanity: nothing selected
        assertTrue(treeTable.getSelectionModel().isSelectionEmpty());
        int editingRow = treeTable.getEditingRow();
        treeTable.setRowSelectionInterval(editingRow, editingRow);
        assertEquals("after selection treeTable editing state must be unchanged", canEdit, treeTable.isEditing());
    }
    
    /**
     * Issue #399-swingx: editing terminated by selecting editing row.<p>
     * Assert workaround: setExpandsSelectedPaths(false)
     */
    public void testSelectionKeepsEditingWithExpandsFalse() {
        JXTreeTable treeTable = new JXTreeTable(new FileSystemModel()) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
            
        };
        boolean canEdit = treeTable.editCellAt(1, 2);
        // sanity: editing started
        assertTrue(canEdit);
        // sanity: nothing selected
        assertTrue(treeTable.getSelectionModel().isSelectionEmpty());
        int editingRow = treeTable.getEditingRow();
        treeTable.setExpandsSelectedPaths(false);
        treeTable.setRowSelectionInterval(editingRow, editingRow);
        assertEquals("after selection treeTable editing state must be unchanged", canEdit, treeTable.isEditing());
    }
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
