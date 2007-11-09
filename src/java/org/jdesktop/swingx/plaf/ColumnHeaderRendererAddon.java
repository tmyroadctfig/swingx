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

import java.util.Arrays;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.plaf.BorderUIResource;

import org.jdesktop.swingx.table.ColumnHeaderRenderer;
import org.jdesktop.swingx.util.OS;

/**
 * Addon for ColumnHeaderRenderer.<p>
 * Loads LF specific sort icons.
 * 
 * @author Jeanette Winzenburg
 * @author Karl Schaefer
 */
public class ColumnHeaderRendererAddon extends AbstractComponentAddon {
    
  public ColumnHeaderRendererAddon() {
    super("ColumnHeaderRenderer");
  }
  
    /**
     * {@inheritDoc}
     */
    @Override
    protected void addBasicDefaults(LookAndFeelAddons addon,
            List<Object> defaults) {
        super.addBasicDefaults(addon, defaults);
        
        defaults.addAll(Arrays.asList(new Object[] { 
            ColumnHeaderRenderer.UP_ICON_KEY,
            LookAndFeel.makeIcon(ColumnHeaderRendererAddon.class, "basic/resources/sort-jlf-up.png"),
            ColumnHeaderRenderer.DOWN_ICON_KEY,
            LookAndFeel.makeIcon(ColumnHeaderRendererAddon.class, "basic/resources/sort-jlf-dn.png"),
        }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addLinuxDefaults(LookAndFeelAddons addon,
            List<Object> defaults) {
        super.addLinuxDefaults(addon, defaults);
        
        if (isSynth()) {
            defaults.addAll(Arrays.asList(new Object[] { 
                    ColumnHeaderRenderer.UP_ICON_KEY,
                    LookAndFeel.makeIcon(ColumnHeaderRendererAddon.class, "linux/resources/sort-gtk-up.png"),
                    ColumnHeaderRenderer.DOWN_ICON_KEY,
                    LookAndFeel.makeIcon(ColumnHeaderRendererAddon.class, "linux/resources/sort-gtk-dn.png"),
            }));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addMacDefaults(LookAndFeelAddons addon, List<Object> defaults) {
        super.addMacDefaults(addon, defaults);
        
        defaults.addAll(Arrays.asList(new Object[] { 
                ColumnHeaderRenderer.UP_ICON_KEY,
                LookAndFeel.makeIcon(ColumnHeaderRendererAddon.class, "macosx/resources/sort-osx-up.png"),
                ColumnHeaderRenderer.DOWN_ICON_KEY,
                LookAndFeel.makeIcon(ColumnHeaderRendererAddon.class, "macosx/resources/sort-osx-dn.png"),
        }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addWindowsDefaults(LookAndFeelAddons addon, List<Object> defaults) {
        super.addWindowsDefaults(addon, defaults);
        
        if (OS.isWindowsXP()) {
            defaults.addAll(Arrays.asList(new Object[] { 
                    ColumnHeaderRenderer.UP_ICON_KEY,
                    LookAndFeel.makeIcon(ColumnHeaderRendererAddon.class, "windows/resources/sort-xp-up.png"),
                    ColumnHeaderRenderer.DOWN_ICON_KEY,
                    LookAndFeel.makeIcon(ColumnHeaderRendererAddon.class, "windows/resources/sort-xp-dn.png"),
            }));
        } else {
            defaults.addAll(Arrays.asList(new Object[] { 
                    ColumnHeaderRenderer.UP_ICON_KEY,
                    LookAndFeel.makeIcon(ColumnHeaderRendererAddon.class, "windows/resources/sort-w2k-up.png"),
                    ColumnHeaderRenderer.DOWN_ICON_KEY,
                    LookAndFeel.makeIcon(ColumnHeaderRendererAddon.class, "windows/resources/sort-w2k-dn.png"),
            }));
        }
        
        if (OS.isWindowsVista()) {
            defaults.addAll(Arrays.asList(new Object[] { 
                    ColumnHeaderRenderer.VISTA_BORDER_HACK,
                    new BorderUIResource.EmptyBorderUIResource(5, 5, 5, 5), 
            }));
        }
    }
}
