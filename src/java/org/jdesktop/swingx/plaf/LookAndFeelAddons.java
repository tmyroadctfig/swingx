/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

import org.jdesktop.swingx.plaf.aqua.AquaLookAndFeelAddons;
import org.jdesktop.swingx.plaf.metal.MetalLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsClassicLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsLookAndFeelAddons;
import org.jdesktop.swingx.util.OS;

/**
 * Provides additional pluggable UI for new components added by the
 * library. By default, the library uses the pluggable UI returned by
 * {@link #getBestMatchAddonClassName()}.
 * <p>
 * The default addon can be configured using the
 * <code>swing.addon</code> system property as follow:
 * <ul>
 * <li>on the command line,
 * <code>java -Dswing.addon=ADDONCLASSNAME ...</code></li>
 * <li>at runtime and before using the library components
 * <code>System.getProperties().put("swing.addon", ADDONCLASSNAME);</code>
 * </li>
 * </ul>
 * <p>
 * The addon can also be installed directly by calling the
 * {@link #setAddon(String)}method. For example, to install the
 * Windows addons, add the following statement
 * <code>LookAndFeelAddons.setAddon("org.jdesktop.swingx.plaf.windows.WindowsLookAndFeelAddons");</code>.
 */
public class LookAndFeelAddons {

  private static List contributedComponents = new ArrayList();

