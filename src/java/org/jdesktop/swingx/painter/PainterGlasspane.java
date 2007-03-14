/*
 * PainterGlasspane.java
 *
 * Created on November 21, 2006, 7:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * This is a glasspane which will draw the specified painter on
 * top of the specified target components. The PainterGlasspane would
 * commonly be used for drawing a translucent overlay or icon badge on top
 * of components which are invalid, indicating to the user what the problem
 * is. 
 * 
 * The PainterGlasspane can also be used to apply a Painter on top of a component
 * which does not already support painters on it's own.
 * 
 * @author joshy
 */
public class PainterGlasspane extends JComponent {
    private Painter painter;
    private List<JComponent>targets;
    
    /** Creates a new instance of ValidationOverlay */
    public PainterGlasspane() {
        targets = new ArrayList<JComponent>();
    }
    
    public void addTarget(JComponent comp) {
        targets.add(comp);
        repaint();
    }
    public void removeTarget(JComponent comp) {
        targets.remove(comp);
        repaint();
    }
    
    protected void paintComponent(Graphics gfx) {
        Graphics2D g = (Graphics2D)gfx;
        if(getPainter() != null) {
            for(JComponent target : targets) {
                Point offset = calcOffset(target);
                g.translate(offset.x,offset.y);
                getPainter().paint(g, target, target.getWidth(), target.getHeight());
                g.translate(-offset.x,-offset.y);
            }
        }
    }

    private Point calcOffset(JComponent target) {
        if(target == null) {
            return new Point(0,0);
        }
        // if the parent is the top then we must be the rootpane?
        if(target.getParent() == SwingUtilities.getWindowAncestor(target)) {
            return new Point(0,0);
        }
        
        Point parent = calcOffset((JComponent)target.getParent());
        Point self = target.getLocation();
        Point loc = new Point(parent.x + self.x, parent.y + self.y);
        //u.p("loc = " + loc);
        return loc;
    }

    public Painter getPainter() {
        return painter;
    }

    public void setPainter(Painter painter) {        
        this.painter = painter;
        repaint();
    }
}
