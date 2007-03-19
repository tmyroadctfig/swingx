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
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.Painter;

/**
 * A JLabel subclass which supports Painters with the foregroundPainter and backgroundpainter
 * properties. By default the
 * foregroundPainter is set to a special painter which will draw the label normally, as specified
 * by the current look and feel. Setting a new foregroundPainter will replace the existing one.
 * To modify the standard drawing behavior developers may wrap the standard painter with a
 * CompoundPainter.  
 * 
 * Ex:
 * 
 * JXLabel label = new JXLabel();
 * Painter standardPainter = label.getForegroundPainter();
 * MattePainter blue = new MattePainter(Color.BLUE);
 * CompoundPainter compound = new CompoundPainter(blue,standardPainter);
 * label.setForegroundPainter(label);
 * 
 * @author joshua.marinacci@sun.com
 */
public class JXLabel extends JLabel {
    private Painter foregroundPainter;
    private Painter backgroundPainter;
    /**
     * 
     */
    public JXLabel() {
        super();
        initPainterSupport();
    }
    /**
     * 
     * @param text 
     */
    public JXLabel(String text) {
        super(text);
        initPainterSupport();
    }
    private void initPainterSupport() {
        foregroundPainter = new AbstractPainter<JXLabel>() {
            protected void doPaint(Graphics2D g, JXLabel component, int width, int height) {
                JXLabel.super.paintComponent(g);
            }
        };
    }
    
    /**
     * Returns the current foregroundPainter. This is a bound property.  By default the foregroundPainter
     * will be an internal painter which executes the standard painting code (paintComponent()).
     * @return 
     */
    public Painter getForegroundPainter() {
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
        if (painter != null) {
            setOpaque(false);
        }
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
    public Painter getBackgroundPainter() {
        return backgroundPainter;
    }
    
    /**
     * Overridden to provide Painter support. It will call backgroundPainter.paint()
     * then foregroundPainter.paint() if they are not null. If both are null
     * then it will call super.paintComponent().
     * 
     * @param g graphics to paint on
     */
    protected void paintComponent(Graphics g) {
        if(getBackgroundPainter() != null) {
            Graphics2D g2 = (Graphics2D)g.create();
            getBackgroundPainter().paint(g2, this,
                    this.getWidth(),
                    this.getHeight());
            g2.dispose();
        }
        if(getForegroundPainter() != null) {
            Graphics2D g2 = (Graphics2D)g.create();
            getForegroundPainter().paint(g2, this,
                    this.getWidth(),
                    this.getHeight());
            g2.dispose();
        }
        if(getBackgroundPainter() == null && getForegroundPainter() == null) {
            super.paintComponent(g);
        }
    }
}
