/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
 *
 */
package org.jdesktop.swingx.plaf;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jdesktop.swingx.plaf.windows.WindowsClassicLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsLookAndFeelAddons;
import org.jdesktop.swingx.util.OS;

/**
 * Loads LF specific background striping colors. 
 * 
 * The colors are based on the LF selection colors for certain
 * LFs and themes, for unknown LFs/themes a generic grey is used.  
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class UIColorHighlighterAddon extends AbstractComponentAddon {

    /**
     * @param name
     */
    public UIColorHighlighterAddon() {
        super("UIColorHighlighter");
    }

    /**
     * Loads the LF-specific striping background color for
     * usage in UIColorHighlighter.
     * 
     */
    @Override
    public void initialize(LookAndFeelAddons addon) {
        Color striping = null;
        if (isWindows(addon)) {
            striping = getWindowStripingColor(addon);
        } else if (isMetal(addon)) {
            striping = getMetalStripingColor(addon);
        } else if (isMac(addon)) {
            striping = getMacStripingColor(addon);
        } else if (isMotif(addon)) {
            striping = getMotifStripingColor(addon);
        }
//        // PENDING: JW - fall-back here or in UIColorHighlighter?
//        if (striping == null) {
//            striping = getGenericStriping();
//        }
        List<Object> defaults = new ArrayList<Object>();
        defaults.addAll(Arrays.asList(new Object[] {
                "UIColorHighlighter.stripingBackground", 
                striping,
//                "UIColorHighlighter.genericStripingBackground",
//                getGenericStriping()
                }));
        addon.loadDefaults(defaults.toArray());
    }

    /**
     * Creates and returns a generic striping color.
     * Internally used if no LF specific color is found.
     * 
     * @return a generic striping color
     */
    protected ColorUIResource getGenericStriping() {
        return new ColorUIResource(229, 229, 229);
    }

    /**
     * Creates and returns a the MacX specific striping color, may be null.
     * @param addon the AddOn to create the color for
     * @return the Mac specific striping color.
     */
    protected ColorUIResource getMacStripingColor(LookAndFeelAddons addon) {
        return new ColorUIResource(237, 243, 254);
    }

    /**
     * Creates and returns a the Metal specific striping color, may be null.
     * Checks for Ocean only.
     * 
     * @param addon the AddOn to create the color for
     * @return the Metal specific striping color.
     */
    protected ColorUIResource getMetalStripingColor(LookAndFeelAddons addon) {
        ColorUIResource striping = null;
        try {
            Method method = MetalLookAndFeel.class.getMethod("getCurrentTheme");
            Object currentTheme = method.invoke(null);
            if (Class.forName("javax.swing.plaf.metal.OceanTheme").isInstance(
                    currentTheme)) {
                striping = new ColorUIResource(230, 238, 246);
            }
        } catch (Exception e) {
        }
        if (striping == null) {
            striping = new ColorUIResource(235, 235, 255);
        }
        return striping;
    }

    /**
     * Creates and returns a the Windows specific striping color, may be null.
     * Can handle classic and XP (normal blue, homestead, metalic).<p>
     * 
     * PENDING: Vista?
     * 
     * @param addon the AddOn to create the color for
     * @return the Windows specific striping color.
     */
    protected ColorUIResource getWindowStripingColor(LookAndFeelAddons addon) {
        if (addon instanceof WindowsClassicLookAndFeelAddons) {
            return new ColorUIResource(218, 222, 233);
        }
        ColorUIResource striping = null;
        String xpStyle = OS.getWindowsVisualStyle();
        if (WindowsLookAndFeelAddons.HOMESTEAD_VISUAL_STYLE
                .equalsIgnoreCase(xpStyle)) {
            striping = new ColorUIResource(228, 231, 219);
        } else if (WindowsLookAndFeelAddons.SILVER_VISUAL_STYLE
                .equalsIgnoreCase(xpStyle)) {
            striping = new ColorUIResource(235, 235, 236);
        } else {
            // default blue
            striping = new ColorUIResource(224, 233, 246);
        }
        return striping;
    }

    /**
     * Creates and returns a the motif specific striping color, may be null.
     * 
     * PENDING: no Motif included in color design map, returns
     *   generic.
     *   
     * @param addon the AddOn to create the color for
     * @return the Motif specific striping color, here: generic
     */
    protected ColorUIResource getMotifStripingColor(LookAndFeelAddons addon) {
        return getGenericStriping();
    }

}
