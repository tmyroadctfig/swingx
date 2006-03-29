/*
 * GradientPreviewPanel.java
 *
 * Created on February 13, 2006, 10:47 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.color;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;
import javax.swing.event.MouseInputAdapter;
import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.jdesktop.swingx.JXGradientChooser;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.multislider.Thumb;

/**
 *
 * @author jm158417
 */
public class GradientPreviewPanel extends JXPanel {
    private Paint checker_texture = null;
    private Point2D start, end;
    public JXGradientChooser picker;
    boolean moving_start = false;
    boolean moving_end = false;

    public GradientPreviewPanel() {
	start = new Point2D.Float(10,10);
	end = new Point2D.Float(80,10);
	checker_texture = ColorUtil.getCheckerPaint();
	MouseInputAdapter ma = new GradientMouseHandler();
	this.addMouseListener(ma);
	this.addMouseMotionListener(ma);
    }

    public void setGradient() {
	repaint();
    }

    protected void paintComponent(Graphics g) {
	try {
	    Graphics2D g2 = (Graphics2D)g;

            // fill the background with checker first
	    g2.setPaint(checker_texture);
	    g.fillRect(0,0,getWidth(),getHeight());

	    // calculate the color stops
	    List<Thumb<Color>> stops = picker.getSlider().getModel().getSortedThumbs();
	    int len = stops.size();

	    // set up the data for the gradient
	    float[] fractions = new float[len];
	    Color[] colors = new Color[len];
	    int i = 0;
	    for (Thumb<Color> thumb : stops) {
		colors[i] = (Color)thumb.getObject();
		fractions[i] = thumb.getPosition();
		i++;
	    }

	    // get the final gradient
	    MultipleGradientPaint paint = calculateGradient(fractions, colors);

	    // fill the area
	    if(paint != null) {
		g2.setPaint(paint);
	    } else {
		g2.setPaint(Color.black);
	    }

	    g.fillRect(0,0,getWidth(),getHeight());

	    drawHandles(g2);
	} catch (Exception ex) {
	    System.out.println("ex: " + ex);
	}
    }

    private MultipleGradientPaint calculateGradient(final float[] fractions, final Color[] colors) {
	// set up the end points
	Point2D start = this.start;
	Point2D end = this.end;
	if(picker.reversed.isSelected()) {
	    start = this.end;
	    end = this.start;
	}

	// set up the cycle type
	MultipleGradientPaint.CycleMethodEnum cycle = MultipleGradientPaint.NO_CYCLE;
	if(picker.repeated.isSelected()) {
	    cycle = MultipleGradientPaint.REPEAT;
	}
	if(picker.reflected.isSelected()) {
	    cycle = MultipleGradientPaint.REFLECT;
	}
	
	// create the underlying gradient paint
	MultipleGradientPaint paint = null;
	if(picker.style_list.getSelectedItem().toString().equals("Linear")) {
	    paint = new org.apache.batik.ext.awt.LinearGradientPaint(
	    (float)start.getX(),
	    (float)start.getY(),
	    (float)end.getX(),
	    (float)end.getY(),
	    fractions,colors,cycle);
	}
	if(picker.style_list.getSelectedItem().toString().equals("Radial")) {
	    paint = new org.apache.batik.ext.awt.RadialGradientPaint(
	    start, (float)start.distance(end),start,
	    fractions, colors, cycle, MultipleGradientPaint.SRGB
	    );
	}
	return paint;
    }
    
    private void drawHandles(final Graphics2D g2) {
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	RenderingHints.VALUE_ANTIALIAS_ON);
	// draw the points and gradient line
	g2.setColor(Color.black);
	g2.drawOval((int)start.getX()-5,(int)start.getY()-5,10,10);
	g2.setColor(Color.white);
	g2.drawOval((int)start.getX()-4,(int)start.getY()-4,8,8);
	
	g2.setColor(Color.black);
	g2.drawOval((int)end.getX()-5,(int)end.getY()-5,10,10);
	g2.setColor(Color.white);
	g2.drawOval((int)end.getX()-4,(int)end.getY()-4,8,8);
	
	g2.setColor(Color.darkGray);
	g2.drawLine((int)start.getX(),(int)start.getY(),
	(int)end.getX(),(int)end.getY());
	g2.setColor(Color.gray);
	g2.drawLine((int)start.getX()-1,(int)start.getY()-1,
	(int)end.getX()-1,(int)end.getY()-1);
    }

    private class GradientMouseHandler extends MouseInputAdapter {

	public void mousePressed(MouseEvent evt) {
	    moving_start = false;
	    moving_end = false;
	    if (evt.getPoint().distance(start) < 5) {
		moving_start = true;
		start = evt.getPoint();
		return;
	    }
            
	    if (evt.getPoint().distance(end) < 5) {
		moving_end = true;
		end = evt.getPoint();
		return;
	    }

	    start = evt.getPoint();
	}

	public void mouseDragged(MouseEvent evt) {
	    if (moving_start) {
		start = evt.getPoint();
	    } else {
		end = evt.getPoint();
	    }
	    repaint();
	}
    }
}

