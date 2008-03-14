/*
 * $Id$
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jdesktop.swingx.AbstractSearchable;
import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.SearchFactory;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.test.AncientSwingTeam;

/**
 * Testing clients of ComponentAdapter, mainly clients which rely on uniform string 
 * representation across functionality. Not the optimal location, but where woudl
 * that be? 
 * 
 * @author Jeanette Winzenburg
 */
public class ComponentAdapterClientIssues extends InteractiveTestCase {

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on JXTable.
     */
    public void testGetStringUsedInSearch() {
        JXTable table = new JXTable(new AncientSwingTeam());
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return TO_STRING.getString(value);
            }
            
        };
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        String text = sv.getString(table.getValueAt(2, 2));
        int matchRow = table.getSearchable().search(text);
        assertEquals(2, matchRow);
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on JXTable.
     */
    public void testGetStringUsedInSearchPredicate() {
        JXTable table = new JXTable(new AncientSwingTeam());
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return TO_STRING.getString(value);
            }
            
        };
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        table.putClientProperty(AbstractSearchable.MATCH_HIGHLIGHTER, Boolean.TRUE);
        String text = sv.getString(table.getValueAt(2, 2));
        int matchRow = table.getSearchable().search(text);
        Highlighter[] highlighters = table.getHighlighters();
        ColorHighlighter last = (ColorHighlighter) highlighters[highlighters.length - 1];
        assertEquals("sanity - found match", 2, matchRow);
        assertTrue("sanity - use searchPredicate", last.getHighlightPredicate() instanceof SearchPredicate);
        Component comp = table.prepareRenderer(table.getCellRenderer(2, 2), 2, 2);
        assertEquals(last.getBackground(), comp.getBackground());
    }
    
    public void interactiveGetStringUsedInFind() {
        JXTable table = new JXTable(new AncientSwingTeam());
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return TO_STRING.getString(value);
            }
            
        };
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        table.setColumnControlVisible(true);
        Action action = new AbstractAction("toggle batch/incremental"){
            boolean useFindBar;
            public void actionPerformed(ActionEvent e) {
                useFindBar = !useFindBar;
                SearchFactory.getInstance().setUseFindBar(useFindBar);
            }
            
        };
        
        JXFrame frame = wrapWithScrollingInFrame(table, "Batch/Incremental Search");
        addAction(frame, action);
        addMessage(frame, "Press ctrl-F to open search widget");
        show(frame);
    }


}
