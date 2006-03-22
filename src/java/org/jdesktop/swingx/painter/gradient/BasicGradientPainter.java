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
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import javax.swing.UIManager;

/**
 * <p>A Gradient based Painter that uses GradientPaint to paint the gradient.
 * Simply specify the GradientPaint to use.</p>
 *
 * <p>In order for resizing to work properly with GradientPaint
 * it is necessary that the various control points used in
 * these paints be specified in such a manner that they can be reliably resized.
 * For example, BasicGradientPainter takes GradientPaints who's point1 and point2
 * properties are specified between 0 and 1, representing at what percentage of
 * the distance from the origin the gradient begins and ends. Thus, if I created
 * a GradientPaint like this:
 * <pre><code>
 *  GradientPaint gp = new GradientPaint(
 *      new Point2D.Double(.2d, 0),
 *      Color.BLUE,
 *      new Point2D.Double(.8d, 0),
 *      Color.WHITE);
 * </code></pre>
 * 
 * then when painted, the gradient will start with a BLUE at 20% of the width of
 * the component, and finish with WHITE at 80% of the width of the component.</p>
 *
 * <p>Various built in gradients also exist as public static final properties.
 * They are defined as GradientPaints rather than BasicGradientPainters because
 * BasicGradientPainter is mutable and thus don't make very reliable public static
 * final defaults. To use:
 * <pre><code>
 *  panel.setBackgroundPainter(new BasicGradientPainter(BasicGradientPainter.BLUE_EXPERIENCE));
 * </code></pre></p>
 *
 * @author rbair
 */
public class BasicGradientPainter extends AbstractGradientPainter {
//    public static final GradientPaint WHITE_TO_CONTROL_HORZONTAL = new GradientPaint(
//            new Point2D.Double(0, 0),
//            Color.WHITE,
//            new Point2D.Double(1, 0),
//            new UIColor("control"));
//    public static final GradientPaint WHITE_TO_CONTROL_VERTICAL = new GradientPaint(
//            new Point2D.Double(0, 0),
//            Color.WHITE,
//            new Point2D.Double(0, 1),
//            new UIColor("control"));
    public static final GradientPaint BLUE_EXPERIENCE = new GradientPaint(
            new Point2D.Double(0, 0),
            new Color(168, 204, 241),
            new Point2D.Double(0, 1),
            new Color(44, 61, 146));
    public static final GradientPaint MAC_OSX_SELECTED = new GradientPaint(
            new Point2D.Double(0, 0),
            new Color(81, 141, 236),
            new Point2D.Double(0, 1),
            new Color(36, 96, 192));
    public static final GradientPaint MAC_OSX = new GradientPaint(
            new Point2D.Double(0, 0),
            new Color(167, 210, 250),
            new Point2D.Double(0, 1),
            new Color(99, 147, 206));
    public static final GradientPaint AERITH = new GradientPaint(
            new Point2D.Double(0, 0),
            Color.WHITE,
            new Point2D.Double(0, 1),
            new Color(64, 110, 161));
    public static final GradientPaint GRAY = new GradientPaint(
            new Point2D.Double(0, 0),
            new Color(226, 226, 226),
            new Point2D.Double(0, 1),
            new Color(250, 248, 248));
    public static final GradientPaint RED_XP = new GradientPaint(
            new Point2D.Double(0, 0),
            new Color(236, 81, 81),
            new Point2D.Double(0, 1),
            new Color(192, 36, 36));
    public static final GradientPaint NIGHT_GRAY = new GradientPaint(
            new Point2D.Double(0, 0),
            new Color(102, 111, 127),
            new Point2D.Double(0, 1),
            new Color(38, 45, 61));
    public static final GradientPaint NIGHT_GRAY_LIGHT = new GradientPaint(
            new Point2D.Double(0, 0),
            new Color(129, 138, 155),
            new Point2D.Double(0, 1),
            new Color(58, 66, 82));
    
    
    
    private GradientPaint paint;
    
