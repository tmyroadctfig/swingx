/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jdesktop.swingx.plaf.JXTaskPaneAddon;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.plaf.TaskPaneUI;

/**
 * <code>JXTaskPane</code> is a container for tasks and other
 * arbitrary components. <code>JXTaskPane</code> s are added to
 * a {@link org.jdesktop.swingx.JXTaskPaneContainer}.
 * <code>JXTaskPane</code> provides control to be expanded and
 * collapsed in order to show or hide the task list. It can have an
 * <code>icon</code>, a <code>title</code> and can be marked as
 * <code>special</code>. Marking a <code>JXTaskPane</code> as
 * <code>special</code> is only an hint for the pluggable UI which
 * will usually paint it differently (by example by using another
 * color for the border of the pane).
 * 
 * When the JXTaskPane is expanded or collapsed, it will be
 * animated with a fade effect. The animated can be disabled on a per
 * component basis through {@link #setAnimated(boolean)}.
 * 
 * To disable the animation for all newly created <code>JXTaskPane</code>,
 * use the UIManager property:
 * <code>UIManager.put("TaskPane.animate", Boolean.FALSE);</code>.
 * 
 * @javabean.attribute
 *          name="isContainer"
 *          value="Boolean.TRUE"
 *          rtexpr="true"
 *          
 * @javabean.attribute
 *          name="containerDelegate"
 *          value="getContentPane"
 *          
 * @javabean.class
 *          name="JXTaskPane"
 *          shortDescription="JXTaskPane is a container for tasks and other arbitrary components."
 *          stopClass="java.awt.Component"
 * 
 * @javabean.icons
 *          mono16="JXTaskPane16-mono.gif"
 *          color16="JXTaskPane16.gif"
 *          mono32="JXTaskPane32-mono.gif"
 *          color32="JXTaskPane32.gif"
 */
