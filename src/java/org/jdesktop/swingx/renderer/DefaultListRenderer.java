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

import javax.swing.Icon;
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
public class DefaultListRenderer implements ListCellRenderer, RolloverRenderer,
        Serializable {

    protected ComponentProvider componentController;

    protected CellContext<JList> cellContext;

    /**
     * Instantiates a default list renderer with the default component
     * controller.
     *
     */
    public DefaultListRenderer() {
        this((ComponentProvider) null);
    }

    /**
     * Instantiates a ListCellRenderer with the given componentController.
     * If the controller is null, creates and uses a default.
     * 
     * @param componentController the provider of the configured component to
     *   use for cell rendering
     */
    public DefaultListRenderer(ComponentProvider componentController) {
        if (componentController == null) {
            componentController = new LabelProvider(createDefaultStringValue());
        }
        this.componentController = componentController;
        this.cellContext = new ListCellContext();
    }

    /**
     * Creates and returns the default StringValue for a JList.<p>
     * This is added to keep consistent with core list rendering which
     * shows either the Icon (for Icon value types) or the default 
     * to-string for non-icon types.
     * 
     * @return the StringValue to use by default.
     */
    private StringValue createDefaultStringValue() {
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Icon) {
                    return "";
                }
                return StringValue.TO_STRING.getString(value);
            }

        };
        return new MappedValue(sv, IconValue.ICON);
    }

    /**
     * Instantiates a default table renderer with a default component controller
     * using the given converter.<p>
     * 
     * PENDING JW: how to guarantee core consistent icon handling?
     * 
     * @param converter the converter to use for mapping the content value to a
     *        String representation.
     * 
     */
    public DefaultListRenderer(StringValue converter) {
        this(new LabelProvider(converter));
    }

    /**
     * Instantiates a default list renderer with a default component
     * controller using the given converter and horizontal 
     * alignment. 
     * 
     * PENDING JW: how to guarantee core consistent icon handling?
     * 
     * 
     * @param converter the converter to use for mapping the
     *   content value to a String representation.
     * @param alignment the horizontal alignment.
     */
    public DefaultListRenderer(StringValue converter, int alignment) {
        this(new LabelProvider(converter, alignment));
    }

    // -------------- implements javax.swing.table.ListCellRenderer
    /**
     * 
     * Returns a configured component, appropriate to render the given
     * list cell.  
     * 
     * @param list the <code>JList</code> to render on
     * @param value the value to assign to the cell 
     * @param isSelected true if cell is selected
     * @param cellHasFocus true if cell has focus
     * @param index the row index (in view coordinates) of the cell to render
     * @return a component to render the given list cell.
     */
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        cellContext.installContext(list, value, index, 0, isSelected,
                cellHasFocus, true, true);
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
        return (componentController instanceof RolloverRenderer)
                && ((RolloverRenderer) componentController).isEnabled();
    }

}


