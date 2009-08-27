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
 *
 */
package org.jdesktop.swingx;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComponent;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.painter.AlphaPainter;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.ShapePainter;

/**
 * Base test class for JXLabel related code and issues.
 * 
 * @author rah003
 */
public class JXLabelVisualCheck extends InteractiveTestCase {
    
    static Logger log = Logger.getAnonymousLogger();
    
    public static void main(String[] args) {
        JXLabelVisualCheck test = new JXLabelVisualCheck();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    /**
     * Issue #??-swingx: default foreground painter not guaranteed after change.
     *
     * JXLabel restore default foreground painter.
     * Sequence: 
     *   compose the default with a transparent overlay
     *   try to reset to default
     *   try to compose the overlay again.
     */
    public void interactiveRestoreDefaultForegroundPainter() {
        JComponent box = Box.createVerticalBox();
        final JXLabel foreground = new JXLabel(
                "setup: compound - default and overlay ");
        ShapePainter shapePainter = new ShapePainter();
        final AlphaPainter<?> alpha = new AlphaPainter<Object>();
        alpha.setAlpha(0.2f);
        alpha.setPainters(shapePainter);
        CompoundPainter<?> compound = new CompoundPainter<Object>(foreground
                .getForegroundPainter(), alpha);
        foreground.setForegroundPainter(compound);
        box.add(foreground);
        Action action = new AbstractActionExt("reset default foreground") {
            boolean reset;
            public void actionPerformed(ActionEvent e) {
                if (reset) {
                    CompoundPainter<?> painter = new CompoundPainter<Object>(alpha, foreground.getForegroundPainter());
                    foreground.setForegroundPainter(painter);
                } else {
                  // try to reset to default
                    foreground.setForegroundPainter(null);
                }
                reset = !reset;

            }

        };
        JXFrame frame = wrapInFrame(box, "foreground painters");
        addAction(frame, action);
        frame.pack();
        frame.setVisible(true);
    }
    
}
