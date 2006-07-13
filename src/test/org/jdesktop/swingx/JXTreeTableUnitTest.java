/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.CellEditor;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.swingx.treetable.TreeTableCellEditor;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.util.PropertyChangeReport;
import org.jdesktop.swingx.util.TreeSelectionReport;


public class JXTreeTableUnitTest extends InteractiveTestCase {

    protected TreeTableModel treeTableModel;
    protected TreeTableModel simpleTreeTableModel;
    
    public JXTreeTableUnitTest() {
        super("JXTreeTable Unit Test");
    }

    /**
     * Issue #120-jdnc: data corruption if collapsed while editing.
     * Note: this tests programatic collapse while editing! 
     * Don't know how to test mouse-triggered collapse/expand, "looked"
     * at it in the visualCheck. 
     */
    public void testEditOnCollapse() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
        DefaultMutableTreeNode a = new DefaultMutableTreeNode("A");
        DefaultMutableTreeNode a1 = new DefaultMutableTreeNode("A1");
        DefaultMutableTreeNode b = new DefaultMutableTreeNode("B");
        a.add(a1);
        root.add(a);
        root.add(b);
        TreeTableModel model = new DefaultTreeTableModel(root) {
            public boolean isCellEditable(Object obj,int col) {
                return true;
              }
                                                                                      
              public void setValueAt(Object value,Object node,int col) {
                  MutableTreeNode treeNode = (MutableTreeNode) node;
                 treeNode.setUserObject(value);
                 MutableTreeNode parent = (MutableTreeNode) treeNode.getParent();
                 nodesChanged(parent, new int[] { parent.getIndex(treeNode) } );
              }
                                                                                      
              public Object getValueAt(Object node,int col) {
                  return ((DefaultMutableTreeNode) node).getUserObject();
              }
            };
            
