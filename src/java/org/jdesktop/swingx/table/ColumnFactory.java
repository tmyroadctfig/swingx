/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.table;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;

/**
 * Creates and configures TableColumns.
 * 
 * @author Jeanette Winzenburg
 */
public class ColumnFactory {
    
    private static ColumnFactory columnFactory;
    
    public static synchronized ColumnFactory getInstance() {
        if (columnFactory == null) {
            columnFactory = new ColumnFactory();
        }
        return columnFactory;
    }

    public static synchronized void  setInstance(ColumnFactory factory) {
        columnFactory = factory;
    }
    
    public TableColumnExt createTableColumn(int modelIndex) {
        return new TableColumnExt(modelIndex);
    }
    
    /**
     * Configure column properties from TableModel.
     * 
     * @param model
     * @param column
     * @throws NPE if model or column == null
     * @throws IllegalStateException if column does not have valid modelIndex
     *   (in coordinate space of the tablemodel)
     */
    public void configureTableColumn(TableModel model, TableColumnExt column) {
        if ((column.getModelIndex() < 0) 
                || (column.getModelIndex() >= model.getColumnCount())) 
            throw new IllegalStateException("column must have valid modelIndex");
        column.setHeaderValue(model.getColumnName(column.getModelIndex()));

    }
    
    public TableColumnExt createAndConfigureTableColumn(TableModel model, int modelIndex) {
        TableColumnExt column = createTableColumn(modelIndex);
        configureTableColumn(model, column);
        return column;
    }

    public void configureColumnWidths(JXTable table, TableColumnExt columnx) {
        Dimension cellSpacing = table.getIntercellSpacing();
        Object prototypeValue = columnx.getPrototypeValue();
        if (prototypeValue != null) {
            // calculate how much room the prototypeValue requires
            TableCellRenderer renderer = table.getCellRenderer(0, table
                    .convertColumnIndexToView(columnx.getModelIndex()));
            Component comp = renderer.getTableCellRendererComponent(table,
                    prototypeValue, false, false, 0, 0);
            int prefWidth = comp.getPreferredSize().width + cellSpacing.width;

            // now calculate how much room the column header wants
            renderer = columnx.getHeaderRenderer();
            if (renderer == null) {
                JTableHeader header = table.getTableHeader();
                if (header != null) {
                    renderer = header.getDefaultRenderer();
                }
            }
            if (renderer != null) {
                comp = renderer.getTableCellRendererComponent(table, columnx
                        .getHeaderValue(), false, false, 0, table
                        .convertColumnIndexToView(columnx.getModelIndex()));

                prefWidth = Math.max(comp.getPreferredSize().width, prefWidth);
            }
            prefWidth += table.getColumnModel().getColumnMargin();
            columnx.setPreferredWidth(prefWidth);
        }

    }

    public void packColumn(JXTable table, TableColumnExt col, int margin, int max) {

        /* Get width of column header */
        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) 
            renderer = table.getTableHeader().getDefaultRenderer();
        
        int width = 0;
        
        Component comp = renderer.getTableCellRendererComponent(table, col
                .getHeaderValue(), false, false, 0, 0);
        width = comp.getPreferredSize().width;
        
        int column = table.convertColumnIndexToView(col.getModelIndex());
        if(table.getRowCount() > 0)
            renderer = table.getCellRenderer(0, column);
        for (int r = 0; r < table.getRowCount(); r++) {
            comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r,
                    column), false, false, r, column);
            width = Math.max(width, comp.getPreferredSize().width);
        }
        width += 2 * margin;

        /* Check if the width exceeds the max */
        if( max != -1 && width > max )
            width = max;
        
        col.setPreferredWidth(width);
        
    }
}
