/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.icon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.plaf.UIResource;

public class SortArrowIcon implements Icon, UIResource {
    private boolean ascending = true;
    //REMIND(aim): support more configurable sizes
    private int width = 8;
    private int height = 8;

    public SortArrowIcon(boolean ascending) {
        this.ascending = ascending;
    }

    public int getIconWidth() {
        return width;
    }

    public int getIconHeight() {
        return height;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        Color base = c.getBackground();
        Color shadow = base.darker().darker();
        Color highlight = Color.white;

        if (ascending) {
            g.setColor(shadow);
            int y1 = height-1;
            for(int x1=0; x1 < width/2 ; x1++) {
                g.drawLine(x + x1, y + y1, x + x1, y + y1 - 1);
                y1 -= ((x1+1 == (width/2)-1)? 1 : 2);
            }
            g.setColor(highlight);
            y1 = height-1;
            for (int x1 = width-1; x1 >= width / 2; x1--) {
                g.drawLine(x + x1, y + y1, x + x1, y + y1 - 1);
                y1 -= ( (x1 - 1 == (width / 2)) ? 1 : 2);
            }
            g.drawLine(x + 1, y + height-1, x + width - 1, y + height-1);
        } else {
            // descending
            g.setColor(shadow);
            int y1 = 1;
            for (int x1 = 0; x1 < width/2 ; x1++) {
                g.drawLine(x + x1, y + y1, x + x1, y + y1 + 1);
                y1 += (x1+1 == (width/2-1))? 1 : 2;
            }
            g.setColor(highlight);
            y1 = 1;
            for (int x1 = width - 1; x1 >= width/2; x1--) {
                g.drawLine(x + x1, y + y1, x + x1, y + y1 + 1);
                y1 += (x1-1 == width/2)? 1 : 2;
            }
            g.setColor(shadow);
            g.drawLine(x + 1, y + 1, x + width - 1, y + 1);
        }
    }
}
