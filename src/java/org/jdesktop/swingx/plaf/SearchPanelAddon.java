/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

/**
 * Addon for <code>JXTable</code>.<br>
 *
 */
public class SearchPanelAddon implements ComponentAddon {

  public String getName() {
    return "SearchPanel";
  }

  public void initialize(LookAndFeelAddons addon) {
    // dummy call - ensure loading  
    addon.loadDefaults(new Object[] {
//            testing order of loads/finding values in UIManager
//            "JXSearchPanel.matchCase", "rude overwrite"
    });
  }

  public void uninitialize(LookAndFeelAddons addon) {
  }

}
