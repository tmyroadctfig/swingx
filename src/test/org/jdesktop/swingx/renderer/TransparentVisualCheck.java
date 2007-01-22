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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.action.ActionContainerFactory;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.ConditionalHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter.UIAlternateRowHighlighter;
import org.jdesktop.swingx.painter.IconPainter;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.gradient.BasicGradientPainter;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jdesktop.test.AncientSwingTeam;

/**
 * Experiments with transparent highlighters.<p>
 * 
 * Links
 * <ul>
 * <li> <a href="">Sneak preview II - Transparent Highlighter</a>
 * </ul>
 * 
 * @author Jeanette Winzenburg
 */
public class TransparentVisualCheck extends InteractiveTestCase {
    public static void main(String args[]) {
//      setSystemLF(true);
      TransparentVisualCheck test = new TransparentVisualCheck();
      try {
//         test.runInteractiveTests();
         test.runInteractiveTests(".*Icon.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }

//------------------------ Transparent painter aware button as rendering
    
    /**
     * Use a custom button controller to show both checkbox icon and text to
     * render Actions in a JXList. Use a painter-aware checkbox.
     */
    public void interactiveTableWithListColumnControl() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        JXList list = new JXList();
        Highlighter highlighter = new UIAlternateRowHighlighter();
        table.addHighlighter(highlighter);
        list.setHighlighters(highlighter, new GradientHighlighter());
        // quick-fill and hook to table columns' visibility state
        configureList(list, table, false);
        // a custom rendering button controller showing both checkbox and text
        RenderingButtonController wrapper = new RenderingButtonController() {

            
            @Override
            protected AbstractButton createRendererComponent() {
                return new PainterAwareCheckBox();
            }

            @Override
            protected void format(CellContext context) {
                if (!(context.getValue() instanceof AbstractActionExt)) {
                    super.format(context);
                    return;
                }
                rendererComponent.setSelected(((AbstractActionExt) context.getValue()).isSelected());
                rendererComponent.setText(((AbstractActionExt) context.getValue()).getName());
            }
            
        };
        wrapper.setHorizontalAlignment(JLabel.LEADING);
        list.setCellRenderer(new DefaultListRenderer(wrapper));
        JXFrame frame = showWithScrollingInFrame(table, list,
                "checkbox list-renderer - striping and gradient");
        addStatusMessage(frame, "fake editable list: space/doubleclick on selected item toggles column visibility");
        frame.pack();
    }

    public static class PainterAwareCheckBox extends JCheckBox implements PainterAware {
        Painter painter;

        public void setPainter(Painter painter) {
            this.painter = painter;
            if (painter != null) {
                // ui maps to !opaque
                // Note: this is incomplete - need to keep track of the 
                // "real" contentfilled property
                setContentAreaFilled(false);
            } 
        }
        


        @Override
        protected void paintComponent(Graphics g) {
            if (painter != null) {
                // we have a custom (background) painter
                // try to inject if possible
                // there's no guarantee - some LFs have their own background 
                // handling  elsewhere
                    paintComponentWithPainter((Graphics2D) g);
            } else {
                // no painter - delegate to super
                super.paintComponent(g);
            }
        }

        /**
         * PRE: painter != null
         * @param g
         */
        protected void paintComponentWithPainter(Graphics2D g) {
            // 1. be sure to fill the background
            // 2. paint the painter
            // by-pass ui.update and hook into ui.paint directly
            if (ui != null) {
                Graphics scratchGraphics = (g == null) ? null : g.create();
                try {
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                    painter.paint(g, this);
                    ui.paint(scratchGraphics, this);
                }
                finally {
                    scratchGraphics.dispose();
                }
            }
            
        }
        /**
         * Overridden for performance reasons.<p>
         * PENDING: Think about Painters and opaqueness?
         * 
         */
//        public boolean isOpaque() { 
//            Color back = getBackground();
//            Component p = getParent(); 
//            if (p != null) { 
//                p = p.getParent(); 
//            }
//            // p should now be the JTable. 
//            boolean colorMatch = (back != null) && (p != null) && 
//                back.equals(p.getBackground()) && 
//                            p.isOpaque();
//            return !colorMatch && super.isOpaque(); 
//        }
        
    }
    
    public static class GradientHighlighter extends Highlighter {
        float maxValue = 100;
        private Painter painter;
        private boolean yellowTransparent;

        /**
         */
        public GradientHighlighter() {
            super(Color.YELLOW, null);
        }

        /**
         * @param b
         */
        public void setYellowTransparent(boolean b) {
            this.yellowTransparent = b;
        }

        @Override
        public Component highlight(Component renderer,
                ComponentAdapter adapter) {
            if (renderer instanceof PainterAware) {
                Painter painter = getPainter(0.7f);
                ((PainterAware) renderer).setPainter(painter);
                
            } else {
                    renderer.setBackground(Color.YELLOW.darker());
                }
                return renderer;
         }

        private Painter getPainter(float end) {
            if (painter == null) {
                Color startColor = getTransparentColor(Color.YELLOW, yellowTransparent ? 
                        125 : 254);
                Color endColor = getTransparentColor(Color.WHITE, 0);
             painter = new BasicGradientPainter(0.0f, 0.0f,
                    startColor, end, 0.f, endColor);
        }
            return painter;
        }

        private Color getTransparentColor(Color base, int transparency) {
            return new Color(base.getRed(), base.getGreen(), base.getBlue(), transparency);
        }
 
        
    }

//------------------------
    
    /**
     * Use GradientPainter for value-based background highlighting with SwingX
     * extended default renderer. Trying to get the highlighter transparent:
     * the background color of the cell should shine through in the "white"
     * region of the value-hint. This uses a PainterAwareLabel as rendering 
     * component
     */
    public void interactiveIconPainterHighlight() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        RenderingComponentController<JLabel> numberRendering = new RenderingLabelController(
                JLabel.RIGHT) {
                    @Override
                    protected JLabel createRendererComponent() {
                        return new PainterAwareLabel();
                    }
            
        };
        table.getColumn(0).setCellRenderer(new DefaultTableRenderer(
                numberRendering));
        ImageIcon icon = new ImageIcon(JXPanel.class.getResource("resources/images/kleopatra.jpg"));
        final ImagePainter imagePainter = new ImagePainter(icon.getImage());
        final IconPainter painter = new IconPainter(icon);
        Highlighter gradientHighlighter = new Highlighter() {

            @Override
            public Component highlight(Component renderer, ComponentAdapter adapter) {
                if ((adapter.column == 0) && (renderer instanceof PainterAware)){
                    ((PainterAware) renderer).setPainter(imagePainter);
                }
                return renderer;
            }
            
        };
        Highlighter alternateRowHighlighter = new UIAlternateRowHighlighter();
        table.addHighlighter(alternateRowHighlighter);
        table.addHighlighter(gradientHighlighter);
        // re-use component controller and highlighter in a JXList
        JXList list = new JXList(createListNumberModel(), true);
        list.setCellRenderer(new DefaultListRenderer(numberRendering));
        list.addHighlighter(alternateRowHighlighter);
        list.addHighlighter(gradientHighlighter);
        list.toggleSortOrder();
        final JXFrame frame = showWithScrollingInFrame(table, list,
                "transparent value relative highlighting plus striping");
        addStatusMessage(frame,
                "uses a PainterAwareLabel in renderer");
        frame.pack();
    }


//  ---------------------- Transparent gradients on PainterAwareLabel
    
    /**
     * Use GradientPainter for value-based background highlighting with SwingX
     * extended default renderer. Trying to get the highlighter transparent:
     * the background color of the cell should shine through in the "white"
     * region of the value-hint. This uses a PainterAwareLabel as rendering 
     * component
     */
    public void interactiveTransparentGradientHighlightPlusStriping() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        table.setBackground(Highlighter.ledgerBackground.getBackground());
        RenderingComponentController<JLabel> numberRendering = new RenderingLabelController(
                JLabel.RIGHT) {
                    @Override
                    protected JLabel createRendererComponent() {
                        return new PainterAwareLabel();
                    }
            
        };
        table.setDefaultRenderer(Number.class, new DefaultTableRenderer(
                numberRendering));
        final TransparentGradientHighlighter gradientHighlighter = createTransparentGradientHighlighter();
        Highlighter alternateRowHighlighter = new UIAlternateRowHighlighter();
        table.addHighlighter(alternateRowHighlighter);
        table.addHighlighter(gradientHighlighter);
        // re-use component controller and highlighter in a JXList
        JXList list = new JXList(createListNumberModel(), true);
        list.setCellRenderer(new DefaultListRenderer(numberRendering));
        list.addHighlighter(alternateRowHighlighter);
        list.addHighlighter(gradientHighlighter);
        list.toggleSortOrder();
        final JXFrame frame = showWithScrollingInFrame(table, list,
                "transparent value relative highlighting plus striping");
        addStatusMessage(frame,
                "uses a PainterAwareLabel in renderer");
        // crude binding to play with options - the factory is incomplete...
        ActionContainerFactory factory = new ActionContainerFactory();
        // toggle opaque optimatization
        AbstractActionExt overrideOpaque = new AbstractActionExt("yellow transparent") {

            public void actionPerformed(ActionEvent e) {
                gradientHighlighter.setYellowTransparent(isSelected());
                frame.repaint();
            }
            
        };
        overrideOpaque.setStateAction();
        JCheckBox box = new JCheckBox();
        factory.configureButton(box, overrideOpaque, null);
        getStatusBar(frame).add(box);
        frame.pack();
    }

