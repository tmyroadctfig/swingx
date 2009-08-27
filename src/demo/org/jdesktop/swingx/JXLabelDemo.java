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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImageOp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXLabel.TextAlignment;
import org.jdesktop.swingx.image.FastBlurFilter;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.util.PaintUtils;

/**
 * Simple demo of the JXLabel class. This demo uses both the default foreground
 * painter, and a custom background painter.
 */
public class JXLabelDemo {
    private static final Logger log = Logger.getAnonymousLogger();
    static {
        log.setLevel(Level.FINEST);
    }

    public static void main(String[] args) {
        final JXFrame f = new JXFrame("JXLabel Demo", true);

        final JXLabel label = new JXLabel("<p>Painter support consists of the <code>foregroundPainter</code> and" + 
                " <code>backgroundpainter</code> properties. The <code>backgroundPainter</code>" + 
                " refers to a painter responsible for painting <i>beneath</i> the text and icon." + 
                "</p>");
        try {
            label.setIcon(new ImageIcon(ImageIO.read(JXLabelDemo.class.getResourceAsStream("/org/jdesktop/swingx/renderer/resources/exit.png"))));
            log.fine("Icon:" + label.getIcon().getIconWidth() + ", " + label.getIconTextGap());
        } catch (IOException e1) {
            // ignore
        }
        label.setLineWrap(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(474,474));
        
        Painter<?> standardPainter = label.getForegroundPainter();
        final AbstractPainter<?> ap = (AbstractPainter<?>)standardPainter;
        
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
        JPanel control = new JPanel();
        f.add(control, BorderLayout.SOUTH);
        control.add(slider);
        final JCheckBox lineWrap = new JCheckBox("Wrap lines", true);
        control.add(lineWrap);
        lineWrap.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                label.setLineWrap(lineWrap.isSelected());
                f.repaint();
            }});

        Object[] items = new Object[] {"Left", "Center", "Right","Justified"};
        final List<Object> itemList = new ArrayList<Object>();
        itemList.addAll(Arrays.asList(items));
        JComboBox combo = new JComboBox(items);
        combo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    return;
                }
                log.fine(itemList.indexOf(e.getItem()) + "" + TextAlignment.values()[itemList.indexOf(e.getItem())]);
                label.setTextAlignment(TextAlignment.values()[itemList.indexOf(e.getItem())]);
            }});
        control.add(combo);
        control.add(new JButton(new AbstractAction("Rotate text") {
            
            public void actionPerformed(ActionEvent e) {
                label.setTextRotation((label.getTextRotation() + Math.PI/16 ) % (2* Math.PI));
                f.repaint();
            }}));
        //f.setSize(500, 500);
        f.pack();
        f.setVisible(true);
    }
}
