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
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import org.jdesktop.swingx.JavaBean;
import org.jdesktop.swingx.util.PaintUtils;

/**
 * <p>A convenient base class from which concrete Painter implementations may
 * extend. It extends JavaBean and thus provides property change notification
 * (which is crucial for the Painter implementations to be available in a
 * GUI builder). It also saves off the Graphics2D state in its "saveState" method,
 * and restores that state in the "restoreState" method. Sublasses simply need
 * to extend AbstractPainter and implement the paintBackground method.
 * 
 * <p>For example, here is the paintBackground method of BackgroundPainter:
 * <pre><code>
 *  public void paintBackground(Graphics2D g, JComponent component) {
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
 *  <li>Sets the Clip if there is one</li>
 *  <li>Sets the Composite if there is one</li>
 *  <li>Delegates to paintBackground</li>
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
 *   p.setAntialiasing(RenderingHints.VALUE_ANTIALIAS_ON);
 * </code></pre></p>
 *
 * <p>You can read more about antialiasing and other rendering hints in the
 * java.awt.RenderingHints documentation. <strong>By nature, changing the rendering
 * hints may have an impact on performance. Certain hints require more
 * computation, others require less</strong></p>
 * 
 * @author rbair
 */
public abstract class AbstractPainter extends JavaBean implements Painter {
    private boolean stateSaved = false;
    private Paint oldPaint;
    private Font oldFont;
    private Stroke oldStroke;
    private AffineTransform oldTransform;
    private Composite oldComposite;
    private Shape oldClip;
    private Color oldBackground;
    private Color oldColor;
    private RenderingHints oldRenderingHints;
    
    private Shape clip;
    private Composite composite;
    private boolean useCache;
    private RenderingHints renderingHints;
    private SoftReference<BufferedImage> cachedImage;
    private Effect effect;
    
    /**
     * Creates a new instance of AbstractPainter
     */
    public AbstractPainter() {
        renderingHints = new RenderingHints(new HashMap<RenderingHints.Key,Object>());
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
    public void setUseCache(boolean b) {
        boolean old = isUseCache();
        useCache = b;
        firePropertyChange("useCache", old, isUseCache());
        //if there was a cached image and I'm no longer using the cache, blow it away
        if (cachedImage != null && !isUseCache()) {
            cachedImage = null;
        }
    }
    
    /**
     * @returns whether or not the cache should be used
     */
    public boolean isUseCache() {
        return useCache;
    }
    
    /**
     * <p>Sets an effect (or multiple effects if you use a CompoundEffect) 
     * to apply to the results of the AbstractPainter's painting operation. 
     * Some common effects include blurs, shadows, embossing, and so forth. If 
     * the given effect is null, no effects will be used</p>
     *
     * @param effects the Effect to apply to the results of the AbstractPainter's
     *                painting operation
     */
    public void setEffect(Effect effect) {
        Effect old = getEffect();
        this.effect = effect;
        firePropertyChange("effect", old, getEffect());
    }
    
    /**
     * @param effects the Effect to apply to the results of the AbstractPainter's
     *                painting operation. May be null
     */
    public Effect getEffect() {
        return this.effect;
    }
    
    /**
     * Specifies the Shape to use for clipping the painting area. This
     * may be null
     *
     * @param clip the Shape to use to clip the area. Whatever is inside this
     *        shape will be kept, everything else "clipped". May be null. If
     *        null, the clipping is not set on the graphics object
     */
    public void setClip(Shape clip) {
        Shape old = getClip();
        this.clip = clip;
        firePropertyChange("clip", old, getClip());
    }
    
    /**
     * @returns the clipping shape
     */
    public Shape getClip() {
        return clip;
    }
    
    /**
     * Sets the Composite to use. For example, you may specify a specific
     * AlphaComposite so that when this Painter paints, any content in the
     * drawing area is handled properly
     *
     * @param c The composite to use. If null, then no composite will be
     *        specified on the graphics object
     */
    public void setComposite(Composite c) {
        Composite old = getComposite();
        this.composite = c;
        firePropertyChange("composite", old, getComposite());
    }
    
    /**
     * @returns the composite
     */
    public Composite getComposite() {
        return composite;
    }

    /**
     * @returns the technique used for interpolating alpha values. May be one
     * of:
     * <ul>
     *  <li>RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED</li>
     *  <li>RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY</li>
     *  <li>RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT</li>
     * </ul>
     */
    public Object getAlphaInterpolation() {
        return renderingHints.get(RenderingHints.KEY_ALPHA_INTERPOLATION);
    }

    /**
     * Sets the technique used for interpolating alpha values.
     *
     * @param alphaInterpolation
     * May be one of:
     * <ul>
     *  <li>RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED</li>
     *  <li>RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY</li>
     *  <li>RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT</li>
     * </ul>
     */
    public void setAlphaInterpolation(Object alphaInterpolation) {
        if (!RenderingHints.KEY_ALPHA_INTERPOLATION.isCompatibleValue(alphaInterpolation)) {
            throw new IllegalArgumentException(alphaInterpolation + " is not an acceptable value");
        }
        Object old = getAlphaInterpolation();
        renderingHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, alphaInterpolation);
        firePropertyChange("alphaInterpolation", old, getAlphaInterpolation());
    }

