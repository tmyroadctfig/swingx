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
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import org.jdesktop.beans.AbstractBean;
import org.jdesktop.swingx.util.PaintUtils;

/**
 * <p>A convenient base class from which concrete Painter implementations may
 * extend. It extends AbstractBean and thus provides property change notification
 * (which is crucial for the Painter implementations to be available in a
 * GUI builder).Sublasses simply need
 * to extend AbstractPainter and implement the doPaint method.
 * 
 * <p>For example, here is the doPaint method of RectanglePainter:
 * <pre><code>
 *  public void doPaint(Graphics2D g, T obj, int width, int height) {
 *      g.setPaint(getPaint());
 *      g.fillRect(0, 0, width, height);
 *  }
 * </code></pre>
 * 
 * <p>AbstractPainter provides a very useful default implementation of
 * the paint method. It:
 * <ol>
 *  <li>Sets any specified rendering hints</li>
 *  <li>Sets the Composite if there is one</li>
 *  <li>Delegates to doPaint</li>
 * <ol></p>
 * 
 * <p>Specifying rendering hints can greatly improve the visual impact of your
 * applications. For example, by default Swing doesn't do much in the way of
 * antialiasing (except for Fonts, but that's another story). Pinstripes don't
 * look so good without antialiasing. So if I were going to paint pinstripes, I
 * might do it like this:
 * <pre><code>
 *   PinstripePainter p = new PinstripePainter();
 *   p.setAntialiasing(Antialiasing.ON);
 * </code></pre></p>
 * 
 * <p>You can read more about antialiasing and other rendering hints in the
 * java.awt.RenderingHints documentation. <strong>By nature, changing the rendering
 * hints may have an impact on performance. Certain hints require more
 * computation, others require less</strong></p>
 * 
 * @author rbair
 */
public abstract class AbstractPainter<T> extends AbstractBean implements Painter<T> {
    //--------------------------------------------------- Instance Variables
    /**
     * A hint as to whether or not to attempt caching the image
     */
    private boolean useCache = false;
    /**
     * The cached image, if useCache is true
     */
    private transient SoftReference<BufferedImage> cachedImage;
    /**
     * The Effects to apply to the results of the paint() operation
     */
    private BufferedImageOp[] effects = new BufferedImageOp[0];
    
    /**
     * Creates a new instance of AbstractPainter
     */
    public AbstractPainter() {
    }
    
    /**
     * Returns true whether or not the painter is using caching.
     * @return whether or not the cache should be used
     */
    protected boolean isUseCache() {
        return useCache;
    }
    
    /**
     * <p>A convenience method for specifying the effects to use based on
     * BufferedImageOps. These will each be individually wrapped by an ImageFilter
     * and then setFilters(Effect... effects) will be called with the resulting
     * array</p>
     * 
     * 
     * @param effects the BufferedImageOps to wrap as effects
     */
    public void setFilters(BufferedImageOp ... effects) {
        BufferedImageOp[] old = getFilters();
        this.effects = new BufferedImageOp[effects == null ? 0 : effects.length];
        if (effects != null) {
            System.arraycopy(effects, 0, this.effects, 0, effects.length);
        }
        firePropertyChange("effects", old, getFilters());
    }
    
    /**
     * A defensive copy of the Effects to apply to the results
     *  of the AbstractPainter's painting operation. The array may
     *  be empty but it Will never be null.
     * @return the array of effects applied to this painter
     */
    public BufferedImageOp[] getFilters() {
        BufferedImageOp[] results = new BufferedImageOp[effects.length];
        System.arraycopy(effects, 0, results, 0, results.length);
        return results;
    }

    private boolean antialiasing = true;
    /**
     * Returns if antialiasing is turned on or not. The default value is true. 
     *  This is a bound property.
     * @return the current antialiasing setting
     */
    public boolean isAntialiasing() {
        return antialiasing;
    }
    /**
     * Sets the antialiasing setting.  This is a bound property.
     * @param value the new antialiasing setting
     */
    public void setAntialiasing(boolean value) {
        boolean old = isAntialiasing();
        antialiasing = value;
        firePropertyChange("antialiasing", old, isAntialiasing());
    }
    
    
    private boolean fractionalMetrics = true;
    
    /**
     * Gets the current fractional metrics setting. This property determines if Fractional Metrics will
     * be used when drawing text. @see java.awt.RenderingHints.KEY_FRACTIONALMETRICS.
     * @return the current fractional metrics setting
     */
    public boolean isFractionalMetricsOn() {
        return fractionalMetrics;
    }
    
