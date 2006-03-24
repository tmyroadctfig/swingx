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
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import javax.swing.JComponent;

/**
 * <p>A Painter that paints Shapes. It uses a stroke and a paint to do so. The
 * shape is painted as is, at a specific location. If no Shape is specified, nothing
 * will be painted. If no stroke is specified, the default for the Graphics2D
 * will be used. If no paint is specified, the component background color
 * will be used. And if no location is specified, then the shape will be draw
 * at the origin (0,0)</p>
 *
 * <p>Here is an example that draws a lowly rectangle:
 * <pre><code>
 *  Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, 50, 50);
 *  ShapePainter p = new ShapePainter(rect);
 *  p.setLocation(new Point2D.Double(20, 10));
 * </code></pre>
 *
 * @author rbair
 */
public class ShapePainter extends AbstractPainter {
    /**
     * The Shape to paint. If null, nothing is painted.
     */
    private Shape shape;
    /**
     * The Stroke to use when painting. If null, the default Stroke for
     * the Graphics2D is used
     */
    private Stroke stroke;
    /**
     * The Paint to use when painting the shape. If null, then the component
     * background color is used
     */
    private Paint paint;
    /**
     * The location at which to draw the shape. If null, 0,0 is used
     */
    private Point2D location = new Point2D.Double(0, 0);
    /**
     * Indicates whether the shape should be filled or drawn.
     */
    private boolean isFilled;
    
    /**
     * Create a new ShapePainter
     */
    public ShapePainter() {
        super();
    }
    
    /**
     * Sets the shape to paint. This shape is not resized when the component
     * bounds are. To do that, create a custom shape that is bound to the
     * component width/height
     *
     * @param s the Shape to paint. May be null
     */
    public void setShape(Shape s) {
        Shape old = getShape();
        this.shape = s;
        firePropertyChange("shape", old, getShape());
    }
    
    /**
     * @return the Shape to paint. May be null
     */
    public Shape getShape() {
        return shape;
    }
    
    /**
     * Sets the stroke to use for painting. If null, then the default Graphics2D
     * stroke use used
     *
     * @param s the Stroke to paint with
     */
    public void setStroke(Stroke s) {
        Stroke old = getStroke();
        this.stroke = s;
        firePropertyChange("stroke", old, getStroke());
    }
    
    /**
     * @return the Stroke to use for painting
     */
    public Stroke getStroke() {
        return stroke;
    }
    
    /**
     * The Paint to use for painting the shape. Can be a Color, GradientPaint,
     * TexturePaint, or any other kind of Paint. If null, the component
     * background is used.
     *
     * @param p the Paint to use for painting the shape. May be null.
     */
    public void setPaint(Paint p) {
        Paint old = getPaint();
        this.paint = p;
        firePropertyChange("paint", old, getPaint());
    }
    
    /**
     * @return the Paint used when painting the shape. May be null
     */
    public Paint getPaint() {
        return paint;
    }
    
    /**
     * Specifies the location at which to place the shape prior to painting.
     * If null, the origin (0,0) is used
     *
     * @param location the Point2D at which to paint the shape. may be null
     */
    public void setLocation(Point2D location) {
        Point2D old = getLocation();
        this.location = location == null ? new Point2D.Double(0, 0) : location;
        firePropertyChange("location", old, getLocation());
    }
    
    /**
     * @return the Point2D location at which to paint the shape. Will never be null
     *         (if it was null, new Point2D.Double(0,0) will be returned)
     */
    public Point2D getLocation() {
        return location;
    }
    
    /**
     * The shape can be filled or simply stroked. By default, the shape is
     * stroked. Setting this property to true fills the shape upon drawing.
     *
     * @param isFilled true if the shape must be filled, false otherwise.
     */
    public void setFilled(boolean isFilled) {
        boolean old = isFilled();
        this.isFilled = isFilled;
        firePropertyChange("paint", old, isFilled());
    }
    
    /**
     * @return true is the shape is filled, false if stroked
     */
    public boolean isFilled() {
        return isFilled;
    }
    
    /**
     * @inheritDoc
     */
    public void paintBackground(Graphics2D g, JComponent component) {
        Paint p = getPaint();
        if (p == null) {
            p = component.getBackground();
        }
        g.setPaint(p);
        
        Stroke s = getStroke();
        if (s != null) {
            g.setStroke(s);
        }
        
        Point2D location = getLocation();
        g.translate(-location.getX(), -location.getY());
        if (!isFilled()) {
            g.draw(getShape());
        } else {
            g.fill(getShape());
        }
    }
}
