/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXTreeTableUnitTest.InsertTreeTableModel;


public class JXTreeIssues extends JXTreeUnitTest {
    
    /**
     * Issue #254-swingx: expandAll doesn't expand if root not shown?
     *
     */
    public void testExpandAllWithInvisible() {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        final InsertTreeTableModel model = new InsertTreeTableModel(root);
        int childCount = 5;
        for (int i = 0; i < childCount; i++) {
            model.addChild(root);
        }
        final JXTree treeTable = new JXTree(model);
        // sanity...
        assertTrue(treeTable.isRootVisible());
        assertEquals("all children visible", childCount + 1, treeTable.getRowCount());
        treeTable.collapseAll();
        assertEquals(" all children invisible", 1, treeTable.getRowCount());
        treeTable.setRootVisible(false);
        assertEquals("no rows with invisible root", 0, treeTable.getRowCount());
        treeTable.expandAll();
        assertTrue(treeTable.getRowCount() > 0);
        
    }

    
}
