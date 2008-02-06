/**
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

import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.calendar.CalendarUtils;
import org.jdesktop.swingx.calendar.DateSelectionModel;
import org.jdesktop.swingx.calendar.DaySelectionModel;
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;
import org.jdesktop.swingx.event.DateSelectionListener;
import org.jdesktop.swingx.event.DateSelectionEvent.EventType;
import org.jdesktop.swingx.test.DateSelectionReport;
import org.jdesktop.test.ActionReport;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Test case for <code>JXMonthView</code>
 *
 * There's another class with passing unit tests for JXMonthView (JXMonthViewVisualTest)
 * because this 
 * extends mock while the other extends InteractiveTestCase. Both are expected
 * to pass.
 * 
 * @author Joshua Outwater
 */
public class JXMonthViewTest extends MockObjectTestCase {
    private static final Logger LOG = Logger.getLogger(JXMonthViewTest.class
            .getName());
    private Locale componentLocale;
    // pre-defined reference dates - all relative to current date at around 5 am
    private Date today;
    private Date tomorrow;
    private Date afterTomorrow;
    private Date yesterday;
    // calendar default instance init with today
    private Calendar calendar;

    public void setUp() {
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

        //the test is configured for a US defaulted system
        //the localization tests handle actual localization issues
        componentLocale = JComponent.getDefaultLocale();
//        LOG.info("componentLocale " + componentLocale);
//        JComponent.setDefaultLocale(Locale.US);
    }

    public void tearDown() {
        JComponent.setDefaultLocale(componentLocale);
    }

    /**
     * Issue #751-swingx: property naming violations
     */
    public void testTraversableNotification() {
        JXMonthView monthView = new JXMonthView();
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        boolean showing = monthView.isTraversable();
        monthView.setTraversable(!showing);
        TestUtils.assertPropertyChangeEvent(report, "traversable", showing, !showing);
    }

    /**
     * Issue #751-swingx: property naming violations
     */
    public void testTraversableNoNotification() {
        JXMonthView monthView = new JXMonthView();
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        boolean showing = monthView.isTraversable();
        monthView.setTraversable(showing);
        assertEquals(0, report.getEventCount("traversable"));
    }


    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: test that model settings are respected in constructor - minimaldays.
     */
    public void testCalendarsContructorUnchangedFirstDayOfWeek() {
        DateSelectionModel model = new DaySelectionModel();
        int first = model.getFirstDayOfWeek() + 1;
        model.setFirstDayOfWeek(first);
        JXMonthView monthView = new JXMonthView(new Date(), model);
        assertEquals("model's calendar properties must be unchanged: minimalDays", 
                first, model.getFirstDayOfWeek());
        // sanity: taken in monthView
        assertEquals("monthView's calendar properties must be synched", 
                first, monthView.getFirstDayOfWeek());
    }
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: test that monthView is updated to model after setSelectionModel.
     */
    public void testCalendarsSetModel() {
        JXMonthView monthView = new JXMonthView();
        int firstDayOfWeek = monthView.getFirstDayOfWeek();
        Locale locale = Locale.UK;
        if (locale.equals(monthView.getLocale())) {
            locale = Locale.FRENCH;
        }
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        if (monthView.getTimeZone().equals(tz)) {
            tz = TimeZone.getTimeZone("GMT+5");
        }
        DateSelectionModel model = new DaySelectionModel(locale);
        model.setTimeZone(tz);
        int modelMinimal = model.getMinimalDaysInFirstWeek();
        monthView.setSelectionModel(model);
        assertEquals("timeZone must be updated from model", tz, monthView.getTimeZone());
        assertEquals("Locale must be updated from model", locale, monthView.getLocale());
        // be aware if it makes no sense to assert
        if (firstDayOfWeek != model.getFirstDayOfWeek()) {
            assertEquals("firstDayOfWeek must be updated from model", 
                    model.getFirstDayOfWeek(), monthView.getFirstDayOfWeek());
        } else {
            LOG.info("cannot assert firstDayOfWeek - was same");
        }
        // @KEEP - this is an open issue: monthView must not change the
        // model settings but minimalDaysInFirstWeek > 1 confuse the 
        // BasicMonthViewUI - remove if passing in xIssues
        assertEquals("model minimals must not be changed", 
                modelMinimal, model.getMinimalDaysInFirstWeek());
    }

    /**
     * Issue #736-swingx: model and monthView cal not synched.
     * 
     * Here: test that model settings are respected in constructor - minimaldays.
     */
    @SuppressWarnings("unused")
    public void testCalendarsContructorUnchangedMinimalDaysOfModel() {
        DateSelectionModel model = new DaySelectionModel();
        int first = model.getMinimalDaysInFirstWeek() + 1;
        model.setMinimalDaysInFirstWeek(first);
        JXMonthView monthView = new JXMonthView(new Date(), model);
        assertEquals("model's calendar properties must be unchanged: minimalDays", 
                first, model.getMinimalDaysInFirstWeek());
    }

    /**
     * Issue #736-swingx: model and monthView cal not synched.
     * 
     * Here: test that model settings are respected in setModel - minimaldays.
     * 
     * Model must not reset minimalDaysInfirstWeek, but Locales with values
     * > 1 confuse the BasicDatePickerUI - need to track down and solve there.
     */
    public void testCalendarsSetModelUnchangedMinimalDaysInFirstWeek() {
        JXMonthView monthView = new JXMonthView();
        DateSelectionModel model = new DaySelectionModel();
        int first = model.getMinimalDaysInFirstWeek() + 1;
        model.setMinimalDaysInFirstWeek(first);
        monthView.setSelectionModel(model);
        assertEquals("model minimals must not be changed", 
                first, model.getMinimalDaysInFirstWeek());
    }
    

    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: minimal days of first week.
     */
    public void testCalendarsMinimalDaysOfFirstWeekModelChanged() {
        JXMonthView monthView = new JXMonthView();
        int first = monthView.getCalendar().getMinimalDaysInFirstWeek() + 1;
        assertTrue(first <= Calendar.SATURDAY);
        monthView.getSelectionModel().setMinimalDaysInFirstWeek(first);
        assertEquals(first, monthView.getCalendar().getMinimalDaysInFirstWeek());
    }
    

