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
 * Creates and configures TableColumns. JXTable  
 * delegates all TableColumn creation and configuration to this class. 
 * <p>
 * It's meant to be shared across all tables of an 
 * application. To apply a custom ColumnFactory, subclass and
 * set the shared instance to the new class "early" in the application.
 * <pre>
 * <code>
 *   MyColumnFactory extends ColumnFactory {
 *       //@Override
 *       public void configureTableColumn(TableModel model, 
 *           TableColumnExt columnExt) {
 *           super.configureTableColumn(model, columnExt);
 *           String title = columnExt.getTitle();
 *           title = title.substring(0,1).toUpperCase() + title.substring(1).toLowerCase();
 *           columnExt.setTitle(title);
 *       }
 *   };
 *   ColumnFactory.setInstance(new MyColumnFactory());
 * </code>
 * </pre>
 * 
 * Alternatively, any instance of JXTable can be configured 
 * individually with a custom ColumnFactory:
 * 
 *  <pre>
 *  <code>
 *    JXTable table = new JXTable();
 *    table.setColumnFactory(new MyColumnFactory());
 *    table.setModel(myTableModel);
 *  </code>
 *  </pre>
 * 
 * <p>
 * 
 * 
 * 
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

    private int packMargin = 4;
    
    /**
     * Creates a table column with modelIndex.
     * @param modelIndex column index in model coordinates
     * @return TableColumnExt to use
     */
    public TableColumnExt createTableColumn(int modelIndex) {
        return new TableColumnExt(modelIndex);
    }
    
    /**
     * Configure column properties from TableModel.
     * 
     * @param model the TableModel to read configuration properties from
     * @param columnExt the TableColumnExt to configure.
     * @throws NPE if model or column == null
     * @throws IllegalStateException if column does not have valid modelIndex
     *   (in coordinate space of the tablemodel)
     */
    public void configureTableColumn(TableModel model, TableColumnExt columnExt) {
        if ((columnExt.getModelIndex() < 0) 
                || (columnExt.getModelIndex() >= model.getColumnCount())) 
            throw new IllegalStateException("column must have valid modelIndex");
        columnExt.setHeaderValue(model.getColumnName(columnExt.getModelIndex()));

    }
    
    /**
     * Creates and configures a TableColumnExt.
     * 
     * @param model the TableModel to read configuration properties from
     * @param modelIndex column index in model coordinates
     * @return a TableColumnExt to use for the modelIndex
     * @throws NPE if model == null
     * @throws IllegalStateException if the modelIndex is invalid
     *   (in coordinate space of the tablemodel)
     */
    public TableColumnExt createAndConfigureTableColumn(TableModel model, int modelIndex) {
        TableColumnExt column = createTableColumn(modelIndex);
        configureTableColumn(model, column);
        return column;
    }

    /**
     * configure column widths properties from JXTable. This
     * method is typically called in JXTable initialization 
     * (TODO JW: really? need to check).
     * 
     * Here: set column's preferredWidth from prototype. 
     *  
     * @param table the context the column will live in.
     * @param columnExt the Tablecolumn to configure.
     */
    public void configureColumnWidths(JXTable table, TableColumnExt columnExt) {
        Dimension cellSpacing = table.getIntercellSpacing();
        Object prototypeValue = columnExt.getPrototypeValue();
        if (prototypeValue != null) {
            // calculate how much room the prototypeValue requires
            TableCellRenderer renderer = table.getCellRenderer(0, table
                    .convertColumnIndexToView(columnExt.getModelIndex()));
            Component comp = renderer.getTableCellRendererComponent(table,
                    prototypeValue, false, false, 0, 0);
            int prefWidth = comp.getPreferredSize().width + cellSpacing.width;

            // now calculate how much room the column header wants
            renderer = columnExt.getHeaderRenderer();
            if (renderer == null) {
                JTableHeader header = table.getTableHeader();
                if (header != null) {
                    renderer = header.getDefaultRenderer();
                }
            }
            if (renderer != null) {
                comp = renderer.getTableCellRendererComponent(table, 
                        columnExt.getHeaderValue(), false, false, 0, 
                        table.convertColumnIndexToView(columnExt.getModelIndex()));

                prefWidth = Math.max(comp.getPreferredSize().width, prefWidth);
            }
            prefWidth += table.getColumnModel().getColumnMargin();
            columnExt.setPreferredWidth(prefWidth);
        }

    }

    /**
     * configure the table column's preferredWidth, respecting the table context, 
     * a symmetric left/right margin to add and maximum width.
     * 
     * Here: basically loops through all rows of the given column and measures
     * the renderers pref width. This is a potential performance sink.
     * 
     * PENDING (JW): though 2 * margin is added as spacing, this does not really mean a
     * left/right symmetry - it's up to the table to place the renderer which it 
     * controlled by the intercellspacing.
     * 
     * @param table the context the column will live in.
     * @param columnExt the Tablecolumn to configure.
     * @param margin the spacing to add left/right, if -1 uses this factories default 
     * @param max an upper limit to prefWidth, -1 is interpreted as no limit
     */
    public void packColumn(JXTable table, TableColumnExt columnExt, int margin, int max) {

        /* Get width of column header */
        TableCellRenderer renderer = columnExt.getHeaderRenderer();
        if (renderer == null) 
            renderer = table.getTableHeader().getDefaultRenderer();
        
        int column = table.convertColumnIndexToView(columnExt.getModelIndex());
        
        Component comp = renderer.getTableCellRendererComponent(table, 
                columnExt.getHeaderValue(), false, false, 0, column);
        int width = comp.getPreferredSize().width;
        
        if(table.getRowCount() > 0)
            renderer = table.getCellRenderer(0, column);
        for (int r = 0; r < table.getRowCount(); r++) {
            comp = renderer.getTableCellRendererComponent(table, 
                    table.getValueAt(r, column), false, false, r, column);
            width = Math.max(width, comp.getPreferredSize().width);
        }
        if (margin < 0) {
            margin = getDefaultPackMargin();
        }
        width += 2 * margin;

        /* Check if the width exceeds the max */
        if( max != -1 && width > max )
            width = max;
        
        columnExt.setPreferredWidth(width);
        
    }
    
//------------------------ default state
    
    /**
     * @return the default pack margin to use in packColumn.
     */
    public int getDefaultPackMargin() {
        return packMargin;
    }
    
    /**
     * 
     */
    public void setDefaultPackMargin(int margin) {
        this.packMargin = margin;
    }
    
}
