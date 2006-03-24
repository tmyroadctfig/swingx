/*
 * CompoundEffect.java
 *
 * Created on March 23, 2006, 4:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import java.awt.image.BufferedImage;
import org.jdesktop.swingx.JavaBean;

/**
 *
 * @author Richard
 */
public class CompoundEffect extends JavaBean implements Effect {
    private Effect[] effects = new Effect[0];
    
    /** Creates a new instance of CompoundEffect */
    public CompoundEffect() {
    }
    
    /**
     * Convenience constructor for creating a CompoundEffect for an array
     * of effects. A defensive copy of the given array is made, so that future
     * modification to the array does not result in changes to the CompoundEffect.
     * 
     * 
     * @param effects array of effects, which will be applied in order
     */
    public CompoundEffect(Effect... effects) {
        this.effects = new Effect[effects == null ? 0 : effects.length];
        if (effects != null) {
            System.arraycopy(effects, 0, this.effects, 0, effects.length);
        }
    }
    
    /**
     * Sets the array of Effects to use. These effects will be executed in
     * order. A null value will be treated as an empty array.
     * 
     * 
     * @param effects array of effects, which will be applied in order
     */
    public void setEffects(Effect... effects) {
        Effect[] old = getEffects();
        this.effects = new Effect[effects == null ? 0 : effects.length];
        if (effects != null) {
            System.arraycopy(effects, 0, this.effects, 0, effects.length);
        }
        firePropertyChange("effects", old, getEffects());
    }
    
    /**
     * @return a defensive copy of the effects used by this CompoundEffect.
     *         This will never be null.
     */
    public Effect[] getEffects() {
        Effect[] results = new Effect[effects.length];
        System.arraycopy(effects, 0, results, 0, results.length);
        return results;
    }

    /**
     * @inheritDoc
     */
    public BufferedImage apply(BufferedImage image) {
        for (Effect e : getEffects()) {
            image = e.apply(image);
        }
        return image;
    }
}
