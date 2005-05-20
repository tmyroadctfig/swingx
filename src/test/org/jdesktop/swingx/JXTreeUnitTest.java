/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import javax.swing.JFrame;
import javax.swing.JTree;

import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.swingx.treetable.TreeTableModel;

//import de.kleopatra.view.LFSwitcher;

/**
 * @author Jeanette Winzenburg
 */
public class JXTreeUnitTest extends InteractiveTestCase {

  	TreeTableModel treeTableModel;    // shared

    public JXTreeUnitTest() {
        super("JXTree Test");
    }

    /** 
     * Needed because testCase fails if it does not have at least 
     * one fixture. 
     * learning something new every day :-)
     *
     */
    public void testDummy() {
        
    }
    /**
     * test if lineStyle client property is respected by JXTree.
     * Note that some LFs don't respect anyway (WinLF f.i.)
     */
    public void interactiveTestLineStyle() {
        JXTree tree = new JXTree(treeTableModel);
        tree.putClientProperty("JTree.lineStyle", "None");
        JFrame frame = wrapWithScrollingInFrame(tree, "LineStyle Test");
        frame.setVisible(true);  // RG: Changed from deprecated method show();
    }
 
    /**
     * test if showsRootHandles client property is respected by JXTree.
     */
    public void interactiveTestShowsRootHandles() {
        JXTree tree = new JXTree(treeTableModel);
        JXTree otherTree = new JXTree(treeTableModel);
        otherTree.setShowsRootHandles(!tree.getShowsRootHandles());
        JFrame frame = wrapWithScrollingInFrame(tree, otherTree, "ShowsRootHandles");
        frame.setVisible(true);  // RG: Changed from deprecated method show();
    }

    protected void setUp() throws Exception {
        super.setUp();
        treeTableModel = new FileSystemModel();
    }

    public static void main(String[] args) {
//        LFSwitcher.windowsLF();
        JXTreeUnitTest test = new JXTreeUnitTest();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
}
