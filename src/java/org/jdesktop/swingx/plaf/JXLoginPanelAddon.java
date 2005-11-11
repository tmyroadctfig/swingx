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
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.IconUIResource;
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
      "JXLoginPanel.error.icon",
      new IconUIResource(new ImageIcon(getClass().getResource("resources/error16.png"))),
      "JXLoginPanel.banner.font",
      new FontUIResource("Arial Bold", Font.PLAIN, 36),
      "JXLoginPanel.banner.foreground", new ColorUIResource(Color.WHITE),
      "JXLoginPanel.banner.darkBackground", new ColorUIResource(Color.GRAY),
      "JXLoginPanel.banner.lightBackground", new ColorUIResource(Color.LIGHT_GRAY),
    }));
  }
  
  @Override
  protected void addMetalDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addMetalDefaults(addon, defaults);

    if (isPlastic()) {
      defaults.addAll(Arrays.asList(new Object[] { 
        "JXLoginPanel.banner.foreground", new ColorUIResource(Color.WHITE),
        "JXLoginPanel.banner.darkBackground", new ColorUIResource(Color.GRAY),
        "JXLoginPanel.banner.lightBackground", new ColorUIResource(Color.LIGHT_GRAY),
      }));
    } else {
      defaults.addAll(Arrays.asList(new Object[] { 
        "JXLoginPanel.banner.foreground", new ColorUIResource(255, 255, 255),
        "JXLoginPanel.banner.darkBackground", MetalLookAndFeel.getCurrentTheme().getPrimaryControlDarkShadow(),
        "JXLoginPanel.banner.lightBackground", MetalLookAndFeel.getCurrentTheme().getPrimaryControl()
      }));
    }
  }

  @Override
  protected void addWindowsDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addWindowsDefaults(addon, defaults);
    defaults.addAll(Arrays.asList(new Object[] { 
      "JXLoginPanel.banner.foreground", new ColorUIResource(255, 255, 255),
      "JXLoginPanel.banner.darkBackground", new ColorUIResource(49, 121, 242),
      "JXLoginPanel.banner.lightBackground", new ColorUIResource(198, 211, 247),
    }));
  }
}
