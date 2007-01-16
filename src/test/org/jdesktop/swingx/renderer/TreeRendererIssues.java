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

import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.jdesktop.swingx.InteractiveTestCase;

/**
 * Known/open issues with tree renderer.
 * 
 * @author Jeanette Winzenburg
 */
public class TreeRendererIssues extends InteractiveTestCase {
    private JTree tree;
    private DefaultTreeCellRenderer coreTreeRenderer;
    private DefaultTreeRenderer xTreeRenderer;

    @Override
    protected void setUp() throws Exception {
//        setSystemLF(true);
//        LOG.info("LF: " + UIManager.getLookAndFeel());
//        LOG.info("Theme: " + ((MetalLookAndFeel) UIManager.getLookAndFeel()).getCurrentTheme());
//        UIManager.put("Tree.drawsFocusBorderAroundIcon", Boolean.TRUE);
        tree = new JTree();
        coreTreeRenderer = new DefaultTreeCellRenderer();
        xTreeRenderer = new DefaultTreeRenderer();
    }
    public static void main(String[] args) {
        TreeRendererIssues test = new TreeRendererIssues();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * base interaction with list: renderer uses list's unselected  colors
     * 
     * currently, this test fails because the assumptions are wrong! Core
     * renderer behaves slightly unexpected.
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
     * base interaction with list: renderer uses list's unselected custom
     * colors.
     * 
     * currently, this test fails because the assumptions are wrong! Core
     * renderer behaves slightly unexpected.
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
        Component coreComponent = coreTreeRenderer
                .getTreeCellRendererComponent(tree, null, false, false, false,
                        0, false);
        // sanity: known standard behaviour
        // background is manually painted
        assertEquals(background, coreComponent.getBackground());
        assertEquals(tree.getForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = xTreeRenderer.getTreeCellRendererComponent(tree,
                null, false, false, false, 0, false);
        // assert behaviour same as standard
        assertEquals(background, xComponent.getBackground());
        assertEquals(foreground, xComponent.getForeground());
    }

}
