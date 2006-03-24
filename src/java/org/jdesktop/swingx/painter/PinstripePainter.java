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
import java.awt.Paint;
import javax.swing.JComponent;

/**
 * <p>A fun Painter that paints pinstripes. You can specify the Paint to paint
 * those pinstripes in (could even be a texture paint!), the angle at which
 * to paint the pinstripes, and the spacing between stripes.</p>
 *
 * <p>The default PinstripePainter configuration will paint the pinstripes
 * using the foreground color of the component (the default behavior if a
 * Paint is not specified) at a 45 degree angle with 8 pixels between stripes</p>
 *
 * <p>Here is a custom code snippet that paints Color.GRAY pinstripes at a 135
 * degree angle:
 * <pre><code>
 *  PinstripePainter p = new PinstripePainter();
 *  p.setAngle(135);
 *  p.setPaint(Color.GRAY);
 * </code></pre>
 *
 * @author rbair
 */
public class PinstripePainter extends AbstractPainter {
    /**
     * The angle in degrees to paint the pinstripes at. The default
     * value is 45. The value will be between 0 and 360 inclusive. The
     * setAngle method will ensure this.
     */
    private double angle = 45;
    /**
     * The spacing between pinstripes
     */
    private double spacing = 8;
    /**
     * The Paint to use when drawing the pinstripes
     */
    private Paint paint;

    /**
     * Create a new PinstripePainter. By default the angle with be 45 degrees,
     * the spacing will be 8 pixels, and the color will be the Component foreground
     * color.
     */
    public PinstripePainter() {
    }

    /**
     * Create a new PinstripePainter using an angle of 45, 8 pixel spacing,
     * and the given Paint.
     *
     * @param paint the paint used when drawing the stripes
     * @param angle the angle, in degrees, in which to paint the pinstripes
     */
    public PinstripePainter(Paint paint) {
        this(paint, 45);
    }
    
    /**
     * Create a new PinstripePainter using the given angle, 8 pixel spacing,
     * and the given Paint
     *
     * @param paint the paint used when drawing the stripes
     * @param angle the angle, in degrees, in which to paint the pinstripes
     */
    public PinstripePainter(Paint paint, double angle) {
        this.paint = paint;
        this.angle = angle;
    }
    
    /**
     * Create a new PinstripePainter using the given angle, 8 pixel spacing,
     * and the foreground color of the Component
     *
     * @param angle the angle, in degrees, in which to paint the pinstripes
     */
    public PinstripePainter(double angle) {
        this.angle = angle;
    }

    /**
     * Set the paint to use for drawing the pinstripes
     *
     * @param p the Paint to use. May be a Color.
     */
    public void setPaint(Paint p) {
        Paint old = getPaint();
        this.paint = p;
        firePropertyChange("paint", old, getPaint());
    }
    
    /**
     * @return the Paint to use to draw the pinstripes
     */
    public Paint getPaint() {
        return paint;
    }
    
    /**
     * Sets the angle, in degrees, at which to paint the pinstripes. If the
     * given angle is < 0 or > 360, it will be appropriately constrained. For
     * example, if a value of 365 is given, it will result in 5 degrees. The
     * conversion is not perfect, but "a man on a galloping horse won't be
     * able to tell the difference". 
     *
     * @param angle the Angle in degrees at which to paint the pinstripes
     */
    public void setAngle(double angle) {
        if (angle > 360) {
            angle = angle % 360;
        }

        if (angle < 0) {
            angle = 360 - ((angle * -1) % 360);
        }

        double old = getAngle();
        this.angle = angle;
        firePropertyChange("angle", old, getAngle());
    }
    
    /**
     * @return the angle, in degrees, at which the pinstripes are painted
     */
    public double getAngle() {
        return angle;
    }
    
    /**
     * Sets the spacing between pinstripes
     *
     * @param the spacing between pinstripes
     */
    public void setSpacing(double spacing) {
        double old = getSpacing();
        this.spacing = spacing;
        firePropertyChange("spacing", old, getSpacing());
    }
    
    /**
     * @return the spacing between pinstripes
     */
    public double getSpacing() {
        return spacing;
    }
       
    /**
     * @inheritDoc
     */
    public void paintBackground(Graphics2D g, JComponent component) {
        //draws pinstripes at the angle specified in this class
        //and at the given distance apart
        Paint p = getPaint();
        if (p == null) {
            g.setColor(component.getForeground());
        } else {
            g.setPaint(p);
        }

        double hypLength = Math.sqrt((component.getWidth() * component.getWidth()) +
                                   (component.getHeight() * component.getHeight()));

        double radians = Math.toRadians(getAngle());
        g.rotate(radians);

        int numLines = (int)(hypLength / getSpacing());
        int lineLength = (int)hypLength;

        for (int i=0; i<numLines; i++) {
            int x = (int)(i * getSpacing());
            g.drawLine(x, -lineLength, x, lineLength);
        }
    }
}
