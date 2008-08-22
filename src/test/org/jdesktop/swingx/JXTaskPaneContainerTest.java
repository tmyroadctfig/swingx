/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.event.ActionEvent;

import javax.swing.plaf.UIResource;

import org.jdesktop.swingx.action.AbstractActionExt;


public class JXTaskPaneContainerTest extends InteractiveTestCase {

    public static void main(String[] args) throws Exception {
//      setSystemLF(true);
      JXTaskPaneContainerTest test = new JXTaskPaneContainerTest("TaskPaneContainer");
      try {
          test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
  }

  public JXTaskPaneContainerTest(String testTitle) {
    super(testTitle);
  }

  public void testAddon() throws Exception {
    // move around all addons
    TestUtilities.cycleAddons(new JXTaskPaneContainer());
  }

    /**
     * Issue #843-swingx: BasicTaskPaneContainerUI must respect custom Layout.
     */
    public void testRespectCustomLayoutGap() {
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        VerticalLayout layout = (VerticalLayout) container.getLayout();
        VerticalLayout custom = new VerticalLayout(layout.getGap() + 10);
        container.setLayout(custom);
        container.updateUI();
        assertEquals(custom.getGap(), ((VerticalLayout) container.getLayout())
                .getGap());
    }

    /**
     * Issue #843-swingx: BasicTaskPaneContainerUI must respect custom Layout.
     */
    public void testRespectCustomLayout() {
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        VerticalLayout layout = (VerticalLayout) container.getLayout();
        VerticalLayout custom = new VerticalLayout(layout.getGap() + 10);
        container.setLayout(custom);
        container.updateUI();
        assertSame(custom, container.getLayout());
    }

    /**
     * Issue #843-swingx: BasicTaskPaneContainerUI must respect custom Layout.
     */
    public void testLayoutUIResource() {
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        assertTrue(container.getLayout() instanceof UIResource);
    }

     public void interactiveGap() {
        JXTaskPaneContainer container = new JXTaskPaneContainer();
//        ((VerticalLayout) container.getLayout()).setGap(0);
        JXTaskPane first = new JXTaskPane();
        fillTaskPane(first);
        container.add(first);
        JXTaskPane second = new JXTaskPane();
        fillTaskPane(second);
        container.add(second);
        showWithScrollingInFrame(container, "custom gap");
    }

    private void fillTaskPane(JXTaskPane first) {
        first.add(new AbstractActionExt("some") {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub

            }

        });

        first.add(new AbstractActionExt("other") {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub

            }

        });
    }
}
