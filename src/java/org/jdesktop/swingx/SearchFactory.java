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
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.plaf.LookAndFeelAddons;

/**
 * Factory to create, configure and show application consistent
 * search and find widgets.
 * 
 * PENDING: add methods to return JXSearchPanels?
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

    /** the shared dialog to show input. */
    protected JXDialog findDialog;
    
    /** the shared find widget for batch-find. */
    protected JXFindPanel findPanel;
   
    /** the shared find widget for incremental-find. */
    protected JXFindBar findBar;

    private boolean findInToolBar;
    
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
        if (showFindInToolBar(target, searchable)) {
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
        if (findBar == null) {
            findBar = new JXFindBar();
        }
        Container oldParent = findBar.getParent();
        if (oldParent != null) {
            oldParent.remove(findBar);
            if (oldParent instanceof JComponent) {
                ((JComponent) oldParent).revalidate();
            }
        }
        Window topLevel = SwingUtilities.getWindowAncestor(target);
        if (topLevel instanceof JXFrame) {
            JXRootPane rootPane = ((JXFrame) topLevel).getRootPaneExt();
            JToolBar toolBar = rootPane.getToolBar();
            if (toolBar == null) {
                toolBar = new JToolBar();
                rootPane.setToolBar(toolBar);
            }
            toolBar.add(findBar, 0);
            rootPane.revalidate();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(findBar);
            
        }
        findBar.setSearchable(searchable);
    }

    /**
     * Show a batch-find widget targeted at the given Searchable.
     * 
     * This implementation uses a shared JXFindPanel contained in a shared
     * JXDialog.
     * 
     * @param target - the component associated with the searchable
     * @param searchable - the object to search.
     */
    public void showFindDialog(JComponent target, Searchable searchable) {
        if (findPanel == null) {
            findPanel = new JXFindPanel();
            Frame frame = JOptionPane.getRootFrame();
            if (target != null) {
               Window window = SwingUtilities.getWindowAncestor(target); 
               if (window instanceof Frame) {
                   frame = (Frame) window;
               }
            }
            findDialog = new JXDialog(frame, findPanel, false);
            findDialog.pack();
            findDialog.setLocationRelativeTo(null);
        }
        findPanel.setSearchable(searchable);
        findDialog.setVisible(true);
    }

    
    /**
     * Returns decision about using a batch- vs. incremental-find 
     * for the searchable. This implementation returns the 
     * showFindInToolBar property directly. 
     * 
     * @param target - the component associated with the searchable
     * @param searchable - the object to search.
     * @return true if a incremental-find should be used, false otherwise.
     */
    public boolean showFindInToolBar(JComponent target, Searchable searchable) {
        return findInToolBar;
    }
 
    /**
     * 
     * @param inToolBar
     */
    public void setShowFindInToolBar(boolean inToolBar) {
        this.findInToolBar = inToolBar;
    }
}
