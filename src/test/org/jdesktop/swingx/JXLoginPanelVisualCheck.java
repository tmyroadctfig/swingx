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

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import org.jdesktop.swingx.JXLoginPanel.SaveMode;

/**
 * Simple tests to ensure that the {@code JXDatePicker} can be instantiated and
 * displayed.
 * 
 * @author Karl Schaefer
 */
public class JXLoginPanelVisualCheck extends InteractiveTestCase {
    public JXLoginPanelVisualCheck() {
        super("JXLoginPanel Test");
    }

    public static void main(String[] args) throws Exception {
        // setSystemLF(true);
        JXLoginPanelVisualCheck test = new JXLoginPanelVisualCheck();
        
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
    
    private static class SetPlafAction extends AbstractAction {
        private String plaf;
        
        public SetPlafAction(String name, String plaf) {
            super(name);
            this.plaf = plaf;
        }
        
        /**
         * {@inheritDoc}
         */
        public void actionPerformed(ActionEvent e) {
            try {
                Component c = (Component) e.getSource();
                Window w = null;
                
                for (Container p = c.getParent(); p != null; p = p instanceof JPopupMenu ? (Container) ((JPopupMenu) p)
                        .getInvoker() : p.getParent()) {
                    if (p instanceof Window) {
                        w = (Window) p;
                    }
                }
                
                UIManager.setLookAndFeel(plaf);
                SwingUtilities.updateComponentTreeUI(w);
                w.pack();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (UnsupportedLookAndFeelException e1) {
                e1.printStackTrace();
            }
        }
    }
    
    private JMenuBar createMenuBar(final JComponent component) {
        LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("Set L&F");
        
        for (LookAndFeelInfo info : plafs) {
            menu.add(new SetPlafAction(info.getName(), info.getClassName()));
        }
        menu.add(new AbstractAction("Change Locale") {

            public void actionPerformed(ActionEvent e) {
                if (component.getLocale() == Locale.FRANCE) {
                    component.setLocale(Locale.ENGLISH);
                } else {
                    component.setLocale(Locale.FRANCE);
                }
            }});
        bar.add(menu);
        
        return bar;
    }
    
    public JXFrame wrapInFrame(JComponent component, String title) {
        JXFrame frame = super.wrapInFrame(component, title);
        frame.setJMenuBar(createMenuBar(component));
        
        return frame;
    }
    
    /**
     * Issue #538-swingx Failure to set locale at runtime
     *
     */
    public void interactiveDisplay() {
        sun.awt.AppContext.getAppContext().put("JComponent.defaultLocale", Locale.FRANCE);
        JXLoginPanel panel = new JXLoginPanel();
        JFrame frame = JXLoginPanel.showLoginFrame(panel);
        frame.setJMenuBar(createMenuBar(panel));

        panel.setSaveMode(SaveMode.BOTH);
        
//        JFrame frame = wrapInFrame(panel, "show login panel");
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Do nothing, make the test runner happy
     * (would output a warning without a test fixture).
     *
     */
    public void testDummy() {
        
    }

}
