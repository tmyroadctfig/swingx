/*
 * Created on 26.08.2005
 *
 */
package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class SearchFactory {

    private static SearchFactory searchFactory;

    protected JXDialog findDialog;
    protected JXFindPanel findPanel;
    
    public static SearchFactory getInstance() {
          if (searchFactory == null) {
              searchFactory = new SearchFactory();
          }
          return searchFactory;
      }
    
    public static void setInstance(SearchFactory factory) {
        searchFactory = factory;
    }
    
    public void showFindInput(Searchable searchable) {
        if (findPanel == null) {
            findPanel = new JXFindPanel();
            Frame frame = JOptionPane.getRootFrame();
//            if (component != null) {
//               Window window = SwingUtilities.getWindowAncestor(component); 
//               if (window instanceof Frame) {
//                   frame = (Frame) window;
//               }
//            }
            findDialog = new JXDialog(frame, findPanel, false);
            findDialog.pack();
            findDialog.setLocationRelativeTo(null);
        }
        findPanel.setSearchable(searchable);
//        findPanel.setIncrementalSearch(true);
        findDialog.setVisible(true);
//        showFindInput(searchable, 
//                (searchable instanceof Component) ? (Component) searchable : null);
    }
    
//    public void showFindInput(Searchable searchable, Component component) {
//        if (findPanel == null) {
//            JXFindPanel findPanel = new JXFindPanel();
//            Frame frame = JOptionPane.getRootFrame();
////            if (component != null) {
////               Window window = SwingUtilities.getWindowAncestor(component); 
////               if (window instanceof Frame) {
////                   frame = (Frame) window;
////               }
////            }
//            findDialog = new JXDialog(frame, findPanel, false);
//        }
//        findPanel.setSearchable(searchable);
//        
//    }
}
