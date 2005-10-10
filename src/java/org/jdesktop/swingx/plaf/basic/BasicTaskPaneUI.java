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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.icon.EmptyIcon;
import org.jdesktop.swingx.plaf.TaskPaneUI;

/**
 * Base implementation of the <code>JXTaskPane</code> UI.
 * 
 * @author <a href="mailto:fred@L2FProd.com">Frederic Lavigne</a>
 */
public class BasicTaskPaneUI extends TaskPaneUI {

  private static FocusListener focusListener = new RepaintOnFocus();

  public static ComponentUI createUI(JComponent c) {
    return new BasicTaskPaneUI();
  }

  protected static int TITLE_HEIGHT = 25;
  protected static int ROUND_HEIGHT = 5;
  
  protected JXTaskPane group;

  protected boolean mouseOver;
  protected MouseInputListener mouseListener;

  protected PropertyChangeListener propertyListener;
  
  public void installUI(JComponent c) {
    super.installUI(c);
    group = (JXTaskPane)c;

    installDefaults();
    installListeners();
    installKeyboardActions();
  }

  protected void installDefaults() {
    group.setOpaque(true);
    group.setBorder(createPaneBorder());
    ((JComponent)group.getContentPane()).setBorder(createContentPaneBorder());

    LookAndFeel.installColorsAndFont(
      group,
      "TaskPane.background",
      "TaskPane.foreground",
      "TaskPane.font");

    LookAndFeel.installColorsAndFont(
      (JComponent)group.getContentPane(),
      "TaskPane.background",
      "TaskPane.foreground",
      "TaskPane.font");    
  }

  protected void installListeners() {
    mouseListener = createMouseInputListener();
    group.addMouseMotionListener(mouseListener);
    group.addMouseListener(mouseListener);

    group.addFocusListener(focusListener);
    propertyListener = createPropertyListener();
    group.addPropertyChangeListener(propertyListener);
  }

  protected void installKeyboardActions() {
    InputMap inputMap = (InputMap)UIManager.get("TaskPane.focusInputMap");
    if (inputMap != null) {
      SwingUtilities.replaceUIInputMap(
        group,
        JComponent.WHEN_FOCUSED,
        inputMap);
    }

    ActionMap map = getActionMap();
    if (map != null) {
      SwingUtilities.replaceUIActionMap(group, map);
    }
  }

  ActionMap getActionMap() {
    ActionMap map = new ActionMapUIResource();
    map.put("toggleExpanded", new ToggleExpandedAction());
    return map;
  }

  public void uninstallUI(JComponent c) {
    uninstallListeners();
    super.uninstallUI(c);
  }

  protected void uninstallListeners() {
    group.removeMouseListener(mouseListener);
    group.removeMouseMotionListener(mouseListener);
    group.removeFocusListener(focusListener);
    group.removePropertyChangeListener(propertyListener);
  }

  protected MouseInputListener createMouseInputListener() {
    return new ToggleListener();
  }

  protected PropertyChangeListener createPropertyListener() {
    return new ChangeListener();
  }
  
  protected boolean isInBorder(MouseEvent event) {
    return event.getY() < getTitleHeight();
  }

  protected final int getTitleHeight() {
    return TITLE_HEIGHT;
  }

  protected Border createPaneBorder() {
    return new PaneBorder();
  }

  @Override
  public Dimension getPreferredSize(JComponent c) {
    Component component = group.getComponent(0);
    if (!(component instanceof JXCollapsiblePane)) {
      // something wrong in this JXTaskPane
      return super.getPreferredSize(c);
    }
    
    JXCollapsiblePane collapsible = (JXCollapsiblePane)component;
    Dimension dim = collapsible.getPreferredSize();
    
    Border groupBorder = group.getBorder();
    if (groupBorder instanceof PaneBorder) {
      Dimension border = ((PaneBorder)groupBorder).getPreferredSize(group);
      dim.width = Math.max(dim.width, border.width);
      dim.height += border.height;
    } else {
      dim.height += getTitleHeight();
    }      
    
    return dim;
  }
  
  protected Border createContentPaneBorder() {
    Color borderColor = UIManager.getColor("TaskPane.borderColor");
    return new CompoundBorder(new ContentPaneBorder(borderColor), BorderFactory
      .createEmptyBorder(10, 10, 10, 10));
  }
  
  public Component createAction(Action action) {
    JXHyperlink button = new JXHyperlink(action);
    button.setOpaque(false);
    button.setBorder(null);
    button.setBorderPainted(false);
    button.setFocusPainted(true);
    button.setForeground(UIManager.getColor("TaskPane.titleForeground"));
    return button;
  }

