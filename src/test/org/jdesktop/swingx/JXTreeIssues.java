/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import javax.swing.tree.DefaultMutableTreeNode;

public class JXTreeIssues extends JXTreeUnitTest {
    
    /**
     * Issue #221-swingx: actionMap not initialized in JXTreeNode constructor.
     * 
     * PENDING: test all constructors!
     *
     */
    public void testInitInConstructors() {
        assertFindActionRegistered(new JXTree(new DefaultMutableTreeNode("dummy"), false));
        assertFindActionRegistered(new JXTree(new DefaultMutableTreeNode("dummy")));
    }

    /**
     * @param tree
     */
    private void assertFindActionRegistered(JXTree tree) {
        assertNotNull("Actions must be initialized", tree.getActionMap().get("find"));
    }

}
