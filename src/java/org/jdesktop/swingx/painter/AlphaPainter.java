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

import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Applies an alpha value to an entire stack of painters
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
            Graphics2D g3 = (Graphics2D) g2.create();
            p.paint(g3, component, width, height);
            if(isClipPreserved()) {
                g2.setClip(g3.getClip());
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
