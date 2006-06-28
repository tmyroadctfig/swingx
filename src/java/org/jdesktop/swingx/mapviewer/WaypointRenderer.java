/*
 * WaypointRenderer.java
 *
 * Created on March 30, 2006, 5:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

import java.awt.Graphics2D;

import org.jdesktop.swingx.JXMapViewer;

/**
 *
 * @author joshy
 */
public interface WaypointRenderer {
    public boolean paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint waypoint);
    
}
