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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.lang.ref.SoftReference;

import org.jdesktop.beans.AbstractBean;
import org.jdesktop.swingx.util.PaintUtils;
import org.jdesktop.swingx.graphics.GraphicsUtilities;

/**
 * <p>A convenient base class from which concrete {@link Painter} implementations may
 * extend. It extends {@link org.jdesktop.beans.AbstractBean} as a convenience for
 * adding property change notification support. In addition, <code>AbstractPainter</code>
 * provides subclasses with the ability to cache painting operations, configure the
 * drawing surface with common settings (such as antialiasing and interpolation), and
 * toggle whether a subclass paints or not via the <code>visibility</code> property.</p>
 *
 * <p>Subclasses of <code>AbstractPainter</code> generally need only override the
 * {@link doPaint(Graphics2D, T, int, int)} method. If a subclass requires more control
 * over whether cacheing is enabled, or for configuring the graphics state, then it
 * may override the appropriate protected methods to interpose its own behavior.</p>
 * 
 * <p>For example, here is the doPaint method of a simple <code>Painter</code> that
 * paints an opaque rectangle:
 * <pre><code>
 *  public void doPaint(Graphics2D g, T obj, int width, int height) {
 *      g.setPaint(Color.BLUE);
 *      g.fillRect(0, 0, width, height);
 *  }
 * </code></pre></p>
 *
 * @author rbair
 */
public abstract class AbstractPainter<T> extends AbstractBean implements Painter<T> {
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

    //--------------------------------------------------- Instance Variables
    /**
     * A hint as to whether or not to attempt caching the image
     */
    private boolean cache = true;
    /**
     * The cached image, if useCache() returns true
     */
    private transient SoftReference<BufferedImage> cachedImage;
    private BufferedImageOp[] filters = new BufferedImageOp[0];
    private boolean antialiasing = true;
    private Interpolation interpolation = Interpolation.NearestNeighbor;
    private boolean visible = true;

    /**
     * Creates a new instance of AbstractPainter.
     */
    public AbstractPainter() {
    }

    /**
     * A defensive copy of the Effects to apply to the results
     *  of the AbstractPainter's painting operation. The array may
     *  be empty but it will never be null.
     * @return the array of filters applied to this painter
     */
    public final BufferedImageOp[] getFilters() {
        BufferedImageOp[] results = new BufferedImageOp[filters.length];
        System.arraycopy(filters, 0, results, 0, results.length);
        return results;
    }

