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

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JRootPane;


/**
 * A smarter JFrame specifically used for top level frames for Applications.
 * This frame uses a JXRootPane.
 */
public class JXFrame extends JFrame {
    private Component waitPane = null;
    private Component glassPane = null;
    private boolean waitPaneVisible = false;
    private Cursor realCursor = null;
    private boolean waitCursorVisible = false;
    
    public JXFrame() {
        this(null, false);
    }
    
    public JXFrame(String title, boolean exitOnClose) {
        super(title);
        if (exitOnClose) {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }

    public JXFrame(String title) {
        this(title, false);
    }

//    public void setCancelButton(JButton button) {
//        
//    }
//    
//    public JButton getCancelButton() {
//        
//    }
//    
    public void setDefaultButton(JButton button) {
        JButton old = getDefaultButton();
        getRootPane().setDefaultButton(button);
        firePropertyChange("defaultButton", old, getDefaultButton());
    }
    
    public JButton getDefaultButton() {
        return getRootPane().getDefaultButton();
    }
    
//    public void setKeyPreview(boolean flag) {
//        
//    }
//    
//    public boolean getKeyPreview() {
//        
//    }
//    
//    public void setStartPosition(StartPosition position) {
//        
//    }
//    
//    public StartPosition getStartPosition() {
//        
//    }
    
    public void setWaitCursorVisible(boolean flag) {
        boolean old = isWaitCursorVisible();
        if (flag != old) {
            waitCursorVisible = flag;
            if (isWaitCursorVisible()) {
                realCursor = getCursor();
                super.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            } else {
                super.setCursor(realCursor);
            }
            firePropertyChange("waitCursorVisible", old, isWaitCursorVisible());
        }
    }
    
    public boolean isWaitCursorVisible() {
        return waitCursorVisible;
    }
    
    @Override
    public void setCursor(Cursor c) {
        if (!isWaitCursorVisible()) {
            super.setCursor(c);
        } else {
            this.realCursor = c;
        }
    }
    
    public void setWaitPane(Component c) {
        Component old = getWaitPane();
        this.waitPane = c;
        firePropertyChange("waitPane", old, getWaitPane());
    }
    
    public Component getWaitPane() {
        return waitPane;
    }
    
    public void setWaitPaneVisible(boolean flag) {
        boolean old = isWaitPaneVisible();
        if (flag != old) {
            this.waitPaneVisible = flag;
            Component wp = getWaitPane();
            if (isWaitPaneVisible()) {
                glassPane = getRootPane().getGlassPane();
                if (wp != null) {
                    getRootPane().setGlassPane(wp);
                    wp.setVisible(true);
                }
            } else {
                if (wp != null) {
                    wp.setVisible(false);
                }
                getRootPane().setGlassPane(glassPane);
            }
            firePropertyChange("waitPaneVisible", old, isWaitPaneVisible());
        }
    }
    
    public boolean isWaitPaneVisible() {
        return waitPaneVisible;
    }
    
    //---------------------------------------------------- Root Pane Methods
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
}

