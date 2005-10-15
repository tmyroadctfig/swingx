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
import java.awt.SystemColor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.plaf.windows.WindowsClassicLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsLookAndFeelAddons;
import org.jdesktop.swingx.util.JVM;
import org.jdesktop.swingx.util.OS;

/**
 * Addon for <code>JXTaskPane</code>.<br>
 *
 * @author <a href="mailto:fred@L2FProd.com">Frederic Lavigne</a>
 */
public class JXTaskPaneAddon extends AbstractComponentAddon {

  public JXTaskPaneAddon() {
    super("JXTaskPane");
  }

  @Override
  protected void addBasicDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    Font taskPaneFont = UIManager.getFont("Label.font");
    if (taskPaneFont == null) {
      taskPaneFont = new Font("Dialog", Font.PLAIN, 12);
    }
    taskPaneFont = taskPaneFont.deriveFont(Font.BOLD);
    
    Color menuBackground = new ColorUIResource(SystemColor.menu);
    defaults.addAll(Arrays.asList(new Object[]{
      JXTaskPane.uiClassID,
      "org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI",
      "TaskPane.font",
      new FontUIResource(taskPaneFont),        
      "TaskPane.background",
      UIManager.getColor("List.background"),
      "TaskPane.specialTitleBackground",
      new ColorUIResource(menuBackground.darker()),
      "TaskPane.titleBackgroundGradientStart",
      menuBackground,
      "TaskPane.titleBackgroundGradientEnd",
      menuBackground,
      "TaskPane.titleForeground",
      new ColorUIResource(SystemColor.menuText),
      "TaskPane.specialTitleForeground",
      new ColorUIResource(SystemColor.menuText).brighter(),
      "TaskPane.animate",
      Boolean.TRUE,
      "TaskPane.focusInputMap",
      new UIDefaults.LazyInputMap(
        new Object[] {
          "ENTER",
          "toggleExpanded",
          "SPACE",
          "toggleExpanded" }),
    }));
  }

  @Override
  protected void addMetalDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addMetalDefaults(addon, defaults);
    // if using Ocean, use the Glossy l&f
    String taskPaneGroupUI = "org.jdesktop.swingx.plaf.metal.MetalTaskPaneUI";
    if (JVM.current().isOrLater(JVM.JDK1_5)) {
      try {
        Method method = MetalLookAndFeel.class.getMethod("getCurrentTheme");
        Object currentTheme = method.invoke(null);
        if (Class.forName("javax.swing.plaf.metal.OceanTheme").isInstance(
          currentTheme)) {
          taskPaneGroupUI = "org.jdesktop.swingx.plaf.misc.GlossyTaskPaneUI";
        }
      } catch (Exception e) {
      }
    }
    defaults.addAll(Arrays.asList(new Object[]{
      JXTaskPane.uiClassID,
      taskPaneGroupUI,
      "TaskPane.foreground",
      UIManager.getColor("activeCaptionText"),
      "TaskPane.background",
      MetalLookAndFeel.getControl(),
      "TaskPane.specialTitleBackground",
      MetalLookAndFeel.getPrimaryControl(),
      "TaskPane.titleBackgroundGradientStart",
      MetalLookAndFeel.getPrimaryControl(),
      "TaskPane.titleBackgroundGradientEnd",
      MetalLookAndFeel.getPrimaryControlHighlight(),
      "TaskPane.titleForeground",
      MetalLookAndFeel.getControlTextColor(),        
      "TaskPane.specialTitleForeground",
      MetalLookAndFeel.getControlTextColor(),     
      "TaskPane.borderColor",
      MetalLookAndFeel.getPrimaryControl(),
      "TaskPane.titleOver",
      MetalLookAndFeel.getControl().darker(),
      "TaskPane.specialTitleOver",
      MetalLookAndFeel.getPrimaryControlHighlight()        
    }));      
  }

  @Override
  protected void addWindowsDefaults(LookAndFeelAddons addon,
    List<Object> defaults) {
    super.addWindowsDefaults(addon, defaults);
    
    if (addon instanceof WindowsLookAndFeelAddons) {
      defaults.addAll(Arrays.asList(new Object[]{
        JXTaskPane.uiClassID,
        "org.jdesktop.swingx.plaf.windows.WindowsTaskPaneUI"}));

      String xpStyle = OS.getWindowsVisualStyle();
      if (WindowsLookAndFeelAddons.HOMESTEAD_VISUAL_STYLE
        .equalsIgnoreCase(xpStyle)) {        
        defaults.addAll(Arrays.asList(new Object[]{
          "TaskPane.foreground",
          new ColorUIResource(86, 102, 45),
          "TaskPane.background",
          new ColorUIResource(246, 246, 236),
          "TaskPane.specialTitleBackground",
          new ColorUIResource(224, 231, 184),
          "TaskPane.titleBackgroundGradientStart",
          new ColorUIResource(255, 255, 255),
          "TaskPane.titleBackgroundGradientEnd",
          new ColorUIResource(224, 231, 184),
          "TaskPane.titleForeground",
          new ColorUIResource(86, 102, 45),
          "TaskPane.titleOver",
          new ColorUIResource(114, 146, 29),
          "TaskPane.specialTitleForeground",
          new ColorUIResource(86, 102, 45),
          "TaskPane.specialTitleOver",
          new ColorUIResource(114, 146, 29),
          "TaskPane.borderColor",
          new ColorUIResource(255, 255, 255),
        }));
      } else if (WindowsLookAndFeelAddons.SILVER_VISUAL_STYLE
        .equalsIgnoreCase(xpStyle)) {
        defaults.addAll(Arrays.asList(new Object[]{
          "TaskPane.foreground",
          new ColorUIResource(Color.black),
          "TaskPane.background",
          new ColorUIResource(240, 241, 245),
          "TaskPane.specialTitleBackground",
          new ColorUIResource(222, 222, 222),
          "TaskPane.titleBackgroundGradientStart",
          new ColorUIResource(Color.white),
          "TaskPane.titleBackgroundGradientEnd",
          new ColorUIResource(214, 215, 224),
          "TaskPane.titleForeground",
          new ColorUIResource(Color.black),
          "TaskPane.titleOver",
          new ColorUIResource(126, 124, 124),
          "TaskPane.specialTitleForeground",
          new ColorUIResource(Color.black),
          "TaskPane.specialTitleOver",
          new ColorUIResource(126, 124, 124),
          "TaskPane.borderColor",
          new ColorUIResource(Color.white),
        }));
      } else {        
        defaults.addAll(Arrays.asList(new Object[]{
          "TaskPane.foreground",
          new ColorUIResource(Color.white),
          "TaskPane.background",
          new ColorUIResource(214, 223, 247),
          "TaskPane.specialTitleBackground",
          new ColorUIResource(33, 89, 201),
          "TaskPane.titleBackgroundGradientStart",
          new ColorUIResource(Color.white),
          "TaskPane.titleBackgroundGradientEnd",
          new ColorUIResource(199, 212, 247),
          "TaskPane.titleForeground",
          new ColorUIResource(33, 89, 201),
          "TaskPane.specialTitleForeground",
          new ColorUIResource(Color.white),
          "TaskPane.borderColor",
          new ColorUIResource(Color.white),
        }));
      }
    }
    
    if (addon instanceof WindowsClassicLookAndFeelAddons) {
      defaults.addAll(Arrays.asList(new Object[]{
        JXTaskPane.uiClassID,
        "org.jdesktop.swingx.plaf.windows.WindowsClassicTaskPaneUI",
        "TaskPane.foreground",
        new ColorUIResource(Color.black),
        "TaskPane.background",
        new ColorUIResource(Color.white),
        "TaskPane.specialTitleBackground",
        new ColorUIResource(10, 36, 106),
        "TaskPane.titleBackgroundGradientStart",
        new ColorUIResource(212, 208, 200),
        "TaskPane.titleBackgroundGradientEnd",
        new ColorUIResource(212, 208, 200),
        "TaskPane.titleForeground",
        new ColorUIResource(Color.black),
        "TaskPane.specialTitleForeground",
        new ColorUIResource(Color.white),
        "TaskPane.borderColor",
        new ColorUIResource(212, 208, 200),
      }));
    }
  }
  
  @Override
  protected void addMacDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addMacDefaults(addon, defaults);
    defaults.addAll(Arrays.asList(new Object[]{
      JXTaskPane.uiClassID,
      "org.jdesktop.swingx.plaf.misc.GlossyTaskPaneUI",
      "TaskPane.background",
      new ColorUIResource(245, 245, 245),
      "TaskPane.titleForeground",
      new ColorUIResource(Color.black),
      "TaskPane.specialTitleBackground",
      new ColorUIResource(188,188,188),
      "TaskPane.specialTitleForeground",
      new ColorUIResource(Color.black),
      "TaskPane.titleBackgroundGradientStart",
      new ColorUIResource(250,250,250),
      "TaskPane.titleBackgroundGradientEnd",
      new ColorUIResource(188,188,188),
      "TaskPane.borderColor",
      new ColorUIResource(97, 97, 97),
      "TaskPane.titleOver",
      new ColorUIResource(125, 125, 97),
      "TaskPane.specialTitleOver",
      new ColorUIResource(125, 125, 97),
    }));
  }
  
}
