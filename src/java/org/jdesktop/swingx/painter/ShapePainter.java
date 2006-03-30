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
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import org.jdesktop.swingx.util.Resize;

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
     * Specifies if/how resizing (relocating) the location should occur.
     */
    private Resize resizeLocation = Resize.BOTH;
    /**
     * Specifies if/how resizing of the shape should occur
     */
    private Resize resize = Resize.BOTH;
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
     * Create a new ShapePainter with the specified shape.
     *
     * @param shape the shape to paint
     */
    public ShapePainter(Shape shape) {
        super();
        this.shape = shape;
    }
    
    /**
     * Create a new ShapePainter with the specified shape and paint.
     *
     * @param shape the shape to paint
     * @param paint the paint to be used to paint the shape
     */
    public ShapePainter(Shape shape, Paint paint) {
        super();
        this.shape = shape;
        this.paint = paint;
    }
    
    /**
     * Create a new ShapePainter with the specified shape and paint. The shape
     * can be filled or stroked (only the ouline is painted).
     *
     * @param shape the shape to paint
     * @param paint the paint to be used to paint the shape
     * @param isFilled true to fill the shape, false to stroke the outline
     */
    public ShapePainter(Shape shape, Paint paint, boolean isFilled) {
        super();
        this.shape = shape;
        this.paint = paint;
        this.isFilled = isFilled;
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
     * Specifies the resize behavior for the location property. If r is
     * Resize.HORIZONTAL or Resize.BOTH, then the x value of the location
     * will be treated as if it were a percentage of the width of the component.
     * Likewise, Resize.VERTICAL or Resize.BOTH will affect the y value. For
     * example, if I had a location (.3, .8) then the X will be situated at
     * 30% of the width and the Y will be situated at 80% of the height.
     *
     * @param r value indicating whether/how to resize the Location property when
     *        painting. If null, Resize.BOTH will be used
     */
    public void setResizeLocation(Resize r) {
        Resize old = getResizeLocation();
        this.resizeLocation = r == null ? r.NONE : r;
        firePropertyChange("resizeLocation", old, getResizeLocation());
    }

    /**
     * @return value indication whether/how to resize the location property.
     *         This will never be null
     */
    public Resize getResizeLocation() {
        return resizeLocation;
    }
    
    /**
     * Specifies the resize behavior of the shape. As with all other properties
     * that rely on Resize, the value of the width/height of the shape will
     * represent a percentage of the width/height of the component, as a value
     * between 0 and 1
     *
     * @param r value indication whether/how to resize the shape. If null,
     *        Resize.NONE will be used
     */
    public void setResize(Resize r) {
        Resize old = getResize();
        this.resize = r == null ? r.NONE : r;
        firePropertyChange("resize", old, getResize());
    }
    
    /**
     * @return value indication whether/how to resize the shape. Will never be null
     */
    public Resize getResize() {
        return resize;
    }
    
    /**
     * @inheritDoc
     */
    public void paintBackground(Graphics2D g, JComponent component) {
        //set the paint
        Paint p = getPaint();
        if (p == null) {
            p = component.getBackground();
        }
        g.setPaint(p);
        
        //set the stroke if it is not null
        Stroke s = getStroke();
        if (s != null) {
            g.setStroke(s);
        }
        
        //handle the location
        Point2D location = getLocation();
        Resize resizeLocation = getResizeLocation();
        double x = location.getX();
        double y = location.getY();
        if (resizeLocation == Resize.HORIZONTAL || resizeLocation == Resize.BOTH) {
            x = x * component.getWidth();
        }
        if (resizeLocation == Resize.VERTICAL || resizeLocation == Resize.BOTH) {
            y = y * component.getHeight();
        }
        g.translate(-location.getX(), -location.getY());
        
        //resize the shape if necessary
        Shape shape = getShape();
        Rectangle2D bounds = shape.getBounds2D();
        double width = 1;
        double height = 1;
        Resize resize = getResize();
        if (resize == Resize.HORIZONTAL || resize == Resize.BOTH) {
            width = component.getWidth();
        }
        if (resize == Resize.VERTICAL || resize == Resize.BOTH) {
            height = component.getHeight();
        }
        
        if (shape instanceof RoundRectangle2D) {
            RoundRectangle2D rect = (RoundRectangle2D)shape;
            shape = new RoundRectangle2D.Double(
                    rect.getX(), rect.getY(), width, height,
                    rect.getArcWidth(), rect.getArcHeight());
        } else {
            shape = AffineTransform.getScaleInstance(
                    width, height).createTransformedShape(shape);
        }
        
        //draw/fill the shape
        if (!isFilled()) {
            g.draw(shape);
        } else {
            g.fill(shape);
        }
    }
}
