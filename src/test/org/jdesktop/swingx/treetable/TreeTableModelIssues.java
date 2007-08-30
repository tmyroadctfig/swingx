/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.treetable;

import java.util.logging.Logger;

import javax.swing.tree.TreePath;

import junit.framework.TestCase;

/**
 * 
 * Known issues around TreeTableModel and related classes.
 * 
 */
public class TreeTableModelIssues extends TestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
        .getLogger(TreeTableModelIssues.class.getName());

    /**
     * TreePath issue: null must not be allowed as path element.
     * The constructor doesn't cope with array containing null.
     */
    public void testTreeStructureChangedEmptyPath() {
       TreePath path = new TreePath(new Object[] {null});
       assertNotNull("TreePath must not contain null path elements", 
               path.getLastPathComponent()); 
    }


}
