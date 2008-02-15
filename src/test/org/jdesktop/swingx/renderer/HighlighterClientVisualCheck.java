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
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXSearchPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.RolloverProducer;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.BorderHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.PatternMatcher;
import org.jdesktop.swingx.decorator.PatternPredicate;
import org.jdesktop.swingx.decorator.ShadingColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate.ColumnHighlightPredicate;
import org.jdesktop.swingx.renderer.PainterVisualCheck.ValueBasedGradientHighlighter;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.test.AncientSwingTeam;

/**
 * visual checks of highlighter clients. Mostly by example of JXTable.
 * 
 * @author Jeanette Winzenburg
 */
public class HighlighterClientVisualCheck extends InteractiveTestCase {
    protected TableModel tableModel;
    protected Color background = Color.RED;
    protected Color foreground = Color.BLUE;
    
    
    public static void main(String args[]) {
//      setSystemLF(true);
      HighlighterClientVisualCheck test = new HighlighterClientVisualCheck();
      try {
//         test.runInteractiveTests();
//         test.runInteractiveTests(".*Tool.*");
         test.runInteractiveTests(".*JP.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }

    /**
     * Show variants of border Highlighters.
     *
     */
    public void interactiveTableBorderHighlighter() {
        JXTable table = new JXTable(tableModel);
        table.setVisibleRowCount(table.getRowCount());
        table.setVisibleColumnCount(7);
        table.packAll();
        table.setColumnControlVisible(true);
        
        BorderHighlighter outer = new BorderHighlighter(new HighlightPredicate.ColumnHighlightPredicate(1),
                BorderFactory.createLineBorder(Color.RED, 3));
        BorderHighlighter inner = new BorderHighlighter(new HighlightPredicate.ColumnHighlightPredicate(2),
                BorderFactory.createLineBorder(Color.RED, 3), true, true);
        BorderHighlighter replace = new BorderHighlighter(new HighlightPredicate.ColumnHighlightPredicate(0),
                BorderFactory.createLineBorder(Color.RED, 3), false, true);
        table.setHighlighters(outer, inner, replace);
        showWithScrollingInFrame(table, "Border Highlighters");
    }

    /**
     * Multiple Highlighters (shown as example in Javapolis 2007).
     *
     */
    public void interactiveTablePatternHighlighterJP() {
        JXTable table = new JXTable(tableModel);
        table.setVisibleRowCount(table.getRowCount());
        table.setVisibleColumnCount(7);
        table.packAll();
        table.setColumnControlVisible(true);
        
        Font font = table.getFont().deriveFont(Font.BOLD | Font.ITALIC);
        Highlighter simpleStriping = HighlighterFactory.createSimpleStriping();
        Pattern pattern = Pattern.compile("^M", 0);
        PatternPredicate patternPredicate = new PatternPredicate(pattern, 1);
        ColorHighlighter magenta = new ColorHighlighter(patternPredicate, null,
                Color.MAGENTA, null, Color.MAGENTA);
        FontHighlighter derivedFont = new FontHighlighter(font,
                patternPredicate);
        Highlighter gradient = new ValueBasedGradientHighlighter();
        Highlighter shading = new ShadingColorHighlighter(
                new HighlightPredicate.ColumnHighlightPredicate(1));

        table.setHighlighters(simpleStriping,
                magenta,
                derivedFont,
                shading,
                gradient);
        showWithScrollingInFrame(table, "Multiple Highlighters");
    }

    /**
     * Simulates table by one-list per column.
     * 
     * NOTE: the only purpose is to demonstrate the similarity
     * of highlighter usage across collection components!
     * (shown as example in Javapolis 2007)
     * 
     * @see #interactiveTablePatternHighlighterJP()
     */
    public void interactiveListPatternHighlighterJP() {
        JXTable source = new JXTable(tableModel);
        source.toggleSortOrder(3);
        Font font = source.getFont().deriveFont(Font.BOLD | Font.ITALIC);
        Highlighter simpleStriping = HighlighterFactory.createSimpleStriping();
        Pattern pattern = Pattern.compile("^M", 0);
        PatternPredicate patternPredicate = new PatternPredicate(pattern, 0);
        ColorHighlighter magenta = new ColorHighlighter(patternPredicate, null,
                Color.MAGENTA, null, Color.MAGENTA);
        FontHighlighter derivedFont = new FontHighlighter(font,
                patternPredicate);
        Highlighter gradient = new ValueBasedGradientHighlighter(true);
        Highlighter shading = new ShadingColorHighlighter(
                new HighlightPredicate.ColumnHighlightPredicate(0));
        // create and configure one JXList per column.
        List<JXList> lists = new ArrayList<JXList>();
        // first name
        JXList list0 = new JXList(createListModel(source, 0));
        list0.setHighlighters(simpleStriping);
        lists.add(list0);
        // last name
        JXList list1 = new JXList(createListModel(source, 1));
        list1.setHighlighters(simpleStriping, magenta, derivedFont, shading);
        lists.add(list1);

        // color
        JXList listc = new JXList(createListModel(source, 2));
        listc.setHighlighters(simpleStriping);
        lists.add(listc);

        // number
        JXList listn = new JXList(createListModel(source, 3));
        listn.setCellRenderer(new DefaultListRenderer(
                FormatStringValue.NUMBER_TO_STRING, JLabel.RIGHT));
        listn.setHighlighters(simpleStriping, gradient);
        lists.add(listn);

        // boolean
        JXList listb = new JXList(createListModel(source, 4));
        listb.setCellRenderer(new DefaultListRenderer(new ButtonProvider()));
        listb.setFixedCellHeight(list0.getPreferredSize().height
                / source.getRowCount());
        listb.setHighlighters(simpleStriping, magenta, derivedFont, gradient);
        lists.add(listb);

        JComponent panel = Box.createHorizontalBox();
        for (JXList l : lists) {
            listb.setVisibleRowCount(source.getRowCount());
            l.setFont(source.getFont());
            panel.add(new JScrollPane(l));
        }
        showInFrame(panel, "Multiple Highlighters");
    }

    
    /**
     * @param tableModel2
     * @param i
     * @return
     */
    private ListModel createListModel(final JXTable tableModel, final int i) {
        ListModel listModel = new AbstractListModel(){

            public Object getElementAt(int index) {
                return tableModel.getValueAt(index, i);
            }

            public int getSize() {
                return tableModel.getRowCount();
            }};
        return listModel ;
    }


    public static class FontHighlighter extends AbstractHighlighter {
        
        private Font font;
        public FontHighlighter(Font font, HighlightPredicate predicate) {
            super(predicate);
            this.font = font;
        }
        @Override
        protected Component doHighlight(Component component,
                ComponentAdapter adapter) {
            component.setFont(font);
            return component;
        }
        
    }
    /**
     * Example from forum requirement: highlight all rows of a given group
     * if mouse if over one of them.
     * 
     * PENDING: need to track row view-model coordinates mapping after
     * filtering/sorting.
     *
     */
    public void interactiveRolloverRowGroup() {
        TableModel model = new AncientSwingTeam();
       final List<Integer> rowSet = new ArrayList<Integer>();
       for (int i = 0; i < model.getRowCount(); i++) {
         if ((Integer)model.getValueAt(i, 3) > 10) {
             rowSet.add(i);
         }
       }
       final HighlightPredicate predicate = new HighlightPredicate() {

        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            return rowSet.contains(adapter.row);
        }
           
       };
       final ColorHighlighter highlighter = new ColorHighlighter(HighlightPredicate.NEVER, Color.YELLOW, 
               null);
       JXTable table = new JXTable(model);
       table.addHighlighter(highlighter);
       PropertyChangeListener l = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
            Point location = (Point) evt.getNewValue();
            int row = -1;
            if (location != null) {
                row = location.y;
            }
            if (rowSet.contains(row)) {
                highlighter.setHighlightPredicate(predicate);
            } else {
                highlighter.setHighlightPredicate(HighlightPredicate.NEVER);
            }
        }
           
       };
       table.addPropertyChangeListener(RolloverProducer.ROLLOVER_KEY, l);
       showWithScrollingInFrame(table, "rollover highlight of row groups");
    }
    