    /**
     * Issue #733-swingx: TimeZone in model and monthView not synched.
     *  
     *  Test that the selected is normalized in the monthView's timezone. 
     */
    public void testCalendarsTimeZoneNormalizedDate() {
        JXMonthView monthView = new JXMonthView();
        // config with a known timezone
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        if (tz.equals(monthView.getTimeZone())) {
            tz = TimeZone.getTimeZone("GMT+5");
        }
        monthView.setTimeZone(tz);
        monthView.setSelectionDate(new Date());
        Date selected = monthView.getSelectionDate();
        Calendar calendar = monthView.getCalendar();
        assertEquals(selected, CalendarUtils.startOfDay(calendar, selected));
    }
    
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: timezone of flagged dates not synched.
     * This was introduced by moving the control of flagged dates into
     * a internal model. Need to synch that model as well.
     */
    public void testFlaggedDatesTimeZone() {
        JXMonthView monthView = new JXMonthView();
        // config with a known timezone and date
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        if (tz.equals(monthView.getTimeZone())) {
            tz = TimeZone.getTimeZone("GMT+5");
        }
        monthView.setTimeZone(tz);
        monthView.setFlaggedDates(today);
        Date flagged = monthView.getFlaggedDates().first();
        assertEquals(flagged, CalendarUtils.startOfDay(monthView.getCalendar(), flagged));
    }
    
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: setting the timezone clears the flagged dates, must notify of change.
      */
    public void testFlaggedDatesTimeZoneNotifyOnChange() {
        JXMonthView monthView = new JXMonthView();
        monthView.setFlaggedDates(today);
        SortedSet<Date> flagged = monthView.getFlaggedDates();
        // config with a known timezone and date
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        if (tz.equals(monthView.getTimeZone())) {
            tz = TimeZone.getTimeZone("GMT+5");
        }
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setTimeZone(tz);
        TestUtils.assertPropertyChangeEvent(report, "flaggedDates", 
                flagged, monthView.getFlaggedDates(), false);
    }
    
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: setting the timezone clears the flagged dates, must notify of change.
      */
    public void testFlaggedDatesTimeZoneNotNotifyWithoutChange() {
        JXMonthView monthView = new JXMonthView();
        // config with a known timezone and date
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        if (tz.equals(monthView.getTimeZone())) {
            tz = TimeZone.getTimeZone("GMT+5");
        }
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setTimeZone(tz);
        assertEquals("no change in flaggedDates must not fire", 0, report.getEventCount("flaggedDates"));
    }
    
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: Locale changed in monthView.
     */
    public void testCalendarsLocaleChangedMonthView() {
        JXMonthView monthView = new JXMonthView();
        Locale locale = Locale.UK;
        if (locale.equals(monthView.getLocale())) {
            locale = Locale.FRENCH;
        }
        monthView.setLocale(locale);
        assertEquals("locale set in monthView must be passed to model", 
                locale, monthView.getSelectionModel().getLocale());
    }
    
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: Locale changed in selection model.
     */
    public void testCalendarsLocaleChangedModel() {
        JXMonthView monthView = new JXMonthView();
        Locale locale = Locale.UK;
        if (locale.equals(monthView.getLocale())) {
            locale = Locale.FRENCH;
        }
        monthView.getSelectionModel().setLocale(locale);
        assertEquals("locale set in model must be passed to monthView", 
                locale, monthView.getLocale());
    }
    
    

    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: Locale changed in monthView.
     */
    public void testCalendarsLocaleContructor() {
        Locale locale = Locale.UK;
        if (locale.equals(JComponent.getDefaultLocale())) {
            locale = Locale.FRENCH;
        }
        JXMonthView monthView = new JXMonthView(locale);
        assertEquals("initial locale in constructor must be passed to model", 
                locale, monthView.getSelectionModel().getLocale());
    }

    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: set first day of week in monthView.
     */
    public void testCalendarsFirstDayOfWeekMonthView() {
        JXMonthView monthView = new JXMonthView();
        int first = monthView.getFirstDayOfWeek() + 1;
        // sanity
        assertTrue(first <= Calendar.SATURDAY);
        monthView.setFirstDayOfWeek(first);
        assertEquals(first, monthView.getFirstDayOfWeek());
        assertEquals(first, monthView.getCalendar().getFirstDayOfWeek());
        assertEquals(first, monthView.getSelectionModel().getFirstDayOfWeek());
    }
    
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: set first day of week in model.
     */
    public void testCalendarsFirstDayOfWeekModel() {
        JXMonthView monthView = new JXMonthView();
        int first = monthView.getFirstDayOfWeek() + 1;
        // sanity
        assertTrue(first <= Calendar.SATURDAY);
        monthView.getSelectionModel().setFirstDayOfWeek(first);
        assertEquals(first, monthView.getFirstDayOfWeek());
        assertEquals(first, monthView.getCalendar().getFirstDayOfWeek());
        assertEquals(first, monthView.getSelectionModel().getFirstDayOfWeek());
    }
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: first day of week.
     */
    public void testCalendarsFirstDayOfWeekInitial() {
        JXMonthView monthView = new JXMonthView();
        assertEquals(monthView.getFirstDayOfWeek(), 
                monthView.getSelectionModel().getFirstDayOfWeek());
    }
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: minimal days of first week.
     */
    public void testCalendarsMinimalDaysOfFirstWeekInitial() {
        JXMonthView monthView = new JXMonthView();
        int first = monthView.getCalendar().getMinimalDaysInFirstWeek();
        assertEquals(first, monthView.getSelectionModel().getMinimalDaysInFirstWeek());
    }
    
    
    /**
     * Issue ??-swingx: selection related properties must be independent 
     * of way-of setting.
     * 
     * View must delegate to model, so asking view or model with same 
     * parameters must return the same result.
     * 
     * Here: isSelected
     * 
     */
    public void testMonthViewSameAsSelectionModelIsSelected() {
        JXMonthView monthView = new JXMonthView();
        // guard against accidental startofday
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        Date date = calendar.getTime();
        monthView.setSelectionDate(date);
        assertTrue(monthView.isSelected(date));
        assertTrue(monthView.getSelectionModel().isSelected(date));
    }
    
    /**
     * Issue ??-swingx: selection related properties must be independent 
     * of way-of setting.
     * 
     * View must delegate to model, so asking view or model with same 
     * parameters must return the same result.
     * 
     * Here: isSelected
     * 
     */
    public void testMonthViewSameAsSelectionModelSelectedDate() {
        JXMonthView monthView = new JXMonthView();
        // guard against accidental startofday
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        Date date = calendar.getTime();
        monthView.setSelectionDate(date);
        assertEquals(monthView.getSelectionDate(), 
                monthView.getSelectionModel().getFirstSelectionDate());
    }

    /**
     * Issue ??-swingx: selection related properties must be independent 
     * of way-of setting.
     * 
     * View must delegate to model, so asking view or model with same 
     * parameters must return the same result.
     * 
     * Here: isSelected
     * 
     */
    public void testMonthViewSameAsSelectionModelIsUnselectable() {
        JXMonthView monthView = new JXMonthView();
        // guard against accidental startofday
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        Date date = calendar.getTime();
        monthView.setUnselectableDates(date);
        assertTrue(monthView.isUnselectableDate(date));
        assertTrue(monthView.getSelectionModel().isUnselectableDate(date));
    }

    /**
     * Issue ??-swingx: selection related properties must be independent 
     * of way-of setting.
     * 
     * View must delegate to model, so asking view or model with same 
     * parameters must return the same result.
     * 
     * Here: set unselectables on model
     * 
     */
    public void testSelectionModelSameAsMonthViewIsUnselectableDate() {
        JXMonthView monthView = new JXMonthView();
        // guard against accidental startofday
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        Date date = calendar.getTime();
        SortedSet<Date> unselectables = new TreeSet<Date>();
        unselectables.add(date);
        monthView.getSelectionModel().setUnselectableDates(unselectables);
        assertTrue(monthView.getSelectionModel().isUnselectableDate(date));
        assertTrue(monthView.isUnselectableDate(date));
    }
    
    /**
     * Issue ??-swingx: selection related properties must be independent 
     * of way-of setting.
     * 
     * View must delegate to model, so asking view or model with same 
     * parameters must return the same result.
     * 
     * Here: set selected on model
     * 
     */
    public void testSelectionModelSameAsMonthViewIsSelected() {
        JXMonthView monthView = new JXMonthView();
        // guard against accidental startofday
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        Date date = calendar.getTime();
        monthView.getSelectionModel().setSelectionInterval(date, date);
        assertTrue(monthView.getSelectionModel().isSelected(date));
        assertTrue(monthView.isSelected(date));
    }
    
    /**
     * Issue ??-swingx: selection related properties must be independent 
     * of way-of setting.
     * 
     * View must delegate to model, so asking view or model with same 
     * parameters must return the same result.
     * 
     * Here: set selected on model, ask for selected date
     * 
     */
    public void testSelectionModelSameAsMonthViewSelectedDate() {
        JXMonthView monthView = new JXMonthView();
        // guard against accidental startofday
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        Date date = calendar.getTime();
        monthView.getSelectionModel().setSelectionInterval(date, date);
        assertEquals(monthView.getSelectionModel().getFirstSelectionDate(), 
                monthView.getSelectionDate());
    }
    

    /**
     * #703-swingx: set date to first of next doesn't update the view.
     * 
     * Behaviour is consistent with core components, must not update
     * 
     */
    public void testAutoScrollOnSelection() {
        JXMonthView us = new JXMonthView();
        final Calendar today = Calendar.getInstance();
        CalendarUtils.endOfMonth(today);
        us.setSelectionDate(today.getTime());
        Date first = us.getFirstDisplayedDay();
        today.add(Calendar.DAY_OF_MONTH, 2);
        us.setSelectionDate(today.getTime());
        assertEquals(first, us.getFirstDisplayedDay());
    }

