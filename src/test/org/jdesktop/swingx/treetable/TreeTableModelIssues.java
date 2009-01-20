/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.treetable;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import junit.framework.TestCase;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * 
 * Known issues around TreeTableModel and related classes.
 * 
 */
@RunWith(JUnit4.class)
public class TreeTableModelIssues extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
        .getLogger(TreeTableModelIssues.class.getName());
    
    public static void main(String[] args) {
        TreeTableModelIssues test = new TreeTableModelIssues();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Issue #984-swingx: IllegalArgumentException if removing selected node.
     * 
     * base reason: DefaultTreeTableModel getIndexOfChild violates super's 
     * contract.
     * 
     */
    public void interactiveRemoveLargeModel() {
        final ClearableTreeTableModel model = new ClearableTreeTableModel();
        model.addToRoot("dummy");
        JTree tree = new JTree(model);
        tree.expandRow(0);
        tree.setSelectionInterval(1, 1);
        tree.setLargeModel(true);
        JXFrame frame = wrapWithScrollingInFrame(tree, "remove and large");
        Action remove = new AbstractAction("remove selected") {

            public void actionPerformed(ActionEvent e) {
                model.clear();
                setEnabled(false);
            }
            
        };
        addAction(frame, remove);
        show(frame, 400, 200);
    }
    
    
    // Model that allows to remove all nodes except the root node
    public static class ClearableTreeTableModel extends DefaultTreeTableModel {
        public ClearableTreeTableModel() {
            super(new DefaultMutableTreeTableNode("Root"));

        }

        public void addToRoot(Object userObject) {
            DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode(
                    userObject);
            insertNodeInto(node, (MutableTreeTableNode) getRoot(), 0);
        }
        
        public void clear() {
            // remove all nodes except the root node
            for (int i = this.getChildCount(getRoot()) - 1; i >= 0; i--) {
                this.removeNodeFromParent((MutableTreeTableNode) this.getChild(
                        getRoot(), i));
            }
        }
    };

    
    /**
     * Issue #984-swingx: DefaultTreeTableModel violates contract of TreeModel.
     */
    @Test
    public void testIndexOfChildEmpty() {
        DefaultTreeTableModel model = new DefaultTreeTableModel();
        assertEquals("empty model must not throw on arbitrary node params", 
                -1, model.getIndexOfChild(new Object(), new Object()));
    }

    /**
     * Issue #984-swingx: DefaultTreeTableModel violates contract of TreeModel.
     */
    @Test
    public void testIndexOfChildWithRoot() {
        DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode("dummy");
        DefaultTreeTableModel model = new DefaultTreeTableModel(root);
        assertEquals(" model must not throw on arbitrary child param", 
                -1, model.getIndexOfChild(root, new Object()));
    }
    
    /**
     * Issue #984-swingx: DefaultTreeTableModel violates contract of TreeModel.
     */
    @Test
    public void testIndexOfChildWithRootAndChild() {
        DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode("dummy");
        DefaultTreeTableModel model = new DefaultTreeTableModel(root);
        DefaultMutableTreeTableNode child = new DefaultMutableTreeTableNode("first");
        model.insertNodeInto(child, root, 0);
        assertEquals(" model must not throw on arbitrary parent param", 
                -1, model.getIndexOfChild(new Object(), child));
    }
    
    /**
     * TreePath issue: null must not be allowed as path element.
     * The constructor doesn't cope with array containing null.
     */
    @Test
    public void testTreeStructureChangedEmptyPath() {
       TreePath path = new TreePath(new Object[] {null});
       assertNotNull("TreePath must not contain null path elements", 
               path.getLastPathComponent()); 
    }


}
