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

import org.jdesktop.swingx.table.ColumnHeaderRenderer;
import org.jdesktop.swingx.util.OS;

/**
 * Addon for ColumnHeaderRenderer.<p>
 * Loads LF specific sort icons.
 * 
 * @author Jeanette Winzenburg
 *
 */
public class ColumnHeaderRendererAddon extends AbstractComponentAddon {
    
  public ColumnHeaderRendererAddon() {
    super("ColumnHeaderRenderer");
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
      } else if (isSynth()) {
          upIcon = "sort-gtk-up.png";
          downIcon = "sort-gtk-dn.png";
          
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
      return OS.isWindowsXP();
  }

}
