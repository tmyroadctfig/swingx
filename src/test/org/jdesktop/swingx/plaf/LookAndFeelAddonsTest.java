/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

import java.awt.Color;

import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.plaf.basic.BasicLookAndFeelAddons;

public class LookAndFeelAddonsTest extends TestCase {


   /**
     * Issue #784-swingx: Frequent NPE in getUI of (new) SwingX components.
     * 
     * Here: test reload if same instance of LAF is set (example from
     * bug report)
     * @throws UnsupportedLookAndFeelException 
     */
    public void testReloadAddons() throws UnsupportedLookAndFeelException {
        // load the addon for a new component
        new JXMonthView();
        // reset laf
         UIManager.setLookAndFeel(UIManager.getLookAndFeel());
         // check that the addon is still available
        new JXMonthView();

    }
  /**
   * A look and feel can't override SwingX defaults
   */
  public void testIssue293() throws Exception {
	  class CustomLF extends BasicLookAndFeel {
	    @Override
	    public String getDescription() {
	      return "custom";
	    }
		  @Override
		  public String getID() {
		    return "custom";
		  }
		  @Override
		  public String getName() {
		    return "custom";
		  }
		  @Override
		  public boolean isNativeLookAndFeel() {
		    return false;
		  }
		  @Override
		  public boolean isSupportedLookAndFeel() {
		    return true;
		  }
      @Override
      protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        table.put("CustomProperty", "CustomValue");
      }
	  };
    
    LookAndFeelAddons.setTrackingLookAndFeelChanges(true);
    
    // without addons, the prop is not overriden
    LookAndFeel lf = new CustomLF();
    UIManager.setLookAndFeel(lf);
    assertEquals("CustomValue", UIManager.get("CustomProperty"));
    
    // with an addon, the prop is overriden
    ComponentAddon myAddon = new AbstractComponentAddon("myAddon") {
      @Override
      protected void addBasicDefaults(LookAndFeelAddons addon, DefaultsList defaults) {
        defaults.add("CustomProperty", "customAddonValue");
        defaults.add("AddonProperty", "addonValue");
      }
    };
    LookAndFeelAddons.contribute(myAddon);
    // the addon property was not registered as overriden by the l&f
    assertEquals("CustomValue", UIManager.get("CustomProperty"));
    assertEquals("addonValue", UIManager.get("AddonProperty"));
    
    // now revert to a standard look and feel
    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    // now the addon properties are used 
    assertEquals("customAddonValue", UIManager.get("CustomProperty"));
    assertEquals("addonValue", UIManager.get("AddonProperty"));    
  }
  
  /**
   * LookAndFeelAddons override entries manually added to UIManager
   */
  public void testIssue144() throws Exception {
    UIManager.put("Addon.title", "customized");
    UIManager.put("Addon.border", new LineBorder(Color.blue));
    
    Addon addon = new Addon();
    LookAndFeelAddons.contribute(addon);
    
    assertEquals("customized", UIManager.get("Addon.title"));
    assertEquals("my subtitle", UIManager.get("Addon.subtitle"));
    assertTrue(UIManager.get("Addon.border") instanceof LineBorder);
    
    UIManager.put("Addon.title", null);
    assertEquals("my title", UIManager.get("Addon.title"));
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

    public Addon() {
      super("Addon");
    }
    @Override
    public void initialize(LookAndFeelAddons addon) {
      initialized = true;
      initializedWith = addon;
      addon.loadDefaults(getDefaults());
    }
    @Override
    public void uninitialize(LookAndFeelAddons addon) {
      uninitialized = true;
      uninitializedWith = addon;
      addon.unloadDefaults(getDefaults());
    }
    protected Object[] getDefaults() {
      return new Object[] {
        "Addon.title", "my title",
        "Addon.subtitle", "my subtitle",
        "Addon.border", new BorderUIResource(new EmptyBorder(0, 0, 0, 0)),
        "Addon.color", new ColorUIResource(Color.blue)};
    }
  }
}
