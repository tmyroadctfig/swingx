/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf.windows;

import org.jdesktop.swingx.plaf.basic.BasicLookAndFeelAddons;

/**
 * Adds new pluggable UI following the Windows XP look and feel.
 */
public class WindowsLookAndFeelAddons extends BasicLookAndFeelAddons {

  public void initialize() {
    super.initialize();
    loadDefaults(getDefaults());
  }

  public void uninitialize() {
    super.uninitialize();
    unloadDefaults(getDefaults());
  }
  
  private Object[] getDefaults() {
    
    Object[] defaults =
      new Object[] {
//        "FontChooserUI",
//        "org.jdesktop.jdnc.plaf.windows.WindowsFontChooserUI",
//        "DirectoryChooserUI",
//        "org.jdesktop.jdnc.plaf.windows.WindowsDirectoryChooserUI",
        };
    return defaults;
  }

}