    /**
     * #705-swingx: revalidate must not reset first firstDisplayed.
     * 
     * 
     */
    public void testAutoScrollOnSelectionRevalidate() throws InterruptedException, InvocationTargetException {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        final JXMonthView us = new JXMonthView();
        final Calendar today = Calendar.getInstance();
        CalendarUtils.endOfMonth(today);
        us.setSelectionDate(today.getTime());
        final JXFrame frame = new JXFrame();
        frame.add(us);
        final Date first = us.getFirstDisplayedDay();
        today.add(Calendar.DAY_OF_MONTH, 2);
        us.setSelectionDate(today.getTime());
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                us.revalidate();
                // need to validate frame - why?
                frame.validate();
                assertEquals("firstDisplayed must not be changed on revalidate", 
                        first, us.getFirstDisplayedDay());
//                assertEquals(first, us.getFirstDisplayedDate());
//                fail("weird (threading issue?): the firstDisplayed is changed in layoutContainer - not testable here");
            }
        });
    }
    
    /**
     * Issue 711-swingx: today is notify-only property.
     * Today is start of day.
     */
    public void testTodayInitial() {
        JXMonthView monthView = new JXMonthView();
        CalendarUtils.startOfDay(calendar);
        assertEquals(calendar.getTime(), monthView.getToday());
    }
    
    /**
     * Issue 711-swingx: today is notify-only property.
     * Increment sets to start of day of tomorrow.
     */
    public void testTodayIncrement() {
        JXMonthView monthView = new JXMonthView();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        CalendarUtils.startOfDay(calendar);
        monthView.incrementToday();
        assertEquals(calendar.getTime(), monthView.getToday());
    }
    
    /**
     * Issue 711-swingx: today is notify-only property.
     *  
     */
    @SuppressWarnings("deprecation")
    public void testTodayMillisSet() {
        JXMonthView monthView = new JXMonthView();
        // tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        monthView.setTodayInMillis(calendar.getTimeInMillis());
        CalendarUtils.startOfDay(calendar);
        assertEquals(calendar.getTime(), new Date(monthView.getTodayInMillis()));
    }
    
    /**
     * Issue 711-swingx: today is notify-only property.
     * 
     */
    @SuppressWarnings("deprecation")
    public void testTodayMillisSetNotification() {
        JXMonthView monthView = new JXMonthView();
        long todayInMillis = monthView.getTodayInMillis();
        // tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setTodayInMillis(calendar.getTimeInMillis());
        CalendarUtils.startOfDay(calendar);
        TestUtils.assertPropertyChangeEvent(report, "today", 
                new Date(todayInMillis), calendar.getTime(), false);
        TestUtils.assertPropertyChangeEvent(report, "todayInMillis", 
                todayInMillis, calendar.getTimeInMillis(), false);
    }
    /**
     * Issue 711-swingx: today is notify-only property.
     * SetToday should 
     */
    public void testTodaySetNotification() {
        JXMonthView monthView = new JXMonthView();
        Date today = monthView.getToday();
        // tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setToday(calendar.getTime());
        CalendarUtils.startOfDay(calendar);
        TestUtils.assertPropertyChangeEvent(report, "today", 
                today, calendar.getTime(), false);
        TestUtils.assertPropertyChangeEvent(report, "todayInMillis", 
                today.getTime(), calendar.getTimeInMillis(), false);
    }

    
    /**
     * Issue 711-swingx: today is notify-only property.
     * SetToday should 
     */
    public void testTodaySet() {
        JXMonthView monthView = new JXMonthView();
        // tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        monthView.setToday(calendar.getTime());
        CalendarUtils.startOfDay(calendar);
        assertEquals(calendar.getTime(), monthView.getToday());
    }

    /**
     * For safety, getToday should return a clone.
     */
    public void testTodayCopy() {
        JXMonthView monthView = new JXMonthView();
        Date today = monthView.getToday();
        Date other = monthView.getToday();
        assertNotNull(today);
        assertNotSame(today, other);
    }
    
    /**
     * Issue #708-swingx: updateUI changes state.
     * 
     * Here: test that today is unchanged.
     */
    public void testUpdateUIToday() {
        JXMonthView monthView = new JXMonthView();
        Date first = monthView.getToday();
        monthView.updateUI();
        assertEquals(first, monthView.getToday());
    };

    
    /**
     * Issue #711-swingx: remove fake property change notification.
     * 
     * Here: test that ensureVisibleDate with millis fires once only.
     */
    public void testEnsureVisibleDateNofication() {
        JXMonthView monthView = new JXMonthView();
        Date firstDisplayedDate = monthView.getFirstDisplayedDay();
        // previous month
        calendar.add(Calendar.MONTH, -1);
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.ensureDateVisible(calendar.getTime());
        CalendarUtils.startOfMonth(calendar);
        TestUtils.assertPropertyChangeEvent(report, "firstDisplayedDate", 
                firstDisplayedDate.getTime(), calendar.getTimeInMillis(), false);
        TestUtils.assertPropertyChangeEvent(report, "firstDisplayedDay", 
                firstDisplayedDate, calendar.getTime(), false);
    }

    /**
     * Issue #711-swingx: remove fake property change notification.
     * 
     * Here: test that ensureVisibleDate with date fires once only.
     */
    public void testEnsureVisibleDateParamNofication() {
        JXMonthView monthView = new JXMonthView();
        Date firstDisplayedDate = monthView.getFirstDisplayedDay();
        // previous month
        calendar.add(Calendar.MONTH, -1);
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.ensureDateVisible(calendar.getTime());
        CalendarUtils.startOfMonth(calendar);
        TestUtils.assertPropertyChangeEvent(report, "firstDisplayedDate", 
                firstDisplayedDate.getTime(), calendar.getTimeInMillis(), false);
        TestUtils.assertPropertyChangeEvent(report, "firstDisplayedDay", 
                firstDisplayedDate, calendar.getTime(), false);
    }
    
    /**
     * Issue #711-swingx: remove fake property change notification.
     * 
     * Here: test that setFirstDisplayedDate fires once only.
     */
    public void testFirstDisplayedDateNofication() {
        JXMonthView monthView = new JXMonthView();
        Date firstDisplayedDate = monthView.getFirstDisplayedDay();
        // previous month
        calendar.add(Calendar.MONTH, -1);
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setFirstDisplayedDay(calendar.getTime());
        CalendarUtils.startOfMonth(calendar);
        TestUtils.assertPropertyChangeEvent(report, "firstDisplayedDate", 
                firstDisplayedDate.getTime(), calendar.getTimeInMillis(), false);
        TestUtils.assertPropertyChangeEvent(report, "firstDisplayedDay", 
                firstDisplayedDate, calendar.getTime(), false);
    }
    
    /**
     * Issue #708-swingx
     * 
     * test update of lastDisplayedDate if resized.
     */
    public void testLastDisplayedOnResize() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        // get a reference width so we can simulate a one-month resize
        JXMonthView compare = new JXMonthView();
        compare.setPreferredCols(2);
        JXMonthView monthView = new JXMonthView();
        JXFrame frame = new JXFrame();
        frame.add(monthView);
        frame.pack();
        Date last = monthView.getLastDisplayedDay();
        // set a size that should guarantee the same number of columns as the compare monthView
        frame.setSize(compare.getPreferredSize().width + 50, monthView.getPreferredSize().height + 50);
        frame.validate();
        // build a date corresponding to the expected end of next month
        calendar.setTime(last);
        // next month
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        CalendarUtils.endOfMonth(calendar);
        assertEquals(calendar.getTime(), monthView.getLastDisplayedDay());
    }

    
    /**
     * Issue #708-swingx: updateUI changes state.
     * 
     * Here: test that firstDisplayedDate is unchanged.
     */
    public void testUpdateUIFirst() {
        final JXMonthView monthView = new JXMonthView();
        Date first = monthView.getFirstDisplayedDay();
        monthView.updateUI();
        assertEquals(first, monthView.getFirstDisplayedDay());
    };


    /**
     * Issue #708-swingx: updateUI changes state.
     * 
     * Here: test that lastDisplayedDate is unchanged.
     */
    public void testUpdateUILast() {
        final JXMonthView monthView = new JXMonthView();
        Date first = monthView.getLastDisplayedDay();
        monthView.updateUI();
        assertEquals(first, monthView.getLastDisplayedDay());
    };


    /**
     * Issue #660-swingx: JXMonthView must protect its calendar.
     * Client manipulation on calendar must not change internal state.
     * 
     * This is guaranteed by returning a clone instead of the life object.
     */
    public void testMonthViewCalendarInvariant() {
        JXMonthView monthView = new JXMonthView();
        TimeZone tz = monthView.getTimeZone();
        Calendar calendar = monthView.getCalendar();
        calendar.setTimeZone(getTimeZone(tz, CalendarUtils.THREE_HOURS));
        assertEquals("monthView must protect its calendar", tz, monthView.getTimeZone());
    }

    /**
     * Issue #660-swingx: JXMonthView must protect its calendar.
     * 
     * Added invariant to the monthView's getCalender: clone and
     * config to firstDisplayDate.
     * 
     * The various tests are various contexts which broke the 
     * expectation before fixing the issue. 
     * Here the context is: select.
     */
   public void testMonthViewCalendarInvariantOnSetSelection() {
      JXMonthView monthView = new JXMonthView();
      assertEquals(1, monthView.getCalendar().get(Calendar.DATE));
      Date first = monthView.getFirstDisplayedDay();
      assertEquals("monthViews calendar represents the first day of the month", 
              first, monthView.getCalendar().getTime());
      Calendar cal = Calendar.getInstance();
      // add one day, now we are on the second
      cal.setTime(first);
      cal.add(Calendar.DATE, 1);
      Date date = cal.getTime();
      monthView.addSelectionInterval(date , date);
      assertEquals("selection must not change the calendar", 
              first, monthView.getCalendar().getTime());
   }

   /**
    * Issue #660-swingx: JXMonthView must protect its calendar.
    * 
    * Added invariant to the monthView's getCalender: clone and
    * config to firstDisplayDate.
    * 
    * The various tests are various contexts which broke the 
    * expectation before fixing the issue. 
    * Here the context is: check for selection.
    */
   public void testMonthViewCalendarInvariantOnQuerySelectioon() {
      JXMonthView monthView = new JXMonthView();
      assertEquals(1, monthView.getCalendar().get(Calendar.DATE));
      Date first = monthView.getFirstDisplayedDay();
      assertEquals("monthViews calendar represents the first day of the month", 
              first, monthView.getCalendar().getTime());
      Calendar cal = Calendar.getInstance();
      // add one day, now we are on the second
      cal.setTime(first);
      cal.add(Calendar.DATE, 1);
      Date date = cal.getTime();
      monthView.isSelected(date);
      assertEquals("query selection must not change the calendar", 
              first, monthView.getCalendar().getTime());
   }


    /**
     * Issue #660-swingx: JXMonthView must protect its calendar.
     * 
     * Added invariant to the monthView's getCalender: clone and
     * config to firstDisplayDate.
     * 
     * The various tests are various contexts which broke the 
     * expectation before fixing the issue. 
     * Here the context is: set first displayed date (formerly left
     * the calendar at the last displayed date).
     */
    public void testMonthViewCalendarInvariantOnSetFirstDisplayedDate() {
      JXMonthView monthView = new JXMonthView();
      Date first = monthView.getFirstDisplayedDay();
      Calendar cal = Calendar.getInstance();
      // add one day, now we are on the second
      cal.setTime(first);
      cal.add(Calendar.MONTH, 1);
      Date next = cal.getTime();
      monthView.setFirstDisplayedDay(next);
      assertEquals("monthViews calendar represents the first day of the month", 
              next, monthView.getCalendar().getTime());
    }
    
    /**
     * safety net: add api ensureDateVisible with Date parameter
     */
    public void testEnsureDateVisibleDateParamNextYear() {
        JXMonthView monthView = new JXMonthView();
        Calendar temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        // sanity
        assertEquals("sanity...", temp.getTime(), monthView.getFirstDisplayedDay());
        calendar.add(Calendar.YEAR, 1);
        Date nextYear = calendar.getTime();
        temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        monthView.ensureDateVisible(nextYear);
        assertEquals("must be scrolled to next year", 
                temp.getTime(), monthView.getFirstDisplayedDay());
    }
    
    /**
     * safety net: add api ensureDateVisible with Date parameter
     */
    public void testEnsureDateVisibleDateParamNextMonth() {
        JXMonthView monthView = new JXMonthView();
        Calendar temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        assertEquals("sanity..", temp.getTime(), monthView.getFirstDisplayedDay());
        calendar.add(Calendar.MONTH, 1);
        Date nextMonth = calendar.getTime();
        temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        monthView.ensureDateVisible(nextMonth);
        assertEquals("must be scrolled to next month", 
                temp.getTime(), monthView.getFirstDisplayedDay());
    }

    /**
     * safety net: add api ensureDateVisible with Date parameter
     */
    public void testEnsureDateVisibleDateParamThisMonth() {
        JXMonthView monthView = new JXMonthView();
        Calendar temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        Date first = monthView.getFirstDisplayedDay();
        assertEquals("sanity...", temp.getTime(), first);
        CalendarUtils.endOfMonth(calendar);
        Date thisMonth = calendar.getTime();
        monthView.ensureDateVisible(thisMonth);
        assertEquals("same month, nothing changed", 
                first, monthView.getFirstDisplayedDay());
    }


    /**
     * safety net: refactor ensureDateVisible
     */
    public void testEnsureDateVisibleNextYear() {
        JXMonthView monthView = new JXMonthView();
        Calendar temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        assertEquals("sanity...", temp.getTime(), monthView.getFirstDisplayedDay());
        calendar.add(Calendar.YEAR, 1);
        Date nextYear = calendar.getTime();
        temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        monthView.ensureDateVisible(nextYear);
        assertEquals("must be scrolled to next year", temp.getTime(), monthView.getFirstDisplayedDay());
    }
    
    /**
     * safety net: refactor ensureDateVisible
     */
    public void testEnsureDateVisibleNextMonth() {
        JXMonthView monthView = new JXMonthView();
        Calendar temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        assertEquals("sanity ...", temp.getTime(), monthView.getFirstDisplayedDay());
        calendar.add(Calendar.MONTH, 1);
        Date nextMonth = calendar.getTime();
        temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        monthView.ensureDateVisible(nextMonth);
        assertEquals("must be scrolled to next month", 
                temp.getTime(), monthView.getFirstDisplayedDay());
    }

    /**
     * safety net: refactor ensureDateVisible
     */
    public void testEnsureDateVisibleThisMonth() {
        JXMonthView monthView = new JXMonthView();
        Calendar temp = (Calendar) calendar.clone();
        CalendarUtils.startOfMonth(temp);
        Date first = monthView.getFirstDisplayedDay();
        assertEquals("sanity ...", temp.getTime(), first);
        CalendarUtils.endOfMonth(calendar);
        Date thisMonth = calendar.getTime();
        monthView.ensureDateVisible(thisMonth);
        assertEquals("same month, nothing changed", first, monthView.getFirstDisplayedDay());
    }

    /**
     * safety net: move responsibility for lastDisplayedDate completely into ui.
     */
    public void testLastDisplayedDateInitial() {
        JXMonthView monthView = new JXMonthView();
        calendar.setTime(monthView.getFirstDisplayedDay());
        CalendarUtils.endOfMonth(calendar);
        assertEquals(calendar.getTime(), monthView.getLastDisplayedDay());
    }
    
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Selected dates are "start of day" in the timezone they had been 
     * selected. As such they make no sense in a new timezone: must
     * either be adjusted or cleared. Currently we clear the selection. 
     */
    public void testTimeZoneChangeClearSelection() {
        JXMonthView monthView = new JXMonthView();
        Date date = new Date();
        monthView.setSelectionDate(date);
        // sanity
        assertTrue(monthView.isSelected(date));
        monthView.setTimeZone(getTimeZone(monthView.getTimeZone(), CalendarUtils.THREE_HOURS));
        // accidentally passes - because it is meaningful only in the timezone 
        // it was set ...
        assertFalse(monthView.isSelected(date));
        assertTrue("selection must have been cleared", monthView.isSelectionEmpty());
    }
    
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Bound dates are "start of day" in the timezone they had been 
     * set. As such they make no sense in a new timezone: must
     * either be adjusted or cleared. Currently we clear the bound. 
     */
    public void testTimeZoneChangeResetLowerBound() {
        JXMonthView monthView = new JXMonthView();
        monthView.setLowerBound(yesterday);
        monthView.setTimeZone(getTimeZone(monthView.getTimeZone(), CalendarUtils.THREE_HOURS));
        assertEquals("lowerBound must have been reset", null, monthView.getLowerBound());
    }
    
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Bound dates are "start of day" in the timezone they had been 
     * set. As such they make no sense in a new timezone: must
     * either be adjusted or cleared. Currently we clear the bound. 
     */
    public void testTimeZoneChangeResetUpperBound() {
        JXMonthView monthView = new JXMonthView();
        monthView.setUpperBound(yesterday);
        monthView.setTimeZone(getTimeZone(monthView.getTimeZone(), CalendarUtils.THREE_HOURS));
        assertEquals("upperbound must have been reset", null, monthView.getUpperBound());
    }
    
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Flagged dates are "start of day" in the timezone they had been 
     * set. As such they make no sense in a new timezone: must
     * either be adjusted or cleared. Currently we clear them. 
     */
    public void testTimeZoneChangeResetFlaggedDates() {
        JXMonthView monthView = new JXMonthView();
        monthView.setFlaggedDates(new Date[] {yesterday});
        monthView.setTimeZone(getTimeZone(monthView.getTimeZone(), CalendarUtils.THREE_HOURS));
        // accidentally passes - because it is meaningful only in the timezone 
        // it was set ...
        assertFalse(monthView.isFlaggedDate(yesterday));
        // missing api
        // assertEquals(0, monthView.getFlaggedDates().size());
        assertFalse("flagged dates must have been cleared", monthView.hasFlaggedDates());
    }
    
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Unselectable dates are "start of day" in the timezone they had been 
     * set. As such they make no sense in a new timezone: must
     * either be adjusted or cleared. Currently we clear them. 
     */
    public void testTimeZoneChangeResetUnselectableDates() {
        JXMonthView monthView = new JXMonthView();
        monthView.setUnselectableDates(yesterday);
        monthView.setTimeZone(getTimeZone(monthView.getTimeZone(), CalendarUtils.THREE_HOURS));
        // accidentally passes - because it is meaningful only in the timezone 
        // it was set ...
        assertFalse(monthView.isUnselectableDate(yesterday));
        // missing api on JXMonthView
        assertEquals("unselectable dates must have been cleared", 
                0, monthView.getSelectionModel().getUnselectableDates().size());
    }
    
    /**
     * test anchor: set to param as passed int setFirstDisplayedDate
     */
    public void testAnchorDateInitial() {
        JXMonthView monthView = new JXMonthView();
        // sometime next month
        calendar.add(Calendar.MONTH, 1);
        monthView.setFirstDisplayedDay(calendar.getTime());
        assertEquals(calendar.getTime(), monthView.getAnchorDate());
        CalendarUtils.startOfMonth(calendar);
        assertEquals(calendar.getTime(), monthView.getFirstDisplayedDay());
    }

    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Here: test anchor invariant to time zone change
     */
    public void testTimeZoneChangeAnchorInvariant() {
        JXMonthView monthView = new JXMonthView();
        Date anchor = monthView.getAnchorDate();
        TimeZone timeZone = monthView.getTimeZone();
        // just interested in a different timezone, no quantification intended
        monthView.setTimeZone(getTimeZone(timeZone, CalendarUtils.THREE_HOURS));
        assertEquals("anchor must be invariant to timezone change", 
                anchor, monthView.getAnchorDate());
    }

    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Here: test that the first displayed date is offset by offset diff of 
     * timezones.
     * Configure the monthView with a fixed timezone to clear up the mist ...
     * 
     */
    public void testTimeZoneChangeToday() {
        JXMonthView monthView = new JXMonthView();
        // config with a known timezone and date
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        monthView.setTimeZone(tz);
        Calendar calendar = Calendar.getInstance(tz);
        Date today = calendar.getTime();
        monthView.setFirstDisplayedDay(today);
        Date anchor = monthView.getAnchorDate();
        assertEquals(today, anchor);
        Date firstDisplayed = monthView.getFirstDisplayedDay();
        calendar.setTime(firstDisplayed);
        assertTrue(CalendarUtils.isStartOfMonth(calendar));
        
        // get another timezone with known offset
        TimeZone tzOther = TimeZone.getTimeZone("GMT+7");
        // newOffset minus oldOffset (real time, adjusted to DST)
        int oldOffset = tz.getOffset(anchor.getTime());
        int newOffset = tzOther.getOffset(anchor.getTime());
        int realOffset = oldOffset - newOffset;
        monthView.setTimeZone(tzOther);
        Calendar otherCalendar = Calendar.getInstance(tzOther);
        otherCalendar.setTime(monthView.getFirstDisplayedDay());
        assertTrue(CalendarUtils.isStartOfMonth(otherCalendar));
        // PENDING JW: sure this is the correct direction of the shift?
        // yeah, think so: the anchor is fixed, moving the timezone results
        // in a shift into the opposite direction of the offset
        assertEquals("first displayed must be offset by real offset", 
                realOffset,  monthView.getFirstDisplayedDay().getTime() - firstDisplayed.getTime());
    }

    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Here: test that the first displayed date is offset by offset diff of 
     * timezones.
     * Configure the monthView with a fixed timezone to clear up the mist ...
     * 
     */
    public void testTimeZoneChangeOffsetFirstDisplayedDate() {
        JXMonthView monthView = new JXMonthView();
        // config with a known timezone and date
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        monthView.setTimeZone(tz);
        Calendar calendar = Calendar.getInstance(tz);
        Date today = calendar.getTime();
        monthView.setFirstDisplayedDay(today);
        Date anchor = monthView.getAnchorDate();
        assertEquals(today, anchor);
        Date firstDisplayed = monthView.getFirstDisplayedDay();
        calendar.setTime(firstDisplayed);
        assertTrue(CalendarUtils.isStartOfMonth(calendar));
        
        // get another timezone with known offset
        TimeZone tzOther = TimeZone.getTimeZone("GMT+7");
        // newOffset minus oldOffset (real time, adjusted to DST)
        int oldOffset = tz.getOffset(anchor.getTime());
        int newOffset = tzOther.getOffset(anchor.getTime());
        int realOffset = oldOffset - newOffset;
        monthView.setTimeZone(tzOther);
        Calendar otherCalendar = Calendar.getInstance(tzOther);
        otherCalendar.setTime(monthView.getFirstDisplayedDay());
        assertTrue(CalendarUtils.isStartOfMonth(otherCalendar));
        // PENDING JW: sure this is the correct direction of the shift?
        // yeah, think so: the anchor is fixed, moving the timezone results
        // in a shift into the opposite direction of the offset
        assertEquals("first displayed must be offset by real offset", 
                realOffset,  monthView.getFirstDisplayedDay().getTime() - firstDisplayed.getTime());
    }
    
    /**
     * Returns a timezone with a rawoffset with a different offset.
     * 
     * 
     * PENDING: this is acutally for european time, not really thought of 
     *   negative/rolling +/- problem?
     * 
     * @param timeZone the timezone to start with 
     * @param diffRawOffset the raw offset difference.
     * @return
     */
    private TimeZone getTimeZone(TimeZone timeZone, int diffRawOffset) {
        int offset = timeZone.getRawOffset();
        int newOffset = offset < 0 ? offset + diffRawOffset : offset - diffRawOffset;
        String[] availableIDs = TimeZone.getAvailableIDs(newOffset);
        TimeZone newTimeZone = TimeZone.getTimeZone(availableIDs[0]);
        return newTimeZone;
    }
    
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Here: test timezone fire
     */
    public void testTimeZoneChangeNotification() {
        JXMonthView monthView = new JXMonthView();
        TimeZone timezone = monthView.getTimeZone();
        int offset = timezone.getRawOffset();
        int oneHour = 60 * 1000 * 60;
        int newOffset = offset < 0 ? offset + oneHour : offset - oneHour;
        String[] availableIDs = TimeZone.getAvailableIDs(newOffset);
        TimeZone newTimeZone = TimeZone.getTimeZone(availableIDs[0]);
        // sanity
        assertFalse(timezone.equals(newTimeZone));
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setTimeZone(newTimeZone);
        TestUtils.assertPropertyChangeEvent(report, 
                "timeZone", timezone, newTimeZone, false);
    }
    

    /**
     * Issue #563-swingx: keybindings active if not focused.
     * Test that the bindings are dynamically installed when
     * shown in popup and de-installed if shown not in popup.
    */
    public void testComponentInputMapEnabledControlsFocusedKeyBindings() {
        JXMonthView monthView = new JXMonthView();
        // initial: no bindings
        assertEquals("monthView must not have in-focused keyBindings", 0, 
                monthView.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).size());
        monthView.setComponentInputMapEnabled(true);
        // setting the flag installs bindings
        assertTrue("monthView must have in-focused keyBindings after showing in popup",  
              monthView.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).size() > 0);
        monthView.setComponentInputMapEnabled(false);
        // resetting the flag uninstalls the bindings
        assertEquals("monthView must not have in-focused keyBindings", 0, 
                monthView.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).size());
    }

    /**
     * Test default value and property change notificateion of 
     * the componentInputMapEnabled property.
     *
     */
    public void testComponentInputMapEnabled() {
        JXMonthView monthView = new JXMonthView();
        assertFalse("the default value must be false", 
                monthView.isComponentInputMapEnabled());
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setComponentInputMapEnabled(true);
        TestUtils.assertPropertyChangeEvent(report, 
                "componentInputMapEnabled", false, true, false);
        report.clear();
        monthView.setComponentInputMapEnabled(false);
        TestUtils.assertPropertyChangeEvent(report, 
                "componentInputMapEnabled", true, false, false);
    }
    
    /**
     * test doc'ed behaviour: model must not be null.
     * PENDING: the old problem - how do we test fail-fast implemented? 
     *
     */
    public void testSetModelNull() {
        JXMonthView monthView = new JXMonthView();
        assertNotNull(monthView.getSelectionModel());
        try {
            monthView.setSelectionModel(null);
            fail("null model must not be accepted");
        } catch (NullPointerException ex) {
            // expected - but ...?
            LOG.info("got NPE as expected - but how test fail fast? \n " 
                    + ex.getMessage());
        }
    }
    
    /**
     * Enhanced commit/cancel.
     * 
     * test that actions resets model.adjusting to false.
     */
    public void testCommitCancelResetsAdjusting() {
        JXMonthView monthView = new JXMonthView();
        monthView.getSelectionModel().setAdjusting(true);
        monthView.commitSelection();
        assertFalse("commit must reset adjusting", 
                monthView.getSelectionModel().isAdjusting());
        monthView.getSelectionModel().setAdjusting(true);
        monthView.cancelSelection();
        assertFalse("cancel must reset adjusting", 
                monthView.getSelectionModel().isAdjusting());
        
    }
    /**
     * Enhanced commit/cancel.
     * 
     * test that actions fire as expected.
     *
     */
    public void testCommitCancelAPIFires() {
        JXMonthView picker = new JXMonthView();
        ActionReport report = new ActionReport();
        picker.addActionListener(report);
        picker.commitSelection();
        assertEquals(1, report.getEventCount());
        assertEquals(JXMonthView.COMMIT_KEY, report.getLastActionCommand());
        report.clear();
        picker.cancelSelection();
        assertEquals(1, report.getEventCount());
        assertEquals(JXMonthView.CANCEL_KEY, report.getLastActionCommand());
    }
    
    /**
     * Enhanced commit/cancel.
     * 
     * test that actions fire as expected.
     *
     */
    public void testCommitCancelActionsFire() {
        JXMonthView picker = new JXMonthView();
        Action commitAction = picker.getActionMap().get(JXMonthView.COMMIT_KEY);
        ActionReport report = new ActionReport();
        picker.addActionListener(report);
        commitAction.actionPerformed(null);
        assertEquals(1, report.getEventCount());
        assertEquals(JXMonthView.COMMIT_KEY, report.getLastActionCommand());
        report.clear();
        Action cancelAction = picker.getActionMap().get(JXMonthView.CANCEL_KEY);
        cancelAction.actionPerformed(null);
        assertEquals(1, report.getEventCount());
        assertEquals(JXMonthView.CANCEL_KEY, report.getLastActionCommand());
    }


    /**
     * Enhanced commit/cancel.
     * 
     * test that actions are registered.
     *
     */
    public void testCommitCancelActionExist() {
        JXMonthView picker = new JXMonthView();
        assertNotNull(picker.getActionMap().get(JXMonthView.CANCEL_KEY));
        assertNotNull(picker.getActionMap().get(JXMonthView.COMMIT_KEY));
    }
    
    /**
     * Enhanced commit/cancel.
     * 
     * test that actions are the same for new/old cancel/accept.
     *
     */
    public void testCommitCancelSameAsOld() {
        JXMonthView picker = new JXMonthView();
        assertSame(picker.getActionMap().get("cancelSelection"),
                picker.getActionMap().get(JXMonthView.CANCEL_KEY));
        assertSame(picker.getActionMap().get("acceptSelection"),
                picker.getActionMap().get(JXMonthView.COMMIT_KEY));
    }

    /**
     * for now keep the old postAction.
     *
     */
    @SuppressWarnings("deprecation")
    public void testCommitCancelPreserveOld() {
        JXDatePicker picker = new JXDatePicker();
        ActionReport report = new ActionReport();
        picker.addActionListener(report);
        picker.postActionEvent();
        assertEquals(picker.getActionCommand(), report.getLastActionCommand());
    }

    
    /**
     * BasicMonthViewUI: use adjusting api in keyboard actions.
     * Here: test reset in cancel action.
     */
    public void testAdjustingResetOnCancel() {
        JXMonthView view = new JXMonthView();
        Action select = view.getActionMap().get("selectNextDay");
        select.actionPerformed(null);
        DateSelectionReport report = new DateSelectionReport();
        view.getSelectionModel().addDateSelectionListener(report);
        Action cancel = view.getActionMap().get("cancelSelection");
        cancel.actionPerformed(null);
        assertFalse("ui keyboard action must have stopped model adjusting", 
                view.getSelectionModel().isAdjusting());
        assertEquals(2, report.getEventCount());
    }
    /**
     * BasicMonthViewUI: use adjusting api in keyboard actions.
     * Here: test reset in accept action.
     */
    public void testAdjustingResetOnAccept() {
        JXMonthView view = new JXMonthView();
        Action select = view.getActionMap().get("selectNextDay");
        select.actionPerformed(null);
        DateSelectionReport report = new DateSelectionReport();
        view.getSelectionModel().addDateSelectionListener(report);
        Action cancel = view.getActionMap().get("acceptSelection");
        cancel.actionPerformed(null);
        assertFalse("ui keyboard action must have stopped model adjusting", 
                view.getSelectionModel().isAdjusting());
        assertEquals(1, report.getEventCount());
        assertEquals(EventType.ADJUSTING_STOPPED, report.getLastEvent().getEventType());
    }

    /**
     * BasicMonthViewUI: use adjusting api in keyboard actions.
     * Here: test set selection action.
     */
    public void testAdjustingSetOnSelect() {
        JXMonthView view = new JXMonthView();
        DateSelectionReport report = new DateSelectionReport();
        view.getSelectionModel().addDateSelectionListener(report);
        Action select = view.getActionMap().get("selectNextDay");
        select.actionPerformed(null);
        assertTrue("ui keyboard action must have started model adjusting", 
                view.getSelectionModel().isAdjusting());
        assertEquals(2, report.getEventCount());
        // assert that the adjusting is fired before the set
        assertEquals(EventType.DATES_SET, report.getLastEvent().getEventType());
    }
 
    /**
     * BasicMonthViewUI: use adjusting api in keyboard actions.
     * Here: test add selection action.
     */
    public void testAdjustingSetOnAdd() {
        JXMonthView view = new JXMonthView();
        // otherwise the add action isn't called
        view.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        DateSelectionReport report = new DateSelectionReport();
        view.getSelectionModel().addDateSelectionListener(report);
        Action select = view.getActionMap().get("adjustSelectionNextDay");
        select.actionPerformed(null);
        assertTrue("ui keyboard action must have started model adjusting", 
                view.getSelectionModel().isAdjusting());
        assertEquals(2, report.getEventCount());
        // assert that the adjusting is fired before the add
        // only: the type a set instead or the expected added - bug or feature?
        // assertEquals(EventType.DATES_ADDED, report.getLastEvent().getEventType());
        // for now we are only interested in the adjusting (must not be the last)
        // so go for what's actually fired instead of what's expected
         assertEquals(EventType.DATES_SET, report.getLastEvent().getEventType());
        
    }

    /**
     * Issue #557-swingx: always fire actionEvent after esc/enter.
     * 
     * test fire after accept.
     */
    public void testFireOnKeyboardAccept()  {
        JXMonthView monthView = new JXMonthView();
        Date date = new Date();
        monthView.setSelectionInterval(date, date);
        ActionReport report = new ActionReport();
        monthView.addActionListener(report);
        Action accept = monthView.getActionMap().get("acceptSelection"); 
        accept.actionPerformed(null);
        assertEquals(1, report.getEventCount());
    }

    /**
     * Issue #557-swingx: always fire actionEvent after esc/enter.
     * 
     * test fire after cancel.
     */
    public void testFireOnKeyboardCancel()  {
        JXMonthView monthView = new JXMonthView();
        Date date = new Date();
        monthView.setSelectionInterval(date, date);
        ActionReport report = new ActionReport();
        monthView.addActionListener(report);
        Action accept = monthView.getActionMap().get("cancelSelection");
        accept.actionPerformed(null);
        assertEquals(1, report.getEventCount());
    }

    /**
     * expose more selection constraint methods in JXMonthView
     *
     */
    public void testUpperBound() {
        JXMonthView view = new JXMonthView();
        view.setUpperBound(today);
        assertEquals(startOfDay(today), view.getUpperBound());
        // remove again
        view.setUpperBound(null);
        assertEquals(null, view.getUpperBound());
    }
    
    /**
     * expose more selection constraint methods in JXMonthView
     *
     */
    public void testLowerBound() {
        JXMonthView view = new JXMonthView();
        view.setLowerBound(today);
        assertEquals(startOfDay(today), view.getLowerBound());
        // remove again
        view.setLowerBound(null);
        assertEquals(null, view.getLowerBound());
    }

    /**
     * test unselectable: use methods with Date.
     *
     */
    public void testUnselectableDate() {
        JXMonthView monthView = new JXMonthView();
        // initial
        assertFalse(monthView.isUnselectableDate(today));
        // set unselectable today
        monthView.setUnselectableDates(today);
        assertTrue("raqw today must be unselectable", 
                monthView.isUnselectableDate(today));
        assertTrue("start of today must be unselectable", 
                monthView.isUnselectableDate(startOfDay(today)));
        assertTrue("end of today must be unselectable", 
                monthView.isUnselectableDate(endOfDay(today)));
        monthView.setUnselectableDates();
        assertFalse(monthView.isUnselectableDate(today));
        assertFalse(monthView.isUnselectableDate(startOfDay(today)));
        assertFalse(monthView.isUnselectableDate(endOfDay(today)));
    }

    /**
     * test unselectable: use methods with Date.
     * test NPE as doc'ed.
     */
    public void testUnselectableDatesNPE() {
        JXMonthView monthView = new JXMonthView();
        try {
            monthView.setUnselectableDates((Date[])null);
            fail("null array must throw NPE");
        } catch (NullPointerException e) {
            // expected
            LOG.info("got NPE as expected - how to test fail fast?");
        }
        try {
            monthView.setUnselectableDates(new Date[] {new Date(), null});
            fail("null elements must throw NPE");
        } catch (NullPointerException e) {
            // expected
            LOG.info("got NPE as expected - how to test fail fast?");
        }
    }

   
    /**
     * Issue #494-swingx: JXMonthView changed all passed-in dates
     *
     */
    public void testCleanupCopyDate() {
        JXMonthView monthView = new JXMonthView();
        Date copy = new Date(today.getTime());
        monthView.setSelectionInterval(today, today);
        assertEquals("the date used for selection must be unchanged", copy, today);
    }
    /**
     * test cover method: isSelectedDate
     *
     */
    public void testIsSelectedDate() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectionDate(today);
        assertTrue(monthView.isSelected(today));
        assertTrue(monthView.isSelected(startOfDay(today)));
    }
    
    /**
     * Sanity: test against regression
     * test cover method: isSelectedDate
     *
     */
    public void testIsSelectedDate494() {
        JXMonthView monthView = new JXMonthView();
        Date copy = new Date(today.getTime());
        monthView.setSelectionDate(today);
        // use today
        monthView.isSelected(today);
        assertEquals("date must not be changed in isSelected", copy, today);
    }
   
    /**
     * test cover method: setSelectedDate
     *
     */
    public void testSetSelectedDate() {
        JXMonthView monthView = new JXMonthView();
        Date copy = new Date(today.getTime());
        monthView.setSelectionDate(today);
        // sanity: date unchanged
        assertEquals(copy, today);
        assertEquals(startOfDay(today), monthView.getSelectionDate());
        monthView.setSelectionDate(null);
        assertTrue(monthView.isSelectionEmpty());
    }
    

    /**
     * test new (convenience) api on JXMonthView
     *
     */
    public void testGetSelected() {
        JXMonthView monthView = new JXMonthView();
        assertNull(monthView.getSelectionDate());
        monthView.setSelectionInterval(today, today);
        assertEquals("same day", startOfDay(today), monthView.getSelectionDate());
        // clear selection
        monthView.clearSelection();
        assertNull(monthView.getSelectionDate());
    }
    
    
    public void testDefaultConstructor() {
        JXMonthView monthView = new JXMonthView(Locale.US);
        assertTrue(monthView.isSelectionEmpty());
        assertTrue(SelectionMode.SINGLE_SELECTION == monthView.getSelectionMode());
        assertTrue(Calendar.SUNDAY == monthView.getFirstDayOfWeek());
    }

    public void testLocale() {
        Locale[] locales = Locale.getAvailableLocales();

        for (Locale l : locales) {
            JComponent.setDefaultLocale(l);

            JXMonthView monthView = new JXMonthView();
            Locale locale = monthView.getLocale();
            Calendar cal = Calendar.getInstance(locale);
            int expectedFirstDayOfWeek = cal.getFirstDayOfWeek();

            assertTrue(expectedFirstDayOfWeek == monthView.getFirstDayOfWeek());
        }
    }

    public void testEmptySelectionInitial() {
        JXMonthView monthView = new JXMonthView();
        assertTrue(monthView.isSelectionEmpty());
        SortedSet<Date> selection = monthView.getSelection();
        assertTrue(selection.isEmpty());
    }
    
    public void testEmptySelectionClear() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectionInterval(today, today);
        // sanity
        assertTrue(1 == monthView.getSelection().size());

        monthView.clearSelection();
        assertTrue(monthView.isSelectionEmpty());
        assertTrue(monthView.getSelection().isEmpty());
    }

    public void testSelectionModes() {
        JXMonthView monthView = new JXMonthView();
        assertEquals(SelectionMode.SINGLE_SELECTION, monthView
                .getSelectionMode());
        for (SelectionMode mode : SelectionMode.values()) {
            monthView.setSelectionMode(mode);
            assertEquals(mode, monthView.getSelectionModel().getSelectionMode());
            assertEquals(mode, monthView.getSelectionMode());
        }

    }

    public void testSingleSelection() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectionMode(SelectionMode.SINGLE_SELECTION);

        monthView.setSelectionInterval(yesterday, yesterday);
        assertTrue(1 == monthView.getSelection().size());
        assertEquals(startOfDay(yesterday), monthView.getFirstSelectionDate());

        monthView.setSelectionInterval(yesterday, afterTomorrow);
        assertTrue(1 == monthView.getSelection().size());
        assertEquals(startOfDay(yesterday), monthView.getFirstSelectionDate());
    }

    public void testSingleIntervalSelection() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);

        monthView.setSelectionInterval(yesterday, yesterday);
        assertTrue(1 == monthView.getSelection().size());
        assertEquals(startOfDay(yesterday), monthView.getFirstSelectionDate());

        monthView.setSelectionInterval(yesterday, tomorrow);
        
        assertTrue(3 == monthView.getSelection().size());
        assertEquals(startOfDay(yesterday), monthView.getFirstSelectionDate());
        assertEquals(startOfDay(tomorrow), monthView.getLastSelectionDate());

    }



    public void testMultipleIntervalSelection() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectionMode(SelectionMode.MULTIPLE_INTERVAL_SELECTION);

        monthView.setSelectionInterval(yesterday, yesterday);
        monthView.addSelectionInterval(afterTomorrow, afterTomorrow);
        
        assertEquals(2, monthView.getSelection().size());
        assertEquals(startOfDay(yesterday), monthView.getFirstSelectionDate());
        assertEquals(startOfDay(afterTomorrow), monthView.getLastSelectionDate());
    }



    public void testDateSelectionListener() {
        JXMonthView monthView = new JXMonthView();
        Mock listenerMock = mock(DateSelectionListener.class);
        listenerMock.expects(once()).method("valueChanged");
        DateSelectionListener listener = (DateSelectionListener) listenerMock.proxy();
        monthView.getSelectionModel().addDateSelectionListener(listener);

        Date date = new Date();
        monthView.setSelectionInterval(date, date);
    }

    /**
     * test flagged dates (deprecated api with long).
     */
    @SuppressWarnings("deprecation")
    public void testFlaggedDateMillis() {
        JXMonthView monthView = new JXMonthView();
        Date date = new Date();

        assertFalse(monthView.isFlaggedDate(date.getTime()));
        monthView.setFlaggedDates(new long[]{date.getTime()});
        assertTrue(monthView.isFlaggedDate(date.getTime()));
    }

    /**
     * test setting/checking flagged dates (api with Date)
     */
    public void testFlaggedDateRemoveNotify() {
        JXMonthView monthView = new JXMonthView();
        
        monthView.setFlaggedDates(tomorrow, yesterday);
        SortedSet<Date> oldFlagged = monthView.getFlaggedDates();
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.removeFlaggedDates(tomorrow);
        TestUtils.assertPropertyChangeEvent(report, "flaggedDates", 
                oldFlagged, monthView.getFlaggedDates());
    }

    /**
     * test setting/checking flagged dates (api with Date)
     */
    public void testFlaggedDateRemove() {
        JXMonthView monthView = new JXMonthView();
        
        monthView.addFlaggedDates(tomorrow, yesterday);
        assertEquals(2, monthView.getFlaggedDates().size());
        monthView.removeFlaggedDates(tomorrow);
        assertTrue(monthView.isFlaggedDate(yesterday));
        assertFalse(monthView.isFlaggedDate(tomorrow));
    }

    
    /**
     * test setting/checking flagged dates (api with Date)
     */
    public void testFlaggedDateClear() {
        JXMonthView monthView = new JXMonthView();
        
        monthView.addFlaggedDates(tomorrow, yesterday);
        assertEquals(2, monthView.getFlaggedDates().size());
        monthView.clearFlaggedDates();
        assertFalse("flagged dates must be cleared", monthView.hasFlaggedDates());
    }

    /**
     * test setting/checking flagged dates (api with Date)
     */
    public void testFlaggedDateClearNotify() {
        JXMonthView monthView = new JXMonthView();
        
        monthView.setFlaggedDates(tomorrow, yesterday);
        SortedSet<Date> oldFlagged = monthView.getFlaggedDates();
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.clearFlaggedDates();
        TestUtils.assertPropertyChangeEvent(report, "flaggedDates", 
                oldFlagged, monthView.getFlaggedDates());
    }

    /**
     * test setting/checking flagged dates (api with Date)
     */
    public void testFlaggedDateAdd() {
        JXMonthView monthView = new JXMonthView();
        
        monthView.setFlaggedDates(yesterday);
        monthView.addFlaggedDates(tomorrow);
        assertEquals(2, monthView.getFlaggedDates().size());
        assertTrue(monthView.isFlaggedDate(yesterday));
        assertTrue(monthView.isFlaggedDate(tomorrow));
    }
    /**
     * test setting/checking flagged dates (api with Date)
     */
    public void testFlaggedDateAddNotify() {
        JXMonthView monthView = new JXMonthView();
        
        monthView.setFlaggedDates(yesterday);
        SortedSet<Date> oldFlagged = monthView.getFlaggedDates();
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.addFlaggedDates(tomorrow);
        TestUtils.assertPropertyChangeEvent(report, "flaggedDates", 
                oldFlagged, monthView.getFlaggedDates());
    }
    /**
     * test setting/checking flagged dates (api with Date)
     */
    public void testFlaggedDateSet() {
        JXMonthView monthView = new JXMonthView();
        monthView.setFlaggedDates(today);
        assertTrue(monthView.isFlaggedDate(today));
        monthView.setFlaggedDates();
        assertFalse(monthView.isFlaggedDate(today));
    }
    /**
     * test setting/checking flagged dates (api with Date)
     */
    public void testFlaggedDateNotification() {
        JXMonthView monthView = new JXMonthView();
        SortedSet<Date> oldFlagged = monthView.getFlaggedDates();
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setFlaggedDates(today);
        TestUtils.assertPropertyChangeEvent(report, "flaggedDates", 
                oldFlagged, monthView.getFlaggedDates());
    }

    /**
     * test setting/checking flagged dates (api with Date)
     */
    public void testFlaggedDateGet() {
        JXMonthView monthView = new JXMonthView();
        Date date = new Date();
        SortedSet<Date> set = new TreeSet<Date>();
        set.add(monthView.getSelectionModel().getNormalizedDate(date));
        monthView.setFlaggedDates(date);
        assertEquals(set, monthView.getFlaggedDates());
    }
   
    public void testShowLeadingDates() {
        JXMonthView monthView = new JXMonthView();
        assertFalse(monthView.isShowingLeadingDates());
        monthView.setShowingLeadingDates(true);
        assertTrue(monthView.isShowingLeadingDates());
    }

    public void testShowTrailingDates() {
        JXMonthView monthView = new JXMonthView();
        assertFalse(monthView.isShowingTrailingDates());
        monthView.setShowingTrailingDates(true);
        assertTrue(monthView.isShowingTrailingDates());
    }

    /**
     * test unselectable: use methods with long.
     */
    @SuppressWarnings("deprecation")
    public void testUnselectableDateLong() {
        JXMonthView monthView = new JXMonthView();
        Date date = new Date();

        assertFalse(monthView.isUnselectableDate(date.getTime()));
        monthView.setUnselectableDates(new long[]{date.getTime()});
        assertTrue(monthView.isUnselectableDate(date.getTime()));
        // undocumented
        monthView.setUnselectableDates((long[]) null);
        assertFalse(monthView.isUnselectableDate(date.getTime()));
    }
    
    private Date startOfDay(Date date) {
        return CalendarUtils.startOfDay(calendar, date);
    }
 
    private Date endOfDay(Date date) {
        return CalendarUtils.endOfDay(calendar, date);
    }
}