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
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import javax.swing.JComponent;
import org.jdesktop.swingx.JavaBean;

/**
 * <p>A convenient base class from which concrete Painter implementations may
 * extend. It extends JavaBean and thus provides property change notification
 * (which is crucial for the Painter implementations to be available in a
 * GUI builder). It also saves off the Graphics2D state in its "saveState" method,
 * and restores that state in the "restoreState" method. Simply include these method
 * calls in the paint method of subclasses.</p>
 * 
 * <p>For example, here is the paint method of BackgroundPainter:
 * <pre><code>
 *  public void paint(Graphics2D g, JComponent component) {
 *      saveState(g);
 *      g.setColor(component.getBackground());
 *      g.fillRect(0, 0, component.getWidth(), component.getHeight());
 *      restoreState(g);
 *  }
 * </code></pre></p>
 * 
 * 
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
    
    private Shape clip;
    private Composite composite;
    
    /**
     * Creates a new instance of AbstractPainter
     */
    public AbstractPainter() {
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
        stateSaved = false;
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
        
    /**
     * @inheritDoc
     */
    public void paint(Graphics2D g, JComponent component) {
        saveState(g);
        if (getComposite() != null) {
            g.setComposite(getComposite());
        }
        if (getClip() != null) {
            g.setClip(getClip());
        }
        paintBackground(g, component);
        restoreState(g);
    }
}
