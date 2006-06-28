/*
 * MapViewer.java
 *
 * Created on March 14, 2006, 2:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.beans.DesignMode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.MapOverlay;
import org.jdesktop.swingx.mapviewer.Tile;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.TilePoint;
import org.jdesktop.swingx.mapviewer.TileProviderInfo;
import org.jdesktop.swingx.mapviewer.empty.EmptyTileFactory;
import org.w3c.dom.Document;

/**
 * A tile oriented map component that can easily be used with tile sources
 * such as Google maps or Yahoo maps.
 *
 * @author joshy
 */
public class JXMapViewer extends JXPanel implements DesignMode {
    /**
     * The zoom level. Generally a value between 1 and 15 (TODO Is this true for
     * all the mapping worlds? What does this mean if some mapping system doesn't
     * support the zoom level?
     */
    private int zoom = 6;
   
    /**
     * The position, in "map" coordinates of the center point. This is defined
     * as the distance from the top and left edges of the "map"
     * in pixels. Dragging the map component will change the center position.
     * Zooming in/out will cause the center to be recalculated so as to remain
     * in the center of the new "map".
     */
    private Point2D center = new Point2D.Double(0,0);
    
    /**
     * Indicates whether or not to draw the borders between tiles. Defaults to
     * false. 
     * 
     * TODO Generally not very nice looking, very much a product of testing
     * Consider whether this should really be a property or not.
     */
    private boolean drawTileBorders = false;
    /**
     * Factory used by this component to grab the tiles necessary for painting
     * the map.
     */
    private TileFactory factory;
    /**
     * The position in latitude/longitude of the "address" being mapped. This
     * is a special coordinate that, when moved, will cause the map to be moved
     * as well. It is separate from "center" in that "center" tracks the current
     * center (in pixels) of the viewport whereas this will not change when panning
     * or zooming. Whenever the addressLocation is changed, however, the map will
     * be repositioned.
     */
    private GeoPosition addressLocation;
    /**
     * Specifies whether panning is enabled. Panning is being able to click and
     * drag the map around to cause it to move
     */
    private boolean panEnabled = true;
    /**
     * Specifies whether zooming is enabled (the mouse wheel, for example, zooms)
     */
    private boolean zoomEnabled = true;
    /**
     * Indicates whether the component should recenter the map when the
     * "middle" mouse button is pressed
     */
    private boolean recenterOnClickEnabled = true;
    /**
     * The overlay to delegate to for painting the "foreground" of the
     * map component. This would include painting waypoints, day/night, etc.
     * Also receives mouse events.
     */
    private MapOverlay<JXMapViewer> mapOverlay;
    
    private boolean designTime;
    
    private float zoomScale = 1;
    //private int zoomDirection = 0;
    
    /**
     * Create a new JXMapViewer. By default it will use the EmptyTileFactory
     */
    public JXMapViewer() {
        factory = new EmptyTileFactory();
        //setFactory(new GoogleTileFactory());
        MouseInputListener mia = new PanMouseInputListener();
        setRecenterOnClickEnabled(false);
        this.addMouseListener(mia);
        this.addMouseMotionListener(mia);
        this.addMouseWheelListener(new ZoomMouseWheelListener());
        this.addKeyListener(new PanKeyListener());

        //setAddressLocation(new GeoPosition(37.392137,-121.950431));
    }

    public void setDesignTime(boolean b) {
        this.designTime = b;
    }
    
    public boolean isDesignTime() {
        return designTime;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (isOpaque() || isDesignTime()) {
            g.setColor(getBackground());
            g.fillRect(0,0,getWidth(),getHeight());
        }

        if (isDesignTime()) {
            
        } else {
            int zoom = getZoom();
            Rectangle viewportBounds = getViewportBounds();
            drawMapTiles(g, zoom, viewportBounds);
            drawOverlays(zoom, g, viewportBounds);
        }
    }

