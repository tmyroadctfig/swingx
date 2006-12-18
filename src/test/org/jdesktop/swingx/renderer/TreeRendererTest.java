/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
 *
 */
package org.jdesktop.swingx.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;

/**
 * Tests behaviour of SwingX renderers. Currently: mostly characterization to
 * guarantee that they behave similar to the standard.
 * 
 * @author Jeanette Winzenburg
 */
public class TreeRendererTest extends InteractiveTestCase {

    private static final Logger LOG = Logger.getLogger(TreeRendererTest.class
            .getName());
    
    
    private JTree tree;
    private DefaultTreeCellRenderer coreTreeRenderer;
    private DefaultTreeRenderer<JLabel> xTreeRenderer;
    
    @Override
    protected void setUp() throws Exception {
        tree = new JTree();
        coreTreeRenderer = new DefaultTreeCellRenderer();
        xTreeRenderer = DefaultTreeRenderer.createDefaultTreeRenderer();
    }

    public static void main(String[] args) {
        TreeRendererTest test = new TreeRendererTest();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
//------------------ tree renderer

    /**
     * base interaction with list: renderer uses list's unselected custom colors
     * 
     *
     */
    public void testTreeRendererExtTreeColors() {
        Color background = Color.MAGENTA;
        Color foreground = Color.YELLOW;
        tree.setBackground(background);
        tree.setForeground(foreground);
        coreTreeRenderer.setBackgroundNonSelectionColor(background);
        coreTreeRenderer.setTextNonSelectionColor(foreground);
        // prepare standard
        Component coreComponent = coreTreeRenderer.getTreeCellRendererComponent(tree, null,
                false, false, false, 0, false);
        // sanity: known standard behaviour
        // background is manually painted 
        assertEquals(background, coreComponent.getBackground());
        assertEquals(tree.getForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = xTreeRenderer.getTreeCellRendererComponent(tree, null,
                false, false, false, 0, false);
        // assert behaviour same as standard
        assertEquals(background, xComponent.getBackground());
        assertEquals(foreground, xComponent.getForeground());
    }

    public void interactiveCompareTreeExtTreeColors() {
        JTree xtree = new JTree();
        Color background = Color.MAGENTA;
        Color foreground = Color.YELLOW;
        xtree.setBackground(background);
        xtree.setForeground(foreground);
        DefaultTreeCellRenderer coreTreeCellRenderer = new DefaultTreeCellRenderer();
        coreTreeCellRenderer.setBackgroundNonSelectionColor(background);
        coreTreeCellRenderer.setTextNonSelectionColor(foreground);

        xtree.setCellRenderer(coreTreeCellRenderer);
        
        JTree tree = new JTree();
        tree.setBackground(background);
        tree.setForeground(foreground);
        tree.setCellRenderer(xTreeRenderer);
        
        
        final JXFrame frame = wrapWithScrollingInFrame(xtree, tree, "custom tree colors - core vs. ext renderer");
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
    public void interactiveCompareTreeExtColors() {
        JTree xtree = new JTree();
        xtree.setCellRenderer(coreTreeRenderer);
        
        JTree tree = new JTree();
        tree.setCellRenderer(xTreeRenderer);
        
        
        final JXFrame frame = wrapWithScrollingInFrame(xtree, tree, "normal tree colors - core vs. ext renderer");
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
     * base interaction with list: renderer uses list's unselected  colors
     * 
     *
     */
    public void testTreeRendererExtColors() {
        // prepare standard
        Component coreComponent = coreTreeRenderer.getTreeCellRendererComponent(tree, null,
                false, false, false, 0, false);
        // sanity: known standard behaviour
        assertNull(coreComponent.getBackground());
//        assertNull(coreComponent.getForeground());
        assertNull(tree.getForeground());
        Color uiForeground = UIManager.getColor("Tree.textForeground");
        assertEquals(uiForeground, coreComponent.getForeground());
        // prepare extended
        Component xComponent = xTreeRenderer.getTreeCellRendererComponent(tree, null,
                false, false, false, 0, false);
        // assert behaviour same as standard
//        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
    }

    /**
     * characterize opaqueness of rendering components.
     * Hmm... tree-magic is different
     */
    public void testTreeOpaqueRenderer() {
        // sanity
        assertFalse(new JLabel().isOpaque());
        
//        assertTrue(coreTreeRenderer.isOpaque());
//        assertTrue(xListRenderer.getRendererComponent().isOpaque());
    }

    /**
     * base existence/type tests while adding DefaultTableCellRendererExt.
     *
     */
    public void testTreeRendererExt() {
        DefaultTreeRenderer<JLabel> renderer = DefaultTreeRenderer.createDefaultTreeRenderer();
        assertTrue(renderer instanceof TreeCellRenderer);
        assertTrue(renderer instanceof Serializable);
        
    }


}
