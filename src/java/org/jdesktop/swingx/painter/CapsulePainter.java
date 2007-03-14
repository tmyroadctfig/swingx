/*
 * CapsulePainter.java
 *
 * Created on February 14, 2007, 9:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author joshy
 */
public class CapsulePainter extends AbstractAreaPainter {
    public enum Portion { Top, Full, Bottom }
    private Portion portion;
    public CapsulePainter(Portion portion) {
        this.portion = portion;
    }
    
    protected void doPaint(Graphics2D g, Object component, int width, int height) {
        
        Shape rect = provideShape(g,component,width,height);
        if(getStyle() == Style.BOTH || getStyle() == Style.FILLED) {
            g.setPaint(getFillPaint());
            g.fill(rect);
        }
        if(getStyle() == Style.BOTH || getStyle() == Style.OUTLINE) {
            g.setPaint(getBorderPaint());
            g.draw(rect);
        }
    }
    
    protected Shape provideShape(Graphics2D g, Object comp, int width, int height) {
        int round = 10;
        int rheight = height;
        int ry = 0;
        if(portion == Portion.Top) {
            round = height*2;
            rheight = height*2;
        }
        if(portion == Portion.Bottom) {
            round = height*2;
            rheight = height*2;
            ry = -height;
        }
        
        RoundRectangle2D rect = new RoundRectangle2D.Double(0, ry, width, rheight, round, round);
        return rect;
    }

    protected void paintBackground(Graphics2D g, Object component) {
    }
    
}
