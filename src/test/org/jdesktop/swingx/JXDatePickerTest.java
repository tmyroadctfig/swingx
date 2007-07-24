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
package org.jdesktop.swingx;

import java.awt.Dimension;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JFormattedTextField;

import junit.framework.TestCase;

import org.jdesktop.swingx.calendar.JXMonthView;

/**
 * Created by IntelliJ IDEA.
 * User: joutwate
 * Date: Jun 1, 2006
 * Time: 6:58:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class JXDatePickerTest extends TestCase {
    private Calendar cal;

    public void setUp() {
        cal = Calendar.getInstance();
    }

    public void teardown() {
    }

    /**
     * Issue #551-swingx: editor value not updated after setEditor. 
     * 
     * quick&dirty fix: let the picker manually update.
     * 
     * who should set it? ui-delegate when listening to editor property change?
     * or picker in setEditor?
     * 
     * Compare to JComboBox: BasicComboUI listens to editor change, does internal
     * wiring to editor and call's comboBox configureEditor with the value of the 
     * old editor.
     * 
     * 
     */
    public void testEditorValueOnSetEditor() {
        JXDatePicker picker = new JXDatePicker();
        Object value = picker.getEditor().getValue();
        picker.setEditor(new JFormattedTextField(new JXDatePickerFormatter()));
        assertEquals(value, picker.getEditor().getValue());
    }
    
    /**
     * Issue #551-swingx: editor value must preserve value on LF switch.
     * 
     * This is a side-effect of picker not updating the editor's value
     * on setEditor.
     *
     * @see #testEditorValueOnSetEditor
     */
    public void testEditorUpdateOnLF() {
        JXDatePicker picker = new JXDatePicker();
        Object date = picker.getEditor().getValue();
        picker.updateUI();
        assertEquals(date, picker.getEditor().getValue());
    }


    /**
     * Issue #554-swingx: timezone of formats and picker must be synched.
     */
    public void testTimeZoneInitialSynched() {
        JXDatePicker picker = new JXDatePicker();
        assertNotNull(picker.getTimeZone());
        for (DateFormat format : picker.getFormats()) {
            assertEquals("timezone must be synched", picker.getTimeZone(), format.getTimeZone());
        }
    }
    
    /**
     * Characterization: setting the monthview updates
     * the datePicker's date to the monthView's current
     * selection.
     * Here: monthview with empty selection.
     *
     */
    public void testDatePickerSetMonthViewWithEmptySelection() {
        JXDatePicker picker = new JXDatePicker();
        Date date = picker.getDate();
        // sanity
        assertNotNull(date);
        JXMonthView monthView = new JXMonthView();
        Date selectedDate = monthView.getSelectedDate();
        assertNull(selectedDate);
        picker.setMonthView(monthView);
        // okay, seems to be that the monthView rules 
        assertEquals(selectedDate, picker.getDate());
    }
    
    /**
     * Characterization: setting the monthview updates
     * the datePicker's date to the monthView's current
     * selection.
     * Here: monthview with selection.
     *
     */
    public void testDatePickerSetMonthViewWithSelection() {
        JXDatePicker picker = new JXDatePicker();
        JXMonthView monthView = new JXMonthView();
        Date other = new GregorianCalendar(2007, 6, 28).getTime();
        monthView.setSelectionInterval(other, other);
        // sanity
        assertFalse(other.equals(picker.getDate()));
        picker.setMonthView(monthView);
        // okay, seems to be that the monthView rules 
        assertEquals("montview selection/picker date must be equal after setMonthView ", 
                other, picker.getDate());
    }
    
    /**
     * PrefSize should be independent of empty/filled picker. 
     * If not, the initial size might appear kind of collapsed.
     *
     */
    public void testPrefSizeEmptyEditor() {
        JXDatePicker picker = new JXDatePicker();
        // sanity 
        assertNotNull(picker.getDate());
        Dimension filled = picker.getPreferredSize();
        picker.setDate(null);
        Dimension empty = picker.getPreferredSize();
        assertEquals("pref width must be same for empty/filled", filled.width, empty.width);
    }
    
    public void testDefaultConstructor() {
        JXDatePicker datePicker = new JXDatePicker();
        cal.setTimeInMillis(System.currentTimeMillis());
        Date today = cleanupDate(cal);
        assertTrue(today.equals(datePicker.getDate()));
        assertTrue(today.getTime() == datePicker.getDateInMillis());
    }

    public void testConstructor() {
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_MONTH, 5);
        Date expectedDate = cleanupDate(cal);
        JXDatePicker datePicker = new JXDatePicker(cal.getTimeInMillis());
        assertTrue(expectedDate.equals(datePicker.getDate()));
        assertTrue(expectedDate.getTime() == datePicker.getDateInMillis());
    }

    public void testNullSelection() {
        JXDatePicker datePicker = new JXDatePicker();
        datePicker.setDate(null);
        assertTrue(null == datePicker.getDate());
        assertTrue(-1 == datePicker.getDateInMillis());
    }

    public void testSetDate() {
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_MONTH, 5);
        Date expectedDate = cleanupDate(cal);
        JXDatePicker datePicker = new JXDatePicker();
        datePicker.setDate(cal.getTime());
        assertTrue(expectedDate.equals(datePicker.getDate()));
        assertTrue(expectedDate.equals(datePicker.getEditor().getValue()));

        datePicker.setDate(null);
        assertTrue(null == datePicker.getDate());
        assertTrue(null == datePicker.getEditor().getValue());
    }

    private Date cleanupDate(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}