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
package org.jdesktop.swingx.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.Timer;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.action.ActionContainerFactory;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.PainterHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate.ColumnHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.NotHighlightPredicate;
import org.jdesktop.swingx.painter.AbstractLayoutPainter;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.BusyPainter;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.ShapePainter;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.HorizontalAlignment;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.VerticalAlignment;
import org.jdesktop.swingx.painter.effects.InnerGlowPathEffect;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jdesktop.swingx.test.XTestUtils;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.test.AncientSwingTeam;

/**
 * Experiments with highlighters using painters.<p>
 * 
 * Links
 * <ul>
 * <li> <a href="">Sneak preview II - Transparent Highlighter</a>
 * </ul>
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class PainterVisualCheck extends InteractiveTestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger
            .getLogger(PainterVisualCheck.class.getName());
    public static void main(String args[]) {
//      setSystemLF(true);
      PainterVisualCheck test = new PainterVisualCheck();
      try {
        test.runInteractiveTests();
//         test.runInteractiveTests("interactive.*Gradient.*");
//         test.runInteractiveTests("interactive.*Icon.*");
//        test.runInteractiveTests("interactive.*Animated.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }
    
    public void interactiveTriangleRenderer() {
        JXTable table = new JXTable(new AncientSwingTeam());
        ShapePainter painter = new ShapePainter();
        Shape polygon = new Polygon(new int[] { 0, 5, 5 },
                new int[] { 0, 0, 5 }, 3);
        painter.setShape(polygon);
        painter.setFillPaint(Color.RED);
        painter.setStyle(ShapePainter.Style.FILLED);
        painter.setPaintStretched(false);
        // hmm.. how to make this stick to the trailing upper corner?
        painter.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        painter.setVerticalAlignment(VerticalAlignment.TOP);
        Highlighter hl = new PainterHighlighter(new ColumnHighlightPredicate(3), painter); 
        table.addHighlighter(hl);
        showWithScrollingInFrame(table, "Renderer with Triangle marker");
    }

    /**
     * Use Painter for an underline-rollover effect.
     */
    public void interactiveRolloverPainter() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        MattePainter matte = new MattePainter(getTransparentColor(Color.RED, 80));
        RelativePainter painter = new RelativePainter<Object>(matte);
        painter.setYFactor(0.2);
        painter.setVerticalAlignment(VerticalAlignment.BOTTOM);
        Highlighter hl = new PainterHighlighter(HighlightPredicate.ROLLOVER_ROW, painter);
        table.addHighlighter(hl);
        JXFrame frame = wrapWithScrollingInFrame(table, 
                "painter-aware renderer rollover");
        addStatusComponent(frame, new JLabel("gradient background of cells with value's containing 'y'"));
        show(frame);
    }

    /**
     * Creates and returns a predicate for filtering labels whose text
     * property contains the given text.
     * @return 
     */
    private HighlightPredicate createComponentTextBasedPredicate(final String substring) {
        HighlightPredicate predicate = new HighlightPredicate() {

            public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                if (!(renderer instanceof JLabel)) return false;
                String text = ((JLabel) renderer).getText();
                 return text.contains(substring);
            }
            
        };
        return predicate;
    }
   
    /**
     * Use ?? for fixed portion background highlighting
     * Use SwingX extended default renderer.
     */
    public void interactiveTableBarHighlight() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        MattePainter p =  new MattePainter(getTransparentColor(Color.BLUE, 125));
        RelativePainter relativePainter = new RelativePainter<Object>(p);
        relativePainter.setXFactor(.5);
        Highlighter hl = new PainterHighlighter(createComponentTextBasedPredicate("y"), 
                relativePainter);
        table.addHighlighter(hl);
        JXFrame frame = wrapWithScrollingInFrame(table, 
                "painter-aware renderer with value-based highlighting");
        addMessage(frame, "bar in cells with value containing y");
        show(frame);
    }
   