    /**
     * Example to highlight against a value/color map.
     * Here the control is in predicate. <p>
     * 
     */
    public void interactiveColorValueMappedHighlighterPredicate() {
        JXTable table = new JXTable(new AncientSwingTeam());
        // build a quick color lookup to simulate multi-value value-based
        // coloring
        final int numberColumn = 3;
        table.toggleSortOrder(numberColumn);
        Color[] colors = new Color[] { Color.YELLOW, Color.CYAN, Color.MAGENTA,
                Color.GREEN };
        int rowsPerColor = (table.getRowCount() - 5) / colors.length;
        Map<Color, HighlightPredicate> map = new HashMap<Color, HighlightPredicate>();
        for (int i = 0; i < colors.length; i++) {
            List<Integer> values = new ArrayList<Integer>();
            for (int j = 0; j < rowsPerColor; j++) {
                values.add((Integer) table.getValueAt(i * rowsPerColor + j, numberColumn));
            }
            map.put(colors[i], new ValueMappedHighlightPredicate(values, numberColumn));
        }
        // create one ColorHighlighter for each color/predicate pair and 
        // add to a compoundHighlighter
        CompoundHighlighter chl = new CompoundHighlighter();
        for (Color color : colors) {
            chl.addHighlighter(new ColorHighlighter(map.get(color), color, null));
        }
        table.resetSortOrder();
        table.addHighlighter(chl);
        showWithScrollingInFrame(table,
                "compound highlighter with value-based color mapping predicate");
    }
    
