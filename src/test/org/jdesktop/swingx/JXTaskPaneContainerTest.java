/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;


public class JXTaskPaneContainerTest extends InteractiveTestCase {

  public JXTaskPaneContainerTest(String testTitle) {
    super(testTitle);
  }

  public void testBean() throws Exception {
    new JXTaskPaneContainerBeanInfo();
  }
  
  public void testAddon() throws Exception {
    // move around all addons
    new JXTaskPaneContainer();
    TestUtilities.cycleAddons();
  }

}