        JXTreeTable treeTable = new JXTreeTable(model);
        treeTable.setEditable(true);
        treeTable.expandAll();
        assertEquals(3, treeTable.getRowCount());
        Object valueBelow = treeTable.getValueAt(2, 0);
        treeTable.editCellAt(1, 0);
        ((JTextField) treeTable.getEditorComponent()).setText("other");
        treeTable.collapseRow(0);
        assertEquals(2, treeTable.getRowCount());
        if (treeTable.isEditing()) {
            treeTable.getCellEditor().stopCellEditing();
        }
        assertEquals(valueBelow, treeTable.getValueAt(1, 0));
    }


    /**
     * Issue #212-jdnc: reuse editor, install only once.
     * 
     */
    public void testReuseEditor() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        CellEditor editor = treeTable.getDefaultEditor(TreeTableModel.class);
        assertTrue(editor instanceof TreeTableCellEditor);
        treeTable.setTreeTableModel(simpleTreeTableModel);
        assertSame("hierarchical editor must be unchanged", editor, 
                treeTable.getDefaultEditor(TreeTableModel.class));
    }
    /**
     * Issue #4-, #340-swingx: duplicate notification
     * 
     * starting from unselected, the event count is 1 as expected 
     */
    public void testSelectionEvents() {
        JXTreeTable treeTable = prepareTreeTable(false);
        TreeSelectionReport report = new TreeSelectionReport();
        treeTable.getTreeSelectionModel().addTreeSelectionListener(report);
        treeTable.setRowSelectionInterval(1, 1);
        assertEquals(1, report.getEventCount());
    }


    /**
     * Issue #4-, #340-swingx: duplicate notification
     * 
     * Hmm... unexpected: the eventCount (2) is not effected by 
     * catching isAdjusting listSelectionEvents. The reason is 
     * an intermediate clearSelection which fires the additional.
     */
    public void testSelectionChangedEvents() {
        JXTreeTable treeTable = prepareTreeTable(true);
        TreeSelectionReport report = new TreeSelectionReport();
        treeTable.getTreeSelectionModel().addTreeSelectionListener(report);
        treeTable.setRowSelectionInterval(1, 1);
        assertEquals(1, report.getEventCount());
    }

    /**
     * Issue #4-, #340-swingx: duplicate notification
     * 
     * The old in the event must be the last selected. 
     */
    public void testSelectionChangedHasFirstOldPath() {
        JXTreeTable treeTable = prepareTreeTable(true);
        TreeSelectionReport report = new TreeSelectionReport();
        treeTable.getTreeSelectionModel().addTreeSelectionListener(report);
        treeTable.setRowSelectionInterval(1, 1);
        TreeSelectionEvent event = report.getLastEvent();
        assertEquals(treeTable.getPathForRow(1), event.getNewLeadSelectionPath());
        assertEquals(treeTable.getPathForRow(0), event.getOldLeadSelectionPath());
    }


    /**
     * creates and configures a treetable for usage in selection tests.
     * 
     * @param selectFirstRow boolean to indicate if the first row should
     *   be selected.
     * @return
     */
    protected JXTreeTable prepareTreeTable(boolean selectFirstRow) {
        JXTreeTable treeTable = new JXTreeTable(simpleTreeTableModel);
        treeTable.setRootVisible(true);
        // sanity: assert that we have at least two rows to change selection
        assertTrue(treeTable.getRowCount() > 1);
        if (selectFirstRow) {
            treeTable.setRowSelectionInterval(0, 0);
        }
        return treeTable;
    }

    /**
     * Issue #4-, #340-swingx: duplicate notification
     * 
     * sanity: check if there's only one event fired if selection is 
     * set directly via the treeSelectionModel. Characterize normal
     * treeSelection to mimic.
     */
    public void testSelectionChangedOnTreeSelection() {
        JXTreeTable treeTable = prepareTreeTable(true);
        TreePath oldSelected = treeTable.getPathForRow(0);
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

    /**
     * Issue #270-swingx: NPE in some contexts when accessing the 
     * TreeTableModelAdapter.
     *
     */
    public void testConservativeRowForNodeInAdapter() {
        // for testing we need a model which relies on 
        // node != null
        TreeTableModel model = new DefaultTreeTableModel((TreeNode) simpleTreeTableModel.getRoot()) {

            @Override
            public Object getValueAt(Object node, int column) {
                // access node
                node.toString();
                return super.getValueAt(node, column);
            }

            @Override
            public void setValueAt(Object value, Object node, int column) {
                // access node
                node.toString();
                super.setValueAt(value, node, column);
            }

            @Override
            public boolean isCellEditable(Object node, int column) {
                // access node
                node.toString();
                return super.isCellEditable(node, column);
            }
            
        };
        // can't use ComponentTreeTableModel with JXFrame in headless environment
//        JXTreeTable treeTable = new JXTreeTable(new ComponentTreeTableModel(new JXFrame()));
        JXTreeTable treeTable = new JXTreeTable(model);
        treeTable.setRootVisible(true);
        TableModel adapter = treeTable.getModel();
        treeTable.collapseAll();
        assertEquals(1, treeTable.getRowCount());
        // simulate contexts where the accessed row isn't currently visible
        adapter.getValueAt(treeTable.getRowCount(), 0);
        adapter.isCellEditable(treeTable.getRowCount(), 0);
        adapter.setValueAt("somename", treeTable.getRowCount(), 0);
    }
    /**
     * test if table and tree rowHeights are the same.
     *
     */
    public void testAdjustedRowHeights() {
        JXTreeTable treeTable = new JXTreeTable(simpleTreeTableModel);
        JXTree tree = (JXTree) treeTable.getCellRenderer(0, 0);
        // sanity: same initially
        assertEquals("table and tree rowHeights must be equal", 
                treeTable.getRowHeight(), tree.getRowHeight());
        // change treeTable height
        treeTable.setRowHeight(treeTable.getRowHeight() * 2);
        assertEquals("table and tree rowHeights must be equal", 
                treeTable.getRowHeight(), tree.getRowHeight());
        // change treeTable height
        tree.setRowHeight(tree.getRowHeight() * 2);
        assertEquals("table and tree rowHeights must be equal", 
                treeTable.getRowHeight(), tree.getRowHeight());

    }
    /**
     * #321-swingx: missing tree property toggleClickCount, largeModel.
     *
     */
    public void testToggleClickCount() {
        JXTreeTable treeTable = new JXTreeTable(simpleTreeTableModel);
        int clickCount = treeTable.getToggleClickCount();
        // asserting documented default clickCount == 2
        assertEquals("default clickCount", 2, clickCount);
        int newClickCount = clickCount + 1;
        treeTable.setToggleClickCount(newClickCount);
        assertEquals("toggleClickCount must be changed", 
                newClickCount, treeTable.getToggleClickCount());
        boolean largeModel = treeTable.isLargeModel();
        assertFalse("initial largeModel", largeModel);
        treeTable.setLargeModel(!largeModel);
        assertTrue("largeModel property must be toggled", treeTable.isLargeModel());
        
    }
    /**
     * Issue #168-jdnc: dnd enabled breaks node collapse/expand.
     * testing auto-detection of dragHackEnabled.
     * 
     */
    public void testDragHackFlagOn() {
        JXTreeTable treeTable = new JXTreeTable(simpleTreeTableModel);
        assertNull(treeTable.getClientProperty(JXTreeTable.DRAG_HACK_FLAG_KEY));
        treeTable.getTreeTableHacker().expandOrCollapseNode(0, 
                new MouseEvent(treeTable, MouseEvent.MOUSE_PRESSED, 0, InputEvent.BUTTON1_MASK, 0, 0, 1, false));
        Boolean dragHackFlag = (Boolean) treeTable.getClientProperty(JXTreeTable.DRAG_HACK_FLAG_KEY);
        assertNotNull(dragHackFlag);
        assertTrue(dragHackFlag);
    }

    /**
     * Issue #168-jdnc: dnd enabled breaks node collapse/expand.
     * testing auto-detection of dragHackEnabled.
     * 
     */
    public void testDragHackFlagOff() {
        System.setProperty("sun.swing.enableImprovedDragGesture", "true");
        JXTreeTable treeTable = new JXTreeTable(simpleTreeTableModel);
        assertNull(treeTable.getClientProperty(JXTreeTable.DRAG_HACK_FLAG_KEY));
        treeTable.getTreeTableHacker().expandOrCollapseNode(0, 
                new MouseEvent(treeTable, MouseEvent.MOUSE_PRESSED, 0, InputEvent.BUTTON1_MASK, 0, 0, 1, false));
        Boolean dragHackFlag = (Boolean) treeTable.getClientProperty(JXTreeTable.DRAG_HACK_FLAG_KEY);
        assertNotNull(dragHackFlag);
        assertFalse(dragHackFlag);
        System.getProperties().remove("sun.swing.enableImprovedDragGesture");
    }

    /**
     * loosely related to Issue #248-swingx: setRootVisible (true) after
     * initial rootInvisible didn't show the root.
     * 
     * this here is a sanity test that there is exactly one row, the problem
     * is a missing visual update of the table.
     *
     */
    public void testEmptyModelInitiallyInvisibleRoot() {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        final InsertTreeTableModel model = new InsertTreeTableModel(root);
        final JXTreeTable treeTable = new JXTreeTable(model);
        // sanity...
        assertFalse(treeTable.isRootVisible());
        assertEquals("no rows with invisible root", 0, treeTable.getRowCount());
        treeTable.setRootVisible(true);
        // sanity...
        assertTrue(treeTable.isRootVisible());
        assertEquals("one row with visible root", 1, treeTable.getRowCount());

    }
    
    /**
     * Issue #247-swingx: update probs with insert node.
     * The insert under a collapsed node fires a dataChanged on the table 
     * which results in the usual total "memory" loss (f.i. selection)
     *
     * The tree model is after setup is (see the bug report as well):
     * root
     *   childA
     *     childB
     *     
     * In the view childA is collapsed:
     * root
     *   childA  
     * 
     */
    public void testInsertUnderCollapsedNode() {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        final InsertTreeTableModel model = new InsertTreeTableModel(root);
        DefaultMutableTreeNode childA = model.addChild(root);
        final DefaultMutableTreeNode childB = model.addChild(childA);
        final JXTreeTable treeTable = new JXTreeTable(model);
        treeTable.setRootVisible(true);
        // sanity...
        assertEquals(2, treeTable.getRowCount());
        final int selected = 1;
        // select childA
        treeTable.setRowSelectionInterval(selected, selected);
        model.addChild(childB);
        // need to invoke - the tableEvent is fired delayed as well
        // Note: doing so will make the test _appear_ to pass, the
        // assertion failure can be seen as an output only!
        // any idea how to make the test fail?
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int selectedAfterInsert = treeTable.getSelectedRow();
                assertEquals(selected, selectedAfterInsert);

            }
        });
    }
    
    /**
     * Model used to show insert update issue.
     */
    public static class InsertTreeTableModel extends DefaultTreeTableModel {
        private boolean rootIsFolder;
        
        public InsertTreeTableModel(TreeNode root) {
            super(root);
        }

        public InsertTreeTableModel(TreeNode root, boolean rootIsFolder) {
            super(root);
            this.rootIsFolder = rootIsFolder;
        }
        
        @Override
        public boolean isLeaf(Object node) {
            if (rootIsFolder && (node == getRoot())) {
                return false;
            }
            return super.isLeaf(node);
        }


        public int getColumnCount() {
            return 2;
        }

        public DefaultMutableTreeNode addChild(DefaultMutableTreeNode parent) {
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("Child");
            parent.add(newNode);
            nodesWereInserted(parent, new int[] {parent.getIndex(newNode) });
//            fireTreeNodesInserted(this, getPathToRoot(parent),
//                    new int[] { parent.getIndex(newNode) },
//                    new Object[] { newNode });

            return newNode;
        }
    }


    /**
     * Issue #230-swingx: 
     * JXTreeTable should fire property change on setTreeTableModel.
     * 
     * 
     *
     */
    public void testTreeTableModelIsBoundProperty() {
        JXTreeTable treeTable = new JXTreeTable();
        PropertyChangeReport report = new PropertyChangeReport();
        treeTable.addPropertyChangeListener(report);
        treeTable.setTreeTableModel(simpleTreeTableModel);
        int allPropertyCount = report.getEventCount();
        int treeTMPropertyCount = report.getEventCount("treeTableModel");
        assertEquals("treeTable must have fired exactly one event for property treeTableModel", 
                1, treeTMPropertyCount);
        assertEquals("treeTable must have fired event for property treeTableModel only",
                allPropertyCount, treeTMPropertyCount);
        // sanity: must not fire when setting to same
        report.clear();
        treeTable.setTreeTableModel(simpleTreeTableModel);
        assertEquals("treeTable must not have fired", 0, report.getEventCount()); 
    }
    /**
     * Issue #54: hidden columns not removed on setModel.
     * sanity test (make sure nothing evil introduced in treeTable as 
     * compared to table)
     */
    public void testRemoveAllColumsAfterModelChanged() {
        JXTreeTable table = new JXTreeTable(new FileSystemModel());
        TableColumnExt columnX = table.getColumnExt(1);
        columnX.setVisible(false);
        int columnCount = table.getColumnCount(true);
        assertEquals("total column count must be same as model", table.getModel().getColumnCount(), columnCount);
        assertEquals("visible column count must one less as total", columnCount - 1, table.getColumnCount());
        table.setTreeTableModel(new FileSystemModel());
        assertEquals("visible columns must be same as total", 
                table.getColumnCount(), table.getColumnCount(true));
      }

    /**
     * Issue #241: treeModelListeners not removed.
     *
     */
    public void testRemoveListeners() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        treeTable.setTreeTableModel(new FileSystemModel());
        assertEquals(0, ((AbstractTreeTableModel) treeTableModel).getTreeModelListeners().length);
    }
    
    public void testRowForPath() {
        JXTreeTable treeTable = new JXTreeTable(simpleTreeTableModel);
        // @todo - make sure we find an expandible row instead of hardcoding
        int rowCount = treeTable.getRowCount();
        int row = 2;
        TreePath path = treeTable.getPathForRow(row);
        assertEquals("original row must be retrieved", row, treeTable.getRowForPath(path));
        treeTable.expandRow(row - 1);
        // sanity assert
        assertTrue("really expanded", treeTable.getRowCount() > rowCount);
        TreePath expanded = treeTable.getPathForRow(row);
        assertNotSame("path at original row must be different when expanded", path, expanded);
        assertEquals("original row must be retrieved", row, treeTable.getRowForPath(expanded));
        
    }
    
    public void testPathForRowContract() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        assertNull("row < 0 must return null path", treeTable.getPathForRow(-1));
        assertNull("row >= getRowCount must return null path", treeTable.getPathForRow(treeTable.getRowCount()));
    }
    
    public void testTableRowAtNegativePoint() {
        JXTable treeTable = new JXTable(1, 4);
        int negativeYRowHeight = - treeTable.getRowHeight();
        int negativeYRowHeightPlusOne = negativeYRowHeight + 1;
        int negativeYMinimal = -1;
        assertEquals("negative y location rowheight " + negativeYRowHeight + " must return row -1", 
                -1,  treeTable.rowAtPoint(new Point(-1, negativeYRowHeight)));
        assertEquals("negative y location " + negativeYRowHeightPlusOne +" must return row -1", 
                -1,  treeTable.rowAtPoint(new Point(-1, negativeYRowHeightPlusOne)));
        assertEquals("minimal negative y location must return row -1", 
                -1,  treeTable.rowAtPoint(new Point(-1, negativeYMinimal)));
        
    }


    public void testPathForLocationContract() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        // this is actually a JTable rowAtPoint bug: falsely calculates
        // row == 0 if  - 1 >= y > - getRowHeight()
        
        //assertEquals("location outside must return null path", null, treeTable.getPathForLocation(-1, -(treeTable.getRowHeight() - 1)));
        int negativeYRowHeight = - treeTable.getRowHeight();
        int negativeYRowHeightPlusOne = negativeYRowHeight + 1;
        int negativeYMinimal = -1;
        assertEquals("negative y location rowheight " + negativeYRowHeight + " must return row -1", 
                -1,  treeTable.rowAtPoint(new Point(-1, negativeYRowHeight)));
        assertEquals("negative y location " + negativeYRowHeightPlusOne +" must return row -1", 
                -1,  treeTable.rowAtPoint(new Point(-1, negativeYRowHeightPlusOne)));
        assertEquals("minimal negative y location must return row -1", 
                -1,  treeTable.rowAtPoint(new Point(-1, negativeYMinimal)));
    }
    /**
     * Issue #151: renderer properties ignored after setting treeTableModel.
     * 
     */
    public void testRendererProperties() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        // storing negates of properties
        boolean expandsSelected = !treeTable.getExpandsSelectedPaths();
        boolean scrollsOnExpand = !treeTable.getScrollsOnExpand();
        boolean showRootHandles = !treeTable.getShowsRootHandles();
        boolean rootVisible = !treeTable.isRootVisible();
        // setting negates properties
        treeTable.setExpandsSelectedPaths(expandsSelected);
        treeTable.setScrollsOnExpand(scrollsOnExpand);
        treeTable.setShowsRootHandles(showRootHandles);
        treeTable.setRootVisible(rootVisible);
        // assert negates are set - sanity assert
        assertEquals("expand selected", expandsSelected, treeTable
                .getExpandsSelectedPaths());
        assertEquals("scrolls expand", scrollsOnExpand, treeTable
                .getScrollsOnExpand());
        assertEquals("shows handles", showRootHandles, treeTable
                .getShowsRootHandles());
        assertEquals("root visible", rootVisible, treeTable.isRootVisible());
        // setting a new model
        treeTable.setTreeTableModel(new DefaultTreeTableModel());
        // assert negates are set
        assertEquals("expand selected", expandsSelected, treeTable
                .getExpandsSelectedPaths());
        assertEquals("scrolls expand", scrollsOnExpand, treeTable
                .getScrollsOnExpand());
        assertEquals("shows handles", showRootHandles, treeTable
                .getShowsRootHandles());
        assertEquals("root visible", rootVisible, treeTable.isRootVisible());

    }

    /**
     * Issue #148: line style client property not respected by renderer.
     * 
     */
    public void testLineStyle() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        String propertyName = "JTree.lineStyle";
        treeTable.putClientProperty(propertyName, "Horizontal");
        JXTree renderer = (JXTree) treeTable.getCellRenderer(0, 0);
        assertEquals(propertyName + " set on renderer", "Horizontal", renderer
                .getClientProperty(propertyName));
    }

    /**
     * sanity test: arbitrary client properties not passed to renderer.
     * 
     */
    public void testArbitraryClientProperty() {
        JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        String propertyName = "someproperty";
        treeTable.putClientProperty(propertyName, "Horizontal");
        JXTree renderer = (JXTree) treeTable.getCellRenderer(0, 0);
        assertNull(propertyName + " not set on renderer", renderer
                .getClientProperty(propertyName));

    }


    // ------------------ init
    protected void setUp() throws Exception {
        super.setUp();
        JXTree tree = new JXTree();
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        simpleTreeTableModel = new DefaultTreeTableModel((TreeNode) treeModel.getRoot());
        treeTableModel = new FileSystemModel();
    }

}
