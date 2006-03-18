/*
 * Thumb.java
 *
 * Created on February 14, 2006, 10:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.multislider;


/**
 *
 * @author jm158417
 */
public class Thumb<E> {
    
    private float position;
    private E object;
    
    /** Creates a new instance of Thumb */
    public Thumb() {
    }

    public float getPosition() {
        return position;
    }

    public void setPosition(float position) {
        this.position = position;
    }

    public E getObject() {
        return object;
    }

    public void setObject(E object) {
        this.object = object;
    }
    
    
}
