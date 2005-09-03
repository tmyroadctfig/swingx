/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;

import org.jdesktop.swingx.JXTipOfTheDay;
import org.jdesktop.swingx.plaf.basic.BasicTipOfTheDayUI;
import org.jdesktop.swingx.plaf.windows.WindowsTipOfTheDayUI;

/**
 * Addon for <code>JXTipOfTheDay</code>.<br>
 * 
 * @author <a href="mailto:fred@L2FProd.com">Frederic Lavigne</a>
 */
public class JXTipOfTheDayAddon extends AbstractComponentAddon {

  public JXTipOfTheDayAddon() {
    super("JXTipOfTheDay");
  }

  @Override
  protected void addBasicDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    defaults.add(JXTipOfTheDay.uiClassID);
    defaults.add(BasicTipOfTheDayUI.class.getName());

    defaults.add("TipOfTheDay.font");
    defaults.add(UIManager.getFont("TextPane.font"));

    defaults.add("TipOfTheDay.tipFont");
    defaults.add(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 13f));

    defaults.add("TipOfTheDay.background");
    defaults.add(new ColorUIResource(Color.white));

    defaults.add("TipOfTheDay.icon");
    defaults.add(LookAndFeel.makeIcon(BasicTipOfTheDayUI.class,
      "resources/TipOfTheDay24.gif"));

    defaults.add("TipOfTheDay.border");
    defaults.add(new BorderUIResource(BorderFactory.createLineBorder(new Color(
      117, 117, 117))));

    addResource(defaults,
      "org.jdesktop.swingx.plaf.basic.resources.TipOfTheDay");
  }

  @Override
  protected void addWindowsDefaults(LookAndFeelAddons addon,
    List<Object> defaults) {
    super.addWindowsDefaults(addon, defaults);

    defaults.add(JXTipOfTheDay.uiClassID);
    defaults.add(WindowsTipOfTheDayUI.class.getName());

    defaults.add("TipOfTheDay.background");
    defaults.add(new ColorUIResource(128, 128, 128));

    defaults.add("TipOfTheDay.font");
    defaults.add(UIManager.getFont("Label.font").deriveFont(13f));

    defaults.add("TipOfTheDay.icon");
    defaults.add(LookAndFeel.makeIcon(WindowsTipOfTheDayUI.class,
      "resources/tipoftheday.png"));

    defaults.add("TipOfTheDay.border");
    defaults
      .add(new BorderUIResource(new WindowsTipOfTheDayUI.TipAreaBorder()));

    addResource(defaults,
      "org.jdesktop.swingx.plaf.windows.resources.TipOfTheDay");
  }

}
