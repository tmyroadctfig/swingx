/*
 * $Id: BusyLabelAddon.java 2565 2008-01-03 19:08:32Z rah003 $
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

import org.jdesktop.swingx.JXList;

/**
 * Addon for <code>JXList</code>.
 * <p>
 * 
 * Will install a custom ui if JXList will be sortable/filterable again.
 * Currently unused, does nothing.
 */
public class XListAddon extends AbstractComponentAddon {

    public XListAddon() {
        super("JXList");
    }

    @Override
    protected void addBasicDefaults(LookAndFeelAddons addon,
            DefaultsList defaults) {
        defaults.add(JXList.uiClassID,
                "org.jdesktop.swingx.plaf.basic.core.BasicXListUI");
    }

    @Override
    protected void addNimbusDefaults(LookAndFeelAddons addon,
            DefaultsList defaults) {
        defaults.add(JXList.uiClassID,
                "org.jdesktop.swingx.plaf.synth.SynthXListUI");

    }
}
