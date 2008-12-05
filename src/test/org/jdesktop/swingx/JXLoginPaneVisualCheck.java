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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXLoginPane.JXLoginFrame;
import org.jdesktop.swingx.JXLoginPane.SaveMode;
import org.jdesktop.swingx.auth.LoginService;
import org.jdesktop.swingx.auth.SimpleLoginService;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.plaf.basic.BasicLoginPaneUI;

/**
 * Simple tests to ensure that the {@code JXLoginPane} can be instantiated and
 * displayed.
 *
 * @author Karl Schaefer
 */
public class JXLoginPaneVisualCheck extends InteractiveTestCase {
    public JXLoginPaneVisualCheck() {
        super("JXLoginPane Test");
    }

    public static void main(String[] args) throws Exception {
        // setSystemLF(true);
        JXLoginPaneVisualCheck test = new JXLoginPaneVisualCheck();

        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }


    /**
     * Issue #538-swingx Failure to set locale at runtime
     *
     */
    public void interactiveDisplay() {
        JComponent.setDefaultLocale(Locale.FRANCE);
        JXLoginPane panel = new JXLoginPane();
        JFrame frame = JXLoginPane.showLoginFrame(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(createAndFillMenuBar(panel));

        panel.setSaveMode(SaveMode.BOTH);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Issue #538-swingx Failure to set locale at runtime
     *
     */
    public void interactiveDisplayFixedUser() {
        JComponent.setDefaultLocale(Locale.FRANCE);
        JXLoginPane panel = new JXLoginPane();
        JFrame frame = JXLoginPane.showLoginFrame(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(createAndFillMenuBar(panel));

        panel.setSaveMode(SaveMode.BOTH);
        panel.setUserName("aGuy");
        panel.setUserNameEnabled(false);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Issue #538-swingx Failure to set locale at runtime
     *
     */
    public void interactiveSetBackground() {
        JXLoginPane panel = new JXLoginPane();
        panel.setBackgroundPainter(new MattePainter<Object>(Color.RED, true));
        JFrame frame = JXLoginPane.showLoginFrame(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(createAndFillMenuBar(panel));

        panel.setSaveMode(SaveMode.BOTH);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Issue #777-swingx Custom banner not picked up due to double updateUI() call
     *
     */
    public void interactiveCustomBannerDisplay() {
        JXLoginPane panel = new JXLoginPane();
        panel.setUI(new DummyLoginPaneUI(panel));
        JFrame frame = JXLoginPane.showLoginFrame(panel);
        frame.setJMenuBar(createAndFillMenuBar(panel));

        panel.setSaveMode(SaveMode.BOTH);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Issue #636-swingx Unexpected resize on long exception message.
     *
     */
    public void interactiveError() {
        JComponent.setDefaultLocale(Locale.FRANCE);
        final JXLoginPane panel = new JXLoginPane(new LoginService() {

                        public boolean authenticate(String name, char[] password,
                                        String server) throws Exception {
                                if (true) {
                                        throw new Exception("Ex.");
                                }
                                return false;
                        }});
        final JXLoginFrame frame = JXLoginPane.showLoginFrame(panel);
        // if uncomented dialog will disappear immediatelly dou to invocation of login action
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(createAndFillMenuBar(panel));
        panel.setErrorMessage("TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO Unexpected resize on long exception message. Unexpected resize on long exception message.");

        panel.setSaveMode(SaveMode.BOTH);

        frame.pack();
        frame.setVisible(true);
        SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                                evaluateChildren(frame.getContentPane().getComponents());
                        }});

    }

    /**
     * Issue #636-swingx Unexpected resize on long exception message.
     *
     */
    public void interactiveBackground() {
        JComponent.setDefaultLocale(Locale.FRANCE);
        final JXLoginPane panel = new JXLoginPane(new LoginService() {

                        public boolean authenticate(String name, char[] password,
                                        String server) throws Exception {
                                if (true) {
                                        throw new Exception("Ex.");
                                }
                                return false;
                        }});
        final JXLoginFrame frame = JXLoginPane.showLoginFrame(panel);
        // if uncomented dialog will disappear immediatelly dou to invocation of login action
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(createAndFillMenuBar(panel));
        panel.setErrorMessage("TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO Unexpected resize on long exception message. Unexpected resize on long exception message.");

        panel.setSaveMode(SaveMode.BOTH);
        frame.getContentPane().setBackgroundPainter(new MattePainter<Object>(
                new GradientPaint(0, 0, Color.BLUE, 1, 0, Color.YELLOW), true));

        frame.pack();
        frame.setVisible(true);
        SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                                evaluateChildren(frame.getContentPane().getComponents());
                        }});

    }

    /**
     * Progress message test.
     */
    public void interactiveProgress() {
        final JXLoginPane panel = new JXLoginPane();
        final JFrame frame = JXLoginPane.showLoginFrame(panel);
        panel.setLoginService(new LoginService() {

			public boolean authenticate(String name, char[] password,
					String server) throws Exception {
				panel.startLogin();
				Thread.sleep(5000);
				return true;
			}});

        frame.setJMenuBar(createAndFillMenuBar(panel));

        panel.setSaveMode(SaveMode.BOTH);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				evaluateChildren(frame.getContentPane().getComponents());
			}});

    }

    private boolean evaluateChildren(Component[] components) {
        for (Component c: components) {
        	if (c instanceof JButton && "login".equals(((JButton) c).getActionCommand())) {
        		((JButton) c).doClick();

        		return true;
        	} else if (c instanceof Container) {
        		if (evaluateChildren(((Container) c).getComponents()) ){
        			return true;
        		}
        	}
        }
        return false;

	}


    public class DummyLoginPaneUI extends BasicLoginPaneUI {

        public DummyLoginPaneUI(JXLoginPane dlg) {
            super(dlg);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Image getBanner() {
            Image banner = super.getBanner();
            BufferedImage im = GraphicsUtilities.createCompatibleTranslucentImage(banner.getWidth(null), banner.getHeight(null));
            Graphics2D g = im.createGraphics();
            g.setComposite(AlphaComposite.Src);
            g.drawImage(banner, 0, 0, 100, 100, null);
            g.dispose();
            return im;
        }
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
     * swingx-917
     * TODO: this test works only when not run together with the others
     * @throws Exception
     */
    public void interactiveBrokenLayoutAfterFailedLogin() throws Exception {
        sun.awt.AppContext.getAppContext().put("JComponent.defaultLocale", Locale.FRANCE);
        Map<String, char[]> aMap = new HashMap<String, char[]>();
        aMap.put("asdf", "asdf".toCharArray());
        JXLoginPane panel = new JXLoginPane(new SimpleLoginService(aMap));
        panel.setSaveMode(JXLoginPane.SaveMode.BOTH);
        panel.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(
                    PropertyChangeEvent thePropertyChangeEvent) {
                System.err.println(thePropertyChangeEvent.getPropertyName()
                        + " " + thePropertyChangeEvent.getOldValue()
                        + " -> " + thePropertyChangeEvent.getNewValue());
            }
        });
        JFrame frame = JXLoginPane.showLoginFrame(panel);

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