    /** @noinspection UNUSED_SYMBOL*/
    private void drawOverlays(final int zoom, final Graphics g, final Rectangle viewportBounds) {

        //paint the crosshairs
//        GeoPosition addrPosition = getAddressLocation();
//        if (addrPosition != null) {
//            int as = 10;
//            Point2D addr = GoogleUtil.getBitmapCoordinate(addrPosition, zoom);
//            int ax = (int)(addr.getX() - viewportBounds.x);
//            int ay = (int)(addr.getY() - viewportBounds.y);
//            g.setColor(Color.black);
//            g.drawLine(ax,ay-as,ax,ay+as);
//            g.drawLine(ax-as,ay,ax+as,ay);
//            g.setColor(Color.red);
//            g.drawOval(ax-as,ay-as,as*2,as*2);
//        }

        if (mapOverlay != null) {
            mapOverlay.paint((Graphics2D)g, this);
        }
    }

    public void drawMapTiles(final Graphics g, final int zoom, Rectangle viewportBounds) {
        int size = getFactory().getTileSize();
        Dimension mapSize = getFactory().getMapSize(zoom);
        //Insets insets = getInsets();


        //calculate the "visible" viewport area in tiles
        int numWide = viewportBounds.width/size+1;
        int numHigh = viewportBounds.height/size+1;
        TilePoint topLeftTile = getFactory().getTileCoordinate(new Point2D.Double(viewportBounds.x, viewportBounds.y));

        //fetch the tiles from the factory and store them in the tiles cache
        //attach the tileLoadListener
        for(int x=0; x<=numWide; x++) {
            for(int y=0; y<=numHigh; y++) {
                TilePoint point = new TilePoint(x + topLeftTile.getX(), y + topLeftTile.getY());
                //only proceed if the specified tile point lies within the area being painted
                if (g.getClipBounds().intersects(new Rectangle(point.getX() * size - viewportBounds.x,
                        point.getY() * size - viewportBounds.y, size, size))) {
                    Tile tile =  getFactory().getTile(point, zoom);
                    tile.addUniquePropertyChangeListener("loaded", tileLoadListener); //this is a filthy hack
                    int ox = ((point.getX() * getFactory().getTileSize()) - viewportBounds.x);
                    int oy = ((point.getY() * getFactory().getTileSize()) - viewportBounds.y);

                    //if the tile is off the map to the north/south, then just don't paint anything
                    if (point.getY() < 0 || point.getY() >= mapSize.getHeight()) {
                        if (isOpaque()) {
                            g.setColor(getBackground());
                            g.fillRect(ox,oy,size,size);
                        }
                    } else if(tile.isLoaded()) {
                        g.drawImage(tile.getImage(), ox, oy, null);
                    } else {
                        int imageX = (getFactory().getTileSize() - getLoadingImage().getWidth(null)) / 2;
                        int imageY = (getFactory().getTileSize() - getLoadingImage().getHeight(null)) / 2;
                        g.drawImage(getLoadingImage(), ox + imageX, oy + imageY, null);
                    }
                    if(isDrawTileBorders()) {
                        g.setColor(Color.black);
                        g.drawRect(ox,oy,size,size);
                    }
                }
            }
        }
    }
    
    private Image loadingImage;
    
    @SuppressWarnings("unchecked")
    public void setMapOverlay(MapOverlay<? extends JXMapViewer> overlay) {
        MapOverlay<? extends JXMapViewer> old = getMapOverlay();
        this.mapOverlay = (MapOverlay<JXMapViewer>) overlay;
        firePropertyChange("mapOverlay", old, getMapOverlay());
        repaint();
    }
    
    public MapOverlay<? extends JXMapViewer> getMapOverlay() {
        return mapOverlay;
    }
    
    /**
     * @return the bounds in <em>pixels</em> of the "view" of this map
     */
    public Rectangle getViewportBounds() {
        Insets insets = getInsets();
        //calculate the "visible" viewport area in pixels
        int viewportWidth = getWidth() - insets.left - insets.right;
        int viewportHeight = getHeight() - insets.top - insets.bottom;
        double viewportX = (center.getX() - viewportWidth/2);
        double viewportY = (center.getY() - viewportHeight/2);
        return new Rectangle((int)viewportX, (int)viewportY, viewportWidth, viewportHeight);
    }