public class JXTaskPane extends JPanel implements
  JXCollapsiblePane.JCollapsiblePaneContainer {

  public final static String uiClassID = "swingx/TaskPaneUI";
  
  // ensure at least the default ui is registered
  static {
    LookAndFeelAddons.contribute(new JXTaskPaneAddon());
  }

  /**
   * Used when generating PropertyChangeEvents for the "expanded" property
   */
  public static final String EXPANDED_CHANGED_KEY = "expanded";

  /**
   * Used when generating PropertyChangeEvents for the "scrollOnExpand" property
   */
  public static final String SCROLL_ON_EXPAND_CHANGED_KEY = "scrollOnExpand";

  /**
   * Used when generating PropertyChangeEvents for the "title" property
   */
  public static final String TITLE_CHANGED_KEY = "title";

  /**
   * Used when generating PropertyChangeEvents for the "icon" property
   */
  public static final String ICON_CHANGED_KEY = "icon";

  /**
   * Used when generating PropertyChangeEvents for the "special" property
   */
  public static final String SPECIAL_CHANGED_KEY = "special";

  /**
   * Used when generating PropertyChangeEvents for the "animated" property
   */
  public static final String ANIMATED_CHANGED_KEY = "animated";

  private String title;
  private Icon icon;
  private boolean special;
  private boolean expanded = true;
  private boolean scrollOnExpand;

  private JXCollapsiblePane collapsePane;
  
  /**
   * Creates a new empty <code>JXTaskPane</code>.
   */
  public JXTaskPane() {
    collapsePane = new JXCollapsiblePane();
    super.setLayout(new BorderLayout(0, 0));
    super.addImpl(collapsePane, BorderLayout.CENTER, -1);
    
    updateUI();
    setFocusable(true);
    setOpaque(false);

    // disable animation if specified in UIManager
    setAnimated(!Boolean.FALSE.equals(UIManager.get("TaskPane.animate")));
  }

  public Container getContentPane() {
    return collapsePane.getContentPane();
  }
  
  /**
   * Notification from the <code>UIManager</code> that the L&F has changed.
   * Replaces the current UI object with the latest version from the <code>UIManager</code>.
   * 
   * @see javax.swing.JComponent#updateUI
   */
  public void updateUI() {
    // collapsePane is null when updateUI() is called by the "super()"
    // constructor
    if (collapsePane == null) {
      return;
    }
    setUI((TaskPaneUI)LookAndFeelAddons.getUI(this, TaskPaneUI.class,
      UIManager.getUI(this)));
  }
  
  /**
   * Sets the L&F object that renders this component.
   * 
   * @param ui the <code>TaskPaneUI</code> L&F object
   * @see javax.swing.UIDefaults#getUI
   * 
   * @beaninfo bound: true hidden: true description: The UI object that
   * implements the taskpane group's LookAndFeel.
   */
  public void setUI(TaskPaneUI ui) {
    super.setUI(ui);
  }

  /**
   * Returns the name of the L&F class that renders this component.
   * 
   * @return the string {@link #uiClassID}
   * @see javax.swing.JComponent#getUIClassID
   * @see javax.swing.UIDefaults#getUI
   */
  public String getUIClassID() {
    return uiClassID;
  }

  /**
   * Returns the title currently displayed in the border of this pane.
   * 
   * @return the title currently displayed in the border of this pane
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title to be displayed in the border of this pane.
   * 
   * @param title the title to be displayed in the border of this pane
   * @javabean.property
   *          bound="true"
   *          preferred="true"
   */
  public void setTitle(String title) {
    String old = this.title;
    this.title = title;
    firePropertyChange(TITLE_CHANGED_KEY, old, title);
  }

  /**
   * Returns the icon currently displayed in the border of this pane.
   * 
   * @return the icon currently displayed in the border of this pane
   */
  public Icon getIcon() {
    return icon;
  }

  /**
   * Sets the icon to be displayed in the border of this pane. Some pluggable
   * UIs may impose size constraints for the icon. A size of 16x16 pixels is
   * the recommended icon size.
   * 
   * @param icon the icon to be displayed in the border of this pane
   * @javabean.property
   *          bound="true"
   *          preferred="true"
   */
  public void setIcon(Icon icon) {
    Icon old = this.icon;
    this.icon = icon;
    firePropertyChange(ICON_CHANGED_KEY, old, icon);
  }

  /**
   * Returns true if this pane is "special".
   * 
   * @return true if this pane is "special"
   */
  public boolean isSpecial() {
    return special;
  }

  /**
   * Sets this pane to be "special" or not.
   * 
   * @param special true if this pane is "special", false otherwise
   * @javabean.property
   *          bound="true"
   *          preferred="true"
   */
  public void setSpecial(boolean special) {
    if (this.special != special) {
      this.special = special;
      firePropertyChange(SPECIAL_CHANGED_KEY, !special, special);
    }
  }

  /**
   * Should this group be scrolled to be visible on expand.
   * 
   * 
   * @param scrollOnExpand true to scroll this group to be
   * visible if this group is expanded.
   * 
   * @see #setExpanded(boolean)
   * 
   * @javabean.property
   *          bound="true"
   *          preferred="true"
   */
  public void setScrollOnExpand(boolean scrollOnExpand) {
    if (this.scrollOnExpand != scrollOnExpand) {
      this.scrollOnExpand = scrollOnExpand;
      firePropertyChange(SCROLL_ON_EXPAND_CHANGED_KEY,
        !scrollOnExpand, scrollOnExpand);
    }
  }
  
  /**
   * Should this group scroll to be visible after
   * this group was expanded.
   * 
   * @return true if we should scroll false if nothing
   * should be done.
   */
  public boolean isScrollOnExpand() {
    return scrollOnExpand;
  }
  
  /**
   * Expands or collapses this group.
   * 
   * @param expanded true to expand the group, false to collapse it
   * @javabean.property
   *          bound="true"
   *          preferred="true"
   */
  public void setExpanded(boolean expanded) {
    if (this.expanded != expanded) {
      this.expanded = expanded;
      collapsePane.setCollapsed(!expanded);
      firePropertyChange(EXPANDED_CHANGED_KEY, !expanded, expanded);
    }
  }

  /**
   * Returns true if this taskpane is expanded, false if it is collapsed.
   * 
   * @return true if this taskpane is expanded, false if it is collapsed.
   */
  public boolean isExpanded() {
    return expanded;
  }

  /**
   * Enables or disables animation during expand/collapse transition.
   * 
   * @param animated
   * @javabean.property
   *          bound="true"
   *          preferred="true"
   */
  public void setAnimated(boolean animated) {
    if (isAnimated() != animated) {
      collapsePane.setAnimated(animated);
      firePropertyChange(ANIMATED_CHANGED_KEY, !isAnimated(), isAnimated());
    }
  }
  
  /**
   * @return true if this taskpane is animated during expand/collapse
   *         transition.
   */
  public boolean isAnimated() {
    return collapsePane.isAnimated();
  }
  
  /**
   * Adds an action to this <code>JXTaskPane</code>. Returns a
   * component built from the action. The returned component has been
   * added to the <code>JXTaskPane</code>.
   * 
   * @param action
   * @return a component built from the action
   */
  public Component add(Action action) {
    Component c = ((TaskPaneUI)ui).createAction(action);
    add(c);
    return c;
  }

  public Container getValidatingContainer() {
    return getParent();
  }
  
  protected void addImpl(Component comp, Object constraints, int index) {
    getContentPane().add(comp, constraints, index);
  }

  public void setLayout(LayoutManager mgr) {
    if (collapsePane != null) {
      getContentPane().setLayout(mgr);
    }
  }
  
  /**
   * Overriden to redirect call to the content pane
   */
  public void remove(Component comp) {
    getContentPane().remove(comp);
  }

  /**
   * Overriden to redirect call to the content pane.
   */
  public void remove(int index) {
    getContentPane().remove(index);
  }
  
  /**
   * Overriden to redirect call to the content pane.
   */
  public void removeAll() {
    getContentPane().removeAll();
  }
  
  /**
   * @see JComponent#paramString()
   */
  protected String paramString() {
    return super.paramString()
      + ",title="
      + getTitle()
      + ",icon="
      + getIcon()
      + ",expanded="
      + String.valueOf(isExpanded())
      + ",special="
      + String.valueOf(isSpecial())
      + ",ui=" + getUI();
  }

}
