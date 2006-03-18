/*
 * AbstractMultiThumbModel.java
 *
 * Created on February 13, 2006, 2:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.multislider;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jm158417
 */
public abstract class AbstractMultiThumbModel<E> implements MultiThumbModel<E> {
    
    /** Creates a new instance of AbstractMultiThumbModel */
    public AbstractMultiThumbModel() {
    }
    
    protected float maximumValue = 1.0f;
    
    protected float minimumValue = 0.0f;
    
    public float getMaximumValue()	{
	return maximumValue;
    }
    
    public float getMinimumValue()	{
	return minimumValue;
    }
    
    public void setMaximumValue(float maximumValue)	{
	this.maximumValue = maximumValue;
    }
    
    public void setMinimumValue(float minimumValue)	{
	this.minimumValue = minimumValue;
    }
    
    protected List<ThumbDataListener> thumbDataListeners = new ArrayList<ThumbDataListener>();
    public void addThumbDataListener(ThumbDataListener listener) {
	thumbDataListeners.add(listener);
    }
    public void removeThumbDataListener(ThumbDataListener listener) {
	thumbDataListeners.remove(listener);
    }
    
}