    /**
     * @returns whether or not to antialias
     *          May be one of:
     * <ul>
     *  <li>RenderingHints.VALUE_ANTIALIAS_DEFAULT</li>
     *  <li>RenderingHints.VALUE_ANTIALIAS_OFF</li>
     *  <li>RenderingHints.VALUE_ANTIALIAS_ON</li>
     * </ul>
     */
    public Object getAntialiasing() {
        return renderingHints.get(RenderingHints.KEY_ANTIALIASING);
    }

    /**
     * Sets whether or not to antialias
     * @param antialiasing
     *          May be one of:
     * <ul>
     *  <li>RenderingHints.VALUE_ANTIALIAS_DEFAULT</li>
     *  <li>RenderingHints.VALUE_ANTIALIAS_OFF</li>
     *  <li>RenderingHints.VALUE_ANTIALIAS_ON</li>
     * </ul>
     */
    public void setAntialiasing(Object antialiasing) {
        if (!RenderingHints.KEY_ANTIALIASING.isCompatibleValue(antialiasing)) {
            throw new IllegalArgumentException(antialiasing + " is not an acceptable value");
        }
        Object old = getAntialiasing();
        renderingHints.put(RenderingHints.KEY_ANTIALIASING, antialiasing);
        firePropertyChange("antialiasing", old, getAntialiasing());
    }

    /**
     * @returns the technique to use for rendering colors
     *          May be one of:
     * <ul>
     *  <li>RenderingHints.VALUE_COLOR_RENDER_DEFAULT</li>
     *  <li>RenderingHints.VALUE_RENDER_QUALITY</li>
     *  <li>RenderingHints.VALUE_RENDER_SPEED</li>
     * </ul>
     */
    public Object getColorRendering() {
        return renderingHints.get(RenderingHints.KEY_COLOR_RENDERING);
    }

    /**
     * Sets the technique to use for rendering colors
     * @param colorRendering
     *          May be one of:
     * <ul>
     *  <li>RenderingHints.VALUE_COLOR_RENDER_DEFAULT</li>
     *  <li>RenderingHints.VALUE_RENDER_QUALITY</li>
     *  <li>RenderingHints.VALUE_RENDER_SPEED</li>
     * </ul>
     */
    public void setColorRendering(Object colorRendering) {
        if (!RenderingHints.KEY_COLOR_RENDERING.isCompatibleValue(colorRendering)) {
            throw new IllegalArgumentException(colorRendering + " is not an acceptable value");
        }
        Object old = getColorRendering();
        renderingHints.put(RenderingHints.KEY_COLOR_RENDERING, colorRendering);
        firePropertyChange("colorRendering", old, getColorRendering());
    }

    /**
     * @returns whether or not to dither
     *          May be one of:
     * <ul>
     *  <li>RenderingHints.VALUE_DITHER_DEFAULT</li>
     *  <li>RenderingHints.VALUE_DITHER_ENABLE</li>
     *  <li>RenderingHints.VALUE_DITHER_DISABLE</li>
     * </ul>
     */
    public Object getDithering() {
        return renderingHints.get(RenderingHints.KEY_DITHERING);
    }

    /**
     * Sets whether or not to dither
     * @param dithering
     *          May be one of:
     * <ul>
     *  <li>RenderingHints.VALUE_DITHER_DEFAULT</li>
     *  <li>RenderingHints.VALUE_DITHER_ENABLE</li>
     *  <li>RenderingHints.VALUE_DITHER_DISABLE</li>
     * </ul>
     */
    public void setDithering(Object dithering) {
        if (!RenderingHints.KEY_DITHERING.isCompatibleValue(dithering)) {
            throw new IllegalArgumentException(dithering + " is not an acceptable value");
        }
        Object old = getDithering();
        renderingHints.put(RenderingHints.KEY_DITHERING, dithering);
        firePropertyChange("dithering", old, getDithering());
    }

