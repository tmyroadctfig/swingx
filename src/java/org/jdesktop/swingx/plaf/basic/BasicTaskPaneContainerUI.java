/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.swingx.plaf.basic;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

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
 * 
 * @author <a href="mailto:fred@L2FProd.com">Frederic Lavigne</a>
 */
public class BasicTaskPaneContainerUI extends TaskPaneContainerUI {

  public static ComponentUI createUI(JComponent c) {
    return new BasicTaskPaneContainerUI();
  }

  protected JXTaskPaneContainer taskPane;
  protected boolean useGradient;
  protected Color gradientStart;
  protected Color gradientEnd;

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
    
    useGradient = UIManager.getBoolean("TaskPaneContainer.useGradient");
    if (useGradient) {
      gradientStart = UIManager
      .getColor("TaskPaneContainer.backgroundGradientStart");
      gradientEnd = UIManager
      .getColor("TaskPaneContainer.backgroundGradientEnd");
    }
  }

  @Override
  public void paint(Graphics g, JComponent c) {
    Graphics2D g2d = (Graphics2D)g;
    if (useGradient) {
      Paint old = g2d.getPaint();
      GradientPaint gradient = new GradientPaint(0, 0, gradientStart, 0, c
        .getHeight(), gradientEnd);
      g2d.setPaint(gradient);
      g.fillRect(0, 0, c.getWidth(), c.getHeight());      
      g2d.setPaint(old);
    }
  }

}
