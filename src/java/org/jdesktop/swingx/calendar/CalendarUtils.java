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

/**
 * Calendar manipulation.
 * 
 * PENDING: replace by something tested - as is c&p'ed dateUtils 
 * to work on a calendar instead of using long
 * 
 * @author Jeanette Winzenburg
 */
public class CalendarUtils {

    /**
     * Adjusts the calendar to the start of the current week.
     * That is, first day of the week with all time fields cleared.
     * @param calendar the calendar to adjust.
     */
    public static void startOfWeek(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        startOfDay(calendar);
    }

    /**
     * Adjusts the calendar to the end of the current week.
     * That is, last day of the week with all time fields at max.
     * @param calendar the calendar to adjust.
     */
    public static void endOfWeek(Calendar calendar) {
        startOfWeek(calendar);
        calendar.add(Calendar.DATE, 7);
        calendar.add(Calendar.MILLISECOND, -1);
    }
    /**
     * Adjusts the calendar to the start of the current month.
     * That is, first day of the month with all time fields cleared.
     * @param calendar
     */
    public static void startOfMonth(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        startOfDay(calendar);
    }

    /**
     * Adjusts the calendar to the end of the current month.
     * That is the last day of the month with all time-fields
     * at max.
     * 
     * @param calendar
     */
    public static void endOfMonth(Calendar calendar) {
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.add(Calendar.MILLISECOND, -1);
    }



    /**
     * Adjust the given calendar to the first millisecond of the 
     * current day. that is all time fields cleared.
     *
     * @param calendar calendar to adjust.
     */
    public static void startOfDay(Calendar calendar) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
    }

    /**
     * Adjust the given calendar to the last millisecond of the specified date.
     *
     * Hmm ... really set the fields? Not next day, startOfDay and back a milli?
     * @param calendar calendar to adjust.
     */
    public static void endOfDay(Calendar calendar) {
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MILLISECOND, 999);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MINUTE, 59);
    }


}