    /**
     * @returns whether or not to use fractional metrics
     *          May be one of:
     * <ul>
     *  <li>RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT</li>
     *  <li>RenderingHints.VALUE_FRACTIONALMETRICS_OFF</li>
     *  <li>RenderingHints.VALUE_FRACTIONALMETRICS_ON</li>
     * </ul>
     */
    public Object getFractionalMetrics() {
        return renderingHints.get(RenderingHints.KEY_FRACTIONALMETRICS);
    }

    /**
     * Sets whether or not to use fractional metrics
     *
     * @param fractionalMetrics
     *          May be one of:
     * <ul>
     *  <li>RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT</li>
     *  <li>RenderingHints.VALUE_FRACTIONALMETRICS_OFF</li>
     *  <li>RenderingHints.VALUE_FRACTIONALMETRICS_ON</li>
     * </ul>
     */
    public void setFractionalMetrics(Object fractionalMetrics) {
        if (!RenderingHints.KEY_FRACTIONALMETRICS.isCompatibleValue(fractionalMetrics)) {
            throw new IllegalArgumentException(fractionalMetrics + " is not an acceptable value");
        }
        Object old = getFractionalMetrics();
        renderingHints.put(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics);
        firePropertyChange("fractionalMetrics", old, getFractionalMetrics());
    }

    /**
     * @returns the technique to use for interpolation (used esp. when scaling)
     *          May be one of:
     * <ul>
     *  <li>RenderingHints.VALUE_INTERPOLATION_BICUBIC</li>
     *  <li>RenderingHints.VALUE_INTERPOLATION_BILINEAR</li>
     *  <li>RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR</li>
     * </ul>
     */
    public Object getInterpolation() {
        return renderingHints.get(RenderingHints.KEY_INTERPOLATION);
    }

    /**
     * Sets the technique to use for interpolation (used esp. when scaling)
     * @param interpolation
     *          May be one of:
     * <ul>
     *  <li>RenderingHints.VALUE_INTERPOLATION_BICUBIC</li>
     *  <li>RenderingHints.VALUE_INTERPOLATION_BILINEAR</li>
     *  <li>RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR</li>
     * </ul>
     */
    public void setInterpolation(Object interpolation) {
        if (!RenderingHints.KEY_INTERPOLATION.isCompatibleValue(interpolation)) {
            throw new IllegalArgumentException(interpolation + " is not an acceptable value");
        }
        Object old = getInterpolation();
        renderingHints.put(RenderingHints.KEY_INTERPOLATION, interpolation);
        firePropertyChange("interpolation", old, getInterpolation());
    }

    /**
     * @returns a hint as to techniques to use with regards to rendering quality vs. speed
     *          May be one of:
     * <ul>
     *  <li>RenderingHints.VALUE_RENDER_QUALITY</li>
     *  <li>RenderingHints.VALUE_RENDER_SPEED</li>
     *  <li>RenderingHints.VALUE_RENDER_DEFAULT</li>
     * </ul>
     */
    public Object getRendering() {
        return renderingHints.get(RenderingHints.KEY_RENDERING);
    }

    /**
     * Specifies a hint as to techniques to use with regards to rendering quality vs. speed
     *
     * @param rendering
     *          May be one of:
     * <ul>
     *  <li>RenderingHints.VALUE_RENDER_QUALITY</li>
     *  <li>RenderingHints.VALUE_RENDER_SPEED</li>
     *  <li>RenderingHints.VALUE_RENDER_DEFAULT</li>
     * </ul>
     */
    public void setRendering(Object rendering) {
        if (!RenderingHints.KEY_RENDERING.isCompatibleValue(rendering)) {
            throw new IllegalArgumentException(rendering + " is not an acceptable value");
        }
        Object old = getRendering();
        renderingHints.put(RenderingHints.KEY_RENDERING, rendering);
        firePropertyChange("rendering", old, getRendering());
    }

