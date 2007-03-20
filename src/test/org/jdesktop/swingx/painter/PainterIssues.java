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
package org.jdesktop.swingx.painter;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.renderer.JRendererLabel;

/**
 * Test to exposed known issues of <code>Painter</code>s.
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class PainterIssues extends InteractiveTestCase {
    public static void main(String args[]) {
//      setSystemLF(true);
      PainterIssues test = new PainterIssues();
      try {
        test.runInteractiveTests();
//         test.runInteractiveTests(".*Label.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }

    /**
     * JXLabel background painter not shown if opaque.
     *
     */
    public void interactiveXLabelBackgroundPainter() {
        JXLabel label = new JXLabel("setup: backgroundPainter, opaque = true");
        label.setOpaque(true);
        label.setBackgroundPainter(new ShapePainter());
        showInFrame(label, "background painters");
    }
    

    /**
     * 
     * paint doc relieves impl from restoring graphics. Who
     * is responsible for cleanup?
     *
     */
    public void interactiveRestoreGraphics() {
        final Painter<JComponent> painter = new Painter<JComponent>() {

            public void paint(Graphics2D g, JComponent object, int width, int height) {
                g.translate(50, 0); 
            }
            
        };
        JLabel label = new JLabel() {

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D scratch = (Graphics2D) g.create();
                try {
                    painter.paint(scratch, this, getWidth(), getHeight());
                    ui.paint(scratch, this);
                } finally {
                    scratch.dispose();
                }
            }
            
        };
        label.setText("yet another .......... let's see");
        showInFrame(label, "unrestored graphics");
    }
    
    /**
     * undocumented default shape/properties.
     */
    public void interactiveRenderingLabel() {
        JRendererLabel label = new JRendererLabel();
        label.setText("some dummy long enough .............  opaque? ");
        // fixed: NPE with null shape - but has default instead of null?
        ShapePainter painter = new ShapePainter();
        painter.setStyle(ShapePainter.Style.NONE);
        label.setPainter(painter);
        showInFrame(label, "renderer label with shape painter");
    }

}
