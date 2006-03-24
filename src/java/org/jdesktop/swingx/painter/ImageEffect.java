/*
 * ImageEffect.java
 *
 * Created on March 23, 2006, 4:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import org.jdesktop.swingx.JavaBean;

/**
 *
 * @author Richard
 */
public class ImageEffect extends JavaBean implements Effect {
    private BufferedImageOp op;
    
    /**
     * Creates a new instance of ImageEffect
     */
    public ImageEffect() {
    }

    public ImageEffect(BufferedImageOp op) {
        this.op = op;
    }
    
    public void setOperation(BufferedImageOp op) {
        BufferedImageOp old = getOperation();
        this.op = op;
        firePropertyChange("operation", old, getOperation());
    }
    
    public BufferedImageOp getOperation() {
        return op;
    }

    public BufferedImage apply(BufferedImage image) {
        if (op != null) {
            image = op.filter(image, null);
        }
        return image;
    }
    
}
