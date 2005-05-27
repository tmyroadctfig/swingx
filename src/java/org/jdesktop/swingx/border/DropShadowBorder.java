/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.border.Border;

/**
 * Implements a DropShadow for components. In general, the DropShadowBorder will
 * work with any rectangular components that do not have a default border installed
 * as part of the look and feel, or otherwise. For example, DropShadowBorder works
 * wonderfully with JPanel, but horribly with JComboBox.
 *
 * @author rbair
 */
public class DropShadowBorder implements Border {
    private static final int SHADOW_WIDTH = 4;
    private static final int SHADOW_HEIGHT = 4;
    
    private ImageIcon topRight;
    private ImageIcon right;
    private ImageIcon bottomRight;
    private ImageIcon bottom;
    private ImageIcon bottomLeft;
    
    private Color borderColor;
    private int lineWidth;
    
    public DropShadowBorder() {
        borderColor = Color.BLACK;
        lineWidth = 1;
        initImages();
    }
    
    public DropShadowBorder(Color c, int width) {
        borderColor = c == null ? Color.BLACK : c;
        lineWidth = width;
        initImages();
    }
    
    public DropShadowBorder(Color c, int width, boolean b, boolean b2) {
        this(c, width);
    }
    
    private void initImages() {
        topRight = new ImageIcon(getClass().getResource("top-right.png"));
        right = new ImageIcon(getClass().getResource("right.png"));
        bottomRight = new ImageIcon(getClass().getResource("bottom-right.png"));
        bottom = new ImageIcon(getClass().getResource("bottom.png"));
        bottomLeft = new ImageIcon(getClass().getResource("bottom-left.png"));
    }
    
    /**
     * @inheritDoc
     */
    public void paintBorder(Component c, Graphics graphics, int x, int y, int width, int height) {
        Graphics2D g = (Graphics2D)graphics;
        //save the old color and the old paint
        Color oldColor = g.getColor();
        
        //compute the edges of the component -- not including the border
        int leftEdge = x;
        int rightEdge = x + width - SHADOW_WIDTH;
        int topEdge = y;
        int bottomEdge = y + height - SHADOW_HEIGHT;
        
        //draw a rectangular border.
        g.setColor(borderColor);
        if (lineWidth > 0) {
            g.drawRect(leftEdge, topEdge, rightEdge - leftEdge, bottomEdge - topEdge);
        }
        
        //draw the tiles
        g.drawImage(topRight.getImage(), rightEdge, 0, topRight.getImageObserver());
        g.drawImage(right.getImage(), rightEdge, topRight.getIconHeight(), right.getIconWidth(), bottomEdge - topRight.getIconHeight(), right.getImageObserver());
        g.drawImage(bottomRight.getImage(), rightEdge, bottomEdge, bottomRight.getImageObserver());
        g.drawImage(bottom.getImage(), leftEdge + bottomLeft.getIconWidth(), bottomEdge, rightEdge - leftEdge - bottomLeft.getIconWidth(), bottom.getIconHeight(), bottom.getImageObserver());
        g.drawImage(bottomLeft.getImage(), leftEdge, bottomEdge, bottomLeft.getImageObserver());
        
        g.setColor(oldColor);
    }
    
    /**
     * @inheritDoc
     */
    public Insets getBorderInsets(Component c) {
        int width = lineWidth + SHADOW_WIDTH;
        int height = lineWidth + SHADOW_HEIGHT;
        return new Insets(lineWidth, lineWidth, height, width);
    }
    
    /**
     * @inheritDoc
     */
    public boolean isBorderOpaque() {
        return true;
    }
}