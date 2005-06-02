/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

/**
 * Each new component type of the library will contribute an addon to
 * the LookAndFeelAddons. A <code>ComponentAddon</code> is the
 * equivalent of a {@link javax.swing.LookAndFeel}but focused on one
 * component. <br>
 */
public interface ComponentAddon {

  /**
   * @return the name of this addon
   */
  String getName();

  /**
   * Initializes this addon (i.e register UI classes, colors, fonts,
   * borders, any UIResource used by the component class). When
   * initializing, the addon can register different resources based on
   * the addon or the current look and feel.
   * 
   * @param addon the current addon
   */
  void initialize(LookAndFeelAddons addon);

  /**
   * Uninitializes this addon.
   * 
   * @param addon
   */
  void uninitialize(LookAndFeelAddons addon);

}