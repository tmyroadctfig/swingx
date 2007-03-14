package org.jdesktop.swingx.painter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import org.jdesktop.swingx.color.ColorUtil;

public class BusyPainter extends AbstractPainter {
    private int frame = -1;
    private boolean skewed = false;
    private int points = 8;
    private float barWidth = 4;
    private float barLength = 8;
    private float centerDistance = 5;
    
    private Color baseColor = new Color(200,200,200);
    private Color highlightColor = Color.BLACK;
    private int trailLength = 4;
    
    protected void doPaint(Graphics2D g, Object component, int width, int height) {
        g = (Graphics2D) g.create();
        
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
        g.dispose();
    }
    
    
    private Color calcFrameColor(final int i) {
        // if stopped then all are grey
        int grey = 220;
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
        this.baseColor = baseColor;
    }
    
    public Color getHighlightColor() {
        return highlightColor;
    }
    
    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
    }
    
    public float getBarWidth() {
        return barWidth;
    }
    
    public void setBarWidth(float barWidth) {
        this.barWidth = barWidth;
    }
    
    public float getBarLength() {
        return barLength;
    }
    
    public void setBarLength(float barLength) {
        this.barLength = barLength;
    }
    
    public float getCenterDistance() {
        return centerDistance;
    }
    
    public void setCenterDistance(float centerDistance) {
        this.centerDistance = centerDistance;
    }
    
    public int getPoints() {
        return points;
    }
    
    public void setPoints(int points) {
        this.points = points;
    }

    public int getTrailLength() {
        return trailLength;
    }

    public void setTrailLength(int trailLength) {
        this.trailLength = trailLength;
    }
    
}