/*
 * WaypointMapOverlay.java
 *
 * Created on April 1, 2006, 4:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.painter.AbstractPainter;

/**
 * Paints waypoints on the JXMapViewer
 *
 * TODO AbstractPainter must be altered so that it allows me to extend it
 * and specify the component type I care about
 *
 * @author rbair
 */
public abstract class WaypointMapOverlay<T extends JXMapViewer> extends AbstractPainter<T> implements MapOverlay<T> {
    private WaypointRenderer renderer = new DefaultWaypointRenderer();
    
    /**
     * Creates a new instance of WaypointMapOverlay
     */
    public WaypointMapOverlay() {
        setUseCache(false);
        setAntialiasing(RenderingHints.VALUE_ANTIALIAS_ON);
    }
    
    public void setRenderer(WaypointRenderer r) {
        this.renderer = r;
    }
    
    public Set<Waypoint> getWaypoints() {
        return new HashSet<Waypoint>();
    }
    
    @Override
    protected void paintBackground(Graphics2D g, T map) {
        if (renderer == null) {
            return;
        }
        
        //figure out which waypoints are within this map viewport
        //so, get the bounds
        Rectangle viewportBounds = map.getViewportBounds();
        
        //for each waypoint within these bounds
        Waypoint waypointWithPopup = null;
        for (Waypoint w : getWaypoints()) {
            Point2D point = map.getTileFactory().getBitmapCoordinate(w.getPosition(), map.getZoom());
            //Point2D point = GoogleUtil.getBitmapCoordinate(w.getPosition(), map.getZoom());
            if (viewportBounds.contains(point)) {
                if (renderer.paintWaypoint(g, map, w)) {
                    waypointWithPopup = w;
                }
            }
        }
        
        if (waypointWithPopup != null) {
            paintWaypointSummary(g, map, (Waypoint)waypointWithPopup);
        }
    }

    protected abstract void paintWaypointSummary(Graphics2D g, JXMapViewer map, Waypoint waypoint);
    
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}