    /**
     * Use GradientPainter for value-based background highlighting with SwingX
     * extended default renderer. Trying to get the highlighter transparent:
     * the background color of the cell should shine through in the "white"
     * region of the value-hint. This uses a PainterAwareLabel as rendering 
     * component
     */
    public void interactiveTransparentGradientHighlight() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        table.setBackground(Highlighter.ledgerBackground.getBackground());
        RenderingComponentController<JLabel> numberRendering = new RenderingLabelController(
                JLabel.RIGHT) {
                    @Override
                    protected JLabel createRendererComponent() {
                        return new PainterAwareLabel();
                    }
            
        };
        table.setDefaultRenderer(Number.class, new DefaultTableRenderer(
                numberRendering));
        Highlighter gradientHighlighter = createTransparentGradientHighlighter();
        table.addHighlighter(gradientHighlighter);
        // re-use component controller and highlighter in a JXList
        JXList list = new JXList(createListNumberModel(), true);
        list.setBackground(table.getBackground());
        list.setCellRenderer(new DefaultListRenderer(numberRendering));
        list.addHighlighter(gradientHighlighter);
        list.toggleSortOrder();
        JXFrame frame = showWithScrollingInFrame(table, list,
                "transparent value relative highlighting");
        addStatusMessage(frame,
                "uses a PainterAwareLabel in renderer");
        frame.pack();
    }


    public static class PainterAwareLabel extends JRendererLabel {
        @Override
        protected void paintComponent(Graphics g) {
            if (painter != null) {
                // we have a custom (background) painter
                // try to inject if possible
                // there's no guarantee - some LFs have their own background 
                // handling  elsewhere
                if (isOpaque()) {
                    // replace the paintComponent completely 
                    paintComponentWithPainter((Graphics2D) g);
                } else {
                    // transparent apply the background painter before calling super
                    painter.paint((Graphics2D) g, this);
                    super.paintComponent(g);
                }
            } else {
                // nothing to worry about - delegate to super
                super.paintComponent(g);
            }
        }

        /**
         * PRE: painter != null, isOpaque()
         * @param g
         */
        protected void paintComponentWithPainter(Graphics2D g) {
            // 1. be sure to fill the background
            // 2. paint the painter
            // by-pass ui.update and hook into ui.paint directly
            if (ui != null) {
                Graphics scratchGraphics = (g == null) ? null : g.create();
                try {
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                    painter.paint(g, this);
                    ui.paint(scratchGraphics, this);
                }
                finally {
                    scratchGraphics.dispose();
                }
            }
            
        }

        /**
         * called from super.paint - overridden to do nothing if the painter
         * is called in super.paint.
         */
        @Override
        protected void paintPainter(Graphics2D g) {
        }
        
        
        
        
    }
    
