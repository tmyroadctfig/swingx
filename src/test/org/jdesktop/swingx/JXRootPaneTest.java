/*
 * Created on 22.07.2005
 *
 */
package org.jdesktop.swingx;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.test.PropertyChangeReport;

/**
 * @author  Jeanette Winzenburg
 */
public class JXRootPaneTest extends InteractiveTestCase {
 
    /**
     * Test setStatusBar analogous to setToolBar 
     * (triggered by 
     * Issue #499-swingx: old toolbar not removed on setting new).
     * 
     * had not been broken. 
     */
    public void testStatusBarSet() {
        JXRootPane rootPane = new JXRootPane();
        JXStatusBar toolBar = new JXStatusBar();
        rootPane.setStatusBar(toolBar);
        assertTrue(SwingUtilities.isDescendingFrom(toolBar, rootPane));
        rootPane.setStatusBar(new JXStatusBar());
        assertFalse(SwingUtilities.isDescendingFrom(toolBar, rootPane));
    }
    
    /**
     * Test setStatusBar analogous to setToolBar 
     * (triggered by 
     * Issue #499-swingx: old toolbar not removed on setting new).
     *
     * Additional fix: rootPane must fire property change event on
     * setStatusBar. 
     * 
     */
    public void testStatusBarFirePropertyChange() {
        JXRootPane rootPane = new JXRootPane();
        JXStatusBar toolBar = new JXStatusBar();
        rootPane.setStatusBar(toolBar);
        assertTrue(SwingUtilities.isDescendingFrom(toolBar, rootPane));
        PropertyChangeReport report = new PropertyChangeReport();
        rootPane.addPropertyChangeListener(report);
        rootPane.setStatusBar(new JXStatusBar());
        assertEquals("set statusBar must have fire exactly one property change", 1, report.getEventCount());
        assertTrue(report.hasEvents("statusBar"));
    }

    /**
     * Issue #499-swingx: old toolbar not removed on setting new.
     *
     */
    public void testToolBarSet() {
        JXRootPane rootPane = new JXRootPane();
        JToolBar toolBar = new JToolBar();
        rootPane.setToolBar(toolBar);
        assertTrue(SwingUtilities.isDescendingFrom(toolBar, rootPane));
        rootPane.setToolBar(new JToolBar());
        assertFalse(SwingUtilities.isDescendingFrom(toolBar, rootPane));
    }
    
    /**
     * Issue #499-swingx: old toolbar not removed on setting new.
     *
     * Additional fix: rootPane must fire property change event on
     * setToolBar. 
     * 
     * PENDING: similar issue with statusbar?
     */
    public void testToolBarFirePropertyChange() {
        JXRootPane rootPane = new JXRootPane();
        JToolBar toolBar = new JToolBar();
        rootPane.setToolBar(toolBar);
        assertTrue(SwingUtilities.isDescendingFrom(toolBar, rootPane));
        PropertyChangeReport report = new PropertyChangeReport();
        rootPane.addPropertyChangeListener(report);
        rootPane.setToolBar(new JToolBar());
        assertEquals(1, report.getEventCount());
        assertTrue(report.hasEvents("toolBar"));
    }
    /**
     * Issue #66-swingx: setStatusBar(null) throws NPE.
     *
     */
    public void testStatusBarNPE() {
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
