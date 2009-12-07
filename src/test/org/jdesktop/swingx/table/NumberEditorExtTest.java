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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.NumberFormatter;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTable.NumberEditor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class NumberEditorExtTest extends InteractiveTestCase {

    private static final String TOO_BIG_INTEGER = "11111111111111111111111111";

    
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(NumberEditorExtTest.class.getName());
    
    public static void main(String[] args) {
        NumberEditorExtTest test = new NumberEditorExtTest();
        try {
            test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*Number.*");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final int INTEGER_COLUMN = 0;
    private static final int NUMBER_COLUMN = 1;
    private static final int OBJECT_COLUMN = 2;
    private static final int COLUMN_COUNT = OBJECT_COLUMN + 1;
    
    /** a table with a model which has column class Integer in INTEGER_COLUMN and
     * column class Number in NUMBER_COLUMN. */
    private JXTable table;
    /** a NumberEditorExt configured with IntegerFormat. */
    private NumberEditorExt cellEditor;

    /** a NumberEditorExt configured with strict IntegerFormat. */
    private NumberEditorExt cellEditorStrict;
    
    
    
    /**
     * Issue #1236-swingx: NumberEditor throws for non-number columns.
     * 
     * Strict editor delegates to formatter -  which defaults to string valueclass.
     */
    @Test 
    public void testStrictIsDefault() {
        TableCellEditor editor = table.getDefaultEditor(Number.class);
        assertTrue("sanity: expect NumberEditorExt but was " + editor, editor instanceof NumberEditorExt);
        assertEquals("default formatter is strict", true, 
                ((NumberEditorExt) editor).hasStrictFormatter());
    }
    
    /**
     * Issue #1236-swingx: NumberEditor throws for non-number columns.
     * 
     * Strict editor delegates to formatter -  which defaults to string valueclass.
     */
    @Test 
    public void testEditorStrictObjectColumn() {
        cellEditorStrict.getTableCellEditorComponent(table, null, false, 0, OBJECT_COLUMN);
    }
    
    /**
     * Issue #1236-swingx: NumberEditor throws for non-number columns.
     * 
     * Old default: non-strict editor throws IllegalState if column type
     * is not a subtype of Number.
     */
    @Test (expected = IllegalStateException.class)
    public void testEditorObjectColumn() {
        cellEditor.getTableCellEditorComponent(table, null, false, 0, OBJECT_COLUMN);
    }

//---------------- Sanity testing: StrictNumberFormatter - 
    
    @Test (expected = ParseException.class)
    public void testNumberFormatter() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setMaximum(Integer.MAX_VALUE - 1);
        formatter.setMinimum(Integer.MIN_VALUE + 1);
        formatter.stringToValue(TOO_BIG_INTEGER);
    }
    
    
    @Test (expected = ParseException.class)
    public void testStrictNumberFormatterMinMax() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new StrictNumberFormatter(format);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setMinimum(Integer.MIN_VALUE);
        formatter.stringToValue(TOO_BIG_INTEGER);
    }
    
    @Test (expected = ParseException.class)
    public void testStrictNumberFormatterAutoRangeInteger() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new StrictNumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.stringToValue(TOO_BIG_INTEGER);
    }
    
    @Test (expected = ParseException.class)
    public void testStrictNumberFormatterAutoRangeLong() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new StrictNumberFormatter(format);
        formatter.setValueClass(Long.class);
        String text = new Long(Long.MAX_VALUE).toString() + "9";
        formatter.stringToValue(text);
    }
    
    @Test (expected = ParseException.class)
    public void testStrictNumberFormatterAutoRangeFloat() throws ParseException {
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new StrictNumberFormatter(format);
        formatter.setValueClass(Float.class);
        String text = "9" + new Float(Float.MAX_VALUE).toString();
        formatter.stringToValue(text);
    }
    
   
    @Test (expected = ParseException.class)
    public void testStrictNumberFormatterAutoRangeByte() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new StrictNumberFormatter(format);
        formatter.setValueClass(Byte.class);
        formatter.stringToValue(TOO_BIG_INTEGER);
    }
    
    @Test (expected = ParseException.class)
    public void testStrictNumberFormatterAutoRangeShort() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new StrictNumberFormatter(format);
        formatter.setValueClass(Short.class);
        formatter.stringToValue(TOO_BIG_INTEGER);
    }
    
    @Test (expected = ParseException.class)
    public void testStrictNumberFormatterSmallExceedInteger() throws ParseException {
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new StrictNumberFormatter(format);
        formatter.setValueClass(Integer.class);
        String text = new Integer(Integer.MAX_VALUE).toString() + "1";
        formatter.stringToValue(text);
    }

