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

    private static final String TOO_BIG_INTEGER = "1111111111111111111111";

    
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(NumberEditorExtTest.class.getName());
    
    public static void main(String[] args) {
        NumberEditorExtTest test = new NumberEditorExtTest();
        try {
//            test.runInteractiveTests();
            test.runInteractiveTests("interactive.*Number.*");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final int INTEGER_COLUMN = 0;
    /** a table with a model which has column class Integer in INTEGER_COLUMN. */
    private JXTable table;
    /** a NumberEditorExt configured with IntegerFormat. */
    private NumberEditorExt cellEditor;

    @Test
    public void testNumberEditorExt() {
        NumberFormat format = NumberFormat.getIntegerInstance();
        JFormattedTextField field = new JFormattedTextField(new NumberEditorNumberFormat(format));
        field.setText(TOO_BIG_INTEGER);
        try {
            field.commitEdit();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void testNumberFormatter() {
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setMaximum(Integer.MAX_VALUE - 1);
        formatter.setMinimum(Integer.MIN_VALUE + 1);
//        formatter.setValueClass(Integer.class);
        formatter.setAllowsInvalid(false);
        try {
            Number number = (Number) formatter.stringToValue(TOO_BIG_INTEGER);
            assertTrue(number instanceof Integer);
            LOG.info("too big: " + number );
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testNumberFormatInteger() {
        NumberFormat format = NumberFormat.getIntegerInstance();
        try {
            // this passes - everything fitting into double range is acceptable
            Number number = format.parse(TOO_BIG_INTEGER);
            // this blows - must fit into Integer.MIN/MAX
            new Integer(TOO_BIG_INTEGER);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
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
        final JFormattedTextField field = new JFormattedTextField(NumberFormat.getIntegerInstance());
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
                    e1.printStackTrace();
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
        JXFrame frame = showInFrame(content, "");
    }
    
    /**
     * Issue #393-swingx: localized NumberEditor.
     * 
     * Playing ... looks working :-)
     *
     *  
     */
    public void interactiveFloatingPointEditor(){
        DefaultTableModel model = new DefaultTableModel(
                new String[] {"Double-core", "Double-ext", "Integer-core", "Integer-ext", "Object"}, 10) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if ((columnIndex == 0) || (columnIndex == 1)) {
                    return Double.class;
                }
                if ((columnIndex == 2) || (columnIndex == 3)){
                    return Integer.class;
                }
                return Object.class;
            }
            
        };
        final JXTable table = new JXTable(model);
        table.setSurrendersFocusOnKeystroke(true);
        table.setValueAt(10.2, 0, 0);
        table.setValueAt(10.2, 0, 1);
        table.setValueAt(10, 0, 2);
        table.setValueAt(10, 0, 3);
        
        NumberEditor numberEditor = new NumberEditor();
        table.getColumn(0).setCellEditor(numberEditor);
        table.getColumn(2).setCellEditor(numberEditor);
        showWithScrollingInFrame(table, "Extended NumberEditors (col 1/3)");
    }

    /**
     *  Issue #??-swingx: default number editor shows 3 digits only.
     *  
     *  Compare with plain JFromattedTextField and default NumberFormat - same. 
     *  To see, type a number with fractional digits > 3 in the first text field
     *  and press commit or transfer focus away. 
     */
    public void interactiveFloatingPointEditorDigits(){
        DefaultTableModel model = new DefaultTableModel(
                new String[] {"Double-default", "Double-customMaxDigits"}, 10) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if ((columnIndex == 0) || (columnIndex == 1)) {
                    return Double.class;
                }
                if ((columnIndex == 2) || (columnIndex == 3)){
                    return Integer.class;
                }
                return Object.class;
            }
            
        };
        final JXTable table = new JXTable(model);
        table.setSurrendersFocusOnKeystroke(true);
        table.setValueAt(10.2, 0, 0);
        table.setValueAt(10.2, 0, 1);
        NumberFormat moreFractionalDigits = NumberFormat.getInstance();
        moreFractionalDigits.setMaximumFractionDigits(20);
        NumberEditorExt numberEditor = new NumberEditorExt(moreFractionalDigits);
        table.getColumn(1).setCellEditor(numberEditor);
        JXFrame frame = showWithScrollingInFrame(table, "Extended NumberEditors (col 1/3)");
        Format format = NumberFormat.getInstance();
        final JFormattedTextField field = new JFormattedTextField(format);
        field.setColumns(10);
        final JFormattedTextField target = new JFormattedTextField(format);
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
    }

    
}
