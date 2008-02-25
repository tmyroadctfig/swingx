/*
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * Base class for supporting inclusion of interactive tests into a JUnit test case.
 * Note that the interactive tests are NOT executed by the JUnit framework and
 * are not automated.  They are typically used for visual inspection of features
 * during development. It is convenient to include the interactive tests along with
 * the automated JUnit tests since they may share resources and it keeps tests
 * focused in a single place.
 * <p>
 * All interactive test methods should be prefixed with &quot;interactive&quot;,
 * e.g.  interactiveTestTableSorting().</p>
 * <p>
 * The test class's <code>main</code> method should be used to control which
 * interactive tests should run.  Use <code>runInteractiveTests()</code> method
 * to run all interactive tests in the class.</p>
 * <p>
 * Ultimately we need to investigate moving to a mechanism which can help automate
 * interactive tests.  JFCUnit is being investigated.  In the meantime, this
 * is quick and dirty and cheap.
 * </p>
 * @author Amy Fowler
 * @version 1.0
 */
public abstract class InteractiveTestCase extends junit.framework.TestCase {
    private static final Logger LOG = Logger
            .getLogger(InteractiveTestCase.class.getName());
    protected Point frameLocation = new Point(0,0);
    protected boolean systemLF;
    
    public InteractiveTestCase() {
        super();
        String className = getClass().getName();
        int lastDot = className.lastIndexOf(".");
        String lastElement = className.substring(lastDot + 1);
        setName(lastElement);
    }
    
    public InteractiveTestCase(String testTitle) {
        super(testTitle);
    }

    /**
     * Creates and returns a JXFrame with the specified title, containing
     * the component wrapped into a JScrollPane.
     * 
     * @param component the JComponent to wrap
     * @param title the title to show in the frame
     * @return a configured, packed and located JXFrame.
     */
    public JXFrame wrapWithScrollingInFrame(JComponent component, String title) {
        JScrollPane scroller = new JScrollPane(component);
        return wrapInFrame(scroller, title);
    }

    /**
     * Creates and returns a JXFrame with the specified title, containing
     * two components individually wrapped into a JScrollPane.
     * 
     * @param leftComp the left JComponent to wrap
     * @param rightComp the right JComponent to wrap
     * @param title the title to show in the frame
     * @return a configured, packed and located JXFrame
     */
    public JXFrame wrapWithScrollingInFrame(JComponent leftComp, JComponent rightComp, String title) {
        JComponent comp = Box.createHorizontalBox();
        comp.add(new JScrollPane(leftComp));
        comp.add(new JScrollPane(rightComp));
        JXFrame frame = wrapInFrame(comp, title);
        return frame;
    }

