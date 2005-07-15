/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

import junit.framework.TestCase;

import org.jdesktop.swingx.plaf.basic.BasicLookAndFeelAddons;

public class LookAndFeelAddonsTest extends TestCase {

  public LookAndFeelAddonsTest(String arg0) {
    super(arg0);
  }

  public void testContribute() throws Exception {
    Addon addon = new Addon();
    LookAndFeelAddons.contribute(addon);
    // a ComponentAddon is initialized when it is contributed
    assertTrue(addon.initialized);
    // and uninitialized when "uncontributed"
    LookAndFeelAddons.uncontribute(addon);
    assertTrue(addon.uninitialized);

    // re-contribute the ComponentAddon
    LookAndFeelAddons.contribute(addon);
    // reset its state
    addon.initialized = false;
    addon.uninitialized = false;
    
    // when addon is changed, the ComponentAddon is uninitialized with the
    // previous addon, then initialized with the new
    LookAndFeelAddons oldLFAddon = LookAndFeelAddons.getAddon();
    LookAndFeelAddons.setAddon(BasicLookAndFeelAddons.class);
    LookAndFeelAddons newLFAddon = LookAndFeelAddons.getAddon();
    
    assertTrue(addon.uninitialized);
    assertEquals(oldLFAddon, addon.uninitializedWith);
    
    assertTrue(addon.initialized);
    assertEquals(newLFAddon, addon.initializedWith);
  }
  
  static class Addon extends AbstractComponentAddon {
    boolean initialized;
    LookAndFeelAddons initializedWith;
    
    boolean uninitialized;
    LookAndFeelAddons uninitializedWith;
    
    public Addon() { super("Addon"); }
    @Override
    public void initialize(LookAndFeelAddons addon) {
      initialized = true;
      initializedWith = addon;
    }
    @Override
    public void uninitialize(LookAndFeelAddons addon) {      
      uninitialized = true;
      uninitializedWith = addon;
    }
  }
}
