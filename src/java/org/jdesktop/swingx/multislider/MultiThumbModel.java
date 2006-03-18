/*
 * MultiThumbDataModel.java
 *
 * Created on February 9, 2006, 11:13 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.multislider;


import java.util.List;

/**
 *
 * @author joshy
 */
public interface MultiThumbModel<E> extends Iterable<Thumb<E>> {
    
    public float getMinimumValue();
    public void setMinimumValue(float minimumValue);
    public float getMaximumValue();
    public void setMaximumValue(float maximumValue);
    
    public void addThumb(float value, E obj);
    public void insertThumb(float value, E obj, int index);
    public void removeThumb(int index);
    public int getThumbCount();
    public Thumb<E> getThumbAt(int index);
    public List<Thumb<E>> getSortedThumbs();
    
    public void addThumbDataListener(ThumbDataListener listener);
    public void removeThumbDataListener(ThumbDataListener listener);
}
