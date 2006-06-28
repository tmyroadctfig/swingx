/*
 * DropShadowEffect.java
 *
 * Created on April 25, 2006, 8:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import com.jhlabs.image.ShadowFilter;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;


/**
 *
 * @author joshy
 */
public class DropShadowEffect extends ImageEffect implements OptimizedEffect {
    
    /** Creates a new instance of DropShadowEffect */
    public DropShadowEffect() {
        super(new ShadowFilter());
    }

    public BufferedImage applyOptimized(BufferedImage image, Painter painter, 
            JComponent component) {
        if(painter instanceof ShapePainter) {
            ShapePainter sp = (ShapePainter)painter;
            if(sp.getShape() instanceof Rectangle2D) {
                System.out.println("doing a rectangle");
                return image;
            }
        }
        return super.apply(image);
    }
    
    

}
