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
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.test.SerializableSupport;

/**
 * Tests behaviour of SwingX renderers. Currently: mostly characterization to
 * guarantee that they behave similar to the standard.
 * 
 * @author Jeanette Winzenburg
 */
public class TableRendererTest extends InteractiveTestCase {

    private static final Logger LOG = Logger.getLogger(TableRendererTest.class
            .getName());
    
    private JTable table;
    private int coreColumn;
    private DefaultTableCellRenderer coreTableRenderer;
    private int xColumn;
    private DefaultTableRenderer xTableRenderer;

    
    @Override
    protected void setUp() throws Exception {
        // setup table
        table = new JTable(10, 2);
        coreColumn = 0; 
        coreTableRenderer = new DefaultTableCellRenderer();
        table.getColumnModel().getColumn(coreColumn).setCellRenderer(coreTableRenderer);
        xColumn = 1;
        xTableRenderer = DefaultTableRenderer.createDefaultTableRenderer();
        table.getColumnModel().getColumn(xColumn).setCellRenderer(xTableRenderer);
        
    }

 
    
    /**
     * test serializable of default renderer.
     * 
     */
    public void testSerializeTableRenderer() {
        TableCellRenderer xListRenderer = new DefaultTableRenderer();
        try {
            SerializableSupport.serialize(xListRenderer);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
}
    /**
     * base interaction with table: focused, not-selected uses UI border.
     * 
     *
     */
    public void testTableFocusSelectedBorder() {
        // sanity to see test test validity
//        UIManager.put("Table.focusSelectedCellHighlightBorder", new LineBorder(Color.red));
        // access ui colors
        Border selectedFocusBorder = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
        // sanity
        if (selectedFocusBorder == null) {
            LOG.info("cannot run focusSelectedBorder - UI has no selected focus border");
            return;
            
        }
        // need to prepare directly - focus is true only if table is focusowner
        JComponent coreComponent = (JComponent) coreTableRenderer.getTableCellRendererComponent(table, 
                null, true, true, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(selectedFocusBorder, coreComponent.getBorder());
        // prepare extended
        JComponent xComponent = (JComponent) xTableRenderer.getTableCellRendererComponent(table, 
                null, true, true, 0, xColumn);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBorder(), xComponent.getBorder());
    }

    /**
     * base interaction with table: focused, not-selected uses UI border.
     * 
     *
     */
    public void testTableFocusBorder() {
        // access ui colors
        Border focusBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
//        Border selectedFocusBorder = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
        // sanity
        assertNotNull(focusBorder);
        // need to prepare directly - focus is true only if table is focusowner
        JComponent coreComponent = (JComponent) coreTableRenderer.getTableCellRendererComponent(table, 
                null, false, true, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(focusBorder, coreComponent.getBorder());
        // prepare extended
        JComponent xComponent = (JComponent) xTableRenderer.getTableCellRendererComponent(table, 
                null, false, true, 0, xColumn);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBorder(), xComponent.getBorder());
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
        coreTableRenderer.setBackground(background);
        coreTableRenderer.setForeground(foreground);
        // need to prepare directly - focus is true only if table is focusowner
        Component coreComponent = coreTableRenderer.getTableCellRendererComponent(table, 
                null, false, true, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(uiBackground, coreComponent.getBackground());
        assertEquals(uiForeground, coreComponent.getForeground());
        // prepare extended
        xTableRenderer.setBackground(background);
        xTableRenderer.setForeground(foreground);
        Component xComponent = xTableRenderer.getTableCellRendererComponent(table, 
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
        coreTableRenderer.setBackground(background);
        coreTableRenderer.setForeground(foreground);
        Component coreComponent = table.prepareRenderer(coreTableRenderer, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(background, coreComponent.getBackground());
        assertEquals(foreground, coreComponent.getForeground());
        // prepare extended
        xTableRenderer.setBackground(background);
        xTableRenderer.setForeground(foreground);
        Component xComponent = table.prepareRenderer(xTableRenderer, 0, xColumn);
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
        Component coreComponent = table.prepareRenderer(coreTableRenderer, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(table.getSelectionBackground(), coreComponent.getBackground());
        assertEquals(table.getSelectionForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = table.prepareRenderer(xTableRenderer, 0, xColumn);
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
        Component coreComponent = table.prepareRenderer(coreTableRenderer, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(table.getSelectionBackground(), coreComponent.getBackground());
        assertEquals(table.getSelectionForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = table.prepareRenderer(xTableRenderer, 0, xColumn);
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
        Component coreComponent = table.prepareRenderer(coreTableRenderer, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(table.getBackground(), coreComponent.getBackground());
        assertEquals(table.getForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = table.prepareRenderer(xTableRenderer, 0, xColumn);
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
        Component coreComponent = table.prepareRenderer(coreTableRenderer, 0, coreColumn);
        // sanity: known standard behaviour
        assertEquals(table.getBackground(), coreComponent.getBackground());
        assertEquals(table.getForeground(), coreComponent.getForeground());
        // prepare extended
        Component xComponent = table.prepareRenderer(xTableRenderer, 0, xColumn);
        // assert behaviour same as standard
        assertEquals(coreComponent.getBackground(), xComponent.getBackground());
        assertEquals(coreComponent.getForeground(), xComponent.getForeground());
    }

    /**
     * characterize opaqueness of rendering components.
     *
     */
    public void testTableOpaqueRenderer() {
        // sanity
        assertFalse(new JLabel().isOpaque());
        assertTrue(coreTableRenderer.isOpaque());
//        assertTrue(xTableRenderer.getRendererComponent().isOpaque());
    }
    
    /**
     * characterize opaqueness of rendering components.
     * 
     * that's useless: the opaque magic only applies if parent != null
     */
    public void testTableOpaqueRendererComponent() {
        // sanity
        assertFalse(new JLabel().isOpaque());
        Component coreComponent = table.prepareRenderer(coreTableRenderer, 0, coreColumn);
        // prepare extended
        assertTrue(coreComponent.isOpaque());
        Component xComponent = table.prepareRenderer(xTableRenderer, 0, xColumn);
        assertTrue(xComponent.isOpaque());
    }


    /**
     * base existence/type tests while adding DefaultTableCellRendererExt.
     *
     */
    public void testTableRendererExt() {
        DefaultTableRenderer renderer = DefaultTableRenderer.createDefaultTableRenderer();
        assertTrue(renderer instanceof TableCellRenderer);
        assertTrue(renderer instanceof Serializable);
        
    }
}
