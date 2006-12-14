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

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.jdesktop.swingx.RolloverRenderer;


/**
 * Abstract base class of all extended TableCellRenderers in SwingX.
 * <p>
 * 
 * 
 * @author Jeanette Winzenburg
 * 
 * @see RendererLabel
 * 
 */
public class DefaultListRenderer <T extends JComponent>
        implements ListCellRenderer,  RolloverRenderer, Serializable {

    private RendererController rendererContext;
    private CellContext<JList> cellContext;
    
    public DefaultListRenderer() {
        this((RenderingComponentController<T>)null);
    }
    /**
     * @param context
     */
    public DefaultListRenderer(RenderingComponentController<T> context) {
        this.rendererContext = new RendererController<T, JList>(context);
        this.cellContext = new ListCellContext();
    }


    /**
     * @param context
     */
    public DefaultListRenderer(RendererController context) {
        this.rendererContext = context;
        this.cellContext = new ListCellContext();
    }
    // -------------- implements javax.swing.table.TableCellRenderer
    /**
     * 
     * Returns the default table cell renderer.
     * 
     * @param table the <code>JTable</code>
     * @param value the value to assign to the cell at
     *        <code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus true if cell has focus
     * @param row the row of the cell to render
     * @param column the column of the cell to render
     * @return the default table cell renderer
     */
     public Component getListCellRendererComponent(JList list, Object value, 
             int index, boolean isSelected, boolean cellHasFocus) {
        cellContext.installContext(list, value, index, 0, isSelected, cellHasFocus,
                true, true);
        rendererContext.configure(cellContext);
        return rendererContext.getRendererComponent();
    }
    /**
     * @param background
     */
    public void setBackground(Color background) {
        rendererContext.setBackground(background);
        
    }
    /**
     * @param foreground
     */
    public void setForeground(Color foreground) {
        rendererContext.setForeground(foreground);
    }

//----------------- RolloverRenderer
    
    public void doClick() {
        rendererContext.doClick();
        
    }
    public boolean isEnabled() {
        return rendererContext.isEnabled();
    }


}


