/*
 * EmptyTileFactory.java
 *
 * Created on June 7, 2006, 4:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer.empty;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Tile;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.TilePoint;

/**
 *
 * @author joshy
 */
public class EmptyTileFactory extends TileFactory {
    BufferedImage emptyTile;
    /** Creates a new instance of EmptyTileFactory */
    public EmptyTileFactory() {
        emptyTile = new BufferedImage(256,256,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = emptyTile.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(Color.GRAY);
        g.fillRect(0,0,256,256);
        g.setColor(Color.WHITE);
        g.drawOval(10,10,236,236);
        g.fillOval(70,50,20,20);
        g.fillOval(256-90,50,20,20);
        g.fillOval(128-10,128-10,20,20);
        g.dispose();
    }

    public int getTileSize() {
        return 256;
    }

    public Dimension getMapSize(int zoom) {
        int size = (int)Math.pow(2,17-zoom);
        System.out.println("map size: " + zoom + " " + size);
        return new Dimension(size,size);
    }

    public Tile getTile(TilePoint tilePoint, int zoom) {
        return new Tile(tilePoint, zoom) {
            public boolean isLoaded() {
                return true;
            }
            public BufferedImage getImage() {
                return emptyTile;
            }
            public boolean isDummy() {
                return false;
            }
        };
    }

    public GeoPosition pixelToGeo(Point2D pixelCoordinate, int zoom) {
        return new GeoPosition(0,0);
    }

    public Point2D getBitmapCoordinate(GeoPosition c, int zoomLevel) {
        return new Point2D.Double(0,0);
    }

    public TilePoint getTileCoordinate(double latitude, double longitude, int zoomLevel) {
        return new TilePoint(0,0);
    }

    public TilePoint getTileCoordinate(Point2D pixelCoordinate) {
        return new TilePoint(0,0);
    }
    
}
