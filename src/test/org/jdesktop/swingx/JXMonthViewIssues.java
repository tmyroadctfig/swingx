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
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXMonthView.SelectionMode;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.calendar.CalendarUtils;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
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
//          test.runInteractiveTests();
//        test.runInteractiveTests("interactive.*Locale.*");
          test.runInteractiveTests("interactive.*AutoScroll.*");
//        test.runInteractiveTests("interactive.*Blank.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }
    @SuppressWarnings("unused")
    private Calendar calendar;
 
    /**
     * #702-swingx: no days shown?
     * 
     * Not reproducible.
     */
    public void interactiveBlankMonthViewOnAdd() {
       final JComponent comp = Box.createHorizontalBox();
       comp.add(new JXMonthView());
       final JXFrame frame = wrapInFrame(comp, "blank view on add");
       Action next = new AbstractActionExt("new monthView") {

           public void actionPerformed(ActionEvent e) {
               comp.add(new JXMonthView());
               frame.pack();
           }
           
       };
       addAction(frame, next);
       frame.pack();
       frame.setVisible(true);
    };
    
    /**
     * #703-swingx: set date to first of next doesn't update the view.
     * 
     * Behaviour is consistent with core components. Except that it is doing 
     * too much: revalidate most probably shouldn't change the scrolling state?
     * 
     */
    public void interactiveAutoScrollOnSelectionMonthView() {
        final JXMonthView us = new JXMonthView();
        us.setSelectionMode(JXMonthView.SelectionMode.SINGLE_INTERVAL_SELECTION);
        final Calendar today = Calendar.getInstance();
        CalendarUtils.endOfMonth(today);
        us.setSelectedDate(today.getTime());
        JXFrame frame = wrapInFrame(us, "first day of next month");
        Action nextMonthInterval = new AbstractActionExt("next month interval") {

            public void actionPerformed(ActionEvent e) {
                if (us.isSelectionEmpty()) return;
                today.setTime(us.getSelectedDate());
                today.add(Calendar.DAY_OF_MONTH, -20);
                Date start = today.getTime();
                today.add(Calendar.DAY_OF_MONTH, +40);
                us.setSelectionInterval(start, today.getTime());
                // shouldn't effect scrolling state
                us.revalidate();
                // client code must trigger 
//                us.ensureDateVisible(start.getTime());
            }
            
        };
        addAction(frame, nextMonthInterval);
        Action next = new AbstractActionExt("next month") {

            public void actionPerformed(ActionEvent e) {
                if (us.isSelectionEmpty()) return;
                if (!CalendarUtils.isEndOfMonth(today)) {
                    CalendarUtils.endOfMonth(today);
                    
                }
                today.add(Calendar.DAY_OF_MONTH, 1);
                us.setSelectedDate(today.getTime());
                LOG.info("firstDisplayed before: " + new Date(us.getFirstDisplayedDate()));
                
                // shouldn't effect scrolling state
                us.revalidate();
                LOG.info("firstDisplayed: " + new Date(us.getFirstDisplayedDate()));
                // client code must trigger 
//                us.ensureDateVisible(today.getTimeInMillis());
            }
            
        };
        addAction(frame, next);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * #703-swingx: select date doesn't ensure visibility of selected.
     * 
     * compare with core list: doesn't scroll as well.
     * 
     */
    public void interactiveAutoScrollOnSelectionList() {
        // add hoc model
        SortedSet<Date> dates = getDates();
        
        final JXList us = new JXList(new ListComboBoxModel<Date>(new ArrayList<Date>(dates)));
        us.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        JXFrame frame = wrapWithScrollingInFrame(us, "list - autoscroll on selection");
        Action next = new AbstractActionExt("select last + 1") {

            public void actionPerformed(ActionEvent e) {
                int last = us.getLastVisibleIndex();
                us.setSelectedIndex(last + 1);
                // shouldn't effect scrolling state
                us.revalidate();
                // client code must trigger 
//                us.ensureIndexIsVisible(last+1);
            }
            
        };
        addAction(frame, next);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * #703-swingx: set date to first of next doesn't "scroll".
     * 
     * compare with core tree: doesn't scroll as well.
     * 
     */
    public void interactiveAutoScrollOnSelectionTree() {
        // add hoc model
        SortedSet<Date> dates = getDates();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("dates");
        for (Date date : dates) {
            root.add(new DefaultMutableTreeNode(date));
        }
        
        final JXTree us = new JXTree(root);
        JXFrame frame = wrapWithScrollingInFrame(us, "tree - autoscroll on selection");
        Action next = new AbstractActionExt("select last + 1") {

            public void actionPerformed(ActionEvent e) {
                int last = us.getLeadSelectionRow();
                us.setSelectionRow(last + 1);
                // shouldn't effect scrolling state
                us.revalidate();
                // client code must trigger 
//                us.scrollRowToVisible(last + 1);
            }
            
        };
        addAction(frame, next);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * #703-swingx: set date to first of next doesn't "scroll".
     * 
     * compare with core tree: doesn't scroll as well.
     * 
     */
    public void interactiveAutoScrollOnSelectionTable() {
        // add hoc model
        SortedSet<Date> dates = getDates();
        DefaultTableModel model = new DefaultTableModel(0, 1);
        for (Date date : dates) {
            model.addRow(new Object[] {date});
        }
        
        final JXTable us = new JXTable(model);
        JXFrame frame = wrapWithScrollingInFrame(us, "table - autoscroll on selection");
        Action next = new AbstractActionExt("select last + 1") {

            public void actionPerformed(ActionEvent e) {
                int last = us.getSelectedRow();
                us.setRowSelectionInterval(last + 1, last + 1);
                // shouldn't effect scrolling state
                us.revalidate();
                // client code must trigger 
//                us.scrollRowToVisible(last + 1);
            }
            
        };
        addAction(frame, next);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Convenience to get a bunch of dates.
     * 
     * @return
     */
    private SortedSet<Date> getDates() {
        JXMonthView source = new JXMonthView();
        source.setSelectionMode(JXMonthView.SelectionMode.SINGLE_INTERVAL_SELECTION);
        final Calendar today = Calendar.getInstance();
        Date start = today.getTime();
        today.add(Calendar.DAY_OF_MONTH, +40);
        source.setSelectionInterval(start, today.getTime());
        SortedSet<Date> dates = source.getSelection();
        return dates;
    }
    
    
    /**
     * #681-swingx: first row overlaps days.
     * 
     * Looks like a problem with the constructor taking a locale? 
     * Default is okay (even if German), US is okay, explicit german is wrong.
     */
    public void interactiveFirstRowOfMonthSetLocale() {
        JPanel p = new JPanel();
        // default constructor
        p.add(new JXMonthView());
        // explicit us locale
        JXMonthView us = new JXMonthView();
        us.setLocale(Locale.US);
        p.add(us);
        // explicit german locale
        JXMonthView german = new JXMonthView();
        german.setLocale(Locale.GERMAN);
        p.add(german);
        showInFrame(p, "first row overlapping - setLocale");
    }

   
    /**
     * #681-swingx: first row overlaps days.
     * 
     * Looks like a problem with the constructor taking a locale? 
     * Default is okay (even if German), US is okay, explicit german is wrong.
     */
    public void interactiveFirstRowOfMonthLocaleConstructor() {
        JPanel p = new JPanel();
        // default constructor
        p.add(new JXMonthView());
        // explicit us locale
        p.add(new JXMonthView(Locale.US));
//         explicit german locale
        p.add(new JXMonthView(Locale.GERMAN));
        showInFrame(p, "first row overlapping - constructor");
    }
    /**
     * #681-swingx: first row overlaps days.
     * Here everything looks okay.
     * 
     * @see #interactiveFirstRowOfMonthLocaleDependent()
     */
    public void interactiveFirstRowOfMonth() {
        JXMonthView monthView = new JXMonthView();
        calendar.set(2008, 1, 1);
        monthView.setSelectedDate(calendar.getTime());
        showInFrame(monthView, "first row");
    }

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
    public void interactiveUpdateOnTimeZoneJP() {
        JComponent panel = Box.createVerticalBox();

        final JComboBox zoneSelector = new JComboBox(TimeZone.getAvailableIDs());
        final JXMonthView monthView = new JXMonthView();
        monthView.setTraversable(true);
        // Synchronize the picker and selector's zones.
        zoneSelector.setSelectedItem(monthView.getTimeZone().getID());

        // Set the picker's time zone based on the selected time zone.
        zoneSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String zone = (String) zoneSelector.getSelectedItem();
                TimeZone tz = TimeZone.getTimeZone(zone);
                monthView.setTimeZone(tz);
              
                assertEquals(tz, monthView.getCalendar().getTimeZone());
            }
        });

        panel.add(monthView);
//        JPanel bar = new JPanel();
        JLabel label = new JLabel("Select TimeZone:");
        label.setHorizontalAlignment(JLabel.CENTER);
//        panel.add(label);
        panel.add(zoneSelector);
        JXFrame frame = wrapInFrame(panel, "TimeZone");
        frame.pack();
        frame.setVisible(true);
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
                LOG.info("last(view/ui): " + viewLast + "/" + uiLast);
                
            }
            
        };
        JXFrame frame = wrapInFrame(month, "default - for debugging only");
        addAction(frame, action);
        frame.setVisible(true);
    }

//----------------------
    
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
        final long first = us.getFirstDisplayedDate();
        JXFrame frame = showInFrame(us, "");
        today.add(Calendar.DAY_OF_MONTH, 1);
        us.setSelectedDate(today.getTime());
        us.revalidate();
        LOG.info("firstdisplayed: " + new Date(us.getFirstDisplayedDate()));
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                LOG.info("firstdisplayed: " + new Date(us.getFirstDisplayedDate()));
                assertEquals(first, us.getFirstDisplayedDate());
                fail("weird (threading issue?): the firstDisplayed is changed in layoutContainer - not testable here");
            }
        });
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
