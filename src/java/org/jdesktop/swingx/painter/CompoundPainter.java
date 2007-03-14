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

package org.jdesktop.swingx.painter;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import org.jdesktop.beans.AbstractBean;

/**
 * <p>A Painter implemention that contains an array of Painters, and executes them
 * in order. This allows you to create a layered series of painters, similar to
 * the layer design style in Photoshop or other image processing software.</p>
 *
 * <p>For example, if I want to create a CompoundPainter that started with a blue
 * background, had pinstripes on it running at a 45 degree angle, and those
 * pinstripes appeared to "fade in" from left to right, I would write the following:
 * <pre><code>
 *  Color blue = new Color(0x417DDD);
 *  Color translucent = new Color(blue.getRed(), blue.getGreen(), blue.getBlue(), 0);
 *  panel.setBackground(blue);
 *  panel.setForeground(Color.LIGHT_GRAY);
 *  GradientPaint blueToTranslucent = new GradientPaint(
 *    new Point2D.Double(.4, 0),
 *    blue,
 *    new Point2D.Double(1, 0),
 *    translucent);
 *  Painter veil =  new BasicGradientPainter(blueToTranslucent);
 *  Painter pinstripes = new PinstripePainter(45);
 *  Painter backgroundPainter = new BackgroundPainter();
 *  Painter p = new CompoundPainter(backgroundPainter, pinstripes, veil);
 *  panel.setBackgroundPainter(p);
 * </code></pre></p>
 *
 * @author rbair
 */
public class CompoundPainter<T> extends AbstractPainter<T> {
    private Painter[] painters = new Painter[0];
    private AffineTransform transform;
    private boolean clipPreserved = false;
    
    /** Creates a new instance of CompoundPainter */
    public CompoundPainter() {
    }
    
    /**
     * Convenience constructor for creating a CompoundPainter for an array
     * of painters. A defensive copy of the given array is made, so that future
     * modification to the array does not result in changes to the CompoundPainter.
     *
     * @param painters array of painters, which will be painted in order
     */
    public CompoundPainter(Painter... painters) {
        this.painters = new Painter[painters == null ? 0 : painters.length];
        if (painters != null) {
            System.arraycopy(painters, 0, this.painters, 0, painters.length);
        }
    }
    
    private boolean useCaching;
    public CompoundPainter(boolean useCaching, Painter ... painters) {
        this(painters);
        this.useCaching = useCaching;
    }
    

    /* joshy: not used since we got rid of layers.
    public CompoundPainter(Map<Integer,List<Painter>> painterSet) {
        // create a flat list of painters
        List<Painter> painterList = new ArrayList<Painter>();
        
        // loop through the painter by layer order
        Set<Integer> layerSet = painterSet.keySet();
        List<Integer> layerList = new ArrayList(layerSet);
        Collections.sort(layerList);
        for(Integer n : layerList) {
            List<Painter> layer = painterSet.get(n);
            for(Painter p : layer) {
                painterList.add(p);
            }
        }
        
        this.painters = painterList.toArray(new Painter[0]);
        
    }*/
    
    
    /**
     * Sets the array of Painters to use. These painters will be executed in
     * order. A null value will be treated as an empty array.
     *
     * @param painters array of painters, which will be painted in order
     */
    public void setPainters(Painter... painters) {
        Painter[] old = getPainters();
        this.painters = new Painter[painters == null ? 0 : painters.length];
        if (painters != null) {
            System.arraycopy(painters, 0, this.painters, 0, painters.length);
        }
        firePropertyChange("painters", old, getPainters());
    }
    
    /**
     * Gets the array of painters used by this CompoundPainter
     * @return a defensive copy of the painters used by this CompoundPainter.
     *         This will never be null.
     */
    public Painter[] getPainters() {
        Painter[] results = new Painter[painters.length];
        System.arraycopy(painters, 0, results, 0, results.length);
        return results;
    }
    
    
    /**
     * Indicates if the clip produced by any painter is left set once it finishes painting. 
     * Normally the clip will be reset between each painter. Setting clipPreserved to
     * true can be used to let one painter mask other painters that come after it.
     * @return if the clip should be preserved
     * @see setClipPreserved(boolean)
     */
    public boolean isClipPreserved() {
        return clipPreserved;
    }
    
    /**
     * Sets if the clip should be preserved.
     * Normally the clip will be reset between each painter. Setting clipPreserved to
     * true can be used to let one painter mask other painters that come after it.
     * 
     * @param shouldRestoreState new value of the clipPreserved property
     * @see isClipPreserved()
     */
    public void setClipPreserved(boolean shouldRestoreState) {
        boolean oldShouldRestoreState = isClipPreserved();
        this.clipPreserved = shouldRestoreState;
        firePropertyChange("shouldRestoreState",oldShouldRestoreState,shouldRestoreState);
    }
    

    
    /**
     * @inheritDoc
     */
    public void doPaint(Graphics2D g, T component, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        if(getTransform() != null) {
            g2.setTransform(getTransform());
        }
        for (Painter p : getPainters()) {
            Graphics2D oldGraphics = g2;
            Graphics2D g3 = (Graphics2D) g2.create();
            p.paint(g3, component, width, height);
            if(isClipPreserved()) {
                oldGraphics.setClip(g3.getClip());
            }
            g3.dispose();
        }
        g2.dispose();
    }
    
    /**
     * Gets the current transform applied to all painters in this CompoundPainter. May be null.
     * @return the current AffineTransform
     */
    public AffineTransform getTransform() {
        return transform;
    }
    
    /**
     * Set a transform to be applied to all painters contained in this GradientPainter
     * @param transform a new AffineTransform
     */
    public void setTransform(AffineTransform transform) {
        AffineTransform old = getTransform();
        this.transform = transform;
        firePropertyChange("transform",old,transform);
    }

    protected boolean isUseCache() {
        return useCaching;
    }
}
