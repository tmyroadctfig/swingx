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

import javax.swing.JComponent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.HighlighterIssues;
import org.jdesktop.test.AncientSwingTeam;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
public class HighlighterVisualCheck extends InteractiveTestCase {
    protected Color background = Color.RED;
    protected Color foreground = Color.BLUE;

    public static void main(String args[]) {
//      setSystemLF(true);
      HighlighterIssues test = new HighlighterIssues();
      try {
         test.runInteractiveTests(".*Table.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
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
     * Use SwingX's extended default renderer.
     */
    public void interactiveTableCustomRendererColorBasedOnValue() {
        TableModel model = new AncientSwingTeam();
        JXTable table = new JXTable(model);
        RendererController configurator = new RendererController<JComponent, JComponent>(null) {
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

}
