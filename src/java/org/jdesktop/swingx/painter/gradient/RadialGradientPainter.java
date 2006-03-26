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

package org.jdesktop.swingx.painter.gradient;

import java.awt.Paint;
import java.awt.geom.Point2D;
import org.apache.batik.ext.awt.RadialGradientPaint;
import org.jdesktop.swingx.util.Resize;

/**
 * <p>A Gradient based painter used for painting "multi-stop" radial gradients. These are
 * gradients that imploys more than 2 colors, where each color is defined along
 * with a float value between 0 and 1 indicating at what point along the gradient
 * the new color is used.</p>
 *
 * <p>As with BasicGradienPainter and mentioned in AbstractGradientPainter, the values
 * given to the centerPoint, radius, and focusPoint of the RadialGradientPainter are crucial. They
 * represent what distance from the origin the gradient should begin and end at,
 * depending on the size of the component. That is, they must be specified as values between
 * 0 and 1, where 0 means "all the way on the left/top" and 1 means "all the way on the
 * right/bottom".</p>
 *
 * <p>In addition, the resize behavior of the radius is specified in the resizeRadius
 * property. If HORIZONTAL, then the width of the component is used to calculate
 * the new radius. If VERTICAL then the height of the component is used. If BOTH,
 * then the Math.min(width, height) is used. If NONE, then no resize occurs for
 * the radius.</p>
 *
 * <p><strong>NOTE: RadialGradientPainter relies on LinearGradientPaint, which is
 * included in the optional jar MultipleGradientPaint.jar. Be sure to have this
 * jar on your classpath if you use this class</strong></p>
 *
 * @author rbair
 */
public class RadialGradientPainter extends AbstractGradientPainter {
    private RadialGradientPaint paint;
    private Resize resizeRadius = Resize.BOTH;
    
    /** Creates a new instance of RadialGradientPainter */
    public RadialGradientPainter() {
    }
    
    /** 
     * Creates a new instance of RadialGradientPainter 
     * with the given RadialGradientPaint
     *
     * @param paint the RadialGradientPaint to use
     */
    public RadialGradientPainter(RadialGradientPaint paint) {
        this.paint = paint;
    }
    
    /**
     * Set the gradient paint to use. This may be null. If null, nothing is painted
     *
     * @param paint the RadialGradientPaint to use
     */
    public void setGradientPaint(RadialGradientPaint paint) {
        RadialGradientPaint old = getGradientPaint();
        this.paint = paint;
        firePropertyChange("gradientPaint", old, getGradientPaint());
    }
    
    /**
     * @return the RadialGradientPaint used for painting. This may be null
     */
    public RadialGradientPaint getGradientPaint() {
        return paint;
    }
    
    /**
     * Specifies the resize behavior for the radius of the RadialGradientPaint.
     * If HORIZONTAL, then the width of the component is used to calculate
     * the new radius. If VERTICAL then the height of the component is used. If BOTH,
     * then the Math.min(width, height) is used. If NONE, then no resize occurs for
     * the radius.
     *
     * @param r the Resize behavior for the radius
     */
    public void setResizeRadius(Resize r) {
        Resize old = getResizeRadius();
        this.resizeRadius = r;
        firePropertyChange("resizeRadius", old, getResizeRadius());
    }
    
    /**
     * @return the resize behavior for the radius
     */
    public Resize getResizeRadius() {
        return resizeRadius;
    }
    
    /**
     * @inheritDoc
     */
    protected Paint calculateSizedPaint(int width, int height) {
        RadialGradientPaint paint = getGradientPaint();
        if (paint == null) {
            return null;
        }
        
        Point2D centerPoint = paint.getCenterPoint();
        Point2D focusPoint = paint.getFocusPoint();
        
        double x1 = isResizeHorizontal() ? centerPoint.getX() * width : centerPoint.getX();
        double y1 = isResizeVertical() ? centerPoint.getY() * height : centerPoint.getY();
        double x2 = isResizeHorizontal() ? focusPoint.getX() * width : focusPoint.getX();
        double y2 = isResizeVertical() ? focusPoint.getY() * height : focusPoint.getY();
        centerPoint = new Point2D.Double(x1, y1);
        focusPoint = new Point2D.Double(x2, y2);

        float radius = paint.getRadius();
        Resize r = getResizeRadius();
        r = r == null ? Resize.BOTH : r;
        switch (r) {
            case HORIZONTAL:
                radius = radius * width;
                break;
            case VERTICAL:
                radius = radius * height;
                break;
            case BOTH:
                radius = radius * Math.min(width, height);
                break;
            case NONE:
                break;
            default:
                throw new AssertionError("Cannot happen");
        }
        
        return new RadialGradientPaint(
                centerPoint,
                radius,
                focusPoint,
                paint.getFractions(),
                paint.getColors(),
                paint.getCycleMethod(),
                paint.getColorSpace(),
                paint.getTransform());
    }
}
