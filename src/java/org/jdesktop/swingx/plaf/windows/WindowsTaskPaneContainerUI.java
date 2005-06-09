/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf.windows;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import org.jdesktop.swingx.plaf.basic.BasicTaskPaneContainerUI;

/**
 * Windows implementation of the TaskPaneContainerUI.
 */
public class WindowsTaskPaneContainerUI extends BasicTaskPaneContainerUI {

  public static ComponentUI createUI(JComponent c) {
    return new WindowsTaskPaneContainerUI();
  }

  public void installUI(JComponent c) {
    super.installUI(c);
  }
    
}
