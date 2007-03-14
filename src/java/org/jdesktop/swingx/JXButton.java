/*
 * JXButton.java
 *
 * Created on July 25, 2006, 3:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JButton;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.Painter;

/**
 *
 * @author jm158417
 */
public class JXButton extends JButton {
    
    /** Creates a new instance of JXButton */
    public JXButton() {
        super();
        initPainterSupport();
    }
    
    public JXButton(String text) {
        super(text);
        initPainterSupport();
    }
    
    private void initPainterSupport() {
        foregroundPainter = new AbstractPainter<JXButton>() {
            protected void doPaint(Graphics2D g, JXButton component, int width, int height) {
                JXButton.super.paintComponent(g);
            }
        };
    }
    
    private Painter backgroundPainter;
    private Painter foregroundPainter;

    public Painter getBackgroundPainter() {
        return backgroundPainter;
    }

    public void setBackgroundPainter(Painter backgroundPainter) {
        Painter old = this.getBackgroundPainter();
        this.backgroundPainter = backgroundPainter;
        firePropertyChange("backgroundPainter", old, backgroundPainter);
        repaint();
    }
    
    public Painter getForegroundPainter() {
        return foregroundPainter;
    }
    
    public void setForegroundPainter(Painter painter) {
        Painter old = this.getForegroundPainter();
        this.foregroundPainter = painter;
        firePropertyChange("foregroundPainter", old, foregroundPainter);
        repaint();
    }
    
    protected void paintComponent(Graphics g) {
        if (backgroundPainter != null) {
            Graphics2D g2 = (Graphics2D)g.create();
            backgroundPainter.paint(g2, this, this.getWidth(), this.getHeight());
            g2.dispose();
        }
        
        if (foregroundPainter != null) {
            Graphics2D g2 = (Graphics2D)g.create();
            foregroundPainter.paint(g2, this, this.getWidth(), this.getHeight());
            g2.dispose();
        } else {
            //super.paintComponent(g);
        }        
    }
    
}
