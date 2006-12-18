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
package org.jdesktop.swingx.decorator;

import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;

import junit.framework.TestCase;

/**
 * Test to exposed known issues of <code>ComponentAdapter</code>.
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 * 
 * 
 * @author Jeanette Winzenburg
 */
public class ComponentAdapterIssues extends TestCase {

    /**
     * Issue #??- ComponentAdapter's default implementation does not
     *    return the value at the adapter's view state.
     * 
     * PENDING: update doc, make abstract
     *
     */
    public void testComponentAdapterCoordinates() {
        final JXTable table = new JXTable(createAscendingModel(0, 10));
        Object originalFirstRowValue = table.getValueAt(0,0);
        Object originalLastRowValue = table.getValueAt(table.getRowCount() - 1, 0);
        assertEquals("view row coordinate equals model row coordinate", 
                table.getModel().getValueAt(0, 0), originalFirstRowValue);
        // sort first column - actually does not change anything order 
        table.toggleSortOrder(0);
        // sanity asssert
        assertEquals("view order must be unchanged ", 
                table.getValueAt(0, 0), originalFirstRowValue);
        // invert sort
        table.toggleSortOrder(0);
        // sanity assert
        assertEquals("view order must be reversed changed ", 
                table.getValueAt(0, 0), originalLastRowValue);
        ComponentAdapter adapter = new ComponentAdapter(table) {

            @Override
            public String getColumnIdentifier(int columnIndex) {
                return null;
            }

            @Override
            public String getColumnName(int columnIndex) {
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public Object getFilteredValueAt(int row, int column) {
                return getValueAt(table.convertRowIndexToModel(row), column);
//                return table.getValueAt(row, modelToView(column)); // in view coordinates
            }


            @Override
            public Object getValueAt(int row, int column) {
                return table.getModel().getValueAt(row, column);
            }

            @Override
            public boolean hasFocus() {
                return false;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public boolean isSelected() {
                return false;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
              
            }
            
        };
        assertEquals("adapter filteredValue expects row view coordinates", 
                table.getValueAt(0, 0), adapter.getFilteredValueAt(0, 0));
        // adapter coordinates are view coordinates
        adapter.row = 0;
        adapter.column = 0;
        assertEquals("adapter.getValue must return value at adapter coordinates", 
                table.getValueAt(0, 0), adapter.getValue());
        
        
    }

    private DefaultTableModel createAscendingModel(int startRow, int count) {
        DefaultTableModel model = new DefaultTableModel(count, 5);
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(new Integer(startRow++), i, 0);
        }
        return model;
    }

}
