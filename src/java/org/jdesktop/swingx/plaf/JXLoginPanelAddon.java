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
import java.awt.Font;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jdesktop.swingx.JXLoginPanel;

/**
 *
 * @author rbair
 */
public class JXLoginPanelAddon extends AbstractComponentAddon {
    
    /** Creates a new instance of JXLoginPanelAddon */
    public JXLoginPanelAddon() {
        super("JXLoginPanel");
    }
    
  @Override
  protected void addBasicDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addBasicDefaults(addon, defaults);
    defaults.addAll(Arrays.asList(new Object[] { 
      JXLoginPanel.uiClassID,
      "org.jdesktop.swingx.plaf.basic.BasicLoginPanelUI",
      //TODO this icon is unused; remove?
      "JXLoginPanel.errorIcon",
      LookAndFeel.makeIcon(JXLoginPanelAddon.class, "resources/error16.png"),
      "JXLoginPanel.bannerFont",
      new FontUIResource("Arial Bold", Font.PLAIN, 36),
      "JXLoginPanel.bannerForeground", new ColorUIResource(Color.WHITE),
      "JXLoginPanel.bannerDarkBackground", new ColorUIResource(Color.GRAY),
      "JXLoginPanel.bannerLightBackground", new ColorUIResource(Color.LIGHT_GRAY),
    }));
    // Popuplate UIDefaults with the localizable Strings we will use
    // in the Login panel.
    String clsName = JXLoginPanel.class.getCanonicalName();
    ResourceBundle res = ResourceBundle.getBundle("org.jdesktop.swingx.auth.resources.resources", JXLoginPanel.getDefaultLocale());
    Enumeration<String> keys = res.getKeys();
    while (keys.hasMoreElements()) {
        String key = keys.nextElement();
        UIManager.put(clsName + "." + key, res.getString(key));
        //System.out.println("adding:" + clsName + "." + key +", val:" + res.getString(key));
    }
  }
  
  @Override
  protected void addMetalDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addMetalDefaults(addon, defaults);

    if (isPlastic()) {
      defaults.addAll(Arrays.asList(new Object[] { 
        "JXLoginPanel.bannerForeground", new ColorUIResource(Color.WHITE),
        "JXLoginPanel.bannerDarkBackground", new ColorUIResource(Color.GRAY),
        "JXLoginPanel.bannerLightBackground", new ColorUIResource(Color.LIGHT_GRAY),
      }));
    } else {
      defaults.addAll(Arrays.asList(new Object[] { 
        "JXLoginPanel.bannerForeground", new ColorUIResource(Color.WHITE),
        "JXLoginPanel.bannerDarkBackground", MetalLookAndFeel.getCurrentTheme().getPrimaryControlDarkShadow(),
        "JXLoginPanel.bannerLightBackground", MetalLookAndFeel.getCurrentTheme().getPrimaryControl()
      }));
    }
  }

  @Override
  protected void addWindowsDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addWindowsDefaults(addon, defaults);
    defaults.addAll(Arrays.asList(new Object[] { 
      "JXLoginPanel.bannerForeground", new ColorUIResource(Color.WHITE),
      "JXLoginPanel.bannerDarkBackground", new ColorUIResource(49, 121, 242),
      "JXLoginPanel.bannerLightBackground", new ColorUIResource(198, 211, 247),
    }));
  }
}
