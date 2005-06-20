/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.util;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * A collection of utilties for painting visual effects.
 * 
 * @author Mark Davidson
 */
public class PaintUtils {

    //  Utility methods. 

    private static Border defaultBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);

    public static Border getDefaultBorder() {
	return defaultBorder;
    }

    /**
     * Returns the bounds that the text of a label will be drawn into.
     * Takes into account the current font metrics.
     */
    public static Rectangle getTextBounds(Graphics g, JLabel label) {

	FontMetrics fm = g.getFontMetrics();
	Rectangle2D r2d = fm.getStringBounds(label.getText(), g);
	Rectangle rect = r2d.getBounds();

	int xOffset = 0;
	switch (label.getHorizontalAlignment()) {
	case SwingConstants.RIGHT:
	case SwingConstants.TRAILING:
	    xOffset = label.getBounds().width - rect.width;
	    break;

	case SwingConstants.CENTER:
	    xOffset = (label.getBounds().width - rect.width)/2;
	    break;

	default:
	case SwingConstants.LEFT:
	case SwingConstants.LEADING:
	    xOffset = 0;
	    break;
	}

	int yOffset = 0;
	switch (label.getVerticalAlignment()) {
	case SwingConstants.TOP:
	    yOffset = 0;
	    break;
	case SwingConstants.CENTER:
	    yOffset = (label.getBounds().height - rect.height)/2;
	    break;
	case SwingConstants.BOTTOM:
	    yOffset = label.getBounds().height - rect.height;
	    break;
	}
	return new Rectangle(xOffset, yOffset, rect.width, rect.height);
    }

    /**
     * Paints a top to bottom gradient fill over the component bounds 
     * from color1 to color2.
     */
    public static void paintGradient(Graphics g, JComponent comp, 
				     Color color1, Color color2) {
	GradientPaint paint = new GradientPaint(0, 0, color1, 
						0, comp.getHeight(), color2, 
						true);
	Graphics2D g2 = (Graphics2D)g;
	Paint oldPaint = g2.getPaint();

	g2.setPaint(paint);
	g2.fillRect(0, 0, comp.getWidth(), comp.getHeight());
	g2.setPaint(oldPaint);
    }

    /**
     * Sets the background color for a containment hierarchy.
     */
    public static void setBackgroundColor(Container cont, Color color) {
	cont.setBackground(color);
	Component[] children = cont.getComponents();
	for (int i = 0; i < children.length; i++) {
	    if (children[i] instanceof Container) {
		setBackgroundColor((Container)children[i], color);
	    } else {
		children[i].setBackground(color);
	    }
	}
    }

    /**
     * Sets the foreground color for a containment hierarchy.
     */
    public static void setForegroundColor(Container cont, Color color) {
	cont.setForeground(color);
	Component[] children = cont.getComponents();
	for (int i = 0; i < children.length; i++) {
	    if (children[i] instanceof Container) {
		setForegroundColor((Container)children[i], color);
	    } else {
		children[i].setForeground(color);
	    }
	}
    }

    public static void setFont(Container cont, Font font) {
	cont.setFont(font);
	Component[] children = cont.getComponents();
	for (int i = 0; i < children.length; i++) {
	    if (children[i] instanceof Container) {
		setFont((Container)children[i], font);
	    } else {
		children[i].setFont(font);
	    }
	}
    }
}
