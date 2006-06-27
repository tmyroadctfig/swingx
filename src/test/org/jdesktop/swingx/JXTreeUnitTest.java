/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.swingx.tree.DefaultXTreeCellEditor;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.swingx.treetable.TreeTableModel;


/**
 * @author Jeanette Winzenburg
 */
public class JXTreeUnitTest extends InteractiveTestCase {

    protected TreeTableModel treeTableModel;
        
    public JXTreeUnitTest() {
        super("JXTree Test");
    }

    /**
     * test enhanced getSelectedRows contract: returned 
     * array != null
     *
     */
    public void testNotNullGetSelectedRows() {
        JXTree tree = new JXTree(treeTableModel);
        // sanity: no selection
        assertEquals(0, tree.getSelectionCount());
        assertNotNull("getSelectedRows guarantees not null array", tree.getSelectionRows());
    }
    
    /**
     * test enhanced getSelectedRows contract: returned 
     * array != null
     *
     */
    public void testNotNullGetSelectedPaths() {
        JXTree tree = new JXTree(treeTableModel);
        // sanity: no selection
        assertEquals(0, tree.getSelectionCount());
        assertNotNull("getSelectedPaths guarantees not null array", tree.getSelectionPaths());
    }
    /**
     * Issue #221-swingx: actionMap not initialized in JXTreeNode constructor.
     * Issue #231-swingx: icons lost in editor, enhanced default editor not installed.
     * 
     * PENDING: test all constructors!
     *
     */
    public void testInitInConstructors() {
        assertXTreeInit(new JXTree());
        assertXTreeInit(new JXTree(new Object[] {}));
        assertXTreeInit(new JXTree(new Vector()));
        assertXTreeInit(new JXTree(new Hashtable()));
        assertXTreeInit(new JXTree(new DefaultMutableTreeNode("dummy"), false));
        assertXTreeInit(new JXTree(new DefaultMutableTreeNode("dummy")));
        assertXTreeInit(new JXTree(new DefaultTreeModel(new DefaultMutableTreeNode("dummy"))));
    }

    /**
     * @param tree
     */
    private void assertXTreeInit(JXTree tree) {
        assertNotNull("Actions must be initialized", tree.getActionMap().get("find"));
        assertTrue("Editor must be DefaultXTreeCellEditor", 
                tree.getCellEditor() instanceof DefaultXTreeCellEditor);
        // JW: wrong assumption, available for TreeTableModel impl only?
//        assertNotNull("conversionMethod must be initialized", 
//                tree.getValueConversionMethod(tree.getModel()));
//        tree.getValueConversionMethod(tree.getModel());
    }

    /** 
     * JTree allows null model.
     * learning something new every day :-)
     *
     */
    public void testNullModel() {
        JXTree tree = new JXTree();
        assertNotNull(tree.getModel());
        tree.setModel(null);
        assertEquals(0, tree.getRowCount());
        // tree.getComponentAdapter().isLeaf();
    }

    
    protected void setUp() throws Exception {
        super.setUp();
        treeTableModel = new FileSystemModel();
    }

}
