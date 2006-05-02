/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jdesktop.swingx;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;
import org.jdesktop.swingx.multislider.*;

/**
 * <p>A slider which can have multiple control points or <i>Thumbs</i></p>
 * <p>The thumbs each represent a value between the minimum and maximum values
 * of the slider.  Thumbs can pass each other when being dragged.  Thumbs have
 * no default visual representation. To customize the look of the thumbs and the
 * track behind the thumbs you must provide a ThumbRenderer and a TrackRenderer 
 * implementation. To listen for changes to the thumbs you must provide an 
 * implemention of ThumbDataListener.
 * 
 * @author joshy
 */
public class JXMultiThumbSlider<E> extends JComponent {
    
    /** Creates a new instance of JMultiThumbSlider */
    public JXMultiThumbSlider() {
        thumbs = new ArrayList();
        setLayout(null);
        
	tdl = new ThumbHandler();
        
        setModel(new DefaultMultiThumbModel<E>());
        MultiThumbMouseListener mia = new MultiThumbMouseListener(this);
        addMouseListener(mia);
        addMouseMotionListener(mia);
        
        Dimension dim = new Dimension(20,20);
        setMaximumSize(dim);
        setPreferredSize(dim);
        setSize(dim);
        dim = new Dimension(10,10);
        setMinimumSize(dim);
	
	this.setThumbRenderer(new ThumbRenderer() {
            public void paintThumb(Graphics2D g, JXMultiThumbSlider.ThumbComp thumb, int index, boolean selected) {
                g.setColor(Color.white);
                g.drawLine(0,0,thumb.getWidth(),thumb.getHeight());
                g.drawLine(0,thumb.getHeight(),thumb.getWidth(),0);
                g.drawRect(0,0,thumb.getWidth()-1,thumb.getHeight()-1);
            }
        });
        
        this.setTrackRenderer(new TrackRenderer() {
            public void paintTrack(Graphics2D g, JXMultiThumbSlider slider) {
                g.setColor(Color.black);
                g.fillRect(0,0,slider.getWidth(),slider.getHeight());
                g.setColor(Color.white);
                g.drawLine(0,0,slider.getWidth(),slider.getHeight());
                g.drawLine(0,slider.getHeight(),slider.getWidth(),0);
                g.drawRect(0,0,slider.getWidth()-1,slider.getHeight()-1);
            }
        });
    }
    
    
    private ThumbDataListener tdl;
    public List<ThumbComp> thumbs;
    
    private ThumbRenderer thumbRenderer;
    
    private TrackRenderer trackRenderer;
    
    private MultiThumbModel<E> model;

    List<ThumbListener> listeners = new ArrayList();
        
    ThumbComp selected;
    
    protected void paintComponent(Graphics g) {
        if(isVisible()) {
            if(trackRenderer != null) {
                trackRenderer.paintTrack((Graphics2D)g,this);
            } else {
                paintRange((Graphics2D)g);
            }
        }
    }

    private void paintRange(Graphics2D g) {
        g.setColor(Color.blue);
        g.fillRect(0,0,getWidth(),getHeight());
    }    
    
    private float getThumbValue(int thumbIndex) {
        return getModel().getThumbAt(thumbIndex).getPosition();
    }
    
    private float getThumbValue(ThumbComp thumb) {
        return getThumbValue(thumbs.indexOf(thumb));
    }
    
    private int getThumbIndex(ThumbComp thumb) {
        return thumbs.indexOf(thumb);
    }
    
    private void clipThumbPosition(ThumbComp thumb) {
        if(getThumbValue(thumb) < getModel().getMinimumValue()) {
	    getModel().getThumbAt(getThumbIndex(thumb)).setPosition(
		getModel().getMinimumValue());
        }
        if(getThumbValue(thumb) > getModel().getMaximumValue()) {
	    getModel().getThumbAt(getThumbIndex(thumb)).setPosition(
	    getModel().getMaximumValue());
        }
    }
        
    public ThumbRenderer getThumbRenderer() {
        return thumbRenderer;
    }

    public void setThumbRenderer(ThumbRenderer thumbRenderer) {
        this.thumbRenderer = thumbRenderer;
    }

    public TrackRenderer getTrackRenderer() {
        return trackRenderer;
    }

    public void setTrackRenderer(TrackRenderer trackRenderer) {
        this.trackRenderer = trackRenderer;
    }
    
    private void setThumbPositionByX(ThumbComp selected) {    
        float range = getModel().getMaximumValue()-getModel().getMinimumValue();
        int x = selected.getX();
        // adjust to the center of the thumb
        x += selected.getWidth()/2;
        // adjust for the leading space on the slider
        x -= selected.getWidth()/2;
        
        int w = getWidth();
        // adjust for the leading and trailing space on the slider
        w -= selected.getWidth();
        float delta = ((float)x)/((float)w);
        int thumb_index = getThumbIndex(selected);
        float value = delta*range;
	getModel().getThumbAt(thumb_index).setPosition(value);
        //getModel().setPositionAt(thumb_index,value);
        clipThumbPosition(selected);
    }
    
    private void setThumbXByPosition(ThumbComp thumb, float pos) {
        float tu = pos;
        float lp = getWidth()-thumb.getWidth();
        float lu = getModel().getMaximumValue()-getModel().getMinimumValue();
        float tp = (tu*lp)/lu;
        thumb.setLocation((int)tp-thumb.getWidth()/2 + thumb.getWidth()/2, thumb.getY());
    }
    
