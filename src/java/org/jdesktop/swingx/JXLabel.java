/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JLabel;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.Painter;

/**
 * <p>A {@link javax.swing.JLabel} subclass which supports
 * {@link org.jdesktop.swingx.painter.Painter}s, multi-line text, and text
 * rotation.</p>
 *
 * <p>Painter support consists of the <code>foregroundPainter</code> and
 * <code>backgroundpainter</code> properties. The <code>backgroundPainter</code>
 * refers to a painter responsible for painting <i>beneath</i> the text and icon.
 * This painter, if set, will paint regardless of the <code>opaque</code> property.
 * If the background painter does not fully paint each pixel, then you should
 * make sure the <code>opaque</code> property is set to false.</p>
 *
 * <p>The <code>foregroundPainter</code> is responsible for painting the icon
 * and the text label. If no foregroundPainter is specified, then the look and feel
 * will paint the label. Note that if opaque is set to true and the look and feel is
 * rendering the foreground, then the foreground <i>may</i> paint over the background.
 * Most look and feels will paint a background when <code>opaque</code> is true.
 * To avoid this behavior, set <code>opaque</code> to false.</p>
 * 
 * <p>Since JXLabel is not opaque by default (<code>isOpaque()</code> returns false),
 * neither of these problems typically present themselves.</p>
 *
 * <p>Multi-line text is enabled via the <code>lineWrap</code> property. Simply
 * set it to true. By default, line wrapping occurs on word boundaries.</p>
 *
 * <p>The text (actually, the entire foreground and background) of the JXLabel may
 * be rotated. Set the <code>rotation</code> property to specify what the rotation
 * should be.</p> TODO not yet determined what API this will use.
 *
 * @author joshua.marinacci@sun.com
 * @author rbair
 * @author rah
 * @author mario_cesar
 */
public class JXLabel extends JLabel {
    private Painter foregroundPainter;
    private Painter backgroundPainter;
    
    /**
     * Create a new JXLabel. This has the same semantics as creating a new JLabel.
     */
    public JXLabel() {
        super();
        initPainterSupport();
    }
    
    public JXLabel(Icon image) {
        super(image);
        initPainterSupport();
    }
    
    public JXLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
        initPainterSupport();
    }
    
    /**
     * Create a new JXLabel with the given text as the text for the label. This is
     * shorthand for:
     * <pre><code>
     * JXLabel label = new JXLabel();
     * label.setText("Some Text");
     * </code></pre>
     *
     * @param text the text to set.
     */
    public JXLabel(String text) {
        super(text);
        initPainterSupport();
    }
    
    public JXLabel(String text, Icon image, int horizontalAlignment) {
        super(text, image, horizontalAlignment);
        initPainterSupport();
    }
    
    public JXLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        initPainterSupport();
    }
    
    private void initPainterSupport() {
        foregroundPainter = new AbstractPainter() {
            protected void doPaint(Graphics2D g, Object object, int width, int height) {
                Insets i = getInsets();
                g.translate(-i.left, -i.top);
                JXLabel.super.paintComponent(g);
            }
        };
    }
    
    /**
     * Returns the current foregroundPainter. This is a bound property.  By default the foregroundPainter
     * will be an internal painter which executes the standard painting code (paintComponent()).
     * @return the current foreground painter.
     */
    public final Painter getForegroundPainter() {
        return foregroundPainter;
    }
    
    /**
     * Sets a new foregroundPainter on the label. This will replace the existing foreground painter.
     * Existing painters can be wrapped by using a CompoundPainter.
     * @param painter
     */
    public void setForegroundPainter(Painter painter) {
        Painter old = this.getForegroundPainter();
        this.foregroundPainter = painter;
        firePropertyChange("foregroundPainter", old, getForegroundPainter());
        repaint();
    }
    
    
    
    /**
     * Sets a Painter to use to paint the background of this component
     * By default there is already a single painter installed which draws the normal background for
     * this component according to the current Look and Feel. Calling
     * <CODE>setBackgroundPainter</CODE> will replace that existing painter.
     * @param p the new painter
     * @see #getBackgroundPainter()
     */
    public void setBackgroundPainter(Painter p) {
        Painter old = getBackgroundPainter();
        backgroundPainter = p;
        firePropertyChange("backgroundPainter", old, getBackgroundPainter());
        repaint();
    }
    
    /**
     * Returns the current background painter. The default value of this property
     * is a painter which draws the normal JPanel background according to the current look and feel.
     * @return the current painter
     * @see #setBackgroundPainter(Painter)
     */
    public final Painter getBackgroundPainter() {
        return backgroundPainter;
    }
    
    /**
     * @param g graphics to paint on
     */
    @Override
    protected void paintComponent(Graphics g) {
        if (backgroundPainter == null && foregroundPainter == null) {
            super.paintComponent(g);
        } else {
            Graphics2D g2 = (Graphics2D)g.create();
            Insets i = getInsets();
            g2.translate(i.left, i.top);
            int width = getWidth() - i.left - i.right;
            int height = getHeight() - i.top - i.bottom;
            if(backgroundPainter != null) {
                backgroundPainter.paint(g2, this, width, height);
            }
            if(foregroundPainter != null) {
                foregroundPainter.paint(g2, this, width, height);
            }
            g2.dispose();
        }
    }
}
