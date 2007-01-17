/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
 *
 */
package org.jdesktop.swingx.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JLabel;

import org.jdesktop.swingx.painter.Painter;

/**
 * A <code>JLabel</code> optimized for usage in renderers and
 * with a minimal background painter support. <p>
 * 
 * <i>Note</i>: the painter support will be switched to painter_work as 
 * soon it enters main. 
 * 
 * The reasoning for the performance-overrides is copied from core: <p>
 * 
 * The standard <code>JLabel</code> component was not
 * designed to be used this way and we want to avoid 
 * triggering a <code>revalidate</code> each time the
 * cell is drawn. This would greatly decrease performance because the
 * <code>revalidate</code> message would be
 * passed up the hierarchy of the container to determine whether any other
 * components would be affected.  
 * As the renderer is only parented for the lifetime of a painting operation
 * we similarly want to avoid the overhead associated with walking the
 * hierarchy for painting operations.
 * So this class
 * overrides the <code>validate</code>, <code>invalidate</code>,
 * <code>revalidate</code>, <code>repaint</code>, and
 * <code>firePropertyChange</code> methods to be 
 * no-ops and override the <code>isOpaque</code> method solely to improve
 * performance.  If you write your own renderer component,
 * please keep this performance consideration in mind.
 * <p>
 * 
 * @author Jeanette Winzenburg
 */
public class JRendererLabel extends JLabel implements PainterAware {

    protected Painter painter;

    /**
     * 
     */
    public JRendererLabel() {
        super();
      setOpaque(true);
    }

    /**
     * Overridden for performance reasons.<p>
     * PENDING: Think about Painters and opaqueness?
     * 
     */
    public boolean isOpaque() { 
        Color back = getBackground();
        Component p = getParent(); 
        if (p != null) { 
            p = p.getParent(); 
        }
        // p should now be the JTable. 
        boolean colorMatch = (back != null) && (p != null) && 
            back.equals(p.getBackground()) && 
                        p.isOpaque();
        return !colorMatch && super.isOpaque(); 
    }

    public void setPainter(Painter painter) {
        this.painter = painter;
//        if (painter != null) {
//            setOpaque(false);
//        } else {
//            setOpaque(true);
//        }
    }

    
    @Override
    public void paint(Graphics g) {
        paintPainter((Graphics2D) g);
        super.paint(g);
    }

    /**
     * @param graphics2D
     */
    protected void paintPainter(Graphics2D g) {
        if (painter != null)
            painter.paint(g, this);
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     *
     * @since 1.5
     */
    public void invalidate() {}

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    public void validate() {}

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    public void revalidate() {}

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    public void repaint(long tm, int x, int y, int width, int height) {}

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    public void repaint(Rectangle r) { }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     *
     * @since 1.5
     */
    public void repaint() {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {  
        // Strings get interned...
        if (propertyName=="text") {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) { }


}