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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.action.AbstractActionExt;

/**
 * Quick experiment: concept of underlay/content layer.
 * 
 * TODO move somewhere else (incubator, doesn't belong into core!
 * 
 * @author Jeanette Winzenburg
 */
public class PainterExperiments extends InteractiveTestCase {
    public static void main(String args[]) {
//      setSystemLF(true);
      PainterExperiments test = new PainterExperiments();
      try {
        test.runInteractiveTests();
//         test.runInteractiveTests(".*Label.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }
    /**
     * JXLabel restore default foreground painter.
     * Sequence: 
     *   compose the default with a transparent overlay
     *   try to reset to default
     *   try to compose the overlay again.
     */
    public void interactiveRestoreDefaultForegroundPainter() {
        final JXXLabel opaque = new JXXLabel();
        opaque.setOpaque(true);
        opaque.setBackground(Color.YELLOW);
        opaque.setText("setup: compound - default and overlay ");
       ShapePainter shapePainter = new ShapePainter();
       final AlphaPainter alpha = new AlphaPainter();
       alpha.setAlpha(0.2f);
       alpha.setPainters(shapePainter);
       CompoundPainter<JComponent> compoundA = new CompoundPainter<JComponent>(
               opaque.getDefaultUnderlayPainter(), 
               opaque.getDefaultContentPainter(), alpha);
       opaque.setPainter(compoundA);
        final JXXLabel notOpaque = new JXXLabel();
        notOpaque.setOpaque(false);
         notOpaque.setText("setup: compound - default and overlay ");
//        final AlphaPainter alpha = new AlphaPainter();
//        alpha.setAlpha(0.2f);
//        alpha.setPainters(new ShapePainter());
        CompoundPainter<JComponent> compound = new CompoundPainter<JComponent>(
                notOpaque.getDefaultUnderlayPainter(), 
                notOpaque.getDefaultContentPainter(), alpha);
        notOpaque.setPainter(compound);
        JComponent box = Box.createVerticalBox();
        box.add(opaque);
        box.add(notOpaque);
        Action action = new AbstractActionExt("reset default foreground") {
            boolean reset;
            public void actionPerformed(ActionEvent e) {
                if (reset) {
                    CompoundPainter<JComponent> painter = new CompoundPainter<JComponent>(
                            notOpaque.getDefaultUnderlayPainter(),
                           notOpaque.getDefaultContentPainter(), alpha);
                    notOpaque.setPainter(painter);
                    CompoundPainter<JComponent> painterA = new CompoundPainter<JComponent>(
                            opaque.getDefaultUnderlayPainter(),
                            opaque.getDefaultContentPainter(), alpha);
                     opaque.setPainter(painterA);
                } else {
                  // try to reset to default
                    notOpaque.setPainter(null);
                    opaque.setPainter(null);
                }
                reset = !reset;
                notOpaque.repaint();
                opaque.repaint();
            }

        };
        JXFrame frame = wrapInFrame(box, "foreground painters");
        addAction(frame, action);
        frame.pack();
        frame.setVisible(true);
    }
    
    public void interactiveJXXLabel() {
        JXXLabel label = new JXXLabel();
        label.setText("XXLable: permanent translate");
        Painter<JComponent> permanentTranslate = new Painter<JComponent>() {

            public void paint(Graphics2D g, JComponent object, int width, int height) {
                g.translate(50, 0); 
            }
            
        };
        // the permanentTranslate has no effect because CompoundPainter.paint
        // creates a graphics for each painter
        CompoundPainter<JComponent> painter = new CompoundPainter<JComponent>(
                label.getDefaultUnderlayPainter(), permanentTranslate, 
                label.getDefaultContentPainter(), new ShapePainter());
        label.setPainter(painter);
        JComponent box = Box.createVerticalBox();
        box.add(label);
        showInFrame(box, "JXXLabel - underlay/content/overlay");
        
    }

    
    public static class JXXLabel extends JLabel {
        Painter<JComponent> defaultUnderlayPainter;
        Painter<JComponent> defaultContentPainter;
        
        Painter<JComponent> painter;
        
        public JXXLabel() {
            initDefaultPainters();
        }

        /**
         * 
         */
        private void initDefaultPainters() {
            defaultUnderlayPainter = new Painter<JComponent>() {

                public void paint(Graphics2D g, JComponent comp, int width, int height) {
                    if (comp.isOpaque()) {
                        g.setColor(comp.getBackground());
                        g.fillRect(0, 0, width, height);
                    }
                }
                
            };
            defaultContentPainter = new Painter<JComponent>() {

                public void paint(Graphics2D g, JComponent object, int width, int height) {
                    uiPaint(g);
                }
                
            };
            
        }

        /**
         * Delegates content painting to the ui. Note that this method
         * most probably will change the graphics state. It's up to calling
         * code to guarantee any state invariants.
         * 
         * @param g the graphics to paint on.
         */
        protected void uiPaint(Graphics2D g) {
            if (ui != null) {
                ui.paint(g, this);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Painter<JComponent> painter = getPainter();
            if (painter != null) {
                paintComponentWithPainter(g, painter);
            } else {
                super.paintComponent(g);
            }
        }

        /**
         * @param g
         */
        private void paintComponentWithPainter(Graphics g, Painter<JComponent> painter) {
            Graphics2D scratch = (Graphics2D) g.create();
            try {
                painter.paint(scratch, this, getWidth(), getHeight());
            } finally {
                scratch.dispose();
            }
        }

        /**
         * @return
         */
        private Painter<JComponent> getPainter() {
            return painter;
        }
        
        protected void setPainter(Painter<JComponent> painter) {
            this.painter = painter;
        }
        
        public Painter<JComponent> getDefaultUnderlayPainter() {
            return defaultUnderlayPainter;
        }
        
        public Painter<JComponent> getDefaultContentPainter() {
            return defaultContentPainter;
        }
    }
}
