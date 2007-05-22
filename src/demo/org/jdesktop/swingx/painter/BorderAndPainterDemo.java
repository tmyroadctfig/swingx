/*
 * BorderAndPainterDemo.java
 *
 * Created on May 22, 2007, 1:37:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.util.PaintUtils;

/**
 *
 * @author rbair
 */
public class BorderAndPainterDemo {
    public static void main(String[] args) {
        JXFrame f = new JXFrame("Border and Painter Demo", true);
        f.setStartPosition(JXFrame.StartPosition.CenterInScreen);
        
        JXPanel p = new JXPanel(new BorderLayout());
        JLabel l = new JLabel("Hello Everybody!");
        l.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(l);
        
        RectanglePainter bkg = new RectanglePainter();
        bkg.setRounded(true);
        bkg.setFillPaint(PaintUtils.BLUE_EXPERIENCE);
        bkg.setStyle(RectanglePainter.Style.FILLED);
        bkg.setRoundWidth(25);
        bkg.setRoundHeight(25);
        bkg.setPaintStretched(true);
        
        p.setBackgroundPainter(new ComponentPainterAdapter(bkg, new Insets(11, 11, 10, 10)));
        
        p.setBorder(new CompoundBorder(
                new EmptyBorder(12, 12, 12, 12),
                new RoundedBorder()));
        
        f.add(p);
        f.setSize(400, 300);
        f.setVisible(true);
    }
    
    private static final class ComponentPainterAdapter extends AbstractPainter<JComponent>{
        private static final Insets INSETS = new Insets(0, 0, 0, 0);
        private Painter p;
        private Insets insets = null;
        public ComponentPainterAdapter(Painter p, Insets i) {
            this.p = p;
            this.insets = i;
        }
        
        protected void doPaint(Graphics2D g, JComponent object, int width, int height) {
            if (insets == null) {
                object.getInsets(INSETS);
            } else {
                INSETS.set(insets.top, insets.left, insets.bottom, insets.right);
            }
            
            g.translate(-INSETS.left, -INSETS.top);
            width += INSETS.left + INSETS.right;
            height += INSETS.top + INSETS.bottom;
            
            p.paint(g, object, width, height);
        }
        
    }
    
    /**
     * RoundedBorder - A border implementation that draws a inner shadowed receced border with rounded corners. It also
     * darkens the center contents by about 5-10%.
     *
     * @author Created by Jasper Potts (Jan 18, 2007)
     * @version 1.0
     */
    private static final class RoundedBorder implements Border {
        
        private static BufferedImage img;
        
        static {
            try {
                img = ImageIO.read(RoundedBorder.class.getResource("resources/border.png"));
            } catch (Exception e) {
            }
        }
        
        private static final Insets SPACE_INSETS = new Insets(5, 5, 5, 5);
        private static final Insets INSETS = new Insets(
                SPACE_INSETS.top + 12,
                SPACE_INSETS.left + 12,
                SPACE_INSETS.bottom + 12,
                SPACE_INSETS.right + 12);
        
        /** {@inheritDoc} */
        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
        
        /** {@inheritDoc} */
        public boolean isBorderOpaque() {
            return false;
        }
        
        /** {@inheritDoc} */
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Rectangle b = new Rectangle(
                    x + SPACE_INSETS.left,
                    y + SPACE_INSETS.top,
                    width - (SPACE_INSETS.left + SPACE_INSETS.right + 1),
                    height - (SPACE_INSETS.top + SPACE_INSETS.bottom + 1));
            //top
            g.drawImage(img,
                    b.x, b.y, b.x + 10, b.y + 10,
                    0, 0, 10, 10, null);
            g.drawImage(img,
                    b.x + 10, b.y, b.x + b.width - 10, b.y + 10,
                    11, 0, 12, 10, null);
            g.drawImage(img,
                    b.x + b.width - 10, b.y, b.x + b.width, b.y + 10,
                    22, 0, 32, 10, null);
            // bottom
            g.drawImage(img,
                    b.x, b.y + b.height - 10, b.x + 10, b.y + b.height,
                    0, 22, 10, 32, null);
            g.drawImage(img,
                    b.x + 10, b.y + b.height - 10, b.x + b.width - 10, b.y + b.height,
                    11, 22, 12, 32, null);
            g.drawImage(img,
                    b.x + b.width - 10, b.y + b.height - 10, b.x + b.width, b.y + b.height,
                    22, 22, 32, 32, null);
            // left
            g.drawImage(img,
                    b.x, b.y + 10, b.x + 10, b.y + b.height - 10,
                    0, 11, 10, 12, null);
            // right
            g.drawImage(img,
                    b.x + b.width - 10, b.y + 10, b.x + b.width, b.y + b.height - 10,
                    22, 11, 32, 12, null);
            // fill
            g.drawImage(img,
                    b.x + 10, b.y + 10, b.x + b.width - 10, b.y + b.height - 10,
                    15, 15, 16, 16, null);
            
            
        }
    }
}
