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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.AlphaPainter;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.ShapePainter;

import com.jhlabs.image.AbstractBufferedImageOp;
import com.jhlabs.image.CausticsFilter;
import com.jhlabs.image.ChromeFilter;
import com.jhlabs.image.ShadowFilter;
import com.jhlabs.image.SparkleFilter;

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
     * Example of how-to apply filters to the label's foreground.
     */
    @SuppressWarnings("unchecked")
    public void interactiveFancyFilter() {
        JXLabel label = new JXLabel("that's the real text");
        label.setFont(new Font("SansSerif", Font.BOLD, 80));
        AbstractPainter<?> fg = (AbstractPainter<Object>) label.getForegroundPainter();
        fg.setFilters(new ChromeFilter(), new ShadowFilter(5, 5, 2, .7f));
        JXPanel panel = new JXPanel();
        MattePainter background = new MattePainter(Color.BLUE);
        background.setFilters(new CausticsFilter(), new SparkleFilter());
        panel.setBackgroundPainter(background);
        panel.add(label);
        JXFrame frame = wrapInFrame(panel, "fancy filter");
        show(frame,400, 400);
    }
    
    
    /**
     * Notice that this is not perfect wrt alignment to see just uncomment super.paintComponent(g) 
     * Maybe somebody with BasicLabelUI knowledge could help?
     * 
     */
    public static final class JHLabsLabel extends JLabel {

     private int textX;

     private int textY;

     private AbstractBufferedImageOp[] filters;

     public JHLabsLabel(String string, AbstractBufferedImageOp... filters) {
      super(string);
      this.filters = filters;
     }

     @Override
     public void paintComponent(Graphics g) {

      // super.paintComponent(g);

      BufferedImage img = createTextImage(getText(), getFont());

      for (AbstractBufferedImageOp f : filters) {
       img = f.filter(img, null);
      }

      // g.drawImage(f.filter(img, null), textX, textY, null);
      g.drawImage(img, textX, textY, null);
     }

     private BufferedImage createTextImage(String text, Font font) {

      Rectangle paintIconR = new Rectangle();
      Rectangle paintTextR = new Rectangle();
      Rectangle paintViewR = new Rectangle();
      Insets paintViewInsets = new Insets(0, 0, 0, 0);

      paintViewInsets = getInsets(paintViewInsets);
      paintViewR.x = paintViewInsets.left;
      paintViewR.y = paintViewInsets.top;
      paintViewR.width = getWidth()
        - (paintViewInsets.left + paintViewInsets.right);
      paintViewR.height = getHeight()
        - (paintViewInsets.top + paintViewInsets.bottom);

      String clippedText = SwingUtilities.layoutCompoundLabel(
        (JComponent) this, getFontMetrics(getFont()), text,
        getIcon(), getVerticalAlignment(),
        getHorizontalAlignment(), getVerticalTextPosition(),
        getHorizontalTextPosition(), paintViewR, paintIconR,
        paintTextR, getIconTextGap());

      boolean isAntiAliased = true;
      boolean usesFractionalMetrics = false;
      FontRenderContext frc = new FontRenderContext(null, isAntiAliased,
        usesFractionalMetrics);
      TextLayout layout = new TextLayout(clippedText, font, frc);
      Rectangle2D bounds = layout.getBounds();
      int w = (int) Math.ceil(bounds.getWidth());
      int h = (int) Math.ceil(bounds.getHeight());
      BufferedImage image = new BufferedImage(w, h,
        BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = image.createGraphics();
      g.setColor(new Color(0, 0, 0, 0));
      g.fillRect(0, 0, w, h);
      g.setColor(getForeground());
      g.setFont(font);
      Object antiAliased = isAntiAliased ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        antiAliased);
      Object fractionalMetrics = usesFractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON
        : RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
      g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
        fractionalMetrics);
      g.drawString(clippedText, (float) -bounds.getX(), (float) -bounds
        .getY());
      // g.drawString(clippedText, (float) 0, (float) 0);
      g.dispose();

      textX = paintTextR.x;
      textY = paintTextR.y;// + getFontMetrics(font).getAscent() / 2;
      System.out.println(String.format("X=%d Y=%d, w=%d h=%d", textX,
        textY, w, h));

      return image;
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
    
    /**
     * Issue #1330-swingx: underlined font does not retain underline during wrapping.
     */
    public void interactiveUnderlinedFontWithWrapping() {
        final JXLabel label = new JXLabel("A really long sentence to display the text wrapping features of JXLabel.");
        // when lineWrap is true, can't see underline effects 
        // when lineWrap is false, underline is ok
        label.setLineWrap(true);
        label.setBounds(31, 48, 91, 18);
        // set font underline
        Map<TextAttribute, Integer> map = new HashMap<TextAttribute, Integer>();
        map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        label.setFont(label.getFont().deriveFont(map));
        
        final JXFrame frame = wrapInFrame(label, "Underlined Font with wrapping");
        addAction(frame, new AbstractAction("Toggle wrapping") {
            @Override
            public void actionPerformed(ActionEvent e) {
                label.setLineWrap(!label.isLineWrap());
                frame.repaint();
            }
        });
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Issue #978: Setting background color has no effect
     */
    public void interactiveBackgroundColorSetting() {
        final JXLabel label = new JXLabel("A simple label.");
        label.setOpaque(true);
        label.setBackground(Color.CYAN);
        
        showInFrame(label, "Background Color Check");
    }
}