    public static GeoPosition getPositionForAddress(String street, String city, String state) throws IOException {
        try {
            URL load = new URL("http://api.local.yahoo.com/MapsService/V1/geocode?"+
                               "appid=joshy688"+
                               "&street="+street.replace(' ','+')+
                               "&city="+city.replace(' ','+')+
                               "&state="+state.replace(' ','+'));
            //System.out.println("using address: " + load);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(load.openConnection().getInputStream());
            XPath xpath = XPathFactory.newInstance().newXPath();
            //NodeList str = (NodeList)xpath.evaluate("//Result",doc,XPathConstants.NODESET);
            Double lat = (Double)xpath.evaluate("//Result/Latitude/text()",doc,XPathConstants.NUMBER);
            Double lon = (Double)xpath.evaluate("//Result/Longitude/text()",doc,XPathConstants.NUMBER);
            //System.out.println("got address at: " + lat + " " + lon);
            return new GeoPosition(lat,lon);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Failed to retrieve location information from the internet: " + e.toString());
        }
    }
    
    public void setRecenterOnClickEnabled(boolean b) {
        boolean old = isRecenterOnClickEnabled();
        recenterOnClickEnabled = b;
        firePropertyChange("recenterOnClickEnabled", old, isRecenterOnClickEnabled());
    }
    
    public boolean isRecenterOnClickEnabled() {
        return recenterOnClickEnabled;
    }
    
    public void setZoom(int zoom) {
        
        TileProviderInfo info = getFactory().getInfo();
        if(info != null &&
                zoom >= info.getMinimumZoomLevel() &&
                zoom <= info.getMaximumZoomLevel() &&
                zoom != this.zoom) {
        //if(zoom >= 0 && zoom <= 15 && zoom != this.zoom) {
            int oldzoom = this.zoom;
            Point2D oldCenter = center;
            Dimension oldMapSize = getFactory().getMapSize(oldzoom);
            this.zoom = zoom;
            this.firePropertyChange("zoom", oldzoom, zoom);
            
            Dimension mapSize = getFactory().getMapSize(zoom);
            
            center = new Point2D.Double(
                    oldCenter.getX() * (mapSize.getWidth() / oldMapSize.getWidth()),
                    oldCenter.getY() * (mapSize.getHeight() / oldMapSize.getHeight()));
            repaint();
        }
    }
    
    
    public int getZoom() {
        return this.zoom;
    }
    
    public GeoPosition getAddressLocation() {
        return addressLocation;
    }
    
    public void setAddressLocation(GeoPosition addressLocation) {
        GeoPosition old = getAddressLocation();
        this.addressLocation = addressLocation;
        center = getTileFactory().getBitmapCoordinate(addressLocation, getZoom());
        
        firePropertyChange("addressLocation", old, getAddressLocation());
        repaint();
    }
    
    public boolean isDrawTileBorders() {
        return drawTileBorders;
    }
    
    public void setDrawTileBorders(boolean drawTileBorders) {
        boolean old = isDrawTileBorders();
        this.drawTileBorders = drawTileBorders;
        firePropertyChange("drawTileBorders", old, isDrawTileBorders());
        repaint();
    }
    
    public boolean isPanEnabled() {
        return panEnabled;
    }
    
    public void setPanEnabled(boolean panEnabled) {
        boolean old = isPanEnabled();
        this.panEnabled = panEnabled;
        firePropertyChange("panEnabled", old, isPanEnabled());
    }
        
    public boolean isZoomEnabled() {
        return zoomEnabled;
    }
    
    public void setZoomEnabled(boolean zoomEnabled) {
        boolean old = isZoomEnabled();
        this.zoomEnabled = zoomEnabled;
        firePropertyChange("zoomEnabled", old, isZoomEnabled());
    }
    
    
    
        
    public TileFactory getTileFactory() {
        return getFactory();
    }

    public void setCenterPosition(GeoPosition geoPosition) {
        this.center = getTileFactory().getBitmapCoordinate(geoPosition, zoom);
        //this.center = GoogleUtil.getBitmapCoordinate(geoPosition, zoom);
        repaint();
    }
    
    public GeoPosition getCenterPosition() {
        //return GoogleUtil.getPosition(center, zoom);
        return getTileFactory().pixelToGeo(center, zoom);
    }
    
