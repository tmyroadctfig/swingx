/*
 * Created on 26.08.2005
 *
 */
package org.jdesktop.swingx;

import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.plaf.LookAndFeelAddons;

public class SearchFactory {

    static {
        // Hack to enforce loading of SwingX framework ResourceBundle
        LookAndFeelAddons.getAddon();
    }

    private static SearchFactory searchFactory;

    protected JXDialog findDialog;
    protected JXFindPanel findPanel;
   
    protected JXFindBar findBar;

    private boolean inDialog;
    
    public static SearchFactory getInstance() {
          if (searchFactory == null) {
              searchFactory = new SearchFactory();
          }
          return searchFactory;
      }
    
    public static void setInstance(SearchFactory factory) {
        searchFactory = factory;
    }
    
    public void showFindInput(JComponent target, Searchable searchable) {
        if (showInDialog(target, searchable)) {
            showFindDialog(target, searchable);
            
        } else {
            showFindBar(target, searchable);
        }
    }

    public void showFindBar(JComponent target, Searchable searchable) {
        if (target == null) return;
        if (findBar == null) {
            findBar = new JXFindBar();
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
     * @param target
     * @param searchable
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

    public boolean showInDialog(JComponent target, Searchable searchable) {
        return false;
    }
 
    public void setShowInDialog(boolean inDialog) {
        this.inDialog = inDialog;
    }
}
