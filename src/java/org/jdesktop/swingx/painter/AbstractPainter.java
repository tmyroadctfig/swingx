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
    
    /**
     * Creates a new instance of AbstractPainter
     */
    public AbstractPainter() {
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
}
