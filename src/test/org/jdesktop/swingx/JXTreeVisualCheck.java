/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
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
import org.jdesktop.swingx.tree.DefaultXTreeCellEditor;

public class JXTreeVisualCheck extends JXTreeUnitTest {

    /**
     * visualize editing of the hierarchical column, both
     * in a tree and a xTree switching CO.
     *
     * standard editor
     */
    public void interactiveTreeEditingRToL() {
        JTree tree =  new JTree(); 
        tree.setEditable(true);
        JXTree xTree = new JXTree();
        xTree.setEditable(true);
        final JXFrame frame = wrapWithScrollingInFrame(tree, xTree, "standard Editing: compare tree and xtree");
        Action toggleComponentOrientation = new AbstractAction("toggle orientation") {

            public void actionPerformed(ActionEvent e) {
                ComponentOrientation current = frame.getComponentOrientation();
                if (current == ComponentOrientation.LEFT_TO_RIGHT) {
                    frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                } else {
                    frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                }

            }

        };
        addAction(frame, toggleComponentOrientation);
        frame.setVisible(true);
        
    }



    /**
     * visualize editing of the hierarchical column, both
     * in a tree and a xTree switching CO.
     * using DefaultXTreeCellEditor.
     */
    public void interactiveXTreeEditingRToL() {
        JTree tree =  new JTree(); 
        tree.setEditable(true);
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        tree.setCellRenderer(renderer);
        tree.setCellEditor(new DefaultXTreeCellEditor(tree, renderer));
        JXTree xTree = new JXTree();
        xTree.setEditable(true);
        // JW: changed xTree to use the xEditor by default
//        TreeCellRenderer xRenderer = xTree.getCellRenderer();
//        if (xRenderer instanceof JXTree.DelegatingRenderer) {
//            TreeCellRenderer delegate = ((JXTree.DelegatingRenderer) xRenderer).getDelegateRenderer();
//            if (delegate instanceof DefaultTreeCellRenderer) { 
//                xTree.setCellEditor(new DefaultXTreeCellEditor(xTree, (DefaultTreeCellRenderer) delegate));
//            }   
//        }
        final JXFrame frame = wrapWithScrollingInFrame(tree, xTree, "XEditing: compare tree and xtree");
        Action toggleComponentOrientation = new AbstractAction("toggle orientation") {

            public void actionPerformed(ActionEvent e) {
                ComponentOrientation current = frame.getComponentOrientation();
                if (current == ComponentOrientation.LEFT_TO_RIGHT) {
                    frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                } else {
                    frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                }

            }

        };
        addAction(frame, toggleComponentOrientation);
        frame.setVisible(true);
        
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
        frame.setVisible(true);  
        
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
        frame.setVisible(true);  
        
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
        frame.setVisible(true);  
        
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
        frame.setVisible(true);  

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
        frame.setVisible(true);  
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
        JXFrame frame = wrapWithScrollingInFrame(treeTable,
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
        JXFrame frame = wrapWithScrollingInFrame(treeTable,
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
        frame.setVisible(true);  
    }

    public static void main(String[] args) {
//        setSystemLF(true);
        JXTreeVisualCheck test = new JXTreeVisualCheck();
        try {
//            test.runInteractiveTests();
            test.runInteractiveTests("interactive.*RToL.*");
//            test.runInteractiveTests("interactive.*Edit.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

}
