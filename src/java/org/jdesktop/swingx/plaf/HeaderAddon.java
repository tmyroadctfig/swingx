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
import java.util.Arrays;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.LabelUI;

import org.jdesktop.swingx.JXHeader;

/**
 * Addon for <code>JXHeader</code>.<br>
 *
 */
public class HeaderAddon extends AbstractComponentAddon {

    public HeaderAddon() {
        super("JXHeader");
    }

    @Override
    protected void addBasicDefaults(LookAndFeelAddons addon, List<Object> defaults) {
        super.addBasicDefaults(addon, defaults);
        defaults.addAll(Arrays.asList(new Object[]{
            JXHeader.uiClassID, "org.jdesktop.swingx.plaf.basic.BasicHeaderUI",
            "JXHeader.defaultIcon", LookAndFeel.makeIcon(HeaderAddon.class,
                    "resources/header-default.png"),
                    "JXHeader.titleFont", new FontUIResource(UIManager.getFont("Label.font").deriveFont(Font.BOLD)),
                    "JXHeader.titleForeground", UIManager.getColor("Label.foreground"),
                    "JXHeader.descriptionFont", UIManager.getFont("Label.font"),
                    "JXHeader.descriptionForeground", UIManager.getColor("Label.foreground"),
                    "JXHeader.background", UIManagerExt.getSafeColor("control",
                            new ColorUIResource(Color.decode("#C0C0C0"))),
                    "JXHeader.startBackground", new ColorUIResource(Color.WHITE)
        }));
    }

    @Override
    protected void addMacDefaults(LookAndFeelAddons addon, List<Object> defaults) {
        super.addMacDefaults(addon, defaults);
        defaults.addAll(Arrays.asList(new Object[]{
            JXHeader.uiClassID, "org.jdesktop.swingx.plaf.macosx.MacOSXHeaderUI",
        }));
    }

    @Override
    protected void addNimbusDefaults(LookAndFeelAddons addon, List<Object> defaults) {
        super.addNimbusDefaults(addon, defaults);
        defaults.addAll(Arrays.asList(new Object[]{
            "JXHeader.background", new ColorUIResource(new Color(214, 217, 223, 255))
        }));
    }
}