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
package org.jdesktop.swingx.painter;

import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Demo of the AlphaPainter.
 */
public class AlphaPainterDemo {
    private AlphaPainterDemo() {}

    public static void main(String ... args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JXPanel panel = new JXPanel();
                AlphaPainter alpha = new AlphaPainter();
                alpha.setAlpha(1f);
                alpha.setPainters(new PinstripePainter(new Color(255,255,255,125),45,20,20));
                panel.setBackgroundPainter(new CompoundPainter(
                        new MattePainter(Color.RED),
                        alpha
                        ));

                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(panel);
                frame.pack();
                frame.setSize(200,200);
                frame.setVisible(true);
            }
        });
    }
}
