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
import javax.swing.RepaintManager;

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
    /**
     * If the old alpha value was 1.0, I keep track of the opaque setting because
     * a translucent component is not opaque, but I want to be able to restore
     * opacity to its default setting if the alpha is 1.0. Honestly, I don't know
     * if this is necessary or not, but it sounded good on paper :)
     * <p>TODO: Check whether this variable is necessary or not</p>
     */
    private boolean oldOpaque;
    
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
            float oldAlpha = this.alpha;
            this.alpha = alpha;
            if (alpha > 0f && alpha < 1f) {
                if (oldAlpha == 1) {
                    //it used to be 1, but now is not. Save the oldOpaque
                    oldOpaque = isOpaque();
                    setOpaque(false);
                }
                if (!(RepaintManager.currentManager(this) instanceof TranslucentRepaintManager)) {
                    RepaintManager.setCurrentManager(new RepaintManagerX());
                }
            } else if (alpha == 1) {
                //restore the oldOpaque if it was true (since opaque is false now)
                if (oldOpaque) {
                   setOpaque(true);
                }
            }
            System.out.println(isOpaque());
            firePropertyChange("alpha", oldAlpha, alpha);
            repaint();
        }
    }
    
    /**
     * @return the alpha translucency level for this component. This will be
     * a value between 0 and 1, inclusive.
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * Overriden paint method to take into account the alpha setting
     */
    public void paint(Graphics g) {
        insets = getInsets(insets);
        Graphics2D g2d = (Graphics2D)g;
        Composite oldComp = g2d.getComposite();
        Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(alphaComp);
        super.paint(g2d);
        g2d.setComposite(oldComp);
    }
}
