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

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.ListModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.ConditionalHighlighter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.gradient.BasicGradientPainter;
import org.jdesktop.test.AncientSwingTeam;

/**
 * Examples of interplay of Highlighters and extended Swingx renderers.
 * 
 * @author Jeanette Winzenburg
 */
public class HighlighterVisualCheck extends InteractiveTestCase {
    protected Color background = Color.RED;
    protected Color foreground = Color.BLUE;

    public static void main(String args[]) {
//      setSystemLF(true);
      HighlighterVisualCheck test = new HighlighterVisualCheck();
      try {
         test.runInteractiveTests(".*Table.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }
    /**
     * Use GradientPainter for value-based background highlighting Use SwingX
     * extended default renderer.
     */
    public void interactiveTableAndListNumberProportionalGradientHighlight() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        RenderingComponentController<JLabel> numberRendering = new RenderingLabelController(
                JLabel.RIGHT);
        DefaultTableRenderer renderer = new DefaultTableRenderer<JLabel>(
                numberRendering);
        table.setDefaultRenderer(Number.class, renderer);
        ConditionalHighlighter gradientHighlighter = new ConditionalHighlighter(
                null, null, -1, -1) {
            float maxValue = 100;

            @Override
            public Component highlight(Component renderer,
                    ComponentAdapter adapter) {
                boolean highlight = needsHighlight(adapter);
                if (highlight && (renderer instanceof PainterAware)) {
                    float end = getEndOfGradient((Number) adapter.getValue());
                    if (end > 1) {
                        renderer.setBackground(Color.YELLOW.darker());
                    } else if (end > 0.02) {
                        Painter painter = new BasicGradientPainter(0.0f, 0.0f,
                                Color.YELLOW, end, 0.f, Color.WHITE);
                        ((PainterAware) renderer).setPainter(painter);
                    }
                    return renderer;
                }
                return renderer;
            }

            private float getEndOfGradient(Number number) {
                float end = number.floatValue() / maxValue;
                return end;
            }

            @Override
            protected boolean test(ComponentAdapter adapter) {
                return adapter.getValue() instanceof Number;
            }

        };
        table.addHighlighter(gradientHighlighter);
        // re-use component controller and highlighter in a JXList
        JXList list = new JXList(createListNumberModel(), true);
        list.setCellRenderer(new DefaultListRenderer<JLabel>(numberRendering));
        list.addHighlighter(gradientHighlighter);
        list.toggleSortOrder();
        JXFrame frame = showWithScrollingInFrame(table, list,
                "painter-aware renderer with value relative highlighting");
        addStatusMessage(frame,
                "number column and list share the same rendering component and highlighter");
        frame.pack();
    }
   
    
    /**
     * Use GradientPainter for value-based background highlighting
     * Use SwingX extended default renderer.
     */
    public void interactiveTableGradientHighlight() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        DefaultTableRenderer renderer = DefaultTableRenderer.createDefaultTableRenderer();
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
   
    /**
     * Issue #258-swingx: Background Highlighter must not change custom
     * foreground.
     * <p>
     * 
     * Use SwingX extended default renderer.
     */
    public void interactiveTableCustomRendererColor() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        DefaultTableRenderer renderer = DefaultTableRenderer.createDefaultTableRenderer();
        renderer.setForeground(foreground);
        renderer.setBackground(background);
        table.addHighlighter(AlternateRowHighlighter.genericGrey);
        table.setDefaultRenderer(Object.class, renderer);
        JXTable nohighlight = new JXTable(model);
        nohighlight.setDefaultRenderer(Object.class, renderer);
        showWithScrollingInFrame(table, nohighlight,
                "ext: custom colored renderer with bg highlighter <--> shared without highl");
    }
    

    /**
     * Issue #258-swingx: Background Highlighter must not change custom
     * foreground.
     * <p>
     * 
     * Use SwingX's extended default renderer.<p>
     * 
     * Note: in Swingx' context it's not recommended to change 
     * visual renderer properties on the renderer layer - use
     * a conditional highlighter instead.
     */
    public void interactiveTableCustomRendererColorBasedOnValue() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        RendererController configurator = new RendererController<JLabel, JComponent>(new RenderingLabelController()) {
            @Override
            protected void configureColors(CellContext<JComponent> context) {
                super.configureColors(context);
                if (!context.isSelected()) {
                    if (getStringValue(context).contains("y")) {
                        getRendererComponent().setForeground(Color.RED);
                    } else {
                        getRendererComponent().setForeground(Color.GREEN);
                    }
                }
            }

         

        };
        TableCellRenderer renderer = new DefaultTableRenderer(configurator);
        table.addHighlighter(AlternateRowHighlighter.genericGrey);
        table.setDefaultRenderer(Object.class, renderer);
        JXTable nohighlight = new JXTable(model);
        nohighlight.setDefaultRenderer(Object.class, renderer);
        showWithScrollingInFrame(table, nohighlight,
                "ext: value-based fg renderer with bg highlighter <--> shared without highl");
    }

    /**
     * 
     * Note: in Swingx' context it's not recommended to change 
     * visual renderer properties on the renderer layer - use
     * a conditional highlighter instead. So here is the above 
     * example going the SwingX way.<p>
     * 
     * This is more complicated that it should be ..
     * 
     */
    public void interactiveTableConditionalColorBasedOnValue() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
    
        table.setDefaultRenderer(Object.class, DefaultTableRenderer.createDefaultTableRenderer());
        ConditionalHighlighter highlighter = new ConditionalHighlighter() {
            @Override
            protected void applyForeground(Component renderer, ComponentAdapter adapter) {
                // needsHighlight/test don't have access to the renderer
                // so we check again here
                Color foregroundx;
                if (testAgainstComponent(renderer, adapter)) {
                    foregroundx = Color.RED;
                } else {
                    foregroundx = Color.GREEN;
                }
                renderer.setForeground(foregroundx);
            }

            @Override
            public Component doHighlight(Component renderer, ComponentAdapter adapter) {
                if (adapter.isSelected()) return renderer;
                return super.doHighlight(renderer, adapter);
            }

            private boolean testAgainstComponent(Component renderer, ComponentAdapter adapter) {
                if (!(renderer instanceof JLabel)) return false;
                String text = ((JLabel) renderer).getText();
                 return text.contains("y");
            }

            /**
             * Overridden to always return true. We test against
             * the text property of the label.
             */
            @Override
            protected boolean test(ComponentAdapter adapter) {
                return true;
            }
            
        };
        highlighter.setHighlightColumnIndex(-1);
        highlighter.setTestColumnIndex(-1);
        table.addHighlighter(AlternateRowHighlighter.genericGrey);
        table.addHighlighter(highlighter);
        JXTable nohighlight = new JXTable(model);
        nohighlight.setDefaultRenderer(Object.class, DefaultTableRenderer.createDefaultTableRenderer());
        nohighlight.addHighlighter(highlighter);
        showWithScrollingInFrame(table, nohighlight,
                "value-based rendering by ConditionalHighlighter");
    }

//------------------ helper
    
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


}
