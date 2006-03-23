/*
 * GlossPainter.java
 *
 * Created on 23 mars 2006, 13:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import javax.swing.JComponent;

/**
 *
 * @author Romain Guy <romain.guy@mac.com>
 */
public class GlossPainter extends AbstractPainter {
    public enum GlossPosition {
        TOP, BOTTOM
    }
    
    private Color color;
    private GlossPosition position;
    
    public GlossPainter() {
        this(Color.WHITE, GlossPosition.TOP);
    }
    
    public GlossPainter(Color color) {
        this(color, GlossPosition.TOP);
    }
    
    public GlossPainter(GlossPosition position) {
        this(Color.WHITE, position);
    }
    
    public GlossPainter(Color color, GlossPosition position) {
        this.setColor(color);
        this.setPosition(position);
        
        setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
    }

    protected void paintBackground(Graphics2D g, JComponent component) {
        Ellipse2D ellipse = new Ellipse2D.Double(-component.getWidth(),
            component.getHeight() / 2.7, component.getWidth() * 3.0,
            component.getHeight() * 2.0);
        
        Shape gloss = ellipse;
        if (getPosition() == GlossPosition.TOP) {
            Area area = new Area(component.getBounds());
            area.subtract(new Area(ellipse));
            gloss = area;
        }
        
        g.setColor(getColor());
        g.fill(gloss);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        Color old = this.color;
        this.color = color;
        firePropertyChange("color", old, getColor());
    }

    public GlossPosition getPosition() {
        return position;
    }

    public void setPosition(GlossPosition position) {
        GlossPosition old = this.position;
        this.position = position;
        firePropertyChange("position", old, getPosition());
    }
}
