/*
 * MapOverlay.java
 *
 * Created on April 1, 2006, 8:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

import javax.swing.event.MouseInputListener;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.painter.Painter;

/**
 * MapOverlays are painters that also receive mouse event notifications
 *
 * @author 
 */
public interface MapOverlay<T extends JXMapViewer> extends Painter<T>, MouseInputListener {
}