//----------------- Transparent gradient on default (swingx) rendering label
    /**
         * Use GradientPainter for value-based background highlighting with SwingX
         * extended default renderer. Trying to get the highlighter transparent:
         * the background color of the cell should shine through in the "white"
         * region of the value-hint.
         */
        public void interactiveTableAndListNumberProportionalGradientHighlight() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        table.setBackground(Highlighter.ledgerBackground.getBackground());
        RenderingComponentController<JLabel> numberRendering = new RenderingLabelController(
                JLabel.RIGHT);
        table.setDefaultRenderer(Number.class, new DefaultTableRenderer(
                numberRendering));
        Highlighter gradientHighlighter = createTransparentGradientHighlighter();
        table.addHighlighter(gradientHighlighter);
        // re-use component controller and highlighter in a JXList
        JXList list = new JXList(createListNumberModel(), true);
        list.setBackground(table.getBackground());
        list.setCellRenderer(new DefaultListRenderer(numberRendering));
        list.addHighlighter(gradientHighlighter);
        list.toggleSortOrder();
        JXFrame frame = showWithScrollingInFrame(table, list,
                "transparent value relative highlighting");
        addStatusMessage(frame,
                "uses the default painter-aware label in renderer");
        frame.pack();
    }


//---------------- Transparent renderer on experimenting rendering label
        
        /**
         * Use GradientPainter for value-based background highlighting with SwingX
         * extended default renderer. Trying to get the highlighter transparent:
         * the background color of the cell should shine through in the "white"
         * region of the value-hint.
         */
        public void interactiveNumberProportionalGradientHighlightExperimentWithStriping() {
            TableModel model = new AncientSwingTeam();
            JXTable table = new JXTable(model);
            // dirty, dirty - but I want to play with the options later on ...
            final RenderingLabel label = new RenderingLabel();
            RenderingComponentController<JLabel> numberRendering = new RenderingLabelController(
                    JLabel.RIGHT) {

                        @Override
                        protected JLabel createRendererComponent() {
                            return label;
                        }
                
            };
            table.setDefaultRenderer(Number.class, new DefaultTableRenderer(
                    numberRendering));
            Highlighter gradientHighlighter = createTransparentGradientHighlighter();
            Highlighter alternateRowHighlighter = new UIAlternateRowHighlighter();
            table.addHighlighter(alternateRowHighlighter);
            table.addHighlighter(gradientHighlighter);
            // re-use component controller and highlighter in a JXList
            JXList list = new JXList(createListNumberModel(), true);
            list.setCellRenderer(new DefaultListRenderer(numberRendering));
            list.addHighlighter(alternateRowHighlighter);
            list.addHighlighter(gradientHighlighter);
            list.toggleSortOrder();
            final JXFrame frame = showWithScrollingInFrame(table, list,
                    "transparent value relative highlighting - with striping");
            addStatusMessage(frame,
                    "uses the play-with painter-aware label");
            
            // crude binding to play with options - the factory is incomplete...
            ActionContainerFactory factory = new ActionContainerFactory();
            // toggle opaque optimatization
            AbstractActionExt overrideOpaque = new AbstractActionExt("plain opque") {

                public void actionPerformed(ActionEvent e) {
                    label.overrideSuperIsOpaque = isSelected();
                    frame.repaint();
                }
                
            };
            overrideOpaque.setStateAction();
            JCheckBox box = new JCheckBox();
            factory.configureButton(box, overrideOpaque, null);
            getStatusBar(frame).add(box);
            // call painter in paintComponent
            AbstractActionExt paintComponent = new AbstractActionExt("paintComponent") {

                public void actionPerformed(ActionEvent e) {
                    label.overrideSuperPainter = isSelected();
                    frame.repaint();
                }
                
            };
            paintComponent.setStateAction();
            box = new JCheckBox();
            factory.configureButton(box, paintComponent, null);
            getStatusBar(frame).add(box);
            // call painter in paintComponent
            AbstractActionExt opaqueDepends = new AbstractActionExt("opaqueDepends") {

                public void actionPerformed(ActionEvent e) {
                    label.adjustOpaqueWithPainter = isSelected();
                    frame.repaint();
                }
                
            };
            opaqueDepends.setStateAction();
            box = new JCheckBox();
            factory.configureButton(box, opaqueDepends, null);
            getStatusBar(frame).add(box);

            frame.pack();
        }
      


    /**
     * Use GradientPainter for value-based background highlighting with SwingX
     * extended default renderer. Trying to get the highlighter transparent:
     * the background color of the cell should shine through in the "white"
     * region of the value-hint.
     */
    public void interactiveTableAndListNumberProportionalGradientHighlightExperiment() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        table.setBackground(Highlighter.ledgerBackground.getBackground());
        // dirty, dirty - but I want to play with the options later on ...
        final RenderingLabel label = new RenderingLabel();
        RenderingComponentController<JLabel> numberRendering = new RenderingLabelController(
                JLabel.RIGHT) {

                    @Override
                    protected JLabel createRendererComponent() {
                        return label;
                    }
            
        };
        table.setDefaultRenderer(Number.class, new DefaultTableRenderer(
                numberRendering));
        Highlighter gradientHighlighter = createTransparentGradientHighlighter();
        table.addHighlighter(gradientHighlighter);
        // re-use component controller and highlighter in a JXList
        JXList list = new JXList(createListNumberModel(), true);
        list.setBackground(table.getBackground());
        list.setCellRenderer(new DefaultListRenderer(numberRendering));
        list.addHighlighter(gradientHighlighter);
        list.toggleSortOrder();
        final JXFrame frame = showWithScrollingInFrame(table, list,
                "transparent value relative highlighting");
        addStatusMessage(frame,
                "uses the play-with painter-aware label");
        
        // crude binding to play with options - the factory is incomplete...
        ActionContainerFactory factory = new ActionContainerFactory();
        // toggle opaque optimatization
        AbstractActionExt overrideOpaque = new AbstractActionExt("plain opque") {

            public void actionPerformed(ActionEvent e) {
                label.overrideSuperIsOpaque = isSelected();
                frame.repaint();
            }
            
        };
        overrideOpaque.setStateAction();
        JCheckBox box = new JCheckBox();
        factory.configureButton(box, overrideOpaque, null);
        getStatusBar(frame).add(box);
        // call painter in paintComponent
        AbstractActionExt paintComponent = new AbstractActionExt("paintComponent") {

            public void actionPerformed(ActionEvent e) {
                label.overrideSuperPainter = isSelected();
                frame.repaint();
            }
            
        };
        paintComponent.setStateAction();
        box = new JCheckBox();
        factory.configureButton(box, paintComponent, null);
        getStatusBar(frame).add(box);
        // call painter in paintComponent
        AbstractActionExt opaqueDepends = new AbstractActionExt("opaqueDepends") {

            public void actionPerformed(ActionEvent e) {
                label.adjustOpaqueWithPainter = isSelected();
                frame.repaint();
            }
            
        };
        opaqueDepends.setStateAction();
        box = new JCheckBox();
        factory.configureButton(box, opaqueDepends, null);
        getStatusBar(frame).add(box);

        frame.pack();
    }
  
    /**
     * to play with screws to make transparency work.
     */
    public static class RenderingLabel extends JRendererLabel {

        private boolean opaque;
        private boolean overrideSuperIsOpaque;
        private boolean overrideSuperPainter;
        private boolean adjustOpaqueWithPainter;

        public RenderingLabel() {
            super();
            // the "real" flag
            setOpaque(true);
        }

        @Override
        public void setOpaque(boolean opaque) {
            this.opaque = opaque;
            super.setOpaque(opaque);
        }
        
        @Override
        public boolean isOpaque() { 
            if (overrideSuperIsOpaque) {
                // super does some optimization which might (or might not) interfere
                return opaque;
            }
            return super.isOpaque();
        }

        
        @Override
        public void setPainter(Painter painter) {
            super.setPainter(painter);
            if (adjustOpaqueWithPainter) {
                if (painter != null) {
                    setOpaque(false);
                } else {
                    setOpaque(true);
                }
            } else {
                setOpaque(true);
            }
        }

        @Override
        public void paint(Graphics g) {
            // super calls the painter before calling its super.paint
            super.paint(g);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintPainter((Graphics2D)g, true);
        }

        /**
         * called from super.paint - overridden to do nothing if the painter
         * is called in super.paint.
         */
        @Override
        protected void paintPainter(Graphics2D g) {
            if (overrideSuperPainter) return;
            super.paintPainter(g);
        }
        
        /** 
         * called from paintComponent.
         * @param g
         * @param dummy
         */
        protected void paintPainter(Graphics2D g, boolean dummy) {
            if (!overrideSuperPainter) return;
            if (painter != null) {
                painter.paint(g, this);
            }
        }
    }

