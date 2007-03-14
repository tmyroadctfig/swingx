/*
 * InnerGlowPathEffect.java
 *
 * Created on October 18, 2006, 10:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter.effects;

import java.awt.Color;
import java.awt.Point;

/**
 * An effect which draws a glow inside the painter's shape
 * @author joshy
 */
public class InnerGlowPathEffect extends AbstractAreaEffect {
    
    /** Creates a new instance of InnerGlowPathEffect */
    public InnerGlowPathEffect() {
        super();
        setBrushColor(Color.WHITE);
        setBrushSteps(10);
        setEffectWidth(10);
        setShouldFillShape(false);
        setOffset(new Point(0,0));
        setRenderInsideShape(true);
    }
    
}
