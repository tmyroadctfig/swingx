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

package org.jdesktop.swingx.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * A collection of utilties for painting visual effects.
 *
 * @author Mark Davidson
 */
public class PaintUtils {

    //  Utility methods. 
    private static Border defaultBorder =
            BorderFactory.createBevelBorder(BevelBorder.RAISED);

    private PaintUtils() {
    }

    public static Border getDefaultBorder() {
        return defaultBorder;
    }

    /**
     * Returns the bounds that the text of a label will be drawn into.
     * Takes into account the current font metrics.
     */
    public static Rectangle getTextBounds(Graphics g, JLabel label) {
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D r2d = fm.getStringBounds(label.getText(), g);
        Rectangle rect = r2d.getBounds();
        int xOffset = 0;
        switch (label.getHorizontalAlignment()) {
            case SwingConstants.RIGHT:
            case SwingConstants.TRAILING:
                xOffset = label.getBounds().width - rect.width;
                break;
            case SwingConstants.CENTER:
                xOffset = (label.getBounds().width - rect.width) / 2;
                break;
            default:
            case SwingConstants.LEFT:
            case SwingConstants.LEADING:
                xOffset = 0;
                break;
        }
        int yOffset = 0;
        switch (label.getVerticalAlignment()) {
            case SwingConstants.TOP:
                yOffset = 0;
                break;
            case SwingConstants.CENTER:
                yOffset = (label.getBounds().height - rect.height) / 2;
                break;
            case SwingConstants.BOTTOM:
                yOffset = label.getBounds().height - rect.height;
                break;
        }
        return new Rectangle(xOffset, yOffset, rect.width, rect.height);
    }

    /**
     * Paints a top to bottom gradient fill over the component bounds
     * from color1 to color2.
     */
    public static void paintGradient(Graphics g, JComponent comp,
                                     Color color1, Color color2) {
        GradientPaint paint = new GradientPaint(0, 0, color1,
                                                0, comp.getHeight(), color2,
                                                true);
        Graphics2D g2 = (Graphics2D) g;
        Paint oldPaint = g2.getPaint();
        g2.setPaint(paint);
        g2.fillRect(0, 0, comp.getWidth(), comp.getHeight());
        g2.setPaint(oldPaint);
    }

    /**
     * Sets the background color for a containment hierarchy.
     */
    public static void setBackgroundColor(Container cont, Color color) {
        cont.setBackground(color);
        Component[] children = cont.getComponents();
        for (Component aChildren : children) {
            if (aChildren instanceof Container) {
                setBackgroundColor((Container) aChildren, color);
            } else {
                aChildren.setBackground(color);
            }
        }
    }

    /**
     * Sets the foreground color for a containment hierarchy.
     */
    public static void setForegroundColor(Container cont, Color color) {
        cont.setForeground(color);
        Component[] children = cont.getComponents();
        for (Component aChildren : children) {
            if (aChildren instanceof Container) {
                setForegroundColor((Container) aChildren, color);
            } else {
                aChildren.setForeground(color);
            }
        }
    }

    public static void setFont(Container cont, Font font) {
        cont.setFont(font);
        Component[] children = cont.getComponents();
        for (Component aChildren : children) {
            if (aChildren instanceof Container) {
                setFont((Container) aChildren, font);
            } else {
                aChildren.setFont(font);
            }
        }
    }

    /**
     * @param width  the width of the new BufferedImage
     * @param height the height of the new BufferedImage
     *
     * @return Creates and returns a BufferedImage that is "compatible" with this machines
     *         video card and subsystem
     */
    public static BufferedImage createCompatibleImage(int width, int height) {
        GraphicsEnvironment environment =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice screenDevice = environment.getDefaultScreenDevice();
        GraphicsConfiguration configuration =
                screenDevice.getDefaultConfiguration();
        return configuration.createCompatibleImage(width, height);
    }

    /**
     * @param width         the width of the new BufferedImage
     * @param height        the height of the new BufferedImage
     * @param transparency, one of the values in the Transparency interface
     *
     * @return Creates and returns a BufferedImage that is "compatible" with this machines
     *         video card and subsystem with the given Transparency.
     */
    public static BufferedImage createCompatibleImage(int width, int height,
                                                      int transparency) {
        GraphicsEnvironment environment =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice screenDevice = environment.getDefaultScreenDevice();
        GraphicsConfiguration configuration =
                screenDevice.getDefaultConfiguration();
        return configuration.createCompatibleImage(width, height, transparency);
    }
}
