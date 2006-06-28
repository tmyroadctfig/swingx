/*
 * WaypointRenderer.java
 *
 * Created on March 30, 2006, 5:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.jdesktop.swingx.JXMapViewer;

/**
 *
 * @author joshy
 */
public class DefaultWaypointRenderer implements WaypointRenderer {
        
    public boolean paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint waypoint) {
        
        Point2D center = map.getTileFactory().getBitmapCoordinate(waypoint.getPosition(), map.getZoom());
        //must adjust for the viewport.... this is lame and should be in JXMapViewer, I think
        Rectangle bounds = map.getViewportBounds();
        int x = (int)(center.getX() - bounds.getX());
        int y = (int)(center.getY() - bounds.getY());
        
        g.setStroke(new BasicStroke(3f));
        g.setColor(Color.BLUE);
        g.drawOval(x-10,y-10,20,20);
        return false;
    }
}