//-------------------- transparent gradient highlighter  
    
    public static class TransparentGradientHighlighter extends ConditionalHighlighter {
        float maxValue = 100;
        private Painter painter;
        private boolean yellowTransparent;

        /**
         */
        public TransparentGradientHighlighter() {
            super(null, null, -1, -1);
        }

        /**
         * @param b
         */
        public void setYellowTransparent(boolean b) {
            this.yellowTransparent = b;
        }

        @Override
        public Component highlight(Component renderer,
                ComponentAdapter adapter) {
            boolean highlight = needsHighlight(adapter);
            if (highlight && (renderer instanceof PainterAware)) {
                float end = getEndOfGradient((Number) adapter.getValue());
                if (end > 1) {
                    renderer.setBackground(Color.YELLOW.darker());
                } else if (end > 0.02) {
                    Painter painter = getPainter(end);
                    ((PainterAware) renderer).setPainter(painter);
                }
                return renderer;
            }
            return renderer;
        }

        private Painter getPainter(float end) {
                Color startColor = getTransparentColor(Color.YELLOW, yellowTransparent ? 
                        125 : 254);
                Color endColor = getTransparentColor(Color.WHITE, 0);
             painter = new BasicGradientPainter(0.0f, 0.0f,
                    startColor, end, 0.f, endColor);
            return painter;
        }

        private Color getTransparentColor(Color base, int transparency) {
            return new Color(base.getRed(), base.getGreen(), base.getBlue(), transparency);
        }
        private float getEndOfGradient(Number number) {
            float end = number.floatValue() / maxValue;
            return end;
        }

        @Override
        protected boolean test(ComponentAdapter adapter) {
            return adapter.getValue() instanceof Number;
        }

        
    }
    /**
     * creates and returns a highlighter with a value-based transparent 
     * gradient if the cell content type is a Number.  
     * 
     * @return 
     */
    private TransparentGradientHighlighter createTransparentGradientHighlighter() {
        return new TransparentGradientHighlighter();
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
        final List<Action> actions = new ArrayList();
        ColumnControlButton columnControl = new ColumnControlButton(table, null) {

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


}
