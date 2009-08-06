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
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTableUnitTest.DynamicTableModel;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
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

    /**
     * Keep runner happy ...
     */
    @Test
    public void testDummy() {
        
    }
}
