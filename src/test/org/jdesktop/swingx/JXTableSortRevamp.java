/*
 * $Id$
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableStringConverter;

import org.jdesktop.swingx.JXTableUnitTest.DynamicTableModel;
import org.jdesktop.swingx.decorator.ComponentAdapterTest.JXTableT;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.sort.StringValueRegistry;
import org.jdesktop.swingx.sort.TableSortController;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.test.AncientSwingTeam;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
public class JXTableSortRevamp extends InteractiveTestCase {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(JXTableSortRevamp.class
            .getName());
    
    protected DynamicTableModel tableModel = null;
    protected TableModel sortableTableModel;
    
    // flag used in setup to explicitly choose LF
    private boolean defaultToSystemLF;
    // stored ui properties to reset in teardown
    private Object uiTableRowHeight;


    /**
     * A custom StringValue for Color. Maps to a string composed of the
     * prefix "R/G/B: " and the Color's rgb value.
     */
    private StringValue sv;

    @Before
       public void setUpJu4() throws Exception {
        // just a little conflict between ant and maven builds
        // junit4 @before methods needs to be public, while
        // junit3 setUp() inherited from super is protected
      this.setUp();
    }
    
    @Override
    protected void setUp() throws Exception {
       super.setUp();
        // set loader priority to normal
        if (tableModel == null) {
            tableModel = new DynamicTableModel();
        }
        sortableTableModel = new AncientSwingTeam();
        // make sure we have the same default for each test
        defaultToSystemLF = true;
        setSystemLF(defaultToSystemLF);
        uiTableRowHeight = UIManager.get("JXTable.rowHeight");
        sv = createColorStringValue();
    }

    
    @Override
    @After
       public void tearDown() throws Exception {
        UIManager.put("JXTable.rowHeight", uiTableRowHeight);
        super.tearDown();
    }

    
    public static void main(String[] args) {
        JXTableSortRevamp test = new JXTableSortRevamp();
        try {
            test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*Ignore.*");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//--------------- current re-introduce
    /**
     * Issue #1145-swingx: re-enable filtering with single-string-representation.
     * was: Issue #767-swingx: consistent string representation.
     * 
     * Here: test PatternFilter uses getStringXX
     */
    @Test
    public void testTableGetStringUsedInPatternFilter() {
        JXTableT table = new JXTableT(new AncientSwingTeam());
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        RowFilter<?, ?> filter = RowFilter.regexFilter("R/G/B: -2.*", 2);
        table.getSortController().setRowFilter(filter);
        assertTrue(table.getRowCount() > 0);
        assertEquals(sv.getString(table.getValueAt(0, 2)), table.getStringAt(0, 2));
    }
    /**
     * Issue #1145-swingx: re-enable filtering with single-string-representation.
     * was: Issue #767-swingx: consistent string representation.
     * 
     * Here: test PatternFilter uses getStringXX. 
     * Hard-coded to access the adapter - this fails because the old implementation calls
     * row conversion method during the filtering. Don't quite understand how that 
     * could have worked before (which it did ...)
     */
    @Test
    public void testTableGetStringUsedInPatternFilterCA() {
        final JXTableT table = new JXTableT(new AncientSwingTeam());
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        TableStringConverter coreConverter = new TableStringConverter() {
            
            @Override
            public String toString(TableModel model, int row, int column) {
                String fromAdapter = table.getComponentAdapter().getStringAt(row, column);
                return fromAdapter;
            }
        };
        ((TableSortController) table.getRowSorter()).setStringConverter(coreConverter);
        RowFilter<?, ?> filter = RowFilter.regexFilter("R/G/B: -2", 2);
        table.getSortController().setRowFilter(filter);
        assertTrue(table.getRowCount() > 0);
        assertEquals(sv.getString(table.getValueAt(0, 2)), table.getStringAt(0, 2));
    }
    
    /**
     * Issue #1145-swingx: re-enable filtering with single-string-representation.
     * was: Issue #767-swingx: consistent string representation.
     * 
     * Here: test PatternFilter uses getStringXX
     * Sanity: to check the filter as such works as expected we hard-code the 
     * core converter to use the string value directly.
     */
    @Test
    public void testTableGetStringUsedInPatternFilterSanity() {
        final JXTableT table = new JXTableT(new AncientSwingTeam());
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        TableStringConverter coreConverter = new TableStringConverter() {
            
            @Override
            public String toString(TableModel model, int row, int column) {
                String fromModel = sv.getString(model.getValueAt(row, column));
                return fromModel;
            }
        };
        ((TableSortController) table.getRowSorter()).setStringConverter(coreConverter);
        RowFilter<?, ?> filter = RowFilter.regexFilter("R/G/B: -2", 2);
        table.getSortController().setRowFilter(filter);
        assertTrue(table.getRowCount() > 0);
        assertEquals(sv.getString(table.getValueAt(0, 2)), table.getStringAt(0, 2));
    }
    
    
    

    //--------------------- factory


    /**
     * returns a tableModel with count rows filled with
     * ascending integers in first column
     * starting from startRow.
     * @param startRow the value of the first row
     * @param count the number of rows
     * @return
     */
    protected DefaultTableModel createAscendingModel(int startRow, int count) {
        DefaultTableModel model = new DefaultTableModel(count, 4) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Integer.class : super.getColumnClass(column);
            }
        };
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Integer(startRow++), i, 0);
        }
        return model;
    }

    
    /**
     * returns a tableModel with count rows filled with
     * ascending integers in first/last column depending on fillLast
     * starting from startRow.
     * with columnCount columns
     * @param startRow the value of the first row
     * @param rowCount the number of rows
     * @param columnCount the number of columns
     * @param fillLast boolean to indicate whether to ill the value in the first
     *   or last column
     * @return a configured DefaultTableModel.
     */
    protected DefaultTableModel createAscendingModel(int startRow, final int rowCount, 
            final int columnCount, boolean fillLast) {
        DefaultTableModel model = new DefaultTableModel(rowCount, columnCount) {
            @Override
            public Class<?> getColumnClass(int column) {
                Object value = rowCount > 0 ? getValueAt(0, column) : null;
                return value != null ? value.getClass() : super.getColumnClass(column);
            }
        };
        int filledColumn = fillLast ? columnCount - 1 : 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Integer(startRow++), i, filledColumn);
        }
        return model;
    }
    
    /**
     * Creates and returns a StringValue which maps a Color to it's R/G/B rep, 
     * prepending "R/G/B: "
     * 
     * @return the StringValue for color.
     */
    private StringValue createColorStringValue() {
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return StringValues.TO_STRING.getString(value);
            }
            
        };
        return sv;
    }

}
