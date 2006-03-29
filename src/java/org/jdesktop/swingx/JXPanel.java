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

package org.jdesktop.swingx;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.Scrollable;
import org.jdesktop.swingx.painter.Painter;

/**
 * A simple JPanel extension that adds translucency support.
 * This component and all of its content will be displayed with the specified
 * &quot;alpha&quot; transluscency property value. It also supports the
 * Painter API.
 *
 * @author rbair
 */
public class JXPanel extends JPanel implements Scrollable {
    private boolean scrollableTracksViewportHeight;
    private boolean scrollableTracksViewportWidth;
    
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
    /**
     * Indicates whether this component should inherit its parent alpha value
     */
    private boolean inheritAlpha = true;
    /**
     * Indicates whether the JXPanel should draw a gradient or not
     * @deprecated Use setBackgroundPainter instead
     */
    private boolean drawGradient = false;
    /**
     * @deprecated Specify the Resize property on a GradientPainter instead
     */
    private boolean gradientTrackWidth = true;
    /**
     * @deprecated Specify the Resize property on a GradientPainter instead
     */
    private boolean gradientTrackHeight = true;
    /**
     * If the JXPanel is to draw a gradient, this paint indicates how it should
     * be painted
     * 
     * @deprecated 
     */
    private GradientPaint gradientPaint;
    /**
     * Specifies the Painter to use for painting the background of this panel.
     * If no painter is specified, the normal painting routine for JPanel
     * is called. Old behavior is also honored for the time being if no
     * backgroundPainter is specified
     */
    private Painter backgroundPainter;
    /**
     * Keeps track of the old dimensions so that if the dimensions change, the
     * saved gradient image can be thrown out and re-rendered. This size is
     * AFTER applying the insets!
     */
    private Dimension oldSize;
    /**
     * The cached gradient image
     */
    private BufferedImage cachedGradient;
    
    /** 
     * Creates a new instance of JXPanel
     */
    public JXPanel() {
    }
    
    /**
     * @param isDoubleBuffered
     */
    public JXPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    /**
     * @param layout
     */
    public JXPanel(LayoutManager layout) {
        super(layout);
    }

    /**
     * @param layout
     * @param isDoubleBuffered
     */
    public JXPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
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
     * Unlike other properties, alpha can be set on a component, or on one of
     * its parents. If the alpha of a parent component is .4, and the alpha on
     * this component is .5, effectively the alpha for this component is .4
     * because the lowest alpha in the heirarchy &quot;wins&quot;
     */ 
    public float getEffectiveAlpha() {
        if (inheritAlpha) {
            float a = alpha;
            Component c = this;
            while ((c = c.getParent()) != null) {
                if (c instanceof JXPanel) {
                    a = Math.min(((JXPanel)c).getAlpha(), a);
                }
            }
            return a;
        } else {
            return alpha;
        }
    }
    
    public boolean isInheritAlpha() {
        return inheritAlpha;
    }
    
