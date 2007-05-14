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
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.ConditionalHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.test.AncientSwingTeam;

/**
 * Examples of interplay of Highlighters and 
 * extended Swingx renderers.
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
          test.runInteractiveTests();
//         test.runInteractiveTests(".*Table.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }
    
    /**
     * Example to highlight against a value/color map.
     * PENDING: adjust to new Highlighters
     */
    public void interactiveColorValueMappedHighlighter() {
        JXTable table = new JXTable(new AncientSwingTeam());
        // build a quick color lookup to simulate multi-value value-based
        // coloring
        int numberColumn = 3;
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
        ConditionalHighlighter highlighter = new ConditionalHighlighter() {

            @Override
            protected Color computeUnselectedBackground(Component renderer,
                    ComponentAdapter adapter) {
                return lookup.get(adapter.getFilteredValueAt(adapter.row,
                        testColumn));
            }

            /**
             * Traditional: Override test to look-up if the value is mapped to a
             * color.
             */
            @Override
            protected boolean test(ComponentAdapter adapter) {
                if (adapter.isTestable(testColumn)) {
                    Object value = adapter.getFilteredValueAt(adapter.row,
                            testColumn);
                    return lookup.containsKey(value);
                }
                return false;
            }

        };
        highlighter.setTestColumnIndex(numberColumn);
        highlighter.setHighlightColumnIndex(-1);
        table.addHighlighter(highlighter);
        showWithScrollingInFrame(table,
                "conditional highlighter with value-based color mapping");
    }
    
    /**
     * do-nothing method - suppress warning if there are no other
     * test fixtures to run.
     *
     */
    public void testDummy() {
        
    }

//------------------ helper

}
