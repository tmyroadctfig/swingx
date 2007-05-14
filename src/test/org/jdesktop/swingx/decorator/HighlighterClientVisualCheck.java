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
package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.HighlightPredicate.ColumnHighlightPredicate;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.test.AncientSwingTeam;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
public class HighlighterClientVisualCheck extends InteractiveTestCase {
    protected Color ledger = new Color(0xF5, 0xFF, 0xF5);
    protected TableModel tableModel;
    
    
    public static void main(String args[]) {
//      setSystemLF(true);
      HighlighterClientVisualCheck test = new HighlighterClientVisualCheck();
      try {
         test.runInteractiveTests();
//         test.runInteractiveTests(".*Table.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }

 
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
        ColorHighlighter shader = new ColorHighlighter(Color.WHITE, null,
                new ColumnHighlightPredicate(hierarchicalColumn)) {

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
                .createSimpleStriping(ColorHighlighter.CLASSIC_LINE_PRINTER));
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
            HighlighterFactory.createSimpleStriping(ColorHighlighter.LINE_PRINTER),
            new ColorHighlighter(Color.YELLOW, null, HighlightPredicate.ROLLOVER_ROW));
        showWithScrollingInFrame(table, "LinePrinter plus yellow rollover");
    }

    /**
     * Foreground highlight on column 1 and 3.
     *
     */
    public void interactiveColumnForeground() {
        JXTable table = new JXTable(tableModel);
        HighlightPredicate predicate = new ColumnHighlightPredicate(1, 3);
        table.addHighlighter(new ColorHighlighter(null, Color.BLUE, predicate));
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
        table.addHighlighter(new ColorHighlighter(null, Color.red, 
                new PatternPredicate(pattern, 1, -1)));
        showWithScrollingInFrame(table, "Pattern: highlight row if ^M col 1");
    }

    //------------------ rollover
    
    /**
     * Issue #503-swingx: custom cursor respected when rollover?
     * Seems okay for table, 
     */
    public void interactiveRolloverHighlightCustomCursor() {
        JXTable table = new JXTable(tableModel);
        table.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        table.addHighlighter(new ColorHighlighter(Color.YELLOW, null, HighlightPredicate.ROLLOVER_ROW));
        showWithScrollingInFrame(table, "rollover highlight, custom cursor");
    }

    /**
     * Plain RolloverHighlighter. 
     *
     */
    public void interactiveRolloverHighlight() {
        JXTable table = new JXTable(tableModel);
        table.addHighlighter(new ColorHighlighter(Color.YELLOW, null, HighlightPredicate.ROLLOVER_ROW));
        showWithScrollingInFrame(table, "rollover highlight");
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

    
    protected void setUp() throws Exception {
        super.setUp();
         tableModel = new AncientSwingTeam();
     }

}
