/*
 * GlowPathEffect.java
 *
 * Created on October 18, 2006, 9:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter.effects;

import java.awt.Color;
import java.awt.Point;

/**
 * An effect which draws a glow around the painter
 * @author joshy
 */
public class GlowPathEffect extends AbstractAreaEffect {
    
    /**
     * Creates a new instance of GlowPathEffect
     */
    public GlowPathEffect() {
        super();
        setBrushColor(Color.WHITE);
        setBrushSteps(10);
        setEffectWidth(10);
        setShouldFillShape(false);
        setOffset(new Point(0,0));
    }
    
}
