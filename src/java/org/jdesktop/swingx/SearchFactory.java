/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.Container;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.plaf.LookAndFeelAddons;

/**
 * Factory to create, configure and show application consistent
 * search and find widgets.
 * 
 * Typically a shared JXFindBar is used for incremental search, while
 * a shared JXFindPanel is used for batch search. This implementation 
 * 
 * <ul>
 *  <li> JXFindBar - adds and shows it in the target's toplevel container's
 *    toolbar (assuming a JXRootPane)
 *  <li> JXFindPanel - creates a JXDialog, adds and shows the findPanel in the
 *    Dialog 
 * </ul>
 * 
 * 
 * PENDING: update (?) views/wiring on focus change.
 * PENDING: add methods to return JXSearchPanels (for use by PatternMatchers).
 * 
 * @author Jeanette Winzenburg
 */
public class SearchFactory {
    // PENDING: rename methods to batch/incremental instead of dialog/toolbar

    static {
        // Hack to enforce loading of SwingX framework ResourceBundle
        LookAndFeelAddons.getAddon();
    }

    private static SearchFactory searchFactory;

//    /** the shared dialog to show input. */
    // JW: can't share the Dialog - not possible to reset the owner!
//    protected JXDialog findDialog;
    
    /** the shared find widget for batch-find. */
    protected JXFindPanel findPanel;
   
    /** the shared find widget for incremental-find. */
    protected JXFindBar findBar;

    private boolean useFindBar;
    
    /** 
     * returns the shared SearchFactory.
     * 
     * @return
     */
    public static SearchFactory getInstance() {
          if (searchFactory == null) {
              searchFactory = new SearchFactory();
          }
          return searchFactory;
      }
    
    /**
     * sets the shared SearchFactory.
     * 
     * @param factory
     */
    public static void setInstance(SearchFactory factory) {
        searchFactory = factory;
    }
    
    /**
     * Shows an appropriate find widget targeted at the searchable.
     * This implementation opens a batch-find or incremental-find 
     * widget based on the showFindInToolBar property (which defaults
     * to false).
     *  
     *  
     * @param target - the component associated with the searchable
     * @param searchable - the object to search.
     */
    public void showFindInput(JComponent target, Searchable searchable) {
        if (isUseFindBar(target, searchable)) {
            showFindBar(target, searchable);
        } else {
            showFindDialog(target, searchable);
        }
    }

    /**
     * Show a incremental-find widget targeted at the searchable.
     * 
     * This implementation uses a JXFindBar and inserts it into the
     * target's toplevel container toolbar. 
     * 
     * PENDING: Nothing shown if there is no toolbar found. 
     * 
     * @param target - the component associated with the searchable
     * @param searchable - the object to search.
     */
    public void showFindBar(JComponent target, Searchable searchable) {
        if (target == null) return;
        removeFromParent(getSharedFindBar());
        Window topLevel = SwingUtilities.getWindowAncestor(target);
        if (topLevel instanceof JXFrame) {
            JXRootPane rootPane = ((JXFrame) topLevel).getRootPaneExt();
            JToolBar toolBar = rootPane.getToolBar();
            if (toolBar == null) {
                toolBar = new JToolBar();
                rootPane.setToolBar(toolBar);
            }
            toolBar.add(getSharedFindBar(), 0);
            rootPane.revalidate();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(getSharedFindBar());
            
        }
        getSharedFindBar().setSearchable(searchable);
    }

    /**
     * convenience method to remove a component from its parent
     * and revalidate the parent
     */
    protected void removeFromParent(JComponent component) {
        Container oldParent = component.getParent();
        if (oldParent != null) {
            oldParent.remove(component);
            if (oldParent instanceof JComponent) {
                ((JComponent) oldParent).revalidate();
            } else {
                // not sure... never have non-j comps
                oldParent.invalidate();
                oldParent.validate();
            }
        }
    }