    /**
     * Creates a new instance of BasicGradientPainter
     */
    public BasicGradientPainter() {
    }
    
    /**
     * Creates a new instance of BasicGradientPainter
     */
    public BasicGradientPainter(GradientPaint paint) {
        this.paint = paint;
    }
    
    /**
     * Constructs a simple acyclic <code>BasicGradientPainter</code> object.
     * 
     * @param x1 x coordinate of the first specified
     * <code>Point</code> in user space
     * @param y1 y coordinate of the first specified
     * <code>Point</code> in user space
     * @param startColor <code>Color</code> at the first specified 
     * <code>Point</code>
     * @param x2 x coordinate of the second specified
     * <code>Point</code> in user space
     * @param y2 y coordinate of the second specified
     * <code>Point</code> in user space
     * @param endColor <code>Color</code> at the second specified 
     * <code>Point</code>
     * @throws NullPointerException if either one of colors is null
     */
    public BasicGradientPainter(float x1,
			   float y1,
			   Color startColor,
			   float x2,
			   float y2,
			   Color endColor) {
        this.paint = new GradientPaint(x1, y1, startColor, x2, y2, endColor);
    }

    /**
     * Constructs a simple acyclic <code>BasicGradientPainter</code> object.
     * 
     * @param startPoint the first specified <code>Point</code> in user space
     * @param startColor <code>Color</code> at the first specified 
     * <code>Point</code>
     * @param endPoint the second specified <code>Point</code> in user space
     * @param endColor <code>Color</code> at the second specified 
     * <code>Point</code>
     * @throws NullPointerException if either one of colors or points 
     * is null
     */
    public BasicGradientPainter(Point2D startPoint,
			   Color startColor,
			   Point2D endPoint,
			   Color endColor) {
        
        this.paint = new GradientPaint(startPoint, startColor, endPoint, endColor);
    }

    /**
     * Constructs either a cyclic or acyclic <code>BasicGradientPainter</code>
     * object depending on the <code>boolean</code> parameter.
     * 
     * @param x1 x coordinate of the first specified
     * <code>Point</code> in user space
     * @param y1 y coordinate of the first specified
     * <code>Point</code> in user space
     * @param startColor <code>Color</code> at the first specified 
     * <code>Point</code>
     * @param x2 x coordinate of the second specified
     * <code>Point</code> in user space
     * @param y2 y coordinate of the second specified
     * <code>Point</code> in user space
     * @param endColor <code>Color</code> at the second specified 
     * <code>Point</code>
     * @param cyclic <code>true</code> if the gradient pattern should cycle
     * repeatedly between the two colors; <code>false</code> otherwise
     */
    public BasicGradientPainter(float x1,
			   float y1,
			   Color startColor,
			   float x2,
			   float y2,
			   Color endColor,
			   boolean cyclic) {
	paint = new GradientPaint(x1, y1, startColor, x2, y2, endColor, cyclic);
    }

    /**
     * Constructs either a cyclic or acyclic <code>BasicGradientPainter</code>
     * object depending on the <code>boolean</code> parameter.
     * 
     * @param startPoint the first specified <code>Point</code> 
     * in user space
     * @param startColor <code>Color</code> at the first specified 
     * <code>Point</code>
     * @param endPoint the second specified <code>Point</code> 
     * in user space
     * @param endColor <code>Color</code> at the second specified 
     * <code>Point</code>
     * @param cyclic <code>true</code> if the gradient pattern should cycle
     * repeatedly between the two colors; <code>false</code> otherwise
     * @throws NullPointerException if either one of colors or points 
     * is null
     */
    public BasicGradientPainter(Point2D startPoint,
			   Color startColor,
			   Point2D endPoint,
			   Color endColor,
			   boolean cyclic) {
	paint = new GradientPaint(startPoint, startColor, endPoint, endColor, cyclic);
    }
    
