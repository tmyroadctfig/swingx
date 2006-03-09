/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
    defaults.addAll(Arrays.asList(new Object[] { 
        //I'd actually like to use these colors with JGoodies PlaticLookAndFeel, if we had a
        //mechanism for supporting it (do we?)
//        "JXTitledPanel.title.foreground", new ColorUIResource(255, 255, 255),
//        "JXTitledPanel.title.darkBackground", new ColorUIResource(49, 121, 242),
//        "JXTitledPanel.title.lightBackground", new ColorUIResource(198, 211, 247),
        "JXTitledPanel.title.foreground", UIManager.getColor("InternalFrame.activeTitleForeground"),
        "JXTitledPanel.title.darkBackground", UIManager.getColor("InternalFrame.activeTitleBackground"),
        "JXTitledPanel.title.lightBackground", UIManager.getColor("InternalFrame.inactiveTitleGradient")
    }));
  }

}
