/*
 * GeoPosition.java
 *
 * Created on March 31, 2006, 9:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

/**
 * A coordinate in the real world, composed of a latitude and a longitude
 *
 * @author rbair
 */
public class GeoPosition {
    private double latitude;
    private double longitude;
    
    /**
     * Creates a new instance of GeoPosition
     */
    public GeoPosition(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof GeoPosition) {
            GeoPosition coord = (GeoPosition)obj;
            return coord.latitude == latitude && coord.longitude == longitude;
        }
        return false;
    }
    
    public String toString() {
        return "[" + latitude + ", " + longitude + "]";
    }
}