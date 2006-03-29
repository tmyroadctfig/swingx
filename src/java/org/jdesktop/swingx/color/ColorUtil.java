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
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * TODO May want to move to org.jdesktop.swingx.util
 *
 * @author joshy
 */
public class ColorUtil {
    
    /** Creates a new instance of ColorUtil */
    public ColorUtil() {
    }
    
    public static Color removeAlpha(Color color) {
        return new Color(color.getRed(),color.getGreen(),color.getBlue());
    }
    public static Color setAlpha(Color col, int alpha) {
        return new Color(col.getRed(),col.getGreen(),col.getBlue(),alpha);
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
    
}
