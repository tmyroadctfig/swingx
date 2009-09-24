/*
 * $Id$
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.jdesktop.swingx.action.AbstractActionExt;

/**
 * 
 * @author Jeanette Winzenburg
 */
public class JXTaskPaneIssues extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(JXTaskPaneIssues.class
            .getName());
    
    public static void main(String[] args) {
        JXTaskPaneIssues test = new JXTaskPaneIssues();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Issue #1181-swingx: scrollRectToVisible not effective if component in JXTaskPane.
     * 
     * example adapted from tjwolf,
     * http://forums.java.net/jive/thread.jspa?threadID=66759&tstart=0
     */
    public void interactiveScrollRectToVisible() {
        final JXTree tree = new JXTree();
        tree.expandAll();
        JXTaskPane pane = new JXTaskPane();
        pane.add(tree);
        JComponent component = new JPanel(new BorderLayout());
        component.add(pane);
        JXFrame frame = wrapWithScrollingInFrame(component, "scroll to last row must work");
        Action action = new AbstractActionExt("scrollToLastRow") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                tree.scrollRowToVisible(tree.getRowCount()- 1);
            }
        };
        addAction(frame, action);
        // small dimension, make sure there is something to scroll
        show(frame, 300, 200);
    }
    
    /**
     * Trying to resize a top-level window on collapsed state changes of a taskpane.
     * Need to listen to "animationState" which is fired when animation is complete.
     */
    public void interactiveDialogWithCollapsible() {
        JXTaskPane pane = new JXTaskPane();
        pane.setTitle("dummy ... with a looooooooooooong title");
        Action action = new AbstractActionExt("something to click") {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got me");
            }
            
        };
        JComponent button = (JComponent) pane.add(action);
        Object actionKey = "dummy";
        button.getActionMap().put(actionKey, action);
        KeyStroke keyStroke = KeyStroke.getKeyStroke("F3");
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, actionKey);
        final JXDialog dialog = new JXDialog(pane);
        PropertyChangeListener l = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ("animationState".equals(evt.getPropertyName())) {
                    dialog.pack();
                }
            }
            
        };
        pane.addPropertyChangeListener(l);
        dialog.pack();
        dialog.setVisible(true);
    }
    

    /**
     * Quick check to see if hidden comps receive a keybinding (no).
     */
    public void interactiveDialogWithHidden() {
        JXPanel pane = new JXPanel(new BorderLayout());
        final JButton label = new JButton("dummy ... with a looooooooooooong title");
        Action action = new AbstractActionExt("something to click") {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got me");
            }
            
        };
        pane.add(label);
        Object actionKey = "dummy";
        label.getActionMap().put(actionKey, action);
        KeyStroke keyStroke = KeyStroke.getKeyStroke("F3");
        label.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, actionKey);
        
        Action hide = new AbstractActionExt("hide other, then press F3") {

            public void actionPerformed(ActionEvent e) {
                // OOPS ... visible is _not_ a bean property
                // so property change listener is useless if we want to pack
                label.setVisible(!label.isVisible());
            }
            
        };
        pane.add(new JButton(hide), BorderLayout.NORTH);
        final JXDialog dialog = new JXDialog(pane);
        dialog.setTitle("hiding a component plain ");
        dialog.pack();
        dialog.setVisible(true);
    }


    /**
     * Empty test method to keep the test runner happy if we have no 
     * open issues.
     */
    public void testDummy() {
        
    }
}
