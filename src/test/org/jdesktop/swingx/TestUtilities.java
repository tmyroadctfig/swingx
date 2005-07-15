/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import javax.swing.UIManager;

import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.plaf.aqua.AquaLookAndFeelAddons;
import org.jdesktop.swingx.plaf.metal.MetalLookAndFeelAddons;
import org.jdesktop.swingx.plaf.motif.MotifLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsClassicLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsLookAndFeelAddons;

/**
 * Provides helper methods to test SwingX components .<br>
 */
public class TestUtilities {

  /**
   * Go through all existing LookAndFeelAddons. This leads all registered
   * {@link org.jdesktop.swingx.plaf.ComponentAddon} to initialize/uninitialize
   * themselves.
   */
  public static void cycleAddons() throws Exception {
    LookAndFeelAddons.setAddon(AquaLookAndFeelAddons.class.getName());
    LookAndFeelAddons.setAddon(MetalLookAndFeelAddons.class.getName());
    LookAndFeelAddons.setAddon(MotifLookAndFeelAddons.class.getName());
    LookAndFeelAddons.setAddon(WindowsLookAndFeelAddons.class.getName());

    String property = UIManager.getString("win.xpstyle.name");
    try {
      UIManager.put("win.xpstyle.name",
        WindowsLookAndFeelAddons.HOMESTEAD_VISUAL_STYLE);
      LookAndFeelAddons.setAddon(WindowsClassicLookAndFeelAddons.class
        .getName());
      UIManager.put("win.xpstyle.name",
        WindowsLookAndFeelAddons.SILVER_VISUAL_STYLE);
      LookAndFeelAddons.setAddon(WindowsClassicLookAndFeelAddons.class
        .getName());
      UIManager.put("win.xpstyle.name", null);
      LookAndFeelAddons.setAddon(WindowsClassicLookAndFeelAddons.class
        .getName());
    } finally {
      UIManager.put("win.xpstyle.name", property);
    }
  }

}
