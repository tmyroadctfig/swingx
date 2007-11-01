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
package org.jdesktop.swingx.plaf;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.LookAndFeel;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

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
    defaults.add(UIManagerExt.getSafeFont("TipOfTheDay.font",
                new FontUIResource("Serif", Font.PLAIN, 12)));

    defaults.add("TipOfTheDay.tipFont");
    Font font = UIManagerExt.getSafeFont("Label.font",
            new Font("Dialog", Font.PLAIN, 12));
    font = font.deriveFont(Font.BOLD, 13f);
    defaults.add(new FontUIResource(font));

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
    Font font = UIManagerExt.getSafeFont("Label.font",
            new Font("Dialog", Font.PLAIN, 12));
    font = font.deriveFont(13f);
    defaults.add(new FontUIResource(font));

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
