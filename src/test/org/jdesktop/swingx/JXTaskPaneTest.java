/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.BorderLayout;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.UIManager;

import org.jdesktop.swingx.icon.EmptyIcon;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.plaf.aqua.AquaLookAndFeelAddons;
import org.jdesktop.swingx.plaf.metal.MetalLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsClassicLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsLookAndFeelAddons;
import org.jdesktop.swingx.util.PropertyChangeReport;

public class JXTaskPaneTest extends InteractiveTestCase {

  public JXTaskPaneTest(String testTitle) {
    super(testTitle);
  }

  public void testBean() {
    PropertyChangeReport report = new PropertyChangeReport();
    JXTaskPane group = new JXTaskPane();
    group.setAnimated(false);
    group.addPropertyChangeListener(report);

    // ANIMATED PROPERTY
    group.setAnimated(true);
    assertTrue(group.isAnimated());
    assertEquals(JXTaskPane.ANIMATED_CHANGED_KEY, report.getLastEvent()
      .getPropertyName());
    assertTrue(report.getLastNewBooleanValue());

    group.setAnimated(false);
    assertFalse(group.isAnimated());
    assertFalse(report.getLastNewBooleanValue());

    UIManager.put("TaskPane.animate", Boolean.FALSE);
    JXTaskPane anotherGroup = new JXTaskPane();
    assertFalse(anotherGroup.isAnimated());

    UIManager.put("TaskPane.animate", null);
    anotherGroup = new JXTaskPane();
    assertTrue(anotherGroup.isAnimated());

    // TITLE
    group.setTitle("the title");
    assertEquals("the title", group.getTitle());
    assertEquals(JXTaskPane.TITLE_CHANGED_KEY, report.getLastEvent()
      .getPropertyName());
    assertEquals("the title", report.getLastNewValue());

    // ICON
    assertNull(group.getIcon());
    Icon icon = new EmptyIcon();
    group.setIcon(icon);
    assertNotNull(group.getIcon());
    assertEquals(JXTaskPane.ICON_CHANGED_KEY, report.getLastEvent()
      .getPropertyName());
    assertEquals(icon, report.getLastNewValue());
    group.setIcon(null);
    assertEquals(icon, report.getLastOldValue());
    assertNull(report.getLastNewValue());

    // SPECIAL
    assertFalse(group.isSpecial());
    group.setSpecial(true);
    assertTrue(group.isSpecial());
    assertEquals(JXTaskPane.SPECIAL_CHANGED_KEY, report.getLastEvent()
      .getPropertyName());
    assertTrue(report.getLastNewBooleanValue());
    assertFalse(report.getLastOldBooleanValue());

    // SCROLL ON EXPAND
    assertFalse(group.isScrollOnExpand());
    group.setScrollOnExpand(true);
    assertTrue(group.isScrollOnExpand());
    assertEquals(JXTaskPane.SCROLL_ON_EXPAND_CHANGED_KEY, report.getLastEvent()
      .getPropertyName());
    assertTrue(report.getLastNewBooleanValue());
    assertFalse(report.getLastOldBooleanValue());

    // EXPANDED
    assertTrue(group.isExpanded());
    group.setExpanded(false);
    assertFalse(group.isExpanded());
    assertEquals(JXTaskPane.EXPANDED_CHANGED_KEY, report.getLastEvent()
      .getPropertyName());
    assertFalse(report.getLastNewBooleanValue());
    assertTrue(report.getLastOldBooleanValue());
    
    try {
      JXTaskPaneBeanInfo beanInfo = new JXTaskPaneBeanInfo(); 
    } catch (Exception e) {
      throw new Error(e);
    }    
  }

  public void testContentPane() {
    JXTaskPane group = new JXTaskPane();
    assertEquals(0, group.getContentPane().getComponentCount());
    
    // Objects are not added to the taskPane but to its contentPane
    JButton button = new JButton();
    group.add(button);
    assertEquals(group.getContentPane(), button.getParent());
    group.remove(button);
    assertNull(button.getParent());
    assertEquals(0, group.getContentPane().getComponentCount());
    group.add(button);
    group.removeAll();
    assertEquals(0, group.getContentPane().getComponentCount());
    group.add(button);
    group.remove(0);
    assertEquals(0, group.getContentPane().getComponentCount());
    
    BorderLayout layout = new BorderLayout();
    group.setLayout(layout);
    assertEquals(layout, group.getContentPane().getLayout());
    assertFalse(layout == group.getLayout());
  }
  
  public void testAddon() throws Exception {
    // move around all addons
    JXTaskPane group = new JXTaskPane();
    LookAndFeelAddons.setAddon(AquaLookAndFeelAddons.class.getName());
    LookAndFeelAddons.setAddon(MetalLookAndFeelAddons.class.getName());
    LookAndFeelAddons.setAddon(WindowsLookAndFeelAddons.class.getName());
    LookAndFeelAddons.setAddon(WindowsClassicLookAndFeelAddons.class.getName());
  }
  
}
