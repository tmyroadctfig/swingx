/*
 * Tile.java
 *
 * Created on March 14, 2006, 4:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JavaBean;
import org.jdesktop.swingx.util.PaintUtils;

/**
 * 
 *
 * @author joshy
 */

public class Tile extends JavaBean {
    public enum Priority { High, Low }
    private Priority priority = Priority.High;
    
    private static final Logger LOG = Logger.getLogger(Tile.class.getName());
    static {
        LOG.setLevel(Level.OFF);
    }
    /**
     * Thread pool for loading the tiles
     */
    //private static BlockingQueue tileQueue = new LinkedBlockingQueue();
    private static BlockingQueue<Tile> tileQueue = new PriorityBlockingQueue<Tile>(5,
            new Comparator<Tile>() {
        public int compare(Tile o1, Tile o2) {
            if(o1.getPriority() == Priority.Low && o2.getPriority() == Priority.High) {
                return 1;
            }
            if(o1.getPriority() == Priority.High && o2.getPriority() == Priority.Low ) {
                return -1;
            }
            return 0;
            
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this;
        }
    });
    private static ExecutorService service = Executors.newFixedThreadPool(8, new ThreadFactory() {
        private int count = 0;

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "tile-pool-" + count++);
            t.setPriority(Thread.MIN_PRIORITY);
            t.setDaemon(true);
            return t;
        }
    });
    /**
     * If an error occurs while loading a tile, store the exception
     * here.
     */
    private Throwable error;
    /**
     * The url of the image to load for this tile
     */
    private String url;
    /**
     * A "dummy" tile is one that doesn't load any data from the server. Once a
     * dummy, always a dummy.
     */
    private boolean dummy = false;
    /**
     * Indicates that loading has succeeded. A PropertyChangeEvent will be fired
     * when the loading is completed
     */
    private boolean loaded = false;
    /**
     * The zoom level this tile is for
     */
    private int zoom;
    /**
     * The location of this tile in its world at this zoom level
     */
    private TilePoint location;
    /**
     * The image loaded for this Tile
     */
    private SoftReference<BufferedImage> image = new SoftReference<BufferedImage>(null);
    
    /**
     * Create a new Tile
     */
    public Tile(TilePoint location, int zoom) {
        loaded = false;
        dummy = true;
        this.location = location;
        this.zoom = zoom;
    }
    
    /**
     * Create a new Tile that loads its data from the given URL. The URL must
     * resolve to an image
     */
    public Tile(TilePoint location, int zoom, String url, Priority priority) {
        this.url = url;
        loaded = false;
        this.location = location;
        this.zoom = zoom;
        this.priority = priority;
        startLoading();
    }
    
    /**
     * @returns true if the Tile has been loaded
     */
    public synchronized boolean isLoaded() {
        return loaded;
    }
    
    /**
     * Toggles the loaded state, and fires the appropriate property change notification
     */
    private synchronized void setLoaded(boolean loaded) {
        boolean old = isLoaded();
        this.loaded = loaded;
        firePropertyChange("loaded", old, isLoaded());
    }
    
    /**
     * @returns the last error in a possible chain of errors that occured during
     * the loading of the tile
     */
    public Throwable getUnrecoverableError() {
        return error;
    }
    
    /**
     * @returns the Throwable tied to any error that may have ocurred while
     * loading the tile. This error may change several times if multiple
     * errors occur
     */
    public Throwable getLoadingError() {
        return error;
    }
    
    /**
     * @returns the Image associated with this Tile. This is a read only property
     *          This may return null at any time, however if this returns null,
     *          a load operation will automatically be started for it.
     */
    public BufferedImage getImage() {
        BufferedImage img = image.get();
        if (img == null) {
            setLoaded(false);
            startLoading();
        }
        return img;
    }
    
    /**
     * @returns true if this is a dummy tile, ie: one that doesn't actually load
     * any data from any server
     */
    public boolean isDummy() {
        return dummy;
    }
    
    /**
     * @return the location in the world at this zoom level that this tile should
     * be placed
     */
    public TilePoint getLocation() {
        return location;
    }
    
    /**
     * @return the zoom level that this tile belongs in
     */
    public int getZoom() {
        return zoom;
    }
    
    //////////////////JavaOne Hack///////////////////
    private PropertyChangeListener uniqueListener = null;
    /**
     * Addes a property change listener *only* if one wasn't already added
     */
    public void addUniquePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (uniqueListener != null && uniqueListener != listener) {
            removePropertyChangeListener(propertyName, uniqueListener);
        }
        if (uniqueListener != listener) {
            uniqueListener = listener;
            addPropertyChangeListener(propertyName, uniqueListener);
        }
    }
        
    /////////////////End JavaOne Hack/////////////////

    @SuppressWarnings("unchecked")
    private void startLoading() {
        try {
            tileQueue.put(this);
            service.submit(new TileRunner());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     */
    private void firePropertyChangeOnEDT(final String propertyName, final Object oldValue, final Object newValue) {
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    firePropertyChange(propertyName, oldValue, newValue);
                }
            });
        }
    }
    
    static class TileRunner implements Runnable {
        public void run() {
            /*
             * 3 strikes and you're out. Attempt to load the url. If it fails,
             * decrement the number of tries left and try again. Log failures.
             * If I run out of try s just get out. This way, if there is some
             * kind of serious failure, I can get out and let other tiles
             * try to load.
             */
            final Tile tile = tileQueue.remove();
            
            int trys = 3;
            while (!tile.isLoaded() && trys > 0) {
                try {
                    //Thread.sleep(1000);
                    final BufferedImage img;
                    URI uri = new URI(tile.url);
                    if (!LocalResponseCache.IS_CACHE_DISABLED) {
                        /*
                         * We're having cacheing issues, so for now we have a
                         * hardcoded cache below. This must be removed at some
                         * point prior to releasing the code on Java.net
                         */
                        File localFile = LocalResponseCache.getLocalFile(uri);
                        if (!localFile.exists()) {
                            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
                            connection.setUseCaches(false);
                            InputStream in = connection.getInputStream();
    
                            OutputStream out = new FileOutputStream(localFile);
                            byte[] b = new byte[8192];
                            int read = -1;
                            while ((read = in.read(b)) != -1) {
                                out.write(b, 0, read);
                            }

                            in.close();
                            out.close();
                            connection.disconnect();
                        } else {
                            //System.out.println("loading tile from cache: " + url);
                        }
                        img = PaintUtils.loadCompatibleImage(localFile.toURI().toURL());//ImageIO.read(localFile);
                    } else {
                        img = PaintUtils.loadCompatibleImage(uri.toURL());//ImageIO.read(new URL(tile.url));
                    }
                    //System.out.println("[IMAGE TYPE] " + img.getType());
                    //System.out.println("completed: " + tile.getPriority() + " " + tile.url);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            tile.image = new SoftReference<BufferedImage>(img);
                            tile.setLoaded(true);
                        }
                    });
                } catch (Exception e) {
                    LOG.log(Level.INFO, 
                            "Failed to load a tile at url: " + tile.url + ", retrying", e);
                    Object oldError = tile.error;
                    tile.error = e;
                    tile.firePropertyChangeOnEDT("loadingError", oldError, tile.error);
                    if (trys == 0) {
                        tile.firePropertyChangeOnEDT("unrecoverableError", null, tile.error);
                    } else {
                        trys--;
                    }
                }
            }
        }
    }

    public Priority getPriority() {
        return priority;
    }

    public synchronized void promote() {
        if(tileQueue.contains(this)) {
            try {
                tileQueue.remove(this);
                this.priority = Priority.High;
                tileQueue.put(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

