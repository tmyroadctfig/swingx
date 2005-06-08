/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.border;

import java.awt.*;
import java.awt.event.*;

import java.util.Timer;

import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;

/**
 * @author Amy Fowler
 * @version 1.0
 */
public class IconBorder implements Border {

    private static final int PAD = 4;
    private Icon icon;
    private int iconPosition;
    private Rectangle iconBounds = new Rectangle();
    public IconBorder() {
        this(null);
    }

    public IconBorder(Icon validIcon) {
        this(validIcon, SwingConstants.EAST);
    }

    public IconBorder(Icon validIcon, int iconPosition) {
        this.icon = validIcon;
        this.iconPosition = iconPosition;
    }

    public Insets getBorderInsets(Component c) {
        int horizontalInset = icon.getIconWidth() + (2 * PAD);
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
        Graphics2D g2d = (Graphics2D) g;
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

}