    private TileLoadListener tileLoadListener = new TileLoadListener();
    private final class TileLoadListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if ("loaded".equals(evt.getPropertyName()) &&
                    Boolean.TRUE.equals(evt.getNewValue())) {
                Tile t = (Tile)evt.getSource();
                if (t.getZoom() == getZoom()) {
                    Rectangle viewportBounds = getViewportBounds();
                    TilePoint tilePoint = t.getLocation();
                    Point point = new Point(tilePoint.getX() * getFactory().getTileSize(), tilePoint.getY() * getFactory().getTileSize());
                    Rectangle tileRect = new Rectangle(point, new Dimension(getFactory().getTileSize(), getFactory().getTileSize()));
                    if (viewportBounds.intersects(tileRect)) {
                        //convert tileRect from world space to viewport space
                        repaint(new Rectangle(
                                tileRect.x - viewportBounds.x,
                                tileRect.y - viewportBounds.y,
                                tileRect.width,
                                tileRect.height
                                ));
                    }
                }
            }
        }
    }

    public float getZoomScale() {
        return zoomScale;
    }

    public void setZoomScale(float zoomScale) {
        this.zoomScale = zoomScale;
    }

    private class PanKeyListener extends KeyAdapter {
        private static final int OFFSET = 10;

        @Override
        public void keyPressed(KeyEvent e) {
            int delta_x = 0;
            int delta_y = 0;

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    delta_x = -OFFSET;
                    break;
                case KeyEvent.VK_RIGHT:
                    delta_x = OFFSET;
                    break;
                case KeyEvent.VK_UP:
                    delta_y = -OFFSET;
                    break;
                case KeyEvent.VK_DOWN:
                    delta_y = OFFSET;
                    break;
            }

            if (delta_x != 0 || delta_y != 0) {
                Rectangle bounds = getViewportBounds();
                double x = bounds.getCenterX() + delta_x;
                double y = bounds.getCenterY() + delta_y;
                center = new Point2D.Double(x, y);
                repaint();
            }
        }
    }

    private class PanMouseInputListener implements MouseInputListener {
        Point prev;

        public void mousePressed(MouseEvent evt) {
            //if the middle mouse button is clicked, recenter the view
            if (isRecenterOnClickEnabled() && (SwingUtilities.isMiddleMouseButton(evt) ||
                (SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() == 2))) {
                recenterMap(evt);
            } else {
                //otherwise, just remember this point (for panning)
                prev = evt.getPoint();
            }

            if (mapOverlay != null) {
                mapOverlay.mousePressed(evt);
            }
        }

        private void recenterMap(MouseEvent evt) {
            Rectangle bounds = getViewportBounds();
            double x = bounds.getX() + evt.getX();
            double y = bounds.getY() + evt.getY();
            center = new Point2D.Double(x, y);
            repaint();
        }

        public void mouseDragged(MouseEvent evt) {
            if(isPanEnabled()) {
                Point current = evt.getPoint();
                double x = center.getX() - (current.x - prev.x);
                double y = center.getY() - (current.y - prev.y);

                if (y < 0) {
                    y = 0;
                }

                int maxHeight = (int)(getFactory().getMapSize(getZoom()).getHeight()*getFactory().getTileSize());
                if (y > maxHeight) {
                    y = maxHeight;
                }

                prev = current;
                center = new Point2D.Double(x, y);
                repaint();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
            if (mapOverlay != null) {
                mapOverlay.mouseDragged(evt);
            }
        }

        public void mouseReleased(MouseEvent evt) {
            prev = null;
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            if (mapOverlay != null) {
                mapOverlay.mouseReleased(evt);
            }
        }

        public void mouseMoved(MouseEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    requestFocusInWindow();
                }
            });
            if (mapOverlay != null) {
                mapOverlay.mouseMoved(e);
            }
        }

        public void mouseExited(MouseEvent e) {
            if (mapOverlay != null) {
                mapOverlay.mouseExited(e);
            }
        }

        public void mouseEntered(MouseEvent e) {
            if (mapOverlay != null) {
                mapOverlay.mouseEntered(e);
            }
        }

        public void mouseClicked(MouseEvent e) {
            if (mapOverlay != null) {
                mapOverlay.mouseClicked(e);
            }
        }
    }

    private class ZoomMouseWheelListener implements MouseWheelListener {
        public void mouseWheelMoved(MouseWheelEvent e) {
            if(isZoomEnabled()) {
                setZoom(getZoom()+e.getWheelRotation());
            }
        }
    }

    public TileFactory getFactory() {
        return factory;
    }

    public void setFactory(TileFactory factory) {
        this.factory = factory;
    }

    public Image getLoadingImage() {
        return loadingImage;
    }

    public void setLoadingImage(Image loadingImage) {
        this.loadingImage = loadingImage;
    }
}
