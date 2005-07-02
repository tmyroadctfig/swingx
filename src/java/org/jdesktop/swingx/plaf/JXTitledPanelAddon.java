/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jdesktop.swingx.JXTitledPanel;

/**
 * Addon for <code>JXTitledPanel</code>.<br>
 *
 */
public class JXTitledPanelAddon extends AbstractComponentAddon {

  public JXTitledPanelAddon() {
    super("JXTitledPanel");
  }

  @Override
  protected void addBasicDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addBasicDefaults(addon, defaults);
    defaults.addAll(Arrays.asList(new Object[] { 
      JXTitledPanel.uiClassID,
      "org.jdesktop.swingx.plaf.metal.MetalTitledPanelUI",
      "JXTitledPanel.title.font",
      UIManager.getFont("Button.font"),
      "JXTitledPanel.title.foreground", new ColorUIResource(Color.WHITE),
      "JXTitledPanel.title.darkBackground", new ColorUIResource(Color.GRAY),
      "JXTitledPanel.title.lightBackground", new ColorUIResource(Color.LIGHT_GRAY),
    }));
  }
  
  @Override
  protected void addMetalDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addMetalDefaults(addon, defaults);

    if (isPlastic()) {
      defaults.addAll(Arrays.asList(new Object[] { 
        "JXTitledPanel.title.foreground", new ColorUIResource(Color.WHITE),
        "JXTitledPanel.title.darkBackground", new ColorUIResource(Color.GRAY),
        "JXTitledPanel.title.lightBackground", new ColorUIResource(Color.LIGHT_GRAY),
      }));
    } else {
      defaults.addAll(Arrays.asList(new Object[] { 
        "JXTitledPanel.title.foreground", new ColorUIResource(255, 255, 255),
        "JXTitledPanel.title.darkBackground", MetalLookAndFeel.getCurrentTheme().getPrimaryControlDarkShadow(),
        "JXTitledPanel.title.lightBackground", MetalLookAndFeel.getCurrentTheme().getPrimaryControl()
      }));
    }
  }

  @Override
  protected void addWindowsDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addWindowsDefaults(addon, defaults);
//        if (OS.isWindowsXP() && OS.getWindowsVisualStyle() == OS.WinXPTheme.LUNA) {
    defaults.addAll(Arrays.asList(new Object[] { 
      "JXTitledPanel.title.foreground", new ColorUIResource(255, 255, 255),
      "JXTitledPanel.title.darkBackground", new ColorUIResource(49, 121, 242),
      "JXTitledPanel.title.lightBackground", new ColorUIResource(198, 211, 247),
    }));
//        } else {
//            defaults.addAll(Arrays.asList(new Object[] { 
//            "JXTitledPanel.title.foreground", new ColorUIResource(255, 255, 255),
//            "JXTitledPanel.title.darkBackground", new ColorUIResource(49, 121, 242),
//            "JXTitledPanel.title.lightBackground", new ColorUIResource(198, 211, 247),
//            }));
//        }
  }

}
