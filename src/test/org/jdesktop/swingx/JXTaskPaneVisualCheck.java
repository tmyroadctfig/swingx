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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;

/**
 * Simple tests to ensure that the {@code JXTaskPane} can be instantiated and
 * displayed.
 * 
 * @author rah003
 */
public class JXTaskPaneVisualCheck extends InteractiveTestCase {
    public JXTaskPaneVisualCheck() {
        super("JXLoginPane Test");
    }

    public static void main(String[] args) throws Exception {
        // setSystemLF(true);
        JXTaskPaneVisualCheck test = new JXTaskPaneVisualCheck();
        
        try {
//            test.runInteractiveTests();
//            test.runInteractiveTests("interactiveDisplay");
            test.runInteractiveTests("interactiveMnemonic");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
    
    public void interactiveMnemonic() {
        JXTaskPane pane = new JXTaskPane();
        pane.setTitle("Use Me");
        pane.setMnemonic(KeyEvent.VK_U);
        pane.setForeground(Color.RED);
        pane.setBackground(Color.YELLOW);
        pane.add(new JLabel("another"));
        pane.add(new JButton("wow!!"));
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        container.add(pane);
        showInFrame(container, "Mnemonic Test", true);
    }
    
    public void interactiveColors() {
        JXTaskPane pane = new JXTaskPane();
        pane.setTitle("just something....");
        pane.setForeground(Color.RED);
        pane.setBackground(Color.YELLOW);
        pane.add(new JLabel("another"));
        pane.add(new JButton("wow!!"));
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        container.add(pane);
        showInFrame(container, "background", true);
    }
    
    /**
     * Issue #249-swingx JXTaskPane looks ugly under different default LaFs
     *
     */
    public void interactiveDisplay() {
        sun.awt.AppContext.getAppContext().put("JComponent.defaultLocale", Locale.FRANCE);
        JXTaskPane panel = new JXTaskPane();
        panel.setTitle("Hi there");
        panel.setForeground(Color.RED);
        panel.setFont(new Font("tahoma",Font.BOLD, 72));
        panel.add(new JLabel("Hi there again"));
        showInFrame(panel, "JXTaskPane interactive", true);
    }

    /**
     * Ensure that resizing scroll pane's properly enables/disables scrollbars.
     * <p>
     * SwingX issue #740
     */
    public void interactiveJScrollPaneTest() {
        JXTaskPane panel = new JXTaskPane();
        JXTable table = new JXTable(10, 15);
        table.setHorizontalScrollEnabled(true);
        panel.add(new JScrollPane(table));
        showInFrame(panel, "Ensure scrolling works");
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