    /**
     * returns the shared JXFindBar. Creates and configures on 
     * first call.
     * @return
     */
    public JXFindBar getSharedFindBar() {
        if (findBar == null) {
            findBar = createFindBar();
            configureSharedFindBar();
        }
        return findBar;
    }
    
    /**
     * called after creation of shared FindBar.
     * Subclasses can add configuration code. 
     * Here: registers a custom action to remove the 
     * findbar from its ancestor container.
     * 
     * PRE: findBar != null.
     *
     */
    protected void configureSharedFindBar() {
        Action removeAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                removeFromParent(findBar);
                
            }
            
        };
        findBar.getActionMap().put(JXDialog.CLOSE_ACTION_COMMAND, removeAction);
    }

    /**
     * Factory method to create a JXFindBar.
     * 
     * @return
     */
    public JXFindBar createFindBar() {
        return new JXFindBar();
    }


    /**
     * returns the shared JXFindPanel. Creates and configures on 
     * first call.
     * @return
     */
    public JXFindPanel getSharedFindPanel() {
        if (findPanel == null) {
            findPanel = createFindPanel();
            configureSharedFindPanel();
        }
        return findPanel;
    }
    
    /**
     * called after creation of shared FindPanel.
     * Subclasses can add configuration code. 
     * Here: no-op
     * PRE: findPanel != null.
     *
     */
    protected void configureSharedFindPanel() {
    }

    /**
     * Factory method to create a JXFindPanel.
     * 
     * @return
     */
    public JXFindPanel createFindPanel() {
        return new JXFindPanel();
    }


    /**
     * Show a batch-find widget targeted at the given Searchable.
     * 
     * This implementation uses a shared JXFindPanel contained 
     * JXDialog.
     * 
     * @param target -
     *            the component associated with the searchable
     * @param searchable -
     *            the object to search.
     */
    public void showFindDialog(JComponent target, Searchable searchable) {
        Frame frame = JOptionPane.getRootFrame();
        if (target != null) {
            Window window = SwingUtilities.getWindowAncestor(target);
            if (window instanceof Frame) {
                frame = (Frame) window;
            }
        }
        JXDialog topLevel = getDialogForSharedFilePanel();
        JXDialog findDialog;
        if ((topLevel != null) && (topLevel.getOwner().equals(frame))) {
            findDialog = topLevel;
            KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(findDialog);
        } else {
            Point location = hideSharedFilePanel();
            findDialog = new JXDialog(frame, getSharedFindPanel(), false);
            findDialog.setAlwaysOnTop(true);
            findDialog.pack();
            if (location == null) {
                findDialog.setLocationRelativeTo(frame);
            } else {
                findDialog.setLocation(location);
            }
            findDialog.setVisible(true);
        }    
        getSharedFindPanel().setSearchable(searchable);
    }

    
    private JXDialog getDialogForSharedFilePanel() {
        if (findPanel == null) return null;
        Window window = SwingUtilities.getWindowAncestor(findPanel);
        return (window instanceof JXDialog) ? (JXDialog) window : null;
    }

    protected Point hideSharedFilePanel() {
        if (findPanel == null) return null;
        Window window = SwingUtilities.getWindowAncestor(findPanel);
        if (window != null) {
            Point location = window.getLocationOnScreen();
            findPanel.getParent().remove(findPanel);
            window.dispose();
            return location;
            
        }
        return null;
    }

    /**
     * Returns decision about using a batch- vs. incremental-find for the
     * searchable. This implementation returns the useFindBar property
     * directly.
     * 
     * @param target -
     *            the component associated with the searchable
     * @param searchable -
     *            the object to search.
     * @return true if a incremental-find should be used, false otherwise.
     */
    public boolean isUseFindBar(JComponent target, Searchable searchable) {
        return useFindBar;
    }
 
    /**
     * 
     * @param inToolBar
     */
    public void setUseFindBar(boolean inToolBar) {
        this.useFindBar = inToolBar;
    }
}
