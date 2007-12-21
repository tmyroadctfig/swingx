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

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JComponent;

import org.jdesktop.swingx.JXMonthView.SelectionMode;
import org.jdesktop.swingx.calendar.CalendarUtils;
import org.jdesktop.swingx.calendar.DateSelectionModel;
import org.jdesktop.swingx.calendar.DateUtils;
import org.jdesktop.swingx.event.DateSelectionListener;
import org.jdesktop.swingx.event.DateSelectionEvent.EventType;
import org.jdesktop.swingx.test.DateSelectionReport;
import org.jdesktop.swingx.test.XTestUtils;
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
    private Calendar cal;
    private Locale componentLocale;
    // Constants used internally; unit is milliseconds
    private static final int ONE_MINUTE = 60*1000;
    private static final int ONE_HOUR   = 60*ONE_MINUTE;
    private static final int THREE_HOURS = 3 * ONE_HOUR;
    @SuppressWarnings("unused")
    private static final int ONE_DAY    = 24*ONE_HOUR;

    public void setUp() {
        cal = Calendar.getInstance();

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
     * Issue #660-swingx: JXMonthView must protect its calendar.
     * Client manipulation on calendar must not change internal state.
     * 
     * This is guaranteed by returning a clone instead of the life object.
     */
    public void testMonthViewCalendarInvariant() {
        JXMonthView monthView = new JXMonthView();
        TimeZone tz = monthView.getTimeZone();
        Calendar calendar = monthView.getCalendar();
        calendar.setTimeZone(getTimeZone(tz, THREE_HOURS));
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
      Date first = new Date(monthView.getFirstDisplayedDate());
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
      Date first = new Date(monthView.getFirstDisplayedDate());
      assertEquals("monthViews calendar represents the first day of the month", 
              first, monthView.getCalendar().getTime());
      Calendar cal = Calendar.getInstance();
      // add one day, now we are on the second
      cal.setTime(first);
      cal.add(Calendar.DATE, 1);
      Date date = cal.getTime();
      monthView.isSelectedDate(date);
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
      Date first = new Date(monthView.getFirstDisplayedDate());
      Calendar cal = Calendar.getInstance();
      // add one day, now we are on the second
      cal.setTime(first);
      cal.add(Calendar.MONTH, 1);
      Date next = cal.getTime();
      monthView.setFirstDisplayedDate(next.getTime());
      assertEquals("monthViews calendar represents the first day of the month", 
              next, monthView.getCalendar().getTime());
    }

    /**
     * safety net: refactor ensureDateVisible
     */
    public void testEnsureDateVisibleNextYear() {
        JXMonthView monthView = new JXMonthView();
        Calendar temp = (Calendar) cal.clone();
        CalendarUtils.startOfMonth(temp);
        assertEquals(monthView.getFirstDisplayedDate(), temp.getTimeInMillis());
        cal.add(Calendar.YEAR, 1);
        Date nextYear = cal.getTime();
        temp = (Calendar) cal.clone();
        CalendarUtils.startOfMonth(temp);
        monthView.ensureDateVisible(nextYear.getTime());
        assertEquals(monthView.getFirstDisplayedDate(), temp.getTimeInMillis());
    }
    
    /**
     * safety net: refactor ensureDateVisible
     */
    public void testEnsureDateVisibleNextMonth() {
        JXMonthView monthView = new JXMonthView();
        Calendar temp = (Calendar) cal.clone();
        CalendarUtils.startOfMonth(temp);
        assertEquals(monthView.getFirstDisplayedDate(), temp.getTimeInMillis());
        cal.add(Calendar.MONTH, 1);
        Date nextMonth = cal.getTime();
        temp = (Calendar) cal.clone();
        CalendarUtils.startOfMonth(temp);
        monthView.ensureDateVisible(nextMonth.getTime());
        assertEquals(monthView.getFirstDisplayedDate(), temp.getTimeInMillis());
    }

    /**
     * safety net: refactor ensureDateVisible
     */
    public void testEnsureDateVisibleThisMonth() {
        JXMonthView monthView = new JXMonthView();
        Calendar temp = (Calendar) cal.clone();
        CalendarUtils.startOfMonth(temp);
        long first = monthView.getFirstDisplayedDate();
        assertEquals(first, temp.getTimeInMillis());
        CalendarUtils.endOfMonth(cal);
        Date thisMonth = cal.getTime();
        monthView.ensureDateVisible(thisMonth.getTime());
        assertEquals("same month, nothing changed", first, monthView.getFirstDisplayedDate());
    }

    /**
     * safety net: move responsibility for lastDisplayedDate completely into ui.
     */
    public void testLastDisplayedDateInitial() {
        JXMonthView monthView = new JXMonthView();
        cal.setTimeInMillis(monthView.getFirstDisplayedDate());
        CalendarUtils.endOfMonth(cal);
        assertEquals(cal.getTime(), new Date(monthView.getLastDisplayedDate()));
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
        monthView.setSelectedDate(date);
        // sanity
        assertTrue(monthView.isSelectedDate(date));
        monthView.setTimeZone(getTimeZone(monthView.getTimeZone(), THREE_HOURS));
        // accidentally passes - because it is meaningful only in the timezone 
        // it was set ...
        assertFalse(monthView.isSelectedDate(date));
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
        monthView.setLowerBound(XTestUtils.getStartOfToday(-5));
        monthView.setTimeZone(getTimeZone(monthView.getTimeZone(), THREE_HOURS));
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
        monthView.setUpperBound(XTestUtils.getStartOfToday(-5));
        monthView.setTimeZone(getTimeZone(monthView.getTimeZone(), THREE_HOURS));
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
        long time = XTestUtils.getStartOfToday(-5).getTime();
        monthView.setFlaggedDates(new long[] {time});
        monthView.setTimeZone(getTimeZone(monthView.getTimeZone(), THREE_HOURS));
        // accidentally passes - because it is meaningful only in the timezone 
        // it was set ...
        assertFalse(monthView.isFlaggedDate(time));
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
        Date date = XTestUtils.getStartOfToday(-5);
        monthView.setUnselectableDates(date);
        monthView.setTimeZone(getTimeZone(monthView.getTimeZone(), THREE_HOURS));
        // accidentally passes - because it is meaningful only in the timezone 
        // it was set ...
        assertFalse(monthView.isUnselectableDate(date));
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
        cal.add(Calendar.MONTH, 1);
        monthView.setFirstDisplayedDate(cal.getTimeInMillis());
        assertEquals(cal.getTime(), monthView.getAnchorDate());
        CalendarUtils.startOfMonth(cal);
        assertEquals(cal.getTimeInMillis(), monthView.getFirstDisplayedDate());
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
        monthView.setTimeZone(getTimeZone(timeZone, THREE_HOURS));
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
        monthView.setFirstDisplayedDate(today.getTime());
        Date anchor = monthView.getAnchorDate();
        assertEquals(today, anchor);
        long firstDisplayed = monthView.getFirstDisplayedDate();
        calendar.setTimeInMillis(firstDisplayed);
        assertTrue(CalendarUtils.isStartOfMonth(calendar));
        
        // get another timezone with known offset
        TimeZone tzOther = TimeZone.getTimeZone("GMT+7");
        // newOffset minus oldOffset (real time, adjusted to DST)
        int oldOffset = tz.getOffset(anchor.getTime());
        int newOffset = tzOther.getOffset(anchor.getTime());
        int realOffset = oldOffset - newOffset;
        monthView.setTimeZone(tzOther);
        Calendar otherCalendar = Calendar.getInstance(tzOther);
        otherCalendar.setTimeInMillis(monthView.getFirstDisplayedDate());
        assertTrue(CalendarUtils.isStartOfMonth(otherCalendar));
        // PENDING JW: sure this is the correct direction of the shift?
        // yeah, think so: the anchor is fixed, moving the timezone results
        // in a shift into the opposite direction of the offset
        assertEquals("first displayed must be offset by real offset", 
                realOffset,  monthView.getFirstDisplayedDate() - firstDisplayed);
    }

    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     * Here: test today notification.
     */
    public void testTimeZoneChangeTodayNotification() {
        JXMonthView monthView = new JXMonthView();
        TimeZone other = getTimeZone(monthView.getTimeZone(), THREE_HOURS);
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setTimeZone(other);
        Calendar calendar = Calendar.getInstance();
        CalendarUtils.startOfDay(calendar);
        Date today = calendar.getTime();
        calendar.setTimeZone(other);
        CalendarUtils.startOfDay(calendar);
        Date otherToday = calendar.getTime(); 
            // sanity
        assertFalse(today.equals(otherToday));
        TestUtils.assertPropertyChangeEvent(report, 
                "today", today.getTime(), otherToday.getTime(), false);
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
        monthView.setFirstDisplayedDate(today.getTime());
        Date anchor = monthView.getAnchorDate();
        assertEquals(today, anchor);
        long firstDisplayed = monthView.getFirstDisplayedDate();
        calendar.setTimeInMillis(firstDisplayed);
        assertTrue(CalendarUtils.isStartOfMonth(calendar));
        
        // get another timezone with known offset
        TimeZone tzOther = TimeZone.getTimeZone("GMT+7");
        // newOffset minus oldOffset (real time, adjusted to DST)
        int oldOffset = tz.getOffset(anchor.getTime());
        int newOffset = tzOther.getOffset(anchor.getTime());
        int realOffset = oldOffset - newOffset;
        monthView.setTimeZone(tzOther);
        Calendar otherCalendar = Calendar.getInstance(tzOther);
        otherCalendar.setTimeInMillis(monthView.getFirstDisplayedDate());
        assertTrue(CalendarUtils.isStartOfMonth(otherCalendar));
        // PENDING JW: sure this is the correct direction of the shift?
        // yeah, think so: the anchor is fixed, moving the timezone results
        // in a shift into the opposite direction of the offset
        assertEquals("first displayed must be offset by real offset", 
                realOffset,  monthView.getFirstDisplayedDate() - firstDisplayed);
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
        Date full = cal.getTime();
        Date cleaned = XTestUtils.getStartOfDay(cal);
        view.setUpperBound(full);
        assertEquals(cleaned, view.getUpperBound());
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
        Date full = cal.getTime();
        Date cleaned = XTestUtils.getStartOfDay(cal);
        view.setLowerBound(full);
        assertEquals(cleaned, view.getLowerBound());
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
        Date date = XTestUtils.getStartOfToday();
        assertFalse(monthView.isUnselectableDate(date));
        monthView.setUnselectableDates(date);
        assertTrue(monthView.isUnselectableDate(date));

        monthView.setUnselectableDates();
        assertFalse(monthView.isUnselectableDate(date));
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
        if (cal.get(Calendar.HOUR_OF_DAY) < 5) { 
            // sanity: has time elements
            cal.set(Calendar.HOUR_OF_DAY, 5);
        }
        Date today = cal.getTime();
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
        Date today = new Date();
        Date selected = XTestUtils.getStartOfToday();
        monthView.setSelectedDate(today);
        assertTrue(monthView.isSelectedDate(today));
        assertTrue(monthView.isSelectedDate(selected));
    }
    
    /**
     * Sanity: test against regression
     * test cover method: isSelectedDate
     *
     */
    public void testIsSelectedDate494() {
        JXMonthView monthView = new JXMonthView();
        Date today = new Date();
        Date copy = new Date(today.getTime());
        Date selected = XTestUtils.getStartOfToday();
        monthView.setSelectedDate(selected);
        // use today
        monthView.isSelectedDate(today);
        assertEquals("date must not be changed in isSelected", copy, today);
    }
   
    /**
     * test cover method: setSelectedDate
     *
     */
    public void testSetSelectedDate() {
        JXMonthView monthView = new JXMonthView();
        Date today = new Date();
        Date copy = new Date(today.getTime());
        Date selected = XTestUtils.getStartOfToday();
        monthView.setSelectedDate(today);
        // sanity: date unchanged
        assertEquals(copy, today);
        assertEquals(selected, monthView.getSelectedDate());
        // sanity:
        assertEquals(selected, monthView.getSelection().first());
        monthView.setSelectedDate(null);
        assertTrue(monthView.isSelectionEmpty());
    }
    

    /**
     * test new (convenience) api on JXMonthView
     *
     */
    public void testGetSelected() {
        JXMonthView monthView = new JXMonthView();
        Date selected = monthView.getSelectedDate();
        assertNull(selected);
        Date today = cleanupDate(cal);
        monthView.setSelectionInterval(today, today);
        assertEquals("same day", today, monthView.getSelectedDate());
    }
    
    public void testDefaultConstructor() {
        JXMonthView monthView = new JXMonthView(Locale.US);
        assertTrue(monthView.isSelectionEmpty());
        assertTrue(JXMonthView.SelectionMode.SINGLE_SELECTION == monthView.getSelectionMode());
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

    public void testNullSelection() {
        JXMonthView monthView = new JXMonthView();
        SortedSet<Date> selection = monthView.getSelection();
        assertTrue(selection.isEmpty());

        Date date = new Date();
        monthView.setSelectionInterval(date, date);
        selection = monthView.getSelection();
        assertTrue(1 == selection.size());

        //NB JXMonthView removes the time component from its dates; oh and just in case it's tested at *exactly* midnight..
        assertTrue(date.after(selection.first()) || (date != selection.first() && date.equals(selection.first())));    // our Date object shouldnt change
        assertTrue(DateUtils.startOfDay(date).equals(selection.first()));

        monthView.clearSelection();
        assertTrue(monthView.isSelectionEmpty());
        selection = monthView.getSelection();
        assertTrue(selection.isEmpty());
    }

    public void testNoSelectionMode() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectionMode(JXMonthView.SelectionMode.NO_SELECTION);

        Date date = new Date();
        monthView.setSelectionInterval(date, date);
        assertTrue(monthView.isSelectionEmpty());
    }

    public void testSingleSelection() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectionMode(JXMonthView.SelectionMode.SINGLE_SELECTION);

        Date today = new Date();
        monthView.setSelectionInterval(today, today);
        SortedSet<Date> selection = monthView.getSelection();
        assertTrue(1 == selection.size());
        assertTrue(DateUtils.startOfDay(today).equals(selection.first()));

        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date tomorrow = cleanupDate(cal);
        monthView.setSelectionInterval(today, tomorrow);
        selection = monthView.getSelection();
        assertTrue(1 == selection.size());
        assertTrue(DateUtils.startOfDay(today).equals(selection.first()));
    }

    public void testSingleIntervalSelection() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectionMode(JXMonthView.SelectionMode.SINGLE_INTERVAL_SELECTION);

        Date today = new Date();
        monthView.setSelectionInterval(today, today);
        SortedSet<Date> selection = monthView.getSelection();
        assertTrue(1 == selection.size());
        assertTrue(DateUtils.startOfDay(today).equals(selection.first()));

        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date tomorrow = cleanupDate(cal);
        monthView.setSelectionInterval(today, tomorrow);
        selection = monthView.getSelection();
        assertTrue(2 == selection.size());
        assertTrue(DateUtils.startOfDay(today).equals(selection.first()));
        assertTrue(tomorrow.equals(selection.last()));
    }

    public void testWeekIntervalSelection() {
        JXMonthView monthView = new JXMonthView(Locale.US);
        monthView.setSelectionMode(JXMonthView.SelectionMode.WEEK_INTERVAL_SELECTION);

        // Use a known date that falls on a Sunday, which just happens to be my birthday.
        cal.set(Calendar.YEAR, 2006);
        cal.set(Calendar.MONTH, Calendar.APRIL);
        cal.set(Calendar.DAY_OF_MONTH, 9);
        Date startDate = cleanupDate(cal);

        Date endDate;
        cal.set(Calendar.DAY_OF_MONTH, 13);
        endDate = cal.getTime();

        monthView.setSelectionInterval(startDate, endDate);
        SortedSet<Date> selection = monthView.getSelection();
        assertTrue(startDate.equals(selection.first()));
        assertTrue(endDate.equals(selection.last()));

        cal.set(Calendar.DAY_OF_MONTH, 20);
        endDate = cal.getTime();
        monthView.setSelectionInterval(startDate, endDate);

        cal.set(Calendar.DAY_OF_MONTH, 22);
        endDate = cal.getTime();
        selection = monthView.getSelection();

        assertEquals(startDate, selection.first());
        assertTrue(endDate.equals((selection.last())));
    }

    public void testMultipleIntervalSelection() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectionMode(JXMonthView.SelectionMode.MULTIPLE_INTERVAL_SELECTION);

        cal.setTimeInMillis(System.currentTimeMillis());
        Date date1 = cleanupDate(cal);

        cal.add(Calendar.DAY_OF_MONTH, 5);
        Date date2 = cal.getTime();

        monthView.setSelectionInterval(date1, date1);
        monthView.addSelectionInterval(date2, date2);

        SortedSet<Date> selection = monthView.getSelection();
        assertTrue(2 == selection.size());
        assertTrue(date1.equals(selection.first()));
        assertTrue(date2.equals(selection.last()));
    }

    private Date cleanupDate(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public void testModelSelectionUpdate() {
        JXMonthView monthView = new JXMonthView();

        // The JXMonthView uses an underlying model mode of single selection when it is in no selection mode.
        monthView.setSelectionMode(JXMonthView.SelectionMode.NO_SELECTION);
        assertTrue(
                DateSelectionModel.SelectionMode.SINGLE_SELECTION == monthView.getSelectionModel().getSelectionMode());

        monthView.setSelectionMode(JXMonthView.SelectionMode.SINGLE_SELECTION);
        assertTrue(
                DateSelectionModel.SelectionMode.SINGLE_SELECTION == monthView.getSelectionModel().getSelectionMode());

        monthView.setSelectionMode(JXMonthView.SelectionMode.SINGLE_INTERVAL_SELECTION);
        assertTrue(
                DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION ==
                        monthView.getSelectionModel().getSelectionMode());

        // The JXMonthView uses an underlying model mode of single interval selection when it is in week selection mode.
        monthView.setSelectionMode(JXMonthView.SelectionMode.WEEK_INTERVAL_SELECTION);
        assertTrue(
                DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION ==
                        monthView.getSelectionModel().getSelectionMode());

        monthView.setSelectionMode(JXMonthView.SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        assertTrue(
                DateSelectionModel.SelectionMode.MULTIPLE_INTERVAL_SELECTION ==
                        monthView.getSelectionModel().getSelectionMode());

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

    public void testFlaggedDate() {
        JXMonthView monthView = new JXMonthView();
        Date date = new Date();

        assertFalse(monthView.isFlaggedDate(date.getTime()));
        monthView.setFlaggedDates(new long[]{date.getTime()});
        assertTrue(monthView.isFlaggedDate(date.getTime()));
    }

    public void testShowLeadingDates() {
        JXMonthView monthView = new JXMonthView();
        assertFalse(monthView.isShowingLeadingDates());
        monthView.setShowLeadingDates(true);
        assertTrue(monthView.isShowingLeadingDates());
    }

    public void testShowTrailingDates() {
        JXMonthView monthView = new JXMonthView();
        assertFalse(monthView.isShowingTrailingDates());
        monthView.setShowTrailingDates(true);
        assertTrue(monthView.isShowingTrailingDates());
    }

    /**
     * test unselectable: use methods with long.
     */
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
}