    /**
     * Sets a new value for the fractional metrics setting. This setting determines if fractional
     * metrics should be used when drawing text. @see java.awt.RenderingHints.KEY_FRACTIONALMETRICS.
     * @param fractionalMetrics the new fractional metrics setting
     */
    public void setFractionalMetricsOn(boolean fractionalMetrics) {
        boolean old = isFractionalMetricsOn();
        this.fractionalMetrics = fractionalMetrics;
        firePropertyChange("fractionalMetricsOn", old, isFractionalMetricsOn());
    }

    /**
     * An enum representing the possible interpolation values of Bicubic, Bilinear, and
     * Nearest Neighbor. These map to the underlying RenderingHints, 
     * but are easier to use and serialization safe.
     */
    public enum Interpolation { 
        /**
         * use bicubic interpolation
         */
        Bicubic(RenderingHints.VALUE_INTERPOLATION_BICUBIC), 
        /**
         * use bilinear interpolation
         */
        Bilinear(RenderingHints.VALUE_INTERPOLATION_BILINEAR), 
        /**
         * use nearest neighbor interpolation
         */
        NearestNeighbor(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        
        private Object value;
        Interpolation(Object value) {
            this.value = value;
        }
        private void configureGraphics(Graphics2D g) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, value);
        }
    }
    
    private Interpolation interpolation = Interpolation.NearestNeighbor;
    
    /**
     * Gets the current interpolation setting. This property determines if interpolation will
     * be used when drawing scaled images. @see java.awt.RenderingHints.KEY_INTERPOLATION.
     * @return the current interpolation setting
     */
    public Interpolation getInterpolation() {
        return interpolation;
    }
    
    /**
     * Sets a new value for the interpolation setting. This setting determines if interpolation
     * should be used when drawing scaled images. @see java.awt.RenderingHints.KEY_INTERPOLATION.
     * @param value the new interpolation setting
     */
    public void setInterpolation(Interpolation value) {
        Object old = getInterpolation();
        this.interpolation = value;
        firePropertyChange("interpolation", old, getInterpolation());
    }

    /**
     * Overrides paint to support painters, filters, and a global alpha value.
     * 
     * @param g 
     * @param component 
     * @param width 
     * @param height 
     */
    public void paint(Graphics2D g, T component, int width, int height) {
        if(!isVisible()) {
            return;
        }

        configureGraphics(g, component);
        
        //if I am caching, and the cache is not null, and the image has the
        //same dimensions as the component, then simply paint the image
        BufferedImage image = cachedImage == null ? null : cachedImage.get();
        if (isUseCache() && image != null
                && image.getWidth() == width
                && image.getHeight() == height) {
            g.drawImage(image, 0, 0, null);
        } else {
            BufferedImageOp[] effects = getFilters();
            if (effects.length > 0 || isUseCache()) {
                image = PaintUtils.createCompatibleImage(
                        width,
                        height,
                        Transparency.TRANSLUCENT);
                
                Graphics2D gfx = image.createGraphics();
                configureGraphics(gfx, component);
                doPaint(gfx, component, width, height);
                gfx.dispose();
                
                for (BufferedImageOp effect : effects) {
                    image = effect.filter(image,null);
                }
                
                g.drawImage(image, 0, 0, null);
                
                if (isUseCache()) {
                    cachedImage = new SoftReference<BufferedImage>(image);
                }
            } else {
                doPaint(g, component, width, height);
            }
        }
    }
    
    /**
     * Utility method for configuring the given Graphics2D with the rendering hints,
     * composite, and clip
     */
    private void configureGraphics(Graphics2D g, T c) {
        if(c instanceof JComponent) {
            g.setFont(((JComponent)c).getFont());
        }
        
        if(isAntialiasing()) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        
        getInterpolation().configureGraphics(g);
    }
    
    
    /**
     * Subclasses must implement this method and perform custom painting operations
     * here.
     * @param width 
     * @param height 
     * @param g The Graphics2D object in which to paint
     * @param component The JComponent that the Painter is delegate for.
     */
    protected abstract void doPaint(Graphics2D g, T component, int width, int height);
    
    
    /**
     * Holds value of property visible.
     */
    private boolean visible = true;
    
    /**
     * Getter for property visible.
     * 
     * @return Value of property visible.
     */
    public boolean isVisible() {
        return this.visible;
    }
    
    /**
     * Setter for property visible.
     * 
     * @param visible New value of property visible.
     */
    public void setVisible(boolean visible) {
        boolean oldEnabled = isVisible();
        this.visible = visible;
        firePropertyChange("visible", oldEnabled, isVisible());
    }
}
