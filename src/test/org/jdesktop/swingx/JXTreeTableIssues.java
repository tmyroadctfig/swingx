/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
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
import org.jdesktop.swingx.renderer.LabelProvider;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.WrappingIconPanel;
import org.jdesktop.swingx.renderer.WrappingProvider;
import org.jdesktop.swingx.test.ActionMapTreeTableModel;
import org.jdesktop.swingx.test.ComponentTreeTableModel;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.test.TableModelReport;

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
//            test.runInteractiveTests();
            test.runInteractiveTests(".*Adapter.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
    
    /**
     * Issue #493-swingx: JXTreeTable.TreeTableModelAdapter: Inconsistency
     * firing update.
     * 
     * Test update events after updating treeTableModel.
     * 
     * from tiberiu@dev.java.net
     */
    public void testTableEventUpdateOnTreeTableModelSetValue() {
        TreeTableModel model = createCustomTreeTableModelFromDefault();
        final JXTreeTable table = new JXTreeTable(model);
        table.setRootVisible(true);
        table.expandAll();
        final int row = 6;
        // sanity
        assertEquals("sports", table.getValueAt(row, 0).toString());
        final TableModelReport report = new TableModelReport();
        table.getModel().addTableModelListener(report);
        model.setValueAt("games",
                table.getPathForRow(6).getLastPathComponent(), 0);   
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                assertEquals("tableModel must have fired", 1, report.getEventCount());
                assertEquals("the event type must be update", 1, report.getUpdateEventCount());
                TableModelEvent event = report.getLastUpdateEvent();
                assertEquals("the updated row ", row, event.getFirstRow());
            }
        });        
    }

    /**
     * Issue #493-swingx: JXTreeTable.TreeTableModelAdapter: Inconsistency
     * firing update.
     * 
     * Test delete events after tree table model.
     * 
     * from tiberiu@dev.java.net
     */
    public void testTableEventDeleteOnTreeTableModel() {
        TreeTableModel model = createCustomTreeTableModelFromDefault();
        MutableTreeNode root = (MutableTreeNode) model.getRoot();
        MutableTreeNode sportsNode = (MutableTreeNode) root.getChildAt(1);
        int childrenToDelete = sportsNode.getChildCount() - 1;
        
        for (int i = 0; i < childrenToDelete; i++) {
            MutableTreeNode firstChild = (MutableTreeNode) sportsNode.getChildAt(0);
            ((DefaultTreeModel) model).removeNodeFromParent(firstChild);
        }
        // sanity
        assertEquals(1, sportsNode.getChildCount());
        final JXTreeTable table = new JXTreeTable(model);
        table.setRootVisible(true);
        table.expandAll();
        final int row = 6;
        // sanity
        assertEquals("sports", table.getValueAt(row, 0).toString());
        final TableModelReport report = new TableModelReport();
        table.getModel().addTableModelListener(report);
        // remove the last child from sports node
        MutableTreeNode firstChild = (MutableTreeNode) sportsNode.getChildAt(0);
        ((DefaultTreeModel) model).removeNodeFromParent(firstChild);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                assertEquals("tableModel must have fired exactly one event", 1, report.getEventCount());
                TableModelEvent event = report.getLastEvent();
                assertEquals("event type must be delete", TableModelEvent.DELETE, event.getType());
                assertEquals("the deleted row ", row + 1, event.getFirstRow());
            }
        });        
    }
    /**
     * Issue #493-swingx: JXTreeTable.TreeTableModelAdapter: Inconsistency
     * firing update.
     * 
     * Test update events after updating table.
     * 
     * from tiberiu@dev.java.net
     */
    public void testTableEventUpdateOnTreeTableSetValue() {
        TreeTableModel model = createCustomTreeTableModelFromDefault();
        final JXTreeTable table = new JXTreeTable(model);
        table.setRootVisible(true);
        table.expandAll();
        final int row = 6;
        // sanity
        assertEquals("sports", table.getValueAt(row, 0).toString());
        final TableModelReport report = new TableModelReport();
        table.getModel().addTableModelListener(report);
        // doesn't fire or isn't detectable? 
        // Problem was: model was not-editable.
        table.setValueAt("games", row, 0);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                assertEquals("tableModel must have fired", 1, report.getEventCount());
                assertEquals("the event type must be update", 1, report.getUpdateEventCount());
                TableModelEvent event = report.getLastUpdateEvent();
                assertEquals("the updated row ", row, event.getFirstRow());
            }
        });        
    }

    // -------------- interactive tests

    /**
     * Issue #493-swingx: JXTreeTable.TreeTableModelAdapter: Inconsistency
     * firing update.
     * Use the second child of root - first is accidentally okay.
     * 
     * from tiberiu@dev.java.net
     */
    public void interactiveTreeTableModelAdapterUpdate() {
        TreeTableModel customTreeTableModel = createCustomTreeTableModelFromDefault();

        final JXTreeTable table = new JXTreeTable(customTreeTableModel);
        table.setRootVisible(true);
        table.expandAll();
        table.setLargeModel(true);
        JXTree xtree = new JXTree(customTreeTableModel);
        xtree.setRootVisible(true);
        xtree.expandAll();
        final JXFrame frame = wrapWithScrollingInFrame(table, xtree,
                "JXTreeTable.TreeTableModelAdapter: Inconsistency firing update");
        Action changeValue = new AbstractAction("change sports to games") {
            public void actionPerformed(ActionEvent e) {
                String newValue = "games";
                table.getTreeTableModel().setValueAt(newValue,
                        table.getPathForRow(6).getLastPathComponent(), 0);
            }
        };
        addAction(frame, changeValue);
        frame.setVisible(true);
    }

    /**
     * Issue #493-swingx: JXTreeTable.TreeTableModelAdapter: Inconsistency
     * firing delete.
     * 
     * from tiberiu@dev.java.net
     */
    public void interactiveTreeTableModelAdapterDelete() {
        final TreeTableModel customTreeTableModel = createCustomTreeTableModelFromDefault();
        final JXTreeTable table = new JXTreeTable(customTreeTableModel);
        table.setRootVisible(true);
        table.expandAll();
        JXTree xtree = new JXTree(customTreeTableModel);
        xtree.setRootVisible(true);
        xtree.expandAll();
        final JXFrame frame = wrapWithScrollingInFrame(table, xtree,
                "JXTreeTable.TreeTableModelAdapter: Inconsistency firing update");
        Action changeValue = new AbstractAction("delete first child of sports") {
            public void actionPerformed(ActionEvent e) {
                MutableTreeNode firstChild = (MutableTreeNode) table.getPathForRow(6 +1).getLastPathComponent();
                ((DefaultTreeModel) customTreeTableModel).removeNodeFromParent(firstChild);
            }
        };
        addAction(frame, changeValue);
        frame.setVisible(true);
    }

    /**
     * Creates and returns a custom model from JXTree default model. The model
     * is of type DefaultTreeModel, allowing for easy insert/remove.
     * 
     * @return
     */
    private TreeTableModel createCustomTreeTableModelFromDefault() {
        JXTree tree = new JXTree();
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
         TreeTableModel customTreeTableModel = new
         CustomTreeTableModel((TreeNode) treeModel.getRoot());
        return customTreeTableModel;
    }

    /**
     * A TreeTableModel inheriting from DefaultTreeModel (to ease
     * insert/delete).
     */
    public static class CustomTreeTableModel extends DefaultTreeModel implements
            TreeTableModel {

        /**
         * @param root
         */
        public CustomTreeTableModel(TreeNode root) {
            super(root);
        }

        public Class getColumnClass(int column) {
            return TreeTableModel.class;
        }

        public int getColumnCount() {
            return 1;
        }

        public String getColumnName(int column) {
            return "User Object";
        }

        public Object getValueAt(Object node, int column) {
            return ((DefaultMutableTreeNode) node).getUserObject();
        }

        public boolean isCellEditable(Object node, int column) {
            return true;
        }

        public void setValueAt(Object value, Object node, int column) {
            ((MutableTreeNode) node).setUserObject(value);
            nodeChanged((TreeNode) node);
        }

    }

    /**
     * Issue #??-swingx: hyperlink in JXTreeTable hierarchical column not
     * active.
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
        tree.setHighlighters(new UIAlternateRowHighlighter());
        JFrame frame = wrapWithScrollingInFrame(tree, "treetable and custom renderer");
        frame.setVisible(true);
    }

    /**
     * example how to use a custom component as
     * renderer in tree column of TreeTable.
     *
     */
    public void interactiveTreeTableWrappingProvider() {
        final JXTreeTable treeTable = new JXTreeTable(createActionTreeModel());
        treeTable.setHorizontalScrollEnabled(true);
        treeTable.packColumn(0, -1);
        
        StringValue format = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Action) {
                    return ((Action) value).getValue(Action.NAME) + "xx";
                }
                return StringValue.TO_STRING.getString(value);
            }
            
        };
        ComponentProvider tableProvider = new LabelProvider(format);
        TableCellRenderer tableRenderer = new DefaultTableRenderer(tableProvider);
        WrappingProvider wrappingProvider = new WrappingProvider(tableProvider) {
            Border redBorder = BorderFactory.createLineBorder(Color.RED);
            @Override
            public WrappingIconPanel getRendererComponent(CellContext context) {
                Dimension old = rendererComponent.getPreferredSize();
                rendererComponent.setPreferredSize(null);
                super.getRendererComponent(context);
                Dimension dim = rendererComponent.getPreferredSize();
                dim.width = Math.max(dim.width, treeTable.getColumn(0).getWidth());
                rendererComponent.setPreferredSize(dim);
                rendererComponent.setBorder(redBorder);
                return rendererComponent;
            }
            
        };
        DefaultTreeRenderer treeCellRenderer = new DefaultTreeRenderer(wrappingProvider);
        treeTable.setTreeCellRenderer(treeCellRenderer);
        treeTable.setHighlighters(new UIAlternateRowHighlighter());
        JXTree tree = new JXTree(treeTable.getTreeTableModel());
        tree.setCellRenderer(treeCellRenderer);
        tree.setLargeModel(true);
        tree.setScrollsOnExpand(false);
        JFrame frame = wrapWithScrollingInFrame(treeTable, tree, "treetable and default wrapping provider");
        frame.setVisible(true);
    }

    /**
     * Dirty example how to configure a custom renderer
     * to use treeTableModel.getValueAt(...) for showing.
     *
     */
    public void interactiveTreeTableGetValueRenderer() {
        JXTreeTable tree = new JXTreeTable(new ComponentTreeTableModel(new JXFrame()));
        ComponentProvider provider = new ButtonProvider() {
            /**
             * show a unselected checkbox and text.
             */
            @Override
            protected void format(CellContext context) {
                // this is dirty because the design idea was to keep the renderer 
                // unaware of the context type
                TreeTableModel model = (TreeTableModel) ((JXTree) context.getComponent()).getModel();
                // beware: currently works only if the node is not a DefaultMutableTreeNode
                // otherwise the WrappingProvider tries to be smart and replaces the node
                // by the userObject before passing on to the wrappee! 
                Object nodeValue = model.getValueAt(context.getValue(), 0);
                rendererComponent.setText(" ... " + formatter.getString(nodeValue));
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
        tree.expandAll();
        tree.setHighlighters(new HighlighterPipeline(new Highlighter[] { 
                new UIAlternateRowHighlighter()}));
        JFrame frame = wrapWithScrollingInFrame(tree, "treeTable and getValueAt renderer");
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

    /**
     * @return
     */
    private TreeTableModel createActionTreeModel() {
        JXTable table = new JXTable(10, 10);
        table.setHorizontalScrollEnabled(true);
        return new ActionMapTreeTableModel(table);
    }

}
