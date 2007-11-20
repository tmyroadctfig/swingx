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

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXMonthView.SelectionMode;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.calendar.CalendarUtils;
import org.jdesktop.swingx.event.DateSelectionEvent.EventType;
import org.jdesktop.swingx.test.DateSelectionReport;
import org.jdesktop.swingx.test.XTestUtils;

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
//      setSystemLF(true);
      JXMonthViewIssues  test = new JXMonthViewIssues();
      try {
          test.runInteractiveTests();
//        test.runInteractiveTests("interactive.*TimeZone.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }
    @SuppressWarnings("unused")
    private Calendar calendar;
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     */
    public void interactiveUpdateOnTimeZone() {
        JPanel panel = new JPanel();

        final JComboBox zoneSelector = new JComboBox(TimeZone.getAvailableIDs());
        final JXDatePicker picker = new JXDatePicker();
        final JXMonthView monthView = new JXMonthView();
        monthView.setSelectedDate(picker.getDate());
        monthView.setTraversable(true);
        // Synchronize the picker and selector's zones.
        zoneSelector.setSelectedItem(picker.getTimeZone().getID());

        // Set the picker's time zone based on the selected time zone.
        zoneSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String zone = (String) zoneSelector.getSelectedItem();
                TimeZone tz = TimeZone.getTimeZone(zone);
                picker.setTimeZone(tz);
                monthView.setTimeZone(tz);
              
                assertEquals(tz, monthView.getCalendar().getTimeZone());
            }
        });

        panel.add(zoneSelector);
        panel.add(picker);
        panel.add(monthView);
        JXFrame frame = showInFrame(panel, "display problems with non-default timezones");
        Action assertAction = new AbstractActionExt("assert dates") {

            public void actionPerformed(ActionEvent e) {
                Calendar cal = monthView.getCalendar();
                LOG.info("cal/firstDisplayed" + 
                        cal.getTime() +"/" + new Date(monthView.getFirstDisplayedDate()));
            }
            
        };
        addAction(frame, assertAction);
        frame.pack();
    }
    
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     */
    public void interactiveTimeZoneClearDateState() {
        JPanel panel = new JPanel();

        final JComboBox zoneSelector = new JComboBox(TimeZone.getAvailableIDs());
        final JXDatePicker picker = new JXDatePicker();
        final JXMonthView monthView = new JXMonthView();
        monthView.setSelectedDate(picker.getDate());
        monthView.setLowerBound(XTestUtils.getStartOfToday(-10));
        monthView.setUpperBound(XTestUtils.getStartOfToday(10));
        monthView.setUnselectableDates(XTestUtils.getStartOfToday(2));
        monthView.setFlaggedDates(new long[] {XTestUtils.getStartOfToday(4).getTime()});
        monthView.setTraversable(true);
        // Synchronize the picker and selector's zones.
        zoneSelector.setSelectedItem(picker.getTimeZone().getID());

        // Set the picker's time zone based on the selected time zone.
        zoneSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String zone = (String) zoneSelector.getSelectedItem();
                TimeZone tz = TimeZone.getTimeZone(zone);
                picker.setTimeZone(tz);
                monthView.setTimeZone(tz);
              
                assertEquals(tz, monthView.getCalendar().getTimeZone());
            }
        });

        panel.add(zoneSelector);
        panel.add(picker);
        panel.add(monthView);
        JXFrame frame = showInFrame(panel, "clear internal date-related state");
        Action assertAction = new AbstractActionExt("assert dates") {

            public void actionPerformed(ActionEvent e) {
                Calendar cal = monthView.getCalendar();
                LOG.info("cal/firstDisplayed" + 
                        cal.getTime() +"/" + new Date(monthView.getFirstDisplayedDate()));
            }
            
        };
        addAction(frame, assertAction);
        frame.pack();
    }
    
    private String[] getTimeZoneIDs() {
        List<String> zoneIds = new ArrayList<String>();
        for (int i = -12; i <= 12; i++) {
            String sign = i < 0 ? "-" : "+";
            zoneIds.add("GMT" + sign + i);
        }
        return zoneIds.toArray(new String[zoneIds.size()]);
    }
    
    /**
     * Issue #659-swingx: lastDisplayedDate must be synched.
     * 
     */
    public void interactiveLastDisplayed() {
        final JXMonthView month = new JXMonthView();
        month.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
        month.setTraversable(true);
        Action action = new AbstractActionExt("check lastDisplayed") {

            public void actionPerformed(ActionEvent e) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(month.getLastDisplayedDate());
                Date viewLast = cal.getTime();
                cal.setTimeInMillis(month.getUI().getLastDisplayedDate());
                Date uiLast = cal.getTime();
                if (!uiLast.equals(viewLast))
                LOG.info("last(view/ui): " + viewLast + "/" + uiLast);
                
            }
            
        };
        JXFrame frame = wrapInFrame(month, "default - for debugging only");
        addAction(frame, action);
        frame.setVisible(true);
    }

