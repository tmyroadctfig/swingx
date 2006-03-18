/*
 * ColorUtil.java
 *
 * Created on March 13, 2006, 5:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.color;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
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
