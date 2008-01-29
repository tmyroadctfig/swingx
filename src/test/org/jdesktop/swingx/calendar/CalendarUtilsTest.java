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
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.jdesktop.swingx.InteractiveTestCase;

/**
 * Tests CalendarUtils.
 * 
 * @author Jeanette Winzenburg
 */
public class CalendarUtilsTest extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(CalendarUtilsTest.class
            .getName());
    /**
     * default calendar instance
     */
    private Calendar today;
    private Calendar todayUS;
    private Calendar midJune;

    
    public void testSameDay() {
        Date now = today.getTime();
        CalendarUtils.endOfDay(today);
        Date end = today.getTime();
        assertTrue(CalendarUtils.isSameDay(today, now));
        assertEquals(end, today.getTime());
        today.add(Calendar.DAY_OF_MONTH, 1);
        assertFalse(CalendarUtils.isSameDay(today, now));
    }
    
    public void testAreEqual() {
        assertTrue(CalendarUtils.areEqual(null, null));
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        assertFalse(CalendarUtils.areEqual(now, null));
        assertFalse(CalendarUtils.areEqual(null, now));
        assertTrue(CalendarUtils.areEqual(now, now));
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        assertFalse(CalendarUtils.areEqual(now, calendar.getTime()));
    }
    public void testIsStartOfWeek() {
        CalendarUtils.startOfWeek(midJune);
        assertTrue(CalendarUtils.isStartOfWeek(midJune));
        midJune.add(Calendar.MILLISECOND, -1);
        Date date = midJune.getTime();
        assertFalse(CalendarUtils.isStartOfWeek(midJune));
        assertEquals("calendar must be unchanged", date, midJune.getTime());
    }
 
    /**
     */
    public void testIsEndOfWeek() {
        CalendarUtils.endOfWeek(midJune);
        assertTrue(CalendarUtils.isEndOfWeek(midJune));
        midJune.add(Calendar.MILLISECOND, 1);
        Date date = midJune.getTime();
        assertFalse(CalendarUtils.isEndOfWeek(midJune));
        assertEquals("calendar must be unchanged", date, midJune.getTime());
    }

    /**
     */
    public void testEndOfWeek() {
        int week = midJune.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.endOfWeek(midJune);
        assertEquals(week, midJune.get(Calendar.WEEK_OF_YEAR));
        midJune.add(Calendar.MILLISECOND, 1);
        assertEquals(week + 1 , midJune.get(Calendar.WEEK_OF_YEAR));
    }

    public void testStartOfWeekFromMiddle() {
        int day = Calendar.WEDNESDAY;
        today.set(Calendar.DAY_OF_WEEK, day);
        int week = today.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.startOfWeek(today);
        assertEquals(week, today.get(Calendar.WEEK_OF_YEAR));
        assertEquals(today.getFirstDayOfWeek(), today.get(Calendar.DAY_OF_WEEK));
    }
    
    public void testStartOfWeekFromFirst() {
        today.set(Calendar.DAY_OF_WEEK, today.getFirstDayOfWeek());
        int week = today.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.startOfWeek(today);
        assertEquals(week, today.get(Calendar.WEEK_OF_YEAR));
        assertEquals(today.getFirstDayOfWeek(), today.get(Calendar.DAY_OF_WEEK));
    }
    
    public void testStartOfWeekFromLast() {
        today.set(Calendar.DAY_OF_WEEK, today.getFirstDayOfWeek());
        int week = today.get(Calendar.WEEK_OF_YEAR);
        today.add(Calendar.DATE, 6);
        // sanity
        assertEquals(week, today.get(Calendar.WEEK_OF_YEAR));
        CalendarUtils.startOfWeek(today);
        assertEquals(week, today.get(Calendar.WEEK_OF_YEAR));
        assertEquals(today.getFirstDayOfWeek(), today.get(Calendar.DAY_OF_WEEK));
    }
    
    public void testStartOfWeekFromFirstJan() {
        today.set(Calendar.MONTH, Calendar.JANUARY);
        today.set(Calendar.DATE, 1);
        if (today.get(Calendar.DAY_OF_WEEK) == today.getFirstDayOfWeek()) {
            today.add(Calendar.YEAR, -1);
        }
        int week = today.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.startOfWeek(today);
        assertEquals(week, today.get(Calendar.WEEK_OF_YEAR));
        assertEquals(today.getFirstDayOfWeek(), today.get(Calendar.DAY_OF_WEEK));
    }
    
    public void testStartOfWeekUS() {
        int day = Calendar.WEDNESDAY;
        assertFalse(day == todayUS.getFirstDayOfWeek());
        int week = todayUS.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.startOfWeek(todayUS);
        assertEquals(week, todayUS.get(Calendar.WEEK_OF_YEAR));
    }
    
    
    public void testIsStartOfMonth() {
        // want to be in the middle of a year
        int month = 5;
        today.set(Calendar.MONTH, month);
        CalendarUtils.startOfMonth(today);
        Date start = today.getTime();
        assertTrue(CalendarUtils.isStartOfMonth(today));
        // sanity: calendar must not be changed
        assertEquals(start, today.getTime());
        today.add(Calendar.MILLISECOND, 1);
        assertFalse(CalendarUtils.isStartOfMonth(today));
    }
    
    
    public void testIsEndOfMonth() {
        // want to be in the middle of a year
        int month = 5;
        today.set(Calendar.MONTH, month);
        CalendarUtils.endOfMonth(today);
        Date start = today.getTime();
        assertTrue(CalendarUtils.isEndOfMonth(today));
        assertEquals(start, today.getTime());
        today.add(Calendar.MILLISECOND, -1);
        assertFalse(CalendarUtils.isEndOfMonth(today));
        // sanity: calendar must not be changed
    }
    
    public void testEndOfMonth() {
        // want to be in the middle of a year
        int month = midJune.get(Calendar.MONTH);
        CalendarUtils.endOfMonth(midJune);
        assertEquals(month, midJune.get(Calendar.MONTH));
        midJune.add(Calendar.MILLISECOND, 1);
        assertEquals(month + 1, midJune.get(Calendar.MONTH));
    }

    public void testStartOfMonth() {
        // want to be in the middle of a year
        int month = midJune.get(Calendar.MONTH);
        CalendarUtils.startOfMonth(midJune);
        assertEquals(month, midJune.get(Calendar.MONTH));
        midJune.add(Calendar.MILLISECOND, -1);
        assertEquals(month - 1, midJune.get(Calendar.MONTH));
    }
    
    public void testEndOfDay() {
        // want to be in the middle of a month
        int day = midJune.get(Calendar.DAY_OF_MONTH);
        CalendarUtils.endOfDay(midJune);
        assertEquals(day, midJune.get(Calendar.DATE));
        midJune.add(Calendar.MILLISECOND, 1);
        assertEquals(day + 1, midJune.get(Calendar.DATE));
    }

    public void testEndOfDayWithReturn() {
        Date date = midJune.getTime();
        Date start = CalendarUtils.endOfDay(midJune, date);
        assertTrue(CalendarUtils.isEndOfDay(midJune));
        assertEquals(start, midJune.getTime());
    }
    
    public void testStartOfDay() {
        // want to be in the middle of a month
        int day = midJune.get(Calendar.DAY_OF_MONTH);
        CalendarUtils.startOfDay(midJune);
        assertEquals(day, midJune.get(Calendar.DATE));
        midJune.add(Calendar.MILLISECOND, -1);
        assertEquals(day - 1, midJune.get(Calendar.DATE));
    }

    public void testStartOfDayWithReturn() {
        Date date = midJune.getTime();
        Date start = CalendarUtils.startOfDay(midJune, date);
        assertTrue(CalendarUtils.isStartOfDay(midJune));
        assertEquals(start, midJune.getTime());
    }
    
    public void testStartOfDayUnique() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
        CalendarUtils.startOfMonth(calendar);
        // sanity
        assertTrue(CalendarUtils.isStartOfDay(calendar));
        assertNotStartOfDayInTimeZones(calendar, "GMT+");
        assertNotStartOfDayInTimeZones(calendar, "GMT-");
    }
    
    private void assertNotStartOfDayInTimeZones(Calendar calendar, String id) {
        for (int i = 1; i < 13; i++) {
            calendar.setTimeZone(TimeZone.getTimeZone(id + i));
            assertFalse(CalendarUtils.isStartOfDay(calendar));
        }
    }

    public void testStartOfMonthUnique() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
        CalendarUtils.startOfMonth(calendar);
        // sanity
        assertTrue(CalendarUtils.isStartOfMonth(calendar));
        assertNotStartOfMonthInTimeZones(calendar, "GMT+");
        assertNotStartOfMonthInTimeZones(calendar, "GMT-");
    }

    private void assertNotStartOfMonthInTimeZones(Calendar calendar, String id) {
        for (int i = 1; i < 13; i++) {
            calendar.setTimeZone(TimeZone.getTimeZone(id + i));
            assertFalse(CalendarUtils.isStartOfMonth(calendar));
        }
    }
    /**
     * sanity ...
     */
    public void testNextMonthCal() {
        today.set(Calendar.MONTH, Calendar.JANUARY);
        Date date = today.getTime();
        for (int i = Calendar.JANUARY; i <= Calendar.DECEMBER; i++) {
            int month = today.get(Calendar.MONTH);
            CalendarUtils.startOfMonth(today);
            assertEquals(month, today.get(Calendar.MONTH));
            CalendarUtils.endOfMonth(today);
            assertEquals(month, today.get(Calendar.MONTH));
            // restore original and add
            today.setTime(date);
            today.add(Calendar.MONTH, 1);
            date = today.getTime();
            if (i < Calendar.DECEMBER) {
                assertEquals(month + 1, today.get(Calendar.MONTH));
            } else {
                assertEquals(Calendar.JANUARY, today.get(Calendar.MONTH));
            }
        }
    }
    
    public void testNextMonth() {
        today.set(Calendar.MONTH, Calendar.JANUARY);
        today.set(Calendar.DATE, 31);
        for (int i = Calendar.JANUARY; i <= Calendar.DECEMBER; i++) {
            int month = today.get(Calendar.MONTH);
            long nextMonth = DateUtils.getNextMonth(today.getTimeInMillis());
            today.setTimeInMillis(nextMonth);
            if (i < Calendar.DECEMBER) {
                assertEquals(month + 1, today.get(Calendar.MONTH));
            } else {
                assertEquals(Calendar.JANUARY, today.get(Calendar.MONTH));
            }
        }
    }
    @Override
    protected void setUp() throws Exception {
        today = Calendar.getInstance(Locale.GERMAN);
        todayUS = Calendar.getInstance(Locale.US);
        midJune = Calendar.getInstance(Locale.GERMAN);
        midJune.set(Calendar.DAY_OF_MONTH, 14);
        midJune.set(Calendar.MONTH, Calendar.JUNE);
    }
   
}
