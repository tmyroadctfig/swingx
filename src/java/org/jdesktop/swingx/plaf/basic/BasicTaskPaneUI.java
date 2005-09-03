/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
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

  protected int getTitleHeight() {
    return 25;
  }

  protected Border createPaneBorder() {
    return new PaneBorder();
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

  protected static int getTitleHeight(Component c) {
    return ((BasicTaskPaneUI) ((JXTaskPane)c).getUI())
      .getTitleHeight();
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
  protected static class PaneBorder implements Border {

    protected Color borderColor;
    protected Color titleForeground;
    protected Color specialTitleBackground;
    protected Color specialTitleForeground;
    protected Color titleBackgroundGradientStart;
    protected Color titleBackgroundGradientEnd;

    protected Color titleOver;
    protected Color specialTitleOver;
    
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
    }
    
    public Insets getBorderInsets(Component c) {
      return new Insets(getTitleHeight(c), 0, 0, 0);
    }

    public boolean isBorderOpaque() {
      return true;
    }

    protected void paintTitleBackground(JXTaskPane group, Graphics g) {
      if (group.isSpecial()) {
        g.setColor(specialTitleBackground);
      } else {
        g.setColor(titleBackgroundGradientStart);
      }
      g.fillRect(0, 0, group.getWidth(), getTitleHeight(group) - 1);
    }

    protected void paintTitle(
      JXTaskPane group,
      Graphics g,
      Color textColor,
      int x,
      int y,
      int width,
      int height) {
      JLabel label = new JLabel();
      label.setOpaque(false);
      label.setForeground(textColor);
      label.setFont(g.getFont());
      label.setIconTextGap(8);
      label.setText(group.getTitle());
      label.setIcon(
        group.getIcon() == null ? new EmptyIcon() : group.getIcon());
      g.translate(x, y);
      label.setBounds(0, 0, width, height);
      label.paint(g);
      g.translate(-x, -y);
    }

    protected void paintExpandedControls(JXTaskPane group, Graphics g) {
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
      if (group.isSpecial()) {
        paintColor = specialTitleForeground;
      } else {
        paintColor = titleForeground;
      }

      // focus painted same color as text
      if (group.hasFocus()) {
        g.setColor(paintColor);
        BasicGraphicsUtils.drawDashedRect(
          g,
          3,
          3,
          width - 6,
          getTitleHeight(c) - 6);
      }

      paintTitle(
        group,
        g,
        paintColor,
        3,
        0,
        c.getWidth() - getTitleHeight(c) - 3,
        getTitleHeight(c));
    }
  }

}
