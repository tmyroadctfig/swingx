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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import javax.swing.JComponent;

/**
 * A Painter implementation that uses a Paint to fill the entire background
 * area using that Paint. For example, if I wanted to paint the entire background
 * in Color.GREEN, I would:
 * <pre><code>
 *  MattePainter p = new MattePainter(Color.GREEN);
 *  panel.setBackgroundPainter(p);
 * </code></pre></p>
 *
 * <p>Since it accepts a Paint, it is also possible to paint a texture or use other
 * more exotic Paint implementations. To paint a BufferedImage texture as the
 * background:
 * <pre><code>
 *  TexturePaint paint = new TexturePaint(bufferedImage, 
 *      new Rectangle2D.Double(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight()));
 *  MattePainter p = new MattePainter(paint);
 *  panel.setBackgroundPainter(p);
 * </code></pre></p>
 *
 * <p>If no paint is specified, then nothing is painted</p>
 *
 * @author rbair
 */
public class MattePainter extends AbstractPainter {
    /**
     * The paint to use
     */
    private Paint paint;

    /**
     * Creates a new MattePainter with "null" as the paint used
     */
    public MattePainter() {
    }
    
    /**
     * Create a new MattePainter that uses the given color. This is only
     * a convenience constructor since Color is a Paint, and thus the
     * other constructor is perfectly suited for specify a color as well
     *
     * @param color Color to fill with
     */
    public MattePainter(Color color) {
        this((Paint)color);
    }
    
    /**
     * Create a new MattePainter for the given Paint. This can be a GradientPaint
     * (though not recommended because the gradient will not grow when the
     * component becomes larger), TexturePaint, Color, or other Paint instance.
     *
     * @param paint Paint to fill with
     */
    public MattePainter(Paint paint) {
        super();
        this.paint = paint;
    }

    /**
     * Sets the Paint to use. If null, nothing is painted
     *
     * @param p the Paint to use
     */
    public void setPaint(Paint p) {
        Paint old = getPaint();
        this.paint = p;
        firePropertyChange("paint", old, getPaint());
    }
    
    /**
     * @return Gets the Paint being used. May be null
     */
    public Paint getPaint() {
        return paint;
    }
    
    /**
     * @inheritDoc
     */
    public void paintBackground(Graphics2D g, JComponent component) {
        Paint p = getPaint();
        if (p != null) {
            g.setPaint(p);
            g.fillRect(0, 0, component.getWidth(), component.getHeight());
        }
    }
}
