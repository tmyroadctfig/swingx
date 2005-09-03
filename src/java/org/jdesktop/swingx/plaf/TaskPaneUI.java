/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.plaf.PanelUI;

/**
 * Pluggable UI for <code>JXTaskPane</code>.
 *  
 * @author <a href="mailto:fred@L2FProd.com">Frederic Lavigne</a>
 */
public class TaskPaneUI extends PanelUI {

  /**
   * Called by the component when an action is added to the component through
   * the {@link org.jdesktop.swingx.JXTaskPane#add(Action)} method.
   * 
   * @param action
   * @return a component built from the action.
   */
  public Component createAction(Action action) {
    return new JButton(action);
  }

}
