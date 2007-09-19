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

import javax.swing.*;

import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.Painter;

/**
 * <p>A {@link org.jdesktop.swingx.painter.Painter} enabled subclass of {@link javax.swing.JButton}.
 * This class supports setting the foreground and background painters of the button separately. By default,
 * <code>JXButton</code> creates and installs two <code>Painter</code>s; one for the foreground, and one
 * for the background. These default <code>Painter</code>s delegate to the installed UI delegate.</p>
 *
 * <p>For example, if you wanted to blur <em>just the text</em> on the button, and let everything else be
 * handled by the UI delegate for your look and feel, then you could:
 * <pre><code>
 *  JXButton b = new JXButton("Execute");
 *  AbstractPainter fgPainter = (AbstractPainter)b.getForegroundPainter();
 *  StackBlurFilter filter = new StackBlurFilter();
 *  fgPainter.setFilters(filter);
 * </code></pre>
 *
 * <p>If <em>either</em> the foreground painter or the background painter is set,
 * then super.paintComponent() is not called. By setting both the foreground and background
 * painters to null, you get <em>exactly</em> the same painting behavior as JButton.
 * By contrast, the <code>Painters</code> installed by default will delegate to the UI delegate,
 * thus achieving the same look as a typical JButton, but at the cost of some additional painting
 * overhead.</p>
 *
 * <div class="examples">
 * <h3>Examples</h3>
 * {@demo org.jdesktop.swingx.JXButtonDemo ../../../../../demo}
 * </div>
 *
 * @author rbair
 * @author rah003
 */
public class JXButton extends JButton {
    //properties used to split foreground and background painting.
    //overwritten to suppress event notification while painting
    private String text;
    private boolean borderPainted = true;
    private boolean contentAreaFilled = true;
    
    //the default fg and bg painters
    private static final Painter<JXButton> DEFAULT_BACKGROUND_PAINTER = new DefaultBackgroundPainter();
    private static final Painter<JXButton> DEFAULT_FOREGROUND_PAINTER = new DefaultForegroundPainter();

    private Painter<JXButton> fgPainter = DEFAULT_FOREGROUND_PAINTER;
    private Painter<JXButton> bgPainter = DEFAULT_BACKGROUND_PAINTER;

    /** Creates a new instance of JXButton */
    public JXButton() {}
    public JXButton(String text) { super(text); }
    public JXButton(Action a) { super(a); }
    public JXButton(Icon icon) { super(icon); }
    public JXButton(String text, Icon icon) { super(text, icon); }
    
    @Override
    public void setText(String text) {
        this.text = text;
        super.setText(text);
    }
    
    @Override
    public String getText() {
        return this.text;
    }
    
    @Override
    public void setBorderPainted(boolean b) {
        this.borderPainted = b;
        super.setBorderPainted(b);
    }
    
    @Override
    public boolean isBorderPainted() {
        return this.borderPainted;
    }
    
    @Override
    public void setContentAreaFilled(boolean b) {
        this.contentAreaFilled = b;
        super.setContentAreaFilled(b);
    }
    
    @Override
    public boolean isContentAreaFilled() {
        return this.contentAreaFilled;
    }
    
    public Painter<JXButton> getBackgroundPainter() {
        return bgPainter;
    }

    public void setBackgroundPainter(Painter<JXButton> p) {
        Painter old = getBackgroundPainter();
        this.bgPainter = p;
        firePropertyChange("backgroundPainter", old, getBackgroundPainter());
        repaint();
    }
    public Painter<JXButton> getForegroundPainter() {
        return fgPainter;
    }

    public void setForegroundPainter(Painter<JXButton> p) {
        Painter old = getForegroundPainter();
        this.fgPainter = p;
        firePropertyChange("foregroundPainter", old, getForegroundPainter());
        repaint();
    }
    
    private boolean paintBorderInsets = true;
    private boolean painting;
    
    /**
     * Returns true if the background painter should paint where the border is
     * or false if it should only paint inside the border. This property is 
     * true by default. This property affects the width, height,
     * and intial transform passed to the background painter.
     */
    public boolean isPaintBorderInsets() {
        return paintBorderInsets;
    }
    
    /**
     * Sets the paintBorderInsets property.
     * Set to true if the background painter should paint where the border is
     * or false if it should only paint inside the border. This property is true by default.
     * This property affects the width, height,
     * and intial transform passed to the background painter.
     * 
     * This is a bound property.
     */
    public void setPaintBorderInsets(boolean paintBorderInsets) {
        boolean old = this.isPaintBorderInsets();
        this.paintBorderInsets = paintBorderInsets;
        firePropertyChange("paintBorderInsets", old, isPaintBorderInsets());
    }
    
    protected void paintComponent(Graphics g) {
        Painter<JXButton> bgPainter = getBackgroundPainter();
        Painter<JXButton> fgPainter = getForegroundPainter();
        if (painting || (bgPainter == null && fgPainter == null)) {
            super.paintComponent(g);
        } else {
            invokePainter(g, bgPainter);
            invokePainter(g, fgPainter);
        }
    }
    
    private void invokePainter(Graphics g, Painter<JXButton> ptr) {
        if(ptr == null) return;
        
        if(isPaintBorderInsets()) {
            ptr.paint((Graphics2D)g, this, getWidth(), getHeight());
        } else {
            Graphics2D g2 = (Graphics2D) g;
            Insets ins = this.getInsets();
            g2.translate(ins.left, ins.top);
            ptr.paint(g2, this,
                    this.getWidth() - ins.left - ins.right,
                    this.getHeight() - ins.top - ins.bottom);
            g2.translate(-ins.left, -ins.top);
        }
    }
    // paint anything but text and icon
    private static final class DefaultBackgroundPainter extends AbstractPainter<JXButton> {
        protected void doPaint(Graphics2D g, JXButton b, int width, int height) {
            b.setPainting(true);
            String tmp = b.text;
            b.text = "";
            b.paint(g);
            b.text = tmp;
            b.setPainting(false);
        }

        //if any of the state of the JButton that affects the background has changed,
        //then I must clear the cache. This is really hard to get right, there are
        //bound to be bugs. An alternative is to NEVER cache.
        protected boolean shouldUseCache() {
            return false;
        }
    }
    // paint only a text and icon (if any)
    private static final class DefaultForegroundPainter extends AbstractPainter<JXButton> {
        protected void doPaint(Graphics2D g, JXButton b, int width, int height) {
            b.setPainting(true);
            boolean t1 = b.isBorderPainted();
            boolean t2 = b.isContentAreaFilled();
            b.borderPainted = false;
            b.contentAreaFilled = false;
            b.paint(g);
            b.borderPainted = t1;
            b.contentAreaFilled = t2;
            b.setPainting(false);
             
        }

        //if any of the state of the JButton that affects the foreground has changed,
        //then I must clear the cache. This is really hard to get right, there are
        //bound to be bugs. An alternative is to NEVER cache.
        protected boolean shouldUseCache() {
            return false;
        }
    }

    protected void setPainting(boolean b) {
        painting = b;
    }
}
