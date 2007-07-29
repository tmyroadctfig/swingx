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
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import javax.swing.Action;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.calendar.JXMonthView.SelectionMode;
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
    public static void main(String[] args) {
//      setSystemLF(true);
      JXMonthViewIssues  test = new JXMonthViewIssues();
      try {
          test.runInteractiveTests();
//        test.runInteractiveTests(".*Move.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }

    public void interactiveSimple() {
        JXMonthView month = new JXMonthView();
        showInFrame(month, "default - for debugging only");
    }

//----------------------
    
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
