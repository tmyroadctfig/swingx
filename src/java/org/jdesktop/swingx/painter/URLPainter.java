/*
 * URLPainter.java
 *
 * Created on August 2, 2006, 11:39 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */ 

package org.jdesktop.swingx.painter;

import java.awt.Graphics2D;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.jdesktop.swingx.editors.PainterUtil;

/**
 *
 * @author joshy
 */
public class URLPainter extends CompoundPainter {
    URL url;
    /**
     * Creates a new instance of URLPainter
     */
    public URLPainter() {
        this.url = null;
    }
    
    public URLPainter(URL url) {
        this.url = url;
    }
    
    public URLPainter(File file) {
        try {
            this.url = file.toURI().toURL();
        } catch (MalformedURLException exception) {
            p(exception);
            this.url = null;
        }
    }
        
    public URLPainter(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException ex) {
            p(ex);
            this.url = null;
        }
    }
    
    public URLPainter(Class<?> baseClass, String resource) {
        url = baseClass.getResource(resource);
    }
    
    public void setURL(URL url) {
        URL old = this.url;
        this.url = url;
        firePropertyChange("file", old, this.url);
    }
    
    public URL getURL() {
        return this.url;
    }
    
    private boolean loaded = false;
    
    private void load() {
        try {
            System.out.println("loading");
            Painter painter = PainterUtil.loadPainter(url);
            this.setPainters(painter);
            loaded = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void doPaint(Graphics2D g, Object component, int width, int height) {
        System.out.println("creating new url painter");
        if(!loaded) {
            load();
        }
        super.doPaint(g, component, width, height);
    }
    
    
    private static void p(String str) {
        System.out.println(str);
    }
    private static void p(Throwable thr) {
        System.out.println(thr.getMessage());
        thr.printStackTrace();
    }
}
