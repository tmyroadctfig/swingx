/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf.basic;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;

import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.plaf.TaskPaneContainerUI;

/**
 * Base implementation of the <code>JXTaskPaneContainer</code> UI.
 */
public class BasicTaskPaneContainerUI extends TaskPaneContainerUI {

  public static ComponentUI createUI(JComponent c) {
    return new BasicTaskPaneContainerUI();
  }

  protected JXTaskPaneContainer taskPane;

  public void installUI(JComponent c) {
    super.installUI(c);
    taskPane = (JXTaskPaneContainer)c;
    taskPane.setLayout(new VerticalLayout(14));
    taskPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
    taskPane.setOpaque(true);

    if (taskPane.getBackground() == null
      || taskPane.getBackground() instanceof ColorUIResource) {
      taskPane
        .setBackground(UIManager.getColor("TaskPaneContainer.background"));
    }
  }

}
