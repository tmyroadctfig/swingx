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
 * Creates and configures <code>TableColumnExt</code>s.
 * <p>
 * TODO JW: explain types of configuration - initial from tableModel, initial
 * from table context, user triggered at runtime.
 * <p>
 * 
 * <code>JXTable</code> delegates all <code>TableColumn</code> creation and
 * configuration to a <code>ColumnFactory</code>. Enhanced column
 * configuration should be implemented in a custom factory subclass. The example
 * beautifies the column titles to always start with a capital letter:
 * 
 * <pre>
 * <code>
 *    MyColumnFactory extends ColumnFactory {
 *        //@Override
 *        public void configureTableColumn(TableModel model, 
 *            TableColumnExt columnExt) {
 *            super.configureTableColumn(model, columnExt);
 *            String title = columnExt.getTitle();
 *            title = title.substring(0,1).toUpperCase() + title.substring(1).toLowerCase();
 *            columnExt.setTitle(title);
 *        }
 *    };
 * </code>
 * </pre>
 * 
 * By default a single instance is shared across all tables of an application.
 * This instance can be replaced by a custom implementation, preferably "early"
 * in the application's lifetime.
 * 
 * <pre><code>
 * ColumnFactory.setInstance(new MyColumnFactory());
 * </code></pre> 
 * 
 * Alternatively, any instance of <code>JXTable</code> can be configured
 * individually with its own <code>ColumnFactory</code>.
 * 
 * <pre>
 *  <code>
 * JXTable table = new JXTable();
 * table.setColumnFactory(new MyColumnFactory());
 * table.setModel(myTableModel);
 * </code>
 *  </pre>
 * 
 * <p>
 * 
 * @see org.jdesktop.swingx.JXTable#setColumnFactory(ColumnFactory)
 * 
 * @author Jeanette Winzenburg
 * @author M.Hillary (the pack code)
 */
public class ColumnFactory {
    
    /** the shared instance. */
    private static ColumnFactory columnFactory;
    /** the default margin to use in pack. */
    private int packMargin = 4;
    
    /**
     * Returns the shared default factory. 
     * 
     * @return the shared instance of <code>ColumnFactory</code>
     * @see #setInstance(ColumnFactory)
     */
    public static synchronized ColumnFactory getInstance() {
        if (columnFactory == null) {
            columnFactory = new ColumnFactory();
        }
        return columnFactory;
    }

    /**
     * Sets the shared default factory. The shared instance is used
     * by <code>JXTable</code> if none has been set individually.
     * 
     * @param factory the default column factory.
     * @see #getInstance()
     * @see org.jdesktop.swingx.JXTable#getColumnFactory()
     */
    public static synchronized void  setInstance(ColumnFactory factory) {
        columnFactory = factory;
    }

    /**
     * Creates and configures a TableColumnExt. <code>JXTable</code> calls
     * this method for each column in the <code>TableModel</code>.
     * 
     * @param model the TableModel to read configuration properties from
     * @param modelIndex column index in model coordinates
     * @return a TableColumnExt to use for the modelIndex
     * @throws NPE if model == null
     * @throws IllegalStateException if the modelIndex is invalid
     *   (in coordinate space of the tablemodel)
     *  
     * @see #createTableColumn(int)
     * @see #configureTableColumn(TableModel, TableColumnExt)
     * @see org.jdesktop.swingx.JXTable#createDefaultColumnsFromModel() 
     */
    public TableColumnExt createAndConfigureTableColumn(TableModel model, int modelIndex) {
        TableColumnExt column = createTableColumn(modelIndex);
        configureTableColumn(model, column);
        return column;
    }
    
    /**
     * Creates a table column with modelIndex.
     * <p>
     * The factory's column creation is passed through this method, so 
     * subclasses can override to return custom column types.
     * 
     * @param modelIndex column index in model coordinates
     * @return a TableColumnExt with <code>modelIndex</code>
     * 
     * @see #createAndConfigureTableColumn(TableModel, int)
     * 
     */
    public TableColumnExt createTableColumn(int modelIndex) {
        return new TableColumnExt(modelIndex);
    }
    
