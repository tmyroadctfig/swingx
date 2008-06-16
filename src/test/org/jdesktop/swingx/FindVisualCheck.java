/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.FindTest.TestListModel;
import org.jdesktop.swingx.FindTest.TestTableModel;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.search.SearchFactory;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.test.AncientSwingTeam;

public class FindVisualCheck extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(FindVisualCheck.class
            .getName());
    public static void main(String args[]) {
      setSystemLF(true);
//      Locale.setDefault(new Locale("es"));
      FindVisualCheck test = new FindVisualCheck();
      try {
//        test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*Compare.*");
          test.runInteractiveTests("interactive.*Close.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }
    @Override
    protected void setUp() {
        editorURL = FindVisualCheck.class.getResource("resources/test.txt");
    }
    

    private URL editorURL;

    public FindVisualCheck() {
        super("Find Action Test");
    }

   /**
    * Requirement: hook into close. 
    * 
    * Solution: put the custom action in the panel's action map with the 
    * key JXDialog.CLOSE_ACTION_COMMAND.
    * 
    * Nasty problem is that the custom action overwrites the default, 
    * so the dialog is not closed. Nasty solution is to go up the until the
    * JXDialog is found and manually invoke its close.
    */
    public void interactiveCustomClose() {
        final SearchFactory custom = new SearchFactory() {

            @Override
            public JXFindPanel createFindPanel() {
                final JXFindPanel panel = super.createFindPanel();
                Action customClose = new AbstractAction() {

                    public void actionPerformed(ActionEvent e) {
                        LOG.info("my action");
                        // this is nasty ... 
                        Window window = SwingUtilities.getWindowAncestor(panel);
                        if (window instanceof JXDialog) {
                            ((JXDialog) window).doClose();
                        }
                    }
                    
                };
                panel.getActionMap().put(JXDialog.CLOSE_ACTION_COMMAND, customClose);
                return panel;
            }
            
        };
        final JXTable table = new JXTable(new AncientSwingTeam());
        Action customFind = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                custom.showFindDialog(table, table.getSearchable());
                
            }
            
        };
        table.getActionMap().put("find", customFind);
        showWithScrollingInFrame(table, "augment close action of dialog");
        
    }
    /**
     * Issue #720, 692-swingx: findDialog on tree selection as match-marker lost
     * 
     * Scenario (#692): open find via button
     * - press button to open find
     * - run a search with match, selects a node
     * - close findDialog, clears selection
     * 
     * Scenario (#702): open find via ctrl-f
     * - focus tree
     * - ctrl-f to open findDialog
     * - run a search with match, selects a node
     * - close findDialog
     * - tab to button, clears selection
     */
    public void interactiveFindDialogSelectionTree() {
        final JXTree table = new JXTree();
        JComponent comp = Box.createVerticalBox();
        comp.add(new JScrollPane(table));
        Action action = new AbstractActionExt("open find dialog") {

            public void actionPerformed(ActionEvent e) {
                SearchFactory.getInstance().showFindDialog(table, table.getSearchable());
                
            }
            
        };
        comp.add(new JButton(action));
        JXFrame frame = wrapInFrame(comp, "Tree FindDialog: selection lost");
        frame.setVisible(true);
    }
    
    /**
     * Issue #718-swingx: finddialog not updated on LF switch.
     * 
     * Hmm .. shouldn't a lf-switcher update all windows? Like the
     * setPlafAction in InteractiveTestCase does (since today <g>).
     * 
     * Yeah, but the dialog had been disposed and the findPanel unparented 
     * if focus is moved somewhere "outside" of the target. Needed to add something
     * focusable to reproduce here: 
     * - open find in table
     * - click button
     * - toggle LF
     * - open find in table: the panel is not changed to new LF
     */
    public void interactiveFindDialogUpdateLF() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.setColumnControlVisible(true);
        JComponent comp = Box.createVerticalBox();
        comp.add(new JScrollPane(table));
        comp.add(new JButton("something to focus"));
        JXFrame frame = wrapInFrame(comp, "FindDialog on toggleLF", true);
        frame.setVisible(true);
    }
    
    
    public void interactiveShowTree() {
        JXTree tree = new JXTree(new FileSystemModel());
        showComponent(tree, "Search in XTree");
    }
    public void interactiveShowList() {
        showComponent(new JXList(new TestListModel()), "Search in XList");
    }
    
    public void interactiveShowTable() {
        showComponent(new JXTable(new TestTableModel()), "Search in XTable");
    }



    public void interactiveCompareFindStrategy() {
        final JXTable first = new JXTable(new TestTableModel());
        first.setColumnControlVisible(true);
        final JXTreeTable second = new JXTreeTable(new FileSystemModel());
        JXFrame frame = wrapWithScrollingInFrame(first, second, "Batch/Incremental Search");
        addSearchModeToggle(frame);
        addMessage(frame, "Press ctrl-F to open search widget");
        frame.setVisible(true);
    }

    public void interactiveShowSplitPane() {
       showComponent(createEditor(), new JXTable(new TestTableModel()), "Targetable Search");
    }
    
    public void interactiveShowEditor() {
        showComponent(createEditor(), "Search in XEditorPane");
    }

    /**
     * @return
     * @throws IOException
     */
    private JXEditorPane createEditor()  {
        try {
            return new JXEditorPane(editorURL);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    public void showComponent(JComponent component, JComponent second, String title) {
        
        JXFrame frame;
        if (second != null) {
          frame = wrapWithScrollingInFrame(component, second, title);
        } else {
            frame= wrapWithScrollingInFrame(component, title);
        }
        
        addMessage(frame, "Press ctrl-F to open search widget");
        frame.setSize(600, 400);
        frame.setVisible(true);
        
    }
    public void showComponent(JComponent component, String title) {
        showComponent(component, null, title);
    }

    
    

    /**
     * Do nothing, keep testRunner happy.
     */
    public void testDummy() {
        
    }
}
