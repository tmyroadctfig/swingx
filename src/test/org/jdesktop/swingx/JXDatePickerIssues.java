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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SortedSet;
import java.util.TimeZone;
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


    private Calendar calendar;

    /**
     * NYI: visual testing of bounds.
     *
     */
    public void interactiveBounds() {
        JXDatePicker picker = new JXDatePicker();
        calendar.add(Calendar.MONTH, 2);
        cleanupDate(calendar);
        picker.getMonthView().getSelectionModel().setUpperBound(calendar.getTime());
        calendar.add(Calendar.MONTH, - 4);
        picker.getMonthView().getSelectionModel().setLowerBound(calendar.getTime());
        showInFrame(picker, "bounds");
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
     * ComboBox
     * - fires on enter always
     * - fires on click in dropdown
     * 
     */
    public void interactiveActionEvent() {
        JXDatePicker picker = new JXDatePicker();
        picker.setDate(null);
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
        picker.getMonthView().addActionListener(l);
        box.addActionListener(l);
        JPanel panel = new JPanel();
        panel.add(simpleField);
        panel.add(textField);
        panel.add(picker);
        panel.add(box);
        
        JXFrame frame = showInFrame(panel, "trace action events");
        // JXRootPane eats esc 
        frame.getRootPaneExt().getActionMap().remove("esc-action");
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
        showInFrame(panel, "null date");
    }
 
    public void interactiveInitialDate() {
        long todaysDate = (new GregorianCalendar(2007, 6, 28)).getTimeInMillis();
        final JXDatePicker datePicker = new JXDatePicker();
        JXMonthView calend = new JXMonthView(todaysDate);


        calend.setTraversable(true);
        calend.setDayForeground(1, Color.RED);
        calend.setDayForeground(7, Color.RED);
        calend.setDaysOfTheWeekForeground(Color.BLUE);
        calend.setSelectedBackground(Color.YELLOW);
        calend.setFirstDayOfWeek(Calendar.MONDAY);
        datePicker.setMonthView(calend);
        showInFrame(datePicker, "null date");
    }
    
//-------------------- unit tests
    
    /**
     * Issue ??-swingx: timezone of formats and picker must be synched.
     */
    public void testTimeZoneInitialSynched() {
        JXDatePicker picker = new JXDatePicker();
        assertNotNull(picker.getTimeZone());
        for (DateFormat format : picker.getFormats()) {
            assertEquals("timezone must be synched", picker.getTimeZone(), format.getTimeZone());
        }
    }
    
    /**
     * Issue #554-swingx: timezone of formats and picker must be synched.
     */
    public void testTimeZoneModifiedSynched() {
        JXDatePicker picker = new JXDatePicker();
        TimeZone defaultZone = picker.getTimeZone();
        TimeZone alternative = TimeZone.getTimeZone("GMT-6");
        // sanity
        assertNotNull(alternative);
        if (alternative.equals(defaultZone)) {
            alternative = TimeZone.getTimeZone("GMT-7");
            // paranoid ... but shit happens
            assertNotNull(alternative);
            assertFalse(alternative.equals(defaultZone));
        }
        picker.setTimeZone(alternative);
        assertEquals(alternative, picker.getTimeZone());
        for (DateFormat format : picker.getFormats()) {
            assertEquals("timezone must be synched", picker.getTimeZone(), format.getTimeZone());
        }
    }
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
     * Neither? - the month view's dateSelectionModel.
     * 
     * Here: picker with date, monthView with empty
     * Setting the monthview resets the 
     * picker's date to the view's selected - should be documented?
     *
     */
    public void testDatePickerSetMonthViewWithEmptySelection() {
        JXDatePicker picker = new JXDatePicker();
        Date date = picker.getDate();
        // sanity
        assertNotNull(date);
        JXMonthView monthView = new JXMonthView();
        SortedSet<Date> selection = monthView.getSelection();
        Date selectedDate = selection.isEmpty() ? null : selection.first();
        assertNull(selectedDate);
        picker.setMonthView(monthView);
//        fail("need to clarify how to synch picker/monthView selection on setMonthView");
        // okay, seems to be that the monthView rules 
        assertEquals(selectedDate, picker.getDate());
    }
    
    /**
     * Who rules? the picker or the month view?
     * Neither? - the month view's dateSelectionModel.
     * 
     * Here: set the monthview's selection in constructor
     *
     */
    public void testDatePickerSetMonthViewWithSelection() {
        JXDatePicker picker = new JXDatePicker();
        Date date = picker.getDate();
        // why? null selection is perfectly valid?
        assertNotNull(date);
        JXMonthView monthView = new JXMonthView();
        Date other = new GregorianCalendar(2007, 6, 28).getTime();
        monthView.setSelectionInterval(other, other);
        picker.setMonthView(monthView);
        // okay, seems to be that the monthView rules 
        assertEquals("", other, picker.getDate());
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

    /**
     * NYI: respect unselectables - 
     *
     */
    public void testMonthViewUnselectableDates() {
        JXMonthView monthView = new JXMonthView();
        Date today = calendar.getTime();
        long[] unselectableDates = new long[]{today.getTime()};
        monthView.setUnselectableDates(unselectableDates);
        SortedSet<Date> unselectables = monthView.getSelectionModel().getUnselectableDates();
        // paranoid - use the date as returned from the model
        // to be sure it's "cleaned"
        monthView.setSelectionInterval(unselectables.first(), unselectables.first());
        assertEquals("selection must be empty", 0, monthView.getSelection().size());
    }
    
    /**
    *
    * Okay ... looks more like a confusing (me!) doc: the date
    * in the constructor is not the selection, but the date
    * to use for the first display. Hmm ...
    */
   public void testMonthViewInitialSelection() {
       JXMonthView monthView = new JXMonthView(new GregorianCalendar(2007, 6, 28).getTimeInMillis());
       SortedSet<Date> selection = monthView.getSelection();
       Date other = selection.isEmpty() ? null : selection.first();
       assertNotNull(other);
   }


   private Date cleanupDate(Calendar cal) {
       cal.set(Calendar.HOUR_OF_DAY, 0);
       cal.set(Calendar.MINUTE, 0);
       cal.set(Calendar.SECOND, 0);
       cal.set(Calendar.MILLISECOND, 0);
       return cal.getTime();
   }


    @Override
    protected void setUp() throws Exception {
        calendar = Calendar.getInstance();
    }
    
    
}
