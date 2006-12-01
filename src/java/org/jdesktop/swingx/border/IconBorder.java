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

package org.jdesktop.swingx.border;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 * @author Amy Fowler
 * @version 1.0
 */
public class IconBorder implements Border, Serializable {

    private static final int PAD = 4;
    private Icon icon;
    private int iconPosition;
    private Rectangle iconBounds = new Rectangle();
    public IconBorder() {
        this(null);
    }

    public IconBorder(Icon validIcon) {
        this(validIcon, SwingConstants.TRAILING);
    }

    public IconBorder(Icon validIcon, int iconPosition) {
        this.icon = validIcon;
        this.iconPosition = iconPosition;
    }

    public Insets getBorderInsets(Component c) {
        int horizontalInset = icon.getIconWidth() + (2 * PAD);
        int iconPosition = bidiDecodeLeadingTrailing(c.getComponentOrientation(), this.iconPosition);
        if (iconPosition == SwingConstants.EAST) {
            return new Insets(0, 0, 0, horizontalInset);
        }
        return new Insets(0, horizontalInset, 0, 0);
    }

    public void setIcon(Icon validIcon) {
        this.icon = validIcon;
    }
    
    public boolean isBorderOpaque() {
        return false;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width,
        int height) {
        int iconPosition = bidiDecodeLeadingTrailing(c.getComponentOrientation(), this.iconPosition);
        if (iconPosition == SwingConstants.NORTH_EAST) {
            iconBounds.y = y + PAD;
            iconBounds.x = x + width - PAD - icon.getIconWidth();
        } else if (iconPosition == SwingConstants.EAST) {    // EAST
            iconBounds.y = y
                + ((height - icon.getIconHeight()) / 2);
            iconBounds.x = x + width - PAD - icon.getIconWidth();
        } else if (iconPosition == SwingConstants.WEST) {
            iconBounds.y = y
                + ((height - icon.getIconHeight()) / 2);
            iconBounds.x = x + PAD;
        }
        iconBounds.width = icon.getIconWidth();
        iconBounds.height = icon.getIconHeight();
        icon.paintIcon(c, g, iconBounds.x, iconBounds.y);
    }

    /**
     * Returns EAST or WEST depending on the ComponentOrientation and 
     * the given postion LEADING/TRAILING this method has no effect for other
     * position values
     */
    private int bidiDecodeLeadingTrailing(ComponentOrientation c, int position) {
        if(position == SwingConstants.TRAILING) {
            if(!c.isLeftToRight()) {
                return SwingConstants.WEST;
            }
            return SwingConstants.EAST;
        }
        if(position == SwingConstants.LEADING) {
            if(!c.isLeftToRight()) {
                return SwingConstants.WEST;
            }
            return SwingConstants.EAST;
        }
        return position;
    }

}