//---------- end sanity testing: StrictNumberFormatter
    
    /**
     * Issue #1183-swingx: NumberEditorExt throws in getCellEditorValue if
     *   Integer (short, byte..) below/above min/max.
     *   
     *  Sanity: checks that stopCellEditing returns false on parsing errors.   
     */
    @Test
    public void testEditorValueParsing() {
        JFormattedTextField field = (JFormattedTextField) cellEditor
        .getTableCellEditorComponent(table, 100, false, 0, INTEGER_COLUMN);
        // add valid digit
        field.setText(field.getText() + "9");
        assertTrue("valid input " + field.getText(), cellEditor.stopCellEditing());
        // add invalid character
        field.setText(field.getText() + "x");
        assertFalse("invalid input " + field.getText(), cellEditor.stopCellEditing());
    }
    
    /**
     * Issue #1183-swingx: NumberEditorExt throws in getCellEditorValue if
     *   Integer (short, byte..) below/above min/max.
     *   
     *  Sanity: checks that stopCellEditing returns false on parsing errors, use strict.   
     */
    @Test
    public void testEditorStrictValueParsing() {
        JFormattedTextField field = (JFormattedTextField) cellEditorStrict
                .getTableCellEditorComponent(table, 100, false, 0, INTEGER_COLUMN);
        // add valid digit
        field.setText(field.getText() + "9");
        assertTrue("valid input " + field.getText(), cellEditorStrict.stopCellEditing());
        // add invalid character
        field.setText(field.getText() + "x");
        assertFalse("invalid input " + field.getText(), cellEditorStrict.stopCellEditing());
    }
    
    /**
     * Issue #1183-swingx: NumberEditorExt throws in getCellEditorValue if
     *   Integer (short, byte..) below/above min/max.
     *   
     * Check that stopCellEditing returns false if bounds exceeded.
     */
    @Test
    public void testEditorValueExceedBounds() {
        JFormattedTextField field = (JFormattedTextField) cellEditor
        .getTableCellEditorComponent(table, Integer.MAX_VALUE, false, 0, INTEGER_COLUMN);
        // add valid digit - but exceeds Integer bounds so must not return true
        field.setText(field.getText() + "9");
        assertFalse("valid input but exceeds bounds " + field.getText(), cellEditor.stopCellEditing());
    }
    
    /**
     * Issue #1183-swingx: NumberEditorExt throws in getCellEditorValue if
     *   Integer (short, byte..) below/above min/max.
     *   
     * Check that stopCellEditing returns false if bounds exceeded, use strict
     */
    @Test
    public void testEditorStrictValueExceedBounds() {
        JFormattedTextField field = (JFormattedTextField) cellEditorStrict
            .getTableCellEditorComponent(table, Integer.MAX_VALUE, false, 0, INTEGER_COLUMN);
        // add valid digit - but exceeds Integer bounds so must not return true
        field.setText(field.getText() + "9");
        assertFalse("valid input but exceeds bounds " + field.getText(), cellEditorStrict.stopCellEditing());
    }
    
    /**
     * Issue #1183-swingx: NumberEditorExt throws in getCellEditorValue if
     *   Integer (short, byte..) below/above min/max.
     *   
     * Check IllegalStateException as doc'ed
     */
    @Test(expected = IllegalStateException.class)
    public void testEditorValueIllegalState() {
        JFormattedTextField field = (JFormattedTextField) cellEditor
        .getTableCellEditorComponent(table, Integer.MAX_VALUE, false, 0, INTEGER_COLUMN);
        // add valid digit - but exceeds Integer bounds so must not return true
        field.setText(field.getText() + "9");
        cellEditor.getCellEditorValue();
    }

    /**
     * Issue ??-swingx: editor with strict number formatter throws on 
     *    committing null value.
     *   
     *  happens only if active editor in table, so this test fails 
     */
    @Test
    public void testEditorStrictNullValue() {
        table.getColumn(INTEGER_COLUMN).setCellEditor(cellEditorStrict);
        table.editCellAt(0, INTEGER_COLUMN);
        cellEditorStrict.stopCellEditing();
    }
    
    /**
     * Issue ??-swingx: editor with strict number formatter throws on 
     *    committing null value.
     *   
     */
    @Test
    public void testEditorStrictNullValueStandAlone() {
        cellEditorStrict
            .getTableCellEditorComponent(table, null, false, 0, INTEGER_COLUMN);
        cellEditorStrict.getCellEditorValue();
    }

