/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.PatternHighlighter;
import org.jdesktop.swingx.decorator.RolloverHighlighter;
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
    public void testNullModel() {
        JXTree tree = new JXTree();
        assertNotNull(tree.getModel());
        tree.setModel(null);
        assertEquals(0, tree.getRowCount());
        // tree.getComponentAdapter().isLeaf();
    }

    /**
     * Issue ??: Background highlighters not working on JXTree.
     *
     */
    public void interactiveUnselectedFocusedBackground() {
        JXTree xtree = new JXTree(treeTableModel);
        xtree.setBackground(new Color(0xF5, 0xFF, 0xF5));
        JTree tree = new JTree(treeTableModel);
        tree.setBackground(new Color(0xF5, 0xFF, 0xF5));
        JFrame frame = wrapWithScrollingInFrame(xtree, tree, "Unselected focused background: JXTree/JTree" );
        frame.setVisible(true);  // RG: Changed from deprecated method show();
        
    }

    /**
     * Issue ??: Background highlighters not working on JXTree.
     *
     */
    public void interactiveTestRolloverHighlight() {
        JXTree tree = new JXTree(treeTableModel);
        tree.setRolloverEnabled(true);
        tree.setHighlighters(createRolloverPipeline(true));
        JFrame frame = wrapWithScrollingInFrame(tree, "Rollover  " );
        frame.setVisible(true);  // RG: Changed from deprecated method show();
        
    }

    private HighlighterPipeline createRolloverPipeline(boolean useForeground) {
        Color color = new Color(0xF0, 0xF0, 0xE0); //Highlighter.ledgerBackground.getBackground();
        Highlighter highlighter = new RolloverHighlighter(
                useForeground ? null : color, useForeground ? color.darker() : null);
        return new HighlighterPipeline( new Highlighter[] {highlighter});
    }
    
    /**
     * Issue ??: Background highlighters not working on JXTree.
     *
     */
    public void interactiveTestHighlighters() {
        JXTree tree = new JXTree(treeTableModel);
        String pattern = "o";
        tree.setHighlighters(new HighlighterPipeline(new Highlighter[] {
                new PatternHighlighter(null, Color.red, pattern, 0, 1),
            }));
//        tree.setHighlighters(new HighlighterPipeline(
//                new Highlighter[] { AlternateRowHighlighter.classicLinePrinter, }));
        JFrame frame = wrapWithScrollingInFrame(tree, "Highlighters: " + pattern);
        frame.setVisible(true);  // RG: Changed from deprecated method show();
        
    }
    
    
    public void interactiveTestToolTips() {
        JXTree tree = new JXTree(treeTableModel);
        // JW: don't use this idiom - Stackoverflow...
        // multiple delegation - need to solve or discourage
        tree.setCellRenderer(createRenderer());
        // JW: JTree does not automatically register itself
        // should JXTree? 
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.registerComponent(tree);
        JFrame frame = wrapWithScrollingInFrame(tree, "tooltips");
        frame.setVisible(true);  // RG: Changed from deprecated method show();

    }
    
    
    private TreeCellRenderer createRenderer() {
        final TreeCellRenderer delegate = new DefaultTreeCellRenderer();
        TreeCellRenderer renderer = new TreeCellRenderer() {

            public Component getTreeCellRendererComponent(JTree tree, Object value, 
                    boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component result = delegate.getTreeCellRendererComponent(tree, value, 
                        selected, expanded, leaf, row, hasFocus);
                ((JComponent) result).setToolTipText(String.valueOf(tree.getPathForRow(row)));
                 return result;
            }
            
        };
        return renderer;
    }

    /**
     * test if lineStyle client property is respected by JXTree.
     * Note that some LFs don't respect anyway (WinLF f.i.)
     */
    public void interactiveTestLineStyle() {
        JXTree tree = new JXTree(treeTableModel);
        tree.setDragEnabled(true);
        tree.putClientProperty("JTree.lineStyle", "None");
        JFrame frame = wrapWithScrollingInFrame(tree, "LineStyle Test");
        frame.setVisible(true);  // RG: Changed from deprecated method show();
    }

    /**    
     * setting tree properties: JXTree is updated properly.
     */    
    public void interactiveTestTreeProperties() {
        final JXTree treeTable = new JXTree(treeTableModel);
        Action toggleHandles = new AbstractAction("Toggle Handles") {

            public void actionPerformed(ActionEvent e) {
                treeTable.setShowsRootHandles(!treeTable.getShowsRootHandles());
                
            }
            
        };
        Action toggleRoot = new AbstractAction("Toggle Root") {

            public void actionPerformed(ActionEvent e) {
                treeTable.setRootVisible(!treeTable.isRootVisible());
                
            }
            
        };
        treeTable.setRowHeight(22);
        JFrame frame = wrapWithScrollingInFrame(treeTable,
                "Toggle Tree properties ");
        addAction(frame, toggleRoot);
        addAction(frame, toggleHandles);
        frame.setVisible(true);
    }
    
    /**    
     * setting tree properties: scrollsOnExpand.
     * does nothing...
     * 
     */    
    public void interactiveTestTreeExpand() {
        final JXTree treeTable = new JXTree(treeTableModel);
        Action toggleScrolls = new AbstractAction("Toggle Scroll") {

            public void actionPerformed(ActionEvent e) {
                treeTable.setScrollsOnExpand(!treeTable.getScrollsOnExpand());
                
            }
            
        };
         Action expand = new AbstractAction("Expand") {

            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = treeTable.getSelectionRows();
                if (selectedRows.length > 0) {
                    treeTable.expandRow(selectedRows[0]);
                }
               
            }
            
        };
 
        treeTable.setRowHeight(22);
        JFrame frame = wrapWithScrollingInFrame(treeTable,
                "Toggle Tree expand properties ");
        addAction(frame, toggleScrolls);
        addAction(frame, expand);
        frame.setVisible(true);
    }
    

    
    /**
     * test if showsRootHandles client property is respected by JXTree.
     */
    public void interactiveTestShowsRootHandles() {
        JXTree tree = new JXTree(treeTableModel);
        tree.setShowsRootHandles(false);
        tree.setRootVisible(false);
        JXTree otherTree = new JXTree(treeTableModel);
        otherTree.setRootVisible(true);
        otherTree.setShowsRootHandles(false);
        JFrame frame = wrapWithScrollingInFrame(tree, otherTree, "ShowsRootHandles");
        frame.setVisible(true);  // RG: Changed from deprecated method show();
    }

    protected void setUp() throws Exception {
        super.setUp();
        treeTableModel = new FileSystemModel();
    }

    public static void main(String[] args) {
        setSystemLF(true);
        JXTreeUnitTest test = new JXTreeUnitTest();
        try {
            test.runInteractiveTests();
          //  test.runInteractiveTests("interactive.*High.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
}
