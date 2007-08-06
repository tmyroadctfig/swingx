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
 */
package org.jdesktop.swingx.table;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;

public class DatePickerCellEditorVisualCheck extends InteractiveTestCase {
    public static void main(String[] args) {
//        setSystemLF(true);
        DatePickerCellEditorVisualCheck test = new DatePickerCellEditorVisualCheck();
        try {
            test.runInteractiveTests();
//          test.runInteractiveTests(".*Text.*");
//          test.runInteractiveTests(".*XLabel.*");
//          test.runInteractiveTests(".*Table.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
 


    /**
     * Issue ??-swingx: picker cell editor popup commit/cancel 
     * transfers focus out-off the table (1.5)
     * 
     * Looks like a core issue - editable combo misbehaves as well.
     * Here we use a JXTable.
     */
    public void interactiveDatePickerCellEditorXTable() {
        final JXTable table = new JXTable(createTableModel(2));
        LookAndFeel lf;
        table.setVisibleColumnCount(6);
//        table.setSurrendersFocusOnKeystroke(true);
        installEditors(table);
        Action action = new AbstractAction("toggle terminate") {

            public void actionPerformed(ActionEvent e) {
                table.setTerminateEditOnFocusLost(!table.isTerminateEditOnFocusLost());
                
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "JXTable - date picker cell editor");
        addAction(frame, action);
        frame.add(new JTextField("yet another thing to focus"), BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }



        
    /**
     * Issue ??-swingx: picker cell editor popup commit/cancel 
     * transfers focus out-off the table (1.5)
     * 
     * Looks like a core issue - editable combo misbehaves as well.
     * Here we use a core table.
     */
    public void interactiveDatePickerCellEditorTable() {
        JTable table = new JTable(createTableModel(2));
        table.putClientProperty("terminateEditOnFocusLost", true);
        installEditors(table);
        JXFrame frame = wrapWithScrollingInFrame(table, "JTable - date picker cell editor");
        frame.add(new JTextField("yet another thing to focus"), BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }



    /**
     * @param table
     */
    private void installEditors(JTable table) {
        table.setDefaultEditor(Date.class, 
                new DatePickerCellEditor(DateFormat.getDateInstance()));
        JComboBox box = new JComboBox(new String[] {"item1", "item2", "item3"});
        box.setEditable(true);
        table.getColumnModel().getColumn(1).setCellEditor(
                new DefaultCellEditor(box));
    }

    /**
     * @return
     */
    private DefaultTableModel createTableModel(int rows) {
        Object[] columns = new Object[]{"Date", "editable combo", "simple field"};
        DefaultTableModel model = new DefaultTableModel(rows, columns.length) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (getRowCount() > 0) {
                    Object value = getValueAt(0, columnIndex);
                    if (value != null) {
                        return value.getClass();
                    }
                }
                return super.getColumnClass(columnIndex);
            }
            
        };
        model.setColumnIdentifiers(columns);
        Date date = new Date();
        model.setValueAt(date, 0, 0);
        model.setValueAt("selectedItem", 0, 1);
        return model;
    }



}
