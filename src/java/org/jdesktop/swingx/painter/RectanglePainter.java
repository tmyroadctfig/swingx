/*
 * RectanglePainter.java
 *
 * Created on July 12, 2006, 6:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

//import org.jdesktop.swingx.painter.Mask;

/**
 *
 * @author jm158417
 */
public class RectanglePainter extends AbstractPainter {
    private Paint fillPaint = Color.RED;
    private Paint borderPaint = Color.BLACK;
    private boolean rounded = false;
    private Insets insets = new Insets(5,5,5,5);
    private int roundWidth = 20;
    private int roundHeight = 20;
    private double strokeWidth = 1;
    
    
    /** Creates a new instance of RectanglePainter */
    public RectanglePainter() {
    }
    public RectanglePainter(int top, int left, int bottom, int right,
            int roundWidth, int roundHeight, boolean rounded, Paint fillPaint,
            double strokeWidth, Paint borderPaint) {
        this();
        insets = new Insets(top,left,bottom,right);
        this.roundWidth = roundWidth;
        this.roundHeight = roundHeight;
        this.rounded = rounded;
        this.fillPaint = fillPaint;
        this.strokeWidth = strokeWidth;
        this.borderPaint = borderPaint;
    }
    
    protected Shape calculateShape(JComponent component) {
        Shape shape = new Rectangle2D.Double(insets.left, insets.top,
                    component.getWidth()-insets.left-insets.right,
                    component.getHeight()-insets.top-insets.bottom);
        if(rounded) {
            shape = new RoundRectangle2D.Double(insets.left, insets.top,
                    component.getWidth()-insets.left-insets.right,
                    component.getHeight()-insets.top-insets.bottom,
                    roundWidth, roundHeight);
        }
        return shape;
    }

    protected void paintBackground(Graphics2D g, JComponent component) {
        Shape shape = calculateShape(component);
        
        // background
        g.setPaint(fillPaint);
        g.fill(shape);
        
        // border
        g.setPaint(borderPaint);
        g.setStroke(new BasicStroke((float)strokeWidth));
        g.draw(shape);
        
        // leave the clip to support masking other painters
        g.setClip(shape);
    }

    public Paint getFillPaint() {
        return fillPaint;
    }

    public void setFillPaint(Paint fillPaint) {
        Paint oldFillPaint = getFillPaint();
        this.fillPaint = fillPaint;
        firePropertyChange("fillPaint",oldFillPaint,fillPaint);
    }

    public Paint getBorderPaint() {
        return borderPaint;
    }

    public void setBorderPaint(Paint borderPaint) {
        Paint oldBorderPaint = getBorderPaint();
        this.borderPaint = borderPaint;
        firePropertyChange("fillPaint",oldBorderPaint,borderPaint);
    }

    public boolean isRounded() {
        return rounded;
    }

    public void setRounded(boolean rounded) {
        boolean oldRounded = isRounded();
        this.rounded = rounded;
        firePropertyChange("rounded",oldRounded,rounded);
    }

    public Insets getInsets() {
        return insets;
    }

    public void setInsets(Insets insets) {
        Insets oldInsets = getInsets();
        this.insets = insets;
        firePropertyChange("insets",oldInsets,insets);
    }

    public int getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(int roundWidth) {
        int oldRoundWidth = getRoundWidth();
        this.roundWidth = roundWidth;
        firePropertyChange("roundWidth",oldRoundWidth,roundWidth);
    }

    public int getRoundHeight() {
        return roundHeight;
    }

    public void setRoundHeight(int roundHeight) {
        int oldRoundHeight = getRoundHeight();
        this.roundHeight = roundHeight;
        firePropertyChange("roundHeight",oldRoundHeight,roundHeight);
    }

    public double getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(double strokeWidth) {
        double oldStrokeWidth = getStrokeWidth();
        this.strokeWidth = strokeWidth;
        firePropertyChange("strokeWidth",oldStrokeWidth,strokeWidth);
    }

    /**
     * Getter for property maskShape.
     * @return Value of property maskShape.
     */
    /*
    public Shape getMaskShape(JComponent component) {
        return calculateShape(component);
    }
     */

    
}
