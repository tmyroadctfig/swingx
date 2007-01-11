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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.jdesktop.swingx.RolloverRenderer;


/**
 * Adapter to glue SwingX renderer support to core api.
 * <p>
 * 
 * @author Jeanette Winzenburg
 * 
 * 
 */
public class DefaultListRenderer 
        implements ListCellRenderer,  RolloverRenderer, Serializable {

    protected RenderingComponentController componentController;
    protected CellContext<JList> cellContext;
    

    /**
     * Instantiates a default list renderer with the default component
     * controller.
     *
     */
    public DefaultListRenderer() {
        this(null);
    }
    
    /**
     * Instantiates a ListCellRenderer with the given componentController.
     * If the controller is null, creates and uses a default.
     * 
     * @param componentController
     */
    public DefaultListRenderer(RenderingComponentController componentController) {
        if (componentController == null) {
            componentController = new RenderingLabelController();
        }
        this.componentController = componentController;
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


