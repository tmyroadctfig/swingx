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
import java.util.TimeZone;
import java.util.TreeSet;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;
import org.jdesktop.swingx.event.DateSelectionEvent.EventType;
import org.jdesktop.swingx.test.DateSelectionReport;

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
    private DateSelectionModel model;

    /**
     * test synch of model properties with its calendar's properties.
     * Here: changed timeZone.
     */
    public void testCalendarTimeZoneNoChangeNoNotify() {
        // config with a known timezone and date
        TimeZone tz = model.getTimeZone();
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setTimeZone(tz);
        assertEquals(0, report.getEventCount());
    }

    /**
     * test synch of model properties with its calendar's properties.
     * Here: changed timeZone.
     */
    public void testCalendarTimeZoneChangedNotify() {
        // config with a known timezone and date
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        if (model.getTimeZone().equals(tz)) {
            tz = TimeZone.getTimeZone("GMT+5");
        }
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setTimeZone(tz);
        assertEquals(1, report.getEventCount());
        assertEquals(EventType.CALENDAR_CHANGED, report.getLastEventType());
    }

    /**
     * test synch of model properties with its calendar's properties.
     * Here: changed timeZone.
     */
    public void testCalendarTimeZoneChanged() {
        // config with a known timezone and date
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        if (model.getTimeZone().equals(tz)) {
            tz = TimeZone.getTimeZone("GMT+5");
        }
        model.setTimeZone(tz);
        assertEquals(tz, model.getTimeZone());
        assertEquals(tz, model.getCalendar().getTimeZone());
    }
    
    /**
     * test synch of model properties with its calendar's properties.
     * Here: initial timeZone.
     */
    public void testCalendarTimeZoneInitial() {
        assertEquals(calendar.getTimeZone(), model.getTimeZone());
        assertEquals(model.getTimeZone(), model.getCalendar().getTimeZone());
    }

    /**
     * test synch of model properties with its calendar's properties.
     * Here: no change notification if not changed minimalDaysInFirstWeek.
     */
    public void testCalendarMinimalDaysInFirstWeekNoChangeNoNotify() {
        int first = model.getMinimalDaysInFirstWeek();
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setMinimalDaysInFirstWeek(first);
        assertEquals(0, report.getEventCount());
    }

    /**
     * test synch of model properties with its calendar's properties.
     * Here: change notification of minimalDaysInFirstWeek.
     */
    public void testCalendarMinimalDaysInFirstWeekNotify() {
        int first = model.getMinimalDaysInFirstWeek() + 1;
        //sanity
        assertTrue(first <= Calendar.SATURDAY);
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setMinimalDaysInFirstWeek(first);
        assertEquals(1, report.getEventCount());
        assertEquals(EventType.CALENDAR_CHANGED, report.getLastEventType());
    }
    /**
     * test synch of model properties with its calendar's properties.
     * Here: modified minimalDaysInFirstWeek.
     */
    public void testCalendarMinimalDaysInFirstWeekChanged() {
        int first = model.getMinimalDaysInFirstWeek() + 1;
        //sanity
        assertTrue(first <= Calendar.SATURDAY);
        model.setMinimalDaysInFirstWeek(first);
        assertEquals(first, model.getMinimalDaysInFirstWeek());
        assertEquals(model.getMinimalDaysInFirstWeek(), model.getCalendar().getMinimalDaysInFirstWeek());
    }
    
    /**
     * test synch of model properties with its calendar's properties.
     * Here: initial minimalDaysInFirstWeek.
     */
    public void testCalendarMinimalDaysInFirstWeekInitial() {
        assertEquals(calendar.getMinimalDaysInFirstWeek(), model.getMinimalDaysInFirstWeek());
        assertEquals(model.getMinimalDaysInFirstWeek(), model.getCalendar().getMinimalDaysInFirstWeek());
    }
    

    
    /**
     * test synch of model properties with its calendar's properties.
     * Here: no change notification of if no change of firstDayOfWeek.
     */
    public void testCalendarFirstDayOfWeekNoChangeNoNotify() {
        int first = model.getFirstDayOfWeek();
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setFirstDayOfWeek(first);
        assertEquals(0, report.getEventCount());
    }

    /**
     * test synch of model properties with its calendar's properties.
     * Here: change notification of firstDayOfWeek.
     */
    public void testCalendarFirstDayOfWeekNotify() {
        int first = model.getFirstDayOfWeek() + 1;
        //sanity
        assertTrue(first <= Calendar.SATURDAY);
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setFirstDayOfWeek(first);
        assertEquals(1, report.getEventCount());
        assertEquals(EventType.CALENDAR_CHANGED, report.getLastEventType());
    }

    /**
     * test synch of model properties with its calendar's properties.
     * Here: modified firstDayOfWeek.
     */
    public void testCalendarFirstDayOfWeekChanged() {
        int first = model.getFirstDayOfWeek() + 1;
        //sanity
        assertTrue(first <= Calendar.SATURDAY);
        model.setFirstDayOfWeek(first);
        assertEquals(first, model.getFirstDayOfWeek());
        assertEquals(model.getFirstDayOfWeek(), model.getCalendar().getFirstDayOfWeek());
    }
    
    /**
     * test synch of model properties with its calendar's properties.
     * Here: initial firstDayOfWeek.
     */
    public void testCalendarFirstDayOfWeekInitial() {
        assertEquals(calendar.getFirstDayOfWeek(), model.getFirstDayOfWeek());
        assertEquals(model.getFirstDayOfWeek(), model.getCalendar().getFirstDayOfWeek());
    }
    
    
    
    /**
     * test the contract as doc'ed 
     */
    public void testNormalizedDateContract() {
        model.setSelectionInterval(today, today);
        assertEquals(model.getNormalizedDate(today), model.getSelection().first());
    }
    
    /**
     * Normalized must throw NPE if given date is null
     */
    public void testNormalizedDateNull() {
        try {
            model.getNormalizedDate(null);
            fail("normalizedDate must throw NPE if date is null");
        } catch (NullPointerException e) {
            // expected 
        } catch (Exception e) {
            fail("unexpected exception " + e);
        }
    }

    /**
     * DaySelectionModel normalizes to start of day.
     */
    public void testNormalizedDateCloned() {
        assertEquals(startOfDay(today), model.getNormalizedDate(today));
        assertNotSame(startOfDay(today), model.getNormalizedDate(today));
    }


    /**
     * setSelectionInterval must throw NPE if given date is null
     */
    public void testSetIntervalNulls() {
        try {
            model.setSelectionInterval(null, null);
            fail("normalizedDate must throw NPE if date is null");
        } catch (NullPointerException e) {
            // expected 
        } catch (Exception e) {
            fail("unexpected exception " + e);
        }
        
    }
    /**
     * setSelectionInterval must throw NPE if given date is null
     */
    public void testAddIntervalNulls() {
        try {
            model.addSelectionInterval(null, null);
            fail("normalizedDate must throw NPE if date is null");
        } catch (NullPointerException e) {
            // expected 
        } catch (Exception e) {
            fail("unexpected exception " + e);
        }
        
    }
    
    /**
     * removeSelectionInterval must throw NPE if given date is null
     */
    public void testRemoveIntervalNulls() {
        try {
            model.removeSelectionInterval(null, null);
            fail("normalizedDate must throw NPE if date is null");
        } catch (NullPointerException e) {
            // expected 
        } catch (Exception e) {
            fail("unexpected exception " + e);
        }
        
    }

    /**
     * expose more selection constraint methods in JXMonthView
     *
     */
    public void testUpperBound() {
        model.setUpperBound(today);
        assertEquals(startOfDay(today), model.getUpperBound());
        // remove again
        model.setUpperBound(null);
        assertEquals(null, model.getUpperBound());
    }
    
    /**
     * expose more selection constraint methods in JXMonthView
     *
     */
    public void testLowerBound() {
        model.setLowerBound(today);
        assertEquals(startOfDay(today), model.getLowerBound());
        // remove again
        model.setLowerBound(null);
        assertEquals(null, model.getLowerBound());
    }

    /**
     * test unselectable: use methods with Date.
     *
     */
    public void testUnselectableDate() {
        // initial
        assertFalse(model.isUnselectableDate(today));
        // set unselectable today
        SortedSet<Date> unselectables = new TreeSet<Date>();
        unselectables.add(today);
        model.setUnselectableDates(unselectables);
        assertTrue("raqw today must be unselectable", 
                model.isUnselectableDate(today));
        assertTrue("start of today must be unselectable", 
                model.isUnselectableDate(startOfDay(today)));
        assertTrue("end of today must be unselectable", 
                model.isUnselectableDate(endOfDay(today)));
        model.setUnselectableDates(new TreeSet<Date>());
        assertFalse(model.isUnselectableDate(today));
        assertFalse(model.isUnselectableDate(startOfDay(today)));
        assertFalse(model.isUnselectableDate(endOfDay(today)));
    }

    /**
     * Issue #494-swingx: JXMonthView changed all passed-in dates
     *
     */
    public void testCleanupCopyDate() {
        Date copy = new Date(today.getTime());
        model.setSelectionInterval(today, today);
        assertEquals("the date used for selection must be unchanged", copy, today);
    }
   
    public void testEmptySelectionInitial() {
        assertTrue(model.isSelectionEmpty());
        SortedSet<Date> selection = model.getSelection();
        assertTrue(selection.isEmpty());
    }
    
    public void testEmptySelectionClear() {
        model.setSelectionInterval(today, today);
        // sanity
        assertTrue(1 == model.getSelection().size());

        model.clearSelection();
        assertTrue(model.isSelectionEmpty());
        assertTrue(model.getSelection().isEmpty());
    }

    public void testSingleSelection() {
        model.setSelectionMode(SelectionMode.SINGLE_SELECTION);

        model.setSelectionInterval(today, today);
        SortedSet<Date> selection = model.getSelection();
        assertTrue(1 == selection.size());
        assertEquals(startOfDay(today), selection.first());

        model.setSelectionInterval(today, afterTomorrow);
        selection = model.getSelection();
        assertTrue(1 == selection.size());
        assertEquals(startOfDay(today), selection.first());
    }
    
    public void testSingleIntervalSelection() {
        model.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);

        model.setSelectionInterval(today, today);
        SortedSet<Date> selection = model.getSelection();
        assertTrue(1 == selection.size());
        assertEquals(startOfDay(today), selection.first());

        model.setSelectionInterval(today, tomorrow);
        
        selection = model.getSelection();
        assertEquals(2, selection.size());
        assertEquals(startOfDay(today), selection.first());
        assertEquals(startOfDay(tomorrow), selection.last());
    }

    public void testWeekIntervalSelection() {
        //TODO...
    }

    public void testMultipleIntervalSelection() {
        model.setSelectionMode(SelectionMode.MULTIPLE_INTERVAL_SELECTION);

        model.setSelectionInterval(yesterDay, yesterDay);
        model.addSelectionInterval(afterTomorrow, afterTomorrow);
        
        SortedSet<Date> selection = model.getSelection();
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
        model = new DaySelectionModel();
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
