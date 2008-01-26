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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.Action;

import org.jdesktop.swingx.JXMonthView.SelectionMode;
import org.jdesktop.swingx.calendar.CalendarUtils;
import org.jdesktop.swingx.event.DateSelectionEvent.EventType;
import org.jdesktop.swingx.test.DateSelectionReport;
import org.jdesktop.test.PropertyChangeReport;
import org.jdesktop.test.TestUtils;

/**
 * Test to expose known issues with JXMonthView.
 * 
 * @author Jeanette Winzenburg
 */
public class JXMonthViewIssues extends InteractiveTestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(JXMonthViewIssues.class
            .getName());

    // Constants used internally; unit is milliseconds
    @SuppressWarnings("unused")
    private static final int ONE_MINUTE = 60*1000;
    @SuppressWarnings("unused")
    private static final int ONE_HOUR   = 60*ONE_MINUTE;
    @SuppressWarnings("unused")
    private static final int THREE_HOURS = 3 * ONE_HOUR;
    @SuppressWarnings("unused")
    private static final int ONE_DAY    = 24*ONE_HOUR;

    public static void main(String[] args) {
      setSystemLF(true);
      JXMonthViewIssues  test = new JXMonthViewIssues();
      try {
          test.runInteractiveTests();
//        test.runInteractiveTests("interactive.*Locale.*");
//          test.runInteractiveTests("interactive.*AutoScroll.*");
//        test.runInteractiveTests("interactive.*UpdateUI.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }
    @SuppressWarnings("unused")
    private Calendar calendar;

    /**
     * Issue #567-swingx: JXDatepicker - clicking on unselectable date clears
     * picker's selection.
     * 
     * Here: visualize JXMonthView's behaviour. It fires a commit ... probably the 
     * wrong thing to do?. 
     * PENDING: better control the bounds ... 
     * PENDING: move into monthView after rename
     */
    public void interactiveBoundsMonthViewClickUnselectable() {
        JXMonthView monthView = new JXMonthView();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 7);
        monthView.setLowerBound(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        monthView.setUpperBound(calendar.getTime());
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got action " + e);
                
            }
            
        };
        monthView.addActionListener(l);
        showInFrame(monthView, "click unselectable fires ActionEvent");
    }

    
    /**
     * Issue #657-swingx: JXMonthView - unintuitive week-wise navigation with bounds
     * 
     * In a month, keyboard navigation beyond the upper/lower bound is prevented.
     * There's a leak in the region of the leading/trailing dates 
     * when navigating week-wise. 
     * 
     * PENDING: move into monthView after rename
     */
    public void interactiveBoundsNavigateBeyond() {
        JXMonthView monthView = new JXMonthView();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 2);
        // access the model directly requires to "clean" the date
        monthView.setLowerBound(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 27);
        monthView.setUpperBound(calendar.getTime());
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got action " + e);
                
            }
            
        };
        monthView.addActionListener(l);
        showInFrame(monthView, "navigate beyond bounds");
    }

    

    /**
     * Issue #657-swingx: JXMonthView - unintuitive week-wise navigation with bounds
     * 
     * Can't navigate at all if today is beyound the bounds
     * PENDING: move into monthView after rename
     */
    public void interactiveBoundsNavigateLocked() {
        JXMonthView monthView = new JXMonthView();
        // same time as monthView's today
        Calendar calendar = Calendar.getInstance();
        // set upper bound a week before today, 
        // to block navigation into all directions
        calendar.add(Calendar.DAY_OF_MONTH, -8);
        monthView.setUpperBound(calendar.getTime());
        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOG.info("got action " + e);
                
            }
            
        };
        monthView.addActionListener(l);
        showInFrame(monthView, "navigate: locked for today beyond bounds");
    }

    
