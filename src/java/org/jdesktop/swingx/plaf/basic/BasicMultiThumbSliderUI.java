/*
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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

package org.jdesktop.swingx.plaf.basic;

import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import org.jdesktop.swingx.JXMultiThumbSlider;
import org.jdesktop.swingx.multislider.ThumbRenderer;
import org.jdesktop.swingx.multislider.TrackRenderer;
import org.jdesktop.swingx.plaf.MultiThumbSliderUI;

/**
 *
 * @author Joshua Marinacci
 */
public class BasicMultiThumbSliderUI extends MultiThumbSliderUI {
    
    protected JXMultiThumbSlider slider;
    
    public static ComponentUI createUI(JComponent c) {
        return new BasicMultiThumbSliderUI();
    }
    
    public void installUI(JComponent c) {
        slider = (JXMultiThumbSlider)c;
	slider.setThumbRenderer(new ThumbRenderer() {
            public void paintThumb(Graphics2D g, JXMultiThumbSlider.ThumbComp thumb, int index, boolean selected) {
                g.setColor(Color.green);
                g.drawLine(0,0,thumb.getWidth(),thumb.getHeight());
                g.drawLine(0,thumb.getHeight(),thumb.getWidth(),0);
                g.drawRect(0,0,thumb.getWidth()-1,thumb.getHeight()-1);
            }
        });
        
        slider.setTrackRenderer(new TrackRenderer() {
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
    public void uninstallUI(JComponent c) {
        slider = null;
    }
    
}
