/*
 * OptimizedEffect.java
 *
 * Created on April 25, 2006, 8:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 *
 * @author joshy
 */
public interface OptimizedEffect {
    public BufferedImage applyOptimized(BufferedImage image, Painter painter, JComponent component);    
}