//----------------- interactive
    
    /**
     * Issue #1183-swingx: NumberEditorExt throws in getCellEditorValue if
     *   Integer (short, byte..) below/above min/max.
     *   
     *  Visualize JFormattedText behaviour.
     *   
     *  Problem is that the formattedTextField.isvalid/isValidEdit only check for 
     *  formal validity. So the assumption that at the time of constructor 
     *  invokation later the value is valid is incorrect. 
     */
    public void interactiveNumberOutOfBounds() {
        StrictNumberFormatter formatter = new StrictNumberFormatter(NumberFormat.getIntegerInstance());
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setMinimum(Integer.MIN_VALUE);
        final JFormattedTextField field = new JFormattedTextField(
                formatter) {

                    @Override
                    protected void invalidEdit() {
                        LOG.info("invalid .... " + getText());
                    }
            
        };
        final JCheckBox validBox = new JCheckBox("valid");
        final JCheckBox editValid = new JCheckBox("edit valid");
        DocumentListener listener = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                LOG.info("insert " + e);
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                // TODO Auto-generated method stub
                LOG.info("remove " + e);
                changedUpdate(e);
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                validBox.setSelected(field.isValid());
                editValid.setSelected(field.isEditValid());
                try {
                    field.commitEdit();
                } catch (ParseException e1) {
                    // TODO Auto-generated catch block
//                    e1.printStackTrace();
                }
                LOG.info("current value " + field.getValue());
            }
            
        };
        field.getDocument().addDocumentListener(listener);
        field.setText(TOO_BIG_INTEGER);
        
        JComponent content = Box.createVerticalBox();
        content.add(field);
        content.add(validBox);
        content.add(editValid);
        JXFrame frame = showInFrame(content, "valid states of formatted text");
        show(frame, 300, 200);
    }

    public void interactiveNumberInputVerifier() {
        final JFormattedTextField field = new JFormattedTextField(NumberFormat.getIntegerInstance()) {

            @Override
            protected void invalidEdit() {
                LOG.info("got invalid edit");
                super.invalidEdit();
            }
            
        };
        field.setText("20");
        try {
            field.commitEdit();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        InputVerifier verifier = new InputVerifier() {
             
            @Override
            public boolean verify(JComponent input) {
                boolean result = ((Number) field.getValue()).intValue() < 200;
                if (!result) LOG.info("in verifier");
                return result;
            }
        };
        field.setInputVerifier(verifier);
        final JFormattedTextField other = new JFormattedTextField(NumberFormat.getIntegerInstance());
        JComponent content = Box.createVerticalBox();
        content.add(field);
        content.add(other);
        showInFrame(content, "");
    }
    
    /**
     * Issue ??-swingx: localized NumberEditor using strict.
     * 
     * 
     */
    public void interactiveStrictNumberEditorAllTypesAsNumber() {
        @SuppressWarnings("unused")
        final Class<?>[] classes = new Class[] {Byte.class, Short.class, Integer.class,
                Float.class, Double.class, BigInteger.class, BigDecimal.class, Number.class};
        DefaultTableModel model = new DefaultTableModel(new String[] {
                "Byte", "Short", "Integer", "Float", "Double", "BigInteger", 
                "BigDecimal", "Number"}, 10) {
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Number.class;
            }
            
        };
        model.setValueAt(Byte.MAX_VALUE, 0, 0);
        model.setValueAt(Short.MAX_VALUE, 0, 1);
        model.setValueAt(Integer.MAX_VALUE, 0, 2);
        model.setValueAt(Float.MAX_VALUE, 0, 3);
        model.setValueAt(Double.MAX_VALUE, 0, 4);
        model.setValueAt(new BigInteger(TOO_BIG_INTEGER), 0, 5);
        model.setValueAt(new BigDecimal("44444444444444.666666666666666666"), 0, 6);
        final JXTable table = new JXTable(model);
        table.setSurrendersFocusOnKeystroke(true);
        NumberEditorExt strictEditor = new NumberEditorExt(true);
        table.setDefaultEditor(Number.class, strictEditor);
        showWithScrollingInFrame(table, "Extended NumberEditors: number class");
    }
    
    /**
     * Issue ??-swingx: localized NumberEditor using strict.
     * 
     * 
     */
    public void interactiveStrictNumberEditorAllTypes() {
        final Class<?>[] classes = new Class[] {Byte.class, Short.class, Integer.class,
                Float.class, Double.class, BigInteger.class, BigDecimal.class, Number.class};
        DefaultTableModel model = new DefaultTableModel(new String[] {
                "Byte", "Short", "Integer", "Float", "Double", "BigInteger", 
                "BigDecimal", "Number"}, 10) {
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return classes[columnIndex];
            }
            
        };
        model.setValueAt(Byte.MAX_VALUE, 0, 0);
        model.setValueAt(Short.MAX_VALUE, 0, 1);
        model.setValueAt(Integer.MAX_VALUE, 0, 2);
        model.setValueAt(Float.MAX_VALUE, 0, 3);
        model.setValueAt(Double.MAX_VALUE, 0, 4);
        model.setValueAt(new BigInteger(TOO_BIG_INTEGER), 0, 5);
        model.setValueAt(new BigDecimal("44444444444444.666666666666666666"), 0, 6);
        final JXTable table = new JXTable(model);
        table.setSurrendersFocusOnKeystroke(true);
        NumberEditorExt strictEditor = new NumberEditorExt(true);
        table.setDefaultEditor(Number.class, strictEditor);
        table.setDefaultEditor(Double.class, strictEditor);
        table.setDefaultEditor(Float.class, strictEditor);
        showWithScrollingInFrame(table, "Extended NumberEditors: concrete Number classes");
    }
    
    /**
     * Issue #393-swingx: localized NumberEditor.
     * 
     * Playing ... looks working :-)
     * 
     * 
     */
    public void interactiveNumberEditor() {
        final int doubleColumns = 3;
        final int integerColumns = 6;
        DefaultTableModel model = new DefaultTableModel(new String[] {
                "Double-core", "Double-ext", "Double-extstrict", 
                "Integer-core", "Integer-ext", "Integer-extstrict",
        "Object" }, 10) {
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Number.class;
            }
            
        };
        final JXTable table = new JXTable(model);
        table.setSurrendersFocusOnKeystroke(true);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i < doubleColumns) {
                table.setValueAt(10.2, 0, i);
            } else {
                table.setValueAt(10, 0, i);
            }
            
        }
        NumberEditor numberEditor = new NumberEditor();
        table.getColumn(0).setCellEditor(numberEditor);
        table.getColumn(doubleColumns).setCellEditor(numberEditor);
        NumberEditorExt strictEditor = new NumberEditorExt(true);
        table.getColumn(doubleColumns -1).setCellEditor(strictEditor);
        table.getColumn(integerColumns -1).setCellEditor(strictEditor);
        showWithScrollingInFrame(table, "Extended NumberEditors: Number.class");
    }
    
    /**
     * Issue #393-swingx: localized NumberEditor.
     * 
     * Playing ... looks working :-)
     * 
     * 
     */
    public void interactiveFloatingPointEditor() {
        final int doubleColumns = 3;
        final int integerColumns = 6;
        DefaultTableModel model = new DefaultTableModel(new String[] {
                "Double-core", "Double-ext", "Double-extstrict", 
                "Integer-core", "Integer-ext", "Integer-extstrict",
                "Object" }, 10) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex < doubleColumns) {
                    return Double.class;
                }
                if (columnIndex < integerColumns) {
                    return Integer.class;

                }
                return Object.class;
            }

        };
        final JXTable table = new JXTable(model);
        table.setSurrendersFocusOnKeystroke(true);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i < doubleColumns) {
                table.setValueAt(10.2, 0, i);
            } else {
                table.setValueAt(10, 0, i);
            }

        }
        NumberEditor numberEditor = new NumberEditor();
        table.getColumn(0).setCellEditor(numberEditor);
        table.getColumn(doubleColumns).setCellEditor(numberEditor);
        NumberEditorExt strictEditor = new NumberEditorExt(true);
        table.getColumn(doubleColumns -1).setCellEditor(strictEditor);
        table.getColumn(integerColumns -1).setCellEditor(strictEditor);
        showWithScrollingInFrame(table, "Extended NumberEditors: Double/Integer");
    }

    /**
     *  Issue #??-swingx: default number editor shows 3 digits only.
     *  
     *  Compare with plain JFromattedTextField and default NumberFormat - same. 
     *  To see, type a number with fractional digits > 3 in the first text field
     *  and press commit or transfer focus away. 
     */
    public void interactiveFloatingPointEditorDigits(){
        final int doubleColumns = 4;
        DefaultTableModel model = new DefaultTableModel(
                new String[] {"Double-default", "Double-customMaxDigits", "Double-ext", "Double-customMaxDigits"}, 10) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex < doubleColumns) {
                    return Double.class;
                }
                return Integer.class;
            }
            
        };
        final JXTable table = new JXTable(model);
        table.setSurrendersFocusOnKeystroke(true);
        for (int i = 0; i < doubleColumns; i++) {
            table.setValueAt(10.123456789, 0, i);
            table.setValueAt(Double.MAX_VALUE, 1, i);
        }
        NumberFormat moreFractionalDigits = NumberFormat.getInstance();
        moreFractionalDigits.setMaximumFractionDigits(20);
        NumberEditorExt numberEditor = new NumberEditorExt(moreFractionalDigits);
        table.getColumn(1).setCellEditor(numberEditor);
        table.getColumn(2).setCellEditor(new NumberEditorExt(true));
        table.getColumn(3).setCellEditor(new NumberEditorExt(moreFractionalDigits, true));
        JXFrame frame = showWithScrollingInFrame(table, "Extended NumberEditors: Doubles");
        Format format = NumberFormat.getInstance();
        final JFormattedTextField field = new JFormattedTextField(format);
        field.setColumns(10);
        final JFormattedTextField target = new JFormattedTextField(moreFractionalDigits);
        target.setColumns(10);
        field.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                target.setValue(field.getValue());
                LOG.info("value: " + field.getValue());
            }
            
        });
        FocusAdapter focusAdapter = new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                LOG.info("field value: " + field.getValue());
                LOG.info("table value: " + table.getValueAt(0, 1));
            }
            
        };
        field.addFocusListener(focusAdapter);
        table.addFocusListener(focusAdapter);
        addStatusComponent(frame, field);
        addStatusComponent(frame, target);
    }


    @Before
    @Override
    public void setUp() throws Exception {
        DefaultTableModel model = new DefaultTableModel(5, COLUMN_COUNT) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == INTEGER_COLUMN)
                    return Integer.class;
                if (columnIndex == NUMBER_COLUMN) 
                    return Number.class;
                return super.getColumnClass(columnIndex);
            }
            
        };
        table = new JXTable(model);
        cellEditor = new NumberEditorExt(NumberFormat.getIntegerInstance());
        cellEditorStrict = new NumberEditorExt(NumberFormat.getIntegerInstance(), true);
    }

    
}
