/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import org.jdesktop.swingx.color.ColorUtil;

/**
 * A specific painter that paints an "infinite progress" like animation.
 *
 */
public class BusyPainter<T> extends AbstractPainter<T> {
    private int frame = -1;
    private boolean skewed = false;
    private int points = 8;
    private float barWidth = 4;
    private float barLength = 8;
    private float centerDistance = 5;
    
    private Color baseColor = new Color(200,200,200);
    private Color highlightColor = Color.BLACK;
    private int trailLength = 4;

    /**
     * @inheritDoc
     */
    @Override
    protected void doPaint(Graphics2D g, T t, int width, int height) {
        RoundRectangle2D rect = new RoundRectangle2D.Float(getCenterDistance(), -getBarWidth()/2,
                getBarLength(), getBarWidth(),
                getBarWidth(), getBarWidth());
        if(skewed) {
            rect = new RoundRectangle2D.Float(5,getBarWidth()/2,8, getBarWidth(),
                    getBarWidth(), getBarWidth());
        }
        g.setColor(Color.GRAY);
        
        g.translate(width/2,height/2);
        for(int i=0; i<getPoints(); i++) {
            g.setColor(calcFrameColor(i));
            g.fill(rect);
            g.rotate(Math.PI*2.0/(double)getPoints());
        }
    }
    
    
    private Color calcFrameColor(final int i) {
        if(frame == -1) {
            return getBaseColor();
        }
        
        for(int t=0; t<getTrailLength(); t++) {
            if(i == (frame-t+getPoints())%getPoints()) {
                float terp = 1-((float)(getTrailLength()-t))/(float)getTrailLength();
                return ColorUtil.interpolate(
                        getBaseColor(),
                        getHighlightColor(), terp);
            }
        }
        return getBaseColor();
    }
    
    public int getFrame() {
        return frame;
    }
    
    public void setFrame(int frame) {
        this.frame = frame;
    }
    
    public Color getBaseColor() {
        return baseColor;
    }
    
    public void setBaseColor(Color baseColor) {
        Color old = getBaseColor();
        this.baseColor = baseColor;
        setDirty(true);
        firePropertyChange("baseColor", old, getBaseColor());
    }
    
    public Color getHighlightColor() {
        return highlightColor;
    }
    
    public void setHighlightColor(Color highlightColor) {
        Color old = getHighlightColor();
        this.highlightColor = highlightColor;
        setDirty(true);
        firePropertyChange("highlightColor", old, getHighlightColor());
    }
    
    public float getBarWidth() {
        return barWidth;
    }
    
    public void setBarWidth(float barWidth) {
        float old = getBarWidth();
        this.barWidth = barWidth;
        setDirty(true);
        firePropertyChange("barWidth", old, getBarWidth());
    }
    
    public float getBarLength() {
        return barLength;
    }
    
    public void setBarLength(float barLength) {
        float old = getBarLength();
        this.barLength = barLength;
        setDirty(true);
        firePropertyChange("barLength", old, getBarLength());
    }
    
    public float getCenterDistance() {
        return centerDistance;
    }
    
    public void setCenterDistance(float centerDistance) {
        float old = getCenterDistance();
        this.centerDistance = centerDistance;
        setDirty(true);
        firePropertyChange("centerDistance", old, getCenterDistance());
    }
    
    public int getPoints() {
        return points;
    }
    
    public void setPoints(int points) {
        int old = getPoints();
        this.points = points;
        setDirty(true);
        firePropertyChange("points", old, getPoints());
    }

    public int getTrailLength() {
        return trailLength;
    }

    public void setTrailLength(int trailLength) {
        int old = getTrailLength();
        this.trailLength = trailLength;
        setDirty(true);
        firePropertyChange("trailLength", old, getTrailLength());
    }
    
}