  protected void ensureVisible() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        group.scrollRectToVisible(
          new Rectangle(group.getWidth(), group.getHeight()));
      }
    });
  }
  
  static class RepaintOnFocus implements FocusListener {
    public void focusGained(FocusEvent e) {
      e.getComponent().repaint();
    }
    public void focusLost(FocusEvent e) {
      e.getComponent().repaint();
    }
  }
  
  class ChangeListener implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent evt) {
      // if group is expanded but not animated
      // or if animated has reached expanded state
      // scroll to visible if scrollOnExpand is enabled
      if ((JXTaskPane.EXPANDED_CHANGED_KEY.equals(evt.getPropertyName())
        && Boolean.TRUE.equals(evt.getNewValue()) && !group.isAnimated())
        || (JXCollapsiblePane.ANIMATION_STATE_KEY.equals(evt.getPropertyName()) && "expanded"
          .equals(evt.getNewValue()))) {
        if (group.isScrollOnExpand()) {
          ensureVisible();
        }
      }
    }
  }
  
  class ToggleListener extends MouseInputAdapter {
    public void mouseEntered(MouseEvent e) {
      if (isInBorder(e)) {
        e.getComponent().setCursor(
          Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      } else {
        mouseOver = false;
        group.repaint();
      }
    }
    public void mouseExited(MouseEvent e) {
      e.getComponent().setCursor(Cursor.getDefaultCursor());
      mouseOver = false;
      group.repaint();
    }
    public void mouseMoved(MouseEvent e) {
      if (isInBorder(e)) {
        e.getComponent().setCursor(
          Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        mouseOver = true;
        group.repaint();
      } else {
        e.getComponent().setCursor(Cursor.getDefaultCursor());
        mouseOver = false;
        group.repaint();
      }
    }
    public void mouseReleased(MouseEvent e) {
      if (isInBorder(e)) {
        group.setExpanded(!group.isExpanded());
      }
    }
  }
  
  class ToggleExpandedAction extends AbstractAction {
    public ToggleExpandedAction() {
      super("toggleExpanded");
    }
    public void actionPerformed(ActionEvent e) {
      group.setExpanded(!group.isExpanded());
    }
    public boolean isEnabled() {
      return group.isVisible();
    }
  }

  protected static class ChevronIcon implements Icon {
    boolean up = true;
    public ChevronIcon(boolean up) {
      this.up = up;
    }
    public int getIconHeight() {
      return 3;
    }
    public int getIconWidth() {
      return 6;
    }
    public void paintIcon(Component c, Graphics g, int x, int y) {
      if (up) {
        g.drawLine(x + 3, y, x, y + 3);
        g.drawLine(x + 3, y, x + 6, y + 3);
      } else {
        g.drawLine(x, y, x + 3, y + 3);
        g.drawLine(x + 3, y + 3, x + 6, y);
      }
    }
  }

  /**
   * The border around the content pane
   */
  protected static class ContentPaneBorder implements Border {
    Color color;
    public ContentPaneBorder(Color color) {
      this.color = color;
    }
    public Insets getBorderInsets(Component c) {
      return new Insets(0, 1, 1, 1);
    }
    public boolean isBorderOpaque() {
      return true;
    }
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      g.setColor(color);
      g.drawLine(x, y, x, y + height - 1);
      g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);
      g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);
    }
  }
  
  /**
   * The border of the taskpane group paints the "text", the "icon", the
   * "expanded" status and the "special" type.
   *  
   */
  protected class PaneBorder implements Border {

    protected Color borderColor;
    protected Color titleForeground;
    protected Color specialTitleBackground;
    protected Color specialTitleForeground;
    protected Color titleBackgroundGradientStart;
    protected Color titleBackgroundGradientEnd;

    protected Color titleOver;
    protected Color specialTitleOver;
    
    protected JLabel label;
    
    public PaneBorder() {
      borderColor = UIManager.getColor("TaskPane.borderColor");      

      titleForeground = UIManager.getColor("TaskPane.titleForeground");

      specialTitleBackground = UIManager
        .getColor("TaskPane.specialTitleBackground");
      specialTitleForeground = UIManager
        .getColor("TaskPane.specialTitleForeground");

      titleBackgroundGradientStart = UIManager
        .getColor("TaskPane.titleBackgroundGradientStart");
      titleBackgroundGradientEnd = UIManager
        .getColor("TaskPane.titleBackgroundGradientEnd");
      
      titleOver = UIManager.getColor("TaskPane.titleOver");
      if (titleOver == null) {
        titleOver = specialTitleBackground.brighter();
      }
      specialTitleOver = UIManager.getColor("TaskPane.specialTitleOver");
      if (specialTitleOver == null) {
        specialTitleOver = specialTitleBackground.brighter();
      }
      
      label = new JLabel();
      label.setOpaque(false);
      label.setIconTextGap(8);
    }
    
    public Insets getBorderInsets(Component c) {
      return new Insets(getTitleHeight(), 0, 0, 0);
    }

    public boolean isBorderOpaque() {
      return true;
    }

    /**
     * Calculates the preferred border size, its size so all its content fits.
     */
    public Dimension getPreferredSize(JXTaskPane group) {
      // calculate the title width so it is fully visible
      // it starts with the title width
      configureLabel(group);
      Dimension dim = label.getPreferredSize();
      // add the title left offset
      dim.width += 3;
      // add the controls width
      dim.width += TITLE_HEIGHT;
      // and some space between label and controls
      dim.width += 3;
      
      dim.height = getTitleHeight();
      return dim;
    }
    
    protected void paintTitleBackground(JXTaskPane group, Graphics g) {
      if (group.isSpecial()) {
        g.setColor(specialTitleBackground);
      } else {
        g.setColor(titleBackgroundGradientStart);
      }
      g.fillRect(0, 0, group.getWidth(), getTitleHeight() - 1);
    }

    protected void paintTitle(
      JXTaskPane group,
      Graphics g,
      Color textColor,
      int x,
      int y,
      int width,
      int height) {
      configureLabel(group);
      label.setForeground(textColor);
      g.translate(x, y);
      label.setBounds(0, 0, width, height);
      label.paint(g);
      g.translate(-x, -y);
    }

    protected void configureLabel(JXTaskPane group) {
      label.applyComponentOrientation(group.getComponentOrientation());
      label.setFont(group.getFont());
      label.setText(group.getTitle());
      label.setIcon(
        group.getIcon() == null ? new EmptyIcon() : group.getIcon());      
    }
    
    protected void paintExpandedControls(JXTaskPane group, Graphics g, int x,
      int y, int width, int height) {}

    protected Color getPaintColor(JXTaskPane group) {
      Color paintColor;
      if (isMouseOverBorder()) {
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
      } else {
        if (group.isSpecial()) {
          paintColor = specialTitleForeground;
        } else {
          paintColor = titleForeground;
        }
      }
      return paintColor;
    }
    
    public void paintBorder(
      Component c,
      Graphics g,
      int x,
      int y,
      int width,
      int height) {

      JXTaskPane group = (JXTaskPane)c;

      // calculate position of title and toggle controls
      int controlWidth = TITLE_HEIGHT - 2 * ROUND_HEIGHT;
      int controlX = group.getWidth() - TITLE_HEIGHT;
      int controlY = ROUND_HEIGHT - 1;
      int titleX = 3;
      int titleY = 0;
      int titleWidth = group.getWidth() - getTitleHeight() - 3;
      int titleHeight = getTitleHeight();
      
      if (!group.getComponentOrientation().isLeftToRight()) {
        controlX = group.getWidth() - controlX - controlWidth;        
        titleX = group.getWidth() - titleX - titleWidth;
      }
      
      // paint the title background
      paintTitleBackground(group, g);

      // paint the the toggles
      paintExpandedControls(group, g, controlX, controlY, controlWidth,
        controlWidth);

      // paint the title text and icon
      Color paintColor = getPaintColor(group);

      // focus painted same color as text
      if (group.hasFocus()) {
        g.setColor(paintColor);
        BasicGraphicsUtils.drawDashedRect(
          g,
          3,
          3,
          width - 6,
          getTitleHeight() - 6);
      }

      paintTitle(
        group,
        g,
        paintColor,
        titleX,
        titleY,
        titleWidth,
        titleHeight);
    }
    
    protected void paintRectAroundControls(JXTaskPane group, Graphics g, int x,
      int y, int width, int height, Color highColor, Color lowColor) {      
      if (mouseOver) {
        int x2 = x + width;
        int y2 = y + height;
        g.setColor(highColor);
        g.drawLine(x, y, x2, y);
        g.drawLine(x, y, x, y2);
        g.setColor(lowColor);
        g.drawLine(x2, y, x2, y2);
        g.drawLine(x, y2, x2, y2);
      }
    }
    
    protected void paintOvalAroundControls(JXTaskPane group, Graphics g, int x,
      int y, int width, int height) {      
      if (group.isSpecial()) {
        g.setColor(specialTitleBackground.brighter());
        g.drawOval(
          x,
          y,
          width,
          height);
      } else {
        g.setColor(titleBackgroundGradientStart);
        g.fillOval(
          x,
          y,
          width,
          height);

        g.setColor(titleBackgroundGradientEnd.darker());
        g.drawOval(
          x,
          y,
          width,
          width);
      }
    }
    
    protected void paintChevronControls(JXTaskPane group, Graphics g, int x,
      int y, int width, int height) {      
      ChevronIcon chevron;
      if (group.isExpanded()) {
        chevron = new ChevronIcon(true);
      } else {
        chevron = new ChevronIcon(false);
      }
      int chevronX = x + width / 2 - chevron.getIconWidth() / 2;
      int chevronY = y + (height / 2 - chevron.getIconHeight());
      chevron.paintIcon(group, g, chevronX, chevronY);
      chevron.paintIcon(
        group,
        g,
        chevronX,
        chevronY + chevron.getIconHeight() + 1);
    }
    
    /**
     * Default implementation returns false.
     *  
     * @return true if this border wants to display things differently when the
     *         mouse is over it
     */
    protected boolean isMouseOverBorder() {
      return false;
    }
  }

}
