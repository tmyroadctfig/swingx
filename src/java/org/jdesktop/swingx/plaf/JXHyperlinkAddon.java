/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

import org.jdesktop.swingx.JXHyperlink;

/**
 * Addon for <code>JXHyperlink</code>.<br>
 *
 */
public class JXHyperlinkAddon implements ComponentAddon {

  public String getName() {
    return "JXHyperlink";
  }

  public void initialize(LookAndFeelAddons addon) {
    addon.loadDefaults(new Object[] {JXHyperlink.uiClassID,
      "org.jdesktop.swingx.plaf.basic.BasicHyperlinkUI",});

//    if (addon instanceof WindowsLookAndFeelAddons) {
//      addon.loadDefaults(new Object[] {JXHyperlink.uiClassID,
//        "org.jdesktop.jdnc.plaf.windows.WindowsLinkButtonUI"});
//    }
  }

  public void uninitialize(LookAndFeelAddons addon) {
  }

}
