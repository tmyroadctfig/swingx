/*
 * AlphaPainter.java
 *
 * Created on March 5, 2007, 9:49 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import org.jdesktop.swingx.JXPanel;

/**
 *
 * @author joshy
 */
public class AlphaPainter<T> extends CompoundPainter<T> {
    private float alpha = 1.0f;
    
    
    public void doPaint(Graphics2D g, T component, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        if(getTransform() != null) {
            g2.setTransform(getTransform());
        }
        if(alpha < 1) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha));
        }
        for (Painter p : getPainters()) {
            Graphics2D oldGraphics = g2;
            Graphics2D g3 = (Graphics2D) g2.create();
            p.paint(g3, component, width, height);
            if(isClipPreserved()) {
                oldGraphics.setClip(g3.getClip());
            }
            g3.dispose();
        }
        g2.dispose();
    }
    
    public static void main(String ... args) {
        JXPanel panel = new JXPanel();
        AlphaPainter alpha = new AlphaPainter();
        alpha.setAlpha(1f);
        alpha.setPainters(new PinstripePainter(new Color(255,255,255,125),45,20,20));
        
        panel.setBackgroundPainter(new CompoundPainter(
                new MattePainter(Color.RED),
                alpha
                ));
        
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.pack();
        frame.setSize(200,200);
        frame.setVisible(true);
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}
