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

package org.jdesktop.swingx.color;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.graphics.PaintUtils;

/**
 * A collection of utility methods for working with {@code Color}s.
 * 
 * @author joshua.marinacci@sun.com
 * @author Karl George Schaefer
 * @deprecated (pre-1.6.3) all methods moved
 */
@Deprecated
public final class ColorUtil {
    private ColorUtil() {
        //prevent instantiation
    }
    
    /**
     * Returns a new color equal to the old one, except that there is no alpha
     * (transparency) channel.
     * <p>
     * This method is a convenience and has the same effect as {@code
     * setAlpha(color, 255)}.
     * 
     * @param color
     *            the color to remove the alpha (transparency) from
     * @return a new non-transparent {@code Color}
     * @throws NullPointerException
     *             if {@code color} is {@code null}
     * @deprecated Use {@link PaintUtils#removeAlpha(Color)} instead
     */
    public static Color removeAlpha(Color color) {
        return PaintUtils.removeAlpha(color);
    }

    /**
     * Returns a new color equal to the old one, except alpha (transparency)
     * channel is set to the new value.
     * 
     * @param color
     *            the color to modify
     * @param alpha
     *            the new alpha (transparency) level. Must be an int between 0
     *            and 255
     * @return a new alpha-applied {@code Color}
     * @throws IllegalArgumentException
     *             if {@code alpha} is not between 0 and 255 inclusive
     * @throws NullPointerException
     *             if {@code color} is {@code null}
     * @deprecated Use {@link PaintUtils#setAlpha(Color,int)} instead
     */
    public static Color setAlpha(Color color, int alpha) {
        return PaintUtils.setAlpha(color, alpha);
    }

    /**
     * Returns a new color equal to the old one, except the saturation is set to
     * the new value. The new color will have the same alpha (transparency) as
     * the original color.
     * <p>
     * The color is modified using HSB calculations. The saturation must be a
     * float between 0 and 1. If 0 the resulting color will be gray. If 1 the
     * resulting color will be the most saturated possible form of the passed in
     * color.
     * 
     * @param color
     *            the color to modify
     * @param saturation
     *            the saturation to use in the new color
     * @return a new saturation-applied {@code Color}
     * @throws IllegalArgumentException
     *             if {@code saturation} is not between 0 and 1 inclusive
     * @throws NullPointerException
     *             if {@code color} is {@code null}
     * @deprecated Use {@link PaintUtils#setSaturation(Color,float)} instead
     */
    public static Color setSaturation(Color color, float saturation) {
        return PaintUtils.setSaturation(color, saturation);
    }

    /**
     * Returns a new color equal to the old one, except the brightness is set to
     * the new value. The new color will have the same alpha (transparency) as
     * the original color.
     * <p>
     * The color is modified using HSB calculations. The brightness must be a
     * float between 0 and 1. If 0 the resulting color will be black. If 1 the
     * resulting color will be the brightest possible form of the passed in
     * color.
     * 
     * @param color
     *            the color to modify
     * @param brightness
     *            the brightness to use in the new color
     * @return a new brightness-applied {@code Color}
     * @throws IllegalArgumentException
     *             if {@code brightness} is not between 0 and 1 inclusive
     * @throws NullPointerException
     *             if {@code color} is {@code null}
     * @deprecated Use {@link PaintUtils#setBrightness(Color,float)} instead
     */
    public static Color setBrightness(Color color, float brightness) {
        return PaintUtils.setBrightness(color, brightness);
    }

    /**
     * Creates a {@code String} that represents the supplied color as a
     * hex-value RGB triplet, including the "#". The return value is suitable
     * for use in HTML. The alpha (transparency) channel is neither include nor
     * used in producing the string.
     * 
     * @param color
     *            the color to convert
     * @return the hex {@code String}
     * @deprecated Use {@link PaintUtils#toHexString(Color)} instead
     */
    public static String toHexString(Color color) {
        return PaintUtils.toHexString(color);
    }

    /**
     * Computes an appropriate foreground color (either white or black) for the
     * given background color.
     * 
     * @param bg
     *            the background color
     * @return {@code Color.WHITE} or {@code Color.BLACK}
     * @throws NullPointerException
     *             if {@code bg} is {@code null}
     * @deprecated Use {@link PaintUtils#computeForeground(Color)} instead
     */
    public static Color computeForeground(Color bg) {
        return PaintUtils.computeForeground(bg);
    }

    /**
     * Blends two colors to create a new color. The {@code origin} color is the
     * base for the new color and regardless of its alpha component, it is
     * treated as fully opaque (alpha 255).
     * 
     * @param origin
     *            the base of the new color
     * @param over
     *            the alpha-enabled color to add to the {@code origin} color
     * @return a new color comprised of the {@code origin} and {@code over}
     *         colors
     * @deprecated Use {@link PaintUtils#blend(Color,Color)} instead
     */
    public static Color blend(Color origin, Color over) {
        return PaintUtils.blend(origin, over);
    }

    /**
     * Interpolates a color.
     * 
     * @param b
     * @param a
     * @param t
     * @return
     * @deprecated Use {@link PaintUtils#interpolate(Color,Color,float)} instead
     */
    public static Color interpolate(Color b, Color a, float t) {
        return PaintUtils.interpolate(b, a, t);
    }

    /**
     * Creates a new {@code Paint} that is a checkered effect using the colors {@link Color#GRAY
     * gray} and {@link Color#WHITE}.
     * 
     * @return a the checkered paint
     * @deprecated Use {@link PaintUtils#getCheckerPaint()} instead
     */
    public static Paint getCheckerPaint() {
        return PaintUtils.getCheckerPaint();
    }

    /**
     * Creates a new {@code Paint} that is a checkered effect using the specified colors.
     * <p>
     * While this method supports transparent colors, this implementation performs painting
     * operations using the second color after it performs operations using the first color. This
     * means that to create a checkered paint with a fully-transparent color, you MUST specify that
     * color first.
     * 
     * @param c1
     *            the first color
     * @param c2
     *            the second color
     * @param size
     *            the size of the paint
     * @return a new {@code Paint} checkering the supplied colors
     * @deprecated Use {@link PaintUtils#getCheckerPaint(Paint,Paint,int)} instead
     */
    public static Paint getCheckerPaint(Color c1, Color c2, int size) {
        return PaintUtils.getCheckerPaint(c1, c2, size);
    }
    
    /**
     * Draws an image on top of a component by doing a 3x3 grid stretch of the image
     * using the specified insets.
     * @deprecated (pre-1.6.3) Use {@link GraphicsUtilities#tileStretchPaint(Graphics,JComponent,BufferedImage,Insets)} instead
     */
    @Deprecated
    public static void tileStretchPaint(Graphics g, 
                JComponent comp,
                BufferedImage img,
                Insets ins) {
                    GraphicsUtilities.tileStretchPaint(g, comp, img, ins);
                }
}