    /**
     * Custom predicate which returns true if the filtered cell value
     * of a given testColumn is contained in a list of values.
     * PENDING: logic similar to search/pattern, enough to abstract?
     */
    public static class ValueMappedHighlightPredicate implements HighlightPredicate {

        private List values;
        private int testColumn;
        public ValueMappedHighlightPredicate(List values, int testColumn) {
            this.values = values;
            this.testColumn = testColumn;
        }
        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            return values.contains(adapter.getValue(testColumn));
        }
        
    }
    /**
     * Example to highlight against a value/color map. <p>
     * Here the Highlighter takes full control. Which is a bit 
     * on the border line of 
     * the intended distribution of responsibility between
     * Highlighter and HighlighterPredicate.
     */
    public void interactiveColorValueMappedHighlighter() {
        JXTable table = new JXTable(new AncientSwingTeam());
        // build a quick color lookup to simulate multi-value value-based
        // coloring
        final int numberColumn = 3;
        table.toggleSortOrder(numberColumn);
        final Map<Integer, Color> lookup = new HashMap<Integer, Color>();
        Color[] colors = new Color[] { Color.YELLOW, Color.CYAN, Color.MAGENTA,
                Color.GREEN };
        int rowsPerColor = (table.getRowCount() - 5) / colors.length;
        for (int i = 0; i < colors.length; i++) {
            for (int j = 0; j < rowsPerColor; j++) {
                lookup.put((Integer) table.getValueAt(i * rowsPerColor + j,
                        numberColumn), colors[i]);
            }
        }
        table.resetSortOrder();
        Highlighter hl = new ColorHighlighter() {

            @Override
            protected void applyBackground(Component renderer, ComponentAdapter adapter) {
                if (adapter.isSelected()) return;
                Color background = lookup.get(adapter.getValue(numberColumn));
                if (background != null) {
                    renderer.setBackground(background);
                }
            }
        };
        table.addHighlighter(hl);
        showWithScrollingInFrame(table,
                "conditional highlighter with value-based color mapping");
    }

    /**
     * test to see searchPanel functionality in new Highlighter api
     * 
     */
    public void interactiveSearchPanel() {
        JXTable table = new JXTable(tableModel);
        final ColorHighlighter cl = new ColorHighlighter(new PatternPredicate(null, 0), null,
                Color.RED);
        table.addHighlighter(cl);
        JXSearchPanel searchPanel = new JXSearchPanel();
        PatternMatcher patternMatcher = new PatternMatcher() {
            public Pattern getPattern() {
                return getPatternPredicate().getPattern();
            }

            public void setPattern(Pattern pattern) {
                PatternPredicate old = getPatternPredicate();
                cl.setHighlightPredicate(new PatternPredicate(pattern, old
                        .getTestColumn(), old.getHighlightColumn()));
            }
            
            private PatternPredicate getPatternPredicate() {
                return (PatternPredicate) cl.getHighlightPredicate();
            }

        };
        searchPanel.addPatternMatcher(patternMatcher);
        JXFrame frame = wrapWithScrollingInFrame(table,
                "Pattern highlighting col 0");
        getStatusBar(frame).add(searchPanel);
        frame.setVisible(true);
    }
    
