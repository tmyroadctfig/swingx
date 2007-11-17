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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.JXMonthView.SelectionMode;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.event.DateSelectionEvent.EventType;
import org.jdesktop.swingx.test.DateSelectionReport;
import org.jdesktop.swingx.test.XTestUtils;
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
    private static final int ONE_MINUTE = 60*1000;
    private static final int ONE_HOUR   = 60*ONE_MINUTE;
    @SuppressWarnings("unused")
    private static final int ONE_DAY    = 24*ONE_HOUR;

    public static void main(String[] args) {
//      setSystemLF(true);
      JXMonthViewIssues  test = new JXMonthViewIssues();
      try {
//          test.runInteractiveTests();
        test.runInteractiveTests(".*Stack.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
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
        final Calendar cal = Calendar.getInstance();
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
                cal.setTimeZone(tz);
                String formatS = "EEE, d MMM yyyy HH:mm:ss z";
                DateFormat format = new SimpleDateFormat(formatS);
                format.setTimeZone(tz);
                LOG.info("time in cal " + format.format(cal.getTime()));
                
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
    
   
    public void interactiveSimple() {
        JXMonthView month = new JXMonthView();
        month.setTraversable(true);
        showInFrame(month, "default - for debugging only");
    }

//----------------------
    

    public void testTimeZone() {
        JXMonthView monthView = new JXMonthView();
        Calendar cal = monthView.getCalendar();
        assertEquals(cal.getTimeZone(), monthView.getTimeZone());
        assertEquals(cal.getTime(), new Date(monthView.getFirstDisplayedDate()));
        assertEquals(0, cal.getTimeZone().getRawOffset() / ONE_HOUR);
        
    }
    /**
     * Issue #618-swingx: JXMonthView displays problems with non-default
     * timezones.
     * 
     */
    public void testTimeZoneUpdate() {
        JXMonthView monthView = new JXMonthView();
        monthView.setSelectedDate(new Date());
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
        TestUtils.assertPropertyChangeEvent(report, monthView, 
                "timeZone", timezone, newTimeZone);
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
    * Okay ... looks more like a confusing (me!) doc: the date
    * in the constructor is not the selection, but the date
    * to use for the first display. Hmm ...
    */
   public void testMonthViewInitialSelection() {
       JXMonthView monthView = new JXMonthView(new GregorianCalendar(2007, 6, 28).getTimeInMillis());
       assertNotNull(monthView.getSelectedDate());
   }

   /**
    * 
    * no invariant for the monthView's calender
    * monthViewUI at some places restores to firstDisplayedDay, why?
    *
    */
   public void testCalendar() {
      JXMonthView monthView = new JXMonthView();
      assertEquals(1, monthView.getCalendar().get(Calendar.DATE));
      Date first = new Date(monthView.getFirstDisplayedDate());
      assertEquals(first, monthView.getCalendar().getTime());
      Date date = XTestUtils.getCleanedToday(10);
      monthView.addSelectionInterval(date , date);
      assertEquals(first, monthView.getCalendar().getTime());
      monthView.isSelectedDate(new Date().getTime());
      assertEquals(first, monthView.getCalendar().getTime());
   }

}
