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
package org.jdesktop.swingx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import org.jdesktop.swingx.calendar.JXMonthView;
import org.jdesktop.swingx.calendar.JXMonthView.SelectionMode;
import org.jdesktop.swingx.test.XTestUtils;
import org.jdesktop.test.ActionReport;

/**
 * Known issues of <code>JXDatePicker</code>.
 * 
 * @author Jeanette Winzenburg
 */
public class JXDatePickerIssues extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(JXDatePickerIssues.class
            .getName());
    public static void main(String[] args) {
//        setSystemLF(true);
        JXDatePickerIssues  test = new JXDatePickerIssues();
        try {
            test.runInteractiveTests();
//          test.runInteractiveTests(".*Show.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }


    private Calendar calendar;

    /**
     * compare JFormattedTextField and JXDatePicker pref.
     * date is slightly cut. Looks like an issue 
     * of the formattedTextField.
     */
    public void interactivePrefSize() {
//        ListSelectionModel l;
//        TreeSelectionModel t;
        JXDatePicker picker = new JXDatePicker();
        JFormattedTextField field = new JFormattedTextField(new JXDatePickerFormatter());
        field.setValue(picker.getDate());
        JComponent panel = new JPanel();
        panel.add(picker);
        panel.add(field);
        JXFrame frame = showInFrame(panel, "compare pref width");
        // JXRootPane eats esc 
        frame.getRootPaneExt().getActionMap().remove("esc-action");
    }

    /**
     * visual testing of selection constraints: upper/lower bounds.
     *
     */
    public void interactiveBounds() {
        JXDatePicker picker = new JXDatePicker();
        calendar.add(Calendar.DAY_OF_MONTH, 10);
        // access the model directly requires to "clean" the date
//        XTestUtils.getCleanedDate(calendar);
//        picker.getMonthView().getSelectionModel().setUpperBound(calendar.getTime());
        picker.getMonthView().setUpperBound(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_MONTH, - 20);
        picker.getMonthView().setLowerBound(calendar.getTimeInMillis());
//        picker.getMonthView().getSelectionModel().setLowerBound(calendar.getTime());
        showInFrame(picker, "bounds");
    }
  
    /**
     * something weird's going on: the picker's date must be null
     * after setting a monthView with null selection. It is, until
     * shown?
     *
     */
    public void interactiveShowPickerSetMonthNull() {
        JXDatePicker picker = new JXDatePicker();
        JXMonthView intervalForPicker = new JXMonthView();
        intervalForPicker.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        picker.setMonthView(intervalForPicker);
        LOG.info("picker date before showing " + picker.getDate());
        assertNull(picker.getDate());
        JXFrame frame = showInFrame(picker, "initial null date");
        // JXRootPane eats esc 
        frame.getRootPaneExt().getActionMap().remove("esc-action");
        LOG.info("picker date after showing " + picker.getDate());
        assertNull(picker.getDate());

    }
    /**
     * Issue #235-swingx: action events
     * 
     * Compare textfield, formatted, picker, combo after keyboard.
     * - simple field fires on enter always
     * - formatted (and picker) fire on enter if value had been edited
     *
     * Picker
     * - fires on click into monthView only if clicked date different
     * - doesn't close monthview on enter/escape if unchanged
     * 
     * MonthView
     * - fires on click always
     * - fires on esc/enter only if selection changed
     * 
     * ComboBox
     * - fires on enter always
     * - fires on click in dropdown
     * 
     */
    public void interactiveActionEvent() {
        JXDatePicker picker = new JXDatePicker();
//        picker.setDate(null);
        JTextField simpleField = new JTextField("simple field");
        JFormattedTextField textField = new JFormattedTextField(DateFormat.getDateInstance());
        textField.setValue(new Date());
        JComboBox box = new JComboBox(new Object[] {"one", "two", "three"});
        box.setEditable(true);
        
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got action from: " + e.getSource().getClass().getName() + 
                        "\n" + e);
            }
            
        };
        simpleField.addActionListener(l);
        textField.addActionListener(l);
        picker.addActionListener(l);
