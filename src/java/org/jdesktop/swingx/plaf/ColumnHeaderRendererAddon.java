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

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.jdesktop.swingx.plaf.metal.MetalLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsLookAndFeelAddons;
import org.jdesktop.swingx.table.ColumnHeaderRenderer;
/**
 * Addon for <code>JXTable</code>.<br>
 *
 */
public class ColumnHeaderRendererAddon implements ComponentAddon {
    
  public String getName() {
    return "ColumnHeaderRenderer";
  }

  public void initialize(LookAndFeelAddons addon) {
      List defaults = new ArrayList();
      String upIcon = null;
      String downIcon = null;
      if (isMac(addon)) {
          upIcon = "sort-osx-up.png";
          downIcon = "sort-osx-dn.png";
//      } else if (isMotif(addon)) {
//          
      } else if (isWindows(addon)) {
          if (isXP(addon)) {
              upIcon = "sort-xp-up.png";
              downIcon = "sort-xp-dn.png";
              
          } else {
              upIcon = "sort-w2k-up.png";
              downIcon = "sort-w2k-dn.png";
              
          }
//      } else if (isMetal(addon)) {
//          addMetalDefaults(addon, defaults);
      } else {
          upIcon = "sort-jlf-up.png";
          downIcon = "sort-jlf-dn.png";
      }
      defaults.addAll(Arrays.asList(new Object[] { 
              ColumnHeaderRenderer.UP_ICON_KEY, 
                  LookAndFeel.makeIcon(getClass(), "resources/" + upIcon),
              ColumnHeaderRenderer.DOWN_ICON_KEY, 
                  LookAndFeel.makeIcon(getClass(), "resources/" + downIcon),
      }));
      addon.loadDefaults(defaults.toArray());
  }


  public void uninitialize(LookAndFeelAddons addon) {
      List defaults = new ArrayList();
      defaults.addAll(Arrays.asList(new Object[] { 
              ColumnHeaderRenderer.UP_ICON_KEY, null,
              ColumnHeaderRenderer.DOWN_ICON_KEY,  null,
      }));
      addon.loadDefaults(defaults.toArray());
  }

  private boolean isXP(LookAndFeelAddons addon) {
      // TODO Auto-generated method stub
      return false;
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
