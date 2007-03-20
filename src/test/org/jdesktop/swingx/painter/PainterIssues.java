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
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.action.AbstractActionExt;

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
     * Issue #??-swingx: default foreground painter not guaranteed after change.
     *
     */
    public void testDefaultForegroundPainter() {
        JXLabel label =  new JXLabel();
        Painter defaultForeground = label.getForegroundPainter();
        // sanity
        assertNotNull(defaultForeground);
        label.setForegroundPainter(null);
        assertEquals(defaultForeground, label.getForegroundPainter());
    }
    

    // ------------------ visual tests
    /**
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
        final AlphaPainter alpha = new AlphaPainter();
        alpha.setAlpha(0.2f);
        alpha.setPainters(shapePainter);
        CompoundPainter compound = new CompoundPainter(alpha, foreground
                .getForegroundPainter());
        foreground.setForegroundPainter(compound);
        box.add(foreground);
        Action action = new AbstractActionExt("reset default foreground") {
            boolean reset;
            public void actionPerformed(ActionEvent e) {
                if (reset) {
                    CompoundPainter painter = new CompoundPainter(alpha, foreground.getForegroundPainter());
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
    
    
    /**
     * JXLabel default foreground painter - share between labels.
     * Probably illegal :-)
     * 
     */
    public void interactiveXLabelSharedDefaultForegroundPainter() {
        JComponent box = Box.createVerticalBox();
        final JXLabel foreground = new JXLabel(
                "setup: compound - default and overlay ");
        ShapePainter shapePainter = new ShapePainter();
        AlphaPainter alpha = new AlphaPainter();
        alpha.setAlpha(0.2f);
        alpha.setPainters(shapePainter);
        CompoundPainter compound = new CompoundPainter(alpha, foreground
                .getForegroundPainter());
        foreground.setForegroundPainter(compound);
        box.add(foreground);
        JXLabel shared = new JXLabel(
                "setup: shared compound of first label - this doesn't show up");
        shared.setForegroundPainter(compound);
        box.add(shared);
        showInFrame(box, "foreground painters");
    }
    
    /**
     * JXLabel background painter not shown if opaque.
     * 
     */
    public void interactiveXLabelBackgroundPainter() {
        JComponent box = Box.createVerticalBox();
        ShapePainter shapePainter = new ShapePainter();
        JXLabel opaqueTrue = new JXLabel("setup: backgroundPainter, opaque = true");
        opaqueTrue.setOpaque(true);
        opaqueTrue.setBackgroundPainter(shapePainter);
        box.add(opaqueTrue);
        JXLabel opaqueFalse = new JXLabel("setup: backgroundPainter, opaque = false");
        opaqueFalse.setOpaque(false);
        opaqueTrue.setBackgroundPainter(shapePainter);
        box.add(opaqueFalse);
        JXLabel opaqueUnchanged = new JXLabel("setup: backgroundPainter, opaque = unchanged");
        opaqueUnchanged.setBackgroundPainter(shapePainter);
        box.add(opaqueUnchanged);
        showInFrame(box, "background painters");
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
     * Style.None - use case? Always invisible?
     */
    public void interactiveRenderingLabel() {
        JComponent box = Box.createVerticalBox();
        final JXLabel label = new JXLabel("setup: ShapePainter with fillstyle none");
        // fixed: NPE with null shape - but has default instead of null?
        final ShapePainter styleNone = new ShapePainter();
        styleNone.setStyle(ShapePainter.Style.NONE);
        label.setBackgroundPainter(styleNone);
        box.add(label);
        final JXLabel label2 = new JXLabel("setup: default ShapePainter");
        final ShapePainter painter = new ShapePainter();
        label2.setBackgroundPainter(painter);
        box.add(label2);
        Action action = new AbstractActionExt("toggle painter visible") {

            public void actionPerformed(ActionEvent e) {
                styleNone.setVisible(!styleNone.isVisible());
                painter.setVisible(!painter.isVisible());
                label.repaint();
                label2.repaint();
            }
            
        };
        JXFrame frame = wrapInFrame(box, "renderer label with shape painter - fillstyle none");
        addAction(frame, action);
        frame.pack();
        frame.setVisible(true);
    }

}
