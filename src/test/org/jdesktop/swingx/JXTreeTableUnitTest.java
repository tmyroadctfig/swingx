/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Point;

import javax.swing.JTable;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.util.PropertyChangeReport;


public class JXTreeTableUnitTest extends InteractiveTestCase {

    protected TreeTableModel treeTableModel;
    protected TreeTableModel simpleTreeTableModel;
    
    public JXTreeTableUnitTest() {
        super("JXTreeTable Unit Test");
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

    public void testTableRowAtOutsidePoint() {
        JTable treeTable = new JTable(2, 4);
        int negativeYRowHeight = (treeTable.getRowHeight()+ treeTable.getRowMargin()) * treeTable.getRowCount() ;
        int negativeYRowHeightPlusOne = negativeYRowHeight - 1;
        int negativeYMinimal = -1;
        assertEquals("negative y location rowheight " + negativeYRowHeight + " must return row -1", 
                -1,  treeTable.rowAtPoint(new Point(-1, negativeYRowHeight)));
        assertEquals("negative y location " + negativeYRowHeightPlusOne +" must return row -1", 
                -1,  treeTable.rowAtPoint(new Point(-1, negativeYRowHeightPlusOne)));
//        assertEquals("minimal negative y location must return row -1", 
//                -1,  treeTable.rowAtPoint(new Point(-1, negativeYMinimal)));
        
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
