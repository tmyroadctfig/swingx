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

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;
import org.apache.batik.ext.awt.LinearGradientPaint;

/**
 * <p>A Gradient based painter used for painting "multi-stop" gradients. These are
 * gradients that imploys more than 2 colors, where each color is defined along
 * with a float value between 0 and 1 indicating at what point along the gradient
 * the new color is used.</p>
 *
 * <p>As with BasicGradienPainter and mentioned in AbstractGradientPainter, the values
 * given to the startPoint and endPoint of the LinearGradientPainter are crucial. They
 * represent what distance from the origin the gradient should begin and end at,
 * depending on the size of the component. That is, they must be specified as values between
 * 0 and 1, where 0 means "all the way on the left/top" and 1 means "all the way on the
 * right/bottom".</p>
 *
 * <p><strong>NOTE: LinearGradientPainter relies on LinearGradientPaint, which is
 * included in the optional jar MultipleGradientPaint.jar. Be sure to have this
 * jar on your classpath if you use this class</strong></p>
 *
 * @author rbair
 */
public class LinearGradientPainter extends AbstractGradientPainter {
    public static final LinearGradientPaint ORANGE_DELIGHT = new LinearGradientPaint(
            new Point2D.Double(0, 0),
            new Point2D.Double(0, 1),
            new float[] {0f, .5f, .51f, 1f},
            new Color[] {
                new Color(248, 192, 75),
                new Color(253, 152, 6),
                new Color(243, 133, 0),
                new Color(254, 124, 0)});
    public static final LinearGradientPaint BLACK_STAR = new LinearGradientPaint(
            new Point2D.Double(0, 0),
            new Point2D.Double(0, 1),
            new float[] {0f, .5f, .51f, 1f},
            new Color[] {
                new Color(54, 62, 78),
                new Color(32, 39, 55),
                new Color(74, 82, 96),
                new Color(123, 132, 145)});
    public static final LinearGradientPaint BLACK_PERSPECTIVE = new LinearGradientPaint (
            new Point2D.Double(0, 0),
            new Point2D.Double(0, 1),
            new float[] {0f, .5f, 1f},
            new Color[] {
                Color.BLACK,
                new Color(110, 110, 110),
                Color.BLACK});
    
    private LinearGradientPaint paint;
    
    /** 
     * Creates a new instance of LinearGradientPainter 
     */
    public LinearGradientPainter() {
    }
    
    /**
     * Creates a new instance of LinearGradientPainter with the given LinearGradientPaint
     * as input
     *
     * @param paint the Paint to use
     */
    public LinearGradientPainter(LinearGradientPaint paint) {
        this.paint = paint;
    }
    
    /**
     * Set the gradient paint to use. This may be null. If null, nothing is painted
     *
     * @param paint the LinearGradientPaint to use
     */
    public void setGradientPaint(LinearGradientPaint paint) {
        LinearGradientPaint old = getGradientPaint();
        this.paint = paint;
        firePropertyChange("gradientPaint", old, getGradientPaint());
    }
    
    /**
     * @return the LinearGradientPaint used for painting. This may be null
     */
    public LinearGradientPaint getGradientPaint() {
        return paint;
    }
    
    /**
     * @inheritDoc
     */
    protected Paint calculateSizedPaint(int width, int height) {
        LinearGradientPaint paint = getGradientPaint();
        if (paint == null) {
            return null;
        }
        
        Point2D startPoint = paint.getStartPoint();
        Point2D endPoint = paint.getEndPoint();
        
        double x1 = isResizeHorizontal() ? startPoint.getX() * width : startPoint.getX();
        double y1 = isResizeVertical() ? startPoint.getY() * height : startPoint.getY();
        double x2 = isResizeHorizontal() ? endPoint.getX() * width : endPoint.getX();
        double y2 = isResizeVertical() ? endPoint.getY() * height : endPoint.getY();
        startPoint = new Point2D.Double(x1, y1);
        endPoint = new Point2D.Double(x2, y2);
        
        return new LinearGradientPaint(
                startPoint,
                endPoint,
                paint.getFractions(),
                paint.getColors(),
                paint.getCycleMethod(),
                paint.getColorSpace(),
                paint.getTransform());
    }
}