//----------------- custom PatternMatcher
    
    /**
     * columm shading (was: hierarchicalColumnHighlighter)
     *
     */
    public void interactiveColumnShading() {
        JXTreeTable treeTable = new JXTreeTable(new FileSystemModel());
        // simulate hierarchicalColumnHighlighter
        int hierarchicalColumn = 0;
        for (int i = 0; i < treeTable.getColumnCount(); i++) {
            if (treeTable.isHierarchical(i)) {
                hierarchicalColumn = i;
                break;
            }
        }
        ColorHighlighter shader = new ColorHighlighter(new ColumnHighlightPredicate(hierarchicalColumn), Color.WHITE,
                null) {

            @Override
            protected void applyBackground(Component renderer,
                    ComponentAdapter adapter) {
                if (adapter.isSelected() || getBackground() == null) return;
                renderer.setBackground(computeBackgroundSeed(getBackground()));
            }
            
            protected Color computeBackgroundSeed(Color seed) {
                return new Color(Math.max((int)(seed.getRed()  * 0.95), 0),
                                 Math.max((int)(seed.getGreen()* 0.95), 0),
                                 Math.max((int)(seed.getBlue() * 0.95), 0));
            }

        };
        treeTable.addHighlighter(shader);
        showWithScrollingInFrame(treeTable, "hierarchical column");
        
    }
    
    /**
     * Classic lineprinter striping and hyperlink (LF only, no action).
     * 
     */
    public void interactiveTableAlternateAndHyperlink() {
        JXTable table = new JXTable(tableModel);
        table.setRowHeight(22);
        table.getColumn(1).setCellRenderer(
                new DefaultTableRenderer(new HyperlinkProvider()));
        table.addHighlighter(HighlighterFactory
                .createSimpleStriping(HighlighterFactory.CLASSIC_LINE_PRINTER));
        JFrame frame = wrapWithScrollingInFrame(table,
                "classic lineprinter and hyperlink on column 1");
        frame.setVisible(true);
    }


    /**
     * LinePrinter striping and rollover.
     *
     */
    public void interactiveTableAlternateAndRollover() {
        JXTable table = new JXTable(tableModel);
        table.setRowHeight(22);
        table.setHighlighters(
            HighlighterFactory.createSimpleStriping(HighlighterFactory.LINE_PRINTER),
            new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.YELLOW, null));
        showWithScrollingInFrame(table, "LinePrinter plus yellow rollover");
    }

    /**
     * Foreground highlight on column 1 and 3.
     *
     */
    public void interactiveColumnForeground() {
        JXTable table = new JXTable(tableModel);
        HighlightPredicate predicate = new ColumnHighlightPredicate(1, 3);
        table.addHighlighter(new ColorHighlighter(predicate, null, Color.BLUE));
        showWithScrollingInFrame(table, "Foreground highlight col 1 and 3");
    }


    /**
     * ColorHighlighter with pattern predicate
     *
     */
    public void interactiveTablePatternHighlighter() {
        JXTable table = new JXTable(tableModel);
        table.setColumnControlVisible(true);
        Pattern pattern = Pattern.compile("^M", 0);
        table.addHighlighter(new ColorHighlighter(new PatternPredicate(pattern, 1), null, 
                Color.red));
        showWithScrollingInFrame(table, "Pattern: highlight row if ^M col 1");
    }

    /**
     * Issue #258-swingx: Background LegacyHighlighter must not change custom
     * foreground.
     * <p>
     * 
     * Use SwingX extended default renderer.
     */
    public void interactiveTableCustomRendererColor() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        DefaultTableRenderer renderer = new DefaultTableRenderer();
        renderer.setForeground(foreground);
        renderer.setBackground(background);
        table.addHighlighter(HighlighterFactory.createAlternateStriping(Color.WHITE, HighlighterFactory.GENERIC_GRAY));
        table.setDefaultRenderer(Object.class, renderer);
        JXTable nohighlight = new JXTable(model);
        nohighlight.setDefaultRenderer(Object.class, renderer);
        showWithScrollingInFrame(table, nohighlight,
                "ext: custom colored renderer with bg highlighter <--> shared without highl");
    }
    
    /**
     * Requirement from forum: value based color and tooltip.
     * 
     * Here the tooltip is regarded as visual decoration and 
     * set in a specialized Highlighter. 
     *
     */
    public void interactiveValueBasedToolTipAndColorOnHighlighter() {
        JXTable table = new JXTable(new AncientSwingTeam());
        HighlightPredicate predicate = new HighlightPredicate() {

            public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                if (!(adapter.getValue() instanceof Number)) return false;
                return ((Number) adapter.getValue()).intValue() < 10;
            }
            
        };
        ColorHighlighter hl = new ColorHighlighter(
                predicate, null, Color.RED, null, Color.RED);
        // THINK this is possible, but maybe not the correct place 
        // ... more on the what-side of "what vs. how" ?
        Highlighter tl = new AbstractHighlighter(predicate) {

            @Override
            protected Component doHighlight(Component component, ComponentAdapter adapter) {
                String text = "low on luck: " + ((JLabel) component).getText();
                ((JComponent) component).setToolTipText(text);
                return component;
            }
            @Override
            protected boolean canHighlight(Component component,
                    ComponentAdapter adapter) {
                return component instanceof JLabel;
            }
            
        };
        table.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.GENERIC_GRAY),
                hl, tl);
        showWithScrollingInFrame(table, "Value-based Tooltip ... on Highlighter");
    }

    /**
     * Requirement from forum: value based color and tooltip.<p>
     * 
     * Here the tooltip is regarded as belonging more to the "what"
     * of rendering and set in a custom provider. The implication
     * is that the logic (whether to show the tooltip or not) is
     * duplicated (in the predicate and the provider.
     * 
     *
     */
    public void interactiveValueBasedToolTipAndColorOnProvider() {
        JXTable table = new JXTable(new AncientSwingTeam());
        HighlightPredicate predicate = new HighlightPredicate() {

            public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                if (!(adapter.getValue() instanceof Number)) return false;
                return ((Number) adapter.getValue()).intValue() < 10;
            }
            
        };
        ColorHighlighter hl = new ColorHighlighter(
                predicate, null, Color.RED, null, Color.RED);
        table.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.GENERIC_GRAY),
                hl); //, tl);
        // here: set's value based .. this duplicates logic of 
        // predicate
        LabelProvider provider = new LabelProvider() {

            
            @Override
            protected void configureState(CellContext context) {
                super.configureState(context);
                rendererComponent.setToolTipText(getToolTipText(context));
            }


            private String getToolTipText(CellContext context) {
                if ((context.getValue() instanceof Number))  {
                    int luck = ((Number) context.getValue()).intValue();
                    if (luck < 10) {
                        return "low on luck: " + luck;
                    }
                }
                return null;
            }
            
        };
        provider.setHorizontalAlignment(JLabel.RIGHT);
        table.setDefaultRenderer(Number.class, new DefaultTableRenderer(provider));
        showWithScrollingInFrame(table, "Value-based Tooltip ... on provider");
    }

    /**
     * Example of custom predicate based on the component's value, 
     * (as opposed to on the value of the adapter). 
     * 
     * 
     */
    public void interactiveTableColorBasedOnComponentValue() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        table.setForeground(Color.GREEN);
        HighlightPredicate predicate = new HighlightPredicate() {

            public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                if (!(renderer instanceof JLabel)) return false;
                String text = ((JLabel) renderer).getText();
                 return text.contains("y");
            }
            
        };
        ColorHighlighter hl = new ColorHighlighter(predicate, null, Color.RED);
        table.addHighlighter(HighlighterFactory.createSimpleStriping(HighlighterFactory.GENERIC_GRAY));
        table.addHighlighter(hl);
        showWithScrollingInFrame(table, 
                "component value-based rendering (label text contains y) ");
    }


    //------------------ rollover
    
    /**
     * Issue #503-swingx: custom cursor respected when rollover?
     * Seems okay for table, 
     */
    public void interactiveRolloverHighlightCustomCursor() {
        JXTable table = new JXTable(tableModel);
        table.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        table.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.YELLOW, null));
        showWithScrollingInFrame(table, "rollover highlight, custom cursor");
    }

    /**
     * Plain RolloverHighlighter. 
     * Issue #513-swingx: no rollover effect for disabled table.
     *
     */
    public void interactiveRolloverHighlight() {
        final JXTable table = new JXTable(tableModel);
        ColorHighlighter colorHighlighter = new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.YELLOW, null);
        table.addHighlighter(colorHighlighter);
        Action action = new AbstractAction("toggle table enabled") {

            public void actionPerformed(ActionEvent e) {
                table.setEnabled(!table.isEnabled());
                
            }
            
        };
        JXFrame frame = showWithScrollingInFrame(table, "rollover highlight, enabled/disabled table");
        addAction(frame, action);
        frame.pack();
    }
    

