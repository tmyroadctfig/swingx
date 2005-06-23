/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.plaf.metal.MetalLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsLookAndFeelAddons;
import org.jdesktop.swingx.util.OS;

/**
 * Addon for <code>JXTitledPanel</code>.<br>
 *
 */
public class JXTitledPanelAddon implements ComponentAddon {

  public String getName() {
    return "JXTitledPanel";
  }

//  public void initialize(LookAndFeelAddons addon) {
//    addon.loadDefaults(new Object[] {JXHyperlink.uiClassID,
//      "org.jdesktop.swingx.plaf.basic.BasicHyperlinkUI",});
//
////    if (addon instanceof WindowsLookAndFeelAddons) {
////      addon.loadDefaults(new Object[] {JXHyperlink.uiClassID,
////        "org.jdesktop.jdnc.plaf.windows.WindowsLinkButtonUI"});
////    }
//  }
//
//  public void uninitialize(LookAndFeelAddons addon) {
//  }
  public void initialize(LookAndFeelAddons addon) {
      addon.loadDefaults(getDefaults(addon));
    }

  public void uninitialize(LookAndFeelAddons addon) {
      addon.unloadDefaults(getDefaults(addon));
    }
  
    private Object[] getDefaults(LookAndFeelAddons addon) {
        List defaults = new ArrayList();
        defaults.addAll(Arrays.asList(new Object[] { 
           JXTitledPanel.uiClassID, "org.jdesktop.swingx.plaf.metal.MetalTitledPanelUI",
           "JXTitledPanel.title.font", UIManager.getFont("Button.font"),

                }));
        if (isMac(addon)) {
            addBasicDefaults(addon, defaults);
        } else if (isMotif(addon)) {
            addBasicDefaults(addon, defaults);
        } else if (isWindows(addon)) {
            addWindowsDefaults(addon, defaults);
        } else if (isMetal(addon)) {
            addMetalDefaults(addon, defaults);
        } else {
            addBasicDefaults(addon, defaults);
        }
        return defaults.toArray();
    }


    private void addBasicDefaults(LookAndFeelAddons addon, List defaults) {
        defaults.addAll(Arrays.asList(new Object[] { 
            "JXTitledPanel.title.foreground", new ColorUIResource(Color.WHITE),
            "JXTitledPanel.title.darkBackground", new ColorUIResource(Color.GRAY),
            "JXTitledPanel.title.lightBackground", new ColorUIResource(Color.LIGHT_GRAY),
        }));
        
    }

    private void addMetalDefaults(LookAndFeelAddons addon, List defaults) {
        if (isPlastic(addon)) {
        
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

    private void addWindowsDefaults(LookAndFeelAddons addon, List defaults) {
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

    private boolean isPlastic(LookAndFeelAddons addon) {
        return UIManager.getLookAndFeel().getClass().getName().contains("Plastic");
    }

    private boolean isMetal(LookAndFeelAddons addon) {
        return addon instanceof MetalLookAndFeelAddons;
    }

    private boolean isWindows(LookAndFeelAddons addon) {
        return addon instanceof WindowsLookAndFeelAddons;
    }
    
    private boolean isMotif(LookAndFeelAddons addon) {
        return UIManager.getLookAndFeel().getID().equals("Motif");
    }

    private boolean isMac(LookAndFeelAddons addon) {
        // TODO Auto-generated method stub
        return false;
    }
    


}