    /**
     * Creates and returns a JXFrame with the specified title, containing
     * the component.
     * 
     * @param component the JComponent to wrap
     * @param title the title to show in the frame
     * @return a configured, packed and located JXFrame.
     */
    public JXFrame wrapInFrame(JComponent component, String title) {
        JXFrame frame = new JXFrame(title, false);
        JToolBar toolbar = new JToolBar();
        frame.getRootPaneExt().setToolBar(toolbar);
        frame.getContentPane().add(BorderLayout.CENTER, component);
        frame.pack();
        frame.setLocation(frameLocation);
        if (frameLocation.x == 0) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setTitle(title+"  [close me and all tests will close]");
        }
        frameLocation.x += 30;
        frameLocation.y += 30;
        return frame;
    }

    /**
     * Creates, shows and returns a JXFrame with the specified title, containing
     * the component wrapped into a JScrollPane.
     * 
     * @param component the JComponent to wrap
     * @param title the title to show in the frame
     * @return a configured, packed and located JXFrame.
     * @see #wrapWithScrollingInFrame(JComponent, String)
     */
    public JXFrame showWithScrollingInFrame(JComponent component, String title) {
        JXFrame frame = wrapWithScrollingInFrame(component, title);
        frame.setVisible(true);
        return frame;
    }

    /**
     * Creates and returns a JXFrame with the specified title, containing
     * two components individually wrapped into a JScrollPane.
     * 
     * @param leftComp the left JComponent to wrap
     * @param rightComp the right JComponent to wrap
     * @param title the title to show in the frame
     * @return a configured, packed and located JXFrame
     */
    public JXFrame showWithScrollingInFrame(JComponent leftComp, JComponent rightComp, String title) {
        JXFrame frame = wrapWithScrollingInFrame(leftComp, rightComp, title);
        frame.setVisible(true);
        return frame;
    }
    /**
     * Creates, shows and returns a JXFrame with the specified title, containing
     * the component.
     * 
     * @param component the JComponent to wrap
     * @param title the title to show in the frame
     * @return a configured, packed and located JXFrame.
     */
    public JXFrame showInFrame(JComponent component, String title) {
        JXFrame frame = wrapInFrame(component, title);
        frame.setVisible(true);
        return frame;
    }

    /**
     * Runs all tests whose method names match the specified regex pattern.
     * @param regexPattern regular expression pattern used to match test method names
     * @throws java.lang.Exception
     */
    public void runInteractiveTests(String regexPattern)  throws java.lang.Exception {
        setUp();
        Class testClass = getClass();
        Method methods[] = testClass.getMethods();

        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().matches(regexPattern)) {
                try {
                    methods[i].invoke(this);
                }
                catch (Exception e) {
                    System.out.println("could not run interactive test: " +
                                       methods[i].getName());
                    e.printStackTrace();
                }
            }
        }
        if (methods.length == 0) {
            System.out.println("no test methods found matching the pattern: "+
                               regexPattern);
        }
        tearDown();
    }

    /**
     * Runs all test methods which are prefixed with &quot;interactive&quot;.
     * @throws java.lang.Exception
     */
    public void runInteractiveTests() throws java.lang.Exception {
        runInteractiveTests("interactive.*");
    }

    public void addAction(JXFrame frame, Action action) {
        JToolBar toolbar = frame.getRootPaneExt().getToolBar();
        if (toolbar != null) {
            AbstractButton button = toolbar.add(action);
            button.setFocusable(false);
        }
    }

    /**
     * Creates and adds a button toggling the frame's componentOrientation.
     * @param frame
     */
    public void addComponentOrientationToggle(final JXFrame frame) {
        Action toggleComponentOrientation = new AbstractAction("toggle orientation") {

            public void actionPerformed(ActionEvent e) {
                ComponentOrientation current = frame.getComponentOrientation();
                if (current == ComponentOrientation.LEFT_TO_RIGHT) {
                    frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                } else {
                    frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                }
                frame.getRootPane().revalidate();
                frame.invalidate();
                frame.validate();
                frame.repaint();
            }

        };
        addAction(frame, toggleComponentOrientation);
    }
    
    public void addMessage(JXFrame frame, String message) {
        JXStatusBar statusBar = getStatusBar(frame);
        statusBar.add(new JLabel(message), JXStatusBar.Constraint.ResizeBehavior.FILL);
    }

    /**
     * Returns the <code>JXFrame</code>'s status bar. Lazily creates and 
     * sets an instance if necessary.
     * @param frame the target frame
     * @return the frame's statusbar
     */
    public JXStatusBar getStatusBar(JXFrame frame) {
        JXStatusBar statusBar = frame.getRootPaneExt().getStatusBar();
        if (statusBar == null) {
            statusBar = new JXStatusBar();
            frame.setStatusBar(statusBar);
        }
        return statusBar;
    }

    /**
     * Adds the component to the statusbar of the frame.  
     * 
     * @param frame
     * @param component
     */
    public void addStatusComponent(JXFrame frame, JComponent component) {
        getStatusBar(frame).add(component);
        frame.pack();
    }
    
    /**
     * @param frame
     * @param string
     */
    public void addStatusMessage(JXFrame frame, String message) {
        JXStatusBar bar = getStatusBar(frame);
        bar.add(new JLabel(message));
        frame.pack();
    }

    /**
     * PENDING: JW - this is about toggling the LF, does nothing to update the
     * UI. Check all tests using this method to see if they make sense!
     * 
     * 
     * @param system
     */
    public static void setSystemLF(boolean system) {
        String lfName = system ? UIManager.getSystemLookAndFeelClassName()
                : UIManager.getCrossPlatformLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lfName);
//            systemLF = system;
        } catch (Exception e1) {
            LOG.info("exception when setting LF to " + lfName);
            LOG.log(Level.FINE, "caused by ", e1);
        }
    }

    /**
     * Returns whether the current lf is the system lf. It assumes that the
     * lf is either cross-platform or system. Not really safe 
     */
    public static boolean isSystemLF() {
        LookAndFeel lf = UIManager.getLookAndFeel();
        return UIManager.getSystemLookAndFeelClassName().equals(lf.getClass().getName());
    }

    /**
     * Returns whether the current lf is the cross-platform lf. It assumes that the
     * lf is either cross-platform or system. Not really safe 
     */
    public static boolean isCrossPlatformLF() {
        LookAndFeel lf = UIManager.getLookAndFeel();
        return UIManager.getCrossPlatformLookAndFeelClassName().equals(lf.getClass().getName());
    }
    
    /**
     * Action to toggle plaf and update all toplevel windows of the
     * current application. Used to setup the plaf-menu.
     */
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
//                Component c = (Component) e.getSource();
//                Window w = null;
//                
//                for (Container p = c.getParent(); p != null; p = p instanceof JPopupMenu ? (Container) ((JPopupMenu) p)
//                        .getInvoker() : p.getParent()) {
//                    if (p instanceof Window) {
//                        w = (Window) p;
//                    }
//                }
                
                UIManager.setLookAndFeel(plaf);
                SwingXUtilities.updateAllComponentTreeUIs();
                
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
    
    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();
        bar.add(createPlafMenu());
        
        return bar;
    }

    /**
     * Creates a menu filled with one SetPlafAction for each of the currently
     * installed LFs.
     * 
     * @return the menu to use for plaf switching.
     */
    private JMenu createPlafMenu() {
        LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
        JMenu menu = new JMenu("Set L&F");
        
        for (LookAndFeelInfo info : plafs) {
            menu.add(new SetPlafAction(info.getName(), info.getClassName()));
        }
        return menu;
    }
    
    public JXFrame wrapInFrame(JComponent component, String title, boolean showMenu) {
        JXFrame frame = wrapInFrame(component, title);
        if (showMenu) {
            frame.setJMenuBar(createMenuBar());
        }
        frame.pack();
        return frame;
    }

    /**
     * Packs and shows the frame.
     * 
     * @param frame
     */
    public void show(final JXFrame frame) {
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Packs, sizes and shows the frame.
     * 
     * @param frame
     */
    public void show(final JXFrame frame, int width, int height) {
        frame.pack();
        frame.setSize(width, height);
        frame.setVisible(true);
    }
    

}