//----------------------

    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: first day of week.
     */
    public void testCalendarsFirstDayOfWeek() {
        JXMonthView monthView = new JXMonthView();
        int first = monthView.getFirstDayOfWeek() + 1;
        // sanity
        assertTrue(first <= Calendar.SATURDAY);
        monthView.setFirstDayOfWeek(first);
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
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: MinimalDaysInFirstWeek.
     */
    public void testCalendarsMinimalDaysInFirstWeek() {
        
    }
    
    
    /**
     * Issue #733-swingx: model and monthView cal not synched.
     * 
     * Here: MinimalDaysInFirstWeek.
     */
    public void testCalendarsLocale() {
        
    }
    
    /**
     * Issue #733-swingx: TimeZone in model and monthView not synched.
     *  
     *  Test that the selected is normalized in the monthView's timezone. 
     */
    public void testCalendarsTimeZoneFlaggedDate() {
        JXMonthView monthView = new JXMonthView();
        // config with a known timezone and date
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        monthView.setTimeZone(tz);
        Date date = new Date();
        monthView.setFlaggedDates(new Date[] {date});
        assertTrue(monthView.isFlaggedDate(date));
        fail("no way to test same normalization for flagged and selected dates");
    }

    /**
     * Issue #733-swingx: TimeZone in model and monthView not synched.
     *  
     *  Test that the selected is normalized in the monthView's timezone. 
     */
    public void testCalendarsTimeZoneNormalizedDate() {
        JXMonthView monthView = new JXMonthView();
        // config with a known timezone and date
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        monthView.setTimeZone(tz);
        monthView.setSelectedDate(new Date());
        Date selected = monthView.getSelectedDate();
        Calendar calendar = monthView.getCalendar();
        calendar.setTime(selected);
        CalendarUtils.startOfDay(calendar);
        assertEquals(selected, calendar.getTime());
        assertTrue(CalendarUtils.isStartOfDay(calendar));
    }
    
    /**
     * Issue #733-swingx: TimeZone in model and monthView not synched.
     * 
     * Test selected - tells nothing, because it's normalized in the 
     * model's (default) calendar.
     */
    public void testCalendarsTimeZoneSelectedDate() {
        JXMonthView monthView = new JXMonthView();
        // config with a known timezone and date
        TimeZone tz = TimeZone.getTimeZone("GMT+4");
        monthView.setTimeZone(tz);
        Date date = new Date();
        monthView.setSelectedDate(date);
        assertTrue(monthView.isSelectedDate(date));
    }
    
    /**
     * temporarily removed weekinterval selection.
     * Need to review - why not in selectionModel?
     */
    public void testWeekIntervalSelection() {
        // PENDING: simplify to use pre-defined dates
        JXMonthView monthView = new JXMonthView(Locale.US);
        monthView.setSelectionMode(JXMonthView.SelectionMode.WEEK_INTERVAL_SELECTION);

        // Use a known date that falls on a Sunday, which just happens to be my birthday.
        calendar.set(Calendar.YEAR, 2006);
        calendar.set(Calendar.MONTH, Calendar.APRIL);
        calendar.set(Calendar.DAY_OF_MONTH, 9);
        CalendarUtils.startOfDay(calendar);
        Date startDate = calendar.getTime();
//        Date startDate = cleanupDate(calendar);

        Date endDate;
        calendar.set(Calendar.DAY_OF_MONTH, 13);
        endDate = calendar.getTime();

        monthView.setSelectionInterval(startDate, endDate);
        SortedSet<Date> selection = monthView.getSelection();
        assertTrue(startDate.equals(selection.first()));
        assertTrue(endDate.equals(selection.last()));

        calendar.set(Calendar.DAY_OF_MONTH, 20);
        endDate = calendar.getTime();
        monthView.setSelectionInterval(startDate, endDate);

        calendar.set(Calendar.DAY_OF_MONTH, 22);
        endDate = calendar.getTime();
        selection = monthView.getSelection();

        assertEquals(startDate, selection.first());
        assertTrue(endDate.equals((selection.last())));
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
        fail("spurious failures - probably wrong assumption in Timezone math");
    }
   
   /**
    * characterize calendar: minimal days in first week
    * Different for US (1) and Europe (4)
    */
   public void testCalendarMinimalDaysInFirstWeek() {
       Calendar us = Calendar.getInstance(Locale.US);
       assertEquals(1, us.getMinimalDaysInFirstWeek());
       Calendar french = Calendar.getInstance(Locale.FRENCH);
       assertEquals("french/european calendar", 4, french.getMinimalDaysInFirstWeek());
       fail("Revisit: monthView should respect locale setting? (need to test)");
   }
   
   /**
    * characterize calendar: first day of week 
    * Can be set arbitrarily. Hmmm ... when is that useful?
    */
   public void testCalendarFirstDayOfWeek() {
       Calendar french = Calendar.getInstance(Locale.FRENCH);
       assertEquals(Calendar.MONDAY, french.getFirstDayOfWeek());
       Calendar us = Calendar.getInstance(Locale.US);
       assertEquals(Calendar.SUNDAY, us.getFirstDayOfWeek());
       // JW: when would we want that?
       us.setFirstDayOfWeek(Calendar.FRIDAY);
       assertEquals(Calendar.FRIDAY, us.getFirstDayOfWeek());
       fail("Revisit: why expose setting of firstDayOfWeek? Auto-set with locale");
   }

   
   /**
    * BasicMonthViewUI: use adjusting api in keyboard actions.
    * Here: test add selection action.
    * 
    * TODO: this fails (unrelated to the adjusting) because the
    * the selectionn changing event type is DATES_SET instead of 
    * the expected DATES_ADDED.  What's wrong - expectation or type?
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
       // only: the ui fires a set instead - bug or feature?
        assertEquals(EventType.DATES_ADDED, report.getLastEvent().getEventType());
   }

//------------------- utility
   
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
   @SuppressWarnings("unused")
   private TimeZone getTimeZone(TimeZone timeZone, int diffRawOffset) {
       int offset = timeZone.getRawOffset();
       int newOffset = offset < 0 ? offset + diffRawOffset : offset - diffRawOffset;
       String[] availableIDs = TimeZone.getAvailableIDs(newOffset);
       TimeZone newTimeZone = TimeZone.getTimeZone(availableIDs[0]);
       return newTimeZone;
   }

   @SuppressWarnings("unused")
   private String[] getTimeZoneIDs() {
       List<String> zoneIds = new ArrayList<String>();
       for (int i = -12; i <= 12; i++) {
           String sign = i < 0 ? "-" : "+";
           zoneIds.add("GMT" + sign + i);
       }
       return zoneIds.toArray(new String[zoneIds.size()]);
   }
   
  
    @Override
    protected void setUp() throws Exception {
        calendar = Calendar.getInstance();
    }

  
}
