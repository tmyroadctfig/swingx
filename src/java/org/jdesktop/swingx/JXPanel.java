/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JPanel;

/**
 * A simple JPanel extension that adds translucency support.
 * This component and all of its content will be displayed with the specified
 * &quot;alpha&quot; transluscency property value.
 *
 * @author rbair
 */
public class JXPanel extends JPanel {
    /**
     * The alpha level for this component.
     */
    private float alpha = 1.0f;
    private transient Insets insets = new Insets(0,0,0,0); //scratch
    
    /** 
     * Creates a new instance of JXPanel
     */
    public JXPanel() {
    }
    
    /**
     * Set the alpha transparency level for this component. This automatically
     * causes a repaint of the component.
     * 
     * <p>TODO add support for animated changes in translucency</p>
     *
     * @param alpha must be a value between 0 and 1 inclusive.
     */
    public void setAlpha(float alpha) {
        if (this.alpha != alpha) {
            assert alpha >= 0 && alpha <= 1.0;
            float oldAlpha = alpha;
            this.alpha = alpha;
            firePropertyChange("alpha", oldAlpha, alpha);
            paintImmediately(0, 0, getWidth(), getHeight());
        }
    }
    
    /**
     * @return the alpha translucency level for this component. This will be
     * a value between 0 and 1, inclusive.
     */
    public float getAlpha() {
        return alpha;
    }

    public void paint(Graphics g) {
        insets = getInsets(insets);
        Graphics2D g2d = (Graphics2D)g;
        Composite oldComp = g2d.getComposite();
        Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(alphaComp);
        g2d.setColor(getBackground());
        g2d.fillRect(insets.left, insets.top,
                     getWidth() - insets.left - insets.right,
                     getHeight() - insets.top - insets.bottom);
        super.paint(g2d);
        g2d.setComposite(oldComp);
    }
}