    /**
     * @returns technique for rendering strokes
     *          May be one of:
     * <ul>
     *  <li>RenderingHints.VALUE_STROKE_DEFAULT</li>
     *  <li>RenderingHints.VALUE_STROKE_NORMALIZE</li>
     *  <li>RenderingHints.VALUE_STROKE_PURE</li>
     * </ul>
     */
    public Object getStrokeControl() {
        return renderingHints.get(RenderingHints.KEY_STROKE_CONTROL);
    }

    /**
     * Specifies a technique for rendering strokes
     *
     * @param strokeControl
     *          May be one of:
     * <ul>
     *  <li>RenderingHints.VALUE_STROKE_DEFAULT</li>
     *  <li>RenderingHints.VALUE_STROKE_NORMALIZE</li>
     *  <li>RenderingHints.VALUE_STROKE_PURE</li>
     * </ul>
     */
    public void setStrokeControl(Object strokeControl) {
        if (!RenderingHints.KEY_STROKE_CONTROL.isCompatibleValue(strokeControl)) {
            throw new IllegalArgumentException(strokeControl + " is not an acceptable value");
        }
        Object old = getStrokeControl();
        renderingHints.put(RenderingHints.KEY_STROKE_CONTROL, strokeControl);
        firePropertyChange("strokeControl", old, getStrokeControl());
    }

    /**
     * @returns technique for anti-aliasing text.
     *          (TODO this needs to be updated for Mustang. You may use the
     *           new Mustang values, and everything will work, but support in
     *           the GUI builder and documentation need to be added once we
     *           branch for Mustang)<br/>
     *          May be one of:
     * <ul>
     *  <li>RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT</li>
     *  <li>RenderingHints.VALUE_TEXT_ANTIALIAS_OFF</li>
     *  <li>RenderingHints.VALUE_TEXT_ANTIALIAS_ON</li>
     * </ul>
     */
    public Object getTextAntialiasing() {
        return renderingHints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
    }

