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
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.jdesktop.swingx.RolloverRenderer;


/**
 * Adapter to glue SwingX renderer support to core api.
 * <p>
 * 
 * PENDING: this is not really serializable - no default constructor
 * 
 * @author Jeanette Winzenburg
 * 
 * 
 */
public class DefaultTreeRenderer 
        implements TreeCellRenderer, RolloverRenderer, Serializable {

    private RenderingComponentController componentController;
    private CellContext<JTree> cellContext;
    
//    public static DefaultTreeRenderer createDefaultTreeRenderer() {
//        return createDefaultTreeRenderer(null);
//    }
//    
//    public static DefaultTreeRenderer createDefaultTreeRenderer(ToStringConverter converter) {
//        return new DefaultTreeRenderer(new WrappingIconController(converter));
//    }

    /**
     * Instantiates a default tree renderer with the given componentController.
     * If the controller is null, creates and uses a default.
     * 
     * @param componentController
     */
    public DefaultTreeRenderer(RenderingComponentController componentController) {
        if (componentController == null) {
            componentController = new WrappingIconController();
        }
        this.componentController = componentController;
        this.cellContext = new TreeCellContext();
    }

    /**
     * 
     */
    public DefaultTreeRenderer() {
        this((RenderingComponentController)null);
    }

    /**
     * @param converter
     */
    public DefaultTreeRenderer(ToStringConverter converter) {
        this(new WrappingIconController(converter));
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
       public Component getTreeCellRendererComponent(JTree tree, Object value, 
                boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            cellContext.installContext(tree, value, row, 0, selected, hasFocus, expanded, leaf);
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


