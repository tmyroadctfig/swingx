/*
 * Created on 22.07.2005
 *
 */
package org.jdesktop.swingx;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.table.DefaultTableModel;

/**
 * @author  Jeanette Winzenburg
 */
public class JXRootPaneTest extends InteractiveTestCase {
    
    /**
     * Issue #66-swingx: setStatusBar(null) throws NPE.
     *
     */
    public void testSetStatusBar() {
        JXRootPane rootPane = new JXRootPane();
        rootPane.setStatusBar(null);
    }
    
    public void interactiveTestStatusBar() {
        JXTable table = new JXTable(new DefaultTableModel(10, 3));
        final JXFrame frame = wrapWithScrollingInFrame(table, "Statusbar");
        Action action = new AbstractAction("toggle StatusBar") {

            public void actionPerformed(ActionEvent e) {
                JXStatusBar bar = frame.getRootPaneExt().getStatusBar();
                frame.getRootPaneExt().setStatusBar(bar != null ? null : new JXStatusBar());
                frame.getRootPaneExt().revalidate();
            }
            
        };
        addAction(frame, action);
        frame.setVisible(true);
    }
    
    public static void main(String args[]) {
        setSystemLF(true);
        JXRootPaneTest test = new JXRootPaneTest();
        try {
          test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*ColumnControlColumnModel.*");
//            test.runInteractiveTests("interactive.*TableHeader.*");
       //     test.runInteractiveTests("interactive.*Sort.*");
//            test.runInteractiveTests("interactive.*ColumnControlAndF.*");
//            test.runInteractiveTests("interactive.*RowHeight.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

}
