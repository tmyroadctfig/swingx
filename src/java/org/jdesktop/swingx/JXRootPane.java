/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JToolBar;

import org.jdesktop.swingx.event.MessageSource;
import org.jdesktop.swingx.event.ProgressSource;


/**
 * Extends the JRootPane by supporting specific placements for a toolbar and
 * a status bar. If a status bar exists, then toolbars, menus and
 * any MessageSource components will be registered with the status bar.
 * <p>
 * Components should be added using the <code>addComponent</code> method.
 * This method will walk the containment hierarchy of the added component
 * and will register all <code>MessageSource</code> or
 * <code>ProgressSource</code> components.
 *
 * @see JXStatusBar
 * @see org.jdesktop.swing.event.MessageEvent
 * @see org.jdesktop.swing.event.MessageSource
 * @see org.jdesktop.swing.event.ProgressSource
 * @author Mark Davidson
 */
public class JXRootPane extends JRootPane {

    private JXStatusBar statusBar;
    private JToolBar toolBar;

    private JPanel contentPanel;

    public JXRootPane() {
	contentPanel = new JPanel();
	contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

	getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Adds a component to the root pane.
     * If this component and/or it's children is a <code>MessageSource</code>
     * then it will be registered with the status bar.
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
     * Return an array of components that were added to the content panel
     * with addComponent.
     */
    public Component[] getContentComponents() {
	return contentPanel.getComponents();
    }

    private void registerStatusBar(Component comp) {
	if (statusBar == null || comp == null) {
	    return;
	}
	if (comp instanceof MessageSource) {
	    MessageSource source = (MessageSource)comp;
	    source.addMessageListener(statusBar);
	}
	if (comp instanceof ProgressSource) {
	    ProgressSource source = (ProgressSource)comp;
	    source.addProgressListener(statusBar);
	}
	if (comp instanceof Container) {
	    Component[] comps = ((Container)comp).getComponents();
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
	    MessageSource source = (MessageSource)comp;
	    source.removeMessageListener(statusBar);
	}
	if (comp instanceof ProgressSource) {
	    ProgressSource source = (ProgressSource)comp;
	    source.removeProgressListener(statusBar);
	}
	if (comp instanceof Container) {
	    Component[] comps = ((Container)comp).getComponents();
	    for (int i = 0; i < comps.length; i++) {
		unregisterStatusBar(statusBar, comps[i]);
	    }
	}
    }

    /**
     * Set the status bar for this root pane. Any components held by this
     * root pane will be registered. If this is replacing an existing
     * status bar then the existing component will be unregistered from
     * the old status bar.
     *
     * @param statusBar the status bar to use
     */
    public void setStatusBar(JXStatusBar statusBar) {
	JXStatusBar oldStatusBar = this.statusBar;
	this.statusBar = statusBar;

	if (statusBar != null) {
	    if (handler == null) {
		// Create the new mouse handler and register the toolbar
		// and menu components.
		handler = new MouseMessagingHandler(this, statusBar);
		if (toolBar != null) {
		    handler.registerListeners(toolBar.getComponents());
		}
		if (menuBar != null) {
		    handler.registerListeners(menuBar.getSubElements());
		}
	    } else {
		handler.setMessageListener(statusBar);
	    }
	}

	Component[] comps = contentPanel.getComponents();
	for (int i = 0; i < comps.length; i++) {
	    // Unregister the old status bar.
	    unregisterStatusBar(oldStatusBar, comps[i]);

	    // register the new status bar.
	    registerStatusBar(comps[i]);
	}
	getContentPane().add(BorderLayout.SOUTH, statusBar);
    }

    public JXStatusBar getStatusBar() {
	return statusBar;
    }

    private MouseMessagingHandler handler;


    /**
     * Set the toolbar bar for this root pane.
     * If the status bar exists, then all components will be registered
     * with a <code>MouseMessagingHandler</code> so that mouse over
     * messages will be sent to the status bar.
     *
     * @param toolBar the toolbar to register
     * @see MouseMessagingHandler
     */
    public void setToolBar(JToolBar toolBar) {
	JToolBar oldToolBar = this.toolBar;
	this.toolBar = toolBar;

	if (handler != null && oldToolBar != null) {
	    handler.unregisterListeners(oldToolBar.getComponents());
	}

	if (handler != null && toolBar != null) {
	    handler.registerListeners(toolBar.getComponents());
	}

	getContentPane().add(BorderLayout.NORTH, toolBar);
    }

    public JToolBar getToolBar() {
	return toolBar;
    }

    /**
     * Set the menu bar for this root pane.
     * If the status bar exists, then all components will be registered
     * with a <code>MouseMessagingHandler</code> so that mouse over
     * messages will be sent to the status bar.
     *
     * @param menuBar the menu bar to register
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

}
