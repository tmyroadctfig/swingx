/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf.metal;

import org.jdesktop.swingx.plaf.basic.BasicLookAndFeelAddons;

/**
 * MetalLookAndFeelAddons.<br>
 *
 */
public class MetalLookAndFeelAddons extends BasicLookAndFeelAddons {

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
//        "DirectoryChooserUI",
//        "org.jdesktop.jdnc.swing.plaf.windows.WindowsDirectoryChooserUI",
    };
    return defaults;
  }
  
}