//----------------------
    
    /**
     * Issue #659-swingx: lastDisplayedDate must be synched.
     * test that lastDisplayed from monthView is same as lastDisplayed from ui.
     * 
     * Here: initial packed size - one month shown.
     * 
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    public void testLastDisplayedDateInitial() throws InterruptedException, InvocationTargetException {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run lastDisplayedDate - headless");
            return;
        }
        final JXMonthView monthView = new JXMonthView();
        final JXFrame frame = wrapInFrame(monthView, "");
        frame.setVisible(true);
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                long uiLast = monthView.getUI().getLastDisplayedDate();
                long viewLast = monthView.getLastDisplayedDate();
                assertEquals(uiLast, viewLast);
            }
        });
    }
    
    /**
     * 
     * Issue #659-swingx: lastDisplayedDate must be synched.
     * 
     * test that lastDisplayed from monthView is same as lastDisplayed from ui.
     * 
     * Here: change the size of the view which allows the ui to display more
     * columns/rows.
     * 
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    public void testLastDisplayedDateSizeChanged() throws InterruptedException, InvocationTargetException {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run lastDisplayedDate - headless");
            return;
        }
        final JXMonthView monthView = new JXMonthView();
        final JXFrame frame = wrapInFrame(monthView, "");
        frame.setVisible(true);
        frame.setSize(frame.getWidth() * 3, frame.getHeight() * 2);
        // force a revalidate
        frame.invalidate();
        frame.validate();
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                long uiLast = monthView.getUI().getLastDisplayedDate();
                long viewLast = monthView.getLastDisplayedDate();
                assertEquals(uiLast, viewLast);
            }
        });
    }
    

    /**
     * 
     * Issue #659-swingx: lastDisplayedDate must be synched.
     * 
     * test that ensureDateVisible works as doc'ed if multiple months shown: 
     * if the new date is in the
     * month following the last visible then the first must be set in a manner that
     * the date must be visible in the last month. 
     * 
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    public void testLastDisplayedDateSizeChangedEnsureVisible() throws InterruptedException, InvocationTargetException {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run lastDisplayedDate - headless");
            return;
        }
        final JXMonthView monthView = new JXMonthView();
        final JXFrame frame = wrapInFrame(monthView, "");
        frame.setVisible(true);
        frame.setSize(frame.getWidth() * 3, frame.getHeight() * 2);
        // force a revalidate
        frame.invalidate();
        frame.validate();
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(monthView.getFirstDisplayedDate());
                int firstMonth = calendar.get(Calendar.MONTH);
                long uiLast = monthView.getUI().getLastDisplayedDate();
                calendar.setTimeInMillis(uiLast);
                int lastMonth = calendar.get(Calendar.MONTH);
                // sanity: more than one month shown
                assertFalse(firstMonth == lastMonth);
                // first day of next month 
                calendar.add(Calendar.DATE, 1);
                // sanity
                int newLastMonth = calendar.get(Calendar.MONTH);
                assertFalse(lastMonth == newLastMonth);
                monthView.ensureDateVisible(calendar.getTimeInMillis());
                CalendarUtils.endOfMonth(calendar);
                long newUILast = monthView.getUI().getLastDisplayedDate();
                assertEquals(newUILast, monthView.getLastDisplayedDate());
                calendar.setTimeInMillis(newUILast);
                LOG.info("first/last: " + new Date(monthView.getFirstDisplayedDate()) + 
                        "/" + new Date(newUILast));
                assertEquals(newLastMonth, calendar.get(Calendar.MONTH));
            }
        });
    }
    

    /**
    * Characterize MonthView: initial firstDisplayedDate set to 
    * first day in the month of the current date.
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
    * Characterize MonthView: initial firstDisplayedDate set to 
    * first day in the month of the current date.
    */
   public void testMonthViewCalendarWasLastDisplayedDateSetFirstDisplayedDate() {
     JXMonthView monthView = new JXMonthView();
     Date first = new Date(monthView.getFirstDisplayedDate());
     Calendar cal = Calendar.getInstance();
     // add one day, now we are on the second
     cal.setTime(first);
     cal.add(Calendar.MONTH, 1);
     Date next = cal.getTime();
     monthView.setFirstDisplayedDate(next.getTime());
     assertEquals("calendar is changed to lastDisplayedDate", 
             new Date(monthView.getLastDisplayedDate()), monthView.getCalendar().getTime());
   }
   /**
    * 
    * no invariant for the monthView's calender?
    * monthViewUI at some places restores to firstDisplayedDay, why?
    * It probably should always - the calendar represents the 
    * first day of the currently shown month.
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
      monthView.isSelectedDate(new Date().getTime());
      assertEquals(first, monthView.getCalendar().getTime());
   }

   /**
    * 
    * no invariant for the monthView's calender?
    * monthViewUI at some places restores to firstDisplayedDay, why?
    * It probably should always - the calendar represents the 
    * first day of the currently shown month.
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
    * characterize calendar: minimal days in first week
    * Different for US (1) and Europe (4)
    */
   public void testCalendarMinimalDaysInFirstWeek() {
       Calendar us = Calendar.getInstance(Locale.US);
       assertEquals(1, us.getMinimalDaysInFirstWeek());
       Calendar french = Calendar.getInstance(Locale.FRENCH);
       assertEquals("french/european calendar", 1, french.getMinimalDaysInFirstWeek());
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
   }

   /**
    * Trying to figure monthView's calendar's invariant: has none?
    */
   public void testTimeZone() {
       JXMonthView monthView = new JXMonthView();
       Calendar cal = monthView.getCalendar();
       assertEquals(cal.getTimeZone(), monthView.getTimeZone());
       assertEquals(cal.getTime(), new Date(monthView.getFirstDisplayedDate()));
       assertEquals(0, cal.getTimeZone().getRawOffset() / ONE_HOUR);
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

  

   /**
     * 
     * Okay ... looks more like a confusing (me!) doc: the date in the
     * constructor is not the selection, but the date to use for the first
     * display. Hmm ...
     */
    public void testMonthViewInitialSelection() {
        JXMonthView monthView = new JXMonthView(new GregorianCalendar(2007, 6,
                28).getTimeInMillis());
        assertNotNull(monthView.getSelectedDate());
    }

    @Override
    protected void setUp() throws Exception {
        calendar = Calendar.getInstance();
    }

  
}
