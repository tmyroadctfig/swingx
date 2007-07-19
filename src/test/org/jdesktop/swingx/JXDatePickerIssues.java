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
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import org.jdesktop.swingx.calendar.JXMonthView;
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
        setSystemLF(true);
        JXDatePickerIssues  test = new JXDatePickerIssues();
        try {
            test.runInteractiveTests();
//          test.runInteractiveTests(".*Text.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    /**
     * Issue #235-swingx: action events
     * 
     * Compare textfield, formatted and picker.
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
     */
    public void interactiveActionEvent() {
        JXDatePicker picker = new JXDatePicker();
        JTextField simpleField = new JTextField("simple field");
        JFormattedTextField textField = new JFormattedTextField(DateFormat.getDateInstance());
        textField.setValue(new Date());
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got action from: " + e.getSource().getClass().getName());
            }
            
        };
        simpleField.addActionListener(l);
        textField.addActionListener(l);
        picker.addActionListener(l);
        picker.getMonthView().addActionListener(l);
        JPanel panel = new JPanel();
        panel.add(simpleField);
        panel.add(textField);
        panel.add(picker);
        JXFrame frame = showInFrame(panel, "trace action events");
        // JXRootPane eats esc 
        frame.getRootPaneExt().getActionMap().remove("esc-action");
    }

    /**
     * Issue #99-swingx: null date and opening popup forces selection.
     * Status? Looks fixed..
     *
     */
    public void interactiveNullDate() {
        JXDatePicker picker = new JXDatePicker();
        picker.setDate(null);
        showInFrame(picker, "null date");
    }
 
//-------------------- unit tests
    
    /**
     * sanity: when does the picker fire an action event?
     * @throws ParseException
     */
    public void testDatePickerFire() throws ParseException {
        JXDatePicker picker = new JXDatePicker();
        ActionReport report = new ActionReport();
        picker.addActionListener(report);
        // fires on setDate
        picker.setDate(null);
        assertEquals(1, report.getEventCount());
    }



    /**
     * Who rules? the picker or the month view?
     * Neither - the month view's dateSelectionModel.
     * 
     * Setting the monthview resets the 
     * picker's date to the view's selected - should be documented?
     *
     */
    public void testDatePickerSetMonthView() {
        JXDatePicker picker = new JXDatePicker();
        Date date = picker.getDate();
        // why? null selection is perfectly valid?
        assertNotNull(date);
        picker.setMonthView(new JXMonthView());
        assertEquals(date, picker.getDate());
    }
    
    /**
     * Allowed to set a null editor? No (which is reasonable) 
     * - should be documented as of SwingX doc convention.
      */
    public void testEditorNull() {
       JXDatePicker picker = new JXDatePicker();
       assertNotNull(picker.getEditor());
       picker.setEditor(null);
    }

    /**
     * Issue ??-swingx: editor value must preserve value on LF switch.
     * This is a side-effect of picker not updating the editor's value
     * on setEditor.
     *
     */
    public void testEditorUpdateOnLF() {
        JXDatePicker picker = new JXDatePicker();
        Object date = picker.getEditor().getValue();
        picker.updateUI();
        assertEquals(date, picker.getEditor().getValue());
    }

    /**
     * Issue ??-swingx: editor value not updated after setEditor
     * who should set it? ui-delegate when listening to editor property change?
     * or picker in setEditor?
     * 
     * Compare to JComboBox: BasicComboUI listens to editor change, does internal
     * wiring to editor and call's comboBox configureEditor with the value of the 
     * old editor.
     */
    public void testEditorValueOnSetEditor() {
        JXDatePicker picker = new JXDatePicker();
        Object value = picker.getEditor().getValue();
        picker.setEditor(new JFormattedTextField(new JXDatePickerFormatter()));
        assertEquals(value, picker.getEditor().getValue());
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
    
//    @Override
//    protected void setUp() throws Exception {
//        defaultToSystemLF = true;
//        setSystemLF(defaultToSystemLF);
//    }
//    
    
}
