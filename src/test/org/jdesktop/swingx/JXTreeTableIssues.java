/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.action.LinkAction;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter.UIAlternateRowHighlighter;
import org.jdesktop.swingx.renderer.ButtonProvider;
import org.jdesktop.swingx.renderer.CellContext;
import org.jdesktop.swingx.renderer.ComponentProvider;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.test.ComponentTreeTableModel;
import org.jdesktop.swingx.treetable.FileSystemModel;

/**
 * Test to exposed known issues of <code>JXTreeTable</code>. <p>
 * 
 * Ideally, there would be at least one failing test method per open
 * issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.<p>
 * 
 * Once the issues are fixed and the corresponding methods are passing, they
 * should be moved over to the XXTest. 
 * 
 * @author Jeanette Winzenburg
 */
public class JXTreeTableIssues extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXTreeTableIssues.class
            .getName());
    public static void main(String[] args) {
        setSystemLF(true);
        JXTreeTableIssues test = new JXTreeTableIssues();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
    
//-------------- interactive tests
    /**
     * Issue #??-swingx: hyperlink in JXTreeTable hierarchical 
     * column not active.
     *
     */
    public void interactiveTreeTableLinkRendererSimpleText() {
        LinkAction simpleAction = new LinkAction<Object>(null) {

            public void actionPerformed(ActionEvent e) {
                LOG.info("hit: " + getTarget());
                
            }
            
        };
        JXTreeTable tree = new JXTreeTable(new FileSystemModel());
        HyperlinkProvider provider =  new HyperlinkProvider(simpleAction);
        tree.getColumn(2).setCellRenderer(new DefaultTableRenderer(provider));
        tree.setTreeCellRenderer(new DefaultTreeRenderer(provider));
//        tree.setCellRenderer(new LinkRenderer(simpleAction));
        tree.setHighlighters(new HighlighterPipeline(new Highlighter[] { 
                new UIAlternateRowHighlighter()}));
        JFrame frame = wrapWithScrollingInFrame(tree, "table and simple links");
        frame.setVisible(true);
    }

    /**
     * example how to use a custom component as
     * renderer in tree column of TreeTable.
     *
     */
    public void interactiveTreeTableCustomRenderer() {
        JXTreeTable tree = new JXTreeTable(new FileSystemModel());
        ComponentProvider provider = new ButtonProvider() {
            /**
             * show a unselected checkbox and text.
             */
            @Override
            protected void format(CellContext context) {
                super.format(context);
                rendererComponent.setText(" ... " + getStringValue(context));
            }

            /**
             * custom tooltip: show row. Note: the context is that 
             * of the rendering tree. No way to get at table state?
             */
            @Override
            protected void configureState(CellContext context) {
                super.configureState(context);
                rendererComponent.setToolTipText("Row: " + context.getRow());
            }
            
        };
        provider.setHorizontalAlignment(JLabel.LEADING);
        tree.setTreeCellRenderer(new DefaultTreeRenderer(provider));
//        tree.setCellRenderer(new LinkRenderer(simpleAction));
        tree.setHighlighters(new HighlighterPipeline(new Highlighter[] { 
                new UIAlternateRowHighlighter()}));
        JFrame frame = wrapWithScrollingInFrame(tree, "table and simple links");
        frame.setVisible(true);
    }


//------------- unit tests    
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