    /**
     * Set the gradient paint to use. This may be null. If null, nothing is painted
     *
     * @param paint the GradientPaint to use
     */
    public void setGradientPaint(GradientPaint paint) {
        GradientPaint old = getGradientPaint();
        this.paint = paint;
        firePropertyChange("gradientPaint", old, getGradientPaint());
    }
    
    /**
     * @return the GradientPaint used for painting. This may be null
     */
    public GradientPaint getGradientPaint() {
        return paint;
    }
    
    /**
     * @inheritDoc
     */
    protected Paint calculateSizedPaint(int width, int height) {
        GradientPaint paint = getGradientPaint();
        if (paint == null) {
            return null;
        }
        
        Point2D startPoint = paint.getPoint1();
        Point2D endPoint = paint.getPoint2();
        
        double x1 = isResizeHorizontal() ? startPoint.getX() * width : startPoint.getX();
        double y1 = isResizeVertical() ? startPoint.getY() * height : startPoint.getY();
        double x2 = isResizeHorizontal() ? endPoint.getX() * width : endPoint.getX();
        double y2 = isResizeVertical() ? endPoint.getY() * height : endPoint.getY();
        startPoint = new Point2D.Double(x1, y1);
        endPoint = new Point2D.Double(x2, y2);
        
        return new GradientPaint(
                startPoint,
                paint.getColor1(),
                endPoint,
                paint.getColor2());
    }

    //Experimental support for getting colors for gradients out of UIManager
    //and hiding that fact from users, at least part of the time
//    private static final class UIColor extends Color {
//        private Color c = null;
//        private Object key;
//        
//        public UIColor(Object key) {
//            super(0xffffffff);
//            this.key = key;
//            maybeUpdate();
//        }
//        
//        private void maybeUpdate() {
//            Color newc = UIManager.getColor(key);
//            if (c != newc) {
//                c = newc;
//            }
//            if (c == null) {
//                c = Color.WHITE;
//            }
//        }
//        
//        public float[] getComponents(ColorSpace cspace, float[] compArray) {
//            maybeUpdate();
//            return c.getComponents(cspace, compArray);
//        }
//
//        public float[] getColorComponents(ColorSpace cspace, float[] compArray) {
//            maybeUpdate();
//            return c.getColorComponents(cspace, compArray);
//        }
//
//        public PaintContext createContext(ColorModel cm, Rectangle r, Rectangle2D r2d, AffineTransform xform, RenderingHints hints) {
//            maybeUpdate();
//            return c.createContext(cm, r, r2d, xform, hints);
//        }
//
//        public float[] getRGBComponents(float[] compArray) {
//            maybeUpdate();
//            return c.getRGBComponents(compArray);
//        }
//
//        public float[] getRGBColorComponents(float[] compArray) {
//            maybeUpdate();
//            return c.getRGBColorComponents(compArray);
//        }
//
//        public float[] getColorComponents(float[] compArray) {
//            maybeUpdate();
//            return c.getColorComponents(compArray);
//        }
//
//        public float[] getComponents(float[] compArray) {
//            maybeUpdate();
//            return c.getComponents(compArray);
//        }
//
//        public int getGreen() {
//            maybeUpdate();
//            return c.getGreen();
//        }
//
//        public int getBlue() {
//            maybeUpdate();
//            return c.getBlue();
//        }
//
//        public ColorSpace getColorSpace() {
//            maybeUpdate();
//            return c.getColorSpace();
//        }
//
//        public Color brighter() {
//            maybeUpdate();
//            return c.brighter();
//        }
//
//        public int getAlpha() {
//            maybeUpdate();
//            return c.getAlpha();
//        }
//
//        public int getRed() {
//            maybeUpdate();
//            return c.getRed();
//        }
//
//        public int getRGB() {
//            maybeUpdate();
//            return c.getRGB();
//        }
//
//        public int getTransparency() {
//            maybeUpdate();
//            return c.getTransparency();
//        }
//
//        public Color darker() {
//            maybeUpdate();
//            return c.darker();
//        }
//    }
}
