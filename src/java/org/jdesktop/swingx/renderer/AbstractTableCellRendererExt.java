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
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


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
public abstract class AbstractTableCellRendererExt<T extends JComponent>
        extends AbstractCellRenderer<T, JTable> implements TableCellRenderer,
        Serializable {

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
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        CellContext<JTable> context = getCellContext();
        context.installContext(table, value, row, column, isSelected, hasFocus,
                true, true);
        configureVisuals(context);
        configureContent(context);
        return rendererComponent;
    }

    @Override
    protected CellContext<JTable> getCellContext() {
        if (cellContext == null) {
            cellContext = new TableCellContext();
        }
        return cellContext;
    }

    /**
     * {@inheritDoc} <p>
     * Overridden to additionally configure focus colors.
     */
    @Override
    protected void configureVisuals(CellContext<JTable> context) {
        super.configureVisuals(context);
        if (context.isFocused()) {
            configureFocusColors(context);
        }
    }

    /**
     * Configures focus-related colors form given cell context.<p>
     * 
     * PENDING: move to context as well? - it's the only comp
     * with focus specifics? Problem is the parameter type...
     * 
     * @param context the cell context to configure from.
     */
    protected void configureFocusColors(CellContext<JTable> context) {
        if (!context.isSelected() && context.isEditable()) {
            Color col = context.getFocusForeground();
            if (col != null) {
                rendererComponent.setForeground(col);
            }
            col = context.getFocusBackground();
            if (col != null) {
                rendererComponent.setBackground(col);
            }
        }
    }

}


