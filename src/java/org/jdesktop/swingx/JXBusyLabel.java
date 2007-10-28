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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.Timer;
import org.jdesktop.swingx.painter.BusyPainter;
import org.jdesktop.swingx.painter.PainterIcon;

/**
 * <p>A simple circular animation, useful for denoting an action is taking
 * place that may take an unknown length of time to complete. Similar to an
 * indeterminant JProgressBar, but with a different look.</p>
 *
 * <p>For example:
 * <pre><code>
 *     JXFrame frame = new JXFrame("test", true);
 *     JXBusyLabel label = new JXBusyLabel();
 *     frame.add(label);
 *     //...
 *     label.setBusy(true);
 * </code></pre></p>
 *
 * @author rbair
 * @author joshy
 */
public class JXBusyLabel extends JLabel {
    private BusyPainter busyPainter;
    private Timer busy;
    private boolean running;
    
    /** Creates a new instance of JXBusyLabel */
    public JXBusyLabel() {
        busyPainter = new BusyPainter();
        busyPainter.setBaseColor(Color.LIGHT_GRAY);
        busyPainter.setHighlightColor(getForeground());
        Dimension dim = new Dimension(26,26);
        PainterIcon icon = new PainterIcon(dim);
        icon.setPainter(busyPainter);
        this.setIcon(icon);
    }
    
    /**
     * <p>Gets whether this <code>JXBusyLabel</code> is busy. If busy, then
     * the <code>JXBusyLabel</code> instance will indicate that it is busy,
     * generally by animating some state.</p>
     * 
     * @return true if this instance is busy
     */
    public boolean isBusy() {
        return busy != null;
    }

    /**
     * <p>Sets whether this <code>JXBusyLabel</code> instance should consider
     * itself busy. A busy component may indicate that it is busy via animation,
     * or some other means.</p>
     *
     * @param busy whether this <code>JXBusyLabel</code> instance should
     *        consider itself busy
     */
    public void setBusy(boolean busy) {
        boolean old = isBusy();
        if (!old && busy) {
        	running = true;
            startAnimation();
            firePropertyChange("busy", old, isBusy());
        } else if (old && !busy) {
        	running = false;
            stopAnimation();
            firePropertyChange("busy", old, isBusy());
        }
    }
    
    private void startAnimation() {
        if (!running || getParent() == null) {
        	return;
        }
        if(busy != null) {
            stopAnimation();
        }
        busy = new Timer(100, new ActionListener() {
            int frame = 8;
            public void actionPerformed(ActionEvent e) {
                frame = (frame+1)%8;
                busyPainter.setFrame(frame);
                repaint();
            }
        });
        busy.start();
    }
    
    private void stopAnimation() {
        busy.stop();
        busyPainter.setFrame(-1);
        repaint();
        busy = null;
    }
    
    @Override
    public void removeNotify() {
    	// fix for #626
    	stopAnimation();
    	super.removeNotify();
    }
    
    @Override
    public void addNotify() {
    	super.addNotify();
    	// fix for #626
    	startAnimation();
    }
}
