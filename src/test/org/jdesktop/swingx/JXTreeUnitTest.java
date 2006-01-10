/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

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
