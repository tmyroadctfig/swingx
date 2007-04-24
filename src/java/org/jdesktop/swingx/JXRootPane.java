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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.jdesktop.swingx.event.MessageSource;
import org.jdesktop.swingx.event.ProgressSource;

/**
 * Extends the JRootPane by supporting specific placements for a toolbar and a
 * status bar. If a status bar exists, then toolbars, menus and any
 * MessageSource components will be registered with the status bar.
 * <p>
 * Components should be added using the <code>addComponent</code> method. This
 * method will walk the containment hierarchy of the added component and will
 * register all <code>MessageSource</code> or <code>ProgressSource</code>
 * components.
 * 
 * @see JXStatusBar
 * @see org.jdesktop.swingx.event.MessageEvent
 * @see org.jdesktop.swingx.event.MessageSource
 * @see org.jdesktop.swingx.event.ProgressSource
 * @author Mark Davidson
 */
public class JXRootPane extends JRootPane {
    private JXStatusBar statusBar;

    private JToolBar toolBar;

    private JPanel contentPanel;

    /** 
     * The button that gets activated when the pane has the focus and
     * a UI-specific action like pressing the <b>ESC</b> key occurs.
     */
    private JButton cancelButton;

    public JXRootPane() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        getContentPane().add(contentPanel, BorderLayout.CENTER);
        
