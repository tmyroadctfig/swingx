/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

import java.util.List;

import org.jdesktop.swingx.JXHyperlink;

/**
 * Addon for <code>JXHyperlink</code>.<br>
 *
 */
public class JXHyperlinkAddon extends AbstractComponentAddon {

  public JXHyperlinkAddon() {
    super("JXHyperlink");
  }

  @Override
  protected void addBasicDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    defaults.add(JXHyperlink.uiClassID);
    if (isMetal(addon)) {
      defaults.add("org.jdesktop.swingx.plaf.basic.BasicHyperlinkUI");
    } else {
      defaults.add("org.jdesktop.swingx.plaf.windows.WindowsHyperlinkUI");
    }
  }

}
