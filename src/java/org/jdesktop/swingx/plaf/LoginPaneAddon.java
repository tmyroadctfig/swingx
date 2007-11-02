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

import org.jdesktop.swingx.JXLoginPane;

/**
 *
 * @author rbair
 */
public class LoginPaneAddon extends AbstractComponentAddon {
    
    /** Creates a new instance of LoginPaneAddon */
    public LoginPaneAddon() {
        super("JXLoginPane");
    }
    
  @Override
  protected void addBasicDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addBasicDefaults(addon, defaults);
    defaults.addAll(Arrays.asList(new Object[] { 
      JXLoginPane.uiClassID,
      "org.jdesktop.swingx.plaf.basic.BasicLoginPaneUI",
      //TODO this icon is unused; remove?
      "JXLoginPane.errorIcon",
      LookAndFeel.makeIcon(LoginPaneAddon.class, "resources/error16.png"),
      "JXLoginPane.bannerFont",
      new FontUIResource("Arial Bold", Font.PLAIN, 36),
      "JXLoginPane.bannerForeground", new ColorUIResource(Color.WHITE),
      "JXLoginPane.bannerDarkBackground", new ColorUIResource(Color.GRAY),
      "JXLoginPane.bannerLightBackground", new ColorUIResource(Color.LIGHT_GRAY),
    }));
    // Popuplate UIDefaults with the localizable Strings we will use
    // in the Login panel.
    String clsName = JXLoginPane.class.getCanonicalName();
    ResourceBundle res = ResourceBundle.getBundle("org.jdesktop.swingx.auth.resources.resources", JXLoginPane.getDefaultLocale());
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
        "JXLoginPane.bannerForeground", new ColorUIResource(Color.WHITE),
        "JXLoginPane.bannerDarkBackground", new ColorUIResource(Color.GRAY),
        "JXLoginPane.bannerLightBackground", new ColorUIResource(Color.LIGHT_GRAY),
      }));
    } else {
      defaults.addAll(Arrays.asList(new Object[] { 
        "JXLoginPane.bannerForeground", new ColorUIResource(Color.WHITE),
        "JXLoginPane.bannerDarkBackground", MetalLookAndFeel.getCurrentTheme().getPrimaryControlDarkShadow(),
        "JXLoginPane.bannerLightBackground", MetalLookAndFeel.getCurrentTheme().getPrimaryControl()
      }));
    }
  }

  @Override
  protected void addWindowsDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addWindowsDefaults(addon, defaults);
    defaults.addAll(Arrays.asList(new Object[] { 
      "JXLoginPane.bannerForeground", new ColorUIResource(Color.WHITE),
      "JXLoginPane.bannerDarkBackground", new ColorUIResource(49, 121, 242),
      "JXLoginPane.bannerLightBackground", new ColorUIResource(198, 211, 247),
    }));
  }
}
