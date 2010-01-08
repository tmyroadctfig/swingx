/*
 * $Id$
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx;

import java.awt.ComponentOrientation;
import java.util.logging.Logger;

import javax.swing.JTree;

/**
 * Issue with core JTree.
 * 
 */
public class JTreeIssues extends InteractiveTestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(JTreeIssues.class
            .getName());
    
    public static void main(String[] args) {
        JTreeIssues test = new JTreeIssues();
        try {
            setLookAndFeel("Nimbus");
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Core Issue: Nimbus Tree Handle Icon not bidi compliant.
     * 
     * several problems in RToL
     * - the closed-icon should point to the left, that is the opposite direction or LToR
     * - handle position is incorrect, overlapping under the node content
     * 
     * Unrelated to Nimbus: editor completely wrecked (node icon jumps to the left)
     */
    public void interactiveNimbusHandleIconRToL() {
        JTree tree = new JTree();
        tree.setEditable(true);
        JXFrame frame = wrapWithScrollingInFrame(tree, "Nimbus handle");
        frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        addComponentOrientationToggle(frame);
        show(frame);
    }
    

}
