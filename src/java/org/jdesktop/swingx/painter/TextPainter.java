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

package org.jdesktop.swingx.painter;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Point2D;
import javax.swing.JComponent;
import org.jdesktop.swingx.util.Resize;

/**
 * "Paints" text at the given location. The location should be a point where
 * the x and y values are in the range of 0 to 1. Similar to the CSS background
 * positioning algorithm, each value will be scaled along each axis of the component
 * this TextPainter is painting, and then used to position the text. A value of
 * 0,0 would put the text in the upper lefthand corner. 0.5,0.5 would position the
 * text in the center and 1.0,1.0 would be at the lower righthand corner. For a more
 * complete defintion of the positioning algorithm see the 
 * <a href="http://www.w3.org/TR/CSS21/colors.html#propdef-background-position">CSS 2.1 spec</a>.
 *
 * @author rbair
 */
public class TextPainter extends AbstractPainter {
    private Resize resize;
    private String text = "";
    private Font font;
    private Paint paint;
    private Point2D location = new Point2D.Double(.0, .0);
    
    /** Creates a new instance of TextPainter */
    public TextPainter() {
    }
    
    public TextPainter(String text) {
        this(text, new Font("Dialog", Font.PLAIN, 12));
    }
    
    public TextPainter(String text, Font font) {
        this(text, font, Color.BLACK);
    }
    
    public TextPainter(String text, Font font, Paint paint) {
        this.text = text;
        this.font = font;
        this.paint = paint;
    }
    
    public void setFont(Font f) {
        Font old = getFont();
        this.font = f;
        firePropertyChange("font", old, getFont());
    }
    
    public Font getFont() {
        return font;
    }
    
    public void setText(String text) {
        String old = getText();
        this.text = text == null ? "" : text;
        firePropertyChange("text", old, getText());
    }
    
    public String getText() {
        return text;
    }
    
    public void setPaint(Paint paint) {
        Paint old = getPaint();
        this.paint = paint;
        firePropertyChange("paint", old, getPaint());
    }
    
    public Paint getPaint() {
        return paint;
    }
    
    public void setLocation(Point2D location) {
        Point2D old = getLocation();
        this.location = location == null ? new Point2D.Double(.0, .0) : location;
        firePropertyChange("location", old, getLocation());
    }
    
    public Point2D getLocation() {
        return location;
    }

    protected void paintBackground(Graphics2D g, JComponent component) {
        Font font = getFont();
        if (font != null) {
            g.setFont(font);
        }
        
        Paint paint = getPaint();
        if (paint != null) {
            g.setPaint(paint);
        }
        
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        String text = getText();
        
        Point2D location = getLocation();
        int tw = metrics.stringWidth(text);
        int th = metrics.getHeight();
        double x = location.getX() * (component.getWidth()-tw);
        double y = location.getY() * (component.getHeight()-th);
        y += metrics.getAscent();
        
        //double stringWidth = SwingUtilities.computeStringWidth(metrics, text);
        //x -= stringWidth/2;
        g.drawString(text, (float)x, (float)y);
    }
}