//------------------------ Transparent painter aware button as rendering component
    
    /**
     * Use a custom button controller to show both checkbox icon and text to
     * render Actions in a JXList. Apply striping and a simple gradient highlighter.
     */
    public void interactiveTableWithListColumnControl() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        JXList list = new JXList();
        Highlighter highlighter = HighlighterFactory.createSimpleStriping(HighlighterFactory.LINE_PRINTER);
        table.addHighlighter(highlighter);
        Painter gradient = createGradientPainter(Color.YELLOW, .7f, true);
        list.setHighlighters(highlighter, new PainterHighlighter(gradient));
        // quick-fill and hook to table columns' visibility state
        configureList(list, table, false);
        // a custom rendering button controller showing both checkbox and text
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof AbstractActionExt) {
                    return ((AbstractActionExt) value).getName();
                }
                return "";
            }
            
        };
        BooleanValue bv = new BooleanValue() {

            public boolean getBoolean(Object value) {
                if (value instanceof AbstractActionExt) {
                    return ((AbstractActionExt) value).isSelected();
                }
                return false;
            }
            
        };
        CheckBoxProvider wrapper = new CheckBoxProvider(new MappedValue(sv, null, bv), JLabel.LEADING);
        list.setCellRenderer(new DefaultListRenderer(wrapper));
        JXFrame frame = showWithScrollingInFrame(table, list,
                "checkbox list-renderer - striping and gradient");
        addStatusMessage(frame, "fake editable list: space/doubleclick on selected item toggles column visibility");
        frame.pack();
    }


    /**
     * Creates and returns a Painter with a gradient paint starting with
     * startColor to WHITE.
     * 
     * @param startColor
     * @param percentage
     * @param transparent
     * @return
     */
    protected Painter createGradientPainter(Color startColor, float end,
            boolean transparent) {
        startColor = getTransparentColor(startColor, transparent ? 125 : 254);
        Color endColor = getTransparentColor(Color.WHITE, 0);
        GradientPaint paint = new GradientPaint(
                    new Point2D.Double(0, 0),
                    startColor,
                   new Point2D.Double(1000, 0),
                   endColor);

        MattePainter painter = new MattePainter(paint);
        painter.setPaintStretched(true);
        // not entirely successful - the relative stretching is on
        // top of a .5 stretched gradient in matte
        RelativePainter wrapper = new RelativePainter<Object>(painter);
        wrapper.setXFactor(end);
        return wrapper;
    }

    private static Color getTransparentColor(Color base, int transparency) {
        return new Color(base.getRed(), base.getGreen(), base.getBlue(),
                transparency);
    }
    
    // ------------------------
    /**
     * Use highlighter with background image painter. Shared by table and list.
     */
    public void interactiveIconPainterHighlight() throws Exception {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        ComponentProvider<JLabel> controller = new LabelProvider(
                JLabel.RIGHT);
        table.getColumn(0).setCellRenderer(
                new DefaultTableRenderer(controller));
        final ImagePainter imagePainter = new ImagePainter(XTestUtils.loadDefaultImage());
        HighlightPredicate predicate = new ColumnHighlightPredicate(0);
        Highlighter iconHighlighter = new PainterHighlighter(predicate, imagePainter );
        Highlighter alternateRowHighlighter = HighlighterFactory.createSimpleStriping();
        table.addHighlighter(alternateRowHighlighter);
        table.addHighlighter(iconHighlighter);
        // re-use component controller and highlighter in a JXList
        JXList list = new JXList(createListNumberModel(), true);
        list.setCellRenderer(new DefaultListRenderer(controller));
        list.addHighlighter(alternateRowHighlighter);
        list.addHighlighter(iconHighlighter);
        list.toggleSortOrder();
        final JXFrame frame = showWithScrollingInFrame(table, list,
                "image highlighting plus striping");
        frame.pack();
    }
  
    /**
     * Use highlighter with image painter which is positioned relative to 
     * cell value. 
     */
    public void interactivePositionedIconPainterHighlight()  {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        final ImagePainter imagePainter = new ImagePainter(XTestUtils.loadDefaultImage("green-orb.png"));
        imagePainter.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        imagePainter.setAreaEffects(new InnerGlowPathEffect());
        ValueBasedPositionalHighlighter iconHighlighter = new ValueBasedPositionalHighlighter();
        iconHighlighter.setDelegatePainter(imagePainter);
        iconHighlighter.setHorizontalAlignment(HorizontalAlignment.LEFT);
        table.getColumnExt(3).addHighlighter(iconHighlighter);
        // re-use component controller and highlighter in a JXList
        JXList list = new JXList(createListNumberModel(), true);
        list.setCellRenderer(new DefaultListRenderer(new LabelProvider(JLabel.RIGHT)));
        list.addHighlighter(iconHighlighter);
        list.toggleSortOrder();
        showWithScrollingInFrame(table, list, 
                "value-based image position highlighting");
    }

    
    
    /**
     * Use highlighter with image painter which is positioned relative to 
     * cell value. 
     */
    public void interactiveAnimatedIconPainterHighlight()  {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        ImagePainter imagePainter = new ImagePainter(XTestUtils.loadDefaultImage("green-orb.png"));
        imagePainter.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        final RelativePainter painter = new RelativePainter<Component>(imagePainter);
        PainterHighlighter iconHighlighter = new PainterHighlighter();
        iconHighlighter.setHighlightPredicate(HighlightPredicate.ROLLOVER_ROW);
        iconHighlighter.setPainter(painter);
        ActionListener l = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                double fraction = painter.getXFactor();
                fraction = fraction > 1 ? 0.0 : fraction + 0.1;
                painter.setXFactor(fraction);
            }
            
        };
        table.addHighlighter(iconHighlighter);
        showWithScrollingInFrame(table, 
                "Animated highlighter: marching icon on rollover");
        Timer timer = new Timer(100, l);
        timer.start();
    }
    
    /**
     * Use highlighter with BusyPainter.
     */
    public void interactiveAnimatedBusyPainterHighlight()  {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        table.getColumn(0).setCellRenderer(new DefaultTableRenderer(
                new HyperlinkProvider()));
        final BusyPainter busyPainter = new BusyPainter() {
            /**
             * Overridden to fix Issue #861-swingx: must notify on change
             * @param frame
             */
            @Override
            public void setFrame(int frame) {
                int old = getFrame();
                super.setFrame(frame);
                firePropertyChange("frame", old, getFrame());
            }
            
        };
        // JW: how do we ask for the height of the painter?
        table.setRowHeight(26);
        PainterHighlighter iconHighlighter = new PainterHighlighter();
        iconHighlighter.setHighlightPredicate(HighlightPredicate.ROLLOVER_ROW);
        iconHighlighter.setPainter(busyPainter);
        ActionListener l = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int frame = busyPainter.getFrame();
                frame = (frame+1)%busyPainter.getPoints();
                busyPainter.setFrame(frame);
            }
            
        };
        table.addHighlighter(iconHighlighter);
        showWithScrollingInFrame(table, 
                "Animated highlighter: BusyPainter on Rollover");
        Timer timer = new Timer(100, l);
        timer.start();
    }
    
    /**
     * Issue #862-swingx: SwingX rendering components should be PainterAware.
     * 
     * Currently this works only with a local version which has WrappingIconPanel
     * implement the PainterAware by delegating to its content delegate. 
     */
    public void interactiveAnimatedIconPainterHighlightTree()  {
        TreeModel model = new FileSystemModel();
        JXTree tree = new JXTree(model);
        tree.setRolloverEnabled(true);
        
        tree.setCellRenderer(new DefaultTreeRenderer(StringValues.FILE_NAME));
        ImagePainter imagePainter = new ImagePainter(XTestUtils.loadDefaultImage("green-orb.png"));
        imagePainter.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        final RelativePainter painter = new RelativePainter<Component>(imagePainter);
        PainterHighlighter iconHighlighter = new PainterHighlighter();
        iconHighlighter.setHighlightPredicate(HighlightPredicate.ROLLOVER_ROW);
        iconHighlighter.setPainter(painter);
        ActionListener l = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                double fraction = painter.getXFactor();
                fraction = fraction > 1 ? 0.0 : fraction + 0.1;
                painter.setXFactor(fraction);
            }
            
        };
        tree.addHighlighter(iconHighlighter);
        showWithScrollingInFrame(tree, 
                "Animated highlighter: marching icon on rollover");
        Timer timer = new Timer(100, l);
        timer.start();
    }

    
    /**
     * Use custom painter and highlighter for per-row image decoration.
     * 
     * @throws IOException
     */
    public void interactivePerRowImage() throws IOException {
        
        JXTable table = new JXTable(new AncientSwingTeam());
        table.setForeground(Color.MAGENTA);
        table.setSelectionForeground(Color.BLUE);
        table.setColumnControlVisible(true);
        table.setRowHeight(25);
        final BufferedImage moon = XTestUtils.loadDefaultImage("moon.jpg");
        final BufferedImage rocket = XTestUtils.loadDefaultImage("500by500.png");
        HighlightPredicate rocketPredicate = new HighlightPredicate() {

            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                return ((Integer) adapter.getValue(3)).intValue() < 50;
            }
            
        };
        HighlightPredicate moonPredicate = new NotHighlightPredicate(rocketPredicate);
        table.setHighlighters(
                new SubImagePainterHighlighter(rocketPredicate, 
                        new SubImagePainter(rocket))
                ,
                new SubImagePainterHighlighter(moonPredicate, 
                        new SubImagePainter(moon))
        );
        JXFrame frame = wrapWithScrollingInFrame(table, "painter in renderer");
        show(frame);
    }
    
    /**
     * Custom PainterHighlighter configures a SubImagePainter.
     * 
     */
    public static class SubImagePainterHighlighter extends PainterHighlighter {

        
        public SubImagePainterHighlighter(HighlightPredicate predicate, SubImagePainter painter) {
            super(predicate, painter);
        }
        
        @Override
        protected Component doHighlight(Component component,
                ComponentAdapter adapter) {
            Rectangle cellRect = getCellBounds(adapter);
            ((SubImagePainter) getPainter()).setImageClip(cellRect);
            return super.doHighlight(component, adapter);
        }
        
        private Rectangle getCellBounds(ComponentAdapter adapter) {
            // PENDING JW: add method to adapter
            JXTable table = (JXTable) adapter.getComponent();
            Rectangle cellRect = table.getCellRect(adapter.row, 
                    adapter.column, false);
            return cellRect;
        }

        @Override
        protected boolean canHighlight(Component component,
                ComponentAdapter adapter) {
            return super.canHighlight(component, adapter) 
                && (adapter.getComponent() instanceof JTable);
        }

        
    }
    
    /**
     * Simple Painter for subimage.
     */
    public static class SubImagePainter extends AbstractPainter {
        BufferedImage image;
        Rectangle imageClip;

        public SubImagePainter(BufferedImage image) {
            super(false);
            this.image = image;
        }
        
        public void setImageClip(Rectangle imageClip) {
            this.imageClip = imageClip;
        }
        
        @Override
        protected void doPaint(Graphics2D g, Object object, int width,
                int height) {
            if ((imageClip == null) || (imageClip.width <= 0) || (imageClip.height <= 0)) return;
            if (imageClip.x + width >= image.getWidth()) return;
            if (imageClip.y + height >= image.getWidth()) return;
            Image subImage = image.getSubimage(
                    imageClip.x, imageClip.y, 
                    width, height);
            g.drawImage(subImage, 0, 0, width, height, null);
            
        }
        
    }
    

    //  ----------------- Transparent gradient on default (swingx) rendering label

    
    /**
     * Use transparent gradient painter for value-based background highlighting
     * with SwingX extended default renderer. Shared by table and list with
     * striping.
     */
    public void interactiveNumberProportionalGradientHighlightPlusStriping() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        ComponentProvider<JLabel> controller = new LabelProvider(
                JLabel.RIGHT) ;
        final ValueBasedGradientHighlighter gradientHighlighter = createTransparentGradientHighlighter();
        gradientHighlighter.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        Highlighter alternateRowHighlighter = HighlighterFactory.createSimpleStriping();
        table.addHighlighter(alternateRowHighlighter);
        table.addHighlighter(gradientHighlighter);
        // re-use component controller and highlighter in a JXList
        JXList list = new JXList(createListNumberModel(), true);
        list.setCellRenderer(new DefaultListRenderer(controller));
        list.addHighlighter(alternateRowHighlighter);
        list.addHighlighter(gradientHighlighter);
        list.toggleSortOrder();
        final JXFrame frame = wrapWithScrollingInFrame(table, list,
                "transparent value relative highlighting plus striping");
        addStatusMessage(frame,
                "uses a PainterAwareLabel in renderer");
        // crude binding to play with options - the factory is incomplete...
        addStatusComponent(frame, createTransparencyToggle(gradientHighlighter));
        show(frame);
    }

    /**
     * Use transparent gradient painter for value-based background highlighting
     * with SwingX extended default renderer. Shared by table and list with
     * background color.
     */
    public void interactiveNumberProportionalGradientHighlight() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        table.setBackground(HighlighterFactory.LEDGER);
        ComponentProvider<JLabel> controller = new LabelProvider(
                JLabel.RIGHT);
