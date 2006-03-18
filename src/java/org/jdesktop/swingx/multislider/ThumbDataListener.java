/*
 * ThumbDataListener.java
 *
 * Created on February 22, 2006, 1:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.multislider;

/**
 *
 * @author jm158417
 */
public interface ThumbDataListener {
    
    public void valueChanged(ThumbDataEvent e);
    public void positionChanged(ThumbDataEvent e);
    public void thumbAdded(ThumbDataEvent e);
    public void thumbRemoved(ThumbDataEvent e);
    
}