        Action escAction = new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                JButton cancelButton = getCancelButton();
                if (cancelButton != null) {
                    cancelButton.doClick(20);
                }
            }
        };
        getActionMap().put("esc-action", escAction);
        InputMap im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        im.put(key, "esc-action");
    }
    
    /**
     * Adds a component to the root pane. If this component and/or it's children
     * is a <code>MessageSource</code> then it will be registered with the
     * status bar.
     */
    public void addComponent(Component comp) {
        contentPanel.add(comp);
        registerStatusBar(comp);
    }

    /**
     * Removes a component from the center panel.
     */
    public void removeComponent(Component comp) {
        contentPanel.remove(comp);
        unregisterStatusBar(statusBar, comp);
    }

    /**
     * Return an array of components that were added to the content panel with
     * addComponent.
     */
    public Component[] getContentComponents() {
        return contentPanel.getComponents();
    }

    private void registerStatusBar(Component comp) {
        if (statusBar == null || comp == null) {
            return;
        }
        if (comp instanceof MessageSource) {
            MessageSource source = (MessageSource) comp;
//            source.addMessageListener(statusBar);
        }
        if (comp instanceof ProgressSource) {
            ProgressSource source = (ProgressSource) comp;
//            source.addProgressListener(statusBar);
        }
        if (comp instanceof Container) {
            Component[] comps = ((Container) comp).getComponents();
            for (int i = 0; i < comps.length; i++) {
                registerStatusBar(comps[i]);
            }
        }
    }

    private void unregisterStatusBar(JXStatusBar statusBar, Component comp) {
        if (statusBar == null || comp == null) {
            return;
        }
        if (comp instanceof MessageSource) {
            MessageSource source = (MessageSource) comp;
//            source.removeMessageListener(statusBar);
        }
        if (comp instanceof ProgressSource) {
            ProgressSource source = (ProgressSource) comp;
//            source.removeProgressListener(statusBar);
        }
        if (comp instanceof Container) {
            Component[] comps = ((Container) comp).getComponents();
            for (int i = 0; i < comps.length; i++) {
                unregisterStatusBar(statusBar, comps[i]);
            }
        }
    }

    /**
     * Set the status bar for this root pane. Any components held by this root
     * pane will be registered. If this is replacing an existing status bar then
     * the existing component will be unregistered from the old status bar.
     * 
     * @param statusBar
     *            the status bar to use
     */
    public void setStatusBar(JXStatusBar statusBar) {
        JXStatusBar oldStatusBar = this.statusBar;
        this.statusBar = statusBar;

        if (statusBar != null) {
            if (handler == null) {
                // Create the new mouse handler and register the toolbar
                // and menu components.
//                handler = new MouseMessagingHandler(this, statusBar);
                if (toolBar != null) {
//                    handler.registerListeners(toolBar.getComponents());
                }
                if (menuBar != null) {
//                    handler.registerListeners(menuBar.getSubElements());
                }
            } else {
//                handler.setMessageListener(statusBar);
            }
        }

        Component[] comps = contentPanel.getComponents();
        for (int i = 0; i < comps.length; i++) {
            // Unregister the old status bar.
            unregisterStatusBar(oldStatusBar, comps[i]);

            // register the new status bar.
            registerStatusBar(comps[i]);
        }
        if (oldStatusBar != null) {
            getContentPane().remove(oldStatusBar);
        }
        if (statusBar != null) {
            getContentPane().add(BorderLayout.SOUTH, statusBar);
        }
    }

    public JXStatusBar getStatusBar() {
        return statusBar;
    }

    private MouseMessagingHandler handler;

    /**
     * Set the toolbar bar for this root pane. If the status bar exists, then
     * all components will be registered with a
     * <code>MouseMessagingHandler</code> so that mouse over messages will be
     * sent to the status bar. If a tool bar is currently registered with this
     * {@code JXRootPane}, then it is removed prior to setting the new tool
     * bar. If an implementation needs to handle more than one tool bar, a
     * subclass will need to override the singleton logic used here or manually
     * add toolbars with {@code getContentPane().add}.
     * 
     * @param toolBar
     *            the toolbar to register
     * @see MouseMessagingHandler
     */
    public void setToolBar(JToolBar toolBar) {
        JToolBar oldToolBar = getToolBar();
        this.toolBar = toolBar;

        if (oldToolBar != null) {
            getContentPane().remove(oldToolBar);
            
            if (handler != null) {
                handler.unregisterListeners(oldToolBar.getComponents());
            }
        }
        
        if (handler != null && this.toolBar != null) {
            handler.registerListeners(this.toolBar.getComponents());
        }

        getContentPane().add(BorderLayout.NORTH, this.toolBar);
        
        //ensure the new toolbar is correctly sized and displayed
        getContentPane().validate();
        
        firePropertyChange("toolBar", oldToolBar, getToolBar());
    }

    public JToolBar getToolBar() {
        return toolBar;
    }

    /**
     * Set the menu bar for this root pane. If the status bar exists, then all
     * components will be registered with a <code>MouseMessagingHandler</code>
     * so that mouse over messages will be sent to the status bar.
     * 
     * @param menuBar
     *            the menu bar to register
     * @see MouseMessagingHandler
     */
    public void setJMenuBar(JMenuBar menuBar) {
        JMenuBar oldMenuBar = this.menuBar;

        super.setJMenuBar(menuBar);

        if (handler != null && oldMenuBar != null) {
            handler.unregisterListeners(oldMenuBar.getSubElements());
        }

        if (handler != null && menuBar != null) {
            handler.registerListeners(menuBar.getSubElements());
        }
    }

    /**
     * Sets the <code>cancelButton</code> property,
     * which determines the current default cancel button for this <code>JRootPane</code>.
     * The cancel button is the button which will be activated 
     * when a UI-defined activation event (typically the <b>ESC</b> key) 
     * occurs in the root pane regardless of whether or not the button 
     * has keyboard focus (unless there is another component within 
     * the root pane which consumes the activation event,
     * such as a <code>JTextPane</code>).
     * For default activation to work, the button must be an enabled
     * descendent of the root pane when activation occurs.
     * To remove a cancel button from this root pane, set this
     * property to <code>null</code>.
     *
     * @see JButton#getCancelButton 
     * @param cancelButton the <code>JButton</code> which is to be the cancel button
     *
     * @beaninfo
     *  description: The button activated by default for cancel actions in this root pane
     */
    public void setCancelButton(JButton cancelButton) { 
        JButton old = this.cancelButton;

        if (old != cancelButton) {
            this.cancelButton = cancelButton;

            if (old != null) {
                old.repaint();
            }
            if (cancelButton != null) {
                cancelButton.repaint();
            } 
        }

        firePropertyChange("cancelButton", old, cancelButton);        
    }

    /**
     * Returns the value of the <code>cancelButton</code> property. 
     * @return the <code>JButton</code> which is currently the default cancel button
     * @see #setCancelButton
     */
    public JButton getCancelButton() { 
        return cancelButton;
    }

}