    public void recalc() {
        for(ThumbComp th : thumbs) {
            setThumbXByPosition(th,getModel().getThumbAt(getThumbIndex(th)).getPosition());
	    //getPositionAt(getThumbIndex(th)));
        }
    }
    
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x,y,w,h);
        recalc();
    }


    public ThumbComp getSelectedThumb() {
        return selected;
    }
    
    public int getSelectedIndex() {
        return getThumbIndex(selected);
    }
        
    public MultiThumbModel<E> getModel() {
        return model;
    }

    public void setModel(MultiThumbModel<E> model) {
	if(this.model != null) {
	    this.model.removeThumbDataListener(tdl);
	}
        this.model = model;
	this.model.addThumbDataListener(tdl);	
    }
    
    public void addMultiThumbListener(ThumbListener listener) {
        listeners.add(listener);
    }

   
    class MultiThumbMouseListener extends MouseInputAdapter {
        private JXMultiThumbSlider slider;
        
        public MultiThumbMouseListener(JXMultiThumbSlider slider) {
            this.slider = slider;
        }

        public void mousePressed(MouseEvent evt) {
            ThumbComp handle = findHandle(evt);
            if(handle != null) {
                selected = handle;
                selected.setSelected(true);
                int thumb_index = getThumbIndex(selected);
                for(ThumbListener tl : listeners) {
                    tl.thumbSelected(thumb_index);
                }
                repaint();
            } else {
                selected = null;
                for(ThumbListener tl : listeners) {
                    tl.thumbSelected(-1);
                }
                repaint();
            }
	    for(ThumbListener tl : listeners) {
		tl.mousePressed(evt);
	    }
        }

        public void mouseReleased(MouseEvent evt) {
            if(selected != null) {
                selected.setSelected(false);
            }
        }
	    
        public void mouseDragged(MouseEvent evt) {
            if(selected != null) {
                int nx = (int)evt.getPoint().getX()- selected.getWidth()/2;
                if(nx < 0) {
                    nx = 0;
                }
                if(nx > getWidth()-selected.getWidth()) {
                    nx = getWidth()-selected.getWidth();
                }
                selected.setLocation(nx,(int)selected.getLocation().getY());
                setThumbPositionByX(selected);
                int thumb_index = getThumbIndex(selected);
                //System.out.println("still dragging: " + thumb_index);
                for(ThumbListener mtl : listeners) {
                    mtl.thumbMoved(thumb_index,getModel().getThumbAt(thumb_index).getPosition());
		    //getPositionAt(thumb_index));
                }
                repaint();
            }
        }

        
        private ThumbComp findHandle(MouseEvent evt) {
            for(ThumbComp hand : thumbs) {
                Point p2 = new Point();
                p2.setLocation(evt.getPoint().getX() - hand.getX(),
                    evt.getPoint().getY() - hand.getY());
                if(hand.contains(p2)) {
                    return hand;
                }
            }
            return null;
        }
    }

    
    
    
    public interface ThumbListener {
        public void thumbMoved(int thumb, float pos);
        public void thumbSelected(int thumb);
	public void mousePressed(MouseEvent evt);
    }
    
    public interface ThumbRenderer {
        public void paintThumb(Graphics2D g, JXMultiThumbSlider.ThumbComp thumb, int index, boolean selected);
    }
    
    public interface TrackRenderer {
        public void paintTrack(Graphics2D g, JXMultiThumbSlider slider);
    }
    
    
    
    
    public class ThumbComp extends JComponent {
        
        private JXMultiThumbSlider slider;
        
        public ThumbComp(JXMultiThumbSlider slider) {
            this.slider = slider;
            Dimension dim = new Dimension(11,22);
            setSize(dim);
            setMinimumSize(dim);
            setPreferredSize(dim);
            setMaximumSize(dim);
            setBackground(Color.white);
        }
        
        public void paintComponent(Graphics g) {
            if(slider.getThumbRenderer() != null) {
                slider.getThumbRenderer().paintThumb((Graphics2D)g,this,
                        slider.getThumbIndex(this),isSelected());
            } else {
                g.setColor(getBackground());
                g.fillRect(0,0,getWidth(),getHeight());
                if(isSelected()) {
                    g.setColor(Color.black);
                    g.drawRect(0,0,getWidth()-1,getHeight()-1);
                }
            }
        }
        
        private boolean selected;

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    private class ThumbHandler implements ThumbDataListener {

        public void positionChanged(ThumbDataEvent e) {
            System.out.println("position changed");
        }

        public void thumbAdded(ThumbDataEvent evt) {
            ThumbComp thumb = new ThumbComp(JXMultiThumbSlider.this);
            thumb.setLocation(0, 0);
            add(thumb);
            thumbs.add(evt.getIndex(), thumb);
            clipThumbPosition(thumb);
            setThumbXByPosition(thumb, evt.getThumb().getPosition());
            repaint();
        }

        public void thumbRemoved(ThumbDataEvent evt) {
            ThumbComp thumb = thumbs.get(evt.getIndex());
            remove(thumb);
            thumbs.remove(thumb);
            repaint();
        }

        public void valueChanged(ThumbDataEvent e) {
            System.out.println("value changed");
        }
    }


}
