/*
 * Copyright 2005 Sun Microsystems, Inc., 4150 Network Circle,
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

import javax.swing.BorderFactory;
import javax.swing.LookAndFeel;
import javax.swing.border.LineBorder;
import javax.swing.plaf.BorderUIResource;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.plaf.basic.BasicDatePickerUI;
import org.jdesktop.swingx.util.OS;

/**
 * @author Joshua Outwater
 */
public class DatePickerAddon extends AbstractComponentAddon {
    public DatePickerAddon() {
        super("JXDatePicker");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addBasicDefaults(LookAndFeelAddons addon, List<Object> defaults) {
        super.addBasicDefaults(addon, defaults);
        defaults.addAll(Arrays.asList(new Object[]{
                JXDatePicker.uiClassID,
                BasicDatePickerUI.class.getName(),
                "JXDatePicker.border",
                new BorderUIResource(BorderFactory.createCompoundBorder(
                        LineBorder.createGrayLineBorder(),
                        BorderFactory.createEmptyBorder(3, 3, 3, 3)))
        }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addWindowsDefaults(LookAndFeelAddons addon, List<Object> defaults) {
        super.addWindowsDefaults(addon, defaults);
        
        defaults.add("JXDatePicker.arrowIcon");
        
        if (OS.isWindowsXP()) {
            if (OS.isUsingWindowsVisualStyles()) {
                defaults.add(LookAndFeel.makeIcon(DatePickerAddon.class,
                        "windows/resources/combo-xp.png"));
            } else {
                defaults.add(LookAndFeel.makeIcon(DatePickerAddon.class,
                        "windows/resources/combo-w2k.png"));
            }
        }
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addLinuxDefaults(LookAndFeelAddons addon, List<Object> defaults) {
        super.addLinuxDefaults(addon, defaults);
        defaults.addAll(Arrays.asList(new Object[] {
                "JXDatePicker.arrowIcon",
                LookAndFeel.makeIcon(DatePickerAddon.class, "linux/resources/combo-gtk.png")
        }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addMacDefaults(LookAndFeelAddons addon, List<Object> defaults) {
        super.addMacDefaults(addon, defaults);
        defaults.addAll(Arrays.asList(new Object[] {
                "JXDatePicker.arrowIcon",
                LookAndFeel.makeIcon(DatePickerAddon.class, "macosx/resources/combo-osx.png")
        }));
    }
}

