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
 */

package org.jdesktop.swingx.painter;

import java.awt.Graphics2D;
import javax.swing.JComponent;

/**
 * <p>Simple API for delegating painting. The JXPanel supports using this class
 * as a delegate for painting the background of the panel. This allows developers
 * to be able to customize the background painting of a JXPanel without having
 * to subclass it. Since many components within SwingX extend JXPanel, the
 * developer can implement custom painting on many parts of SwingX.</p>
 * 
 * <p>Painters can be combined together by using the CompoundPainter. CompoundPainter
 * uses an array to store several painters, and the order in which they should be
 * painted.</p>
 * 
 * <p>For example, if I want to create a CompoundPainter that started with a blue
 * background, had pinstripes on it running at a 45 degree angle, and those
 * pinstripes appeared to "fade in" from left to right, I would write the following:
 * <pre><code>
 *  Color blue = new Color(0x417DDD);
 *  Color translucent = new Color(blue.getRed(), blue.getGreen(), blue.getBlue(), 0);
 *  panel.setForeground(Color.LIGHT_GRAY);
 *  GradientPaint blueToTranslucent = new GradientPaint(
 *    new Point2D.Double(.4, 0),
 *    blue,
 *    new Point2D.Double(1, 0),
 *    translucent);
 *  Painter veil =  new MattePainter(blueToTranslucent);
 *  Painter pinstripes = new PinstripePainter(45);
 *  Painter backgroundPainter = new MattePainter(blue);
 *  Painter p = new CompoundPainter(backgroundPainter, pinstripes, veil);
 *  panel.setBackgroundPainter(p);
 * </code></pre></p>
 * 
 * <p>For convenience, AbstractPainter handles some basic painting chores and
 * should be extended for most concrete Painter implementations</p>
 * 
 * 
 * @author rbair
 * @see AbstractPainter
 */
public interface Painter<T> {
    /**
     * <p>Paints on the given Graphics2D object to fill a portion
     * of the area defined by the width and height. Painters do not
     * have to fill the entire area and may also paint outside that area.
     * 
     * <p>The object parameter may be null. If the painter uses the object then
     * it should check for null first.
     * 
     * <p>The Graphics2D object does not need to be returned to the same state it started
     * at by the end of the method.</p>
     * 
     * @param g The Graphics2D object in which to paint
     * @param object The object that the Painter should paint. This may be null.
     * @param width of the area to paint. Must be greater than zero.
     * @param height of the area to paint. Must be greater than zero.
     */
    public void paint(Graphics2D g, T object, int width, int height);
}
