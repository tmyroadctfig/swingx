/*
 * DefaultTileFactory.java
 *
 * Created on June 27, 2006, 2:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author joshy
 */
public class DefaultTileFactory extends TileFactory {
    
    /** Creates a new instance of DefaultTileFactory */
    public DefaultTileFactory(TileProviderInfo info) {
        this.info = info;
    }

    protected static final boolean doEagerLoading = true;

    private TileProviderInfo info;

    
    
    //TODO the tile map should be static ALWAYS, regardless of the number
    //of GoogleTileFactories because each tile is, really, a singleton.
    protected Map<String, Tile> tileMap = new HashMap<String, Tile>();

    
    public int getTileSize() {
        return getInfo().getTileSize();
    }


    /**
     * <em>IN TILES!!!</em>
     */
    public Dimension getMapSize(int zoom) {
        return GeoUtil.getMapSize(zoom, getInfo());
    }


    public Point2D getBitmapCoordinate(GeoPosition c, int zoomLevel) {
        return GeoUtil.getBitmapCoordinate(c, zoomLevel, getInfo());
    }


    public TilePoint getTileCoordinate(Point2D pixelCoordinate) {
        return GeoUtil.getTileCoordinate(pixelCoordinate, getInfo());
    }


    public TilePoint getTileCoordinate(double latitude, double longitude, int zoomLevel) {
        return GeoUtil.getTileCoordinate(latitude, longitude, zoomLevel, getInfo());
    }

    
        
    // convert an on screen pixel coordinate and a zoom level to a
    // geo position
    public GeoPosition pixelToGeo(Point2D pixelCoordinate, int zoom) {
        return GeoUtil.getPosition(pixelCoordinate,zoom, getInfo());
    }

    
    protected void eagerlyLoad(int x, int y, int zoom) {
        TilePoint t1 = new TilePoint(x,y);
        if(!isLoaded(t1,zoom)) {
            getTile(t1,zoom,false);
        }    
    }

    
    /**
     * @return the tile that is located at the given tilePoint for this zoom. For
     *         example, if getMapSize() returns 10x20 for this zoom, and the
     *         tilePoint is (3,5), then the appropriate tile will be located
     *         and returned.
     */
    public Tile getTile(TilePoint tilePoint, int zoom) {
        return getTile(tilePoint, zoom, true);
    }

    
    protected Tile getTile(TilePoint tilePoint, int zoom, boolean eagerLoad) {
        //wrap the tiles horizontally --> mod the X with the max width
        //and use that
        int tileX = tilePoint.getX();
        int numTilesWide = (int)getMapSize(zoom).getWidth();
        if (tileX < 0) {
            tileX = numTilesWide - (Math.abs(tileX)  % numTilesWide);
        }
         
        tileX = tileX % numTilesWide;
        tilePoint = new TilePoint(tileX, tilePoint.getY());
        String url = getInfo().getTileUrl(zoom, tilePoint);
        //System.out.println("loading: " + url);
        
        
        Tile.Priority pri = Tile.Priority.High;
        if (!eagerLoad) {
            pri = Tile.Priority.Low;
        }
        Tile tile = null;
        if (!tileMap.containsKey(url)) {
            if (!GeoUtil.isValidTile(tilePoint, zoom, getInfo())) {
                tile = new Tile(tilePoint, zoom);
            }  else {
                tile = new Tile(tilePoint, zoom, url, pri);
            }
            tileMap.put(url,tile);
        }  else {
            tile = tileMap.get(url);
            // if its in the map but is low and isn't loaded yet 
            // but we are in high mode
            if (tile.getPriority()  == Tile.Priority.Low && eagerLoad && !tile.isLoaded()) {
                //System.out.println("in high mode and want a low");
                tile.promote();
            }
        }
        
        
        if (eagerLoad && doEagerLoading) {
            for (int i = 0; i<1; i++) {
                for (int j = 0; j<1; j++) {
                    // preload the 4 tiles under the current one
                    if(zoom > 0) {
                        eagerlyLoad(tilePoint.getX()*2,   tilePoint.getY()*2,   zoom-1);
                        eagerlyLoad(tilePoint.getX()*2+1, tilePoint.getY()*2,   zoom-1);
                        eagerlyLoad(tilePoint.getX()*2,   tilePoint.getY()*2+1, zoom-1);
                        eagerlyLoad(tilePoint.getX()*2+1, tilePoint.getY()*2+1, zoom-1);
                    }
                }
            }
        }
        
        
        return tile;
    }

    
    protected boolean isLoaded(TilePoint tilePoint, int zoom) {
        String url = getInfo().getTileUrl(zoom,tilePoint);
        return tileMap.containsKey(url);
    }

    public TileProviderInfo getInfo() {
        return info;
    }
    
}
