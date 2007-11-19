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
    private Calendar calendar;
    private Calendar calendarUS;

    public void testEndOfWeek() {
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.endOfWeek(calendar);
        assertEquals(week, calendar.get(Calendar.WEEK_OF_YEAR));
        calendar.add(Calendar.MILLISECOND, 1);
        assertEquals(week + 1 , calendar.get(Calendar.WEEK_OF_YEAR));
    }

    public void testStartOfWeekFromMiddle() {
        int day = Calendar.WEDNESDAY;
        calendar.set(Calendar.DAY_OF_WEEK, day);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.startOfWeek(calendar);
        assertEquals(week, calendar.get(Calendar.WEEK_OF_YEAR));
        assertEquals(calendar.getFirstDayOfWeek(), calendar.get(Calendar.DAY_OF_WEEK));
    }
    
    public void testStartOfWeekFromFirst() {
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.startOfWeek(calendar);
        assertEquals(week, calendar.get(Calendar.WEEK_OF_YEAR));
        assertEquals(calendar.getFirstDayOfWeek(), calendar.get(Calendar.DAY_OF_WEEK));
    }
    
    public void testStartOfWeekFromLast() {
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.add(Calendar.DATE, 6);
        // sanity
        assertEquals(week, calendar.get(Calendar.WEEK_OF_YEAR));
        CalendarUtils.startOfWeek(calendar);
        assertEquals(week, calendar.get(Calendar.WEEK_OF_YEAR));
        assertEquals(calendar.getFirstDayOfWeek(), calendar.get(Calendar.DAY_OF_WEEK));
    }
    
    public void testStartOfWeekFromFirstJan() {
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DATE, 1);
        if (calendar.get(Calendar.DAY_OF_WEEK) == calendar.getFirstDayOfWeek()) {
            calendar.add(Calendar.YEAR, -1);
        }
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.startOfWeek(calendar);
        assertEquals(week, calendar.get(Calendar.WEEK_OF_YEAR));
        assertEquals(calendar.getFirstDayOfWeek(), calendar.get(Calendar.DAY_OF_WEEK));
    }
    
    public void testStartOfWeekUS() {
        calendarUS = Calendar.getInstance(Locale.US);
        int day = Calendar.WEDNESDAY;
        assertFalse(day == calendarUS.getFirstDayOfWeek());
        int week = calendarUS.get(Calendar.WEEK_OF_YEAR);
        CalendarUtils.startOfWeek(calendarUS);
        assertEquals(week, calendarUS.get(Calendar.WEEK_OF_YEAR));
    }
    
    public void testEndOfMonth() {
        // want to be in the middle of a year
        int month = 5;
        calendar.set(Calendar.MONTH, month);
        CalendarUtils.endOfMonth(calendar);
        assertEquals(month, calendar.get(Calendar.MONTH));
        calendar.add(Calendar.MILLISECOND, 1);
        assertEquals(month + 1, calendar.get(Calendar.MONTH));
    }

    public void testStartOfMonth() {
        // want to be in the middle of a year
        int month = 5;
        calendar.set(Calendar.MONTH, month);
        CalendarUtils.startOfMonth(calendar);
        assertEquals(month, calendar.get(Calendar.MONTH));
        calendar.add(Calendar.MILLISECOND, -1);
        assertEquals(month - 1, calendar.get(Calendar.MONTH));
    }
    
    public void testEndOfDay() {
        // want to be in the middle of a month
        int day = 10;
        calendar.set(Calendar.DATE, day);
        CalendarUtils.endOfDay(calendar);
        assertEquals(day, calendar.get(Calendar.DATE));
        calendar.add(Calendar.MILLISECOND, 1);
        assertEquals(day + 1, calendar.get(Calendar.DATE));
    }

    public void testStartOfDay() {
        // want to be in the middle of a month
        int day = 10;
        calendar.set(Calendar.DATE, day);
        CalendarUtils.startOfDay(calendar);
        assertEquals(day, calendar.get(Calendar.DATE));
        calendar.add(Calendar.MILLISECOND, -1);
        assertEquals(day - 1, calendar.get(Calendar.DATE));
    }

    /**
     * sanity ...
     */
    public void testNextMonthCal() {
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        Date date = calendar.getTime();
        for (int i = Calendar.JANUARY; i <= Calendar.DECEMBER; i++) {
            int month = calendar.get(Calendar.MONTH);
            CalendarUtils.startOfMonth(calendar);
            assertEquals(month, calendar.get(Calendar.MONTH));
            CalendarUtils.endOfMonth(calendar);
            assertEquals(month, calendar.get(Calendar.MONTH));
            // restore original and add
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, 1);
            date = calendar.getTime();
            if (i < Calendar.DECEMBER) {
                assertEquals(month + 1, calendar.get(Calendar.MONTH));
            } else {
                assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
            }
        }
    }
    @Override
    protected void setUp() throws Exception {
        calendar = Calendar.getInstance(Locale.GERMAN);
        calendarUS = Calendar.getInstance(Locale.US);
    }
    
    public void testNextMonth() {
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DATE, 31);
        for (int i = Calendar.JANUARY; i <= Calendar.DECEMBER; i++) {
            int month = calendar.get(Calendar.MONTH);
            long nextMonth = DateUtils.getNextMonth(calendar.getTimeInMillis());
            calendar.setTimeInMillis(nextMonth);
            if (i < Calendar.DECEMBER) {
                assertEquals(month + 1, calendar.get(Calendar.MONTH));
            } else {
                assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
            }
        }
    }
    
}
