/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;

import org.jdesktop.swingx.util.PropertyChangeReport;

/**
 * @author Jeanette Winzenburg
 */
public class JXTitledPanelTest extends InteractiveTestCase {

    public JXTitledPanelTest() {
        super("JXTitledPane interactive test");
    }

    /**
     * SwingX Issue #9: missing notification on title change.
     *
     */
    public void testTitleNotify() {
        String title = "starting title";
        final JXTitledPanel panel = new JXTitledPanel(title);
        PropertyChangeReport report = new PropertyChangeReport();
        panel.addPropertyChangeListener(report);
        panel.setTitle("new title");
        assertTrue("panel must have fired propertyChange", report.hasEvents());
        
    }
    public void interactiveTitleTest() {
        String title = "starting title";
        final JXTitledPanel panel = new JXTitledPanel(title);
        Action action = new AbstractAction("toggle title") {
            int count = 0;
            public void actionPerformed(ActionEvent e) {
                panel.setTitle(" * " + count++ + " title");
                
            }
            
        };

        panel.add(new JButton(action));
        JFrame frame = wrapInFrame(panel, "toggle Title");
        frame.setVisible(true);
    }
    
    public static void main(String args[]) {
        JXTitledPanelTest test = new JXTitledPanelTest();
        try {
          test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }
}
