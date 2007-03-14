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
 * extend. It extends JavaBean and thus provides property change notification
 * (which is crucial for the Painter implementations to be available in a
 * GUI builder). It also saves off the Graphics2D state in its "saveState" method,
 * and restores that state in the "restoreState" method. Sublasses simply need
 * to extend AbstractPainter and implement the doPaint method.
 * 
 * <p>For example, here is the doPaint method of BackgroundPainter:
 * <pre><code>
 *  public void doPaint(Graphics2D g, JComponent component) {
 *      g.setColor(component.getBackground());
 *      g.fillRect(0, 0, component.getWidth(), component.getHeight());
 *  }
 * </code></pre>
 * 
 * <p>AbstractPainter provides a very useful default implementation of
 * the paint method. It:
 * <ol>
 *  <li>Saves off the old state</li>
 *  <li>Sets any specified rendering hints</li>
 *  <li>Sets the Composite if there is one</li>
 *  <li>Delegates to doPaint</li>
 *  <li>Restores the original Graphics2D state</li>
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
    //------------------------------------------------- Saved Graphics State
    private boolean stateSaved = false;
    private Stroke oldStroke;
    private AffineTransform oldTransform;
    private Shape oldClip;
    private Color oldBackground;
    private Color oldColor;
    
    //--------------------------------------------------- Instance Variables
    /**
     * A Shape that is used to clip the graphics area. Anything within this
     * clip shape is included in the final output.
     */
    //private Shape clip;
    /**
     * RenderingHints to apply when painting
     */
    private RenderingHints renderingHints;
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
     * <p>Sets whether to cache the painted image with a SoftReference in a BufferedImage
     * between calls. If true, and if the size of the component hasn't changed,
     * then the cached image will be used rather than causing a painting operation.</p>
     *
     * <p>This should be considered a hint, rather than absolute. Several factors may
     * force repainting, including low memory, different component sizes, or possibly
     * new rendering hint settings, etc.</p>
     *
     * @param b whether or not to use the cache
     */
    /*
    public void setUseCache(boolean b) {
        boolean old = isUseCache();
        useCache = b;
        firePropertyChange("useCache", old, isUseCache());
        //if there was a cached image and I'm no longer using the cache, blow it away
        if (cachedImage != null && !isUseCache()) {
            cachedImage = null;
        }
    }
     */
    
    /**
     * Returns true whether or not the painter is using caching.
     * @return whether or not the cache should be used
     */
    protected boolean isUseCache() {
        return useCache;
    }
    
    /*
     * <p>Sets the effects to apply to the results of the AbstractPainter's
     * painting operation. Some common effects include blurs, shadows, embossing,
     * and so forth. If the given effects is a null array, no effects will be used</p>
     *
     * @param effects the Effects to apply to the results of the AbstractPainter's
     *                painting operation
     */
    /*
    public void setFilters(ImageFilter... effects) {
        ImageFilter[] old = getFilters();
        this.effects = new ImageFilter[effects == null ? 0 : effects.length];
        if (effects != null) {
            System.arraycopy(effects, 0, this.effects, 0, effects.length);
        }
        firePropertyChange("effects", old, getFilters());
    }*/
    
    
    /**
     * <p>A convenience method for specifying the effects to use based on
     * BufferedImageOps. These will each be individually wrapped by an ImageFilter
     * and then setFilters(Effect... effects) will be called with the resulting
     * array</p>
     * 
     * 
     * @param filters the BufferedImageOps to wrap as effects
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
    
    /**
     * Specifies the Shape to use for clipping the painting area. This
     * may be null
     *
     * @param clip the Shape to use to clip the area. Whatever is inside this
     *        shape will be kept, everything else "clipped". May be null. If
     *        null, the clipping is not set on the graphics object
     */
    /*
    public void setClip(Shape clip) {
        Shape old = getClip();
        this.clip = clip;
        firePropertyChange("clip", old, getClip());
    }*/
    
    /**
     * @return the clipping shape
     */
    /*
    public Shape getClip() {
        return clip;
    }*/
    
    
    /**
     * Sets the Composite to use. For example, you may specify a specific
     * AlphaComposite so that when this Painter paints, any content in the
     * drawing area is handled properly
     *
     * @param c The composite to use. If null, then no composite will be
     *        specified on the graphics object
     *//*
    public void setComposite(Composite c) {
        Composite old = getComposite();
        this.composite = c;
        firePropertyChange("composite", old, getComposite());
    }*/
    
    /**
     * Gets the current value of the composite property
     * @return current value of the composite property
     *//*
    public Composite getComposite() {
        return composite;
    }*/
    
    
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
    public void setFractionalMetricsOn(boolean frationalMetrics) {
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
     * 
     * 
     * @inheritDoc 
     * @param g 
     * @param component 
     * @param width 
     * @param height 
     */
    public void paint(Graphics2D g, T component, int width, int height) {
        if(!isVisible()) {
            return;
        }
        //saveState(g);
        
        //Graphics2D oldGraphics = g;
        //g = (Graphics2D)g.create();
        
        configureGraphics(g, component);
        
        //if I am cacheing, and the cache is not null, and the image has the
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
        
        //restoreState(g);
        g.dispose();
    }
    
    /**
     * Utility method for configuring the given Graphics2D with the rendering hints,
     * composite, and clip
     */
    private void configureGraphics(Graphics2D g, T c) {
        //if (getComposite() != null) {
            //g.setComposite(getComposite());
        //}
        /*
        Shape clip = getClip();
        if (clip != null) {
            g.setClip(clip);
        }*/
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
        /*
        if(interpolation.equals(Interpolation.Bicubic)) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        }
        if(interpolation.equals(Interpolation.Bilinear)) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }
        if(interpolation.equals(Interpolation.NearestNeighbor)) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        }*/
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
    public void setVisible(boolean enabled) {
        boolean oldEnabled = this.isVisible();
        this.visible = enabled;
        firePropertyChange("visible", oldEnabled, enabled);
    }
}
