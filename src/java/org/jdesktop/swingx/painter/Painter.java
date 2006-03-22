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

package org.jdesktop.swingx.painter;

import java.awt.Graphics2D;
import javax.swing.JComponent;

/**
 * <p>Simple API for delegating painting. The JXPanel supports using this class
 * as a delegate for painting the background of the panel. This allows developers
 * to be able to customize the background painting of a JXPanel without having
 * to override it. Since many components within SwingX extend JXPanel, the
 * developer can implement custom painting on many parts of SwingX.</p>
 *
 * <p>Painters are generally expected to work with JComponent or one of its
 * subclasses. Most painters don't use the component beyond requesting its width
 * and height, but it is conceivable that certain painters will only work with
 * specific subclasses (JXTitledPanel, for instance, so that the text can
 * be extracted and used to paint a glow effect).</p>
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
 *  panel.setBackground(blue);
 *  panel.setForeground(Color.LIGHT_GRAY);
 *  GradientPaint blueToTranslucent = new GradientPaint(
 *    new Point2D.Double(.4, 0),
 *    blue,
 *    new Point2D.Double(1, 0),
 *    translucent);
 *  Painter veil =  new BasicGradientPainter(blueToTranslucent);
 *  Painter pinstripes = new PinstripePainter(45);
 *  Painter backgroundPainter = new BackgroundPainter();
 *  Painter p = new CompoundPainter(backgroundPainter, pinstripes, veil);
 *  panel.setBackgroundPainter(p);
 * </code></pre></p>
 *
 * <p>For convenience, AbstractPainter handles some basic painting chores and
 * should be extended for most concrete Painter implementations</p>
 *
 * @author rbair
 */
public interface Painter<T extends JComponent> {
    /**
     * <p>Paints on the given Graphics2D object some effect which may or may not
     * be related to the given component. For example, BackgroundPainter will
     * use the background property of the component and the width/height of the
     * component to perform a fill rect. Most other Painters will disregard the
     * component entirely, except to get the component width/height.</p>
     *
     * <p>The Graphics2D object must be returned to the same state it started
     * at by the end of the method. For example, if "setColor(c)" was called
     * on the graphics object, it should be reset to the original color before
     * the method returns.</p>
     *
     * @param g The Graphics2D object in which to paint
     * @param component The JComponent that the Painter is delegate for.
     */
    public void paint(Graphics2D g, T component);
}