    /**
     * Configure column properties from TableModel. This implementation
     * sets the column's <code>headerValue</code> property from the 
     * model's <code>columnName</code>.
     * <p>
     * 
     * The factory's initial column configuration is passed through this method, so 
     * subclasses can override to customize.
     * <p>
     * 
     * @param model the TableModel to read configuration properties from
     * @param columnExt the TableColumnExt to configure.
     * @throws NullPointerException if model or column == null
     * @throws IllegalStateException if column does not have valid modelIndex
     *   (in coordinate space of the tablemodel)
     *   
     * @see #createAndConfigureTableColumn(TableModel, int)  
     */
    public void configureTableColumn(TableModel model, TableColumnExt columnExt) {
        if ((columnExt.getModelIndex() < 0) 
                || (columnExt.getModelIndex() >= model.getColumnCount())) 
            throw new IllegalStateException("column must have valid modelIndex");
        columnExt.setHeaderValue(model.getColumnName(columnExt.getModelIndex()));
    }
    

    /**
     * Configures column initial widths properties from <code>JXTable</code>. 
     * This bare-bones implementation sets the column's <code>preferredWidth</code>
     * using it's <code>prototype</code> property. <p>
     * 
     * TODO JW - rename method to better convey what's happening, maybe
     *   initializeColumnWidths like the old method in JXTable.
     * 
     * @param table the context the column will live in.
     * @param columnExt the Tablecolumn to configure.
     * 
     * @see org.jdesktop.swingx.JXTable#getPreferredScrollableViewportSize()
     */
    public void configureColumnWidths(JXTable table, TableColumnExt columnExt) {
        /*
         * PENDING JW: really only called once in a table's lifetime? 
         * unfortunately: yes - should be called always after
         * structureChanged.
         * 
         */
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
     * Configures the column's <code>preferredWidth</code> to fit the content.
     * It respects the table context, a margin to add and a maximum width. This is
     * typically called in response to a user gesture to adjust the column's width
     * to the "widest" cell content of a column.  
     * <p>
     * 
     * This implementation loops through all rows of the given column and
     * measures the renderers pref width (it's a potential performance sink).
     * Subclasses can override to implement a different strategy.
     * <p>
     * 
     * Note: though 2 * margin is added as spacing, this does <b>not</b> imply
     * a left/right symmetry - it's up to the table to place the renderer and/or
     * the renderer/highlighter to configure a border.
     * 
     * @param table the context the column will live in.
     * @param columnExt the column to configure.
     * @param margin the extra spacing to add twice, if -1 uses this factories
     *        default
     * @param max an upper limit to preferredWidth, -1 is interpreted as no
     *        limit
     * 
     * @see #setDefaultPackMargin(int)
     * @see org.jdesktop.swingx.JXTable#packTable(int)
     * @see org.jdesktop.swingx.JXTable#packColumn(int, int)
     * 
     */
    public void packColumn(JXTable table, TableColumnExt columnExt, int margin,
            int max) {

        /* Get width of column header */
        TableCellRenderer renderer = columnExt.getHeaderRenderer();
        if (renderer == null)
            renderer = table.getTableHeader().getDefaultRenderer();

        int column = table.convertColumnIndexToView(columnExt.getModelIndex());

        Component comp = renderer.getTableCellRendererComponent(table,
                columnExt.getHeaderValue(), false, false, 0, column);
        int width = comp.getPreferredSize().width;

        if (table.getRowCount() > 0)
            renderer = table.getCellRenderer(0, column);
        for (int r = 0; r < table.getRowCount(); r++) {
            comp = renderer.getTableCellRendererComponent(table, table
                    .getValueAt(r, column), false, false, r, column);
            width = Math.max(width, comp.getPreferredSize().width);
        }
        if (margin < 0) {
            margin = getDefaultPackMargin();
        }
        width += 2 * margin;

        /* Check if the width exceeds the max */
        if (max != -1 && width > max)
            width = max;

        columnExt.setPreferredWidth(width);

    }
    
// ------------------------ default state
    
    /**
     * Returns the default pack margin.
     * 
     * @return the default pack margin to use in packColumn.
     * 
     * @see #setDefaultPackMargin(int)
     */
    public int getDefaultPackMargin() {
        return packMargin;
    }
    
    /**
     * Sets the default pack margin. <p>
     * 
     * Note: this is <b>not</b> really a margin in the sense of symmetrically 
     * adding white space to the left/right of a cell's content. It's simply an 
     * amount of space which is added twice to the measured widths in packColumn.
     * 
     * @param margin the default marging to use in packColumn.
     * 
     * @see #getDefaultPackMargin()
     * @see #packColumn(JXTable, TableColumnExt, int, int)
     */
    public void setDefaultPackMargin(int margin) {
        this.packMargin = margin;
    }
    
}