    /**
     * Sets the technique for anti-aliasing text.
     *          (TODO this needs to be updated for Mustang. You may use the
     *           new Mustang values, and everything will work, but support in
     *           the GUI builder and documentation need to be added once we
     *           branch for Mustang)<br/>
     *
     * @param textAntialiasing
     *          May be one of:
     * <ul>
     *  <li>RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT</li>
     *  <li>RenderingHints.VALUE_TEXT_ANTIALIAS_OFF</li>
     *  <li>RenderingHints.VALUE_TEXT_ANTIALIAS_ON</li>
     * </ul>
     */
    public void setTextAntialiasing(Object textAntialiasing) {
        if (!RenderingHints.KEY_TEXT_ANTIALIASING.isCompatibleValue(textAntialiasing)) {
            throw new IllegalArgumentException(textAntialiasing + " is not an acceptable value");
        }
        Object old = getTextAntialiasing();
        renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, textAntialiasing);
        firePropertyChange("textAntialiasing", old, getTextAntialiasing());
    }

    /**
     * @return the rendering hint associated with the given key. May return null
     */
    public Object getRenderingHint(RenderingHints.Key key) {
        return renderingHints.get(key);
    }

    /**
     * Set the given hint for the given key. This will end up firing the appropriate
     * property change event if the key is recognized. For example, if the key is
     * RenderingHints.KEY_ANTIALIASING, then the setAntialiasing method will be
     * called firing an "antialiasing" property change event if necessary. If
     * the key is not recognized, no event will be fired but the key will be saved.
     * The key must not be null
     *
     * @param key cannot be null
     * @param hint must be a hint compatible with the given key
     */
    public void setRenderingHint(RenderingHints.Key key, Object hint) {
        if (key == null) {
            throw new NullPointerException("RenderingHints key cannot be null");
        }
        
        if (key == RenderingHints.KEY_ALPHA_INTERPOLATION) {
            setAlphaInterpolation(hint);
        } else if (key == RenderingHints.KEY_ANTIALIASING) {
            setAntialiasing(hint);
        } else if (key == RenderingHints.KEY_COLOR_RENDERING) {
            setColorRendering(hint);
        } else if (key == RenderingHints.KEY_DITHERING) {
            setDithering(hint);
        } else if (key == RenderingHints.KEY_FRACTIONALMETRICS) {
            setFractionalMetrics(hint);
        } else if (key == RenderingHints.KEY_INTERPOLATION) {
            setInterpolation(hint);
        } else if (key == RenderingHints.KEY_RENDERING) {
            setRendering(hint);
        } else if (key == RenderingHints.KEY_STROKE_CONTROL) {
            setStrokeControl(hint);
        } else if (key == RenderingHints.KEY_TEXT_ANTIALIASING) {
            setTextAntialiasing(hint);
        } else {
            renderingHints.put(key, hint);
        }
    }

    /**
     * @return a copy of the map of rendering hints held by this class. This
     *         returned value will never be null
     */
    public RenderingHints getRenderingHints() {
        return (RenderingHints)renderingHints.clone();
    }

    /**
     * Sets the rendering hints to use. This will <strong>replace</strong> the
     * rendering hints entirely, clearing any hints that were previously set.
     *
     * @param renderingHints map of hints. May be null. I null, a new Map of
     * rendering hints will be created
     */
    public void setRenderingHints(RenderingHints renderingHints) {
        RenderingHints old = this.renderingHints;
        if (renderingHints != null) {
            this.renderingHints = (RenderingHints)renderingHints.clone();
        } else {
            this.renderingHints = new RenderingHints(new HashMap<RenderingHints.Key, Object>());
        }
        firePropertyChange("renderingHints", old, getRenderingHints());
    }
    
    /**
     * Saves the state in the given Graphics2D object so that it may be
     * restored later.
     *
     * @param g the Graphics2D object who's state will be saved
     */
    protected void saveState(Graphics2D g) {
        oldPaint = g.getPaint();
        oldFont = g.getFont();
        oldStroke = g.getStroke();
        oldTransform = g.getTransform();
        oldComposite = g.getComposite();
        oldClip = g.getClip();
        oldBackground = g.getBackground();
        oldColor = g.getColor();
        
        //save off the old rendering hints
        oldRenderingHints = g.getRenderingHints();
        
        stateSaved = true;
    }
    
    /**
     * Restores previously saved state. A call to saveState must have occured
     * prior to calling restoreState, or an IllegalStateException will be thrown.
     * 
     * @param g the Graphics2D object to restore previously saved state to
     */
    protected void restoreState(Graphics2D g) {
        if (!stateSaved) {
            throw new IllegalStateException("A call to saveState must occur " +
                    "prior to calling restoreState");
        }
        
        g.setPaint(oldPaint);
        g.setFont(oldFont);
        g.setTransform(oldTransform);
        g.setStroke(oldStroke);
        g.setComposite(oldComposite);
        g.setClip(oldClip);
        g.setBackground(oldBackground);
        g.setColor(oldColor);
        
        //restore the rendering hints
        g.setRenderingHints(oldRenderingHints);
        
        stateSaved = false;
    }
        
    /**
     * @inheritDoc
     */
    public void paint(Graphics2D g, JComponent component) {
        saveState(g);
        
        configureGraphics(g);
        
        //if I am cacheing, and the cache is not null, and the image has the
        //same dimensions as the component, then simply paint the image
        BufferedImage image = cachedImage == null ? null : cachedImage.get();
        if (isUseCache() && image != null 
                && image.getWidth() == component.getWidth()
                && image.getHeight() == component.getHeight()) {
            g.drawImage(image, 0, 0, null);
        } else {
            Effect effect = getEffect();
            if (effect != null || isUseCache()) {
                image = PaintUtils.createCompatibleImage(
                        component.getWidth(),
                        component.getHeight(),
                        Transparency.TRANSLUCENT);
                
                Graphics2D gfx = image.createGraphics();
                configureGraphics(gfx);
                paintBackground(gfx, component);
                gfx.dispose();

                if (effect != null) {
                    image = effect.apply(image);
                }
                
                g.drawImage(image, 0, 0, null);
                
                if (isUseCache()) {
                    cachedImage = new SoftReference<BufferedImage>(image);
                }
            } else {
                paintBackground(g, component);
            }
        }
        
        restoreState(g);
    }
    
    /**
     * Utility method for configuring the given Graphics2D with the rendering hints,
     * composite, and clip
     */
    private void configureGraphics(Graphics2D g) {
        g.setRenderingHints(getRenderingHints());

        if (getComposite() != null) {
            g.setComposite(getComposite());
        }
        if (getClip() != null) {
            g.setClip(getClip());
        }
    }

    /**
     * Subclasses should implement this method and perform custom painting operations
     * here. Common behavior, such as setting the clip and composite, saving and restoring
     * state, is performed in the "paint" method automatically, and then delegated here.
     *
     * @param g The Graphics2D object in which to paint
     * @param component The JComponent that the Painter is delegate for.
     */
    protected abstract void paintBackground(Graphics2D g, JComponent component);
}
