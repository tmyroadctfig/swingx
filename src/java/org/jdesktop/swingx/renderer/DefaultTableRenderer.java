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

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.RolloverRenderer;


/**
 * Adapter to glue SwingX renderer support to core api.
 * <p>
 * 
 * 
 * @author Jeanette Winzenburg
 * 
 * @see ComponentProvider
 * @see LabelProvider
 * @see CellContext
 * 
 */
public class DefaultTableRenderer 
        implements TableCellRenderer, RolloverRenderer, Serializable {

    protected ComponentProvider componentController;
    private CellContext<JTable> cellContext;
    
    
    /**
     * Instantiates a default table renderer with the default component
     * controller. 
     * 
     */
    public DefaultTableRenderer() {
        this((ComponentProvider) null);
    }

    /**
     * Instantiates a default table renderer with the given componentController.
     * If the controller is null, creates and uses a default. The default
     * controller is of type <code>LabelProvider</code>.
     * 
     * @param componentController the provider of the configured component to
     *        use for cell rendering
     */
    public DefaultTableRenderer(ComponentProvider componentController) {
        if (componentController == null) {
            componentController = new LabelProvider();
        }
        this.componentController = componentController;
        this.cellContext = new TableCellContext();
    }

    /**
     * Instantiates a default table renderer with a default component
     * controller using the given converter. 
     * 
     * @param converter the converter to use for mapping the
     *   content value to a String representation.
     *   
     */
    public DefaultTableRenderer(StringValue converter) {
        this(new LabelProvider(converter));
    }

    /**
     * Instantiates a default table renderer with a default component
     * controller using the given converter and horizontal 
     * alignment. 
     * 
     * @param converter the converter to use for mapping the
     *   content value to a String representation.
     */
    public DefaultTableRenderer(StringValue converter, int alignment) {
        this(new LabelProvider(converter, alignment));
    }

    // -------------- implements javax.swing.table.TableCellRenderer
    /**
     * 
     * Returns a configured component, appropriate to render the given
     * list cell.  
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
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        cellContext.installContext(table, value, row, column, isSelected, hasFocus,
                true, true);
        return componentController.getRendererComponent(cellContext);
    }
    /**
     * @param background
     */
    public void setBackground(Color background) {
        componentController.getRendererController().setBackground(background);
        
    }
    /**
     * @param foreground
     */
    public void setForeground(Color foreground) {
        componentController.getRendererController().setForeground(foreground);
    }

//----------------- RolloverRenderer
    
    /**
     * {@inheritDoc}
     */
    public void doClick() {
        if (isEnabled()) {
            ((RolloverRenderer) componentController).doClick(); 
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return (componentController instanceof RolloverRenderer) && 
           ((RolloverRenderer) componentController).isEnabled();
    }


}