//--------------------- factory    
    /**
     * shows the effect of a simple striping highlighter on a 
     * colored table.
     *
     */
    public void interactiveSimpleStriping() {
        JXTable table = new JXTable(tableModel);
        table.setVisibleRowCount(table.getRowCount() + 3);
        table.setBackground(Color.YELLOW);
        table.addHighlighter(HighlighterFactory.createSimpleStriping(Color.LIGHT_GRAY));
        showWithScrollingInFrame(table, "Simple gray striping");
    }

    /**
     * shows the effect of a simple striping highlighter on a 
     * colored table.
     *
     */
    public void interactiveSimpleStripingGroup() {
        JXTable table = new JXTable(tableModel);
        table.setVisibleRowCount(table.getRowCount() + 3);
        table.setBackground(Color.YELLOW);
        table.addHighlighter(HighlighterFactory.createSimpleStriping(Color.LIGHT_GRAY, 3));
        showWithScrollingInFrame(table, "Simple gray striping, grouped by 3");
    }
    /**
     * shows the effect of a simple striping highlighter on a 
     * colored table.
     *
     */
    public void interactiveAlternateStriping() {
        JXTable table = new JXTable(tableModel);
        table.setVisibleRowCount(table.getRowCount() + 3);
        table.setBackground(Color.YELLOW);
        table.addHighlighter(HighlighterFactory.createAlternateStriping(Color.WHITE, Color.LIGHT_GRAY));
        showWithScrollingInFrame(table, "Alternate white/gray striping");
    }
    
    /**
     * shows the effect of a simple striping highlighter on a 
     * colored table.
     *
     */
    public void interactiveAlternateStripingGroup() {
        JXTable table = new JXTable(tableModel);
        table.setVisibleRowCount(table.getRowCount() + 3);
        table.setBackground(Color.YELLOW);
        table.addHighlighter(HighlighterFactory.createAlternateStriping(Color.WHITE, Color.LIGHT_GRAY, 3));
        showWithScrollingInFrame(table, "Alternate white/gray striping");
    }

    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
         tableModel = new AncientSwingTeam();
     }

    /**
     * Do nothing, make the test runner happy
     * (would output a warning without a test fixture).
     *
     */
    public void testDummy() {
        
    }
}
