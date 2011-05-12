/*
 * $Id$
 *
 * Copyright 2010 Sun Microsystems, Inc., 4150 Network Circle,
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

import java.awt.HeadlessException;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.JToolBar;

/**
 * An applet that uses {@link JXRootPane} as its root container.
 * 
 * @author kschaefer
 */
public class JXApplet extends JApplet {
    /**
     * Creates a the applet instance.
     * <p>
     * This constructor sets the component's locale property to the value returned by
     * <code>JComponent.getDefaultLocale</code>.
     * 
     * @throws HeadlessException
     *             if GraphicsEnvironment.isHeadless() returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public JXApplet() throws HeadlessException {
        super();
    }

    /**
     * Overridden to create a JXRootPane.
     */
    @Override
    protected JXRootPane createRootPane() {
        return new JXRootPane();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public JXRootPane getRootPane() {
        return (JXRootPane) super.getRootPane();
    }
    
    /**
     * Returns the value of the status bar property from the underlying
     * {@code JXRootPane}.
     * 
     * @return the {@code JXStatusBar} which is the current status bar
     * @see #setStatusBar(JXStatusBar)
     * @see JXRootPane#getStatusBar()
     */
    public JXStatusBar getStatusBar() {
        return getRootPane().getStatusBar();
    }
    
    /**
     * Sets the status bar property on the underlying {@code JXRootPane}.
     * 
     * @param statusBar
     *            the {@code JXStatusBar} which is to be the status bar
     * @see #getStatusBar()
     * @see JXRootPane#setStatusBar(JXStatusBar)
     */
    public void setStatusBar(JXStatusBar statusBar) {
        getRootPane().setStatusBar(statusBar);
    }
    
    /**
     * Returns the value of the tool bar property from the underlying
     * {@code JXRootPane}.
     * 
     * @return the {@code JToolBar} which is the current tool bar
     * @see #setToolBar(JToolBar)
     * @see JXRootPane#getToolBar()
     */
    public JToolBar getToolBar() {
        return getRootPane().getToolBar();
    }
    
    /**
     * Sets the tool bar property on the underlying {@code JXRootPane}.
     * 
     * @param toolBar
     *            the {@code JToolBar} which is to be the tool bar
     * @see #getToolBar()
     * @see JXRootPane#setToolBar(JToolBar)
     */
    public void setToolBar(JToolBar toolBar) {
        getRootPane().setToolBar(toolBar);
    }
    
    /**
     * Returns the value of the default button property from the underlying
     * {@code JRootPane}.
     * 
     * @return the {@code JButton} which is the default button
     * @see #setDefaultButton(JButton)
     * @see JRootPane#getDefaultButton()
     */
    public JButton getDefaultButton() {
        return getRootPane().getDefaultButton();
    }
    
    /**
     * Sets the default button property on the underlying {@code JRootPane}.
     * 
     * @param button
     *            the {@code JButton} which is to be the default button
     * @see #getDefaultButton()
     * @see JRootPane#setDefaultButton(JButton)
     */
    public void setDefaultButton(JButton button) {
        getRootPane().setDefaultButton(button);
    }
    
    /**
     * Returns the value of the cancel button property from the underlying
     * {@code JXRootPane}.
     * 
     * @return the {@code JButton} which is the cancel button
     * @see #setCancelButton()
     * @see JXRootPane#getCancelButton()
     */
    public JButton getCancelButton() {
        return getRootPane().getCancelButton();
    }

    /**
     * Sets the cancel button property on the underlying {@code JXRootPane}.
     * 
     * @param button
     *            the {@code JButton} which is to be the cancel button
     * @see #getCancelButton()
     * @see JXRootPane#setCancelButton(JButton)
     */
    public void setCancelButton(JButton button) {
        getRootPane().setCancelButton(button);
    }
}
