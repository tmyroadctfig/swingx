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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXMonthView.SelectionMode;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.calendar.CalendarUtils;
import org.jdesktop.swingx.event.DateSelectionEvent.EventType;
import org.jdesktop.swingx.plaf.basic.BasicMonthViewUI;
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
     * Issue #706-swingx: picker doesn't update monthView.
     * 
     * Here: visualize weird side-effects of monthView.updateUI - year 
     * incremented.
     */
    public void interactiveSetToday() {
        final JXMonthView monthView = new JXMonthView(); //calendar.getTimeInMillis());
        monthView.setTraversable(true);
        final JXFrame frame = showInFrame(monthView, "MonthView update ui");
        Action action = new AbstractActionExt("increment today") {
            public void actionPerformed(ActionEvent e) {
                monthView.incrementToday();
//                SwingUtilities.updateComponentTreeUI(frame);
            }
            
        };
        addAction(frame, action);
        frame.pack();
    };


    /**
     * Issue #706-swingx: picker doesn't update monthView.
     * 
     * Here: visualize weird side-effects of monthView.updateUI - year 
     * incremented.
     */
    public void interactiveUpdateUIMonthView() {
//        calendar.set(1955, 10, 9);
        final JXMonthView monthView = new JXMonthView(); //calendar.getTimeInMillis());
        monthView.setTraversable(true);
        final JXFrame frame = showInFrame(monthView, "MonthView update ui");
        Action action = new AbstractActionExt("toggleUI") {
            public void actionPerformed(ActionEvent e) {
                monthView.updateUI();
//                SwingUtilities.updateComponentTreeUI(frame);
            }
            
        };
        addAction(frame, action);
        frame.pack();
    };

    
    /**
     * #703-swingx: set date to first of next doesn't update the view.
     * 
     * Behaviour is consistent with core components. Except that it is doing 
     * too much: revalidate most probably shouldn't change the scrolling state?
     * 
     * Misbehaviour here : multi-month spanning selection, travers two month into the future and
     * resize the frame - jumps back to first. Auto-scroll in the delegates
     * selection listener would have a similar effect.
     * 
     */
    public void interactiveAutoScrollOnResize() {
        final JXMonthView us = new JXMonthView();
        us.setTraversable(true);
        us.setSelectionMode(JXMonthView.SelectionMode.SINGLE_INTERVAL_SELECTION);
        final Calendar today = Calendar.getInstance();
        CalendarUtils.endOfMonth(today);
        Date start = today.getTime();
        today.add(Calendar.DAY_OF_MONTH, 60);
        us.setSelectionInterval(start, today.getTime());
        JXFrame frame = wrapInFrame(us, "resize");
        // quick check if lastDisplayed is updated on resize
        Action printLast = new AbstractActionExt("log last") {

            public void actionPerformed(ActionEvent e) {
                
                LOG.info("last updated?" + new Date(us.getLastDisplayedDate()));
            }
            
        };
        addAction(frame, printLast);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * #703-swingx: set date to first of next doesn't update the view.
     * 
     * Behaviour is consistent with core components. Except that it is doing 
     * too much: revalidate most probably shouldn't change the scrolling state?
     * 
     * Simulated misbehaviour here: multi-month spanning selection, travers into the future and
     * add selection at the end - jumps back to first. Auto-scroll in the delegates
     * selection listener would have the effect.
     * 
     */
    public void interactiveAutoScrollOnSelectionSim() {
        final JXMonthView us = new JXMonthView();
        us.setTraversable(true);
        us.setSelectionMode(JXMonthView.SelectionMode.SINGLE_INTERVAL_SELECTION);
        final Calendar today = Calendar.getInstance();
        CalendarUtils.endOfMonth(today);
        Date start = today.getTime();
        today.add(Calendar.DAY_OF_MONTH, 60);
        us.setSelectionInterval(start, today.getTime());
        JXFrame frame = wrapInFrame(us, "resize");
        Action nextMonthInterval = new AbstractActionExt("add selected") {

            public void actionPerformed(ActionEvent e) {
                if (us.isSelectionEmpty()) return;
                Date start = us.getSelectedDate();
                
                today.setTime(us.getSelection().last());
                today.add(Calendar.DAY_OF_MONTH, 5);
                us.addSelectionInterval(start, today.getTime());
                // here we simulate an auto-scroll
                us.ensureDateVisible(start.getTime());
            }
            
        };
        addAction(frame, nextMonthInterval);
        frame.pack();
        frame.setVisible(true);
    }
    
//----------------------
    
    /**
     * Issue 711-swingx: today is notify-only property.
     * Today is start of day.
     */
    public void testTodayIntial() {
        JXMonthView monthView = new JXMonthView();
        CalendarUtils.startOfDay(calendar);
        assertEquals(calendar.getTimeInMillis(), monthView.getTodayInMillis());
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
        assertEquals(calendar.getTimeInMillis(), monthView.getTodayInMillis());
    }
    
    /**
     * Issue 711-swingx: today is notify-only property.
     * SetToday should 
     */
    public void testTodaySet() {
        JXMonthView monthView = new JXMonthView();
        // tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        monthView.setTodayInMillis(calendar.getTimeInMillis());
        CalendarUtils.startOfDay(calendar);
        assertEquals(calendar.getTime(), new Date(monthView.getTodayInMillis()));
    }
    
    /**
     * Issue 711-swingx: today is notify-only property.
     * SetToday should 
     */
    public void testTodaySetNotification() {
        JXMonthView monthView = new JXMonthView();
        long today = monthView.getTodayInMillis();
        // tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        PropertyChangeReport report = new PropertyChangeReport();
        monthView.addPropertyChangeListener(report);
        monthView.setTodayInMillis(calendar.getTimeInMillis());
        CalendarUtils.startOfDay(calendar);
        TestUtils.assertPropertyChangeEvent(report, "todayInMillis", 
                today, calendar.getTimeInMillis());
    }
    /**
     * Issue #708-swingx: updateUI changes state.
     * 
     * Here: test that today is unchanged.
     */
    public void testUpdateUIToday() {
        JXMonthView monthView = new JXMonthView(0);
        long first = monthView.getTodayInMillis();
        monthView.updateUI();
        assertEquals(first, monthView.getTodayInMillis());
    };

    /**
     * #703-swingx: set date to first of next doesn't update the view.
     * 
     * Behaviour is consistent with core components. Except that it is doing 
     * too much: revalidate most probably shouldn't change the scrolling state?
     * 
     */
    public void testAutoScrollOnSelection() {
        JXMonthView us = new JXMonthView();
        final Calendar today = Calendar.getInstance();
        CalendarUtils.endOfMonth(today);
        us.setSelectedDate(today.getTime());
        long first = us.getFirstDisplayedDate();
        today.add(Calendar.DAY_OF_MONTH, 1);
        us.setSelectedDate(today.getTime());
        assertEquals(first, us.getFirstDisplayedDate());
        fail("expected behaviour but test is unsafe as long as the revalidate doesn't fail");
    }
    
    /**
     * #703-swingx: set date to first of next doesn't update the view.
     * 
     * Behaviour is consistent with core components. Except that it is doing 
     * too much: revalidate most probably shouldn't change the scrolling state?
     * 
     * Note: this test is inconclusive - expected to fail because the Handler.layoutContainer
     * actually triggers a ensureVisible (which it shouldn't) which changes the 
     * firstDisplayedDate, but the change has not yet happened in the invoke. Can be seen
     * while debugging, though. 
     * 
     * 
     * @throws InvocationTargetException 
     * @throws InterruptedException 
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
        us.setSelectedDate(today.getTime());
        final JXFrame frame = showInFrame(us, "");
        final long first = us.getFirstDisplayedDate();
        today.add(Calendar.DAY_OF_MONTH, 1);
        us.setSelectedDate(today.getTime());
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                us.revalidate();
                // need to validate frame - why?
                frame.validate();
                assertEquals("firstDisplayed must not be changed on revalidate", 
                        new Date(first), new Date(us.getFirstDisplayedDate()));
                assertEquals(first, us.getFirstDisplayedDate());
                fail("weird (threading issue?): the firstDisplayed is changed in layoutContainer - not testable here");
            }
        });
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
