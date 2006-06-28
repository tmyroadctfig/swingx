/*
 * TileFactory.java
 *
 * Created on March 17, 2006, 8:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

import java.awt.Dimension;
import java.awt.geom.Point2D;

/**
 *
 * @author joshy
 */
public abstract class TileFactory {
    
    /** Creates a new instance of TileFactory */
    protected TileFactory() {
    }

    /**
     * @return the size of the tiles in pixels. All tiles must be square. A return
     * value of 256, for example, means that each tile will be 256 pixels wide and tall
     */
    public abstract int getTileSize();

    /**
     * @return the ...
     */
//    public abstract BitmapCoordinate getBitmapOffset(BitmapCoordinate point2D, int zoom);
//    public abstract BitmapCoordinate getCoordinateFromPixels(BitmapCoordinate point2D, int zoom);
//    public abstract BitmapCoordinate getTileNumberCoordinates(BitmapCoordinate location, int zoom);
    /**
     * Returns a Tile that is associated with the given ... what the freak
     */
//    public abstract Tile getTile(Point2D location, int zoom, int xoff, int yoff);
    
    /**
     * @return a Dimension containing the width and height of the map, in tiles.
     *           So a Dimension that returns 10x20 would be 10 tiles wide and 20 tiles
     *           tall. These values can be multipled by getTileSize() to determine the
     *           pixel width/height for the map at the given zoom level
     */
    public abstract Dimension getMapSize(int zoom);
    
    /**
     * @return the tile that is located at the given tilePoint for this zoom. For
     *         example, if getMapSize() returns 10x20 for this zoom, and the
     *         tilePoint is (3,5), then the appropriate tile will be located
     *         and returned.
     */
    public abstract Tile getTile(TilePoint tilePoint, int zoom);
    
    
    // convert an on screen pixel coordinate and a zoom level to a
    // geo position
    public abstract GeoPosition pixelToGeo(Point2D pixelCoordinate, int zoom);
    
    /**
     * Given a position (latitude/longitude pair) and a zoom level, return
     * the appropriate point in <em>pixels</em>. The zoom level is necessary because
     * pixel coordinates are in terms of the zoom level
     * 
     * 
     * @param c A lat/lon pair
     * @param zoomLevel the zoom level to extract the pixel coordinate for
     */
     public abstract Point2D getBitmapCoordinate(GeoPosition c, int zoomLevel);

    /**
     * @return the coordinate in <em>tiles</em> of the tile at the given latitude and longitude, with
     * the given zoom level. For example. if the map at this zoom level is a 10x10 grid of tiles,
     * the point will be between 0 and 9 in both the x and y axis.
     */
    public abstract TilePoint getTileCoordinate(double latitude, double longitude, int zoomLevel);
    
     /**
     * @return the coordinate in <em>tiles</em> of the tile at the given <em>pixel</em>
     * based coordinates, with the given zoom level
     */
    public abstract TilePoint getTileCoordinate(Point2D pixelCoordinate);

    public TileProviderInfo getInfo() {
        return null;
    }
    
}