    public void setInheritAlpha(boolean val) {
        if (inheritAlpha != val) {
            inheritAlpha = val;
            firePropertyChange("inheritAlpha", !inheritAlpha, inheritAlpha);
        }
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
     */
    public boolean getScrollableTracksViewportHeight() {
        return scrollableTracksViewportHeight;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
     */
    public boolean getScrollableTracksViewportWidth() {
        return scrollableTracksViewportWidth;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
     */
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int, int)
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }
    /**
     * @param scrollableTracksViewportHeight The scrollableTracksViewportHeight to set.
     */
    public void setScrollableTracksViewportHeight(boolean scrollableTracksViewportHeight) {
        this.scrollableTracksViewportHeight = scrollableTracksViewportHeight;
    }
    /**
     * @param scrollableTracksViewportWidth The scrollableTracksViewportWidth to set.
     */
    public void setScrollableTracksViewportWidth(boolean scrollableTracksViewportWidth) {
        this.scrollableTracksViewportWidth = scrollableTracksViewportWidth;
    }

    /**
     * @deprecated To specify a gradient for the panel, use the
     *             #setBackgroundPainter method, along with a Painter, like
     *             this:
     *  <pre><code>
     *      BasicGradientPainter gradient = 
     *          new BasicGradientPainter(new GradientPaint(
     *              new Point2D.Double(0,0),
     *              Color.WHITE, 
     *              new Point2D.Double(1,0), 
     *              UIManager.getColor("control")));
     *      panel.setBackgroundPainter(gradient);
     *  </code></pre>
     *
     *  There are several predefined gradients that may also be used. For example:
     *  <pre><code>
     *      BasicGradientPainter gradient = 
     *          new BasicGradientPainter(BasicGradientPainter.WHITE_TO_CONTROL_HORIZONTAL);
     *      panel.setBackgroundPainter(gradient);
     *  </code></pre>
     */
    public void setGradientPaint(GradientPaint paint) {
        GradientPaint oldPaint = this.gradientPaint;
        this.gradientPaint = paint;
        firePropertyChange("gradientPaint", oldPaint, paint);
        repaint();
    }

    /**
     * @deprected. See setGradientPaint
     */
    public GradientPaint getGradientPaint() {
        return gradientPaint;
    }

    /**
     * @deprected. See setGradientPaint
     */
    public void setDrawGradient(boolean b) {
        if (drawGradient != b) {
            boolean old = drawGradient;
            drawGradient = b;
            oldSize = getSize();
            firePropertyChange("drawGradient", old, b);
            repaint();
        }
    }
    
    /**
     * @deprected. See setGradientPaint
     */
    public boolean isDrawGradient() {
        return drawGradient;
    }
    
    /**
     * @deprected. See setGradientPaint
     */
    public void setGradientTrackWidth(boolean b) {
        if (gradientTrackWidth != b) {
            boolean old = gradientTrackWidth;
            gradientTrackWidth = b;
            firePropertyChange("gradientTrackWidth", old, b);
            repaint();
        }
    }
    
    /**
     * @deprected. See setGradientPaint
     */
    public boolean isGradientTrackWidth() {
        return gradientTrackWidth;
    }
    
    /**
     * @deprected. See setGradientPaint
     */
    public void setGradientTrackHeight(boolean b) {
        if (gradientTrackHeight != b) {
            boolean old = gradientTrackHeight;
            gradientTrackHeight = b;
            firePropertyChange("gradientTrackHeight", old, b);
            repaint();
        }
    }
    
    /**
     * @deprected. See setGradientPaint
     */
    public boolean isGradientTrackHeight() {
        return gradientTrackHeight;
    }
    
    /**
     * Specifies a Painter to use to paint the background of this JXPanel.
     * If <code>p</code> is not null, then setOpaque(false) will be called
     * as a side effect. A component should not be opaque if painters are
     * being used, because Painters may paint transparent pixels or not
     * paint certain pixels, such as around the border insets.
     */
    public void setBackgroundPainter(Painter p) {
        Painter old = getBackgroundPainter();
        this.backgroundPainter = p;
        
        if (p != null) {
            setOpaque(false);
        }
        
        firePropertyChange("backgroundPainter", old, getBackgroundPainter());
        repaint();
    }
    
    public Painter getBackgroundPainter() {
        return backgroundPainter;
    }
    
    /**
     * Overriden paint method to take into account the alpha setting
     */
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        Composite oldComp = g2d.getComposite();
        float alpha = getEffectiveAlpha();
        Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(alphaComp);
        super.paint(g2d);
        g2d.setComposite(oldComp);
    }

    /**
     * overridden to provide gradient painting
     * 
     * TODO: Chris says that in OGL we actually suffer here by caching the
     * gradient paint since the OGL pipeline will render gradients on
     * hardware for us. The decision to use cacheing is based on my experience
     * with gradient title borders slowing down repaints -- this could use more
     * extensive analysis.
     */
    protected void paintComponent(Graphics g) {
        if (backgroundPainter != null) {
            backgroundPainter.paint((Graphics2D)g, this);
        } else {
            super.paintComponent(g);
            if (drawGradient) {
                Insets insets = getInsets();
                int width = getWidth() - insets.right - insets.left;
                int height = getHeight() - insets.top - insets.bottom;

                //TODO need to detect a change in gradient paint as well
                if (gradientPaint == null || oldSize == null || oldSize.width != width || oldSize.height != height) {
                    Color c1 = null;//UIManager.getColor("control");
                    Color c2 = null;//c.darker();
                    if (gradientPaint == null) {
                        c1 = getBackground();
                        c2 = new Color(c1.getRed() - 40, c1.getGreen() - 40, c1.getBlue() - 40);
                        float x1 = 0f;
                        float y1 = 0f;
                        float x2 = width;
                        float y2 = 0;
                        boolean cyclic = false;
                        gradientPaint = new GradientPaint(x1, y1, c1, x2, y2, c2, cyclic);
                    } else {
                        //same GP as before, but where the values differed for x1, x2, replace
                        //x2 with the current width, and where values differed for y1, y2
                        //replace with current height
                        GradientPaint gp = gradientPaint;
                        float x2 = (float)gp.getPoint2().getX();
                        if (gradientTrackWidth) {
                            float ratio = (float)width / (float)oldSize.width;
                            x2 = ((float)gp.getPoint2().getX()) * ratio;
                        }
                        float y2 = (float)gp.getPoint2().getY();
                        if (gradientTrackHeight) {
                            float ratio = (float)height / (float)oldSize.height;
                            y2 = ((float)gp.getPoint2().getY()) * ratio;
                        }
                        gradientPaint = new GradientPaint((float)gp.getPoint1().getX(),
                                (float)gp.getPoint1().getY(), gp.getColor1(),
                                x2, y2, gp.getColor2(),
                                gp.isCyclic());
                    }

                    oldSize = new Dimension(width, height);
                    cachedGradient = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D imgg = (Graphics2D)cachedGradient.getGraphics();
                    imgg.setPaint(gradientPaint);
                    imgg.fillRect(0, 0, width, height);
                }

                // draw the image
                Graphics2D g2 = (Graphics2D)g;
                g2.drawImage(cachedGradient, null, insets.left, insets.top);
            }
        }
    }
}
