/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
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
import javax.swing.UIManager;



/**
 * A simple JPanel extension that adds translucency support.
 * This component and all of its content will be displayed with the specified
 * &quot;alpha&quot; transluscency property value.
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
     */
    private boolean drawGradient = false;
    /**
     * <p>If true, then the gradient will track the width of the panel. For example,
     * if I had the following gradient paint:
     * <code>
     *  int width = getWidth();
     *  GradientPaint gp = new GradientPaint(0, 0, Color.BLUE, width, 0, Color.WHITE);
     * </code>
     * Then at the left edge of the component BLUE will be painted, and at the
     * right edge WHITE will be painted with a nice gradient between. However,
     * when the width of the JXPanel changes, the &quot;width&quot; of the
     * GradientPaint does not since GradientPaint is immutable.</p>
     *
     * <p>To solve this problem, the JXPanel employs a unique algorithm. Consider
     * the following gradients on a panel with a width of 5 and a height of 5:
     * <code>
     * GradientPaint a = new GradientPaint(0, 0, Color.BLUE, 5, 0, Color.WHITE);
     * GradientPaint b = new GradientPaint(0, 0, Color.BLUE, 10, 0, Color.WHITE);
     * </code></p>
     *
     * <p>GradientPaint &quot;a&quot; would paint a BLUE vertical line at x position 0, and
     * a WHITE vertical line at x position 5, with a gradient of colors between.
     *
     * <p>GradientPaint &quot;b&quot; would paint a BLUE vertical line at x position 0, and
     * a WHITE vertical line at x position 10 which is outside the clipping
     * bounds, and thus not actually painted! The color at x position 5 would be
     * halfway between BLUE and WHITE.</p>
     *
     * <p>If the JXPanel was then resized to be 10 pixels wide and 10 pixels tall,
     * we would expect to see the gradient paints be updated like to be these:
     * <code>
     * GradientPaint a = new GradientPaint(0, 0, Color.BLUE, 10, 0, Color.WHITE);
     * GradientPaint b = new GradientPaint(0, 0, Color.BLUE, 20, 0, Color.WHITE);
     * </code></p>
     *
     * <p>This is exactly what happens. Whatever GradientPaint is set by the
     * <code>setGradientPaint</code> method is assumed to be in terms of the
     * current width and height of the component. As the component&apos;s size
     * changes, the GradientPaint is updated proportionately to the change so that
     * the color drawn at position 0 and at position N where N is the width/height
     * will be consistent regardless of the changes in dimension of the component.</p>
     */
    private boolean gradientTrackWidth = true;
    /**
     * same as gradientTrackWidth, but in the vertical direction
     */
    private boolean gradientTrackHeight = true;
    /**
     * If the JXPanel is to draw a gradient, this paint indicates how it should
     * be painted
     */
    private GradientPaint gradientPaint;
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

    public void setGradientPaint(GradientPaint paint) {
        GradientPaint oldPaint = this.gradientPaint;
        this.gradientPaint = paint;
        firePropertyChange("gradientPaint", oldPaint, paint);
        repaint();
    }
    
    public GradientPaint getGradientPaint() {
        return gradientPaint;
    }
    
    public void setDrawGradient(boolean b) {
        if (drawGradient != b) {
            boolean old = drawGradient;
            drawGradient = b;
            firePropertyChange("drawGradient", old, b);
            repaint();
        }
    }
    
    public boolean isDrawGradient() {
        return drawGradient;
    }
    
    public void setGradientTrackWidth(boolean b) {
        if (gradientTrackWidth != b) {
            boolean old = gradientTrackWidth;
            gradientTrackWidth = b;
            firePropertyChange("gradientTrackWidth", old, b);
            repaint();
        }
    }
    
    public boolean isGradientTrackWidth() {
        return gradientTrackWidth;
    }
    
    public void setGradientTrackHeight(boolean b) {
        if (gradientTrackHeight != b) {
            boolean old = gradientTrackHeight;
            gradientTrackHeight = b;
            firePropertyChange("gradientTrackHeight", old, b);
            repaint();
        }
    }
    
    public boolean isGradientTrackHeight() {
        return gradientTrackHeight;
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