//        table.setDefaultRenderer(Number.class, new DefaultTableRenderer(
//                controller));
        ValueBasedGradientHighlighter gradientHighlighter = createTransparentGradientHighlighter();
        gradientHighlighter.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        table.addHighlighter(gradientHighlighter);
        // re-use component controller and highlighter in a JXList
        JXList list = new JXList(createListNumberModel(), true);
        list.setBackground(table.getBackground());
        list.setCellRenderer(new DefaultListRenderer(controller));
        list.addHighlighter(gradientHighlighter);
        list.toggleSortOrder();
        JXFrame frame = wrapWithScrollingInFrame(table, list,
                "transparent value relative highlighting");
        addStatusMessage(frame,
                "uses the default painter-aware label in renderer");
        // crude binding to play with options - the factory is incomplete...
        addStatusComponent(frame, createTransparencyToggle(gradientHighlighter));
        show(frame);
    }

    //--------- hack around missing size proportional painters
    
    public static class RelativePainter<T> extends AbstractLayoutPainter<T> {

        private Painter<? super T> painter;
        private double xFactor;
        private double yFactor;

        public RelativePainter() {
            this(null);
        }
        
        public void setPainter(Painter<? super T> painter) {
            Object old = getPainter();
            this.painter = painter;
            firePropertyChange("painter", old, getPainter());
        }
        
        public Painter<? super T> getPainter() {
            return painter;
        }
        
        public RelativePainter(Painter<? super T> delegate) {
            this.painter = delegate;
        }
        
        public void setXFactor(double xPercent) {
            double old = getXFactor();
            this.xFactor = xPercent;
            firePropertyChange("xFactor", old, getXFactor());
        }
        
        /**
         * @return
         */
        private double getXFactor() {
            return xFactor;
        }

        public void setYFactor(double yPercent) {
            this.yFactor = yPercent;
        }
        @Override
        protected void doPaint(Graphics2D g, T object, int width, int height) {
            if (painter == null) return;
            // use epsilon
            if (xFactor != 0.0) {
                int oldWidth = width;
                width = (int) (xFactor * width);
                if (getHorizontalAlignment() == HorizontalAlignment.RIGHT) {
                    g.translate(oldWidth - width, 0);
                }
            }
            if (yFactor != 0.0) {
                int oldHeight = height;
                height = (int) (yFactor * height);
                if (getVerticalAlignment() == VerticalAlignment.BOTTOM) {
                    g.translate(0, oldHeight - height);
                }
            }
            
            painter.paint(g, object, width, height);
        }
        
    }
    // -------------------- Value-based transparent gradient highlighter

    /**
     * A Highlighter which applies a value-proportional gradient to PainterAware
     * rendering components if the value is a Number. The gradient is a simple
     * red to red.brigther paint. The red can be toggled to
     * half-transparent.<p>
     * 
     */
    public static class ValueBasedGradientHighlighter extends
            PainterHighlighter {
        float maxValue = 100;

        private boolean yellowTransparent;

        public ValueBasedGradientHighlighter() {
            this(false);
        }
        
        /**
         * @param b
         */
        public ValueBasedGradientHighlighter(boolean b) {
            super(new RelativePainter<JComponent>());
            setYellowTransparent(b);
        }
        
        /**
         * Overridden to do nothing if it's not a RelativePainter. We 
         * roll our own.
         */
        @Override
        public void setPainter(Painter painter) {
            if (!(painter instanceof RelativePainter)) return; 
            super.setPainter(painter);
        }

        @Override
        public RelativePainter getPainter() {
            return (RelativePainter) super.getPainter();
        }

        @Override
        protected Component doHighlight(Component renderer,
                ComponentAdapter adapter) {
            float end = getEndOfGradient((Number) adapter.getValue());
            if (end > 1) {
                renderer.setBackground(Color.RED.darker());
                renderer.setForeground(Color.WHITE);
            } else if (end > 0.02) {
                Painter painter = getPainter(end);
                ((PainterAware) renderer).setPainter(painter);
            }
            return renderer;
        }

        
        @Override
        protected boolean canHighlight(Component component,
                ComponentAdapter adapter) {
            return (adapter.getValue() instanceof Number) && 
                super.canHighlight(component, adapter);
        }

        /**
         * @param yellowTransparent
         */
        public void setYellowTransparent(boolean yellowTransparent) {
            if (this.yellowTransparent == yellowTransparent) return;
            this.yellowTransparent = yellowTransparent;
            getPainter().setPainter(null);
            fireStateChanged();
        }

        public void setHorizontalAlignment(HorizontalAlignment align) {
            getPainter().setHorizontalAlignment(align);
            fireStateChanged();
        }
        
        public HorizontalAlignment getHorizontalAlignment() {
            return getPainter().getHorizontalAlignment();
        }
        
        private Painter getPainter(float end) {
            if (getPainter().getPainter() == null) {
                Color startColor = getTransparentColor(Color.RED,
                        yellowTransparent ? 125 : 254);
                Color endColor = getTransparentColor(Color.RED.brighter(), 0);
                boolean isRightAligned = HorizontalAlignment.RIGHT == getHorizontalAlignment();
                GradientPaint paint = new GradientPaint(new Point2D.Double(0, 0),
                        isRightAligned ? endColor : startColor, 
                        new Point2D.Double(100, 0), 
                        isRightAligned ? startColor : endColor);
                MattePainter painter = new MattePainter(paint);
                painter.setPaintStretched(true);
                getPainter().setPainter(painter);
            } 
            getPainter().setXFactor(end);
            return getPainter();
        }

 
        private Color getTransparentColor(Color base, int transparency) {
            return new Color(base.getRed(), base.getGreen(), base.getBlue(),
                    transparency);
        }

        private float getEndOfGradient(Number number) {
            float end = number.floatValue() / maxValue;
            return end;
        }
       
    }

    
    
    /**
     * A Highlighter which applies a value-proportional gradient to PainterAware
     * rendering components if the value is a Number. The gradient is a simple
     * red to red.brigther paint. The red can be toggled to
     * half-transparent.<p>
     * 
     */
    public static class ValueBasedPositionalHighlighter extends
            PainterHighlighter {
        float maxValue = 100;

        public ValueBasedPositionalHighlighter() {
            super(new RelativePainter<JComponent>());
        }
        
        /**
         * Overridden to do nothing if it's not a RelativePainter. We 
         * roll our own.
         */
        @Override
        public void setPainter(Painter painter) {
            if (!(painter instanceof RelativePainter)) return; 
            super.setPainter(painter);
        }
        
        @Override
        public RelativePainter getPainter() {
            return (RelativePainter) super.getPainter();
        }

        public void setDelegatePainter(Painter painter) {
            getPainter().setPainter(painter);
        }
        
        @Override
        protected Component doHighlight(Component renderer,
                ComponentAdapter adapter) {
            float end = getEndOfGradient((Number) adapter.getValue());
            if (end > 1) {
                renderer.setBackground(Color.RED.darker());
                renderer.setForeground(Color.WHITE);
            } else if (end > 0.02) {
                Painter painter = getPainter(end);
                ((PainterAware) renderer).setPainter(painter);
            }
            return renderer;
        }

        @Override
        protected boolean canHighlight(Component component,
                ComponentAdapter adapter) {
            return (adapter.getValue() instanceof Number) && 
                super.canHighlight(component, adapter);
        }

        public void setHorizontalAlignment(HorizontalAlignment align) {
            getPainter().setHorizontalAlignment(align);
            fireStateChanged();
        }
        
        public HorizontalAlignment getHorizontalAlignment() {
            return getPainter().getHorizontalAlignment();
        }
        
        private Painter getPainter(float end) {
            getPainter().setXFactor(end);
            return getPainter();
        }
        private float getEndOfGradient(Number number) {
            float end = number.floatValue() / maxValue;
            return end;
        }
        
    }

    /**
     * creates and returns a highlighter with a value-based transparent gradient
     * if the cell content type is a Number.
     * 
     * @return
     */
    private ValueBasedGradientHighlighter createTransparentGradientHighlighter() {
        return new ValueBasedGradientHighlighter();
    }

    /**
     * Creates and returns a checkbox to toggle the gradient's yellow
     * transparency.
     * 
     * @param gradientHighlighter
     * @return
     */
    private JCheckBox createTransparencyToggle(
            final ValueBasedGradientHighlighter gradientHighlighter) {
        ActionContainerFactory factory = new ActionContainerFactory();
        // toggle opaque optimatization
        AbstractActionExt toggleTransparent = new AbstractActionExt(
                "yellow transparent") {

            public void actionPerformed(ActionEvent e) {
                gradientHighlighter.setYellowTransparent(isSelected());
            }

        };
        toggleTransparent.setStateAction();
        JCheckBox box = new JCheckBox();
        factory.configureButton(box, toggleTransparent, null);
        return box;
    }

