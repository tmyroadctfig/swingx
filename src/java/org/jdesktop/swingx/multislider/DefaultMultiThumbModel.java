/*
 * DefaultMultiThumbModel.java
 *
 * Created on February 9, 2006, 11:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.multislider;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author joshy
 */
public class DefaultMultiThumbModel<E> extends AbstractMultiThumbModel<E> implements MultiThumbModel<E> {
    
    protected List<Thumb<E>>thumbs = new ArrayList<Thumb<E>>();
    
    /** Creates a new instance of DefaultMultiThumbModel */
    public DefaultMultiThumbModel() {
	setMinimumValue(0.0f);
	setMaximumValue(1.0f);
    }
    
    public void addThumb(float value, E obj) {
	Thumb<E> thumb = new Thumb<E>();
	thumb.setPosition(value);
	thumb.setObject(obj);
	thumbs.add(thumb);
	ThumbDataEvent evt = new ThumbDataEvent(this,-1,thumbs.size()-1,thumb);
	for(ThumbDataListener tdl : thumbDataListeners) {
	    tdl.thumbAdded(evt);
	}
    }

    public void insertThumb(float value, E obj, int index) {
	Thumb thumb = new Thumb();
	thumb.setPosition(value);
	thumb.setObject(obj);
	thumbs.add(index,thumb);
	ThumbDataEvent evt = new ThumbDataEvent(this,-1,index,thumb);
	for(ThumbDataListener tdl : thumbDataListeners) {
	    tdl.thumbAdded(evt);
	}
	/*
	for(ThumbChangeListener tcl : thumbChangeListeners) {
	    tcl.thumbAdded(thumb,index);
	}
	 */
    }

    public void removeThumb(int index) {
	Thumb thumb = thumbs.remove(index);
	ThumbDataEvent evt = new ThumbDataEvent(this,-1,index,thumb);
	for(ThumbDataListener tdl : thumbDataListeners) {
	    tdl.thumbRemoved(evt);
	}
	/*
	for(ThumbChangeListener tcl : thumbChangeListeners) {
	    tcl.thumbRemoved(thumb, index);
	}
	 */
    }

    public int getThumbCount() {
	return thumbs.size();
    }

    public Thumb getThumbAt(int index) {
	return thumbs.get(index);
    }

    public List<Thumb<E>> getSortedThumbs() {
	List<Thumb<E>> list = new ArrayList<Thumb<E>>();
	list.addAll(thumbs);
        Collections.sort(list, new Comparator<Thumb<E>>() {
	    public int compare(Thumb<E> o1, Thumb<E> o2) {
		float f1 = o1.getPosition();
		float f2 = o2.getPosition();
		if(f1<f2) {
		    return -1;
		}
		if(f1>f2) {
		    return 1;
		}
		return 0;
	    }
	});
	return list;
    }

    public Iterator<Thumb<E>> iterator() {
	return thumbs.iterator();
    }
}
