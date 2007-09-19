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

import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.image.StackBlurFilter;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImageOp;

/**
 * Simple demo
 *
 */
public class JXButtonDemo extends JPanel {
    public JXButtonDemo() {
        //simple demo that blurs the button's text
        final JXButton b = new JXButton("Execute");
        final AbstractPainter fgPainter = (AbstractPainter)b.getForegroundPainter();
        final StackBlurFilter filter = new StackBlurFilter();
        fgPainter.setFilters(filter);

        b.addMouseListener(new MouseAdapter() {
            boolean entered = false;
            public void mouseEntered(MouseEvent mouseEvent) {
                if (!entered) {
                    fgPainter.setFilters(new BufferedImageOp[0]);
                    b.repaint();
                    entered = true;
                }
            }
            public void mouseExited(MouseEvent mouseEvent) {
                if (entered) {
                    fgPainter.setFilters(filter);
                    b.repaint();
                    entered = false;
                }
            }
        });
        add(b);
    }

    public static void main(String[] args) {
        JXFrame f = new JXFrame("JXButton Demo", true);
        f.add(new JXButtonDemo());
        f.setSize(400, 300);
        f.setStartPosition(JXFrame.StartPosition.CenterInScreen);
        f.setVisible(true);
    }
}
