/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.plaf.aqua.AquaLookAndFeelAddons;
import org.jdesktop.swingx.plaf.basic.BasicLookAndFeelAddons;
import org.jdesktop.swingx.plaf.metal.MetalLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsClassicLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsLookAndFeelAddons;
import org.jdesktop.swingx.util.OS;

/**
 * Addon for <code>JXTaskPaneContainer</code>. <br>
 *  
 */
public class JXTaskPaneContainerAddon implements ComponentAddon {

  public String getName() {
    return "JXTaskPaneContainer";
  }

  public void initialize(LookAndFeelAddons addon) {
    addon.loadDefaults(getDefaults(addon));
  }
  
  public void uninitialize(LookAndFeelAddons addon) {
    addon.unloadDefaults(getDefaults(addon));
  }

  private Object[] getDefaults(LookAndFeelAddons addon) {
    List defaults = new ArrayList();
    
    if (addon instanceof BasicLookAndFeelAddons) {
      defaults.addAll(Arrays.asList(new Object[]{
        JXTaskPaneContainer.uiClassID,
        "org.jdesktop.swingx.plaf.basic.BasicTaskPaneContainerUI",
        "TaskPaneContainer.useGradient",
        Boolean.FALSE,
        "TaskPaneContainer.background",
        UIManager.getColor("Desktop.background")
      }));
    }
    
    if (addon instanceof MetalLookAndFeelAddons) {
      defaults.addAll(Arrays.asList(new Object[]{
        "TaskPaneContainer.background",
        MetalLookAndFeel.getDesktopColor()
      }));
    }
    
    if (addon instanceof WindowsClassicLookAndFeelAddons) {
      defaults.addAll(Arrays.asList(new Object[]{
        "TaskPaneContainer.background",
        UIManager.getColor("List.background")
      }));      
    } else if (addon instanceof WindowsLookAndFeelAddons) {     
      String xpStyle = OS.getWindowsVisualStyle();
      ColorUIResource background;
      ColorUIResource backgroundGradientStart;
      ColorUIResource backgroundGradientEnd;
      
      if (WindowsLookAndFeelAddons.HOMESTEAD_VISUAL_STYLE
        .equalsIgnoreCase(xpStyle)) {        
        background = new ColorUIResource(201, 215, 170);
        backgroundGradientStart = new ColorUIResource(204, 217, 173);
        backgroundGradientEnd = new ColorUIResource(165, 189, 132);
      } else if (WindowsLookAndFeelAddons.SILVER_VISUAL_STYLE
        .equalsIgnoreCase(xpStyle)) {
        background = new ColorUIResource(192, 195, 209);
        backgroundGradientStart = new ColorUIResource(196, 200, 212);
        backgroundGradientEnd = new ColorUIResource(177, 179, 200);
      } else {        
        background = new ColorUIResource(117, 150, 227);
        backgroundGradientStart = new ColorUIResource(123, 162, 231);
        backgroundGradientEnd = new ColorUIResource(99, 117, 214);
      }      
      defaults.addAll(Arrays.asList(new Object[]{
        "TaskPaneContainer.useGradient",
        Boolean.TRUE,
        "TaskPaneContainer.background",
        background,
        "TaskPaneContainer.backgroundGradientStart",
        backgroundGradientStart,
        "TaskPaneContainer.backgroundGradientEnd",
        backgroundGradientEnd,
      }));
    }
        
    if (addon instanceof AquaLookAndFeelAddons) {
      defaults.addAll(Arrays.asList(new Object[]{
        "TaskPaneContainer.background",
        new ColorUIResource(238, 238, 238),
      }));            
    }
    
    return defaults.toArray();
  }

}
