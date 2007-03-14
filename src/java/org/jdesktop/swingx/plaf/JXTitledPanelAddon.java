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
import java.awt.GradientPaint;
import java.util.Arrays;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.MattePainter;
//import org.jdesktop.swingx.painter.gradient.BasicGradientPainter;

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
      "JXTitledPanel.title.painter", new PainterUIResource(
              new MattePainter(new GradientPaint(0, 0, Color.LIGHT_GRAY, 0, 1, Color.GRAY)))
    }));
  }
  
  @Override
  protected void addMetalDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addMetalDefaults(addon, defaults);

    if (isPlastic()) {
      defaults.addAll(Arrays.asList(new Object[] { 
        "JXTitledPanel.title.foreground", new ColorUIResource(255, 255, 255),
        "JXTitledPanel.title.painter", new PainterUIResource(
                new MattePainter(new GradientPaint(0, 0, 
                    new Color(49, 121, 242),
                    0, 1, 
                    new Color(198, 211, 247)
                    )))
      }));
    } else {
      defaults.addAll(Arrays.asList(new Object[] { 
        "JXTitledPanel.title.foreground", new ColorUIResource(255, 255, 255),
        "JXTitledPanel.title.painter", new PainterUIResource(
                new MattePainter(new GradientPaint(0, 0, 
                    MetalLookAndFeel.getCurrentTheme().getPrimaryControl(), 0, 1,
                    MetalLookAndFeel.getCurrentTheme().getPrimaryControlDarkShadow())))
      }));
    }
  }

  @Override
  protected void addWindowsDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addWindowsDefaults(addon, defaults);
    // JW: copied to get hold of the old colors
//  "JXTitledPanel.title.foreground", new ColorUIResource(255, 255, 255),
//  "JXTitledPanel.title.darkBackground", new ColorUIResource(49, 121, 242),
//  "JXTitledPanel.title.lightBackground", new ColorUIResource(198, 211, 247),
  
    // JW: hot fix for #291-swingx
    // was tracked down by Neil Weber - the requested colors are not available in 
    // all LFs, so changed to fall-back to something real
    // don't understand why this has blown when trying to toggle to Metal...
    // definitely needs deeper digging 
    defaults.addAll(Arrays.asList(new Object[] { 
            "JXTitledPanel.title.foreground", 
                getSafeColor("InternalFrame.activeTitleForeground", new ColorUIResource(255, 255, 255)),
            "JXTitledPanel.title.painter", new PainterUIResource(
                    new MattePainter(new GradientPaint(0, 0, 
                        getSafeColor("InternalFrame.inactiveTitleGradient", new ColorUIResource(49, 121, 242)), 0, 1,
                        getSafeColor("InternalFrame.activeTitleBackground", new ColorUIResource(198, 211, 247)))))
        }));

//    defaults.addAll(Arrays.asList(new Object[] { 
//        "JXTitledPanel.title.foreground", UIManager.getColor("InternalFrame.activeTitleForeground"),
//        "JXTitledPanel.title.painter", new PainterUIResource(
//                new BasicGradientPainter(0, 0, 
//                    UIManager.getColor("InternalFrame.inactiveTitleGradient"), 0, 1,
//                    UIManager.getColor("InternalFrame.activeTitleBackground")))
//    }));

  
  }

  protected Color getSafeColor(String uiKey, Color fallBack) {
      Color color = UIManager.getColor(uiKey);
      if (color == null) {
          color = fallBack;
      }
      return color;
  }
}
