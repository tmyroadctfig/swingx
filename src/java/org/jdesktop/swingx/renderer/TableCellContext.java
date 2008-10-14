/*
 * $Id$
 *
 * Copyright 2008 Sun Microsystems, Inc., 4150 Network Circle,
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
 */
package org.jdesktop.swingx.renderer;

import java.awt.Color;

import javax.swing.JTable;

/**
 * Table specific <code>CellContext</code>.
 */
public class TableCellContext extends CellContext<JTable> {

    /**
     * Returns the cell's editable property as returned by table.isCellEditable
     * or false if the table is null.
     * 
     * @return the cell's editable property. 
     */
    @Override
    public boolean isEditable() {
        return getComponent() != null ? getComponent().isCellEditable(
                getRow(), getColumn()) : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Color getSelectionBackground() {
        return getComponent() != null ? getComponent()
                .getSelectionBackground() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Color getSelectionForeground() {
        return getComponent() != null ? getComponent()
                .getSelectionForeground() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getUIPrefix() {
        return "Table.";
    }

}