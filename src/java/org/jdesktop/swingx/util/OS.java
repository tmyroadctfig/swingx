/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.util;

import java.awt.Toolkit;

import javax.swing.UIManager;

/**
 * Provides methods related to the runtime environment.
 */
public class OS {

  private static final boolean osIsMacOsX;
  private static final boolean osIsWindows;
  private static final boolean osIsWindowsXP;
  private static final boolean osIsWindows2003;

  static {
    String os = System.getProperty("os.name").toLowerCase();

    osIsMacOsX = "mac os x".equals(os);
    osIsWindows = os.indexOf("windows") != -1;
    osIsWindowsXP = "windows xp".equals(os);
    osIsWindows2003 = "windows 2003".equals(os);
  }

  /**
   * @return true if this VM is running on Mac OS X
   */
  public static boolean isMacOSX() {
    return osIsMacOsX;
  }

  /**
   * @return true if this VM is running on Windows
   */
  public static boolean isWindows() {
    return osIsWindows;
  }

  /**
   * @return true if this VM is running on Windows XP
   */
  public static boolean isWindowsXP() {
    return osIsWindowsXP;
  }

  /**
   * @return true if this VM is running on Windows 2003
   */
  public static boolean isWindows2003() {
    return osIsWindows2003;
  }

  /**
   * @return true if the VM is running Windows and the Java
   *         application is rendered using XP Visual Styles.
   */
  public static boolean isUsingWindowsVisualStyles() {
    if (!isWindows()) {
      return false;
    }

    boolean xpthemeActive = Boolean.TRUE.equals(Toolkit.getDefaultToolkit()
        .getDesktopProperty("win.xpstyle.themeActive"));
    if (!xpthemeActive) {
      return false;
    } else {
      try {
        return System.getProperty("swing.noxp") == null;
      } catch (RuntimeException e) {
        return true;
      }
    }
  }

  /**
   * Returns the name of the current Windows visual style.
   * <ul>
   * <li>it looks for a property name "win.xpstyle.name" in UIManager and if not found
   * <li>it queries the win.xpstyle.colorName desktop property ({@link Toolkit#getDesktopProperty(java.lang.String)})
   * </ul>
   * 
   * @return the name of the current Windows visual style if any. 
   */
  public static String getWindowsVisualStyle() {
    String style = UIManager.getString("win.xpstyle.name");
    if (style == null) {
      // guess the name of the current XPStyle
      // (win.xpstyle.colorName property found in awt_DesktopProperties.cpp in
      // JDK source)
      style = (String)Toolkit.getDefaultToolkit().getDesktopProperty(
        "win.xpstyle.colorName");
    }
    return style;
  }
  
}
