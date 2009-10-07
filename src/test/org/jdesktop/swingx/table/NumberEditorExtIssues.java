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
package org.jdesktop.swingx.table;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.NumberFormatter;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
public class NumberEditorExtIssues extends InteractiveTestCase {

    private static final String TOO_BIG_INTEGER = "11111111111111111111111111";
    private static final int INTEGER_COLUMN = 0;
    /** a table with a model which has column class Integer in INTEGER_COLUMN. */
    private JXTable table;
    /** a NumberEditorExt configured with IntegerFormat. */
    private NumberEditorExt cellEditor;


    private NumberEditorExt cellEditorStrict;

    /**
     * Issue #1183-swingx: NumberEditorExt throws in getCellEditorValue if
     *   Integer (short, byte..) below/above min/max.
     *   
     * Check IllegalStateException as doc'ed - the strict version doesn't. 
     * Need to check the delegate implementation?
     */
    @Test(expected = IllegalStateException.class)
    public void testEditorStrictValueIllegalState() {
        JFormattedTextField field = (JFormattedTextField) cellEditorStrict
                .getTableCellEditorComponent(table, Integer.MAX_VALUE, false, 0, INTEGER_COLUMN);
        // add valid digit - but exceeds Integer bounds so must not return true
        field.setText(field.getText() + "9");
        cellEditorStrict.getCellEditorValue();
    }
 
    /**
     * Issue ??-swingx: editor with strict number formatter throws on 
     *    committing null value.
     *   
     *  happens only if active editor in table. Use non-strict for comparison.
     *  InternationalFormatter with bounds throws as well. But not using
     *  strict catches this in isValid, that is stopCellEditing returns false.
     */
    @Test
    public void testEditorNullValue() {
        table.getColumn(INTEGER_COLUMN).setCellEditor(cellEditor);
        ((InternationalFormatter) cellEditor.getComponent().getFormatter()).setMinimum(0);
        table.editCellAt(0, INTEGER_COLUMN);
        assertTrue(cellEditor.stopCellEditing());
    }
    

//--------------------- core issues
    /**
     * Formatted text field commit can't handle empty string.
     * 
     * @throws ParseException
     */
    @Test
    public void testTextFieldWithEmptyString() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        JFormattedTextField field = new JFormattedTextField(format);
        field.setText("");
        field.commitEdit();
    }
    
    /**
     * NumberFormatter parsing can't handle null.
     * 
     * @throws ParseException
     */
    @Test
    public void testNumberFormatterEmptyStringValue() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.stringToValue("");
    }
    
    /**
     * NumberFormat parsing cant handle empty string.
     * 
     * @throws ParseException
     */
    @Test
    public void testNumberFormatEmptyStringValue() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.parse("");
    }
    
    /**
     * NumberFormatter parsing can't handle null.
     * @throws ParseException
     */
    @Test
    public void testNumberFormatterNullValue() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.stringToValue(null);
    }
    
    /**
     * NumberFormat parse can't handle null.
     * 
     * @throws ParseException
     */
    @Test
    public void testNumberFormatNullValue() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.parse(null);
    }
    

    @Test (expected = ParseException.class)
    public void testNumberFormatterMinMax() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setMinimum(Integer.MIN_VALUE);
        formatter.stringToValue(TOO_BIG_INTEGER);
    }

    @Test
    public void testNumberFormatInteger() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        // this passes - everything fitting into double range is acceptable
        Number number = format.parse(TOO_BIG_INTEGER);
        // this blows - must fit into Integer.MIN/MAX
        new Integer(TOO_BIG_INTEGER);
    }
    
    @Before
    @Override
    public void setUp() throws Exception {
        DefaultTableModel model = new DefaultTableModel(5, 1) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == INTEGER_COLUMN)
                    return Integer.class;
                return super.getColumnClass(columnIndex);
            }
            
        };
        table = new JXTable(model);
        cellEditor = new NumberEditorExt(NumberFormat.getIntegerInstance());
        cellEditorStrict = new NumberEditorExt(NumberFormat.getIntegerInstance(), true);
    }


}
