/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.plaf.ComponentAddon;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
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
    
    if (addon instanceof BasicLookAndFeelAddons) {
      addon.loadDefaults(new Object[] {
        JXTaskPaneContainer.uiClassID,
        "org.jdesktop.swingx.plaf.basic.BasicTaskPaneContainerUI",
        "TaskPaneContainer.background",
        UIManager.getColor("Desktop.background")
      });
    }
    
    if (addon instanceof MetalLookAndFeelAddons) {
      addon.loadDefaults(new Object[]{
        "TaskPaneContainer.background",
        MetalLookAndFeel.getDesktopColor()
      });
    }
    
    if (addon instanceof WindowsLookAndFeelAddons) {     
      String xpStyle = OS.getWindowsVisualStyle();
      Object background;
      if ("homestead".equalsIgnoreCase(xpStyle)) {        
        background = new ColorUIResource(201, 215, 170);
      } else if ("metallic".equalsIgnoreCase(xpStyle)) {
        background = new ColorUIResource(192, 195, 209);
      } else {        
        background = new ColorUIResource(117, 150, 227);
      }      
      addon.loadDefaults(new Object[]{
        "TaskPaneContainer.background",
        background,
      });
    }
    
    if (addon instanceof WindowsClassicLookAndFeelAddons) {
      addon.loadDefaults(new Object[]{
        "TaskPaneContainer.background",
        UIManager.getColor("List.background")
      });      
    }
    
    if (addon instanceof AquaLookAndFeelAddons) {
      addon.loadDefaults(new Object[]{
        "TaskPaneContainer.background",
        new ColorUIResource(238, 238, 238),
      });            
    }
  }

  public void uninitialize(LookAndFeelAddons addon) {
  }

}
