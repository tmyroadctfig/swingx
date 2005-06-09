/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI;

/**
 * Windows implementation of the TaskPaneUI.
 */
public class WindowsTaskPaneUI extends BasicTaskPaneUI {

  public static ComponentUI createUI(JComponent c) {
    return new WindowsTaskPaneUI();
  }

  private static int TITLE_HEIGHT = 25;
  private static int ROUND_HEIGHT = 5;
    
  protected Border createPaneBorder() {
    return new XPPaneBorder();
  }
  
  /**
   * Overriden to paint the background of the component but keeping the rounded
   * corners.
   */
  public void update(Graphics g, JComponent c) {
    if (c.isOpaque()) {
      g.setColor(c.getParent().getBackground());
      g.fillRect(0, 0, c.getWidth(), c.getHeight());
      g.setColor(c.getBackground());
      g.fillRect(0, ROUND_HEIGHT, c.getWidth(), c.getHeight() - ROUND_HEIGHT);
    }
    paint(g, c);
  }

  protected int getTitleHeight() {
    return TITLE_HEIGHT;
  }

  /**
   * The border of the taskpane group paints the "text", the "icon", the
   * "expanded" status and the "special" type.
   *  
   */
  class XPPaneBorder extends PaneBorder {

    protected void paintTitleBackground(JXTaskPane group, Graphics g) {
      if (group.isSpecial()) {
        g.setColor(specialTitleBackground);
        g.fillRoundRect(
          0,
          0,
          group.getWidth(),
          ROUND_HEIGHT * 2,
          ROUND_HEIGHT,
          ROUND_HEIGHT);
        g.fillRect(
          0,
          ROUND_HEIGHT,
          group.getWidth(),
          TITLE_HEIGHT - ROUND_HEIGHT);
      } else {
        Paint oldPaint = ((Graphics2D)g).getPaint();
        GradientPaint gradient =
          new GradientPaint(
            0f,
            group.getWidth() / 2,
            titleBackgroundGradientStart,
            group.getWidth(),
            TITLE_HEIGHT,
            titleBackgroundGradientEnd);
        ((Graphics2D)g).setRenderingHint(
          RenderingHints.KEY_COLOR_RENDERING,
          RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        ((Graphics2D)g).setRenderingHint(
          RenderingHints.KEY_INTERPOLATION,
          RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        ((Graphics2D)g).setRenderingHint(
          RenderingHints.KEY_RENDERING,
          RenderingHints.VALUE_RENDER_QUALITY);
        ((Graphics2D)g).setPaint(gradient);
        g.fillRoundRect(
          0,
          0,
          group.getWidth(),
          ROUND_HEIGHT * 2,
          ROUND_HEIGHT,
          ROUND_HEIGHT);
        g.fillRect(
          0,
          ROUND_HEIGHT,
          group.getWidth(),
          TITLE_HEIGHT - ROUND_HEIGHT);
        ((Graphics2D)g).setPaint(oldPaint);
      }
    }

    protected void paintExpandedControls(JXTaskPane group, Graphics g) {
      ((Graphics2D)g).setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

      int ovalSize = TITLE_HEIGHT - 2 * ROUND_HEIGHT;

      if (group.isSpecial()) {
        g.setColor(specialTitleBackground.brighter());
        g.drawOval(
          group.getWidth() - TITLE_HEIGHT,
          ROUND_HEIGHT - 1,
          ovalSize,
          ovalSize);
      } else {
        g.setColor(titleBackgroundGradientStart);
        g.fillOval(
          group.getWidth() - TITLE_HEIGHT,
          ROUND_HEIGHT - 1,
          ovalSize,
          ovalSize);

        g.setColor(titleBackgroundGradientEnd.darker());
        g.drawOval(
          group.getWidth() - TITLE_HEIGHT,
          ROUND_HEIGHT - 1,
          ovalSize,
          ovalSize);
      }

      Color paintColor;
      if (mouseOver) {
        if (group.isSpecial()) {
          paintColor = specialTitleOver;
        } else {
          paintColor = titleOver;
        }
      } else {
        if (group.isSpecial()) {
          paintColor = specialTitleForeground;
        } else {
          paintColor = titleForeground;
        }
      }

      ChevronIcon chevron;
      if (group.isExpanded()) {
        chevron = new ChevronIcon(true);
      } else {
        chevron = new ChevronIcon(false);
      }
      int chevronX =
        group.getWidth()
          - TITLE_HEIGHT
          + ovalSize / 2
          - chevron.getIconWidth() / 2;
      int chevronY =
        ROUND_HEIGHT + (ovalSize / 2 - chevron.getIconHeight()) - 1;
      g.setColor(paintColor);
      chevron.paintIcon(group, g, chevronX, chevronY);
      chevron.paintIcon(
        group,
        g,
        chevronX,
        chevronY + chevron.getIconHeight() + 1);

      ((Graphics2D)g).setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    public void paintBorder(
      Component c,
      Graphics g,
      int x,
      int y,
      int width,
      int height) {

      JXTaskPane group = (JXTaskPane)c;

      // paint the title background
      paintTitleBackground(group, g);

      // paint the the toggles
      paintExpandedControls(group, g);

      // paint the title text and icon
      Color paintColor;
      if (mouseOver) {
        if (group.isSpecial()) {
          paintColor = specialTitleOver;
        } else {
          paintColor = titleOver;
        }
      } else {
        if (group.isSpecial()) {
          paintColor = specialTitleForeground;
        } else {
          paintColor = titleForeground;
        }
      }

      // focus painted same color as text
      if (group.hasFocus()) {
        g.setColor(paintColor);
        BasicGraphicsUtils.drawDashedRect(g, 3, 3, width - 6, TITLE_HEIGHT - 6);
      }
      
      paintTitle(
        group,
        g,
        paintColor,
        3,
        0,
        c.getWidth() - TITLE_HEIGHT - 3,
        TITLE_HEIGHT);
    }
  }

}
