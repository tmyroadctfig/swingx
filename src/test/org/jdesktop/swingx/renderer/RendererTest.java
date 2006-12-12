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
import java.io.Serializable;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.InteractiveTestCase;

/**
 * Tests behaviour of SwingX renderers. Currently: mostly characterization to
 * guarantee that they behave similar to the standard.
 * 
 * @author Jeanette Winzenburg
 */
public class RendererTest extends InteractiveTestCase {

    
    
    private JTable table;
    private int coreColumn;
    private DefaultTableCellRenderer coreRenderer;
    private int xColumn;
    private DefaultTableCellRendererExt xRenderer;
    
    @Override
    protected void setUp() throws Exception {
        table = new JTable(10, 2);
        coreColumn = 0; 
        coreRenderer = new DefaultTableCellRenderer();
        table.getColumnModel().getColumn(coreColumn).setCellRenderer(coreRenderer);
        xColumn = 1;
        xRenderer = new DefaultTableCellRendererExt();
        table.getColumnModel().getColumn(xColumn).setCellRenderer(xRenderer);
    }

    /**
     * characterize opaqueness of rendering components.
     *
     */
    public void testOpaqueRenderer() {
        // sanity
        assertFalse(new JLabel().isOpaque());
        assertTrue(coreRenderer.isOpaque());
        assertTrue(xRenderer.rendererComponent.isOpaque());
    }
    
    /**
     * characterize opaqueness of rendering components.
     * 
     * that's useless: the opaque magic only applies if parent != null
     */
    public void testOpaqueRendererComponent() {
        // sanity
        assertFalse(new JLabel().isOpaque());
        Component coreComponent = table.prepareRenderer(coreRenderer, 0, coreColumn);
        // prepare extended
        assertTrue(coreComponent.isOpaque());
        Component xComponent = table.prepareRenderer(xRenderer, 0, xColumn);
        assertTrue(xComponent.isOpaque());
    }
    /**
     * base interaction with table: focused, not-selected and editable 
     * uses UI colors.
     *
     */
    public void testTableRendererExtFocusedNotSelectedEditable() {
        // sanity
        assertTrue(table.isCellEditable(0, coreColumn));
        // access ui colors
        Color uiForeground = UIManager.getColor("Table.focusCellForeground");
        Color uiBackground = UIManager.getColor("Table.focusCellBackground");
        // sanity
        assertNotNull(uiForeground);
        assertNotNull(uiBackground);
        Color background = Color.MAGENTA;
        Color foreground = Color.YELLOW;
        // prepare standard
        coreRenderer.setBackground(background);
        coreRenderer.setForeground(foreground);
        // need to prepare directly - focus is true only if table is focusowner
        Component coreComponent = coreRenderer.getTableCellRendererComponent(table, 
                null, false, true, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(uiBackground, coreComponent.getBackground());
        assertEquals(uiForeground, coreComponent.getForeground());
        // prepare extended
        xRenderer.setBackground(background);
        xRenderer.setForeground(foreground);
        Component xComponent = xRenderer.getTableCellRendererComponent(table, 
                null, false, true, 0, xColumn);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
    }
    
    /**
     * base interaction with table: custom color of renderer precedes
     * table color.
     *
     */
    public void testTableRendererExtCustomColor() {
        Color background = Color.MAGENTA;
        Color foreground = Color.YELLOW;
        // prepare standard
        coreRenderer.setBackground(background);
        coreRenderer.setForeground(foreground);
        Component coreComponent = table.prepareRenderer(coreRenderer, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(background, coreComponent.getBackground());
        assertEquals(foreground, coreComponent.getForeground());
        // prepare extended
        xRenderer.setBackground(background);
        xRenderer.setForeground(foreground);
        Component xComponent = table.prepareRenderer(xRenderer, 0, xColumn);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
    }

    /**
     * base interaction with table: renderer uses table's selection color.
     *
     */
    public void testTableRendererExtSelectedColors() {
        // select first row
        table.setRowSelectionInterval(0, 0);
        // prepare standard
        Component coreComponent = table.prepareRenderer(coreRenderer, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(table.getSelectionBackground(), coreComponent.getBackground());
        assertEquals(table.getSelectionForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = table.prepareRenderer(xRenderer, 0, xColumn);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
    }
    
    /**
     * base interaction with table: renderer uses table's custom selection color.
     *
     */
    public void testTableRendererExtTableSelectedColors() {
        Color background = Color.MAGENTA;
        Color foreground = Color.YELLOW;
        table.setSelectionBackground(background);
        table.setSelectionForeground(foreground);
        // select first row
        table.setRowSelectionInterval(0, 0);
        // prepare standard
        Component coreComponent = table.prepareRenderer(coreRenderer, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(table.getSelectionBackground(), coreComponent.getBackground());
        assertEquals(table.getSelectionForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = table.prepareRenderer(xRenderer, 0, xColumn);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
    }

    /**
     * base interaction with table: renderer uses table's unselected colors.
     *
     */
    public void testTableRendererExtColors() {
        // prepare standard
        Component coreComponent = table.prepareRenderer(coreRenderer, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(table.getBackground(), coreComponent.getBackground());
        assertEquals(table.getForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = table.prepareRenderer(xRenderer, 0, xColumn);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
    }
    
    /**
     * base interaction with table: renderer uses table's unselected custom colors
     * 
     *
     */
    public void testTableRendererExtTableColors() {
        Color background = Color.MAGENTA;
        Color foreground = Color.YELLOW;
        table.setBackground(background);
        table.setForeground(foreground);
        // prepare standard
        Component coreComponent = table.prepareRenderer(coreRenderer, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(table.getBackground(), coreComponent.getBackground());
        assertEquals(table.getForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = table.prepareRenderer(xRenderer, 0, xColumn);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
    }
    /**
     * base existence/type tests while adding DefaultTableCellRendererExt.
     *
     */
    public void testTableRendererExt() {
        DefaultTableCellRendererExt renderer = new DefaultTableCellRendererExt();
        assertTrue(renderer instanceof TableCellRenderer);
        assertTrue(renderer instanceof Serializable);
        
    }
}
