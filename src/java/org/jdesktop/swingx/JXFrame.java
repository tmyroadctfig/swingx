/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JRootPane;


/**
 * A smarter JFrame specifically used for top level frames for Applications.
 * This frame uses a JXRootPane.
 */
public class JXFrame extends JFrame {

    public JXFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public JXFrame(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Overloaded to create a JXRootPane.
     */
    protected JRootPane createRootPane() {
        return new JXRootPane();
    }

    /**
     * Overloaded to make this public.
     */
    public void setRootPane(JRootPane root) {
        super.setRootPane(root);
    }

    /**
     * Add a component to the Frame.
     */
    public void addComponent(Component comp) {
        JXRootPane root = getRootPaneExt();
        if (root != null) {
            root.addComponent(comp);
        }
        // XXX should probably fire some sort of container event.
    }

    /**
     * Removes a component from the frame.
     */
    public void removeComponent(Component comp) {
        JXRootPane root = getRootPaneExt();
        if (root != null) {
            root.removeComponent(comp);
        }
        // XXX should probably fire some sort of container event.
    }

    /**
     * Return the extended root pane. If this frame doesn't contain
     * an extended root pane the root pane should be accessed with
     * getRootPane().
     *
     * @return the extended root pane or null.
     */
    public JXRootPane getRootPaneExt() {
        if (rootPane instanceof JXRootPane) {
            return (JXRootPane)rootPane;
        }
        return null;
    }

    /**
     * Overloaded to pack when visible is true.
     */
    public void setVisible(boolean visible) {
        if (visible) {
            pack();
        }
        super.setVisible(visible);
    }
}

