/*
 * $Id$
 *
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
package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.image.BufferedImageOp;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.image.FastBlurFilter;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.util.PaintUtils;

/**
 * Simple demo of the JXLabel class. This demo uses both the default foreground
 * painter, and a custom background painter.
 */
public class JXLabelDemo {
    public static void main(String[] args) {
        final JXFrame f = new JXFrame("JXLabel Demo", true);

        JXLabel label = new JXLabel("That's All Folks!");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        Painter standardPainter = label.getForegroundPainter();
        final AbstractPainter ap = (AbstractPainter)standardPainter;
        
        MattePainter bkground = new MattePainter(PaintUtils.BLUE_EXPERIENCE, true);
        label.setBackgroundPainter(bkground);
        
        final JSlider slider = new JSlider();
        slider.getModel().setMinimum(0);
        slider.getModel().setMaximum(8);
        slider.getModel().setValue(0);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                int value = slider.getValue();
                if (value == 0) {
                    ap.setFilters(new BufferedImageOp[0]);
                    f.repaint();
                } else {
                    ap.setFilters(new FastBlurFilter(value));
                    f.repaint();
                }
            }
        });
        
        f.add(label);
        f.add(slider, BorderLayout.SOUTH);
        f.setSize(400, 300);
        f.setVisible(true);
    }
}
