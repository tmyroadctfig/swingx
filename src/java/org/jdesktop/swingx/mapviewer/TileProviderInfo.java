/*
 * TileProviderInfo.java
 *
 * Created on June 26, 2006, 10:47 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

import java.awt.geom.Point2D;

/**
 * encapsulates information specific to a map server
 * @author joshy
 */
public class TileProviderInfo {
    private int minimumZoomLevel;
    private int maximumZoomLevel;
    private int totalMapZoom;
    // the size of each tile (assumes they are square)
    private int tileSize = 256;
    
    /*
     * The number of tiles wide at each zoom level
     */
    private int[] mapWidthInTilesAtZoom;
   /**
     * An array of coordinates in <em>pixels</em> that indicates the center in the
     * world map for the given zoom level.
     */
    private Point2D[] mapCenterInPixelsAtZoom;// = new Point2D.Double[18];
    
    /**
     * An array of doubles that contain the number of pixels per degree of
     * longitude at a give zoom level.
     */
    private double[] longitudeDegreeWidthInPixels;
     
    /**
     * An array of doubles that contain the number of radians per degree of
     * longitude at a given zoom level (where longitudeRadianWidthInPixels[0] is 
     * the most zoomed out)
     */
    private double[] longitudeRadianWidthInPixels;
    
    protected String baseURL;
    private String xparam;
    private String yparam;
    private String zparam;
    private boolean xr2l = true;
    private boolean yt2b = true;
    
    /** Creates a new instance of TileProviderInfo */
    /*
     * @param xr2l true if tile x is measured from the far left of the map to the far right, or
     * else false if based on the center line. 
     * @param yt2b true if tile y is measured from the top (north pole) to the bottom (south pole)
     * or else false if based on the equator.
     */
    public TileProviderInfo(int minimumZoomLevel, int maximumZoomLevel, int totalMapZoom, 
            int tileSize, boolean xr2l, boolean yt2b,
            String baseURL, String xparam, String yparam, String zparam) {
        this.minimumZoomLevel = minimumZoomLevel;
        this.maximumZoomLevel = maximumZoomLevel;
        this.totalMapZoom = totalMapZoom;
        this.baseURL = baseURL;
        this.xparam = xparam;
        this.yparam = yparam;
        this.zparam = zparam;
        this.setXr2l(xr2l);
        this.setYt2b(yt2b);
                
        this.tileSize = tileSize;
        
        // init the num tiles wide
        int tilesize = this.getTileSize();

        longitudeDegreeWidthInPixels = new double[totalMapZoom+1];
        longitudeRadianWidthInPixels = new double[totalMapZoom+1];
        mapCenterInPixelsAtZoom = new Point2D.Double[totalMapZoom+1];
        mapWidthInTilesAtZoom = new int[totalMapZoom+1];
    
        // for each zoom level
        for (int z = totalMapZoom; z >= 0; --z) {
            // how wide is each degree of longitude in pixels
            longitudeDegreeWidthInPixels[z] = (double)tilesize / 360;
            // how wide is each radian of longitude in pixels
            longitudeRadianWidthInPixels[z] = (double)tilesize / (2.0*Math.PI);
            int t2 = tilesize / 2;
            mapCenterInPixelsAtZoom[z] = new Point2D.Double(t2, t2);
            mapWidthInTilesAtZoom[z] = tilesize / this.getTileSize();
            tilesize *= 2;
        }

    }

    public int getMinimumZoomLevel() {
        return minimumZoomLevel;
    }

//    public void setMinimumZoomLevel(int minimumZoomLevel) {
//        this.minimumZoomLevel = minimumZoomLevel;
//    }

    public int getMaximumZoomLevel() {
        return maximumZoomLevel;
    }
//
//    public void setMaximumZoomLevel(int maximumZoomLevel) {
//        this.maximumZoomLevel = maximumZoomLevel;
//    }

    public int getTotalMapZoom() {
        return totalMapZoom;
    }
/*
    public void setTotalMapZoom(int totalMapZoom) {
        this.totalMapZoom = totalMapZoom;
    }
*/
    public int getMapWidthInTilesAtZoom(int zoom) {
        return mapWidthInTilesAtZoom[zoom];
    }

    public Point2D getMapCenterInPixelsAtZoom(int zoom) {
        return mapCenterInPixelsAtZoom[zoom];
    }

    
    public String getTileUrl(int zoom, TilePoint tilePoint) {
        //System.out.println("getting tile at zoom: " + zoom);
        //System.out.println("map width at zoom = " + getMapWidthInTilesAtZoom(zoom));
        String ypart = "&" + yparam + "="+tilePoint.getY();
        //System.out.println("ypart = " + ypart);
        
        if(!yt2b) {
            int tilemax = getMapWidthInTilesAtZoom(zoom);
            int y = tilePoint.getY();
            ypart = "&" + yparam + "="+ (tilemax/2-y-1);
        }
        //System.out.println("new ypart = " + ypart);
        String url = baseURL + 
                "&" + xparam + "=" + tilePoint.getX() + 
                ypart +
                //"&" + yparam + "=" + tilePoint.getY() +
                "&" + zparam + "=" + zoom;
        return url;
    }
    //private void setMapCenterInPixelsAtZoom(Point2D[] mapCenterInPixelsAtZoom) {
    //    this.mapCenterInPixelsAtZoom = mapCenterInPixelsAtZoom;
    //}

    public int getTileSize() {
        return tileSize;
    }

    public double getLongitudeDegreeWidthInPixels(int zoom) {
        return longitudeDegreeWidthInPixels[zoom];
    }

    public double getLongitudeRadianWidthInPixels(int zoom) {
        return longitudeRadianWidthInPixels[zoom];
    }

    public boolean isXr2l() {
        return xr2l;
    }

    public void setXr2l(boolean xr2l) {
        this.xr2l = xr2l;
    }

    public boolean isYt2b() {
        return yt2b;
    }

    public void setYt2b(boolean yt2b) {
        this.yt2b = yt2b;
    }
    
}
