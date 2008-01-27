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
package org.jdesktop.swingx.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.jdesktop.swingx.event.DateSelectionEvent.EventType;
import org.jdesktop.swingx.test.DateSelectionReport;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
public class AbstractDateSelectionModelTest extends TestCase {

    protected DateSelectionModel model;
    protected Calendar calendar;

    /**
     * test synch of model properties with its calendar's properties.
     * Here: null locale falls back to Locale.default.
     */
    public void testCalendarLocaleNull() {
        // config with a known timezone and date
        Locale tz = Locale.GERMAN;
        if (model.getLocale().equals(tz)) {
            tz = Locale.FRENCH;
        }
        // different from default
        model.setLocale(tz);
        model.setLocale(null);
        assertEquals(Locale.getDefault(), model.getLocale());
    }
    /**
     * test synch of model properties with its calendar's properties.
     * Here: null locale falls back to Locale.default, no fire if had.
     */
    public void testCalendarLocaleNullNoNofify() {
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setLocale(null);
        assertEquals(0, report.getEventCount());
    }
    /**
     * test synch of model properties with its calendar's properties.
     * Here: changed timeZone.
     */
    public void testCalendarLocaleNoChangeNoNotify() {
        // config with a known timezone and date
        Locale tz = model.getLocale();
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setLocale(tz);
        assertEquals(0, report.getEventCount());
    }

    /**
     * test synch of model properties with its calendar's properties.
     * Here: changed timeZone.
     */
    public void testCalendarLocaleChangedNotify() {
        // config with a known timezone and date
        Locale tz = Locale.GERMAN;
        if (model.getLocale().equals(tz)) {
            tz = Locale.FRENCH;
        }
        DateSelectionReport report = new DateSelectionReport();
        model.addDateSelectionListener(report);
        model.setLocale(tz);
        assertEquals(1, report.getEventCount());
        assertEquals(EventType.CALENDAR_CHANGED, report.getLastEventType());
    }

    /**
     * test synch of model properties with its calendar's properties.
     * Here: changed timeZone.
     */
    public void testCalendarLocaleChanged() {
        // config with a known timezone and date
        Locale tz = Locale.GERMAN;
        if (model.getLocale().equals(tz)) {
            tz = Locale.FRENCH;
        }
        model.setLocale(tz);
        assertEquals(tz, model.getLocale());
    }

    /**
     * test synch of model properties with its calendar's properties.
     * Here: initial timeZone.
     */
    public void testCalendarLocaleInitial() {
        assertEquals(Locale.getDefault(), model.getLocale());
    }


    
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

    protected Date today;
    protected Date tomorrow;
    @SuppressWarnings("unused")
    protected Date afterTomorrow;
    protected Date yesterday;

    protected Date startOfDay(Date date) {
        calendar.setTime(date);
        CalendarUtils.startOfDay(calendar);
        return calendar.getTime();
    }

    protected Date endOfDay(Date date) {
        calendar.setTime(date);
        CalendarUtils.endOfDay(calendar);
        return calendar.getTime();
    }

    protected void setUpCalendar() {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        today = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        yesterday = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        tomorrow = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        afterTomorrow = calendar.getTime();
        
        calendar.setTime(today);
    }

}
