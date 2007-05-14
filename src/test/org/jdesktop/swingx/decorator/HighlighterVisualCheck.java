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

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.test.AncientSwingTeam;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
public class HighlighterVisualCheck extends InteractiveTestCase {
    protected Color ledger = new Color(0xF5, 0xFF, 0xF5);
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
     * shows the effect of a simple striping highlighter on a 
     * colored table.
     *
     */
    public void interactiveSimpleStriping() {
        JXTable table = new JXTable(new AncientSwingTeam());
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
        JXTable table = new JXTable(new AncientSwingTeam());
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
        JXTable table = new JXTable(new AncientSwingTeam());
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
        JXTable table = new JXTable(new AncientSwingTeam());
        table.setVisibleRowCount(table.getRowCount() + 3);
        table.setBackground(Color.YELLOW);
        table.addHighlighter(HighlighterFactory.createAlternateStriping(Color.WHITE, Color.LIGHT_GRAY, 3));
        showWithScrollingInFrame(table, "Alternate white/gray striping");
    }

}
