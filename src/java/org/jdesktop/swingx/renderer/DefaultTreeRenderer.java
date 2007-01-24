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
 * 
 * @author Jeanette Winzenburg
 * 
 * 
 */
public class DefaultTreeRenderer 
        implements TreeCellRenderer, RolloverRenderer, Serializable {

    protected ComponentProvider componentController;
    private CellContext<JTree> cellContext;
    
    /**
     * Instantiates a default tree renderer with the default component
     * controller. 
     * 
     */
    public DefaultTreeRenderer() {
        this((ComponentProvider)null);
    }


    /**
     * Instantiates a default tree renderer with the given componentController.
     * If the controller is null, creates and uses a default. The default
     * controller is of type <code>WrappingProvider</code>.
     * 
     * @param componentController the provider of the configured component to
     *        use for cell rendering
     */
    public DefaultTreeRenderer(ComponentProvider componentController) {
        if (componentController == null) {
            componentController = new WrappingProvider();
        }
        this.componentController = componentController;
        this.cellContext = new TreeCellContext();
    }

    /**
     * Instantiates a default table renderer with a default component
     * controller using the given converter. 
     * 
     * @param converter the converter to use for mapping the
     *   content value to a String representation.
     *   
     */
    public DefaultTreeRenderer(StringValue converter) {
        this(new WrappingProvider(converter));
    }

    // -------------- implements javax.swing.table.TableCellRenderer
    /**
     * 
     * Returns a configured component, appropriate to render the given
     * tree cell.  
     * 
     * @param tree the <code>JTree</code>
     * @param value the value to assign to the cell 
     * @param selected true if cell is selected
     * @param expanded true if the cell is expanded
     * @param leaf true if the cell is a leaf
     * @param hasFocus true if cell has focus
     * @param row the row of the cell to render
     * @return a component to render the given list cell.
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