    /**
     * <p>A convenience method for specifying the filters to use based on
     * BufferedImageOps. These will each be individually wrapped by an ImageFilter
     * and then setFilters(Effect... filters) will be called with the resulting
     * array</p>
     * 
     * 
     * @param effects the BufferedImageOps to wrap as filters
     */
    public void setFilters(BufferedImageOp ... effects) {
        if (effects == null) effects = new BufferedImageOp[0];
        BufferedImageOp[] old = getFilters();
        this.filters = new BufferedImageOp[effects == null ? 0 : effects.length];
        System.arraycopy(effects, 0, this.filters, 0, this.filters.length);
        clearCache();
        firePropertyChange("filters", old, getFilters());
    }

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
        if (old != value) clearCache();
        firePropertyChange("antialiasing", old, isAntialiasing());
    }

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
        this.interpolation = value == null ? Interpolation.NearestNeighbor : value;
        if (old != value) clearCache();
        firePropertyChange("interpolation", old, getInterpolation());
    }

    /**
     * Gets the visible property. This controls if the painter should
     * paint itself. It is true by default. Setting visible to false
     * is good when you want to temporarily turn off a painter. An example
     * of this is a painter that you only use when a button is highlighted.
     *
     * @return current value of visible property
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * <p>Sets the visible property. This controls if the painter should
     * paint itself. It is true by default. Setting visible to false
     * is good when you want to temporarily turn off a painter. An example
     * of this is a painter that you only use when a button is highlighted.</p>
     *
     * @param visible New value of visible property.
     */
    public void setVisible(boolean visible) {
        boolean old = isVisible();
        this.visible = visible;
        if (old != visible) clearCache(); //not the most efficient, but I must do this otherwise a CompoundPainter
                                          //or other aggregate painter won't know that it is now invalid
                                          //there might be a tricky solution involving isCacheCleared, and not
                                          //*really* throwing away the cache, but that is a performance optimization
        firePropertyChange("visible", old, isVisible());
    }

    /**
     * <p>Gets whether this <code>AbstractPainter</code> can be cached as an image.
     * If cacheing is enabled, then it is the responsibility of the developer to
     * invalidate the painter (via {@link #clearCache}) if external state has
     * changed in such a way that the painter is invalidated and needs to be
     * repainted.</p>
     *
     * @return whether this is cacheable
     */
    public boolean isCacheable() {
        return cache;
    }

    /**
     * <p>Sets whether this <code>AbstractPainter</code> can be cached as an image.
     * If true, this is treated as a hint. That is, a cache may or may not be used.
     * The {@link #useCache} method actually determines whether the cache is used.
     * However, if false, then this is treated as an absolute value. That is, no
     * cache will be used.</p>
     *
     * @param cacheable
     */
    public void setCacheable(boolean cacheable) {
        boolean old = isCacheable();
        this.cache = cacheable;
        firePropertyChange("cacheable", old, isCacheable());
    }

    /**
     * <p>Call this method to clear the cache. This may be called whether there is
     * a cache being used or not. If cleared, on the next call to <code>paint</code>,
     * the painting routines will be called.</p>
     */
    public final void clearCache() {
        boolean old = isCacheCleared();
        BufferedImage cache = cachedImage == null ? null : cachedImage.get();
        if (cache != null) {
            cache.flush();
        }
        cachedImage = null;
        firePropertyChange("cacheCleared", old, isCacheCleared());
    }

    public boolean isCacheCleared() {
        BufferedImage cache = cachedImage == null ? null : cachedImage.get();
        return useCache() && cache == null;
    }

    protected void validateCache(T object) {
    }

    /**
     * Returns true whether or not the painter is using caching.
     * @return whether or not the cache should be used
     */
    protected boolean useCache() {
        return isCacheable() && filters.length > 0;  //NOTE, I can only do this because getFilters() is final
    }

    /**
     * <p>This method is called by the <code>paint</code> method prior to
     * any drawing operations to configure the drawing surface. The default
     * implementation sets the rendering hints that have been specified for
     * this <code>AbstractPainter</code>.</p>
     *
     * <p>This method can be overriden by subclasses to modify the drawing
     * surface before any painting happens.</p>
     *
     * @param g the graphics surface to configure. This will never be null.
     * @see #paint(Graphics2D, T, int, int)
     */
    protected void configureGraphics(Graphics2D g) {
        //configure antialiasing
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
     * @param object
     */
    protected abstract void doPaint(Graphics2D g, T object, int width, int height);

    /**
     * @inheritDoc
     */
    public final void paint(Graphics2D g, T obj, int width, int height) {
        if (g == null) {
            throw new NullPointerException("The Graphics2D must be supplied");
        }

        if(!isVisible() || width < 1 || height < 1) {
            return;
        }

        configureGraphics(g);

        //paint to a temporary image if I'm caching, or if there are filters to apply
        if (useCache() || filters.length > 0) {
            validateCache(obj);
            BufferedImage cache = cachedImage == null ? null : cachedImage.get();
            if (cache == null || cache.getWidth() != width || cache.getHeight() != height) {
                //rebuild the cache. I do this both if a cache is needed, and if any
                //filters exist. I only *save* the resulting image if caching is turned on
                cache = GraphicsUtilities.createCompatibleTranslucentImage(width, height);
                Graphics2D gfx = cache.createGraphics();
                configureGraphics(gfx);
                doPaint(gfx, obj, width, height);
                gfx.dispose();

                for (BufferedImageOp f : getFilters()) {
                    cache = f.filter(cache, null);
                }

                //only save the temporary image as the cache if I'm caching
                if (useCache()) {
                    cachedImage = new SoftReference<BufferedImage>(cache);
                }
            }

            g.drawImage(cache, 0, 0, null);
        } else {
            //can't use the cache, so just paint
            doPaint(g, obj, width, height);
        }
    }
}
