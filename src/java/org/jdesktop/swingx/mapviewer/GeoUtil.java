/*
 * GeoUtil.java
 *
 * Created on June 26, 2006, 10:44 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

import java.awt.Dimension;
import java.awt.geom.Point2D;

/**
 * These are math utilities for converting between pixels, tiles, and geographic
 * coordinates.
 * @author joshy
 */
public final class GeoUtil {
        
    /**
     * @return the coordinate in <em>tiles</em> of the tile at the given <em>pixel</em>
     * based coordinates, with the given zoom level
     */
    public static TilePoint getTileCoordinate(Point2D pixelCoordinate, TileProviderInfo info) {
        int x = (int)Math.floor(pixelCoordinate.getX() / info.getTileSize());
        int y = (int)Math.floor(pixelCoordinate.getY() / info.getTileSize());
        return new TilePoint(x, y);
    }
    
    /**
     * @return the size of the map at the given zoom, in tiles (num tiles tall
     *         by num tiles wide)
     */
    public static Dimension getMapSize(int zoom, TileProviderInfo info) {
        return new Dimension(info.getMapWidthInTilesAtZoom(zoom), info.getMapWidthInTilesAtZoom(zoom));
    }
    
    /**
     * @return the coordinate in <em>tiles</em> of the tile at the given latitude and longitude, with
     * the given zoom level. For example. if the map at this zoom level is a 10x10 grid of tiles,
     * the point will be between 0 and 9 in both the x and y axis.
     */
    public static TilePoint getTileCoordinate(double latitude, double longitude, int zoomLevel,
            TileProviderInfo info) {
        return getTileCoordinate(getBitmapCoordinate(latitude, longitude, zoomLevel, info), info);
    }

    
    /**
     * @returns true if this point in <em>tiles</em> is valid at this zoom level. For example,
     * if the zoom level is 0 (zoomed all the way out, where there is only
     * one tile), then x,y must be 0,0
     */
    public static boolean isValidTile(TilePoint coord, int zoomLevel, TileProviderInfo info ) {
        int x = (int)coord.getX();
        int y = (int)coord.getY();
        // if off the map to the top or left
        if(x < 0 || y < 0) {
            return false;
        }
        // if of the map to the right
        if(info.getMapCenterInPixelsAtZoom(zoomLevel).getX()*2 <= x*256) {
            return false;
        }
        // if off the map to the bottom
        if(info.getMapCenterInPixelsAtZoom(zoomLevel).getY()*2 <= y*256) {
            return false;
        }
        //if out of zoom bounds
        if(zoomLevel < info.getMinimumZoomLevel() || zoomLevel > info.getMaximumZoomLevel()) {
            return false;
        }
        return true;
    }
    /**
     * Given a position (latitude/longitude pair) and a zoom level, return
     * the appropriate point in <em>pixels</em>. The zoom level is necessary because
     * pixel coordinates are in terms of the zoom level
     * 
     * 
     * @param c A lat/lon pair
     * @param zoomLevel the zoom level to extract the pixel coordinate for
     */
    public static Point2D getBitmapCoordinate(GeoPosition c, int zoomLevel, TileProviderInfo info) {
        return getBitmapCoordinate(c.getLatitude(), c.getLongitude(), zoomLevel, info);
    }
    
    /**
     * Given a position (latitude/longitude pair) and a zoom level, return
     * the appropriate point in <em>pixels</em>. The zoom level is necessary because
     * pixel coordinates are in terms of the zoom level
     * 
     * 
     * @param double latitude
     * @param double longitude
     * @param zoomLevel the zoom level to extract the pixel coordinate for
     */
    public static Point2D getBitmapCoordinate(
            double latitude, 
            double longitude,
            int zoomLevel, TileProviderInfo info) {
        
        double x = info.getMapCenterInPixelsAtZoom(zoomLevel).getX() + longitude
                * info.getLongitudeDegreeWidthInPixels(zoomLevel);
        double e = Math.sin(latitude * (Math.PI / 180.0));
        if (e > 0.9999) {
            e = 0.9999;
        }
        if (e < -0.9999) {
            e = -0.9999;
        }
        double y = info.getMapCenterInPixelsAtZoom(zoomLevel).getY() + 0.5
                * Math.log((1 + e) / (1 - e)) * -1
                * (info.getLongitudeRadianWidthInPixels(zoomLevel));
        return new Point2D.Double(x, y);
    }
    
        
    // convert an on screen pixel coordinate and a zoom level to a
    // geo position
    public static GeoPosition getPosition(Point2D pixelCoordinate, int zoom, TileProviderInfo info) {
        //        p(" --bitmap to latlon : " + coord + " " + zoom);
        double wx = pixelCoordinate.getX();
        double wy = pixelCoordinate.getY();
        // this reverses getBitmapCoordinates
        double flon = (wx - info.getMapCenterInPixelsAtZoom(zoom).getX())
                / info.getLongitudeDegreeWidthInPixels(zoom);
        double e1 = (wy - info.getMapCenterInPixelsAtZoom(zoom).getY())
                / (-1 * info.getLongitudeRadianWidthInPixels(zoom));
        double e2 = (2 * Math.atan(Math.exp(e1)) - Math.PI / 2) / (Math.PI / 180.0);
        double flat = e2;
        GeoPosition wc = new GeoPosition(flat, flon);
        return wc;
    }

    
}
