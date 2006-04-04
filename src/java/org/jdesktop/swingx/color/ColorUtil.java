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

package org.jdesktop.swingx.color;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * TODO May want to move to org.jdesktop.swingx.util
 *
 * @author joshua.marinacci@sun.com
 */
public class ColorUtil {
    
    public static Color removeAlpha(Color color) {
        return new Color(color.getRed(),color.getGreen(),color.getBlue());
    }
    
    public static Color setAlpha(Color col, int alpha) {
        return new Color(col.getRed(),col.getGreen(),col.getBlue(),alpha);
    }
    
    public static Color setBrightness(Color col, float brightness) {
        int alpha = col.getAlpha();
        
        float[] cols = Color.RGBtoHSB(col.getRed(),col.getGreen(),col.getBlue(),null);
        cols[2] = brightness;
        Color c2 = col.getHSBColor(cols[0],cols[1],cols[2]);
        
        return setAlpha(c2,alpha);
    }
    
    public static String toHexString(Color color) {
        return "#"+(""+Integer.toHexString(color.getRGB())).substring(2);        
    }
        
    private static Paint checker_texture = null;

    public static Paint getCheckerPaint() {
	if(checker_texture == null) {
            checker_texture = Color.white;
	    try {
		BufferedImage checker_image = ImageIO.read(
			ColorUtil.class.getResourceAsStream("/icons/checker8.png"));
		Rectangle rect = new Rectangle(0,0,
			checker_image.getWidth(),checker_image.getHeight());
		checker_texture = new TexturePaint(checker_image,rect);
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
        return checker_texture;           
    }
    
    public static void tileStretchPaint(Graphics g, 
                JComponent comp,
                BufferedImage img,
                Insets ins) {
        
        int left = ins.left;
        int right = ins.right;
        int top = ins.top;
        int bottom = ins.bottom;
        
        // top
        g.drawImage(img,
                    0,0,left,top,
                    0,0,left,top,
                    null);
        g.drawImage(img,
                    left,                 0, 
                    comp.getWidth() - right, top, 
                    left,                 0, 
                    img.getWidth()  - right, top, 
                    null);
        g.drawImage(img,
                    comp.getWidth() - right, 0, 
                    comp.getWidth(),         top, 
                    img.getWidth()  - right, 0, 
                    img.getWidth(),          top, 
                    null);

        // middle
        g.drawImage(img,
                    0,    top, 
                    left, comp.getHeight()-bottom,
                    0,    top,   
                    left, img.getHeight()-bottom,
                    null);
        
        g.drawImage(img,
                    left,                  top, 
                    comp.getWidth()-right,      comp.getHeight()-bottom,
                    left,                  top,   
                    img.getWidth()-right,  img.getHeight()-bottom,
                    null);
         
        g.drawImage(img,
                    comp.getWidth()-right,     top, 
                    comp.getWidth(),           comp.getHeight()-bottom,
                    img.getWidth()-right, top,   
                    img.getWidth(),       img.getHeight()-bottom,
                    null);
        
        // bottom
        g.drawImage(img,
                    0,comp.getHeight()-bottom, 
                    left, comp.getHeight(),
                    0,img.getHeight()-bottom,   
                    left,img.getHeight(),
                    null);
        g.drawImage(img,
                    left,                    comp.getHeight()-bottom, 
                    comp.getWidth()-right,        comp.getHeight(),
                    left,                    img.getHeight()-bottom,   
                    img.getWidth()-right,    img.getHeight(),
                    null);
        g.drawImage(img,
                    comp.getWidth()-right,     comp.getHeight()-bottom, 
                    comp.getWidth(),           comp.getHeight(),
                    img.getWidth()-right, img.getHeight()-bottom,   
                    img.getWidth(),       img.getHeight(),
                    null);
    }

}