//----------------- Utility    
    /**
     * 
     * @return a ListModel wrapped around the AncientSwingTeam's Number column.
     */
    private ListModel createListNumberModel() {
        AncientSwingTeam tableModel = new AncientSwingTeam();
        int colorColumn = 3;
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            model.addElement(tableModel.getValueAt(i, colorColumn));
        }
        return model;
    }

    /**
     * Fills the list with a collection of actions (as returned from the 
     * table's column control). Binds space and double-click to toggle
     * the action's selected state.
     * 
     * note: this is just an example to show-off the button renderer in a list!
     * ... it's very dirty!!
     * 
     * @param list
     * @param table
     */
    private void configureList(final JXList list, final JXTable table, boolean useRollover) {
        final List<Action> actions = new ArrayList<Action>();
        @SuppressWarnings("all")
        ColumnControlButton columnControl = new ColumnControlButton(table) {

            @Override
            protected void addVisibilityActionItems() {
                actions.addAll(Collections
                        .unmodifiableList(getColumnVisibilityActions()));
            }

        };
        list.setModel(createListeningListModel(actions));
        // action toggling selected state of selected list item
        final Action toggleSelected = new AbstractActionExt(
                "toggle column visibility") {

            public void actionPerformed(ActionEvent e) {
                if (list.isSelectionEmpty())
                    return;
                AbstractActionExt selectedItem = (AbstractActionExt) list
                        .getSelectedValue();
                selectedItem.setSelected(!selectedItem.isSelected());
            }

        };
        if (useRollover) {
            list.setRolloverEnabled(true);
        } else {
            // bind action to space
            list.getInputMap().put(KeyStroke.getKeyStroke("SPACE"),
                    "toggleSelectedActionState");
        }
        list.getActionMap().put("toggleSelectedActionState", toggleSelected);
        // bind action to double-click
        MouseAdapter adapter = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    toggleSelected.actionPerformed(null);
                }
            }

        };
        list.addMouseListener(adapter);

    }

    /**
     * Creates and returns a ListModel containing the given actions. 
     * Registers a PropertyChangeListener with each action to get
     * notified and fire ListEvents.
     * 
     * @param actions the actions to add into the model.
     * @return the filled model.
     */
    private ListModel createListeningListModel(final List<Action> actions) {
        final DefaultListModel model = new DefaultListModel() {

            DefaultListModel reallyThis = this;
            @Override
            public void addElement(Object obj) {
                super.addElement(obj);
                ((Action) obj).addPropertyChangeListener(l);
                
            }
            
            PropertyChangeListener l = new PropertyChangeListener() {
                
                public void propertyChange(PropertyChangeEvent evt) {
                    int index = indexOf(evt.getSource());
                    if (index >= 0) {
                        fireContentsChanged(reallyThis, index, index);
                    }
                }
                
            };
        };
        for (Action action : actions) {
            model.addElement(action);
        }
        return model;
    }

    /**
     * do-nothing method - suppress warning if there are no other
     * test fixtures to run.
     *
     */
    public void testDummy() {
        
    }

}
