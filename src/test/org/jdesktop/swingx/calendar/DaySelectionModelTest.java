/*
 * $Id$
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;

/**
 * Test DaySelectionModel.
 * 
 * @author Jeanette Winzenburg
 */
public class DaySelectionModelTest extends InteractiveTestCase {

    // pre-defined reference dates - all relative to current date at around 5 am
    private Date today;
    private Date tomorrow;
    private Date afterTomorrow;
    private Date yesterDay;
    // calendar default instance init with today
    private Calendar calendar;


    /**
     * expose more selection constraint methods in JXMonthView
     *
     */
    public void testUpperBound() {
        DateSelectionModel monthView = new DaySelectionModel();
        monthView.setUpperBound(today);
        assertEquals(startOfDay(today), monthView.getUpperBound());
        // remove again
        monthView.setUpperBound(null);
        assertEquals(null, monthView.getUpperBound());
    }
    
    /**
     * expose more selection constraint methods in JXMonthView
     *
     */
    public void testLowerBound() {
        DateSelectionModel monthView = new DaySelectionModel();
        monthView.setLowerBound(today);
        assertEquals(startOfDay(today), monthView.getLowerBound());
        // remove again
        monthView.setLowerBound(null);
        assertEquals(null, monthView.getLowerBound());
    }

    /**
     * test unselectable: use methods with Date.
     *
     */
    public void testUnselectableDate() {
        DateSelectionModel monthView = new DaySelectionModel();
        // initial
        assertFalse(monthView.isUnselectableDate(today));
        // set unselectable today
        SortedSet<Date> unselectables = new TreeSet<Date>();
        unselectables.add(today);
        monthView.setUnselectableDates(unselectables);
        assertTrue("raqw today must be unselectable", 
                monthView.isUnselectableDate(today));
        assertTrue("start of today must be unselectable", 
                monthView.isUnselectableDate(startOfDay(today)));
        assertTrue("end of today must be unselectable", 
                monthView.isUnselectableDate(endOfDay(today)));
        monthView.setUnselectableDates(new TreeSet<Date>());
        assertFalse(monthView.isUnselectableDate(today));
        assertFalse(monthView.isUnselectableDate(startOfDay(today)));
        assertFalse(monthView.isUnselectableDate(endOfDay(today)));
    }

    /**
     * Issue #494-swingx: JXMonthView changed all passed-in dates
     *
     */
    public void testCleanupCopyDate() {
        DateSelectionModel monthView = new DaySelectionModel();
        Date copy = new Date(today.getTime());
        monthView.setSelectionInterval(today, today);
        assertEquals("the date used for selection must be unchanged", copy, today);
    }
   
    public void testEmptySelectionInitial() {
        DateSelectionModel monthView = new DaySelectionModel();
        assertTrue(monthView.isSelectionEmpty());
        SortedSet<Date> selection = monthView.getSelection();
        assertTrue(selection.isEmpty());
    }
    
    public void testEmptySelectionClear() {
        DateSelectionModel monthView = new DaySelectionModel();
        monthView.setSelectionInterval(today, today);
        // sanity
        assertTrue(1 == monthView.getSelection().size());

        monthView.clearSelection();
        assertTrue(monthView.isSelectionEmpty());
        assertTrue(monthView.getSelection().isEmpty());
    }

    public void testSingleSelection() {
        DateSelectionModel monthView = new DaySelectionModel();
        monthView.setSelectionMode(SelectionMode.SINGLE_SELECTION);

        monthView.setSelectionInterval(today, today);
        SortedSet<Date> selection = monthView.getSelection();
        assertTrue(1 == selection.size());
        assertEquals(startOfDay(today), selection.first());

        monthView.setSelectionInterval(today, afterTomorrow);
        selection = monthView.getSelection();
        assertTrue(1 == selection.size());
        assertEquals(startOfDay(today), selection.first());
    }
    
    public void testSingleIntervalSelection() {
        DateSelectionModel monthView = new DaySelectionModel();
        monthView.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);

        monthView.setSelectionInterval(today, today);
        SortedSet<Date> selection = monthView.getSelection();
        assertTrue(1 == selection.size());
        assertEquals(startOfDay(today), selection.first());

        monthView.setSelectionInterval(today, tomorrow);
        
        selection = monthView.getSelection();
        assertEquals(2, selection.size());
        assertEquals(startOfDay(today), selection.first());
        assertEquals(startOfDay(tomorrow), selection.last());
    }

    public void testWeekIntervalSelection() {
        //TODO...
    }

    public void testMultipleIntervalSelection() {
        DaySelectionModel monthView = new DaySelectionModel();
        monthView.setSelectionMode(SelectionMode.MULTIPLE_INTERVAL_SELECTION);

        monthView.setSelectionInterval(yesterDay, yesterDay);
        monthView.addSelectionInterval(afterTomorrow, afterTomorrow);
        
        SortedSet<Date> selection = monthView.getSelection();
        assertEquals(2, selection.size());
        assertEquals(startOfDay(yesterDay), selection.first());
        assertEquals(startOfDay(afterTomorrow), selection.last());
    }

    private Date startOfDay(Date date) {
        calendar.setTime(date);
        CalendarUtils.startOfDay(calendar);
        return calendar.getTime();
    }

    private Date endOfDay(Date date) {
        calendar.setTime(date);
        CalendarUtils.endOfDay(calendar);
        return calendar.getTime();
    }

    @Override
    protected void setUp() throws Exception {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        today = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        yesterDay = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        tomorrow = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        afterTomorrow = calendar.getTime();
        
        calendar.setTime(today);
    }

    
}