//        picker.getMonthView().addActionListener(l);
        box.addActionListener(l);
        JPanel panel = new JPanel();
        panel.add(simpleField);
        panel.add(textField);
        panel.add(picker);
        panel.add(box);
        
        JXFrame frame = showInFrame(panel, "trace action events: keyboard");
        // JXRootPane eats esc 
        frame.getRootPaneExt().getActionMap().remove("esc-action");
    }

    /**
     * Issue #235-swingx: action events
     * 
     * Compare textfield, formatted, picker and combo: programatic change.
     * - only picker and combo fire
     * 
     */
    public void interactiveActionEventSetValue() {
        final JXDatePicker picker = new JXDatePicker();
//        picker.setDate(null);
        final JTextField simpleField = new JTextField("simple field");
        final JFormattedTextField textField = new JFormattedTextField(DateFormat.getDateInstance());
        textField.setValue(new Date());
        final JComboBox box = new JComboBox(new Object[] {"one", "two", "three"});
        box.setEditable(true);
        
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got action from: " + e.getSource().getClass().getName() + 
                        "\n" + e);
            }
            
        };
        simpleField.addActionListener(l);
        textField.addActionListener(l);
        picker.addActionListener(l);
        picker.getMonthView().addActionListener(l);
        box.addActionListener(l);
        Action action = new AbstractAction("set new value") {
            int dayToAdd = 1;
            public void actionPerformed(ActionEvent e) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, dayToAdd++);
                Date date = cal.getTime();
                String text = DateFormat.getDateInstance().format(date);
                simpleField.setText(text);
                textField.setValue(date);
                picker.setDate(date);
                box.setSelectedItem(text);
            }
            
        };
        
        JPanel panel = new JPanel();
        panel.add(simpleField);
        panel.add(textField);
        panel.add(picker);
        panel.add(box);
        
        JXFrame frame = showInFrame(panel, "trace action events: programmatic change");
        // JXRootPane eats esc 
        frame.getRootPaneExt().getActionMap().remove("esc-action");
        addAction(frame, action);
    }


    /**
     * Issue #99-swingx: null date and opening popup forces selection.
     * Status? Looks fixed..
     * 
     * Sizing issue if init with null date
     */
    public void interactiveNullDate() {
        JXDatePicker picker = new JXDatePicker();
        picker.setDate(null);
        JPanel panel = new JPanel();
        panel.add(picker);
        JXFrame frame = showInFrame(panel, "null date");
        // JXRootPane eats esc 
        frame.getRootPaneExt().getActionMap().remove("esc-action");
    }
 
    
//-------------------- unit tests
    /**
     * test that selectionListener is uninstalled.
     * 
     * Hmm ... missing api or overshooting?
     */
    public void testSelectionListening() {
//        JXMonthView monthView = new JXMonthView();
//        int selectionListenerCount = monthView.getSelectionModel()).getListeners().length;
//        JXDatePicker picker = new JXDatePicker();
//        assertEquals("ui must have installed one listener", selectionListenerCount + 1, 
//                picker.getMonthView().getSelectionModel().getListeners().length);
//        picker.getUI().uninstallUI(picker);
//        assertEquals("", selectionListenerCount, 
//                picker.getMonthView().getSelectionModel().getListeners().length);
    }
    

    /**
     * Characterization: when does the picker fire an action event?
     * @throws ParseException
     */
    public void testDatePickerFireOnSelection() throws ParseException {
        JXDatePicker picker = new JXDatePicker();
        ActionReport report = new ActionReport();
        picker.addActionListener(report);
        Date date = XTestUtils.getCleanedToday(5);
        picker.getMonthView().setSelectionInterval(date, date);
        assertEquals(date, picker.getEditor().getValue());
        assertEquals(1, report.getEventCount());
        fail("need to define when action events are fired");
    }

    /**
     * Characterization: when does the picker fire an action event?
     * @throws ParseException
     */
    public void testDatePickerFireOnSetDate() throws ParseException {
        JXDatePicker picker = new JXDatePicker();
        ActionReport report = new ActionReport();
        picker.addActionListener(report);
        // fires on setDate
        picker.setDate(null);
        assertEquals(1, report.getEventCount());
        fail("need to define when action events are fired");
    }




    /**
     * For comparison: behaviour of JComboBox on setEditor.
     *
     */
    public void testEditorValueOnCombo() {
        String[] items = new String[]{"one", "two"};
        JComboBox box = new JComboBox(items);
        box.setEditable(true);
        Object value = box.getEditor().getItem();
        // sanity
        assertEquals(items[0], value);
        ComboBoxEditor editor = new BasicComboBoxEditor();
        box.setEditor(editor);
        assertEquals(value, box.getEditor().getItem());
    }

    

    @Override
    protected void setUp() throws Exception {
        calendar = Calendar.getInstance();
    }
    
    
}
