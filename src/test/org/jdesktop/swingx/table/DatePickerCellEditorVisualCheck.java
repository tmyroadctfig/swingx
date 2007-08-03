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
import java.text.DateFormat;
import java.util.Date;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.LabelProvider;

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
        Date date = new Date();
        DefaultTableModel model = new DefaultTableModel(1, 2) {

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
        model.setColumnIdentifiers(new Object[]{"Date", "editable combo"});
        model.setValueAt(date, 0, 0);
          model.setValueAt("selectedItem", 0, 1);
        JXTable table = new JXTable(model);
        table.setVisibleColumnCount(6);
        // right align to see the difference to normal date renderer
        DefaultTableRenderer renderer = new DefaultTableRenderer(
                new LabelProvider(SwingConstants.RIGHT));
        table.setDefaultRenderer(java.sql.Date.class, renderer);
        table.setDefaultEditor(Date.class, new DatePickerCellEditor(DateFormat.getDateInstance()));
        JComboBox box = new JComboBox(new String[] {"item1", "item2", "item3"});
        box.setEditable(true);
        table.getColumnExt(1).setCellEditor(new DefaultCellEditor(box));
        JXFrame frame = showWithScrollingInFrame(table, "JXTable - date picker cell editor");
        JComboBox freeBox = new JComboBox(box.getModel());
        freeBox.setEditable(true);
//        frame.add(freeBox, BorderLayout.SOUTH);
        frame.add(new JTextField("yet another thing to focus"), BorderLayout.NORTH);
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
        Date date = new Date();
        DefaultTableModel model = new DefaultTableModel(1, 2) {

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
        model.setColumnIdentifiers(new Object[]{"Date", "editable combo"});
        model.setValueAt(date, 0, 0);
          model.setValueAt("selectedItem", 0, 1);
        JTable table = new JTable(model);
        table.putClientProperty("terminateEditOnFocusLost", true);
        // right align to see the difference to normal date renderer
        DefaultTableRenderer renderer = new DefaultTableRenderer(
                new LabelProvider(SwingConstants.RIGHT));
        table.setDefaultRenderer(java.sql.Date.class, renderer);
        table.setDefaultEditor(Date.class, new DatePickerCellEditor(DateFormat.getDateInstance()));
        JComboBox box = new JComboBox(new String[] {"item1", "item2", "item3"});
        box.setEditable(true);
        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(box));
        JXFrame frame = showWithScrollingInFrame(table, "JTable - date picker cell editor");
        JComboBox freeBox = new JComboBox(box.getModel());
        freeBox.setEditable(true);
//        frame.add(freeBox, BorderLayout.SOUTH);
        frame.add(new JTextField("yet another thing to focus"), BorderLayout.NORTH);
        frame.pack();
        frame.setVisible(true);
    }



}
