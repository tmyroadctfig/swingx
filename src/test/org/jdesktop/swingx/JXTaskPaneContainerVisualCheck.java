/*
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * California 95054, U.S.A. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * Simple tests to ensure that the {@code JXTaskPane} can be instantiated and
 * displayed.
 * 
 * @author rah003
 */
public class JXTaskPaneContainerVisualCheck extends InteractiveTestCase {
    public JXTaskPaneContainerVisualCheck() {
        super("JXLoginPane Test");
    }

    public static void main(String[] args) throws Exception {
        // setSystemLF(true);
        JXTaskPaneContainerVisualCheck test = new JXTaskPaneContainerVisualCheck();
        
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
    
    
    /**
     * Ensure that removing a task pane properly repaints the container.
     * <p>
     * SwingX issue #719
     */
    public void interactiveRemovalTest() {
        final JXTaskPaneContainer container = new JXTaskPaneContainer();
        final JXTaskPane pane1 = new JXTaskPane();
        pane1.setTitle("First");
        container.add(pane1);
        
        final JXTaskPane pane2 = new JXTaskPane();
        pane2.setTitle("Second");
        container.add(pane2);
        
        JXFrame frame = wrapInFrame(container, "removal test");
        frame.add(new JButton(new AbstractAction("Remove Second") {
            public void actionPerformed(ActionEvent e) {
                container.remove(pane2);
            }
        }), BorderLayout.SOUTH);
        
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Ensure correct painting when in JSplitPane.
     * <p>
     * SwingX issue #434
     */
    public void interactiveSplitPaneTest() {
        final JXTaskPaneContainer container = new JXTaskPaneContainer();
        final JXTaskPane pane1 = new JXTaskPane();
        pane1.setTitle("First");
        container.add(pane1);
        
        final JXTaskPane pane2 = new JXTaskPane();
        pane2.setTitle("Second");
        container.add(pane2);
        
        final JXTaskPane pane3 = new JXTaskPane();
        pane3.setTitle("Third");
        container.add(pane3);
        
        JSplitPane splitter = new JSplitPane();
        splitter.setLeftComponent(container);
        splitter.setContinuousLayout(true);
        
        JXFrame frame = wrapInFrame(splitter, "split pane test");
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    protected void createAndAddMenus(JMenuBar menuBar, final JComponent component) {
        super.createAndAddMenus(menuBar, component);
        JMenu menu = new JMenu("Locales");
        menu.add(new AbstractAction("Change Locale") {

            public void actionPerformed(ActionEvent e) {
                if (component.getLocale() == Locale.FRANCE) {
                    component.setLocale(Locale.ENGLISH);
                } else {
                    component.setLocale(Locale.FRANCE);
                }
            }});
        menuBar.add(menu);
    }

    /**
     * Do nothing, make the test runner happy
     * (would output a warning without a test fixture).
     *
     */
    public void testDummy() {
        
    }

}
