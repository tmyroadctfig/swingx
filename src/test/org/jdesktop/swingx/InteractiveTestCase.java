/*
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Point;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;

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

    public JXFrame wrapWithScrollingInFrame(JComponent component, String title) {
        JScrollPane scroller = new JScrollPane(component);
        return wrapInFrame(scroller, title);
    }

    public JXFrame wrapWithScrollingInFrame(JComponent leftComp, JComponent rightComp, String title) {
        JComponent comp = Box.createHorizontalBox();
        comp.add(new JScrollPane(leftComp));
        comp.add(new JScrollPane(rightComp));
        JXFrame frame = wrapInFrame(comp, title);
        return frame;
    }


    public JXFrame wrapInFrame(JComponent component, String title) {
        JXFrame frame = new JXFrame(title, false);
        JToolBar toolbar = new JToolBar();
        frame.getRootPaneExt().setToolBar(toolbar);
        frame.getContentPane().add(BorderLayout.CENTER, component);
//        frame.getContentPane().add(BorderLayout.NORTH, toolbar);
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
                    methods[i].invoke(this, null);
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

    public void addMessage(JXFrame frame, String message) {
        JXStatusBar statusBar = frame.getRootPaneExt().getStatusBar();
        if (statusBar == null) {
            statusBar = new JXStatusBar();
            frame.getRootPaneExt().setStatusBar(statusBar);
        }
        statusBar.add(new JLabel(message));
    }
    
    /**
     * PENDING: JW - this is about toggling the LF, does nothing to
     * update the UI. Check all tests using this method to see if they 
     * make sense! 
     *
     * 
     * @param system
     */
    public static void setSystemLF(boolean system) {
        String lfName = system ? UIManager.getSystemLookAndFeelClassName() :
            UIManager.getCrossPlatformLookAndFeelClassName();
        try {
          UIManager.setLookAndFeel(lfName);
       } catch (Exception e1) { 
           LOG.info("exception when setting LF to " + lfName);
      }
    }
}
