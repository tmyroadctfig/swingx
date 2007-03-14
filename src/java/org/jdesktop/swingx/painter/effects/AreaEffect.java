/*
 * AreaEffect.java
 *
 * Created on November 1, 2006, 10:10 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter.effects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

/**
 * An effect which works on AbstractPathPainters or any thing else which can provide a shape to be drawn.
 * @author joshy
 */
public interface AreaEffect {
    /*
     * Applies the shape effect. This effect will be drawn on top of the graphics context.
     */
    /**
     * Draws an effect on the specifed graphics and path using the specified width and height.
     * @param g 
     * @param clipShape 
     * @param width 
     * @param height 
     */
    public abstract void apply(Graphics2D g, Shape clipShape, int width, int height);
}
