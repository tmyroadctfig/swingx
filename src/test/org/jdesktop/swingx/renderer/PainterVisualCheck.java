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
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.DefaultListModel;
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
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.gradient.BasicGradientPainter;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jdesktop.test.AncientSwingTeam;

/**
 * Experiments with highlighters using painters.<p>
 * 
 * Links
 * <ul>
 * <li> <a href="">Sneak preview II - Transparent Highlighter</a>
 * </ul>
 * 
 * @author Jeanette Winzenburg
 */
public class PainterVisualCheck extends InteractiveTestCase {
    private static final Logger LOG = Logger
            .getLogger(PainterVisualCheck.class.getName());
    public static void main(String args[]) {
//      setSystemLF(true);
      PainterVisualCheck test = new PainterVisualCheck();
      try {
         test.runInteractiveTests();
//         test.runInteractiveTests(".*Icon.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }

    /**
     * Use GradientPainter for value-based background highlighting
     * Use SwingX extended default renderer.
     */
    public void interactiveTableGradientHighlight() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        DefaultTableRenderer renderer = new DefaultTableRenderer();
        final Painter painter = new BasicGradientPainter(0.0f, 0.0f, Color.YELLOW, 0.75f, (float) 0.5, Color.WHITE);
        ConditionalHighlighter gradientHighlighter = new ConditionalHighlighter(null, null, -1, -1) {

            @Override
            public Component highlight(Component renderer, ComponentAdapter adapter) {
                boolean highlight = needsHighlight(adapter);
                if (highlight && (renderer instanceof PainterAware)) {
                    ((PainterAware) renderer).setPainter(painter);
                    return renderer;
                }
                return renderer;
            }

            @Override
            protected boolean test(ComponentAdapter adapter) {
                return adapter.getValue().toString().contains("y");
            }
            
        };
        table.addHighlighter(gradientHighlighter);
        table.setDefaultRenderer(Object.class, renderer);
        JXFrame frame = showWithScrollingInFrame(table, 
                "painter-aware renderer with value-based highlighting");
        getStatusBar(frame).add(new JLabel("gradient background of cells with value's containing 'y'"));
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
        Highlighter highlighter = new UIAlternateRowHighlighter();
        table.addHighlighter(highlighter);
        list.setHighlighters(highlighter, new GradientHighlighter());
        // quick-fill and hook to table columns' visibility state
        configureList(list, table, false);
        // a custom rendering button controller showing both checkbox and text
        RenderingButtonController wrapper = new RenderingButtonController() {
            @Override
            protected AbstractButton createRendererComponent() {
                return new JRendererCheckBox();
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

    /**
     * A Highlighter which applies a simple yellow to white-transparent 
     * gradient to a PainterAware rendering component. The yellow can
     * be toggled to half-transparent.
     */
    public static class GradientHighlighter extends Highlighter {

        private Painter painter;

        private boolean yellowTransparent;

        /**
         */
        public GradientHighlighter() {
            super(Color.YELLOW, null);
        }

        /**
         * @param yellowTransparent
         */
        public void setYellowTransparent(boolean yellowTransparent) {
            if (this.yellowTransparent == yellowTransparent) return;
            this.yellowTransparent = yellowTransparent;
            painter = null;
            fireStateChanged();
        }

        @Override
        public Component highlight(Component renderer, ComponentAdapter adapter) {
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
                Color startColor = getTransparentColor(Color.YELLOW,
                        yellowTransparent ? 125 : 254);
                Color endColor = getTransparentColor(Color.WHITE, 0);
                painter = new BasicGradientPainter(0.0f, 0.0f, startColor, end,
                        0.f, endColor);
            }
            return painter;
        }

        private Color getTransparentColor(Color base, int transparency) {
            return new Color(base.getRed(), base.getGreen(), base.getBlue(),
                    transparency);
        }

    }

    // ------------------------

    /**
     * Use highlighter with background image painter. Shared by table and list.
     */
    public void interactiveIconPainterHighlight() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        RenderingComponentController<JLabel> controller = new RenderingLabelController(
                JLabel.RIGHT);
        table.getColumn(0).setCellRenderer(
                new DefaultTableRenderer(controller));
        ImageIcon icon = new ImageIcon(JXPanel.class
                .getResource("resources/images/kleopatra.jpg"));
        final ImagePainter imagePainter = new ImagePainter(icon.getImage());
        Highlighter gradientHighlighter = new Highlighter() {

            @Override
            public Component highlight(Component renderer,
                    ComponentAdapter adapter) {
                if ((adapter.column == 0) && (renderer instanceof PainterAware)) {
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
        list.setCellRenderer(new DefaultListRenderer(controller));
        list.addHighlighter(alternateRowHighlighter);
        list.addHighlighter(gradientHighlighter);
        list.toggleSortOrder();
        final JXFrame frame = showWithScrollingInFrame(table, list,
                "image highlighting plus striping");
        frame.pack();
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
        RenderingComponentController<JLabel> controller = new RenderingLabelController(
                JLabel.RIGHT) ;
        table.setDefaultRenderer(Number.class, new DefaultTableRenderer(
                controller));
        final ValueBasedGradientHighlighter gradientHighlighter = createTransparentGradientHighlighter();
        Highlighter alternateRowHighlighter = new UIAlternateRowHighlighter();
        table.addHighlighter(alternateRowHighlighter);
        table.addHighlighter(gradientHighlighter);
        // re-use component controller and highlighter in a JXList
        JXList list = new JXList(createListNumberModel(), true);
        list.setCellRenderer(new DefaultListRenderer(controller));
        list.addHighlighter(alternateRowHighlighter);
        list.addHighlighter(gradientHighlighter);
        list.toggleSortOrder();
        final JXFrame frame = showWithScrollingInFrame(table, list,
                "transparent value relative highlighting plus striping");
        addStatusMessage(frame,
                "uses a PainterAwareLabel in renderer");
        // crude binding to play with options - the factory is incomplete...
        getStatusBar(frame).add(createTransparencyToggle(gradientHighlighter));
        frame.pack();
    }

    /**
     * Use transparent gradient painter for value-based background highlighting
     * with SwingX extended default renderer. Shared by table and list with
     * background color.
     */
    public void interactiveNumberProportionalGradientHighlight() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        table.setBackground(Highlighter.ledgerBackground.getBackground());
        RenderingComponentController<JLabel> controller = new RenderingLabelController(
                JLabel.RIGHT);
        table.setDefaultRenderer(Number.class, new DefaultTableRenderer(
                controller));
        ValueBasedGradientHighlighter gradientHighlighter = createTransparentGradientHighlighter();
        table.addHighlighter(gradientHighlighter);
        // re-use component controller and highlighter in a JXList
        JXList list = new JXList(createListNumberModel(), true);
        list.setBackground(table.getBackground());
        list.setCellRenderer(new DefaultListRenderer(controller));
        list.addHighlighter(gradientHighlighter);
        list.toggleSortOrder();
        JXFrame frame = showWithScrollingInFrame(table, list,
                "transparent value relative highlighting");
        addStatusMessage(frame,
                "uses the default painter-aware label in renderer");
        // crude binding to play with options - the factory is incomplete...
        getStatusBar(frame).add(createTransparencyToggle(gradientHighlighter));
        frame.pack();
    }

    // -------------------- Value-based transparent gradient highlighter

    /**
     * A Highlighter which applies a value-proportional gradient to PainterAware
     * rendering components if the value is a Number. The gradient is a simple
     * yellow to white-transparent paint. The yellow can be toggled to
     * half-transparent.
     */
    public static class ValueBasedGradientHighlighter extends
            ConditionalHighlighter {
        float maxValue = 100;

        private Painter painter;

        private boolean yellowTransparent;

        /**
         */
        public ValueBasedGradientHighlighter() {
            super(null, null, -1, -1);
        }

        /**
         * @param yellowTransparent
         */
        public void setYellowTransparent(boolean yellowTransparent) {
            if (this.yellowTransparent == yellowTransparent) return;
            this.yellowTransparent = yellowTransparent;
            fireStateChanged();
        }

        @Override
        public Component highlight(Component renderer, ComponentAdapter adapter) {
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
            Color startColor = getTransparentColor(Color.YELLOW,
                    yellowTransparent ? 125 : 254);
            Color endColor = getTransparentColor(Color.WHITE, 0);
            painter = new BasicGradientPainter(0.0f, 0.0f, startColor, end,
                    0.f, endColor);
            return painter;
        }

        private Color getTransparentColor(Color base, int transparency) {
            return new Color(base.getRed(), base.getGreen(), base.getBlue(),
                    transparency);
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