  static {
    // load the default addon
    String addonClassname = getBestMatchAddonClassName();
    try {
      addonClassname = System.getProperty("swing.addon", addonClassname);
    } catch (SecurityException e) {
      // security exception may arise in Java Web Start
    }

    try {
      setAddon(addonClassname);
      setTrackingLookAndFeelChanges(true);
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  private static LookAndFeelAddons currentAddon;

  public void initialize() {
    for (Iterator iter = contributedComponents.iterator(); iter.hasNext();) {
      ComponentAddon addon = (ComponentAddon)iter.next();
      addon.initialize(this);
    }
  }

  public void uninitialize() {
    for (Iterator iter = contributedComponents.iterator(); iter.hasNext();) {
      ComponentAddon addon = (ComponentAddon)iter.next();
      addon.uninitialize(this);
    }
  }

  /**
   * Adds the given defaults in UIManager.
   * 
   * @param keysAndValues
   */
  public void loadDefaults(Object[] keysAndValues) {
    for (int i = 0, c = keysAndValues.length; i < c; i = i + 2) {
      UIManager.put(keysAndValues[i], keysAndValues[i + 1]);
    }
  }

  public void unloadDefaults(Object[] keysAndValues) {
    for (int i = 0, c = keysAndValues.length; i < c; i = i + 2) {
      UIManager.put(keysAndValues[i], null);
    }
  }

  public static void setAddon(String addonClassName)
    throws InstantiationException, IllegalAccessException,
    ClassNotFoundException {
    setAddon(Class.forName(addonClassName));
  }

  public static void setAddon(Class addonClass) throws InstantiationException,
    IllegalAccessException {
    LookAndFeelAddons addon = (LookAndFeelAddons)addonClass.newInstance();
    setAddon(addon);
  }
   
  public static void setAddon(LookAndFeelAddons addon) {
    if (currentAddon != null) {
      currentAddon.uninitialize();
    }

    addon.initialize();
    currentAddon = addon;
  }

  public static LookAndFeelAddons getAddon() {
    return currentAddon;
  }

  /**
   * Based on the current look and feel (as returned by
   * <code>UIManager.getLookAndFeel()</code>), this method returns
   * the name of the closest <code>LookAndFeelAddons</code> to use.
   * 
   * @return the addon matching the currently installed look and feel
   */
  public static String getBestMatchAddonClassName() {
    String lnf = UIManager.getLookAndFeel().getClass().getName();
    String addon;
    if (UIManager.getCrossPlatformLookAndFeelClassName().equals(lnf)) {
      addon = MetalLookAndFeelAddons.class.getName();
    } else if (UIManager.getSystemLookAndFeelClassName().equals(lnf)) {
      addon = getSystemAddonClassName();
    } else if ("com.sun.java.swing.plaf.windows.WindowsLookAndFeel".equals(lnf) ||
      "com.jgoodies.looks.windows.WindowsLookAndFeel".equals(lnf)) {
      if (OS.isUsingWindowsVisualStyles()) {
        addon = WindowsLookAndFeelAddons.class.getName();
      } else {
        addon = WindowsClassicLookAndFeelAddons.class.getName();
      }
    } else if ("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel"
      .equals(lnf)) {
      addon = WindowsClassicLookAndFeelAddons.class.getName();
    } else {
      addon = getSystemAddonClassName();
    }
    return addon;
  }

  /**
   * Gets the addon best suited for the operating system where the
   * virtual machine is running.
   * 
   * @return the addon matching the native operating system platform.
   */
  public static String getSystemAddonClassName() {
    String addon = WindowsClassicLookAndFeelAddons.class.getName();

    if (OS.isMacOSX()) {
      // on Mac OS X, use the class Windows style. This one does not
      // have the bug where "special" TaskPaneGroups are not
      // correctly painted.
      addon = AquaLookAndFeelAddons.class.getName();
    } else if (OS.isWindows()) {
      // see whether of not visual styles are used
      if (OS.isUsingWindowsVisualStyles()) {
        addon = WindowsLookAndFeelAddons.class.getName();
      } else {
        addon = WindowsClassicLookAndFeelAddons.class.getName();
      }
    }

    return addon;
  }

  /**
   * Each new component added by the library will contribute its
   * default UI classes, colors and fonts to the LookAndFeelAddons.
   * See {@link ComponentAddon}.
   * 
   * @param component
   */
  public static void contribute(ComponentAddon component) {
    contributedComponents.add(component);

    if (currentAddon != null) {
      // make sure to initialize any addons added after the
      // LookAndFeelAddons has been installed
      component.initialize(currentAddon);
    }
  }

  /**
   * Removes the contribution of the given addon
   * 
   * @param component
   */
  public static void uncontribute(ComponentAddon component) {
    contributedComponents.remove(component);
    
    if (currentAddon != null) {
      component.uninitialize(currentAddon);
    }
  }

  /**
   * Workaround for IDE mixing up with classloaders (like netbeans).
   * Consider this method as API private. It must not be called
   * directly.
   * 
   * @param p_Component
   * @return an instance of p_ExpectedUIClass 
   */
  public static ComponentUI getUI(JComponent p_Component,
    Class p_ExpectedUIClass, ComponentUI p_CandidateUI) {
    if (p_ExpectedUIClass.isInstance(p_CandidateUI)) {
      return p_CandidateUI;
    } else {
      String realUI = p_CandidateUI.getClass().getName();
      Class realUIClass;
      try {
        realUIClass = p_ExpectedUIClass.getClassLoader()
        .loadClass(realUI);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("Failed to load class " + realUI, e);
      }
      Method createUIMethod = null;
      try {
        createUIMethod = realUIClass.getMethod("createUI", new Class[]{JComponent.class});
      } catch (NoSuchMethodException e1) {
        throw new RuntimeException("Class " + realUI + " has no method createUI(JComponent)");
      }
      try {
        return (ComponentUI)createUIMethod.invoke(null, new Object[]{p_Component});
      } catch (Exception e2) {
        throw new RuntimeException("Failed to invoke " + realUI + "#createUI(JComponent)");
      }
    }
  }
  
  //
  // TRACKING OF THE CURRENT LOOK AND FEEL
  //
  private static boolean trackingChanges = false;
  private static PropertyChangeListener changeListener;    

  private static class UpdateAddon implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent evt) {
      try {
        setAddon(getBestMatchAddonClassName());
      } catch (Exception e) {
        // should not happen
        throw new RuntimeException(e);
      }
    }
  }
  
  /**
   * If true, everytime the Swing look and feel is changed, the addon which
   * best matches the current look and feel will be automatically selected.
   * 
   * @param tracking
   *          true to automatically update the addon, false to not automatically
   *          track the addon. Defaults to false.
   * @see #getBestMatchAddonClassName()
   */
  public static synchronized void setTrackingLookAndFeelChanges(boolean tracking) {
    if (trackingChanges != tracking) {
      if (tracking) {
        if (changeListener == null) {
          changeListener = new UpdateAddon();
        }
        UIManager.addPropertyChangeListener(changeListener);
      } else {
        if (changeListener != null) {
          UIManager.removePropertyChangeListener(changeListener);
        }
        changeListener = null;
      }
      trackingChanges = tracking;
    }
  }
  
  /**
   * @return true if the addon will be automatically change to match the current
   *         look and feel
   * @see #setTrackingLookAndFeelChanges(boolean)
   */
  public static synchronized boolean isTrackingLookAndFeelChanges() {
    return trackingChanges;
  }
   
}