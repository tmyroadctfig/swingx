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
import org.jdesktop.swingx.JXTable;
/**
 * Addon for <code>JXTable</code>.<br>
 *
 */
public class JXTableAddon implements ComponentAddon {

  public String getName() {
    return "JXTable";
  }

  public void initialize(LookAndFeelAddons addon) {
    // dummy call - ensure loading  
    addon.loadDefaults(new Object[] {  });
  }

  public void uninitialize(LookAndFeelAddons addon) {
  }

//  protected Object[] getDefaults() {
//      List defaults = new ArrayList();
//      defaults.addAll(Arrays.asList(new Object[] { 
//           JXTable.UIPREFIX + JXTable.HORIZONTALSCROLL_ACTION_COMMAND, "Horizontal Scroll",
//           JXTable.UIPREFIX + JXTable.PACKALL_ACTION_COMMAND, "Pack All Columns",
//           JXTable.UIPREFIX + JXTable.PACKSELECTED_ACTION_COMMAND, "Pack Selected Column"
//      }));
//      return defaults.toArray();
//      
//  }
}
